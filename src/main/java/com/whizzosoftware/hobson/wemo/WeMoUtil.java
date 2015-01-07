/*******************************************************************************
 * Copyright (c) 2014 Whizzo Software, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.whizzosoftware.hobson.wemo;

import com.whizzosoftware.hobson.wemo.api.WeMoResponseContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides some convenience functions.
 *
 * @author Dan Noguerol
 */
public class WeMoUtil {
    /**
     * Create a Map of response properties from an XML response.
     *
     * @param response the XML response String
     *
     * @return a Map of response properties
     * @throws Exception on failure
     */
    static public Map<String,Object> createResponseMap(String response) throws Exception {
        Map<String,Object> result = new HashMap<>();

        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        SAXParser parser = factory.newSAXParser();
        XMLReader xmlReader = parser.getXMLReader();
        xmlReader.setContentHandler(new WeMoResponseContentHandler(result));
        xmlReader.parse(new InputSource(new StringReader(response)));

        return result;
    }
}
