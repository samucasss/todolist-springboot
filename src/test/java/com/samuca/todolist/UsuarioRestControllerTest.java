package com.samuca.todolist;

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
public class UsuarioRestControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TestUtil testUtil;

    @BeforeEach
    public void beforeEach() {
        testUtil.removeAllUsuarios();
        testUtil.saveUsuario();
    }

    @Test
    public void testAlterarUsuarioOk() {
        String url = "http://localhost:" + port + "/usuarios";

        String token = testUtil.login("samuca@gmail.com");

        Usuario usuario = new Usuario();
        usuario.setNome("Samuel Santos");
        usuario.setEmail("samuca.santos@gmail.com");
        usuario.setSenha("samuca");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Usuario> request = new HttpEntity<>(usuario, headers);
        ResponseEntity<Usuario> result = this.restTemplate.postForEntity(url, request, Usuario.class);
        Usuario usuarioRest = result.getBody();

        Assertions.assertNotNull(usuarioRest);
        Assertions.assertNotNull(usuarioRest.getId());
        Assertions.assertNull(usuarioRest.getSenha());
        Assertions.assertEquals("Samuel Santos", usuarioRest.getNome());
        Assertions.assertEquals("samuca.santos@gmail.com", usuarioRest.getEmail());
    }

    @Test
    public void testAlterarUsuarioSemNome() {
        String url = "http://localhost:" + port + "/usuarios";

        String token = testUtil.login("samuca@gmail.com");

        Usuario usuario = new Usuario();
        usuario.setEmail("samuca.santos@gmail.com");
        usuario.setSenha("samuca");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Usuario> request = new HttpEntity<>(usuario, headers);
        ResponseEntity<Usuario> result = this.restTemplate.postForEntity(url, request, Usuario.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    public void testAlterarUsuarioSemSemEmail() {
        String url = "http://localhost:" + port + "/usuarios";

        String token = testUtil.login("samuca@gmail.com");

        Usuario usuario = new Usuario();
        usuario.setNome("Samuel Santos");
        usuario.setSenha("samuca");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Usuario> request = new HttpEntity<>(usuario, headers);
        ResponseEntity<Usuario> result = this.restTemplate.postForEntity(url, request, Usuario.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    public void testAlterarUsuarioSemSemSenha() {
        String url = "http://localhost:" + port + "/usuarios";

        String token = testUtil.login("samuca@gmail.com");

        Usuario usuario = new Usuario();
        usuario.setEmail("samuca.santos@gmail.com");
        usuario.setNome("Samuel Santos");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Usuario> request = new HttpEntity<>(usuario, headers);
        ResponseEntity<Usuario> result = this.restTemplate.postForEntity(url, request, Usuario.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    public void testAlterarUsuarioNaoAutenticado() {
        String url = "http://localhost:" + port + "/usuarios";

        Usuario usuario = new Usuario();
        usuario.setEmail("samuca.santos@gmail.com");
        usuario.setNome("Samuel Santos");

        HttpEntity<Usuario> request = new HttpEntity<>(usuario);
        ResponseEntity<Usuario> result = this.restTemplate.postForEntity(url, request, Usuario.class);

        Assertions.assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
    }

    @Test
    public void testDeleteOk() {
        String token = testUtil.login("samuca@gmail.com");

        String url = "http://localhost:" + port + "/usuario";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity request = new HttpEntity(headers);

        ResponseEntity<String> result  = restTemplate.exchange(url, HttpMethod.DELETE, request,
                String.class);

        Assertions.assertNotNull("OK", result.getBody());
    }

    @Test
    public void testDeleteUsuarioNaoAutenticado() {
        String url = "http://localhost:" + port + "/usuario";

        HttpEntity request = new HttpEntity(null);

        ResponseEntity<String> result  = restTemplate.exchange(url, HttpMethod.DELETE, request,
                String.class);

        Assertions.assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
    }

}
