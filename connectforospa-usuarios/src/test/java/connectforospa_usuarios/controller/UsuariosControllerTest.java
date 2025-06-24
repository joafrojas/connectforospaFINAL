package connectforospa_usuarios.controller;

import connectforospa_usuarios.model.Rol;
import connectforospa_usuarios.model.Usuarios;
import connectforospa_usuarios.service.UsuariosService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;

@WebMvcTest(UsuariosController.class)
@AutoConfigureMockMvc(addFilters = false)
class UsuariosControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuariosService service;

    @Test
    void deberiaRegistrarUsuario() throws Exception {
        when(service.guardar(any(), anyString()))
                .thenAnswer(inv -> ResponseEntity.status(201)
                .body("Usuario registrado exitosamente con rol: ROLE_USER"));

        mockMvc.perform(post("/api/v1/usuarios/registrar")
                        .header("Authorization", "Bearer TOKEN_USER")
                        .contentType(APPLICATION_JSON)
                        .content("""
                            {
                              "nombreUsuario": "joan",
                              "email": "joan@example.com",
                              "password": "1234",
                              "fechaNacimiento": "2000-01-01"
                            }
                        """))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("registrado exitosamente")));
    }

    

    @Test
    void deberiaObtenerUsuario() throws Exception {
        Usuarios usuario = Usuarios.builder().idUsuario(1L).nombreUsuario("joan").build();
        when(service.obtenerPorId(eq(1L)))
                .thenAnswer(inv -> ResponseEntity.ok(usuario));

        mockMvc.perform(get("/api/v1/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreUsuario").value("joan"));
    }

    @Test
    void deberiaActualizarUsuario() throws Exception {
        when(service.actualizar(eq(1L), any()))
                .thenAnswer(inv -> ResponseEntity.ok("Usuario actualizado correctamente."));

        mockMvc.perform(put("/api/v1/usuarios/1")
                        .contentType(APPLICATION_JSON)
                        .content("""
                            {
                              "nombre": "Nuevo Nombre"
                            }
                        """))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("actualizado correctamente")));
    }

    @Test
    void deberiaActualizarFechaNacimiento() throws Exception {
        when(service.actualizarFechaNacimiento(eq(1L), eq("1999-05-05")))
                .thenAnswer(inv -> ResponseEntity.ok("Fecha de nacimiento actualizada correctamente."));

        mockMvc.perform(patch("/api/v1/usuarios/1/fechadenacimiento?fechaNacimiento=1999-05-05"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("nacimiento actualizada")));
    }

    @Test
    void deberiaEliminarUsuarioAdmin() throws Exception {
        when(service.eliminarUsuarioAdmin(eq(2L), eq(1L)))
                .thenAnswer(inv -> ResponseEntity.ok("Usuario eliminado correctamente."));

        mockMvc.perform(delete("/api/v1/usuarios/admin/2/eliminar/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("eliminado correctamente")));
    }

    @Test
    void deberiaAprobarUsuario() throws Exception {
        when(service.aprobarUsuario(eq(2L), eq(1L), eq("ROLE_SOPORTE")))
                .thenAnswer(inv -> ResponseEntity.ok("Usuario aprobado como: ROLE_SOPORTE"));

        mockMvc.perform(put("/api/v1/usuarios/admin/2/aprobar/1?aprobado=ROLE_SOPORTE"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("aprobado como")));
    }

    @Test
    void deberiaRechazarUsuario() throws Exception {
        when(service.rechazarUsuario(eq(2L), eq(1L)))
                .thenAnswer(inv -> ResponseEntity.ok("Usuario rechazado."));

        mockMvc.perform(put("/api/v1/usuarios/admin/2/usuarios/1/rechazar"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("rechazado")));
    }

    @Test
    void deberiaListarUsuarios() throws Exception {
        when(service.listarTodos()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/usuarios/listar"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void deberiaFiltrarUsuarios() throws Exception {
        when(service.filtrarPorInicial(eq("J"))).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/usuarios/listar/filtro?letra=J"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void deberiaObtenerRoles() throws Exception {
        Rol rolAdmin = new Rol();
        rolAdmin.setNombreRol("ROLE_ADMIN");
        Rol rolUser = new Rol();
        rolUser.setNombreRol("ROLE_USER");

        when(service.listarRolesPorUsuario(eq(1L)))
                .thenReturn(List.of(rolAdmin, rolUser));

        mockMvc.perform(get("/api/v1/usuarios/1/roles"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("ROLE_ADMIN")))
                .andExpect(content().string(containsString("ROLE_USER")));
    }

    @Test
    void login_exitoso_devuelveOkConUsuario() throws Exception {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("id", 1L);
        respuesta.put("nombreUsuario", "admin");
        respuesta.put("roles", List.of("ROLE_ADMIN"));

        when(service.login(eq("admin@connectforo.cl"), eq("123456"))).thenReturn((ResponseEntity) ResponseEntity.ok(respuesta));

        mockMvc.perform(post("/api/v1/usuarios/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "email": "admin@connectforo.cl",
                        "password": "123456"
                    }
                """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nombreUsuario").value("admin"));
    }

    @Test
    void login_credencialesInvalidas_devuelveUnauthorized() throws Exception {
        when(service.login("fake@correo.cl", "incorrecto"))
            .thenReturn((ResponseEntity) ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas."));

        mockMvc.perform(post("/api/v1/usuarios/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "email": "fake@correo.cl",
                        "password": "incorrecto"
                    }
                """))
            .andExpect(status().isUnauthorized())
            .andExpect(content().string("Credenciales inválidas."));
    }
}
