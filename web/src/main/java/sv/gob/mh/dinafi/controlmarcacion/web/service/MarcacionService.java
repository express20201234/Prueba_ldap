package sv.gob.mh.dinafi.controlmarcacion.web.service;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import sv.gob.mh.common.querydsl.AbstractRepository;
import sv.gob.mh.common.querydsl.AbstractService;
import sv.gob.mh.dinafi.controlmarcacion.model.CtrlMarcacion;
import sv.gob.mh.dinafi.controlmarcacion.web.repository.MarcacionRepository;

@Stateless
public class MarcacionService extends AbstractService<CtrlMarcacion, BigInteger> {

    private static final long serialVersionUID = 202101161055002L;
    
    @Inject
    private MarcacionRepository marcacionRepository;

    @Override
    public AbstractRepository<CtrlMarcacion, BigInteger> getRepository() {
        return marcacionRepository;
    }

    public List<CtrlMarcacion> getMarcaciones(LocalDateTime desde, LocalDateTime hasta) {
        return marcacionRepository.getMarcaciones(desde, hasta);
    }
}
