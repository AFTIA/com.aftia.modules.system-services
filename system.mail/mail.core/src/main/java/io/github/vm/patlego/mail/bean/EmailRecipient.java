package com.aftia.modules.mail.bean;

import javax.annotation.Nonnull;

public interface EmailRecipient {

    public @Nonnull String getFrom();

    public @Nonnull String getBounce();
}