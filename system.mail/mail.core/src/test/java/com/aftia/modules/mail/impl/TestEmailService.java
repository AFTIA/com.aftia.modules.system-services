package com.aftia.modules.mail.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.aftia.modules.mail.bean.EmailAttachment;
import com.aftia.modules.mail.bean.EmailContent;
import com.aftia.modules.mail.bean.EmailRecipient;
import com.aftia.modules.mail.bean.SmtpAuthentication;
import com.aftia.modules.mail.bean.SmtpServer;
import com.aftia.modules.mail.exceptions.EmailTransmissionException;
import com.aftia.modules.mail.exceptions.InvalidAddressException;
import com.aftia.modules.mail.template.Templater;
import com.aftia.modules.enc.impl.SimpleSecurity;

public class TestEmailService {

    public SmtpServer getSmtpServer() {
        SmtpServer smtpServer = new SmtpServer();
        smtpServer.setSmtpHost("test.test");
        smtpServer.setSmtpPort(43);
        smtpServer.setSmtpProtocol("tls");

        return smtpServer;
    }

    public SmtpAuthentication getSmtpAuthentication() {
        SmtpAuthentication smtpAuthentication = new SmtpAuthentication();
        smtpAuthentication.setPassword("ckClJXHGxUnVjJcR2LOFyg==");
        smtpAuthentication.setUsername("username");

        return smtpAuthentication;
    }

    public SimpleSecurity getSecurity() {
        SimpleSecurity security = new SimpleSecurity();
        security.activate();

        return security;
    }

    @Test
    public void testGetUsername() {
        EmailServiceImpl emailServiceImpl = new EmailServiceImpl();
        assertNull(emailServiceImpl.getUsername());

        emailServiceImpl.setSmtpAuthentication(getSmtpAuthentication());

        assertEquals("username", emailServiceImpl.getUsername());
    }

    @Test
    public void testGetPassword() {
        EmailServiceImpl emailServiceImpl = new EmailServiceImpl();
        assertNull(emailServiceImpl.getPassword());

        emailServiceImpl.setSmtpAuthentication(getSmtpAuthentication());
        emailServiceImpl.security = getSecurity();

        assertEquals("test", emailServiceImpl.getPassword());
    }

    @Test
    public void testSetupSession() throws AddressException {
        EmailRecipient recipient = Mockito.mock(EmailRecipient.class);

        Mockito.when(recipient.getBounce()).thenReturn("test@test.com");

        EmailServiceImpl emailServiceImpl = new EmailServiceImpl();
        emailServiceImpl.setSmtpAuthentication(getSmtpAuthentication());
        emailServiceImpl.setSmtpServer(getSmtpServer());
        emailServiceImpl.security = getSecurity();
        emailServiceImpl.setupSession(recipient);

        Session session = emailServiceImpl.setupSession(recipient);
        assertEquals("test.test", session.getProperty("mail.smtp.host"));
        assertEquals("43", session.getProperty("mail.smtp.port"));
        assertEquals("true", session.getProperty("mail.smtp.starttls.enable"));
    }

    @Test
    public void testSetupSession_unauthenticated() throws AddressException {
        EmailRecipient recipient = Mockito.mock(EmailRecipient.class);

        Mockito.when(recipient.getBounce()).thenReturn("test@test.com");

        EmailServiceImpl emailServiceImpl = new EmailServiceImpl();
        emailServiceImpl.setSmtpServer(getSmtpServer());
        emailServiceImpl.security = getSecurity();
        emailServiceImpl.setupSession(recipient);

        Session session = emailServiceImpl.setupSession(recipient);
        assertEquals("test.test", session.getProperty("mail.smtp.host"));
        assertEquals("43", session.getProperty("mail.smtp.port"));
        assertEquals("true", session.getProperty("mail.smtp.starttls.enable"));
        assertEquals("false", session.getProperty("mail.smtp.auth"));
        assertEquals("test@test.com", session.getProperty("mail.smtp.from"));
    }

    @Test
    public void testSetTo_v1() throws MessagingException, InvalidAddressException {
        EmailServiceImpl emailServiceImpl = new EmailServiceImpl();
        Message message = Mockito.mock(Message.class);

        EmailContent.Builder builder = new EmailContent.Builder();
        EmailContent content = builder.addTo("test.test@test.com").addTo("test.test2@test.com").build();

        emailServiceImpl.setTo(message, content);
    }

    @Test
    public void testSetTo_v1_Error() throws MessagingException {
        EmailServiceImpl emailServiceImpl = new EmailServiceImpl();
        Message message = Mockito.mock(Message.class);

        EmailContent.Builder builder = new EmailContent.Builder();
        EmailContent content = builder.build();

        Assertions.assertThrows(MessagingException.class, () -> {
            emailServiceImpl.setTo(message, content);
        });
    }

    @Test
    public void testSetTo_v2() throws MessagingException {
        EmailServiceImpl emailServiceImpl = new EmailServiceImpl();
        Message message = Mockito.mock(Message.class);

        emailServiceImpl.setTo(message, new InternetAddress("pat@pat.com"));
    }

    @Test
    public void testSetBcc() throws MessagingException, InvalidAddressException {
        EmailServiceImpl emailServiceImpl = new EmailServiceImpl();
        Message message = Mockito.mock(Message.class);

        EmailContent.Builder builder = new EmailContent.Builder();
        EmailContent content = builder.addBcc("test.test@test.com").addBcc("test.test2@test.com").build();

        emailServiceImpl.setBCC(message, content);
    }

    @Test
    public void testSetCc() throws InvalidAddressException, MessagingException {
        EmailServiceImpl emailServiceImpl = new EmailServiceImpl();
        Message message = Mockito.mock(Message.class);

        EmailContent.Builder builder = new EmailContent.Builder();
        EmailContent content = builder.addCc("test.test@test.com").addCc("test.test2@test.com").build();

        emailServiceImpl.setCC(message, content);
    }

    @Test
    public void testSetFrom() throws MessagingException {
        EmailServiceImpl emailServiceImpl = new EmailServiceImpl();
        Message message = Mockito.mock(Message.class);
        EmailRecipient recipient = Mockito.mock(EmailRecipient.class);
        Mockito.when(recipient.getFrom()).thenReturn("test.test@test.com");

        emailServiceImpl.setFrom(message, recipient);
    }

    @Test
    public void testSetFrom_Error() throws MessagingException {
        EmailServiceImpl emailServiceImpl = new EmailServiceImpl();
        Message message = Mockito.mock(Message.class);
        EmailRecipient recipient = Mockito.mock(EmailRecipient.class);
        Mockito.when(recipient.getFrom()).thenReturn(null);

        Assertions.assertThrows(MessagingException.class, () -> {
            emailServiceImpl.setFrom(message, recipient);
        });

    }

    @Test
    public void testSetAttachment() throws MessagingException, IOException {
        EmailServiceImpl emailServiceImpl = new EmailServiceImpl();

        EmailContent.Builder builder = new EmailContent.Builder();
        EmailContent content = builder
                .addAttachment(new EmailAttachment(IOUtils.toInputStream("Test 1", "UTF-8"), "text/plain"))
                .addAttachment(new EmailAttachment(IOUtils.toInputStream("Test 2", "UTF-8"), "text/plain")).build();

        Multipart multipart = Mockito.mock(Multipart.class);

        Iterator<EmailAttachment> attachmentIterator = content.getAttachments().iterator();

        emailServiceImpl.setAttachment(multipart, attachmentIterator.next());
        emailServiceImpl.setAttachment(multipart, attachmentIterator.next());

    }

    @Test
    public void testSetContent() throws MessagingException {
        EmailServiceImpl emailServiceImpl = new EmailServiceImpl();

        EmailContent.Builder builder = new EmailContent.Builder();
        EmailContent content = builder.addMessage("This is my content").build();

        Templater templater = Mockito.mock(Templater.class);
        Multipart multipart = Mockito.mock(Multipart.class);

        Mockito.when(templater.templateString(Mockito.anyString())).thenReturn("This is my templated content");

        emailServiceImpl.setContent(multipart, content, templater);
    }

    @Test
    public void testSetContent_Plain() throws MessagingException {
        EmailServiceImpl emailServiceImpl = new EmailServiceImpl();

        EmailContent.Builder builder = new EmailContent.Builder();
        EmailContent content = builder.addMessage("This is my content").setHTML(Boolean.FALSE).build();

        Templater templater = Mockito.mock(Templater.class);
        Multipart multipart = Mockito.mock(Multipart.class);

        Mockito.when(templater.templateString(Mockito.anyString())).thenReturn("This is my templated content");

        emailServiceImpl.setContent(multipart, content, templater);
    }

    @Test
    public void testSetContentNull() throws MessagingException {
        EmailServiceImpl emailServiceImpl = new EmailServiceImpl();

        EmailContent.Builder builder = new EmailContent.Builder();
        EmailContent content = builder.addMessage("This is my content").build();

        Multipart multipart = Mockito.mock(Multipart.class);

        emailServiceImpl.setContent(multipart, content, null);
    }

    @Test
    public void testSend() throws MessagingException, IOException, InvalidAddressException, EmailTransmissionException {
        Multipart multipart = Mockito.mock(Multipart.class);
        EmailServiceImpl emailServiceImpl = Mockito.mock(EmailServiceImpl.class);
        Mockito.doCallRealMethod().when(emailServiceImpl).send(Mockito.any(), Mockito.any(), Mockito.any());
        
        EmailRecipient recipients = Mockito.mock(EmailRecipient.class);
        Mockito.when(recipients.getFrom()).thenReturn("patrique.pat@pat.com");
        Mockito.when(emailServiceImpl.setContent(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(multipart);

        EmailContent.Builder builder = new EmailContent.Builder();
        EmailContent content = builder.addTo("ma@ma.com").addMessage("This is my message")
                .addAttachment(new EmailAttachment(IOUtils.toInputStream("Hello World", "UTF-8"), "text/plain"))
                .build();

        emailServiceImpl.send(recipients, null, content);

    }

    @Test
    public void testSend_Unique() throws MessagingException, UnsupportedEncodingException, InvalidAddressException,
            EmailTransmissionException {
        Multipart multipart = Mockito.mock(Multipart.class);

        EmailServiceImpl emailServiceImpl = Mockito.mock(EmailServiceImpl.class);
        Mockito.doCallRealMethod().when(emailServiceImpl).send(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.when(emailServiceImpl.setContent(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(multipart);
        EmailRecipient recipients = Mockito.mock(EmailRecipient.class);
        Mockito.when(recipients.getFrom()).thenReturn("patrique.pat@pat.com");

        EmailContent.Builder builder = new EmailContent.Builder();
        EmailContent content = builder.addTo("ma@ma.com").addTo("ma2@ma.com")
                .addMessage("This is my message").setSendIndependently().build();

        emailServiceImpl.send(recipients, null, content);

    }

    @Test
    public void testSend_Fail_No_To() throws MessagingException, UnsupportedEncodingException,
            EmailTransmissionException {
        Multipart multipart = Mockito.mock(Multipart.class);
        EmailContent.Builder builder = new EmailContent.Builder();
        EmailContent content = builder.addMessage("This is my message").build();

        EmailServiceImpl emailServiceImpl = Mockito.mock(EmailServiceImpl.class);

        Mockito.when(emailServiceImpl.setContent(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(multipart);
        Mockito.doCallRealMethod().when(emailServiceImpl).send(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.doCallRealMethod().when(emailServiceImpl).setTo(Mockito.any(), Mockito.any(EmailContent.class));

        EmailRecipient recipients = Mockito.mock(EmailRecipient.class);
        Mockito.when(recipients.getFrom()).thenReturn("patrique.pat@pat.com");

        Assertions.assertThrows(EmailTransmissionException.class, () -> {
            emailServiceImpl.send(recipients, null, content);
        });

    }

    @Test
    public void testSetContentNull_Error() throws MessagingException {
        EmailServiceImpl emailServiceImpl = new EmailServiceImpl();

        EmailContent.Builder builder = new EmailContent.Builder();
        EmailContent content = builder.build();

        Multipart multipart = Mockito.mock(Multipart.class);

        Assertions.assertThrows(MessagingException.class, () -> {
            emailServiceImpl.setContent(multipart, content, null);
        });

    }
}