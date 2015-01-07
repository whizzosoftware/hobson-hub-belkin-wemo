/*******************************************************************************
 * Copyright (c) 2014 Whizzo Software, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.whizzosoftware.hobson.wemo.api;

import java.util.Map;

/**
 * Encapsulates the details of a WeMo device remote action invocation.
 *
 * @author Dan Noguerol
 */
public class WeMoActionInvocation {
    private String xml;

    public WeMoActionInvocation(String actionURN, String actionId, Map<String, Object> properties) throws Exception {
        StringBuilder sb = new StringBuilder("<?xml version=\"1.0\" encoding=\"utf-8\"?><s:Envelope s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\"><s:Body>");
        sb.append("<u:").append(actionId).append(" xmlns:u=\"").append(actionURN).append("\">");
        if (properties != null) {
            for (String key : properties.keySet()) {
                sb.append("<").append(key).append(">").append(properties.get(key)).append("</").append(key).append(">");
            }
        }
        sb.append("</u:").append(actionId).append("></s:Body></s:Envelope>");
        this.xml = sb.toString();
    }

    public String toXML() throws Exception {
        return xml;
    }
}
