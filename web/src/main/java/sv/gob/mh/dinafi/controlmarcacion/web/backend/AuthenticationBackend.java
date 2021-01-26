package sv.gob.mh.dinafi.controlmarcacion.web.backend;

import java.io.IOException;
import java.io.Serializable;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.logging.Level;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.MessagingException;
import javax.servlet.ServletException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import org.omnifaces.util.Faces;
import org.omnifaces.util.Messages;
import sv.gob.mh.dinafi.controlmarcacion.model.CtrlAdministrador;
import sv.gob.mh.dinafi.controlmarcacion.model.CtrlMarcacion;
import sv.gob.mh.dinafi.controlmarcacion.model.CtrlUsuario;

import static sv.gob.mh.dinafi.controlmarcacion.web.backend.CrudBackend.TAG_ERROR;
import sv.gob.mh.dinafi.controlmarcacion.web.ldap.MailBox;
import sv.gob.mh.dinafi.controlmarcacion.web.ldap.MailUtil;
import sv.gob.mh.dinafi.controlmarcacion.web.ldap.UserRepository;
import sv.gob.mh.dinafi.controlmarcacion.web.ldap.domain.SegUser;
import sv.gob.mh.dinafi.controlmarcacion.web.service.AdministradorService;
import sv.gob.mh.dinafi.controlmarcacion.web.service.UsuarioService;
import sv.gob.mh.dinafi.controlmarcacion.web.service.MarcacionService;

@ViewScoped
@Named
@Getter
@Setter
@Log
public class AuthenticationBackend implements Serializable {

    private static final long serialVersionUID = 920179730866918763L;

    @Inject
    private UsuarioService usuarioService;
    @Inject
    private MarcacionService marcacionService;
    @Inject
    private AdministradorService administradorService;

    @Inject
    transient UserRepository userLdapRepository;

    @Inject
    private LdapBackend ldapBackend;
    @Inject
    private transient MailUtil mailUtil;
    private transient CtrlUsuario usuario;

    private MailBox mailBox;
    private String username;
    private String password;
    private String originalURL;
    private String email;

    public static final String EMAIL_SUCCESS = "login.xhtml";
    public static final String EMAIL_CHANGE_SECRET = "login.xhtml?uid=";

    public void login() throws IOException {
        try {
            Faces.getRequest().login(username, password);
            Principal principal = Faces.getRequest().getUserPrincipal();

            if (principal != null && principal.getName() != null) {
                CtrlUsuario user = aprovisionarUsuario(principal.getName());
                if (isAdministrador(user)) {
                    Faces.getExternalContext().redirect(
                            Faces.getExternalContext().getRequestContextPath()
                            + "/app/index.xhtml");
                } else {
                    Faces.getRequest().logout();
                    String snow = marcacion(user);
                    Faces.setFlashAttribute("MARCACION", snow);
                    Faces.getExternalContext().redirect(
                            Faces.getExternalContext().getRequestContextPath()
                            + "/complete.xhtml");
                }
            } else {
                Faces.getRequest().logout();
                throw new ServletException("Credenciales invalidas.");
            }
        } catch (IOException | ServletException ex) {
            log.log(Level.SEVERE, null, ex);
            String message = ex.getMessage() != null && ex.getMessage().toUpperCase().contains("UT010031")
                    ? "Credenciales no v&#225;lidas o puede ser que el usuario est&#233; bloqueado"
                    : ex.getMessage();
            Messages.create(TAG_ERROR).detail(message, (Object[]) null).error().add();
            Faces.validationFailed();
        }
    }

    public void prepareForgotPassword() {
        username = "";
        email = "";
    }

    public void sendMailToCtrlUsuario() {
        this.mailBox = new MailBox();
        try {
            usuario = usuarioService.findOneByLogin(username);
            if (usuario == null) {
                showMessages("Los datos ingresados no coinciden.");
                return;
            }
            if (!usuario.getEmail().equals(email)) {
                showMessages("Los datos ingresados no coinciden.");
                return;
            }

            mailBox.setTo(usuario.getEmail());
            mailBox.setFrom("no-reply@mh.gob.sv");
            mailBox.setSubject("Sistema de Control de Marcaci\u00F3n de DINAFI: Recuperaci\u00F3n de Contrase\u00f1a");
            String referencia = getReferencia();
            mailBox.setMessage(getBody(referencia, usuario));

            saveToken(referencia);
            mailUtil.sendMailHTML(mailBox.getFrom(), mailBox.getTo(), mailBox.getSubject(),
                    mailBox.getMessage());
            Messages.create("").detail("Se le ha enviado un correo para la recuperaci&#243;n de su contrase&#241;a").add();
        } catch (MessagingException e) {
            showMessages("En este momento no es posible procesar su solicitud.");
            log.log(Level.SEVERE, "Error en Proceso de Recuperacion de Clave: Usuario [{0}]: {1}", new Object[]{getUsuario(), e.getMessage()});
        }
    }

    private void saveToken(String referencia) {
        try {
            usuario.setAuthToken(referencia);
            usuario.setAuthTokenUseDate(null);
            usuarioService.save(usuario);
        } catch (Exception ex) {
            log.log(Level.INFO, null, ex);
        }
    }

    private static String getReferencia() {
        String uuid = UUID.randomUUID().toString();
        String result = uuid.replaceAll("-", "");
        return result.substring(0, 32).toUpperCase();
    }

    private String getBody(String referencia, CtrlUsuario usuario) {
        String fullName = usuario.getNombre() + " " + usuario.getApellidos();
        String fecha = ldapBackend.getLocaleDate();
        String vinculo = Faces.getRequestBaseURL() + EMAIL_CHANGE_SECRET + referencia;

        String template = getEmailTemplate();
        template = template.replaceAll("@@usuario@@", fullName);
        template = template.replaceAll("@@fecha@@", fecha);
        template = template.replaceAll("@@vinculo@@", vinculo);

        return template;
    }

    private void showMessages(String msg) {
        Messages.create("-ALERTA-").detail("<br><font size='5'>" + msg + "</font>").error().flash().add();
        Faces.validationFailed();
    }

    private String getEmailTemplate() {
        StringBuilder template = new StringBuilder();
        template.append("<div style='font-family:Calibri;font-size:11.0pt;color:blue;'>Estimado Usuario(a):&nbsp;@@usuario@@<br><p align='justify'>El Sistema de Control de Marcacion de DINAFI, ha ");
        template.append("recibido su solicitud de Recuperaci&oacute;n de Contrase&ntilde;a, la cual estar&aacute; vigente durante las pr&oacute;ximas 24 horas a partir @@fecha@@, fecha en la que se ");
        template.append("proces&oacute; su solicitud.</p><br>Para completar la Recuperaci&oacute;n de Contrase&ntilde;a es necesario ir al Sistema por medio del v&iacute;nculo siguiente:<br>");
        template.append("<a href='@@vinculo@@'>Ir a Recuperaci&oacute;n de Contrase&ntilde;a</a><br><br>Si usted no puede acceder directamente, por favor copie el siguiente v&iacute;nculo en su navegador:");
        template.append("<br><code>@@vinculo@@</code><br><br>Si usted no es quien realiz&oacute; la solicitud, puede reportarlo a la Administraci&oacute;n del Sistema.</div>");
        return template.toString();
    }

    public void sendMailToUser() {
        this.mailBox = new MailBox();
        try {
            usuario = usuarioService.findByLogin(username);
            if (usuario == null) {
                showMessages("Los datos ingresados no coinciden.");
                return;
            }
            if (!usuario.getEmail().equals(email)) {
                showMessages("Los datos ingresados no coinciden.");
                return;
            }

            mailBox.setTo(usuario.getEmail());
            mailBox.setFrom("no-reply@mh.gob.sv");
            mailBox.setSubject("Sistema de Control de Marcaci\u00F3n del Apoyo Administrativo: Recuperaci\u00F3n de Contrase\u00f1a");
            String referencia = getReferencia();
            mailBox.setMessage(getBody(referencia, usuario));

            saveToken(referencia);
            mailUtil.sendMailHTML(mailBox.getFrom(), mailBox.getTo(), mailBox.getSubject(),
                    mailBox.getMessage());
            Messages.create("").detail("Se le ha enviado un correo para la recuperaci&#243;n de su contrase&#241;a").add();
        } catch (MessagingException e) {
            showMessages("En este momento no es posible procesar su solicitud.");
            log.log(Level.SEVERE, "Error en Proceso de Recuperacion de Clave: Usuario [{0}]: {1}", new Object[]{getUsuario(), e.getMessage()});
        }
    }

    private CtrlUsuario aprovisionarUsuario(String login) {
        String name = login.toLowerCase();
        CtrlUsuario user = usuarioService.findByLogin(name);
        if (user == null) {
            CtrlUsuario newUser = new CtrlUsuario();
            newUser.setLogin(name);
            SegUser ldap = userLdapRepository.findByUserName(name);
            if (ldap != null) {
                newUser.setNit(ldap.getNit());
                newUser.setNombre(ldap.getGivenname());
                newUser.setApellidos(ldap.getMiddlename());
                newUser.setEmail(ldap.getMail());
            }
            user = usuarioService.save(newUser);
        }

        return user;
    }

    private String marcacion(CtrlUsuario user) {
        LocalDateTime now = LocalDateTime.now();
        CtrlMarcacion marcacion = new CtrlMarcacion();
        marcacion.setMarcacion(now);
        marcacion.setUsuarioId(user.getId());
        marcacionService.save(marcacion);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return now.format(formatter);
    }

    private boolean isAdministrador(CtrlUsuario user) {
        CtrlAdministrador admin = administradorService.findByUserId(user.getId());
        return admin != null;
    }

}
