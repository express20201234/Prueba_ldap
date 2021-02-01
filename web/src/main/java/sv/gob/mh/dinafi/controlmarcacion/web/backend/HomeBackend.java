package sv.gob.mh.dinafi.controlmarcacion.web.backend;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import org.omnifaces.util.Faces;

import sv.gob.mh.common.dto.Filter;
import sv.gob.mh.common.dto.Page;
import sv.gob.mh.common.dto.Sort;

import sv.gob.mh.dinafi.controlmarcacion.model.CtrlUsuario;
import sv.gob.mh.dinafi.controlmarcacion.model.CtrlMarcacion;
import sv.gob.mh.dinafi.controlmarcacion.web.common.AbstractLazyModel;
import sv.gob.mh.dinafi.controlmarcacion.web.service.UsuarioService;
import sv.gob.mh.dinafi.controlmarcacion.web.service.MarcacionService;

@ViewScoped
@Named
@Getter
@Setter
@Log
public class HomeBackend implements Serializable {

    private static final long serialVersionUID = 1161892537552163180L;
    @Inject
    SessionBackend sessionBackend;
    @Inject
    private UsuarioService usuarioService;
    @Inject
    private MarcacionService marcacionService;

    private String page;
    
    private String name;

    private AbstractLazyModel<CtrlMarcacion, BigInteger> lastConnections;

    @PostConstruct
    public void init() {
        //sessionBackend.setTitle("CONTROL DE MARCACION");
        page = "page1";
    }

    public void set(String page) {
        this.page = page;
    }

    public void initLastConnections() {
        Principal principal = Faces.getRequest().getUserPrincipal();
        CtrlUsuario user = usuarioService.findOneByLogin(principal.getName());
        if (user != null) {
            //lastConnections = marcacionService.findAllByUser(user.getIdUsuario());
            lastConnections = new AbstractLazyModel<CtrlMarcacion, BigInteger> (marcacionService);
            Set<Filter> customFilters = new HashSet<>();
            customFilters.add(new Filter("id", user.getId()));
            lastConnections.setCustomFilters(customFilters);
            Set<Sort> sortFirst = new HashSet<>();
            sortFirst.add(new Sort("marcacion", Sort.SortDirection.DESC, null));
            lastConnections.setSortFirst(sortFirst);

        }
    }

    /**
     *
     */
    public void doLogout() {
        //Principal principal = Faces.getRequest().getUserPrincipal();
        //CtrlUsuario user = usuarioService.findByLogin(principal.getName());
        //saveLogoutLog(user);
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        redirectIndex("/");
    }

    public void saveLogoutLog(CtrlUsuario user) {
        saveLog("OUT", findUser(user));
    }

    public CtrlUsuario findUser(CtrlUsuario user) {
        CtrlUsuario ret = null;
        if (user != null
                && user.getLogin() != null
                && !"".equals(user.getLogin().trim())) {
            try {
                ret = usuarioService.findOneByLogin(user.getLogin());
            } catch (Exception ex) {
                log.log(Level.INFO, null, ex);
            }
        }
        return ret;
    }

    private void saveLog(String type, CtrlUsuario dbUser) {
        if (dbUser != null
                && dbUser.getId() != null) {
            try {
                CtrlMarcacion sessionLog = new CtrlMarcacion();
                //sessionLog.setLogType(type);
                sessionLog.setMarcacion(LocalDateTime.now());
                sessionLog.setId(dbUser.getId());
                marcacionService.save(sessionLog);
            } catch (Exception ex) {
                log.log(Level.INFO, null, ex);
            }
        }
    }

    public void doHome() {
        redirectIndex("/index.xhtml");
    }

    public static void redirectIndex(String complementoUrl) {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            HttpServletRequest origRequest = (HttpServletRequest) context.getExternalContext().getRequest();
            String contextPath = origRequest.getContextPath();
            FacesContext.getCurrentInstance().getExternalContext()
                    .redirect(contextPath + complementoUrl);
        } catch (IOException ex) {
            Logger.getLogger(HomeBackend.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
