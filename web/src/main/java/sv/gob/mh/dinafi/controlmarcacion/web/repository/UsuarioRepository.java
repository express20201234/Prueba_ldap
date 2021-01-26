package sv.gob.mh.dinafi.controlmarcacion.web.repository;

import java.math.BigInteger;
import sv.gob.mh.common.querydsl.AbstractRepository;
import sv.gob.mh.dinafi.controlmarcacion.model.CtrlUsuario;
import sv.gob.mh.dinafi.controlmarcacion.model.QCtrlUsuario;

public class UsuarioRepository extends AbstractRepository<CtrlUsuario, BigInteger> {

    @Override
    public String getIdAttribute() {
        return "id";
    }

    public CtrlUsuario findByLogin(String login) {
        return queryFactory.select(QCtrlUsuario.ctrlUsuario)
                .from(QCtrlUsuario.ctrlUsuario)
                .where(QCtrlUsuario.ctrlUsuario.login.eq(login))
                .fetchFirst();

    }

    public CtrlUsuario findByToken(String authToken) {
        return queryFactory.select(QCtrlUsuario.ctrlUsuario)
                .from(QCtrlUsuario.ctrlUsuario)
                .where(QCtrlUsuario.ctrlUsuario.authToken.eq(authToken.trim())
                        .and(QCtrlUsuario.ctrlUsuario.authTokenUseDate.isNull()))
                .fetchFirst();
    }
}
