package sv.gob.mh.dinafi.controlmarcacion.web.repository;

import com.querydsl.core.types.OrderSpecifier;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import sv.gob.mh.common.querydsl.AbstractRepository;
import sv.gob.mh.dinafi.controlmarcacion.model.CtrlViewMarcacion;
import sv.gob.mh.dinafi.controlmarcacion.model.QCtrlViewMarcacion;

import com.querydsl.core.types.dsl.DateExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.sql.SQLQuery;

/**
 *
 * @author erick.colocho
 */
public class ViewMarcacionRepository extends AbstractRepository<CtrlViewMarcacion, BigInteger> {

    private static final long serialVersionUID = 202101161101005L;

    protected static final transient String JAVA_FORMAT_DATE = "yyyy-MM-dd";
    protected static final transient String ORACLE_FORMAT_DATE = "YYYY-MM-DD";

    protected static final QCtrlViewMarcacion Q = QCtrlViewMarcacion.ctrlViewMarcacion;

    @Override
    public String getIdAttribute() {
        return "id";
    }

    public List<CtrlViewMarcacion> findMarcacionesByDates(LocalDateTime desde, LocalDateTime hasta) {
        DateExpression<Date> dbColDate = Expressions.dateTemplate(Date.class, "TRUNC({0})", Q.marcacion);
        DateExpression<Date> start = Expressions.dateTemplate(Date.class, "TO_DATE({0}, {1})", format(desde), ORACLE_FORMAT_DATE);
        DateExpression<Date> end = Expressions.dateTemplate(Date.class, "TO_DATE({0}, {1})", format(hasta), ORACLE_FORMAT_DATE);

        OrderSpecifier byNombre = Q.nombre.asc();
        OrderSpecifier byNit = Q.nit.asc();
        OrderSpecifier byFecha = Q.sfecha.asc();
        OrderSpecifier byHora = Q.shora.asc();
        SQLQuery query = queryFactory.select(Q)
                .from(Q)
                .where(dbColDate.goe(start).and(dbColDate.loe(end))
                );
        query.orderBy(byNombre, byNit, byFecha, byHora);
        return query.fetch();
    }

    private String format(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(JAVA_FORMAT_DATE);
        return date.format(formatter);
    }
}
