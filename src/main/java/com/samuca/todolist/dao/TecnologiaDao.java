package com.samuca.todolist.dao;

import com.samuca.todolist.model.Tecnologia;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TecnologiaDao extends MongoRepository<Tecnologia, String> {

}
