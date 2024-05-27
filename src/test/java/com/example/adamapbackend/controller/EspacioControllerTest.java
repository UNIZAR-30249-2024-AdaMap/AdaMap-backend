package com.example.adamapbackend.controller;

import com.example.adamapbackend.domain.Espacio;
import com.example.adamapbackend.domain.Horario;
import com.example.adamapbackend.domain.Persona;
import com.example.adamapbackend.domain.enums.Departamento;
import com.example.adamapbackend.domain.enums.Rol;
import com.example.adamapbackend.domain.enums.TipoEspacio;
import com.example.adamapbackend.service.EspacioService;
import com.example.adamapbackend.service.PersonaService;
import com.example.adamapbackend.service.ReservaService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EspacioControllerTest {
    private final EspacioService espacioService = mock(EspacioService.class);
    private final PersonaService personaService = mock(PersonaService.class);
    private final ReservaService reservaService = mock(ReservaService.class);
    private final Espacio espacio = mock(Espacio.class);
    private final Persona admin = mock(Persona.class);
    private final Persona persona = mock(Persona.class);
    private final EspacioController espacioController = new EspacioController(espacioService, personaService, reservaService);

    @Test
    public void shouldReturnReservaWhenBuscarEspacioPorId() {
        Optional espacioOptional = mock(Optional.class);
        when(espacioOptional.isEmpty()).thenReturn(false);
        when(espacioOptional.get()).thenReturn(espacio);

        when(espacioService.getEspacioById(any())).thenReturn(espacioOptional);

        ResponseEntity result = espacioController.buscarEspacioPorId(UUID.randomUUID().toString());

        assertEquals(espacio, result.getBody());
        assertEquals(HttpStatus.OK.value(), result.getStatusCode().value());

        verify(espacioService, times(1)).getEspacioById(any());
    }

    @Test
    public void shouldReturn400WhenBuscarEspacioPorId() {
        Optional espacioOptional = mock(Optional.class);
        when(espacioOptional.isEmpty()).thenReturn(true);

        when(reservaService.getReservaById(any())).thenReturn(espacioOptional);

        ResponseEntity result = espacioController.buscarEspacioPorId(UUID.randomUUID().toString());

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getStatusCode().value());
    }

    @Test
    public void shouldCallGetEspaciosWhenBuscarEspacios() {
        when(espacioService.getEspacios(any(), any(), any())).thenReturn(List.of(espacio));

        ResponseEntity<List<Espacio>> response = espacioController.buscarEspacios(0, "", 10);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertEquals(List.of(espacio), response.getBody());
        verify(espacioService, times(1)).getEspacios(any(), any(), any());

    }

    @Test
    public void shouldReturnEspacioWhenCambiarReservabilidad() {
        Optional adminOptional = mock(Optional.class);
        Optional espacioOptional = mock(Optional.class);

        when(personaService.getPersonaById("admin")).thenReturn(adminOptional);

        when(adminOptional.isEmpty()).thenReturn(false);
        when(adminOptional.get()).thenReturn(admin);

        when(admin.isAdmin()).thenReturn(true);

        when(espacioService.getEspacioById(any())).thenReturn(espacioOptional);
        when(espacioOptional.isEmpty()).thenReturn(false);
        when(espacioOptional.get()).thenReturn(espacio);

        ResponseEntity<Espacio> response = espacioController.cambiarReservabilidad("idEspacio", "Bearer tokenAdmin");

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertEquals(espacio, response.getBody());

        verify(espacio, times(1)).cambiarReservabilidad();
    }

    @Test
    public void shouldReturn400WhenNoAdminUserCambiarReservabilidad() {
        Optional adminOptional = mock(Optional.class);

        when(personaService.getPersonaById("admin")).thenReturn(adminOptional);

        when(adminOptional.isEmpty()).thenReturn(true);

        ResponseEntity<Espacio> response = espacioController.cambiarReservabilidad("idEspacio", "Bearer tokenAdmin");

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
    }

    @Test
    public void shouldReturn401WhenUserIsNotAdminCambiarReservabilidad() {
        Optional adminOptional = mock(Optional.class);

        when(personaService.getPersonaById("admin")).thenReturn(adminOptional);

        when(adminOptional.isEmpty()).thenReturn(false);
        when(adminOptional.get()).thenReturn(admin);

        when(admin.isAdmin()).thenReturn(false);


        ResponseEntity<Espacio> response = espacioController.cambiarReservabilidad("idEspacio", "Bearer tokenAdmin");

        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusCode().value());
    }

    @Test
    public void shouldReturn400WhenEspacioIsEmptyCambiarReservabilidad() {
        Optional adminOptional = mock(Optional.class);
        Optional espacioOptional = mock(Optional.class);

        when(personaService.getPersonaById("admin")).thenReturn(adminOptional);


        when(adminOptional.isEmpty()).thenReturn(false);
        when(adminOptional.get()).thenReturn(admin);

        when(admin.isAdmin()).thenReturn(true);


        when(espacioService.getEspacioById(any())).thenReturn(espacioOptional);
        when(espacioOptional.isEmpty()).thenReturn(true);

        ResponseEntity<Espacio> response = espacioController.cambiarReservabilidad("idEspacio", "Bearer tokenAdmin");

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
    }

    @Test
    public void shouldReturnEspacioWhenCambiarCategoria() {
        Optional adminOptional = mock(Optional.class);
        Optional espacioOptional = mock(Optional.class);

        when(personaService.getPersonaById("admin")).thenReturn(adminOptional);

        when(adminOptional.isEmpty()).thenReturn(false);
        when(adminOptional.get()).thenReturn(admin);

        when(admin.isAdmin()).thenReturn(true);


        when(espacioService.getEspacioById(any())).thenReturn(espacioOptional);
        when(espacioOptional.isEmpty()).thenReturn(false);
        when(espacioOptional.get()).thenReturn(espacio);

        ResponseEntity<Espacio> response = espacioController.cambiarCategoria("idEspacio", TipoEspacio.AULA.getTipoEspacio(), "Bearer tokenAdmin");

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertEquals(espacio, response.getBody());

        verify(espacio, times(1)).cambiarTipoEspacio(any());
    }

    @Test
    public void shouldReturn400WhenNoAdminUserCambiarCategoria() {
        Optional adminOptional = mock(Optional.class);

        when(personaService.getPersonaById("admin")).thenReturn(adminOptional);

        when(adminOptional.isEmpty()).thenReturn(true);

        ResponseEntity<Espacio> response = espacioController.cambiarCategoria("idEspacio", TipoEspacio.AULA.getTipoEspacio(), "Bearer tokenAdmin");

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
    }

    @Test
    public void shouldReturn401WhenUserIsNotAdminCambiarCategoria() {
        Optional adminOptional = mock(Optional.class);

        when(personaService.getPersonaById("admin")).thenReturn(adminOptional);

        when(adminOptional.isEmpty()).thenReturn(false);
        when(adminOptional.get()).thenReturn(admin);

        when(admin.isAdmin()).thenReturn(false);


        ResponseEntity<Espacio> response = espacioController.cambiarCategoria("idEspacio", TipoEspacio.AULA.getTipoEspacio(), "Bearer tokenAdmin");

        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusCode().value());
    }

    @Test
    public void shouldReturn400WhenEspacioIsEmptyCambiarCategoria() {
        Optional adminOptional = mock(Optional.class);
        Optional espacioOptional = mock(Optional.class);

        when(personaService.getPersonaById("admin")).thenReturn(adminOptional);


        when(adminOptional.isEmpty()).thenReturn(false);
        when(adminOptional.get()).thenReturn(admin);

        when(admin.isAdmin()).thenReturn(true);


        when(espacioService.getEspacioById(any())).thenReturn(espacioOptional);
        when(espacioOptional.isEmpty()).thenReturn(true);

        ResponseEntity<Espacio> response = espacioController.cambiarCategoria("idEspacio", TipoEspacio.AULA.getTipoEspacio(), "Bearer tokenAdmin");

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
    }

    @Test
    public void shouldReturnEspacioWhenCambiarHorario() {
        Optional adminOptional = mock(Optional.class);
        Optional espacioOptional = mock(Optional.class);

        when(personaService.getPersonaById("admin")).thenReturn(adminOptional);

        when(adminOptional.isEmpty()).thenReturn(false);
        when(adminOptional.get()).thenReturn(admin);

        when(admin.isAdmin()).thenReturn(true);


        when(espacioService.getEspacioById(any())).thenReturn(espacioOptional);
        when(espacioOptional.isEmpty()).thenReturn(false);
        when(espacioOptional.get()).thenReturn(espacio);

        ResponseEntity<Espacio> response = espacioController.cambiarHorario("idEspacio", new Horario(), "Bearer tokenAdmin");

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertEquals(espacio, response.getBody());

        verify(espacio, times(1)).cambiarHorario(any());
    }

    @Test
    public void shouldReturn400WhenNoAdminUserCambiarHorario() {
        Optional adminOptional = mock(Optional.class);

        when(personaService.getPersonaById("admin")).thenReturn(adminOptional);

        when(adminOptional.isEmpty()).thenReturn(true);

        ResponseEntity<Espacio> response = espacioController.cambiarHorario("idEspacio", new Horario(), "Bearer tokenAdmin");

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
    }

    @Test
    public void shouldReturn401WhenUserIsNotAdminCambiarHorario() {
        Optional adminOptional = mock(Optional.class);

        when(personaService.getPersonaById("admin")).thenReturn(adminOptional);

        when(adminOptional.isEmpty()).thenReturn(false);
        when(adminOptional.get()).thenReturn(admin);

        when(admin.isAdmin()).thenReturn(false);


        ResponseEntity<Espacio> response = espacioController.cambiarHorario("idEspacio", new Horario(), "Bearer tokenAdmin");

        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusCode().value());
    }

    @Test
    public void shouldReturn400WhenEspacioIsEmptyCambiarHorario() {
        Optional adminOptional = mock(Optional.class);
        Optional espacioOptional = mock(Optional.class);

        when(personaService.getPersonaById("admin")).thenReturn(adminOptional);


        when(adminOptional.isEmpty()).thenReturn(false);
        when(adminOptional.get()).thenReturn(admin);

        when(admin.isAdmin()).thenReturn(true);


        when(espacioService.getEspacioById(any())).thenReturn(espacioOptional);
        when(espacioOptional.isEmpty()).thenReturn(true);

        ResponseEntity<Espacio> response = espacioController.cambiarHorario("idEspacio", new Horario(), "Bearer tokenAdmin");

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
    }

    @Test
    public void shouldReturnEspacioWhenCambiarPropietarioEINA() {
        Optional adminOptional = mock(Optional.class);
        Optional espacioOptional = mock(Optional.class);

        when(personaService.getPersonaById("admin")).thenReturn(adminOptional);

        when(adminOptional.isEmpty()).thenReturn(false);
        when(adminOptional.get()).thenReturn(admin);

        when(admin.isAdmin()).thenReturn(true);


        when(espacioService.getEspacioById(any())).thenReturn(espacioOptional);
        when(espacioOptional.isEmpty()).thenReturn(false);
        when(espacioOptional.get()).thenReturn(espacio);

        ResponseEntity<Espacio> response = espacioController.cambiarPropietarioENIA("idEspacio", "Bearer tokenAdmin");

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertEquals(espacio, response.getBody());

        verify(espacio, times(1)).updatePropietario(any());
    }

    @Test
    public void shouldReturn400WhenNoAdminUserCambiarPropietarioEINA() {
        Optional adminOptional = mock(Optional.class);

        when(personaService.getPersonaById("admin")).thenReturn(adminOptional);

        when(adminOptional.isEmpty()).thenReturn(true);

        ResponseEntity<Espacio> response = espacioController.cambiarPropietarioENIA("idEspacio", "Bearer tokenAdmin");

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
    }

    @Test
    public void shouldReturn401WhenUserIsNotAdminCambiarPropietarioEINA() {
        Optional adminOptional = mock(Optional.class);

        when(personaService.getPersonaById("admin")).thenReturn(adminOptional);

        when(adminOptional.isEmpty()).thenReturn(false);
        when(adminOptional.get()).thenReturn(admin);

        when(admin.isAdmin()).thenReturn(false);


        ResponseEntity<Espacio> response = espacioController.cambiarPropietarioENIA("idEspacio", "Bearer tokenAdmin");

        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusCode().value());
    }

    @Test
    public void shouldReturn400WhenEspacioIsEmptyCambiarPropietarioEINA() {
        Optional adminOptional = mock(Optional.class);
        Optional espacioOptional = mock(Optional.class);

        when(personaService.getPersonaById("admin")).thenReturn(adminOptional);


        when(adminOptional.isEmpty()).thenReturn(false);
        when(adminOptional.get()).thenReturn(admin);

        when(admin.isAdmin()).thenReturn(true);


        when(espacioService.getEspacioById(any())).thenReturn(espacioOptional);
        when(espacioOptional.isEmpty()).thenReturn(true);

        ResponseEntity<Espacio> response = espacioController.cambiarPropietarioENIA("idEspacio", "Bearer tokenAdmin");

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
    }

    @Test
    public void shouldReturnEspacioWhenCambiarPropietarioDepartamento() {
        Optional adminOptional = mock(Optional.class);
        Optional espacioOptional = mock(Optional.class);

        when(personaService.getPersonaById("admin")).thenReturn(adminOptional);

        when(adminOptional.isEmpty()).thenReturn(false);
        when(adminOptional.get()).thenReturn(admin);

        when(admin.isAdmin()).thenReturn(true);


        when(espacioService.getEspacioById(any())).thenReturn(espacioOptional);
        when(espacioOptional.isEmpty()).thenReturn(false);
        when(espacioOptional.get()).thenReturn(espacio);

        ResponseEntity<Espacio> response = espacioController.cambiarPropietarioDepartamento("idEspacio", Departamento.DIIS.getDepartamento(), "Bearer tokenAdmin");

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertEquals(espacio, response.getBody());

        verify(espacio, times(1)).updatePropietario(any());
    }

    @Test
    public void shouldReturn400WhenNoAdminUserCambiarPropietarioDepartamento() {
        Optional adminOptional = mock(Optional.class);

        when(personaService.getPersonaById("admin")).thenReturn(adminOptional);

        when(adminOptional.isEmpty()).thenReturn(true);

        ResponseEntity<Espacio> response = espacioController.cambiarPropietarioDepartamento("idEspacio", Departamento.DIIS.getDepartamento(), "Bearer tokenAdmin");

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
    }

    @Test
    public void shouldReturn401WhenUserIsNotAdminCambiarPropietarioDepartamento() {
        Optional adminOptional = mock(Optional.class);

        when(personaService.getPersonaById("admin")).thenReturn(adminOptional);

        when(adminOptional.isEmpty()).thenReturn(false);
        when(adminOptional.get()).thenReturn(admin);

        when(admin.isAdmin()).thenReturn(false);


        ResponseEntity<Espacio> response = espacioController.cambiarPropietarioDepartamento("idEspacio", Departamento.DIIS.getDepartamento(), "Bearer tokenAdmin");

        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusCode().value());
    }

    @Test
    public void shouldReturn400WhenEspacioIsEmptyCambiarPropietarioDepartamento() {
        Optional adminOptional = mock(Optional.class);
        Optional espacioOptional = mock(Optional.class);

        when(personaService.getPersonaById("admin")).thenReturn(adminOptional);


        when(adminOptional.isEmpty()).thenReturn(false);
        when(adminOptional.get()).thenReturn(admin);

        when(admin.isAdmin()).thenReturn(true);


        when(espacioService.getEspacioById(any())).thenReturn(espacioOptional);
        when(espacioOptional.isEmpty()).thenReturn(true);

        ResponseEntity<Espacio> response = espacioController.cambiarPropietarioDepartamento("idEspacio", Departamento.DIIS.getDepartamento(), "Bearer tokenAdmin");

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
    }

    @Test
    public void shouldReturnEspacioWhenCambiarPropietarioPersonas() {
        Optional adminOptional = mock(Optional.class);
        Optional personaOptional = mock(Optional.class);
        Optional espacioOptional = mock(Optional.class);

        when(personaService.getPersonaById("admin")).thenReturn(adminOptional);
        when(personaService.getPersonaById("correo")).thenReturn(personaOptional);
        when(personaOptional.isPresent()).thenReturn(true);
        when(personaOptional.get()).thenReturn(persona);
        when(persona.getRoles()).thenReturn(List.of(Rol.DOCENTE_INVESTIGADOR));

        when(adminOptional.isEmpty()).thenReturn(false);
        when(adminOptional.get()).thenReturn(admin);

        when(admin.isAdmin()).thenReturn(true);

        when(espacioService.getEspacioById(any())).thenReturn(espacioOptional);
        when(espacioOptional.isEmpty()).thenReturn(false);
        when(espacioOptional.get()).thenReturn(espacio);

        ResponseEntity<Espacio> response = espacioController.cambiarPropietarioPersonas("idEspacio", List.of("correo"), "Bearer tokenAdmin");

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertEquals(espacio, response.getBody());

        verify(espacio, times(1)).updatePropietario(any());
    }

    @Test
    public void shouldReturn400WhenNoAdminUserCambiarPropietarioPersonas() {
        Optional adminOptional = mock(Optional.class);

        when(personaService.getPersonaById("admin")).thenReturn(adminOptional);

        when(adminOptional.isEmpty()).thenReturn(true);

        ResponseEntity<Espacio> response = espacioController.cambiarPropietarioPersonas("idEspacio", List.of("correo"), "Bearer tokenAdmin");

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
    }

    @Test
    public void shouldReturn401WhenUserIsNotAdminCambiarPropietarioPersonas() {
        Optional adminOptional = mock(Optional.class);

        when(personaService.getPersonaById("admin")).thenReturn(adminOptional);

        when(adminOptional.isEmpty()).thenReturn(false);
        when(adminOptional.get()).thenReturn(admin);

        when(admin.isAdmin()).thenReturn(false);

        when(tokenParser.extractEmail(any())).thenReturn("admin");

        ResponseEntity<Espacio> response = espacioController.cambiarPropietarioPersonas("idEspacio", List.of("correo"), "Bearer tokenAdmin");

        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusCode().value());
    }

    @Test
    public void shouldReturn400WhenEspacioIsEmptyCambiarPropietarioPersonas() {
        Optional adminOptional = mock(Optional.class);
        Optional espacioOptional = mock(Optional.class);

        when(personaService.getPersonaById("admin")).thenReturn(adminOptional);


        when(adminOptional.isEmpty()).thenReturn(false);
        when(adminOptional.get()).thenReturn(admin);

        when(admin.isAdmin()).thenReturn(true);

        when(tokenParser.extractEmail(any())).thenReturn("admin");

        when(espacioService.getEspacioById(any())).thenReturn(espacioOptional);
        when(espacioOptional.isEmpty()).thenReturn(true);

        ResponseEntity<Espacio> response = espacioController.cambiarPropietarioPersonas("idEspacio", List.of("correo"), "Bearer tokenAdmin");

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
    }

    @Test
    public void shouldReturnEspacioWhenCambiarPorcentaje() {
        Optional adminOptional = mock(Optional.class);
        Optional personaOptional = mock(Optional.class);
        Optional espacioOptional = mock(Optional.class);

        when(personaService.getPersonaById("admin")).thenReturn(adminOptional);
        when(personaService.getPersonaById("correo")).thenReturn(personaOptional);
        when(personaOptional.isEmpty()).thenReturn(false);
        when(personaOptional.get()).thenReturn(persona);

        when(adminOptional.isEmpty()).thenReturn(false);
        when(adminOptional.get()).thenReturn(admin);

        when(admin.isAdmin()).thenReturn(true);

        when(tokenParser.extractEmail(any())).thenReturn("admin");

        when(espacioService.getEspacioById(any())).thenReturn(espacioOptional);
        when(espacioOptional.isEmpty()).thenReturn(false);
        when(espacioOptional.get()).thenReturn(espacio);

        ResponseEntity<Espacio> response = espacioController.cambiarPorcentajeUso("idEspacio", 100, "Bearer tokenAdmin");

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertEquals(espacio, response.getBody());

        verify(espacio, times(1)).cambiarPorcentajeUso(any());
    }

    @Test
    public void shouldReturn400WhenNoAdminUserCambiarPorcentaje() {
        Optional adminOptional = mock(Optional.class);

        when(personaService.getPersonaById("admin")).thenReturn(adminOptional);

        when(adminOptional.isEmpty()).thenReturn(true);

        ResponseEntity<Espacio> response = espacioController.cambiarPorcentajeUso("idEspacio", 100, "Bearer tokenAdmin");

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
    }

    @Test
    public void shouldReturn401WhenUserIsNotAdminCambiarPorcentaje() {
        Optional adminOptional = mock(Optional.class);

        when(personaService.getPersonaById("admin")).thenReturn(adminOptional);

        when(adminOptional.isEmpty()).thenReturn(false);
        when(adminOptional.get()).thenReturn(admin);

        when(admin.isAdmin()).thenReturn(false);

        when(tokenParser.extractEmail(any())).thenReturn("admin");

        ResponseEntity<Espacio> response = espacioController.cambiarPorcentajeUso("idEspacio", 100, "Bearer tokenAdmin");

        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusCode().value());
    }

    @Test
    public void shouldReturn400WhenEspacioIsEmptyCambiarPorcentaje() {
        Optional adminOptional = mock(Optional.class);
        Optional espacioOptional = mock(Optional.class);

        when(personaService.getPersonaById("admin")).thenReturn(adminOptional);


        when(adminOptional.isEmpty()).thenReturn(false);
        when(adminOptional.get()).thenReturn(admin);

        when(admin.isAdmin()).thenReturn(true);

        when(tokenParser.extractEmail(any())).thenReturn("admin");

        when(espacioService.getEspacioById(any())).thenReturn(espacioOptional);
        when(espacioOptional.isEmpty()).thenReturn(true);

        ResponseEntity<Espacio> response = espacioController.cambiarPorcentajeUso("idEspacio", 100, "Bearer tokenAdmin");

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
    }
}