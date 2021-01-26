package sv.gob.mh.dinafi.controlmarcacion.web.repository;

import com.querydsl.sql.SQLQuery;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import sv.gob.mh.common.querydsl.AbstractRepository;
import sv.gob.mh.dinafi.controlmarcacion.model.CtrlMarcacion;
import sv.gob.mh.dinafi.controlmarcacion.model.QCtrlMarcacion;

public class MarcacionRepository extends AbstractRepository<CtrlMarcacion, BigInteger> {

    @Override
    public String getIdAttribute() {
        return "id";
    }

    public CtrlMarcacion findByToken(BigInteger userId) {
        return queryFactory.select(QCtrlMarcacion.ctrlMarcacion)
                .from(QCtrlMarcacion.ctrlMarcacion)
                .where(QCtrlMarcacion.ctrlMarcacion.usuarioId.eq(userId))
                .fetchFirst();
    }

    public List<CtrlMarcacion> getMarcaciones(LocalDateTime desde, LocalDateTime hasta) {
        SQLQuery q = queryFactory.select(QCtrlMarcacion.ctrlMarcacion)
                .from(QCtrlMarcacion.ctrlMarcacion)
                .where(QCtrlMarcacion.ctrlMarcacion.marcacion.between(desde, hasta));

        return q.fetch();
    }
}
