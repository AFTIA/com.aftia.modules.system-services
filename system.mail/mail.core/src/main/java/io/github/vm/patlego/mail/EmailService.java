package com.aftia.modules.mail;

import java.io.UnsupportedEncodingException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.aftia.modules.mail.bean.EmailContent;
import com.aftia.modules.mail.bean.EmailRecipient;
import com.aftia.modules.mail.exceptions.EmailTransmissionException;
import com.aftia.modules.mail.template.Templater;

public interface EmailService {

    public void send(@Nonnull EmailRecipient recipients, @Nullable Templater template, @Nonnull EmailContent content) throws EmailTransmissionException, UnsupportedEncodingException;
    
}
