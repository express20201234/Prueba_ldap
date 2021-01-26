package sv.gob.mh.dinafi.controlmarcacion.web.ldap.domain;

import java.io.Serializable;
import javax.naming.Name;
import lombok.Getter;
import lombok.Setter;
import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.DnAttribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;
import org.springframework.ldap.odm.annotations.Transient;

@Entry(objectClasses = {"pwdPolicy", "top"})
@Getter
@Setter
public class SegPolicy implements Serializable {

    private static final long serialVersionUID = -978136186526802075L;

    @Id
    private Name dn;

    @Transient
    @DnAttribute(value = "ou", index = 0)
    private String company;

    @Attribute(name = "cn")
    @DnAttribute(value = "cn", index = 1)
    private String cn;

    @Attribute(name = "pwdExpireWarning")
    private String pwdExpireWarning;

    @Attribute(name = "pwdLockoutDuration")
    private String pwdLockoutDuration;

    @Attribute(name = "pwdMaxAge")
    private String pwdMaxAge;

    @Attribute(name = "pwdminlength")
    private String pwdminlength;

    @Attribute(name = "pwdinhistory")
    private String pwdinhistory;

    @Attribute(name = "pwdlockout")
    private String pwdlockout;

    @Attribute(name = "pwdmaxfailure")
    private String pwdmaxfailure;

    @Attribute(name = "orclpwdalphanumeric")
    private String orclpwdalphanumeric;

    @Attribute(name = "orclpwdencryptionenable")
    private String orclpwdencryptionenable;

    @Attribute(name = "orclpwdmaxrptchars")
    private String orclpwdmaxrptchars;

    @Attribute(name = "orclpwdminalphachars")
    private String orclpwdminalphachars;

    @Attribute(name = "orclpwdminspecialchars")
    private String orclpwdminspecialchars;

    @Attribute(name = "orclpwdminuppercase")
    private String orclpwdminuppercase;

    @Attribute(name = "pwdmustchange")
    private String pwdmustchange;

    @Attribute(name = "pwdsafemodify")
    private String pwdsafemodify;

    @Attribute(name = "orclpwdminlowercase")
    private String orclpwdminlowercase;
}
