/*******************************************************************************
 * Copyright (c) 2014 Whizzo Software, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.whizzosoftware.hobson.wemo.device;

import com.whizzosoftware.hobson.api.plugin.HobsonPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.net.URI;

/**
 * A factory for creating WeMoDevice instances from an XML response document. It is driven by the device name
 * and model and uses the device serial number as the unique device ID.
 *
 * @author Dan Noguerol
 */
public class WeMoDeviceFactory {
    private static final Logger logger = LoggerFactory.getLogger(WeMoDeviceFactory.class);

    static public WeMoDevice createWeMoDevice(HobsonPlugin plugin, URI baseURI, Document doc) {
        NodeList nl = doc.getElementsByTagName("device");
        if (nl != null && nl.getLength() == 1) {
            Element deviceElem = (Element)nl.item(0);
            nl = deviceElem.getElementsByTagName("modelName");
            if (nl != null && nl.getLength() == 1) {
                Element modelNameElem = (Element)nl.item(0);
                return createWeMoDevice(plugin, baseURI, doc, modelNameElem.getTextContent().trim());
            }
        }
        return null;
    }

    static public WeMoDevice createWeMoDevice(HobsonPlugin plugin, URI baseURI, Document doc, String modelName) {
        NodeList nl = doc.getElementsByTagName("serialNumber");
        if (nl != null && nl.getLength() == 1) {
            switch (modelName.toUpperCase()) {
                case "INSIGHT":
                    return new WeMoInsightSwitch(plugin, nl.item(0).getTextContent().trim(), baseURI.toASCIIString(), doc);
                default:
                    return null;
            }
        } else {
            logger.warn("Found WeMo device with no serial number at {}; ignoring", baseURI);
        }
        return null;
    }
}
