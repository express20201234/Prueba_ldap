package sv.gob.mh.dinafi.controlmarcacion.web.ldap;

import java.io.Serializable;
import lombok.Data;

@Data
public class MailBox implements Serializable {

    private static final long serialVersionUID = 202101161057005L;

    private String from;
    private String to;
    private String cc;
    private String subject;
    private String message;

}
