package com.samuca.todolist.controller;

import com.samuca.todolist.dao.TarefaDao;
import com.samuca.todolist.model.Tarefa;
import com.samuca.todolist.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
public class TarefaRestController {

    @Autowired
    private TarefaDao tarefaDao;

    @GetMapping("/tarefas")
    public List<Tarefa> findAllTarefas(
            @Valid @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate inicio,
            @Valid @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fim) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = (Usuario) authentication.getPrincipal();

        inicio = inicio.minusDays(1);

        List<Tarefa> tarefaList = tarefaDao.findAllByDataBetweenAndUsuarioId(inicio, fim, usuario.getId());
        return tarefaList;
    }

    @PostMapping("/tarefas")
    public Tarefa save(@RequestBody @Valid Tarefa tarefa) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = (Usuario) authentication.getPrincipal();

        tarefa.setUsuarioId(usuario.getId());

        tarefa = tarefaDao.save(tarefa);

        return tarefa;
    }

    @DeleteMapping("/tarefas/{id}")
    public ResponseEntity<String> delete(@PathVariable String id) {
        Optional<Tarefa> optionalTarefa = tarefaDao.findById(id);

        if (!optionalTarefa.isPresent()) {
            return ResponseEntity.badRequest().body("Nao existe tarefa para o id " + id);
        }

        tarefaDao.delete(optionalTarefa.get());

        return ResponseEntity.ok("OK");
    }
}
