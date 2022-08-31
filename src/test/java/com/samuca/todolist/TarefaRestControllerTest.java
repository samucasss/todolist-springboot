package com.samuca.todolist;

import com.samuca.todolist.dao.TarefaDao;
import com.samuca.todolist.model.Tarefa;
import com.samuca.todolist.model.Usuario;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.time.LocalDate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TarefaRestControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TarefaDao tarefaDao;

    @Autowired
    private TestUtil testUtil;

    @BeforeEach
    public void beforeEach() {
        testUtil.removeAllUsuarios();
        tarefaDao.deleteAll();
    }

    @Test
    public void testFindAllByPeriodo() {
        Usuario usuario = testUtil.saveUsuario();
        saveTarefas(usuario);

        String token = testUtil.login("samuca@gmail.com");

        String url = "http://localhost:" + port + "/tarefas?inicio=2022-08-20&fim=2022-08-31";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        ResponseEntity<Tarefa[]> responseEntity = restTemplate.exchange(RequestEntity.get(url).
                headers(headers).build(), Tarefa[].class);
        Tarefa[] tarefaList = responseEntity.getBody();

        Assertions.assertNotNull(tarefaList);
        Assertions.assertEquals(2, tarefaList.length);

        Tarefa tarefa1 = tarefaList[0];
        Tarefa tarefa2 = tarefaList[1];

        Assertions.assertNotNull(tarefa1.getId());
        Assertions.assertEquals(LocalDate.of(2022, 8, 26), tarefa1.getData());
        Assertions.assertEquals("Tarefa 1", tarefa1.getNome());
        Assertions.assertEquals("Descrição Tarefa 1", tarefa1.getDescricao());
        Assertions.assertEquals(false, tarefa1.getDone());
        Assertions.assertEquals(usuario.getId(), tarefa1.getUsuarioId());

        Assertions.assertNotNull(tarefa2.getId());
        Assertions.assertEquals(LocalDate.of(2022, 8, 27), tarefa2.getData());
        Assertions.assertEquals("Tarefa 2", tarefa2.getNome());
        Assertions.assertNull(tarefa2.getDescricao());
        Assertions.assertEquals(true, tarefa2.getDone());
        Assertions.assertEquals(usuario.getId(), tarefa2.getUsuarioId());
    }

    @Test
    public void testFindAllByPeriodoSemInicio() {
        Usuario usuario = testUtil.saveUsuario();
        saveTarefas(usuario);

        String token = testUtil.login("samuca@gmail.com");

        String url = "http://localhost:" + port + "/tarefas?fim=2022-08-31";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        ResponseEntity<String> result = restTemplate.exchange(RequestEntity.get(url).
                headers(headers).build(), String.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    public void testFindAllByPeriodoSemFim() {
        Usuario usuario = testUtil.saveUsuario();
        saveTarefas(usuario);

        String token = testUtil.login("samuca@gmail.com");

        String url = "http://localhost:" + port + "/tarefas?inicio=2022-08-31";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        ResponseEntity<String> result = restTemplate.exchange(RequestEntity.get(url).
                headers(headers).build(), String.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    public void testFindAllByPeriodoSemAutenticacao() {
        Usuario usuario = testUtil.saveUsuario();
        saveTarefas(usuario);

        String url = "http://localhost:" + port + "/tarefas?inicio=2022-08-31";

        ResponseEntity<String> result = restTemplate.exchange(RequestEntity.get(url).build(), String.class);
        Assertions.assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
    }

    @Test
    public void testSaveOk() {
        Usuario usuario = testUtil.saveUsuario();

        String token = testUtil.login("samuca@gmail.com");

        Tarefa tarefa = new Tarefa();
        tarefa.setData(LocalDate.of(2022, 8, 26));
        tarefa.setNome("Tarefa 1");
        tarefa.setDescricao("Descrição Tarefa 1");
        tarefa.setDone(false);

        String url = "http://localhost:" + port + "/tarefas";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Tarefa> request = new HttpEntity<>(tarefa, headers);
        ResponseEntity<Tarefa> result = this.restTemplate.postForEntity(url, request,
                Tarefa.class);
        Tarefa tarefaRest = result.getBody();

        Assertions.assertNotNull(tarefaRest);
        Assertions.assertEquals(LocalDate.of(2022, 8, 26), tarefaRest.getData());
        Assertions.assertNotNull(tarefaRest.getId());
        Assertions.assertEquals("Tarefa 1", tarefaRest.getNome());
        Assertions.assertEquals("Descrição Tarefa 1", tarefaRest.getDescricao());
        Assertions.assertEquals(false, tarefaRest.getDone());
        Assertions.assertEquals(usuario.getId(), tarefaRest.getUsuarioId());
    }

    @Test
    public void testSaveSemData() {
        Usuario usuario = testUtil.saveUsuario();
        saveTarefa(usuario);

        String token = testUtil.login("samuca@gmail.com");

        Tarefa tarefa = new Tarefa();
        tarefa.setUsuarioId(usuario.getId());
        tarefa.setNome("Tarefa 1");
        tarefa.setDescricao("Descrição Tarefa 1");
        tarefa.setDone(false);

        String url = "http://localhost:" + port + "/tarefas";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Tarefa> request = new HttpEntity<>(tarefa, headers);
        ResponseEntity<Tarefa> result = this.restTemplate.postForEntity(url, request,
                Tarefa.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    public void testSaveSemNome() {
        Usuario usuario = testUtil.saveUsuario();
        saveTarefa(usuario);

        String token = testUtil.login("samuca@gmail.com");

        Tarefa tarefa = new Tarefa();
        tarefa.setUsuarioId(usuario.getId());
        tarefa.setData(LocalDate.of(2022, 8, 26));
        tarefa.setDescricao("Descrição Tarefa 1");
        tarefa.setDone(false);

        String url = "http://localhost:" + port + "/tarefas";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Tarefa> request = new HttpEntity<>(tarefa, headers);
        ResponseEntity<Tarefa> result = this.restTemplate.postForEntity(url, request,
                Tarefa.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    public void testSaveSemAutenticacao() {
        Tarefa tarefa = new Tarefa();
        tarefa.setData(LocalDate.of(2022, 8, 26));
        tarefa.setNome("Tarefa 1");
        tarefa.setDescricao("Descrição Tarefa 1");
        tarefa.setDone(false);

        String url = "http://localhost:" + port + "/tarefas";

        HttpEntity<Tarefa> request = new HttpEntity<>(tarefa);
        ResponseEntity<Tarefa> result = this.restTemplate.postForEntity(url, request,
                Tarefa.class);
        Assertions.assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
    }

    @Test
    public void testSaveAlteracaoOk() {
        Usuario usuario = testUtil.saveUsuario();
        Tarefa t = saveTarefa(usuario);

        String token = testUtil.login("samuca@gmail.com");

        Tarefa tarefa = new Tarefa();
        tarefa.setId(t.getId());
        tarefa.setData(LocalDate.of(2022, 8, 28));
        tarefa.setNome("Tarefa 1 alterada");
        tarefa.setDescricao("Descrição Tarefa 1 alterada");
        tarefa.setDone(true);

        String url = "http://localhost:" + port + "/tarefas";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Tarefa> request = new HttpEntity<>(tarefa, headers);
        ResponseEntity<Tarefa> result = this.restTemplate.postForEntity(url, request,
                Tarefa.class);
        Tarefa tarefaRest = result.getBody();

        Assertions.assertNotNull(tarefaRest);
        Assertions.assertEquals(LocalDate.of(2022, 8, 28), tarefaRest.getData());
        Assertions.assertEquals(t.getId(), tarefaRest.getId());
        Assertions.assertEquals("Tarefa 1 alterada", tarefaRest.getNome());
        Assertions.assertEquals("Descrição Tarefa 1 alterada", tarefaRest.getDescricao());
        Assertions.assertEquals(true, tarefaRest.getDone());
        Assertions.assertEquals(usuario.getId(), tarefaRest.getUsuarioId());
    }

    @Test
    public void testSaveAlteracaoSemData() {
        Usuario usuario = testUtil.saveUsuario();
        Tarefa t = saveTarefa(usuario);

        String token = testUtil.login("samuca@gmail.com");

        Tarefa tarefa = new Tarefa();
        tarefa.setId(t.getId());
        tarefa.setNome("Tarefa 1 alterada");
        tarefa.setDescricao("Descrição Tarefa 1 alterada");
        tarefa.setDone(true);

        String url = "http://localhost:" + port + "/tarefas";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Tarefa> request = new HttpEntity<>(tarefa, headers);
        ResponseEntity<Tarefa> result = this.restTemplate.postForEntity(url, request,
                Tarefa.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    public void testSaveAlteracaoSemNome() {
        Usuario usuario = testUtil.saveUsuario();
        Tarefa t = saveTarefa(usuario);

        String token = testUtil.login("samuca@gmail.com");

        Tarefa tarefa = new Tarefa();
        tarefa.setId(t.getId());
        tarefa.setData(LocalDate.of(2022, 8, 28));
        tarefa.setDescricao("Descrição Tarefa 1 alterada");
        tarefa.setDone(true);

        String url = "http://localhost:" + port + "/tarefas";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Tarefa> request = new HttpEntity<>(tarefa, headers);
        ResponseEntity<Tarefa> result = this.restTemplate.postForEntity(url, request,
                Tarefa.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    public void testSaveAlteracaoSemAutenticacao() {
        Usuario usuario = testUtil.saveUsuario();
        Tarefa t = saveTarefa(usuario);

        Tarefa tarefa = new Tarefa();
        tarefa.setId(t.getId());
        tarefa.setData(LocalDate.of(2022, 8, 28));
        tarefa.setNome("Tarefa 1 alterada");
        tarefa.setDescricao("Descrição Tarefa 1 alterada");
        tarefa.setDone(true);

        String url = "http://localhost:" + port + "/tarefas";

        HttpEntity<Tarefa> request = new HttpEntity<>(tarefa);
        ResponseEntity<Tarefa> result = this.restTemplate.postForEntity(url, request,
                Tarefa.class);
        Assertions.assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
    }

    @Test
    public void testDeleteOk() {
        Usuario usuario = testUtil.saveUsuario();
        Tarefa t = saveTarefa(usuario);

        String token = testUtil.login("samuca@gmail.com");

        String url = "http://localhost:" + port + "/tarefas/" + t.getId();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity request = new HttpEntity(headers);

        ResponseEntity<String> result  = restTemplate.exchange(url, HttpMethod.DELETE, request,
                String.class);

        Assertions.assertNotNull("OK", result.getBody());
    }

    @Test
    public void testDeleteSemTarefa() {
        testUtil.saveUsuario();
        String token = testUtil.login("samuca@gmail.com");

        String url = "http://localhost:" + port + "/tarefas/12345";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity request = new HttpEntity(headers);

        ResponseEntity<String> result  = restTemplate.exchange(url, HttpMethod.DELETE, request,
                String.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    public void testDeleteNaoAutenticado() {
        Usuario usuario = testUtil.saveUsuario();
        Tarefa t = saveTarefa(usuario);

        String url = "http://localhost:" + port + "/tarefas/" + t.getId();

        HttpEntity request = new HttpEntity(null);

        ResponseEntity<String> result  = restTemplate.exchange(url, HttpMethod.DELETE, request,
                String.class);

        Assertions.assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
    }

    private Tarefa saveTarefa(Usuario usuario) {
        Tarefa tarefa = new Tarefa();
        tarefa.setUsuarioId(usuario.getId());
        tarefa.setData(LocalDate.of(2022, 8, 26));
        tarefa.setNome("Tarefa 1");
        tarefa.setDescricao("Descrição Tarefa 1");
        tarefa.setDone(false);

        tarefaDao.save(tarefa);

        return tarefa;
    }

    private void saveTarefas(Usuario usuario) {
        Tarefa tarefa1 = new Tarefa();
        tarefa1.setUsuarioId(usuario.getId());
        tarefa1.setData(LocalDate.of(2022, 8, 26));
        tarefa1.setNome("Tarefa 1");
        tarefa1.setDescricao("Descrição Tarefa 1");
        tarefa1.setDone(false);

        tarefaDao.save(tarefa1);

        Tarefa tarefa2 = new Tarefa();
        tarefa2.setUsuarioId(usuario.getId());
        tarefa2.setData(LocalDate.of(2022, 8, 27));
        tarefa2.setNome("Tarefa 2");
        tarefa2.setDone(true);

        tarefaDao.save(tarefa2);
    }


}
