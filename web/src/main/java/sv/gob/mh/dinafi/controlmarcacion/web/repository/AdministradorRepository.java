package sv.gob.mh.dinafi.controlmarcacion.web.repository;

import java.math.BigInteger;
import sv.gob.mh.common.querydsl.AbstractRepository;
import sv.gob.mh.dinafi.controlmarcacion.model.CtrlAdministrador;
import sv.gob.mh.dinafi.controlmarcacion.model.QCtrlAdministrador;
import sv.gob.mh.dinafi.controlmarcacion.model.QCtrlUsuario;

public class AdministradorRepository extends AbstractRepository<CtrlAdministrador, BigInteger> {

    @Override
    public String getIdAttribute() {
        return "id";
    }

    public CtrlAdministrador findByUserId(BigInteger userId) {
        return queryFactory.select(QCtrlAdministrador.ctrlAdministrador)
                .from(QCtrlAdministrador.ctrlAdministrador)
                .where(QCtrlAdministrador.ctrlAdministrador.usuarioId.eq(userId))
                .fetchFirst();
    }

    public CtrlAdministrador findByUserLogin(String login) {
        return queryFactory.select(QCtrlAdministrador.ctrlAdministrador)
                .from(QCtrlAdministrador.ctrlAdministrador)
                .where(QCtrlUsuario.ctrlUsuario.login.eq(login))
                .leftJoin(QCtrlAdministrador.ctrlAdministrador.ctrlAdministradorRef01, QCtrlUsuario.ctrlUsuario)
                .fetchFirst();
    }
}
