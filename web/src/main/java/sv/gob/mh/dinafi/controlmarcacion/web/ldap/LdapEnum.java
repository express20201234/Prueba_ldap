package sv.gob.mh.dinafi.controlmarcacion.web.ldap;

public class LdapEnum {

    private LdapEnum() {
    }

    public static final String LDAP_URL = System.getProperty("safi.ldap.url");

    public static final String LDAP_BASE = System.getProperty("safi.ldap.base");

    public static final String LDAP_USER = System.getProperty("safi.ldap.manager.dn");

    public static final String LDAP_PASSWORD = System.getProperty("safi.ldap.pass");

    public static final String LDAP_POLICY = System.getProperty("safi.ldap.policy");

    public static final String LDAP_USER_BASE_SEARCH = System.getProperty("safi.ldap.context.search");
}
