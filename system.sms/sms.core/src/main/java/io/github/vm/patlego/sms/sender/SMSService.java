package com.aftia.modules.sms.sender;

import java.util.Map;

import com.twilio.rest.api.v2010.account.Message;

import com.aftia.modules.sms.sender.bean.SMSMessage;

public interface SMSService {
    
    /**
     * 
     * @param smsMessage
     * @return Returns the to number linked to the message object sent
     */
    public Map<String, Message> sendMessage(SMSMessage smsMessage);
}
