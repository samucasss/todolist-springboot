package com.samuca.todolist.security;

import com.samuca.todolist.dao.UsuarioDao;
import com.samuca.todolist.model.Usuario;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class AuthRestController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioDao usuarioDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/auth/register")
    public Usuario register(@RequestBody @Valid Usuario usuario) {
        String senha = passwordEncoder.encode(usuario.getPassword());
        usuario.setSenha(senha);

        usuario = usuarioDao.save(usuario);

        Usuario retorno = new Usuario();
        BeanUtils.copyProperties(usuario, retorno);
        retorno.setSenha(null);

        return retorno;
    }

    @PostMapping("/auth/login")
    public String auth(@RequestBody Login login){
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(login.getUserName(), login.getPassword());

        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        String token = tokenService.generateToken(authentication);

        return token;
    }

    @GetMapping("/auth/get")
    public Usuario get() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = (Usuario) authentication.getPrincipal();

        Usuario retorno = new Usuario();
        BeanUtils.copyProperties(usuario, retorno);
        retorno.setSenha(null);

        return retorno;
    }
}
