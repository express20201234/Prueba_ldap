package sv.gob.mh.dinafi.controlmarcacion.web.backend;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import org.omnifaces.util.Faces;
import sv.gob.mh.dinafi.controlmarcacion.model.CtrlUsuario;
import sv.gob.mh.dinafi.controlmarcacion.web.ldap.LdapEnum;
import sv.gob.mh.dinafi.controlmarcacion.web.ldap.MailBox;
import sv.gob.mh.dinafi.controlmarcacion.web.ldap.MailUtil;
import sv.gob.mh.dinafi.controlmarcacion.web.ldap.PolicyRepository;
import sv.gob.mh.dinafi.controlmarcacion.web.ldap.UserRepository;
import sv.gob.mh.dinafi.controlmarcacion.web.ldap.domain.SegPolicy;
import sv.gob.mh.dinafi.controlmarcacion.web.service.UsuarioService;

@ViewScoped
@Named
@Getter
@Setter
public class LdapBackend implements Serializable {

    private static final long serialVersionUID = -8304870013751057306L;

    @Inject
    transient UserRepository userRepository;
    @Inject
    transient UsuarioService usuarioService;
    @Inject
    transient PolicyRepository policyRepository;
    @Inject
    SessionBackend sessionBackend;
    @Inject
    private transient MailUtil mailUtil;

    private MailBox mailBox;
    private String newPassword;
    private String repeatNewPassword;
    private String oldPassword;
    private String authToken;

    @PostConstruct
    public void init() {
        HttpServletRequest request = Faces.getRequest();
        if (request.getAttribute("javax.servlet.forward.query_string") != null) {
            authToken = request.getParameter("uid");
            if (authToken != null) {
                CtrlUsuario user = usuarioService.findByToken(authToken);
                if (user == null) {
                    authToken = null;
                }
            }
        }
    }

    public void changePasswordByRecovery() {
        if (authToken != null && !"".equals(authToken.trim())) {
            mailBox = new MailBox();
            CtrlUsuario user = usuarioService.findByToken(authToken);
            if (user != null) {
                boolean isValid = validarPoliticaSeguridad(user.getLogin(), false);
                if (isValid) {
                    if (newPassword.equals(repeatNewPassword)) {
                        String result = userRepository.newChangePassword(user.getLogin(), newPassword);
                        if (result == null || "".equals(result.trim())) {
                            mailBox.setTo(user.getEmail());
                            mailBox.setFrom("no-reply@mh.gob.sv");
                            mailBox.setSubject("Sistema de Control de Marcaci&#243;n de DINAFI - Se ha recuperado su contrase\u00f1a");
                            mailBox.setMessage(getBody(user));
                            updateToken(user);
                            try {
                                mailUtil.sendMailHTML(mailBox.getFrom(), mailBox.getTo(), mailBox.getSubject(),
                                        mailBox.getMessage());

                                Faces.logout();
                                Faces.redirect("?faces-redirect=true");
                            } catch (MessagingException | ServletException ex) {
                                String error = ex.getLocalizedMessage();
                                String m1 = "GSL_PWDINHISTORY_EXCP";
                                String m2 = "Your New Password cannot be the same as your Old";
                                if (error.contains(m1) || error.contains(m2)) {
                                    addMessageWarn("La Clave Nueva no puede ser igual a las anteriores, usted no puede utilizar ninguna de las &#250;ltimas 10.");
                                } else {
                                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Fatal!", "No es posible cambiar la clave en este momento. <br/>" + error));
                                    Logger.getLogger(LdapBackend.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        } else {
                            addMessageWarn(result);
                        }
                    } else {
                        addMessageWarn("La nueva contrase&#241;a no coincide con la repetici&#243;n");
                    }
                } else {
                    addMessageWarn("\"No es posible cambiar la clave en este momento.");
                }
            } else {
                addMessageWarn("\"No es posible cambiar la clave en este momento. Usuario no es v&#225;lido.");
            }
        }
    }

    public void changePassword() {
        setMailBox(new MailBox());
        CtrlUsuario usuario = usuarioService.findOneByLogin(sessionBackend.getUser().getLogin());
        try {
            if (usuario == null) {
                return;
            }
            boolean success = validarPoliticaSeguridad(sessionBackend.getUser().getLogin());
            if (!success) {
                return;
            }
            if (!newPassword.equals(repeatNewPassword)) {
                addMessageWarn("La nueva contrase&#241;a no coincide con la repetici&#243;n");
                return;
            }
            String result = userRepository.newChangePassword(sessionBackend.getUser().getLogin(), newPassword);
            if (result == null || "".equals(result.trim())) {
                mailBox.setTo(usuario.getEmail());
                mailBox.setFrom("no-reply@mh.gob.sv");
                mailBox.setSubject("Sistema de Control de Marcaci&#243;n de DINAFI - Se ha cambiado su contrase\u00f1a");
                mailBox.setMessage(getBody(usuario));

                mailUtil.sendMailHTML(mailBox.getFrom(), mailBox.getTo(), mailBox.getSubject(),
                        mailBox.getMessage());

                Faces.logout();
                Faces.redirect("?faces-redirect=true");
            } else {
                addMessageWarn(result);
            }
        } catch (MessagingException | ServletException e) {
            String error = e.getLocalizedMessage();
            String m1 = "GSL_PWDINHISTORY_EXCP";
            String m2 = "Your New Password cannot be the same as your Old";
            if (error.contains(m1) || error.contains(m2)) {
                addMessageWarn("La Clave Nueva no puede ser igual a las anteriores, usted no puede utilizar ninguna de las &#250;ltimas 10.");
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Fatal!", "No es posible cambiar la clave en este momento. <br/>" + error));
                Logger.getLogger(LdapBackend.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }

    private void addMessageWarn(String msg) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Alerta!", msg));
    }

    public String getLocaleDate() {
        Date date = Calendar.getInstance().getTime();
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
        return formatter.format(date);
    }

    private String getEmailTemplate() {
        StringBuilder template = new StringBuilder();
        template.append("<div style='font-family:Calibri;font-size:11.0pt;color:blue;'>Estimado Usuario(a):&nbsp;@@usuario@@<br><p align='justify'>El Sistema de Control de Marcacion de DINAFI, le ");
        template.append("comunica que se realiz&oacute; satisfactoriamente el Cambio de Contrase&ntilde;a, en la fecha @@fecha@@.</p>");
        template.append("<br>Para ir al Sistema utilice el v&iacute;nculo siguiente:<br>");
        template.append("<a href='@@vinculo@@'>Sistema de Control de Marcaci&#243;n de DINAFI</a><br><br>Si usted no puede acceder directamente, por favor copie el siguiente v&iacute;nculo en su navegador:");
        template.append("<br><code>@@vinculo@@</code><br><br>Si usted no es quien realiz&oacute; el cambio, puede reportarlo a la Administraci&oacute;n del Sistema.</div>");
        return template.toString();
    }

    private boolean validarPoliticaSeguridad(String usuario) {
        return validarPoliticaSeguridad(usuario, true);
    }

    private boolean validarPoliticaSeguridad(String usuario, boolean validateOldPassword) {
        SegPolicy segPolicy = policyRepository.findPolicy(LdapEnum.LDAP_POLICY);
        if (validateOldPassword) {
            boolean auth = userRepository.authenticate(usuario, oldPassword);
            if (!auth) {
                addMessageWarn("La Clave Anterior ingresada no es la correcta.");
                return false;
            }
        }

        if (null == segPolicy) {
            addMessageWarn("La pol&#237;tica no esta disponible.");
            return false;
        }

        if (null == newPassword || newPassword.isEmpty()) {
            addMessageWarn("La Clave Nueva esta vac&#237;a.");
            return false;
        }

        if (validateOldPassword) {
            if (newPassword.equals(getOldPassword())) {
                addMessageWarn("La Clave Nueva no puede ser igual a la anterior.");
                return false;
            }
        }

        int pwdMinlength = Integer.parseInt(segPolicy.getPwdminlength());
        if (pwdMinlength > 0 && newPassword.length() < pwdMinlength) {
            addMessageWarn("La Clave Nueva requiere una longitud de " + pwdMinlength + " caracteres.");
        }

        int pwdAlphanumeric = Integer.parseInt(segPolicy.getOrclpwdalphanumeric());
        if (pwdAlphanumeric > 0 && countCharacterIsNumber(newPassword) < pwdAlphanumeric) {
            addMessageWarn("La Clave Nueva requiere al menos " + pwdAlphanumeric + " caracter(es) num&#233;rico(s).");
        }

        int pwdMinspecialchars = Integer.parseInt(segPolicy.getOrclpwdminspecialchars());
        if (pwdMinspecialchars > 0 && countAlphanumeric(newPassword) < pwdMinspecialchars) {
            addMessageWarn("La Clave Nueva requiere al menos " + pwdMinspecialchars + " caracter(es) especial(es).");
        }

        int pwdMinUpperCase = Integer.parseInt(segPolicy.getOrclpwdminuppercase());
        if (pwdMinUpperCase > 0 && countCharacterIsUpperCase(newPassword) < pwdMinUpperCase) {
            addMessageWarn("La Clave Nueva requiere al menos " + pwdMinUpperCase + " caracter(es) en may&#250;scula(s).");
        }

        int pwdMinLowerCase = Integer.parseInt(segPolicy.getOrclpwdminlowercase());
        if (pwdMinLowerCase > 0 && countCharacterIsLowerCase(newPassword) < pwdMinLowerCase) {
            addMessageWarn("La Clave Nueva requiere al menos " + pwdMinLowerCase + " caracter(es) en min&#250;scula(s).");
        }

        FacesContext fc = Faces.getContext();
        return fc.getMessageList().isEmpty();

    }

    private int countAlphanumeric(String str) {
        int count = 0;
        char[] charArray = str.toCharArray();
        for (char c : charArray) {
            if (!Character.isLetterOrDigit(c)) {
                count++;
            }
        }
        return count;
    }

    private int countCharacterIsUpperCase(String str) {
        int count = 0;
        char[] charArray = str.toCharArray();
        for (char c : charArray) {
            if (Character.isUpperCase(c)) {
                count++;
            }
        }
        return count;
    }

    private int countCharacterIsLowerCase(String str) {
        int count = 0;
        char[] charArray = str.toCharArray();
        for (char c : charArray) {
            if (Character.isLowerCase(c)) {
                count++;
            }
        }
        return count;
    }

    private int countCharacterIsNumber(String str) {
        int count = 0;
        char[] charArray = str.toCharArray();
        for (char c : charArray) {
            if (Character.isDigit(c)) {
                count++;
            }
        }
        return count;
    }

    private String getBody(CtrlUsuario usuario) {
        String fullName = usuario.getNombre() + " " + usuario.getApellidos();
        String fecha = getLocaleDate();
        String vinculo = Faces.getRequestBaseURL() + "index.xhtml";

        String template = getEmailTemplate();
        template = template.replaceAll("@@usuario@@", fullName);
        template = template.replaceAll("@@fecha@@", fecha);
        template = template.replaceAll("@@vinculo@@", vinculo);

        return template;
    }

    private void updateToken(CtrlUsuario user) {
        try {
            user.setAuthToken(authToken);
            user.setAuthTokenUseDate(LocalDateTime.now());
            usuarioService.save(user);
        } catch (Exception ex) {
            // do nothing 
        }
    }

}
