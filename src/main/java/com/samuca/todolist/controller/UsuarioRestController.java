package com.samuca.todolist.controller;

import com.samuca.todolist.dao.UsuarioDao;
import com.samuca.todolist.model.Usuario;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class UsuarioRestController {

    @Autowired
    private UsuarioDao usuarioDao;

    @PostMapping("/usuarios")
    public Usuario save(@RequestBody @Valid Usuario usuario) {
        usuario = usuarioDao.save(usuario);

        Usuario retorno = new Usuario();
        BeanUtils.copyProperties(usuario, retorno);
        retorno.setSenha(null);

        return retorno;
    }

    @DeleteMapping("/usuario")
    public String delete() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = (Usuario) authentication.getPrincipal();

        usuarioDao.delete(usuario);

        return "OK";
    }
}
