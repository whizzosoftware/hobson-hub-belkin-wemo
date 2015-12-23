/*******************************************************************************
 * Copyright (c) 2014 Whizzo Software, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.whizzosoftware.hobson.wemo.device;

import com.whizzosoftware.hobson.api.HobsonRuntimeException;
import com.whizzosoftware.hobson.api.device.AbstractHobsonDevice;
import com.whizzosoftware.hobson.api.plugin.HobsonPlugin;
import com.whizzosoftware.hobson.wemo.WeMoPlugin;
import com.whizzosoftware.hobson.wemo.WeMoUtil;
import com.whizzosoftware.hobson.wemo.api.WeMoActionInvocation;
import com.whizzosoftware.hobson.wemo.api.WeMoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract base class for WeMo device implementations.
 *
 * @author Dan Noguerol
 */
abstract public class WeMoDevice extends AbstractHobsonDevice {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public final static String ACTION_SET_BINARY_STATE = "SetBinaryState";
    public final static String PROP_BINARY_STATE = "BinaryState";
    public final static String SERVICE_BASICEVENT1 = "urn:Belkin:service:basicevent:1";

    private String baseURI;
    private String firmwareVersion;
    private final Map<String,WeMoService> serviceMap = new HashMap<>();

    public WeMoDevice(HobsonPlugin plugin, String id, String baseURI, Document doc) {
        super(plugin, id);
        this.baseURI = baseURI;

        parseServiceDocument(doc);
    }

    protected WeMoPlugin getWeMoPlugin() {
        return (WeMoPlugin)getPlugin();
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    protected WeMoService getService(String serviceId) {
        return serviceMap.get(serviceId);
    }

    protected void invokeAction(String serviceId, String actionName, Map<String,Object> properties) throws Exception {
        WeMoService svc = serviceMap.get(serviceId);
        WeMoActionInvocation ai = new WeMoActionInvocation(svc.getServiceType(), actionName, properties);

        Map<String,String> headers = new HashMap<>();
        headers.put("Connection", "close");
        headers.put("SOAPACTION", "\"" + svc.getServiceType() + "#" + actionName + "\"");
        headers.put("Content-Type", "text/xml; charset=\"utf-8\"");

        logger.trace("Sending SOAP request for action: " + actionName);

        getWeMoPlugin().sendDeviceHttpPostRequest(this, svc.getControlURI(), headers, ai.toXML().getBytes("UTF8"), null);
    }

    public void onHttpResponse(int statusCode, List<Map.Entry<String, String>> headers, String response, Object context) {
        logger.trace("Got response status code: {}", statusCode);
        try {
            if (statusCode == 200) {
                onStateUpdate(WeMoUtil.createResponseMap(response));
            } else {
                onHttpRequestFailure(new HobsonRuntimeException("Unexpected response code from device " + getContext() + ": " + statusCode), context);
            }
        } catch (Exception e) {
            logger.error("Unable to parse response from device " + getContext(), e);
        }
    }

    public void onHttpRequestFailure(Throwable cause, Object context) {
        logger.error("Error sending HTTP request for device " + getContext(), cause);
    }

    abstract public void onStateUpdate(Map<String,Object> update);
    abstract public void onRefresh();

    private void parseServiceDocument(Document doc) {
        NodeList deviceList = doc.getElementsByTagName("device");
        if (deviceList.getLength() == 1) {
            Element deviceElem = (Element)deviceList.item(0);
            NodeList deviceChildList = deviceElem.getChildNodes();
            for (int i=0; i < deviceChildList.getLength(); i++) {
                Node n = deviceChildList.item(i);
                if (n.getNodeName().equals("friendlyName")) {
                    setDefaultName(n.getTextContent());
                } else if (n.getNodeName().equals("firmwareVersion")) {
                    this.firmwareVersion = n.getTextContent();
                } else if ("serviceList".equals(n.getNodeName())) {
                    NodeList nl2 = n.getChildNodes();
                    for (int i2=0; i2 < nl2.getLength(); i2++) {
                        Node n2 = nl2.item(i2);
                        if ("service".equals(n2.getNodeName())) {
                            WeMoService svc = new WeMoService(baseURI, (Element)n2);
                            serviceMap.put(svc.getServiceType(), svc);
                        }
                    }
                }
            }
        }
    }
}
