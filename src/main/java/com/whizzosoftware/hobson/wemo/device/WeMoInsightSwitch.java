/*******************************************************************************
 * Copyright (c) 2014 Whizzo Software, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.whizzosoftware.hobson.wemo.device;

import com.whizzosoftware.hobson.api.device.DeviceType;
import com.whizzosoftware.hobson.api.plugin.HobsonPlugin;
import com.whizzosoftware.hobson.api.variable.HobsonVariable;
import com.whizzosoftware.hobson.api.variable.VariableConstants;
import com.whizzosoftware.hobson.api.variable.VariableUpdate;
import com.whizzosoftware.hobson.wemo.api.WeMoInsightState;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A HobsonDevice implementation of a WeMo Insight Switch. It provides on/off functionality as well as device
 * statistics for energy consumption.
 *
 * @author Dan Noguerol
 */
public class WeMoInsightSwitch extends WeMoDevice {
    public final static String ACTION_GET_INSIGHT_PARAMS = "GetInsightParams";
    public final static String PROP_INSIGHT_PARAMS = "InsightParams";
    public final static String SERVICE_INSIGHT1 = "urn:Belkin:service:insight:1";

    private Integer lastState;
    private Double lastECW;

    public WeMoInsightSwitch(HobsonPlugin plugin, String id, String baseURI, Document doc) {
        super(plugin, id, baseURI, doc);
    }

    @Override
    public void onStartup() {
        publishVariable(VariableConstants.ON, true, HobsonVariable.Mask.READ_WRITE);
        publishVariable(VariableConstants.FIRMWARE_VERSION, getFirmwareVersion(), HobsonVariable.Mask.READ_ONLY);
        publishVariable(VariableConstants.ENERGY_CONSUMPTION_WATTS, null, HobsonVariable.Mask.READ_ONLY);
    }

    @Override
    public void onShutdown() {
    }

    @Override
    public DeviceType getType() {
        return DeviceType.SWITCH;
    }

    @Override
    public String getPreferredVariableName() {
        return VariableConstants.ON;
    }

    @Override
    public String[] getTelemetryVariableNames() {
        return new String[] { VariableConstants.ENERGY_CONSUMPTION_WATTS };
    }

    @Override
    public void onRefresh() {
        try {
            invokeAction(SERVICE_INSIGHT1, ACTION_GET_INSIGHT_PARAMS, null);
        } catch (Exception e) {
            logger.error("Error refreshing device " + getId(), e);
        }
    }

    @Override
    public void onSetVariable(final String name, final Object value) {
        try {
            if (VariableConstants.ON.equals(name)) {
                logger.debug("Setting device variable on to {}", value);
                Map<String, Object> props = new HashMap<>();
                props.put(PROP_BINARY_STATE, ((Boolean)value) ? "1" : "0");
                invokeAction(SERVICE_BASICEVENT1, ACTION_SET_BINARY_STATE, props);
            }
        } catch (Exception e) {
            logger.error("Error setting variable", e);
        }
    }

    @Override
    public void onStateUpdate(Map<String,Object> update) {
        if (update.containsKey(PROP_BINARY_STATE)) {
            onInsightStateUpdate(WeMoInsightState.create((String) update.get(PROP_BINARY_STATE)));
        } else if (update.containsKey(PROP_INSIGHT_PARAMS)) {
            onInsightStateUpdate(WeMoInsightState.create((String) update.get(PROP_INSIGHT_PARAMS)));
        }
    }

    private void onInsightStateUpdate(WeMoInsightState state) {
        List<VariableUpdate> updates = null;

        if (lastState == null || lastState != state.getState()) {
            updates = new ArrayList<>();
            updates.add(new VariableUpdate(getPluginId(), getId(), VariableConstants.ON, state.getState() > 0));
        }
        if (lastECW == null || lastECW != state.getCurrentMw()) {
            if (updates == null) {
                updates = new ArrayList<>();
            }
            updates.add(new VariableUpdate(getPluginId(), getId(), VariableConstants.ENERGY_CONSUMPTION_WATTS, state.getCurrentMw() / 1000));
        }

        if (updates != null) {
            logger.trace("Updating variables: {}", updates);
            fireVariableUpdateNotifications(updates);
        }

        lastState = state.getState();
        lastECW = state.getCurrentMw();
    }
}
