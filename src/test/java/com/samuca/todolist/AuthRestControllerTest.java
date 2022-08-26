package com.samuca.todolist;

import com.samuca.todolist.model.Usuario;
import com.samuca.todolist.security.Login;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthRestControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TestUtil testUtil;

    @BeforeEach
    public void removeAllUsuariosBefore() {
        testUtil.removeAllUsuarios();
    }

    @Test
    public void testRegisterOk() {
        String url = "http://localhost:" + port + "/auth/register";

        Usuario usuario = new Usuario();
        usuario.setNome("Samuel");
        usuario.setEmail("samuca@gmail.com");
        usuario.setSenha("samuca");

        Usuario usuarioRest =  this.restTemplate.postForObject(url, usuario, Usuario.class);

        Assertions.assertNotNull(usuarioRest);
        Assertions.assertNotNull(usuarioRest.getId());
        Assertions.assertNull(usuarioRest.getSenha());
        Assertions.assertEquals("Samuel", usuarioRest.getNome());
        Assertions.assertEquals("samuca@gmail.com", usuarioRest.getEmail());
    }

    @Test
    public void testRegisterNomeNaoPreenchido() {
        String url = "http://localhost:" + port + "/auth/register";

        Usuario usuario = new Usuario();
        usuario.setEmail("samuca@gmail.com");
        usuario.setSenha("samuca");

        ResponseEntity<String> response = restTemplate.postForEntity(url, usuario, String.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testRegisterEmailNaoPreenchido() {
        String url = "http://localhost:" + port + "/auth/register";

        Usuario usuario = new Usuario();
        usuario.setNome("Samuel");
        usuario.setSenha("samuca");

        ResponseEntity<String> response = restTemplate.postForEntity(url, usuario, String.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testRegisterSenhaNaoPreenchida() {
        String url = "http://localhost:" + port + "/auth/register";

        Usuario usuario = new Usuario();
        usuario.setNome("Samuel");
        usuario.setEmail("samuca@gmail.com");

        ResponseEntity<String> response = restTemplate.postForEntity(url, usuario, String.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testLoginOk() {
        testUtil.saveUsuario();

        String url = "http://localhost:" + port + "/auth/login";

        Login credentials = new Login("samuca@gmail.com", "samuca");
        String token =  this.restTemplate.postForObject(url, credentials, String.class);

        Assertions.assertNotNull(token);
    }

    @Test
    public void testLoginSenhaIncorreta() {
        testUtil.saveUsuario();

        String url = "http://localhost:" + port + "/auth/login";

        Login credentials = new Login("samuca@gmail.com", "samuca123");

        ResponseEntity<String> response = restTemplate.postForEntity(url, credentials, String.class);
        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void testLoginEmailInexistente() {
        testUtil.saveUsuario();

        String url = "http://localhost:" + port + "/auth/login";

        Login credentials = new Login("samucasss@gmail.com", "samuca");

        ResponseEntity<String> response = restTemplate.postForEntity(url, credentials, String.class);
        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void testLoginCamposNaoPreenchidos() {
        testUtil.saveUsuario();

        String url = "http://localhost:" + port + "/auth/login";

        Login credentials = new Login("", "");

        ResponseEntity<String> response = restTemplate.postForEntity(url, credentials, String.class);
        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void testGetOk() {
        testUtil.saveUsuario();

        String token = testUtil.login("samuca@gmail.com");

        String url = "http://localhost:" + port + "/auth/get";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        ResponseEntity<Usuario> responseEntity = restTemplate.exchange(RequestEntity.get(url).
                headers(headers).build(), Usuario.class);
        Usuario usuarioRest = responseEntity.getBody();

        Assertions.assertNotNull(usuarioRest);
        Assertions.assertNotNull(usuarioRest.getId());
        Assertions.assertNull(usuarioRest.getSenha());
        Assertions.assertEquals("Samuel", usuarioRest.getNome());
        Assertions.assertEquals("samuca@gmail.com", usuarioRest.getEmail());
    }

    @Test
    public void testGetNaoAutenticado() {
        testUtil.saveUsuario();

        String url = "http://localhost:" + port + "/auth/get";

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

}
