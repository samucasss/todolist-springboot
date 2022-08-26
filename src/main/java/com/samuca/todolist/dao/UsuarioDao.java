package com.samuca.todolist.dao;

import com.samuca.todolist.model.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UsuarioDao extends MongoRepository<Usuario, String> {

    Usuario findByEmail(String email);
    Optional<Usuario> findById(String id);
}
