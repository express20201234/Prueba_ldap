package sv.gob.mh.dinafi.controlmarcacion.web.dto;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import sv.gob.mh.dinafi.controlmarcacion.model.CtrlMarcacion;
import sv.gob.mh.dinafi.controlmarcacion.model.CtrlUsuario;

@Getter
@Setter
public class Marcacion implements Serializable {

    private static final long serialVersionUID = -2021011209;

    private CtrlUsuario usuario;
    private CtrlMarcacion marcacion;

    public Marcacion(
            CtrlUsuario usuario,
            CtrlMarcacion marcacion
    ) {
        this.usuario = usuario;
        this.marcacion = marcacion;
    }

}
