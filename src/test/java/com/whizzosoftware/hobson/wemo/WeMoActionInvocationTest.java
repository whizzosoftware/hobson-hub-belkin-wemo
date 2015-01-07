/*******************************************************************************
 * Copyright (c) 2014 Whizzo Software, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.whizzosoftware.hobson.wemo;

import com.whizzosoftware.hobson.wemo.api.WeMoActionInvocation;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

public class WeMoActionInvocationTest {
    @Test
    public void testSetBinaryStateToXML() throws Exception {
        Map<String,Object> properties = new HashMap<>();
        properties.put("BinaryState", "0");
        WeMoActionInvocation ai = new WeMoActionInvocation("urn:Belkin:service:basicevent:1", "SetBinaryState", properties);
        assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?><s:Envelope s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\"><s:Body><u:SetBinaryState xmlns:u=\"urn:Belkin:service:basicevent:1\"><BinaryState>0</BinaryState></u:SetBinaryState></s:Body></s:Envelope>", ai.toXML());
    }

    @Test
    public void testGetBinaryStateToXML() throws Exception {
        WeMoActionInvocation ai = new WeMoActionInvocation("urn:Belkin:service:basicevent:1", "GetBinaryState", null);
        assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?><s:Envelope s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\"><s:Body><u:GetBinaryState xmlns:u=\"urn:Belkin:service:basicevent:1\"></u:GetBinaryState></s:Body></s:Envelope>", ai.toXML());
    }

    @Test
    public void testGetPowerToXML() throws Exception {
        WeMoActionInvocation ai = new WeMoActionInvocation("urn:Belkin:service:insight:1", "GetPower", null);
        assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?><s:Envelope s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\"><s:Body><u:GetPower xmlns:u=\"urn:Belkin:service:insight:1\"></u:GetPower></s:Body></s:Envelope>", ai.toXML());
    }
}
