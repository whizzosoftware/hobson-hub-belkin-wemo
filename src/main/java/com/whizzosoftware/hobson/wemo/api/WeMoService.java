/*******************************************************************************
 * Copyright (c) 2014 Whizzo Software, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.whizzosoftware.hobson.wemo.api;

import com.whizzosoftware.hobson.api.HobsonRuntimeException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Represents an exposed WeMo device service.
 *
 * @author Dan Noguerol
 */
public class WeMoService {
    private URI baseURI;
    private String serviceType;
    private String serviceId;
    private URI controlURI;
    private URI eventSubURI;
    private URI SCPDURI;

    public WeMoService(String baseURI, Element serviceElem) {
        try {
            this.baseURI = new URI(baseURI);
            NodeList nl = serviceElem.getChildNodes();
            for (int i=0; i < nl.getLength(); i++) {
                Node n = nl.item(i);
                if ("serviceType".equals(n.getNodeName())) {
                    this.serviceType = n.getTextContent();
                } else if ("serviceId".equals(n.getNodeName())) {
                    this.serviceId = n.getTextContent();
                } else if ("controlURL".equals(n.getNodeName())) {
                    this.controlURI = createURI(n.getTextContent());
                } else if ("eventSubURL".equals(n.getNodeName())) {
                    this.eventSubURI = createURI(n.getTextContent());
                } else if ("SCPDURL".equals(n.getNodeName())) {
                    this.SCPDURI = createURI(n.getTextContent());
                }
            }
        } catch (URISyntaxException e) {
            throw new HobsonRuntimeException("Error parsing WeMo service descriptor", e);
        }
    }

    public String getServiceType() {
        return serviceType;
    }

    public String getServiceId() {
        return serviceId;
    }

    public URI getControlURI() {
        return controlURI;
    }

    public URI getEventSubURI() {
        return eventSubURI;
    }

    public URI getSCPDURI() {
        return SCPDURI;
    }

    protected URI createURI(String relativePath) throws URISyntaxException {
        return new URI(baseURI.getScheme(), null, baseURI.getHost(), baseURI.getPort(), relativePath, null, null);
    }
}
