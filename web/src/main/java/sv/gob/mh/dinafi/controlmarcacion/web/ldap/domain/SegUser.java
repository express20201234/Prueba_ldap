package sv.gob.mh.dinafi.controlmarcacion.web.ldap.domain;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import javax.naming.Name;
import lombok.Getter;
import lombok.Setter;
import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.DnAttribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;
import org.springframework.ldap.odm.annotations.Transient;

@Entry(objectClasses = {"inetOrgPerson", "organizationalPerson", "person", "shadowAccount", "top"})
@Getter
@Setter
public class SegUser implements Serializable {

    private static final long serialVersionUID = -978136186526802075L;

    @Id
    private Name dn;

    @Transient
    @DnAttribute(value = "ou", index = 0)
    private String company;

    @Attribute(name = "uid")
    @DnAttribute(value = "uid", index = 1)
    private String fullName;

    @Attribute(name = "userPassword")
    private String userPassword;

    @Attribute(name = "creatorsName")
    private String creatorsName;

    @Attribute(name = "modifyTimestamp")
    private String modifyTimestamp;

    @Attribute(name = "mail")
    private String mail;

    @Attribute(name = "pwdAccountLockedTime")
    private String pwdAccountLockedTime;

    @Attribute(name = "pwdChangedTime")
    private String pwdChangedTime;

    @Attribute(name = "pwdFailureTime")
    private String pwdFailureTime;

    @Attribute(name = "pwdReset")
    private String pwdReset;

    @Attribute(name = "pwdHistory")
    private String pwdHistory;

    @Attribute(name = "nit")
    private String nit;

    @Attribute(name = "givenname")
    private String givenname;

    @Attribute(name = "middlename")
    private String middlename;

    public Date getModifyTimestamp() {
        return parseLdap(modifyTimestamp);

    }

    public void setModifyTimestamp(Date modifyTimestamp) {
        this.modifyTimestamp = formatLdap(modifyTimestamp);
    }

    public Date getPwdAccountLockedTime() {
        return parseLdap(pwdAccountLockedTime);
    }

    public void setPwdAccountLockedTime(Date pwdAccountLockedTime) {
        this.pwdAccountLockedTime = formatLdap(pwdAccountLockedTime);
    }

    public void setPwdAccountLockedTime(String pwdAccountLockedTime) {
        this.pwdAccountLockedTime = pwdAccountLockedTime;
    }

    public Date getPwdChangedTime() {
        return parseLdap(pwdChangedTime);
    }

    public void setPwdChangedTime(Date pwdChangedTime) {
        this.pwdChangedTime = formatLdap(pwdChangedTime);
    }

    public void setPwdChangedTime(String pwdChangedTime) {
        this.pwdChangedTime = pwdChangedTime;
    }

    public Date getPwdFailureTime() {
        return parseLdap(pwdFailureTime);
    }

    public void setPwdFailureTime(Date pwdFailureTime) {
        this.pwdFailureTime = formatLdap(pwdFailureTime);
    }

    /**
     * DATE UTIL
     */
    public static final String FORMAT_DATE_LDAP = "yyyyMMddHHmmss";

    public static final String FORMAT_DATE_SAFI = "dd/MM/yyyy hh:mm:ss a";

    public final static Calendar getCalendarNow() {
        Calendar now = Calendar.getInstance();
        return (Calendar) now.clone();
    }

    public static Date parseLdap(String time) {
        if (time == null) {
            return null;
        }
        try {
            return new SimpleDateFormat(FORMAT_DATE_LDAP).parse(time.replace("Z", ""));
        } catch (ParseException e) {
            return null;
        }
    }

    public static String formatLdap(Date time) {
        return new SimpleDateFormat(FORMAT_DATE_LDAP).format(time).concat("Z");
    }

    public static Date parseSafi(String time) {
        if (time == null) {
            return null;
        }
        try {
            return new SimpleDateFormat(FORMAT_DATE_SAFI).parse(time);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String formatSafi(Date time) {
        return new SimpleDateFormat(FORMAT_DATE_SAFI).format(time);
    }

    public static String getDayName(Date date, Locale loc) {
        DateFormat f = new SimpleDateFormat("EEEE", loc);
        try {
            return f.format(date);
        } catch (Exception e) {
            return "";
        }
    }

    public static String getMonthName(Date date, Locale loc) {
        DateFormat f = new SimpleDateFormat("MMMMM", loc);
        try {
            return f.format(date);
        } catch (Exception e) {
            return "";
        }
    }

    public static Integer getDayDiff(Date dateIni, Date dateFin) {
        long diff = dateFin.getTime() - dateIni.getTime();
        long diffDays = diff / (24 * 60 * 60 * 1000);
        return (int) diffDays;
    }

    public static Date conCeroSegundos(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static Date ultimaHoraDelDia(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }
}
