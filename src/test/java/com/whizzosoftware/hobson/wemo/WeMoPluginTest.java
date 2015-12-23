/*******************************************************************************
 * Copyright (c) 2014 Whizzo Software, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.whizzosoftware.hobson.wemo;

import com.whizzosoftware.hobson.api.device.MockDeviceManager;
import com.whizzosoftware.hobson.api.disco.MockDiscoManager;
import com.whizzosoftware.hobson.api.plugin.MockPluginManager;
import com.whizzosoftware.hobson.api.plugin.PluginContext;
import com.whizzosoftware.hobson.api.plugin.http.MockHttpChannel;
import com.whizzosoftware.hobson.api.property.PropertyContainer;
import com.whizzosoftware.hobson.api.variable.MockVariableManager;
import org.junit.Test;

import java.net.URI;

import static org.junit.Assert.*;

public class WeMoPluginTest {
    @Test
    public void testOnStartupWithOneDevice() throws Exception {
        MockHttpChannel mhc = new MockHttpChannel();
        WeMoPlugin plugin = new WeMoPlugin("wemo", mhc);
        MockPluginManager pm = new MockPluginManager();
        MockDeviceManager dm = new MockDeviceManager();
        MockDiscoManager dsm = new MockDiscoManager();
        plugin.setPluginManager(pm);
        plugin.setDeviceManager(dm);
        plugin.setDiscoManager(dsm);

        PropertyContainer config = new PropertyContainer();
        config.setPropertyValue(WeMoPlugin.PROP_WEMO_URIS, "[\"192.168.1.120\"]");

        assertEquals(0, mhc.getGetRequests().size());
        plugin.onStartup(config);

        // make sure a GET request went out
        assertEquals(1, mhc.getGetRequests().size());
        assertEquals("http://192.168.1.120:49153/setup.xml", mhc.getGetRequests().get(0).toASCIIString());

        // make sure the same request isn't made twice
        plugin.onPluginConfigurationUpdate(config);
        assertEquals(1, mhc.getGetRequests().size());

        // get back response from WeMo
        plugin.onHttpResponse(200, null, "<?xml version=\"1.0\"?> <root xmlns=\"urn:Belkin:device-1-0\"> <specVersion> <major>1</major> <minor>0</minor> </specVersion> <device> <deviceType>urn:Belkin:device:insight:1</deviceType> <friendlyName>WeMo Insight</friendlyName> <manufacturer>Belkin International Inc.</manufacturer> <manufacturerURL>http://www.belkin.com</manufacturerURL> <modelDescription>Belkin Insight 1.0</modelDescription> <modelName>Insight</modelName> <modelNumber>1.0</modelNumber> <modelURL>http://www.belkin.com/plugin/</modelURL> <serialNumber>221437K1200D6D</serialNumber> <UDN>uuid:Insight-1_0-221437K1200D6D</UDN> <UPC>123456789</UPC> <macAddress>94103E3B6B1C</macAddress> <firmwareVersion>WeMo_WW_2.00.7166.PVT</firmwareVersion> <iconVersion>0|49153</iconVersion> <binaryState>0</binaryState> <iconList> <icon> <mimetype>jpg</mimetype> <width>100</width> <height>100</height> <depth>100</depth> <url>icon.jpg</url> </icon> </iconList> </device> </root>", new URI("http://192.168.1.120:49153/setup.xml"));

        // make sure no more requests were made
        assertEquals(1, mhc.getGetRequests().size());

        // make sure plugin configuration wasn't updated
        assertNull(pm.getLocalPluginConfiguration(plugin.getContext()));
    }

    @Test
    public void testAddWeMoHostToConfigurationWithFirstStandardURI() throws Exception {
        WeMoPlugin plugin = new WeMoPlugin("wemo");
        MockPluginManager pm = new MockPluginManager();
        MockDeviceManager dm = new MockDeviceManager();
        MockVariableManager vm = new MockVariableManager();
        plugin.setPluginManager(pm);
        plugin.setDeviceManager(dm);
        plugin.setVariableManager(vm);
        assertNull(pm.getLocalPluginConfiguration(PluginContext.createLocal("wemo")));

        plugin.onHttpResponse(200, null, "<?xml version=\"1.0\"?> <root xmlns=\"urn:Belkin:device-1-0\"> <specVersion> <major>1</major> <minor>0</minor> </specVersion> <device> <deviceType>urn:Belkin:device:insight:1</deviceType> <friendlyName>WeMo Insight</friendlyName> <manufacturer>Belkin International Inc.</manufacturer> <manufacturerURL>http://www.belkin.com</manufacturerURL> <modelDescription>Belkin Insight 1.0</modelDescription> <modelName>Insight</modelName> <modelNumber>1.0</modelNumber> <modelURL>http://www.belkin.com/plugin/</modelURL> <serialNumber>221437K1200D6D</serialNumber> <UDN>uuid:Insight-1_0-221437K1200D6D</UDN> <UPC>123456789</UPC> <macAddress>94103E3B6B1C</macAddress> <firmwareVersion>WeMo_WW_2.00.7166.PVT</firmwareVersion> <iconVersion>0|49153</iconVersion> <binaryState>0</binaryState> <iconList> <icon> <mimetype>jpg</mimetype> <width>100</width> <height>100</height> <depth>100</depth> <url>icon.jpg</url> </icon> </iconList> </device> </root>", new URI("http://192.168.1.120:49153/setup.xml"));

        assertEquals("[\"192.168.1.120\"]", pm.getLocalPluginConfiguration(plugin.getContext()).getPropertyValue(WeMoPlugin.PROP_WEMO_URIS));
    }

    @Test
    public void testAddWeMoHostToConfigurationWithFirstNonStandardURI() throws Exception {
        WeMoPlugin plugin = new WeMoPlugin("wemo");
        MockPluginManager pm = new MockPluginManager();
        MockDeviceManager dm = new MockDeviceManager();
        MockVariableManager vm = new MockVariableManager();
        plugin.setPluginManager(pm);
        plugin.setDeviceManager(dm);
        plugin.setVariableManager(vm);
        assertNull(pm.getLocalPluginConfiguration(plugin.getContext()));

        plugin.onHttpResponse(200, null, "<?xml version=\"1.0\"?> <root xmlns=\"urn:Belkin:device-1-0\"> <specVersion> <major>1</major> <minor>0</minor> </specVersion> <device> <deviceType>urn:Belkin:device:insight:1</deviceType> <friendlyName>WeMo Insight</friendlyName> <manufacturer>Belkin International Inc.</manufacturer> <manufacturerURL>http://www.belkin.com</manufacturerURL> <modelDescription>Belkin Insight 1.0</modelDescription> <modelName>Insight</modelName> <modelNumber>1.0</modelNumber> <modelURL>http://www.belkin.com/plugin/</modelURL> <serialNumber>221437K1200D6D</serialNumber> <UDN>uuid:Insight-1_0-221437K1200D6D</UDN> <UPC>123456789</UPC> <macAddress>94103E3B6B1C</macAddress> <firmwareVersion>WeMo_WW_2.00.7166.PVT</firmwareVersion> <iconVersion>0|49153</iconVersion> <binaryState>0</binaryState> <iconList> <icon> <mimetype>jpg</mimetype> <width>100</width> <height>100</height> <depth>100</depth> <url>icon.jpg</url> </icon> </iconList> </device> </root>", new URI("https://192.168.1.120:49153/setup.xml"));

        assertEquals("[\"https://192.168.1.120:49153/setup.xml\"]", pm.getLocalPluginConfiguration(plugin.getContext()).getPropertyValue(WeMoPlugin.PROP_WEMO_URIS));
    }

    @Test
    public void testAddWeMoHostToConfigurationWithSecondStandardURI() throws Exception {
        WeMoPlugin plugin = new WeMoPlugin("wemo");
        MockPluginManager pm = new MockPluginManager();
        MockDeviceManager dm = new MockDeviceManager();
        MockVariableManager vm = new MockVariableManager();
        plugin.setPluginManager(pm);
        plugin.setDeviceManager(dm);
        plugin.setVariableManager(vm);
        PropertyContainer config = new PropertyContainer();
        config.setPropertyValue(WeMoPlugin.PROP_WEMO_URIS, "[\"192.168.1.120\"]");
        plugin.onPluginConfigurationUpdate(config);
        assertNull(pm.getLocalPluginConfiguration(plugin.getContext()));

        plugin.onHttpResponse(200, null, "<?xml version=\"1.0\"?> <root xmlns=\"urn:Belkin:device-1-0\"> <specVersion> <major>1</major> <minor>0</minor> </specVersion> <device> <deviceType>urn:Belkin:device:insight:1</deviceType> <friendlyName>WeMo Insight</friendlyName> <manufacturer>Belkin International Inc.</manufacturer> <manufacturerURL>http://www.belkin.com</manufacturerURL> <modelDescription>Belkin Insight 1.0</modelDescription> <modelName>Insight</modelName> <modelNumber>1.0</modelNumber> <modelURL>http://www.belkin.com/plugin/</modelURL> <serialNumber>221437K1200D6D</serialNumber> <UDN>uuid:Insight-1_0-221437K1200D6D</UDN> <UPC>123456789</UPC> <macAddress>94103E3B6B1C</macAddress> <firmwareVersion>WeMo_WW_2.00.7166.PVT</firmwareVersion> <iconVersion>0|49153</iconVersion> <binaryState>0</binaryState> <iconList> <icon> <mimetype>jpg</mimetype> <width>100</width> <height>100</height> <depth>100</depth> <url>icon.jpg</url> </icon> </iconList> </device> </root>", new URI("http://192.168.1.130:49153/setup.xml"));

        assertEquals("[\"192.168.1.120\",\"192.168.1.130\"]", pm.getLocalPluginConfiguration(plugin.getContext()).getPropertyValue(WeMoPlugin.PROP_WEMO_URIS));
    }

    @Test
    public void testAddWeMoHostToConfigurationWithSecondNonStandardURI() throws Exception {
        WeMoPlugin plugin = new WeMoPlugin("wemo");
        MockPluginManager pm = new MockPluginManager();
        MockDeviceManager dm = new MockDeviceManager();
        MockVariableManager vm = new MockVariableManager();
        plugin.setPluginManager(pm);
        plugin.setDeviceManager(dm);
        plugin.setVariableManager(vm);
        PropertyContainer config = new PropertyContainer();
        config.setPropertyValue(WeMoPlugin.PROP_WEMO_URIS, "[\"192.168.1.120\"]");
        plugin.onPluginConfigurationUpdate(config);
        assertNull(pm.getLocalPluginConfiguration(plugin.getContext()));

        plugin.onHttpResponse(200, null, "<?xml version=\"1.0\"?> <root xmlns=\"urn:Belkin:device-1-0\"> <specVersion> <major>1</major> <minor>0</minor> </specVersion> <device> <deviceType>urn:Belkin:device:insight:1</deviceType> <friendlyName>WeMo Insight</friendlyName> <manufacturer>Belkin International Inc.</manufacturer> <manufacturerURL>http://www.belkin.com</manufacturerURL> <modelDescription>Belkin Insight 1.0</modelDescription> <modelName>Insight</modelName> <modelNumber>1.0</modelNumber> <modelURL>http://www.belkin.com/plugin/</modelURL> <serialNumber>221437K1200D6D</serialNumber> <UDN>uuid:Insight-1_0-221437K1200D6D</UDN> <UPC>123456789</UPC> <macAddress>94103E3B6B1C</macAddress> <firmwareVersion>WeMo_WW_2.00.7166.PVT</firmwareVersion> <iconVersion>0|49153</iconVersion> <binaryState>0</binaryState> <iconList> <icon> <mimetype>jpg</mimetype> <width>100</width> <height>100</height> <depth>100</depth> <url>icon.jpg</url> </icon> </iconList> </device> </root>", new URI("http://192.168.1.130:49154/setup.xml"));

        assertEquals("[\"192.168.1.120\",\"192.168.1.130:49154\"]", pm.getLocalPluginConfiguration(plugin.getContext()).getPropertyValue(WeMoPlugin.PROP_WEMO_URIS));
    }

    @Test
    public void testAddWeMoHostToConfigurationWithDuplicateURI() throws Exception {
        WeMoPlugin plugin = new WeMoPlugin("wemo");
        MockPluginManager pm = new MockPluginManager();
        MockDeviceManager dm = new MockDeviceManager();
        MockVariableManager vm = new MockVariableManager();
        plugin.setPluginManager(pm);
        plugin.setDeviceManager(dm);
        plugin.setVariableManager(vm);

        PropertyContainer config = new PropertyContainer();
        config.setPropertyValue(WeMoPlugin.PROP_WEMO_URIS, "[\"192.168.1.120\"]");
        plugin.onPluginConfigurationUpdate(config);

        // make sure property is unset
        assertNull(pm.getLocalPluginConfiguration(plugin.getContext()));

        plugin.onHttpResponse(200, null, "<?xml version=\"1.0\"?> <root xmlns=\"urn:Belkin:device-1-0\"> <specVersion> <major>1</major> <minor>0</minor> </specVersion> <device> <deviceType>urn:Belkin:device:insight:1</deviceType> <friendlyName>WeMo Insight</friendlyName> <manufacturer>Belkin International Inc.</manufacturer> <manufacturerURL>http://www.belkin.com</manufacturerURL> <modelDescription>Belkin Insight 1.0</modelDescription> <modelName>Insight</modelName> <modelNumber>1.0</modelNumber> <modelURL>http://www.belkin.com/plugin/</modelURL> <serialNumber>221437K1200D6D</serialNumber> <UDN>uuid:Insight-1_0-221437K1200D6D</UDN> <UPC>123456789</UPC> <macAddress>94103E3B6B1C</macAddress> <firmwareVersion>WeMo_WW_2.00.7166.PVT</firmwareVersion> <iconVersion>0|49153</iconVersion> <binaryState>0</binaryState> <iconList> <icon> <mimetype>jpg</mimetype> <width>100</width> <height>100</height> <depth>100</depth> <url>icon.jpg</url> </icon> </iconList> </device> </root>", new URI("http://192.168.1.120:49153/setup.xml"));

        // make sure the property is still unset
        assertNull(pm.getLocalPluginConfiguration(plugin.getContext()));
    }

    @Test
    public void testCreateURIFromHost() throws Exception {
        WeMoPlugin plugin = new WeMoPlugin("wemo");
        assertEquals("http://192.168.0.120:49153/setup.xml", plugin.createURIFromHost("192.168.0.120").toASCIIString());
        assertEquals("http://192.168.0.120:8080/setup.xml", plugin.createURIFromHost("192.168.0.120:8080").toASCIIString());
        assertEquals("https://192.168.0.120:8181/foo.xml", plugin.createURIFromHost("https://192.168.0.120:8181/foo.xml").toASCIIString());
    }

    @Test
    public void testMinifyURI() throws Exception {
        WeMoPlugin plugin = new WeMoPlugin("wemo");
        assertEquals("192.168.0.120", plugin.minifyURI(new URI("http://192.168.0.120:49153/setup.xml")));
        assertEquals("192.168.0.120:8080", plugin.minifyURI(new URI("http://192.168.0.120:8080/setup.xml")));
        assertEquals("http://192.168.0.120:49153/setup2.xml", plugin.minifyURI(new URI("http://192.168.0.120:49153/setup2.xml")));
        assertEquals("https://192.168.0.120:49153/setup.xml", plugin.minifyURI(new URI("https://192.168.0.120:49153/setup.xml")));
    }
}
