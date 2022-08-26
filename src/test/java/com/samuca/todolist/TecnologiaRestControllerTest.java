package com.samuca.todolist;

import com.samuca.todolist.model.Tecnologia;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TecnologiaRestControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testFindAll() {
        String url = "http://localhost:" + port + "/tecnologias";
        List<Tecnologia> tecnologiaList = this.restTemplate.getForObject(url, List.class);

        assertThat(tecnologiaList.size()>0);
    }

}
