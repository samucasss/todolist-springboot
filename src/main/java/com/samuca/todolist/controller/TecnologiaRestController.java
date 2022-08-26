package com.samuca.todolist.controller;

import com.samuca.todolist.dao.TecnologiaDao;
import com.samuca.todolist.model.Tecnologia;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TecnologiaRestController {

    @Autowired
    private TecnologiaDao tecnologiaDao;

    @GetMapping("/tecnologias")
    public List<Tecnologia> hello() {
        List<Tecnologia> tecnologiaList = tecnologiaDao.findAll();
        return tecnologiaList;
    }
}
