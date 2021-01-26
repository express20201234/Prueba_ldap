package sv.gob.mh.dinafi.controlmarcacion.web.service;

import java.math.BigInteger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import sv.gob.mh.common.querydsl.AbstractRepository;
import sv.gob.mh.common.querydsl.AbstractService;
import sv.gob.mh.dinafi.controlmarcacion.model.CtrlAdministrador;
import sv.gob.mh.dinafi.controlmarcacion.web.repository.AdministradorRepository;

@Stateless
public class AdministradorService extends AbstractService<CtrlAdministrador, BigInteger> {

    private static final long serialVersionUID = 202101161056003L;
    
    @Inject
    private AdministradorRepository administradorRepository;

    @Override
    public AbstractRepository<CtrlAdministrador, BigInteger> getRepository() {
        return administradorRepository;
    }

    public CtrlAdministrador findByUserId(BigInteger id) {
        return administradorRepository.findByUserId(id);
    }

    public CtrlAdministrador findByLogin(String login) {
        return administradorRepository.findByUserLogin(login);
    }

}
