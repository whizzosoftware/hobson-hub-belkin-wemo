/*******************************************************************************
 * Copyright (c) 2014 Whizzo Software, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.whizzosoftware.hobson.wemo;

import com.whizzosoftware.hobson.wemo.api.WeMoInsightState;
import org.junit.Test;
import static org.junit.Assert.*;

public class WeMoInsightStateTest {
    @Test
    public void testCreate() {
        WeMoInsightState p = WeMoInsightState.create("1|1418067045|103|103|101|9147|63|62180|95593|95593.000000|8000");
        assertEquals(1, (int)p.getState());
        assertEquals("1418067045", p.getLastChange());
        assertEquals("103", p.getOnFor());
        assertEquals("103", p.getOnToday());
        assertEquals("101", p.getOnTotal());
        assertEquals("9147", p.getTimePeriod());
        assertEquals(62180.0, p.getCurrentMw(), 0);
        assertEquals("95593", p.getTodayMw());
        assertEquals("95593.000000", p.getTotalMw());
        assertEquals("8000", p.getPowerThreshold());
    }
}
