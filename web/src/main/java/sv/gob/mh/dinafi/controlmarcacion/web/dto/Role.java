package sv.gob.mh.dinafi.controlmarcacion.web.dto;

import java.io.Serializable;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Role implements Serializable {

    private static final long serialVersionUID = -202101120854001L;

    String name;

    public Role() {
    }

    public Role(String name) {
        this.name = name;
    }
}
