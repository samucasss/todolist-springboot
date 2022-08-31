package com.samuca.todolist.controller;

import com.samuca.todolist.dao.PreferenciasDao;
import com.samuca.todolist.model.Preferencias;
import com.samuca.todolist.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class PreferenciasRestController {

    @Autowired
    private PreferenciasDao preferenciasDao;

    @GetMapping("/preferencia")
    public Preferencias get() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = (Usuario) authentication.getPrincipal();

        Preferencias preferencias = preferenciasDao.findByUsuarioId(usuario.getId());
        return preferencias;
    }

    @PostMapping("/preferencias")
    public Preferencias save(@RequestBody @Valid Preferencias preferencias) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = (Usuario) authentication.getPrincipal();

        Preferencias preferenciasExistente = preferenciasDao.findByUsuarioId(usuario.getId());
        if (preferenciasExistente != null) {
            preferencias.setId(preferenciasExistente.getId());
        }

        preferencias.setUsuarioId(usuario.getId());

        preferencias = preferenciasDao.save(preferencias);
        return preferencias;
    }

    @DeleteMapping("/preferencia")
    public ResponseEntity<String> delete() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = (Usuario) authentication.getPrincipal();

        Preferencias preferencias = preferenciasDao.findByUsuarioId(usuario.getId());
        if (preferencias == null) {
            return ResponseEntity.badRequest().body("Nao existe preferencias para o usuario");
        }

        preferenciasDao.delete(preferencias);

        return ResponseEntity.ok("OK");
    }
}
