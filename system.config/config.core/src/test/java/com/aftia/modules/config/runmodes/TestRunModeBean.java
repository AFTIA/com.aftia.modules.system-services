package com.aftia.modules.config.runmodes;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.aftia.modules.config.runmodes.RunModeBean;

import org.junit.jupiter.api.Test;

public class TestRunModeBean {

    @Test
    public void testBean() {
        RunModeBean bean = new RunModeBean();
        bean.setRunmode("DEV");
        assertEquals("DEV", bean.getRunmode());
    }
    
}
