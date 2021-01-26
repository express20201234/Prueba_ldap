/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sv.gob.mh.dinafi.controlmarcacion.web.ldap;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Stateless
public class MailUtil implements Serializable {

    private static final long serialVersionUID = 1530600271341725477L;

    @Resource(mappedName = "java:/Safi")
    private Session mailSession;

    @Asynchronous
    public void sendMail(String from, String to, String cc, String subject, String msg) {

        try {
            MimeMessage m = new MimeMessage(mailSession);
            Address addressFrom = new InternetAddress(from);
            Address[] addressTo = new InternetAddress[]{new InternetAddress(to)};

            m.setFrom(addressFrom);
            m.setRecipients(Message.RecipientType.TO, addressTo);
            m.setSubject(subject, "utf-8");
            m.setSentDate(new java.util.Date());
            m.setContent(msg, "text/plain; charset=utf-8");
            Transport.send(m);
        } catch (javax.mail.MessagingException e) {
            //e.printStackTrace();
            Logger.getLogger(MailUtil.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    @Asynchronous
    public void sendMailHTML(String from, String to, String subject, String msg) throws MessagingException {

        try {
            MimeMessage m = new MimeMessage(mailSession);
            Address addressFrom = new InternetAddress(from);
            Address[] addressTo = new InternetAddress[]{new InternetAddress(to)};

            m.setFrom(addressFrom);
            m.setRecipients(Message.RecipientType.TO, addressTo);
            m.setSubject(subject, "utf-8");
            m.setSentDate(new java.util.Date());
            m.setContent(msg, "text/html; charset=utf-8");
            Transport.send(m);
        } catch (javax.mail.MessagingException e) {
            //e.printStackTrace();
            Logger.getLogger(MailUtil.class.getName()).log(Level.SEVERE, null, e);
        }
    }

}
