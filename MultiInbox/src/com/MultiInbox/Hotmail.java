
package com.MultiInbox;

import java.util.Date;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
@SuppressWarnings("unused")
public class Hotmail extends javax.mail.Authenticator {
    private String _user;
    private String _pass;

    private String[] _to;
    private String _from;

    
    private String _port;
    
    private String _sport;
    private String _host;

    private String _subject;
    private String _body;

    private boolean _auth;

    private boolean _debuggable;

    private Multipart _multipart;

    public Hotmail() {
        _host = "pop3.live.com"; // default smtp server
        _port = "995"; // default smtp port
        _sport = "995"; // default socketfactory port

        _user = ""; // username
        _pass = ""; // password
        _from = ""; // email sent from
        _subject = ""; // email subject
        _body = ""; // email body

        _debuggable = false; // debug mode on or off - default off
        _auth = true; // smtp authentication - default on

        _multipart = new MimeMultipart();

        // There is something wrong with MailCap, javamail can not find a
        // handler for the multipart/mixed part, so this bit needs to be added.
        MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
        mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
        mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
        mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
        mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
        mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
        CommandMap.setDefaultCommandMap(mc);
    }

    public Hotmail(String user, String pass) {
        this();

        _user = user;
        _pass = pass;
    }

    public boolean send() throws Exception {
        Properties props = _setProperties();
        
        if (!_user.equals("") && !_pass.equals("") && _to.length > 0 && !_from.equals("")
                && !_subject.equals("") && !_body.equals("")) {
            
            try {
                
                Session session = Session.getInstance(props,this);
                
                MimeMessage msg = new MimeMessage(session);

                msg.setFrom(new InternetAddress(_from));

                InternetAddress[] addressTo = new InternetAddress[_to.length];
                for (int i = 0; i < _to.length; i++) {
                    addressTo[i] = new InternetAddress(_to[i]);
                }
                msg.setRecipients(MimeMessage.RecipientType.TO, addressTo);

                msg.setSubject(_subject);
                msg.setSentDate(new Date());

                // setup message body
                BodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setText(_body);
                _multipart.addBodyPart(messageBodyPart);

                // Put parts in message
                msg.setContent(_multipart);

                // send email
                Transport.send(msg);

                
                return true;
            } catch (Exception e) {
                
                return false;
            }

        } else {
            return false;
        }
    }
    
    public static Message[] getMails(final String username,final String password) {
        Properties props = System.getProperties();
        
        props.setProperty("mail.pop3.ssl.enable", "true");
        props.setProperty("mail.pop3s.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.pop3s.socketFactory.fallback", "false");
        props.setProperty("mail.pop3s.port", "995");
        props.setProperty("mail.pop3s.socketFactory.port", "995");
        
        
        Authenticator auth = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username,password);
            }
            
        };
        
        Session sessioned = Session.getDefaultInstance(props, auth);
        
        Store store;
        try {
            store = sessioned.getStore("pop3s");
            store.connect("pop3.live.com",995,username,password);
           
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);
           // FlagTerm ft = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
            //Message[] unReadMessages = inbox.search(ft);
            
            //Message[] unReadMessages = inbox.getMessages(1,15);
            //return unReadMessages;
            return inbox.getMessages(1,15);
        } catch (NoSuchProviderException e) {
            return null;
        } catch (MessagingException e) {
            return null;
        }

    }


    public void addAttachment(String filename) throws Exception {
        BodyPart messageBodyPart = new MimeBodyPart();
        DataSource source = new FileDataSource(filename);
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(filename);

        _multipart.addBodyPart(messageBodyPart);
    }

    @Override
    public PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(_user, _pass);
    }

    public static boolean doHotmailLogin(final String username,final String password){
        Properties props = System.getProperties();
        
        props.setProperty("mail.pop3.ssl.enable", "true");
        props.setProperty("mail.pop3s.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.pop3s.socketFactory.fallback", "false");
        props.setProperty("mail.pop3s.port", "995");
        props.setProperty("mail.pop3s.socketFactory.port", "995");
        
        Authenticator auth = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username,password);
            }
            
        };
        
        Session sessioned = Session.getDefaultInstance(props, auth);
        
        Store store;
        try {
            store = sessioned.getStore("pop3s");
            store.connect("pop3.live.com",995,username,password);
           
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);
            if(inbox.exists()){
                return true;
            }
            else{
                return false;
           }
           
           
        } catch (NoSuchProviderException e) {
            return false;
        } catch (MessagingException e) {
            return false;
        }

    }
    private Properties _setProperties() {
        Properties props = new Properties();

        if (_debuggable) {
            props.put("mail.debug", "true");
        }

        if (_auth) {
            props.put("mail.smtp.auth", "true");
        }

        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.host", "smtp.live.com");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        
        return props;
    }

    // the getters and setters
    public String getBody() {
        return _body;
    }

    public void setBody(String _body) {
        this._body = _body;
    }

    public void setTo(String[] toArray) {
        this._to = toArray;
    }
    
    public String[] getTo(){
        return _to;
    }
    
    public void setSubject(String subject){
        this._subject = subject;
    }
    public String getSubject(){
        return _subject;
    }
    
    public void setFrom(String from){
        this._from = from;
    }
    public String getFrom(){
        return _from;
    }
    
}
