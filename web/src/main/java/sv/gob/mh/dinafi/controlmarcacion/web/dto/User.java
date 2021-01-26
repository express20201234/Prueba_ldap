package sv.gob.mh.dinafi.controlmarcacion.web.dto;

import java.io.Serializable;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User implements Serializable {

    private static final long serialVersionUID = -2021011285;
    private String login;
    private Set<Role> roles;

    public User() {
    }

    public User(
            String login,
            Set<Role> roles) {
        this.login = login;
        this.roles = roles;
    }
}
