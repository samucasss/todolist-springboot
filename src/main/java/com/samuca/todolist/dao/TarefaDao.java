package com.samuca.todolist.dao;

import com.samuca.todolist.model.Tarefa;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

public interface TarefaDao extends MongoRepository<Tarefa, String> {

    List<Tarefa> findAllByDataBetweenAndUsuarioId(LocalDate inicio, LocalDate fim, String usuarioId);
}
