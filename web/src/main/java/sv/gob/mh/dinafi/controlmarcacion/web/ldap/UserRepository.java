/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sv.gob.mh.dinafi.controlmarcacion.web.ldap;

import java.util.List;
import javax.inject.Inject;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.ldap.LdapName;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.AbstractContextMapper;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.query.LdapQuery;
import static org.springframework.ldap.query.LdapQueryBuilder.query;
import org.springframework.ldap.support.LdapNameBuilder;
import sv.gob.mh.dinafi.controlmarcacion.web.ldap.domain.SegUser;

public class UserRepository {

    private static final String ATTR_USER_INDENTIFIER = "uid";

    @Inject
    private LdapTemplate ldapTemplate;

    protected static final String[] attributes = {ATTR_USER_INDENTIFIER, "userPassword", "creatorsName", "modifyTimestamp",
        "pwdAccountLockedTime", "pwdChangedTime", "pwdFailureTime", "pwdReset", "mail", "pwdHistory", "nit",
        "givenname", "sn"};

    public static final SearchControls searchControls = loadSearchControls();

    public void setLdapTemplate(LdapTemplate ldapTemplate) {
        this.ldapTemplate = ldapTemplate;
    }

    public SegUser findByUserName(String username) {
        LdapName dn = buildUserDn();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        AndFilter filter = new AndFilter();
        filter.and(new EqualsFilter(ATTR_USER_INDENTIFIER, username));
        List<SegUser> users = ldapTemplate.search(dn, filter.encode(), searchControls, new PersonContextMapper());
        return !users.isEmpty() ? users.get(0) : null;
    }

    private static class PersonContextMapper extends AbstractContextMapper<SegUser> {

        @Override
        public SegUser doMapFromContext(DirContextOperations context) {
            SegUser segUser = new SegUser();
            Attributes attributes = null;
            Attribute userPassword = null;
            try {
                attributes = context.getAttributes();
                userPassword = attributes.get("userPassword");
                if (userPassword == null) {
                    segUser.setUserPassword(null);
                } else {
                    segUser.setUserPassword(new String((byte[]) userPassword.get()));
                }

            } catch (NamingException e) {
                //Nothing
            }

            segUser.setFullName(context.getStringAttribute(ATTR_USER_INDENTIFIER));
            segUser.setMail(context.getStringAttribute("mail"));
            segUser.setPwdAccountLockedTime(context.getStringAttribute("pwdAccountLockedTime"));
            segUser.setPwdChangedTime(context.getStringAttribute("pwdChangedTime"));
            segUser.setPwdReset(context.getStringAttribute("pwdReset"));

            segUser.setPwdHistory(context.getStringAttribute("pwdHistory"));
            segUser.setNit(context.getStringAttribute("nit"));
            segUser.setGivenname(context.getStringAttribute("givenname"));
            segUser.setMiddlename(context.getStringAttribute("sn"));

            segUser.setDn(context.getDn());

            return segUser;
        }
    }

    private String getLocaleUsers() {
        String all = LdapEnum.LDAP_USER_BASE_SEARCH;
        return all.replaceAll("," + LdapEnum.LDAP_BASE, "");
    }

    private LdapName buildUserDn() {
        return LdapNameBuilder.newInstance().add(getLocaleUsers()).build();
    }

    private static final SearchControls loadSearchControls() {
        SearchControls controls = new SearchControls();
        controls.setReturningObjFlag(true);
        controls.setReturningAttributes(attributes);
        controls.setCountLimit(100);
        controls.setTimeLimit(180000);
        return controls;
    }

    public List<SegUser> findByAttribute(String attr, String value) {
        LdapQuery query = query().base(getLocaleUsers()).where("objectclass").is("person")
                .and(attr).like("*" + value + "*");
        return ldapTemplate.search(query, new PersonContextMapper());
    }

    private Name getNameForUser(String username) {
        SegUser user = findByUserName(username);
        return user != null ? user.getDn() : null;
    }

    public String newChangePassword(String uid, String plaintextpassword) {
        String ret = "No fue posible cambiar la contrase&#241;a.";
        try {
            Name dn = getNameForUser(uid);
            if (dn != null) {
                Attribute attr = new BasicAttribute("userpassword", plaintextpassword);
                ModificationItem item = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, attr);
                ldapTemplate.modifyAttributes(dn, new ModificationItem[]{item});
                ret = null;
            }
        } catch (Exception ex) {
            String message = ex.getMessage();
            if (message.contains("GSL_PWDINHISTORY_EXCP")) {
                message = "La Clave Nueva no puede ser igual a las anteriores, usted no puede utilizar ninguna de las &#250;ltimas 10.";
            }
            ret += "<br />" + message;
        }
        return ret;
    }

    public boolean authenticate(String username, String plaintextpassword) {
        Name dn = getNameForUser(username);
        if (dn == null) {
            return false;
        }
        return ldapTemplate.authenticate(dn, "(" + ATTR_USER_INDENTIFIER + "=" + username + ")", plaintextpassword);
    }
}
