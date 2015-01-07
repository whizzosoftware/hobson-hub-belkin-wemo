/*******************************************************************************
 * Copyright (c) 2014 Whizzo Software, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.whizzosoftware.hobson.wemo;

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.util.Map;

public class WeMoUtilTest {
    @Test
    public void testCreateResponseMapForGet() throws Exception {
        Map<String,Object> result = WeMoUtil.createResponseMap("<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\"><s:Body><u:GetBinaryStateResponse xmlns:u=\"urn:Belkin:service:basicevent:1\"><BinaryState>0</BinaryState></u:GetBinaryStateResponse></s:Body></s:Envelope>");
        assertEquals(1, result.size());
        assertEquals("0", result.get("BinaryState"));
    }

    @Test
    public void testCreateResponseMapForSet() throws Exception {
        Map<String,Object> result = WeMoUtil.createResponseMap("<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\"><s:Body><u:SetBinaryStateResponse xmlns:u=\"urn:Belkin:service:basicevent:1\"><BinaryState>0|1418059459|0|0|0|1459|0|0|0|0</BinaryState></u:SetBinaryStateResponse></s:Body></s:Envelope>");
        assertEquals(1, result.size());
        assertEquals("0|1418059459|0|0|0|1459|0|0|0|0", result.get("BinaryState"));
    }
}
