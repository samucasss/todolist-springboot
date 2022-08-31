package com.samuca.todolist;

import com.samuca.todolist.dao.PreferenciasDao;
import com.samuca.todolist.model.Preferencias;
import com.samuca.todolist.model.Usuario;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PreferenciasRestControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TestUtil testUtil;

    @Autowired
    private PreferenciasDao preferenciasDao;

    @BeforeEach
    public void beforeEach() {
        testUtil.removeAllUsuarios();
        preferenciasDao.deleteAll();
    }

    @Test
    public void testSaveOk() {
        testUtil.saveUsuario();

        String url = "http://localhost:" + port + "/preferencias";

        String token = testUtil.login("samuca@gmail.com");

        Preferencias preferencias = new Preferencias();
        preferencias.setTipoFiltro("T");
        preferencias.setDone(false);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Preferencias> request = new HttpEntity<>(preferencias, headers);
        ResponseEntity<Preferencias> result = this.restTemplate.postForEntity(url, request,
                Preferencias.class);
        Preferencias preferenciasRest = result.getBody();

        Assertions.assertNotNull(preferenciasRest);
        Assertions.assertNotNull(preferenciasRest.getId());
        Assertions.assertEquals("T", preferenciasRest.getTipoFiltro());
        Assertions.assertEquals(false, preferenciasRest.getDone());
    }

    @Test
    public void testSaveSemTipoFiltro() {
        testUtil.saveUsuario();

        String url = "http://localhost:" + port + "/preferencias";

        String token = testUtil.login("samuca@gmail.com");

        Preferencias preferencias = new Preferencias();
        preferencias.setDone(false);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Preferencias> request = new HttpEntity<>(preferencias, headers);
        ResponseEntity<Preferencias> result = this.restTemplate.postForEntity(url, request,
                Preferencias.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    public void testSaveSemDone() {
        testUtil.saveUsuario();

        String url = "http://localhost:" + port + "/preferencias";

        String token = testUtil.login("samuca@gmail.com");

        Preferencias preferencias = new Preferencias();
        preferencias.setTipoFiltro("T");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Preferencias> request = new HttpEntity<>(preferencias, headers);
        ResponseEntity<Preferencias> result = this.restTemplate.postForEntity(url, request,
                Preferencias.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    public void testSaveSemAutenticacao() {
        testUtil.saveUsuario();

        String url = "http://localhost:" + port + "/preferencias";

        Preferencias preferencias = new Preferencias();
        preferencias.setTipoFiltro("T");
        preferencias.setDone(false);

        HttpEntity<Preferencias> request = new HttpEntity<>(preferencias);
        ResponseEntity<Preferencias> result = this.restTemplate.postForEntity(url, request,
                Preferencias.class);

        Assertions.assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
    }

    @Test
    public void testSaveAlteracaoOk() {
        Usuario usuario = testUtil.saveUsuario();

        savePreferencias(usuario);

        String url = "http://localhost:" + port + "/preferencias";

        String token = testUtil.login("samuca@gmail.com");

        Preferencias preferencias = new Preferencias();
        preferencias.setTipoFiltro("H");
        preferencias.setDone(true);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Preferencias> request = new HttpEntity<>(preferencias, headers);
        ResponseEntity<Preferencias> result = this.restTemplate.postForEntity(url, request,
                Preferencias.class);
        Preferencias preferenciasRest = result.getBody();

        Long count = preferenciasDao.countByUsuarioId(usuario.getId());

        Assertions.assertNotNull(preferenciasRest);
        Assertions.assertNotNull(preferenciasRest.getId());
        Assertions.assertEquals(1, count);
        Assertions.assertEquals("H", preferenciasRest.getTipoFiltro());
        Assertions.assertEquals(true, preferenciasRest.getDone());
    }

    @Test
    public void testSaveAlteracaoSemTipoFiltro() {
        Usuario usuario = testUtil.saveUsuario();

        savePreferencias(usuario);

        String url = "http://localhost:" + port + "/preferencias";

        String token = testUtil.login("samuca@gmail.com");

        Preferencias preferencias = new Preferencias();
        preferencias.setDone(true);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Preferencias> request = new HttpEntity<>(preferencias, headers);
        ResponseEntity<Preferencias> result = this.restTemplate.postForEntity(url, request,
                Preferencias.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    public void testSaveAlteracaoSemDone() {
        Usuario usuario = testUtil.saveUsuario();

        savePreferencias(usuario);

        String url = "http://localhost:" + port + "/preferencias";

        String token = testUtil.login("samuca@gmail.com");

        Preferencias preferencias = new Preferencias();
        preferencias.setTipoFiltro("T");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Preferencias> request = new HttpEntity<>(preferencias, headers);
        ResponseEntity<Preferencias> result = this.restTemplate.postForEntity(url, request,
                Preferencias.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    public void testSaveAlteracaoSemAutenticacao() {
        Usuario usuario = testUtil.saveUsuario();

        savePreferencias(usuario);

        String url = "http://localhost:" + port + "/preferencias";

        Preferencias preferencias = new Preferencias();
        preferencias.setTipoFiltro("T");
        preferencias.setDone(true);

        HttpEntity<Preferencias> request = new HttpEntity<>(preferencias);
        ResponseEntity<Preferencias> result = this.restTemplate.postForEntity(url, request,
                Preferencias.class);
        Assertions.assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
    }

    @Test
    public void testGetOk() {
        Usuario usuario = testUtil.saveUsuario();
        savePreferencias(usuario);

        String token = testUtil.login("samuca@gmail.com");

        String url = "http://localhost:" + port + "/preferencia";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        ResponseEntity<Preferencias> responseEntity = restTemplate.exchange(RequestEntity.get(url).
                headers(headers).build(), Preferencias.class);
        Preferencias preferenciasRest = responseEntity.getBody();

        Assertions.assertNotNull(preferenciasRest);
        Assertions.assertNotNull(preferenciasRest.getId());
        Assertions.assertEquals("T", preferenciasRest.getTipoFiltro());
        Assertions.assertEquals(false, preferenciasRest.getDone());
    }

    @Test
    public void testGetSemAutenticacao() {
        Usuario usuario = testUtil.saveUsuario();
        savePreferencias(usuario);

        String url = "http://localhost:" + port + "/preferencia";

        ResponseEntity<Preferencias> result = restTemplate.exchange(RequestEntity.get(url).build(),
                Preferencias.class);
        Assertions.assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
    }

    @Test
    public void testDeleteOk() {
        Usuario usuario = testUtil.saveUsuario();
        savePreferencias(usuario);
        String token = testUtil.login("samuca@gmail.com");

        String url = "http://localhost:" + port + "/preferencia";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity request = new HttpEntity(headers);

        ResponseEntity<String> result  = restTemplate.exchange(url, HttpMethod.DELETE, request,
                String.class);

        Assertions.assertNotNull("OK", result.getBody());
    }

    @Test
    public void testDeleteSemPreferencias() {
        testUtil.saveUsuario();
        String token = testUtil.login("samuca@gmail.com");

        String url = "http://localhost:" + port + "/preferencia";

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
        savePreferencias(usuario);
        String url = "http://localhost:" + port + "/preferencia";

        HttpEntity request = new HttpEntity(null);

        ResponseEntity<String> result  = restTemplate.exchange(url, HttpMethod.DELETE, request,
                String.class);

        Assertions.assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
    }

    private void savePreferencias(Usuario usuario) {
        Preferencias preferencias = new Preferencias();
        preferencias.setTipoFiltro("T");
        preferencias.setDone(false);
        preferencias.setUsuarioId(usuario.getId());

        preferenciasDao.save(preferencias);
    }

}
