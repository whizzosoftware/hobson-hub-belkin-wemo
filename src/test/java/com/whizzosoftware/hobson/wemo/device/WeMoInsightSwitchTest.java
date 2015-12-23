/*******************************************************************************
 * Copyright (c) 2014 Whizzo Software, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.whizzosoftware.hobson.wemo.device;

import com.whizzosoftware.hobson.api.device.MockDeviceManager;
import com.whizzosoftware.hobson.api.plugin.MockHobsonPlugin;
import com.whizzosoftware.hobson.wemo.api.WeMoService;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;

import java.io.StringReader;

import static org.junit.Assert.*;

public class WeMoInsightSwitchTest {
    @Test
    public void testWeMoDeviceConstructorWithXML() throws Exception {
        String xml = "<?xml version=\"1.0\"?>\n" +
                "<root xmlns=\"urn:Belkin:device-1-0\">\n" +
                "  <specVersion>\n" +
                "    <major>1</major>\n" +
                "    <minor>0</minor>\n" +
                "  </specVersion>\n" +
                "  <device>\n" +
                "<deviceType>urn:Belkin:device:insight:1</deviceType>\n" +
                "<friendlyName>WeMo Insight</friendlyName>\n" +
                "    <manufacturer>Belkin International Inc.</manufacturer>\n" +
                "    <manufacturerURL>http://www.belkin.com</manufacturerURL>\n" +
                "    <modelDescription>Belkin Insight 1.0</modelDescription>\n" +
                "    <modelName>Insight</modelName>\n" +
                "    <modelNumber>1.0</modelNumber>\n" +
                "    <modelURL>http://www.belkin.com/plugin/</modelURL>\n" +
                "<serialNumber>221437K1200D6D</serialNumber>\n" +
                "<UDN>uuid:Insight-1_0-221437K1200D6D</UDN>\n" +
                "    <UPC>123456789</UPC>\n" +
                "<macAddress>94103E3B6B1C</macAddress>\n" +
                "<firmwareVersion>WeMo_WW_2.00.7166.PVT</firmwareVersion>\n" +
                "<iconVersion>0|49153</iconVersion>\n" +
                "<binaryState>0</binaryState>\n" +
                "    <iconList> \n" +
                "      <icon> \n" +
                "        <mimetype>jpg</mimetype> \n" +
                "        <width>100</width> \n" +
                "        <height>100</height> \n" +
                "        <depth>100</depth> \n" +
                "         <url>icon.jpg</url> \n" +
                "      </icon> \n" +
                "    </iconList>\n" +
                "    <serviceList>\n" +
                "      <service>\n" +
                "        <serviceType>urn:Belkin:service:WiFiSetup:1</serviceType>\n" +
                "        <serviceId>urn:Belkin:serviceId:WiFiSetup1</serviceId>\n" +
                "        <controlURL>/upnp/control/WiFiSetup1</controlURL>\n" +
                "        <eventSubURL>/upnp/event/WiFiSetup1</eventSubURL>\n" +
                "        <SCPDURL>/setupservice.xml</SCPDURL>\n" +
                "      </service>\n" +
                "      <service>\n" +
                "        <serviceType>urn:Belkin:service:timesync:1</serviceType>\n" +
                "        <serviceId>urn:Belkin:serviceId:timesync1</serviceId>\n" +
                "        <controlURL>/upnp/control/timesync1</controlURL>\n" +
                "        <eventSubURL>/upnp/event/timesync1</eventSubURL>\n" +
                "        <SCPDURL>/timesyncservice.xml</SCPDURL>\n" +
                "      </service>\n" +
                "      <service>\n" +
                "        <serviceType>urn:Belkin:service:basicevent:1</serviceType>\n" +
                "        <serviceId>urn:Belkin:serviceId:basicevent1</serviceId>\n" +
                "        <controlURL>/upnp/control/basicevent1</controlURL>\n" +
                "        <eventSubURL>/upnp/event/basicevent1</eventSubURL>\n" +
                "        <SCPDURL>/eventservice.xml</SCPDURL>\n" +
                "      </service>\n" +
                "      <service>\n" +
                "        <serviceType>urn:Belkin:service:firmwareupdate:1</serviceType>\n" +
                "        <serviceId>urn:Belkin:serviceId:firmwareupdate1</serviceId>\n" +
                "        <controlURL>/upnp/control/firmwareupdate1</controlURL>\n" +
                "        <eventSubURL>/upnp/event/firmwareupdate1</eventSubURL>\n" +
                "        <SCPDURL>/firmwareupdate.xml</SCPDURL>\n" +
                "      </service>\n" +
                "      <service>\n" +
                "        <serviceType>urn:Belkin:service:rules:1</serviceType>\n" +
                "        <serviceId>urn:Belkin:serviceId:rules1</serviceId>\n" +
                "        <controlURL>/upnp/control/rules1</controlURL>\n" +
                "        <eventSubURL>/upnp/event/rules1</eventSubURL>\n" +
                "        <SCPDURL>/rulesservice.xml</SCPDURL>\n" +
                "      </service>\n" +
                "\t  \n" +
                "      <service>\n" +
                "        <serviceType>urn:Belkin:service:metainfo:1</serviceType>\n" +
                "        <serviceId>urn:Belkin:serviceId:metainfo1</serviceId>\n" +
                "        <controlURL>/upnp/control/metainfo1</controlURL>\n" +
                "        <eventSubURL>/upnp/event/metainfo1</eventSubURL>\n" +
                "        <SCPDURL>/metainfoservice.xml</SCPDURL>\n" +
                "      </service>\n" +
                "\n" +
                "      <service>\n" +
                "        <serviceType>urn:Belkin:service:remoteaccess:1</serviceType>\n" +
                "        <serviceId>urn:Belkin:serviceId:remoteaccess1</serviceId>\n" +
                "        <controlURL>/upnp/control/remoteaccess1</controlURL>\n" +
                "        <eventSubURL>/upnp/event/remoteaccess1</eventSubURL>\n" +
                "        <SCPDURL>/remoteaccess.xml</SCPDURL>\n" +
                "      </service>\n" +
                "\t   \n" +
                "      <service>\n" +
                "        <serviceType>urn:Belkin:service:deviceinfo:1</serviceType>\n" +
                "        <serviceId>urn:Belkin:serviceId:deviceinfo1</serviceId>\n" +
                "        <controlURL>/upnp/control/deviceinfo1</controlURL>\n" +
                "        <eventSubURL>/upnp/event/deviceinfo1</eventSubURL>\n" +
                "        <SCPDURL>/deviceinfoservice.xml</SCPDURL>\n" +
                "      </service>\n" +
                "\n" +
                "      <service>\n" +
                "        <serviceType>urn:Belkin:service:insight:1</serviceType>\n" +
                "        <serviceId>urn:Belkin:serviceId:insight1</serviceId>\n" +
                "        <controlURL>/upnp/control/insight1</controlURL>\n" +
                "        <eventSubURL>/upnp/event/insight1</eventSubURL>\n" +
                "        <SCPDURL>/insightservice.xml</SCPDURL>\n" +
                "      </service>\n" +
                "\n" +
                "      <service>\n" +
                "        <serviceType>urn:Belkin:service:smartsetup:1</serviceType>\n" +
                "        <serviceId>urn:Belkin:serviceId:smartsetup1</serviceId>\n" +
                "        <controlURL>/upnp/control/smartsetup1</controlURL>\n" +
                "        <eventSubURL>/upnp/event/smartsetup1</eventSubURL>\n" +
                "        <SCPDURL>/smartsetup.xml</SCPDURL>\n" +
                "      </service>\n" +
                "      \n" +
                "      <service>\n" +
                "        <serviceType>urn:Belkin:service:manufacture:1</serviceType>\n" +
                "        <serviceId>urn:Belkin:serviceId:manufacture1</serviceId>\n" +
                "        <controlURL>/upnp/control/manufacture1</controlURL>\n" +
                "        <eventSubURL>/upnp/event/manufacture1</eventSubURL>\n" +
                "        <SCPDURL>/manufacture.xml</SCPDURL>\n" +
                "      </service>\n" +
                "\n" +
                "    </serviceList>\n" +
                "   <presentationURL>/pluginpres.html</presentationURL>\n" +
                "</device>\n" +
                "</root>\n" +
                "\n";

        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
        MockDeviceManager dm = new MockDeviceManager();
        MockHobsonPlugin plugin = new MockHobsonPlugin("plugin1");
        plugin.setDeviceManager(dm);
        WeMoDevice device = new WeMoInsightSwitch(plugin, "id", "http://www.foo.com:5000/service.xml", doc);
        assertEquals("WeMo Insight", device.getName());
        assertEquals("WeMo_WW_2.00.7166.PVT", device.getFirmwareVersion());

        WeMoService svc = device.getService(WeMoDevice.SERVICE_BASICEVENT1);
        assertEquals(WeMoDevice.SERVICE_BASICEVENT1, svc.getServiceType());
        assertEquals("urn:Belkin:serviceId:basicevent1", svc.getServiceId());
        assertEquals("http://www.foo.com:5000/upnp/control/basicevent1", svc.getControlURI().toString());
        assertEquals("http://www.foo.com:5000/upnp/event/basicevent1", svc.getEventSubURI().toASCIIString());
        assertEquals("http://www.foo.com:5000/eventservice.xml", svc.getSCPDURI().toASCIIString());
    }
}
