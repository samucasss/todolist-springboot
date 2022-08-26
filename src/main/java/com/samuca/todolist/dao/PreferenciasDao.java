package com.samuca.todolist.dao;

import com.samuca.todolist.model.Preferencias;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PreferenciasDao extends MongoRepository<Preferencias, String> {

    Preferencias findByUsuarioId(String usuarioId);
    Long countByUsuarioId(String usuarioId);
}
