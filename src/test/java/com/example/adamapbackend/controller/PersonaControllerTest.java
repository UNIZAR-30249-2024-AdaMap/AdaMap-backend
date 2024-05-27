package com.example.adamapbackend.controller;

import com.example.adamapbackend.controller.dto.CreateUser;
import com.example.adamapbackend.domain.Persona;
import com.example.adamapbackend.domain.enums.Departamento;
import com.example.adamapbackend.domain.enums.Rol;
import com.example.adamapbackend.service.PersonaService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PersonaControllerTest {
    private static PersonaService personaService = mock(PersonaService.class);
    private static Persona persona = mock(Persona.class);
    private static Persona admin = mock(Persona.class);
    private static PersonaController personaController = new PersonaController(personaService);

    @Test
    public void shouldReturnCorreoWhenLogin() {
        Optional personaOptional = mock(Optional.class);
        when(personaService.getPersonaById(any())).thenReturn(personaOptional);
        when(personaOptional.isEmpty()).thenReturn(false);
        when(personaOptional.get()).thenReturn(persona);
        when(persona.getCorreo()).thenReturn("email");

        ResponseEntity<String> response = personaController.loginUser("correo");

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertEquals("email", response.getBody());
    }

    @Test
    public void shouldReturn400WhenLogin() {
        Optional personaOptional = mock(Optional.class);
        when(personaService.getPersonaById(any())).thenReturn(personaOptional);
        when(personaOptional.isEmpty()).thenReturn(true);

        ResponseEntity<String> response = personaController.loginUser("correo");

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
    }

    @Test
    public void shouldReturnPersonaWhenCambiarRol() {
        Optional adminOptional = mock(Optional.class);
        Optional personaOptional = mock(Optional.class);

        when(personaService.getPersonaById("correo")).thenReturn(personaOptional);
        when(personaService.getPersonaById("admin")).thenReturn(adminOptional);

        when(personaOptional.isEmpty()).thenReturn(false);
        when(personaOptional.get()).thenReturn(persona);

        when(adminOptional.isEmpty()).thenReturn(false);
        when(adminOptional.get()).thenReturn(admin);

        when(admin.isAdmin()).thenReturn(true);

        ResponseEntity<Persona> response = personaController.cambiarRol("correo", List.of(Rol.CONSERJE.getRol()), "Bearer admin");

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertEquals(persona, response.getBody());

        verify(persona, times(1)).updateRoles(List.of(Rol.CONSERJE));
    }

    @Test
    public void shouldReturn400WhenNoAdminUserCambiarRol() {
        Optional adminOptional = mock(Optional.class);

        when(personaService.getPersonaById("admin")).thenReturn(adminOptional);

        when(adminOptional.isEmpty()).thenReturn(true);

        ResponseEntity<Persona> response = personaController.cambiarRol("correo", List.of(Rol.CONSERJE.getRol()), "Bearer admin");

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
    }

    @Test
    public void shouldReturn401WhenUserIsNotAdminCambiarRol() {
        Optional adminOptional = mock(Optional.class);

        when(personaService.getPersonaById("admin")).thenReturn(adminOptional);

        when(adminOptional.isEmpty()).thenReturn(false);
        when(adminOptional.get()).thenReturn(admin);

        when(admin.isAdmin()).thenReturn(false);

        ResponseEntity<Persona> response = personaController.cambiarRol("correo", List.of(Rol.CONSERJE.getRol()), "Bearer admin");

        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusCode().value());
    }

    @Test
    public void shouldReturn400WhenPersonaIsEmptyCambiarRol() {
        Optional adminOptional = mock(Optional.class);
        Optional personaOptional = mock(Optional.class);

        when(personaService.getPersonaById("correo")).thenReturn(personaOptional);
        when(personaService.getPersonaById("admin")).thenReturn(adminOptional);

        when(personaOptional.isEmpty()).thenReturn(true);
        when(personaOptional.get()).thenReturn(persona);

        when(adminOptional.isEmpty()).thenReturn(false);
        when(adminOptional.get()).thenReturn(admin);

        when(admin.isAdmin()).thenReturn(true);

        ResponseEntity<Persona> response = personaController.cambiarRol("correo", List.of(Rol.CONSERJE.getRol()), "Bearer admin");

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
    }

    @Test
    public void shouldReturn400WhenRolIsNullCambiarRol() {
        Optional adminOptional = mock(Optional.class);
        Optional personaOptional = mock(Optional.class);

        when(personaService.getPersonaById("correo")).thenReturn(personaOptional);
        when(personaService.getPersonaById("admin")).thenReturn(adminOptional);

        when(personaOptional.isEmpty()).thenReturn(false);
        when(personaOptional.get()).thenReturn(persona);

        when(adminOptional.isEmpty()).thenReturn(false);
        when(adminOptional.get()).thenReturn(admin);

        when(admin.isAdmin()).thenReturn(true);

        ResponseEntity<Persona> response = personaController.cambiarRol("correo", null, "Bearer admin");

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
    }

    @Test
    public void shouldReturnPersonaWhenCambiarDepartamento() {
        Optional adminOptional = mock(Optional.class);
        Optional personaOptional = mock(Optional.class);

        when(personaService.getPersonaById("correo")).thenReturn(personaOptional);
        when(personaService.getPersonaById("admin")).thenReturn(adminOptional);

        when(personaOptional.isEmpty()).thenReturn(false);
        when(personaOptional.get()).thenReturn(persona);

        when(adminOptional.isEmpty()).thenReturn(false);
        when(adminOptional.get()).thenReturn(admin);

        when(admin.isAdmin()).thenReturn(true);

        ResponseEntity<Persona> response = personaController.cambiarDepartamento("correo", Departamento.DIIS.getDepartamento(), "Bearer admin");

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertEquals(persona, response.getBody());

        verify(persona, times(1)).updateDepartamento(Departamento.DIIS);
    }

    @Test
    public void shouldReturn400WhenNoAdminUserCambiarDepartamento() {
        Optional adminOptional = mock(Optional.class);

        when(personaService.getPersonaById("admin")).thenReturn(adminOptional);

        when(adminOptional.isEmpty()).thenReturn(true);

        ResponseEntity<Persona> response = personaController.cambiarDepartamento("correo", Departamento.DIIS.getDepartamento(), "Bearer admin");

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
    }

    @Test
    public void shouldReturn401WhenUserIsNotAdminCambiarDepartamento() {
        Optional adminOptional = mock(Optional.class);

        when(personaService.getPersonaById("admin")).thenReturn(adminOptional);

        when(adminOptional.isEmpty()).thenReturn(false);
        when(adminOptional.get()).thenReturn(admin);

        when(admin.isAdmin()).thenReturn(false);

        ResponseEntity<Persona> response = personaController.cambiarDepartamento("correo", Departamento.DIIS.getDepartamento(), "Bearer admin");

        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusCode().value());
    }

    @Test
    public void shouldReturn400WhenPersonaIsEmptyCambiarDepartamento() {
        Optional adminOptional = mock(Optional.class);
        Optional personaOptional = mock(Optional.class);

        when(personaService.getPersonaById("correo")).thenReturn(personaOptional);
        when(personaService.getPersonaById("admin")).thenReturn(adminOptional);

        when(personaOptional.isEmpty()).thenReturn(true);
        when(personaOptional.get()).thenReturn(persona);

        when(adminOptional.isEmpty()).thenReturn(false);
        when(adminOptional.get()).thenReturn(admin);

        when(admin.isAdmin()).thenReturn(true);

        ResponseEntity<Persona> response = personaController.cambiarDepartamento("correo", Departamento.DIIS.getDepartamento(), "Bearer admin");

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
    }

    @Test
    public void shouldReturnPersonaWhenAnyadirPersona() {
        Optional adminOptional = mock(Optional.class);

        reset(personaService);
        when(personaService.getPersonaById("admin")).thenReturn(adminOptional);

        when(adminOptional.isEmpty()).thenReturn(false);
        when(adminOptional.get()).thenReturn(admin);

        when(admin.isAdmin()).thenReturn(true);

        CreateUser createUser = new CreateUser();
        createUser.setCorreo("correo");
        createUser.setNombre("usuario");
        createUser.setDepartamento(null);
        createUser.setRoles(List.of(Rol.GERENTE.getRol()));


        ResponseEntity<Persona> response = personaController.anyadirPersona(createUser, "Bearer admin");

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());

        verify(personaService, times(1)).guardarPersona(any());
        reset(personaService);
    }

    @Test
    public void shouldReturn400WhenNoAdminUserAnyadirPersona() {
        Optional adminOptional = mock(Optional.class);

        when(personaService.getPersonaById("admin")).thenReturn(adminOptional);

        when(adminOptional.isEmpty()).thenReturn(true);

        CreateUser createUser = new CreateUser();
        createUser.setCorreo("correo");
        createUser.setNombre("usuario");
        createUser.setDepartamento(null);
        createUser.setRoles(List.of(Rol.GERENTE.getRol()));

        ResponseEntity<Persona> response = personaController.anyadirPersona(createUser, "Bearer admin");

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
    }

    @Test
    public void shouldReturn401WhenUserIsNotAdminAnyadirPersona() {
        Optional adminOptional = mock(Optional.class);

        when(personaService.getPersonaById("admin")).thenReturn(adminOptional);

        when(adminOptional.isEmpty()).thenReturn(false);
        when(adminOptional.get()).thenReturn(admin);

        when(admin.isAdmin()).thenReturn(false);

        CreateUser createUser = new CreateUser();
        createUser.setCorreo("correo");
        createUser.setNombre("usuario");
        createUser.setDepartamento(null);
        createUser.setRoles(List.of(Rol.GERENTE.getRol()));

        ResponseEntity<Persona> response = personaController.anyadirPersona(createUser, "Bearer admin");

        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusCode().value());
    }
}