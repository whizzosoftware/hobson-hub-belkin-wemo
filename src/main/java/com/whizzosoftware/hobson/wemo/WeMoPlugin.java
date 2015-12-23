/*******************************************************************************
 * Copyright (c) 2014 Whizzo Software, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.whizzosoftware.hobson.wemo;

import com.whizzosoftware.hobson.api.device.HobsonDevice;
import com.whizzosoftware.hobson.api.disco.DeviceAdvertisement;
import com.whizzosoftware.hobson.api.event.DeviceAdvertisementEvent;
import com.whizzosoftware.hobson.api.event.EventTopics;
import com.whizzosoftware.hobson.api.event.HobsonEvent;
import com.whizzosoftware.hobson.api.plugin.PluginStatus;
import com.whizzosoftware.hobson.api.plugin.http.AbstractHttpClientPlugin;
import com.whizzosoftware.hobson.api.plugin.http.HttpChannel;
import com.whizzosoftware.hobson.api.property.PropertyContainer;
import com.whizzosoftware.hobson.api.property.TypedProperty;
import com.whizzosoftware.hobson.ssdp.SSDPPacket;
import com.whizzosoftware.hobson.wemo.device.WeMoDevice;
import com.whizzosoftware.hobson.wemo.device.WeMoDeviceFactory;
import org.json.JSONArray;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * A Hobson plugin implementation for monitoring and controlling Belkin WeMo devices.
 *
 * @author Dan Noguerol
 */
public class WeMoPlugin extends AbstractHttpClientPlugin {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static final String PROP_WEMO_URIS = "wemo.uris";

    private final List<String> discoveredDeviceLocations = new ArrayList<>();
    private final List<String> pendingRequests = new ArrayList<>();
    private JSONArray deviceURIsProperty;

    public WeMoPlugin(String pluginId) {
        super(pluginId);
    }

    public WeMoPlugin(String pluginId, HttpChannel channel) {
        super(pluginId, channel);
    }

    @Override
    public void onStartup(PropertyContainer config) {
        // process the config
        onPluginConfigurationUpdate(config);

        // request current SSDP device advertisements
        logger.debug("Requesting device advertisement snapshot");
        requestDeviceAdvertisementSnapshot(SSDPPacket.PROTOCOL_ID);

        // set the status to running
        setStatus(PluginStatus.running());
    }

    @Override
    public void onShutdown() {
        // TODO: perform any cleanup
    }

    /**
     * Returns the plugin name.
     *
     * @return a String
     */
    @Override
    public String getName() {
        return "Example Plugin";
    }

    @Override
    public String[] getEventTopics() {
        return new String[] { EventTopics.createDiscoTopic(SSDPPacket.PROTOCOL_ID) };
    }

    @Override
    public long getRefreshInterval() {
        return 5;
    }

    @Override
    public void onRefresh() {
        for (HobsonDevice device : getAllDevices()) {
            if (device instanceof WeMoDevice) {
                ((WeMoDevice)device).onRefresh();
            }
        }
    }

    @Override
    protected TypedProperty[] createSupportedProperties() {
        return new TypedProperty[] {
            new TypedProperty.Builder(PROP_WEMO_URIS, "WeMo URIs", "A list of hosts for your WeMo devices (should currently be formatted as a JSON array of strings until the web console supports proper lists)", TypedProperty.Type.STRING).build()
        };
    }

    /**
     * Callback method when the plugin's configuration changes.
     *
     * @param config the new configuration
     */
    @Override
    public void onPluginConfigurationUpdate(PropertyContainer config) {
        String json = (String)config.getPropertyValue(PROP_WEMO_URIS);
        if (json != null) {
            deviceURIsProperty = new JSONArray(new JSONTokener(json));
            for (int i=0; i < deviceURIsProperty.length(); i++) {
                onFoundDevice(deviceURIsProperty.getString(i));
            }
        }
    }

    @Override
    public void onHobsonEvent(HobsonEvent event) {
        super.onHobsonEvent(event);

        if (event instanceof DeviceAdvertisementEvent) {
            DeviceAdvertisement advertisement = ((DeviceAdvertisementEvent)event).getAdvertisement();
            if ("ssdp".equals(advertisement.getProtocolId())) {
                final SSDPPacket ssdp = (SSDPPacket)advertisement.getObject();
                if (ssdp != null) {
                    logger.trace("Got SSDP advertisement: {}, {}", ssdp.getST(), ssdp.getLocation());
                    if (ssdp.getUSN() != null && ssdp.getUSN().contains("urn:Belkin") && ssdp.getUSN().contains("uuid:Insight") && !discoveredDeviceLocations.contains(ssdp.getLocation())) {
                        synchronized (discoveredDeviceLocations) {
                            if (!discoveredDeviceLocations.contains(ssdp.getLocation())) {
                                onFoundDevice(ssdp.getLocation());
                            }
                        }
                    }
                } else {
                    logger.warn("Received device advertisement with no SSDP packet");
                }
            }
        }
    }

    protected void onFoundDevice(String host) {
        try {
            if (!pendingRequests.contains(host)) {
                logger.debug("Interrogating device at {}", host);
                URI uri = createURIFromHost(host);
                pendingRequests.add(host);
                sendHttpGetRequest(uri, null, uri);
            }
        } catch (URISyntaxException e) {
            logger.error("Error interrogating device at {}" + host, e);
        }
    }

    @Override
    protected void onHttpResponse(int statusCode, List<Map.Entry<String, String>> headers, String response, Object context) {
        logger.trace("Got HTTP response with context: {}", context);
        // if the context is an SSDPPacket, then this is the response from a new device setup.xml request
        if (context instanceof URI) {
            try {
                URI uri = (URI)context;
                pendingRequests.remove(uri.toASCIIString());
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setNamespaceAware(true);
                Document document = factory.newDocumentBuilder().parse(new InputSource(new StringReader(response)));
                logger.trace("Got HTTP response: " + document);
                WeMoDevice device = WeMoDeviceFactory.createWeMoDevice(this, uri, document);
                if (device != null) {
                    logger.info("Found WeMo device: " + device.getName());
                    publishDevice(device);
                    addWeMoURIToConfiguration(uri);
                } else {
                    logger.warn("Unable to identify WeMo device; ignoring");
                }
            } catch (Exception e) {
                logger.error("Error parsing device setup.xml response", e);
            }
        // if the context is a WeMoDeviceRequestContext, then this is a response for a specific device -- deliver it
        } else if (context instanceof WeMoDeviceRequestContext) {
            WeMoDeviceRequestContext wdrc = (WeMoDeviceRequestContext)context;
            wdrc.getDevice().onHttpResponse(statusCode, headers, response, wdrc.getContext());
        }
    }

    @Override
    protected void onHttpRequestFailure(Throwable cause, Object context) {
        logger.error("HTTP request error", cause);
        if (context instanceof URI) {
            URI uri = (URI)context;
            pendingRequests.remove(uri.toASCIIString());
        } else if (context instanceof WeMoDeviceRequestContext) {
            WeMoDeviceRequestContext wdrc = (WeMoDeviceRequestContext)context;
            wdrc.getDevice().onHttpRequestFailure(cause, wdrc.getContext());
        }
    }

    public void sendDeviceHttpPostRequest(WeMoDevice device, URI uri, Map<String,String> headers, byte[] data, Object context) {
        sendHttpPostRequest(uri, headers, data, new WeMoDeviceRequestContext(device, context));
    }

    protected void addWeMoURIToConfiguration(URI uri) {
        if (uri != null) {
            String suri = minifyURI(uri);

            // create list if it doesn't already exist
            if (deviceURIsProperty == null) {
                deviceURIsProperty = new JSONArray();
            }

            // make sure URI doesn't already exist in the list
            for (int i = 0; i < deviceURIsProperty.length(); i++) {
                if (suri.equals(deviceURIsProperty.getString(i))) {
                    return;
                }
            }

            // add new URI to the list
            deviceURIsProperty.put(suri);
            setPluginConfigurationProperty(getContext(), PROP_WEMO_URIS, deviceURIsProperty.toString());
        }
    }

    protected URI createURIFromHost(String s) throws URISyntaxException {
        if (s.startsWith("http")) {
            return new URI(s);
        } else if (s.contains(":")) {
            int ix = s.indexOf(":");
            String host = s.substring(0, ix);
            Integer port = Integer.parseInt(s.substring(ix+1));
            return new URI("http", null, host, port, "/setup.xml", null, null);
        } else {
            return new URI("http", null, s, 49153, "/setup.xml", null, null);
        }
    }

    protected String minifyURI(URI uri) {
        if ("http".equals(uri.getScheme()) && uri.getPath().endsWith("setup.xml")) {
            if (uri.getPort() == 49153) {
                return uri.getHost();
            } else {
                return uri.getHost() + ":" + uri.getPort();
            }
        } else {
            return uri.toASCIIString();
        }
    }
}
