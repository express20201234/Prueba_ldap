package sv.gob.mh.dinafi.controlmarcacion.web.backend;

import java.io.Serializable;
import java.security.Principal;
import java.util.Calendar;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.omnifaces.util.Faces;
import org.omnifaces.util.Utils;
import sv.gob.mh.dinafi.controlmarcacion.model.CtrlUsuario;
import sv.gob.mh.dinafi.controlmarcacion.web.ldap.LdapEnum;
import sv.gob.mh.dinafi.controlmarcacion.web.ldap.PolicyRepository;
import sv.gob.mh.dinafi.controlmarcacion.web.ldap.UserRepository;
import sv.gob.mh.dinafi.controlmarcacion.web.ldap.domain.SegPolicy;
import sv.gob.mh.dinafi.controlmarcacion.web.ldap.domain.SegUser;
import sv.gob.mh.dinafi.controlmarcacion.web.service.UsuarioService;

@SessionScoped
@Named
@Getter
@Setter
public class SessionBackend implements Serializable {

    private static final long serialVersionUID = -9102878303316368574L;

    private static final String SEC_MAX_AGE = "7776000";
    private static final String SEC_EXPIRE_WARNING = "604800";

    @Inject
    private UsuarioService usuarioService;

    @Inject
    transient PolicyRepository policyRepository;

    @Inject
    transient UserRepository userRepository;

    private CtrlUsuario user;

    private boolean passwordIsAboutToExpire = true;
    private Date expireDate;

    @PostConstruct
    public void init() {
        Principal principal = Faces.getRequest().getUserPrincipal();
        user = usuarioService.findByLogin(principal.getName());

        passwordIsAboutToExpire = isExpireWarningPassword(
                policyRepository.findPolicy(LdapEnum.LDAP_POLICY),
                userRepository.findByUserName(principal.getName()));
    }

    private boolean isExpireWarningPassword(SegPolicy segPolicy, SegUser segUser) {
        Date pwdChangedTime = segUser.getPwdChangedTime();
        String max = segPolicy.getPwdMaxAge();
        String offset = segPolicy.getPwdExpireWarning();

        if (Utils.isEmpty(max)) {
            max = SEC_MAX_AGE;
        }

        if (Utils.isEmpty(offset)) {
            offset = SEC_EXPIRE_WARNING;
        }

        Calendar now = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        end.setTime(pwdChangedTime);
        end.add(Calendar.SECOND, Integer.valueOf(max));
        Calendar start = Calendar.getInstance();
        start.setTime(end.getTime());
        start.add(Calendar.SECOND, -Integer.valueOf(offset));
        expireDate = end.getTime();
        return now.after(start) && now.before(end);
    }

//    public boolean isAdmin() {
//        boolean ret = false;
//        if (user != null
//                && user.getRoles() != null) {
//            for (Role role : user.getRoles()) {
//                if (role.getName().trim().toLowerCase().startsWith("admin")) {
//                    ret = true;
//                    break;
//                }
//            }
//        }
//        return ret;
//    }
//
//    public boolean hasAllRoles(String... roles) {
//        boolean ret = false;
//        if (user != null
//                && user.getRoles() != null
//                && roles != null) {
//            int rolesLength = roles.length;
//            if (rolesLength > 0) {
//                int foundRoles = 0;
//                for (String r : roles) {
//                    for (Role role : user.getRoles()) {
//                        if (role.getName().trim().equalsIgnoreCase(r.trim())) {
//                            foundRoles++;
//                        }
//                    }
//                }
//                ret = rolesLength <= foundRoles;
//            }
//        }
//        return ret;
//    }
//
//    public boolean hasAnyRoles(String... roles) {
//        boolean ret = false;
//        if (user != null
//                && user.getRoles() != null
//                && roles != null) {
//            for (String r : roles) {
//                for (Role role : user.getRoles()) {
//                    if (role.getName().trim().equalsIgnoreCase(r.trim())) {
//                        ret = true;
//                        break;
//                    }
//                }
//            }
//        }
//        return ret;
//    }
}
