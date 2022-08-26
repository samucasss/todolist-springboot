package com.samuca.todolist;

import com.samuca.todolist.dao.UsuarioDao;
import com.samuca.todolist.model.Usuario;
import com.samuca.todolist.security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class TestUtil {

    @Autowired
    private UsuarioDao usuarioDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    public String login(String email) {
        Usuario usuario = usuarioDao.findByEmail(email);

        Authentication auth = new UsernamePasswordAuthenticationToken(usuario, null, null);
        String token =  tokenService.generateToken(auth);

        return token;
    }

    public Usuario saveUsuario() {
        Usuario usuario = new Usuario();
        usuario.setNome("Samuel");
        usuario.setEmail("samuca@gmail.com");
        usuario.setSenha(passwordEncoder.encode("samuca"));

        return usuarioDao.save(usuario);
    }

    public void removeAllUsuarios() {
        usuarioDao.deleteAll();
    }


}
