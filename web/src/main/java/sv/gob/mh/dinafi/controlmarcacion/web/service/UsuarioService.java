package sv.gob.mh.dinafi.controlmarcacion.web.service;

import java.math.BigInteger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import sv.gob.mh.common.querydsl.AbstractRepository;
import sv.gob.mh.common.querydsl.AbstractService;
import sv.gob.mh.dinafi.controlmarcacion.model.CtrlUsuario;
import sv.gob.mh.dinafi.controlmarcacion.web.repository.UsuarioRepository;

@Stateless
public class UsuarioService extends AbstractService<CtrlUsuario, BigInteger> {

    private static final long serialVersionUID = 202101161054001L;

    @Inject
    private UsuarioRepository usuarioRepository;

    @Override
    public AbstractRepository<CtrlUsuario, BigInteger> getRepository() {
        return usuarioRepository;
    }

    public CtrlUsuario findByLogin(String name) {
        return usuarioRepository.findByLogin(name);
    }

    public CtrlUsuario findOneByLogin(String login) {
        return usuarioRepository.findByLogin(login);
    }

    public CtrlUsuario findByToken(String authToken) {
        return usuarioRepository.findByToken(authToken);
    }
}
