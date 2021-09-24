package com.aftia.modules.enc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.aftia.modules.enc.impl.SimpleSecurity;

public class TestSimpleSecurity {

    @Test
    public void testEnc() {
        SimpleSecurity security = new SimpleSecurity();
        security.activate();
        String result = security.encrypt("test");
        assertEquals("test", security.decrypt(result));
    }
    
}
