package sv.gob.mh.dinafi.controlmarcacion.web.backend;

import java.util.List;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.math.BigInteger;
import lombok.Getter;
import lombok.Setter;
import org.omnifaces.util.Faces;
import sv.gob.mh.common.querydsl.AbstractService;
import sv.gob.mh.dinafi.controlmarcacion.model.CtrlMarcacion;
import sv.gob.mh.dinafi.controlmarcacion.model.QCtrlMarcacion;
import sv.gob.mh.dinafi.controlmarcacion.web.dto.Marcacion;
import sv.gob.mh.dinafi.controlmarcacion.web.service.MarcacionService;

@ViewScoped
@Named
@Getter
@Setter
public class MarcacionBackend extends CrudBackend<CtrlMarcacion, BigInteger> {

    private static final long serialVersionUID = -7568134834849640783L;

    @Inject
    private transient MarcacionService marcacionService;
    @Inject
    private SessionBackend sessionBackend;

    private List<Marcacion> administradorList;
    private List<CtrlMarcacion> usuariosActivos;

    @Override
    public AbstractService<CtrlMarcacion, BigInteger> getService() {
        return marcacionService;
    }

    @Override
    public void setDtoList(List list) {
        administradorList = list;
    }


}
