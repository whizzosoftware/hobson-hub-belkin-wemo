/*******************************************************************************
 * Copyright (c) 2014 Whizzo Software, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.whizzosoftware.hobson.wemo;

import com.whizzosoftware.hobson.wemo.device.WeMoDevice;

/**
 * Class that encapsulates the details of a WeMo device request.
 *
 * @author Dan Noguerol
 */
public class WeMoDeviceRequestContext {
    private WeMoDevice device;
    private Object context;

    public WeMoDeviceRequestContext(WeMoDevice device, Object context) {
        this.device = device;
        this.context = context;
    }

    public WeMoDevice getDevice() {
        return device;
    }

    public Object getContext() {
        return context;
    }
}
