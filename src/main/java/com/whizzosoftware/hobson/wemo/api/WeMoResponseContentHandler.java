/*******************************************************************************
 * Copyright (c) 2014 Whizzo Software, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.whizzosoftware.hobson.wemo.api;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Map;

/**
 * A SAX handler for parsing WeMo device responses.
 *
 * @author Dan Noguerol
 */
public class WeMoResponseContentHandler extends DefaultHandler {
    private Map<String,Object> results;
    private State state = State.START;
    private String responseParam;
    private StringBuilder responseParamContent;

    public WeMoResponseContentHandler(Map<String,Object> results) {
        this.results = results;
    }

    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) {
        switch (state) {
            case START:
                if ("Envelope".equals(localName)) {
                    state = State.ENVELOPE;
                }
                break;
            case ENVELOPE:
                if ("Body".equals(localName)) {
                    state = State.BODY;
                }
                break;
            case BODY:
                if (localName != null && localName.endsWith("Response")) {
                    state = State.RESPONSE;
                }
                break;
            case RESPONSE:
                if (localName != null) {
                    this.responseParam = localName;
                    this.responseParamContent = new StringBuilder();
                    state = State.RESPONSE_PARAM;
                }
                break;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        switch (state) {
            case RESPONSE_PARAM:
                results.put(responseParam, responseParamContent.toString());
                this.responseParam = null;
                this.responseParamContent = null;
                state = State.RESPONSE;
                break;
        }
    }

    @Override
    public void characters (char ch[], int start, int length) throws SAXException {
        switch (state) {
            case RESPONSE_PARAM:
                responseParamContent.append(new String(ch, start, length));
                break;
        }
    }

    private enum State {
        START,
        ENVELOPE,
        BODY,
        RESPONSE,
        RESPONSE_PARAM
    }
}
