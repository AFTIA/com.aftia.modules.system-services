package com.aftia.modules.enc.commands;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.aftia.modules.enc.commands.EncryptCommand;
import com.aftia.modules.enc.impl.SimpleSecurity;

public class TestEncryptCommand {

    @Test
    public void testEncrypt() throws Exception {
        EncryptCommand enc = new EncryptCommand();
        SimpleSecurity security = new SimpleSecurity();
        security.activate();
        enc.security = security;
        enc.input = "test";
        assertNotNull(enc.execute());
    }
    
}
