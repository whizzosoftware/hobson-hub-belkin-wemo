/*******************************************************************************
 * Copyright (c) 2014 Whizzo Software, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.whizzosoftware.hobson.wemo.api;

import java.util.StringTokenizer;

/**
 * Represents the state of a WeMo Insight Switch.
 *
 * @author Dan Noguerol
 */
public class WeMoInsightState {
    private Integer state;
    private String lastChange;
    private String onFor;
    private String onToday;
    private String onTotal;
    private String timePeriod;
    private String todayMw;
    private String totalMw;
    private Double currentMw;
    private String powerThreshold;

    static public WeMoInsightState create(String s) {
        WeMoInsightState param = new WeMoInsightState();
        StringTokenizer tok = new StringTokenizer(s, "|");
        if (tok.hasMoreTokens()) {
            param.setState(Integer.parseInt(tok.nextToken()));
        }
        if (tok.hasMoreTokens()) {
            param.setLastChange(tok.nextToken());
        }
        if (tok.hasMoreTokens()) {
            param.setOnFor(tok.nextToken());
        }
        if (tok.hasMoreTokens()) {
            param.setOnToday(tok.nextToken());
        }
        if (tok.hasMoreTokens()) {
            param.setOnTotal(tok.nextToken());
        }
        if (tok.hasMoreTokens()) {
            param.setTimePeriod(tok.nextToken());
        }
        if (tok.hasMoreTokens()) {
            tok.nextToken(); // not sure what this is
        }
        if (tok.hasMoreTokens()) {
            param.setCurrentMw(Double.parseDouble(tok.nextToken()));
        }
        if (tok.hasMoreTokens()) {
            param.setTodayMw(tok.nextToken());
        }
        if (tok.hasMoreTokens()) {
            param.setTotalMw(tok.nextToken());
        }
        if (tok.hasMoreTokens()) {
            param.setPowerThreshold(tok.nextToken());
        }
        return param;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getLastChange() {
        return lastChange;
    }

    public void setLastChange(String lastChange) {
        this.lastChange = lastChange;
    }

    public String getOnFor() {
        return onFor;
    }

    public void setOnFor(String onFor) {
        this.onFor = onFor;
    }

    public String getOnToday() {
        return onToday;
    }

    public void setOnToday(String onToday) {
        this.onToday = onToday;
    }

    public String getOnTotal() {
        return onTotal;
    }

    public void setOnTotal(String onTotal) {
        this.onTotal = onTotal;
    }

    public String getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(String timePeriod) {
        this.timePeriod = timePeriod;
    }

    public String getTodayMw() {
        return todayMw;
    }

    public void setTodayMw(String todayMw) {
        this.todayMw = todayMw;
    }

    public String getTotalMw() {
        return totalMw;
    }

    public void setTotalMw(String totalMw) {
        this.totalMw = totalMw;
    }

    public Double getCurrentMw() {
        return currentMw;
    }

    public void setCurrentMw(Double currentMw) {
        this.currentMw = currentMw;
    }

    public String getPowerThreshold() {
        return powerThreshold;
    }

    public void setPowerThreshold(String powerThreshold) {
        this.powerThreshold = powerThreshold;
    }
}
