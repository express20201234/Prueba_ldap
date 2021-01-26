package sv.gob.mh.dinafi.controlmarcacion.web.ldap;

import javax.inject.Inject;
import javax.naming.ldap.LdapName;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.support.LdapNameBuilder;
import sv.gob.mh.dinafi.controlmarcacion.web.ldap.domain.SegPolicy;

public class PolicyRepository {

    @Inject
    private LdapTemplate ldapTemplate;

    public SegPolicy findPolicy(String policies) {
        LdapName dn = buildDn(policies);
        SegPolicy segPolicy = ldapTemplate.findByDn(dn, SegPolicy.class);
        return segPolicy;
    }

    private LdapName buildDn(String policies) {
        return LdapNameBuilder.newInstance().add(policies).build();
    }

    public void setLdapTemplate(LdapTemplate ldapTemplate) {
        this.ldapTemplate = ldapTemplate;
    }
}
