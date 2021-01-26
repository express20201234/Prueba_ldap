package sv.gob.mh.dinafi.controlmarcacion.web.service;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import sv.gob.mh.common.querydsl.AbstractRepository;
import sv.gob.mh.common.querydsl.AbstractService;
import sv.gob.mh.dinafi.controlmarcacion.model.CtrlViewMarcacion;
import sv.gob.mh.dinafi.controlmarcacion.web.repository.ViewMarcacionRepository;

/**
 *
 * @author erick.colocho
 */
@Stateless
public class ViewMarcacionService extends AbstractService<CtrlViewMarcacion, BigInteger> {
    
    private static final long serialVersionUID = 202101161057004L;
    
    @Inject
    private ViewMarcacionRepository viewMarcacionRepository;
    
    @Override
    public AbstractRepository<CtrlViewMarcacion, BigInteger> getRepository() {
        return viewMarcacionRepository;
    }
    
    public List<CtrlViewMarcacion> findMarcacionesByDates(LocalDateTime desde, LocalDateTime hasta) {
        return viewMarcacionRepository.findMarcacionesByDates(desde, hasta);
    }
    
}
