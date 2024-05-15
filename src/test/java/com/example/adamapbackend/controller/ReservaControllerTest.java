package com.example.adamapbackend.controller;


import com.example.adamapbackend.domain.Espacio;
import com.example.adamapbackend.domain.Persona;
import com.example.adamapbackend.domain.Reserva;
import com.example.adamapbackend.domain.enums.Rol;
import com.example.adamapbackend.service.EspacioService;
import com.example.adamapbackend.service.PersonaService;
import com.example.adamapbackend.service.ReservaService;
import com.example.adamapbackend.token.TokenParser;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.WeakHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReservaControllerTest {

    ReservaService reservaService = mock(ReservaService.class);
    EspacioService espacioService = mock(EspacioService.class);
    TokenParser tokenParser = mock(TokenParser.class);
    PersonaService personaService = mock(PersonaService.class);
    Reserva reserva = mock(Reserva.class);
    Persona persona = mock(Persona.class);
    Espacio espacio = mock(Espacio.class);
    ReservaController reservaController = new ReservaController(reservaService, espacioService, tokenParser, personaService);

    @Test
    public void shouldReturnReservaWhenBuscarReservaPorId() {
        Optional reservaOptional = mock(Optional.class);
        when(reservaOptional.isEmpty()).thenReturn(false);
        when(reservaOptional.get()).thenReturn(reserva);

        when(reservaService.getReservaById(any())).thenReturn(reservaOptional);

        ResponseEntity result = reservaController.buscarReservaPorId(UUID.randomUUID().toString());

        assertEquals(reserva, result.getBody());
        assertEquals(HttpStatus.OK.value(), result.getStatusCode().value());

        verify(reservaService, times(1)).getReservaById(any());
    }

    @Test
    public void shouldReturn400WhenBuscarReservaPorId() {
        Optional reservaOptional = mock(Optional.class);
        when(reservaOptional.isEmpty()).thenReturn(true);

        when(reservaService.getReservaById(any())).thenReturn(reservaOptional);

        ResponseEntity result = reservaController.buscarReservaPorId(UUID.randomUUID().toString());

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getStatusCode().value());
    }

    @Test
    public void shouldCreateReserva() {
        Optional espacioOptional = mock(Optional.class);
        Optional personaOptional = mock(Optional.class);

        when(personaService.getPersonaById("correo")).thenReturn(personaOptional);
        when(personaOptional.isEmpty()).thenReturn(false);
        when(personaOptional.get()).thenReturn(persona);

        when(tokenParser.extractEmail(any())).thenReturn("correo");

        when(espacioService.getEspacioById(any())).thenReturn(espacioOptional);
        when(espacioOptional.isPresent()).thenReturn(true);
        when(espacioOptional.get()).thenReturn(espacio);
        doNothing().when(espacio).checkHorario(any(), any(), any());
        when(espacio.esReservablePorElUsuario(any())).thenReturn(true);
        when(espacio.getMaxPersonasParaReserva()).thenReturn(100);

        doNothing().when(reservaService).checkEspacios(any(), any(), any(), any(), any());

        ResponseEntity result = reservaController.buscarEspacios(List.of("idEspacio"), "docencia", 10, "10:00", 120, null, new Date(), "Bearer correo");

        verify(reservaService, times(1)).guardarReserva(any());

        assertEquals(HttpStatus.OK.value(), result.getStatusCode().value());
    }

    @Test
    public void shouldReturn400WhenCreateReservaIfNoParamsOk() {
        ResponseEntity result = reservaController.buscarEspacios(List.of(), null, null, null, null, null, null, null);
        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getStatusCode().value());
    }

    @Test
    public void shouldReturn400WhenCreateReservaIfNoPersona() {
        Optional espacioOptional = mock(Optional.class);
        Optional personaOptional = mock(Optional.class);

        when(personaService.getPersonaById("correo")).thenReturn(personaOptional);
        when(personaOptional.isEmpty()).thenReturn(true);

        when(tokenParser.extractEmail(any())).thenReturn("correo");

        when(espacioService.getEspacioById(any())).thenReturn(espacioOptional);
        when(espacioOptional.isPresent()).thenReturn(true);
        when(espacioOptional.get()).thenReturn(espacio);

        ResponseEntity result = reservaController.buscarEspacios(List.of("idEspacio"), "docencia", 10, "10:00", 120, null, new Date(), "Bearer correo");

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getStatusCode().value());
    }


    @Test
    public void shouldReturnReservasVivas() {
        Optional adminOptional = mock(Optional.class);

        when(personaService.getPersonaById("admin")).thenReturn(adminOptional);

        when(adminOptional.isEmpty()).thenReturn(false);
        when(adminOptional.get()).thenReturn(persona);

        when(persona.isAdmin()).thenReturn(true);

        when(tokenParser.extractEmail(any())).thenReturn("admin");

        when(reservaService.reservasVivas()).thenReturn(List.of(reserva));

        ResponseEntity<List<Reserva>> response = reservaController.verReservasVivas("Bearer tokenAdmin");

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertEquals(List.of(reserva), response.getBody());

        verify(reservaService, times(1)).reservasVivas();
    }

    @Test
    public void shouldReturn400WhenNoAdminUserReservasVivas() {
        Optional adminOptional = mock(Optional.class);

        when(personaService.getPersonaById("admin")).thenReturn(adminOptional);

        when(adminOptional.isEmpty()).thenReturn(true);

        when(tokenParser.extractEmail(any())).thenReturn("admin");

        ResponseEntity<List<Reserva>> response = reservaController.verReservasVivas("Bearer tokenAdmin");

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
    }

    @Test
    public void shouldReturn401WhenUserIsNotAdminReservasVivas() {
        Optional adminOptional = mock(Optional.class);

        when(personaService.getPersonaById("admin")).thenReturn(adminOptional);

        when(adminOptional.isEmpty()).thenReturn(false);
        when(adminOptional.get()).thenReturn(persona);

        when(persona.isAdmin()).thenReturn(false);

        when(tokenParser.extractEmail(any())).thenReturn("admin");

        ResponseEntity<List<Reserva>> response = reservaController.verReservasVivas("Bearer tokenAdmin");

        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusCode().value());
    }

    @Test
    public void shouldEliminarReserva() {
        Optional adminOptional = mock(Optional.class);

        when(personaService.getPersonaById("admin")).thenReturn(adminOptional);

        when(adminOptional.isEmpty()).thenReturn(false);
        when(adminOptional.get()).thenReturn(persona);

        when(persona.isAdmin()).thenReturn(true);

        when(tokenParser.extractEmail(any())).thenReturn("admin");

        ResponseEntity<Reserva> response = reservaController.eliminarReserva(UUID.randomUUID().toString(), "Bearer tokenAdmin");

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());

        verify(reservaService, times(1)).eliminarReserva(any());
    }

    @Test
    public void shouldReturn400WhenNoAdminUserEliminarReserva() {
        Optional adminOptional = mock(Optional.class);

        when(personaService.getPersonaById("admin")).thenReturn(adminOptional);

        when(adminOptional.isEmpty()).thenReturn(true);

        when(tokenParser.extractEmail(any())).thenReturn("admin");

        ResponseEntity<Reserva> response = reservaController.eliminarReserva(UUID.randomUUID().toString(), "Bearer tokenAdmin");

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
    }

    @Test
    public void shouldReturn401WhenUserIsNotAdminEliminarReserva() {
        Optional adminOptional = mock(Optional.class);

        when(personaService.getPersonaById("admin")).thenReturn(adminOptional);

        when(adminOptional.isEmpty()).thenReturn(false);
        when(adminOptional.get()).thenReturn(persona);

        when(persona.isAdmin()).thenReturn(false);

        when(tokenParser.extractEmail(any())).thenReturn("admin");

        ResponseEntity<Reserva> response = reservaController.eliminarReserva(UUID.randomUUID().toString(), "Bearer tokenAdmin");

        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusCode().value());
    }

    @Test
    public void shouldCreateReservaAutomatica() {
        Optional espacioOptional = mock(Optional.class);
        Optional personaOptional = mock(Optional.class);

        when(personaService.getPersonaById("correo")).thenReturn(personaOptional);
        when(personaOptional.isEmpty()).thenReturn(false);
        when(personaOptional.get()).thenReturn(persona);

        when(tokenParser.extractEmail(any())).thenReturn("correo");

        when(reservaService.reservaAutomatica(any(), any(), any(), any(), any(), any(),any())).thenReturn(reserva);

        ResponseEntity result = reservaController.reservaAutomatica("docencia", null, null, null, null, null,"Bearer correo");

        verify(reservaService, times(1)).reservaAutomatica(any(), any(), any(), any(), any(), any(),any());

        assertEquals(HttpStatus.OK.value(), result.getStatusCode().value());
        assertEquals(reserva, result.getBody());
    }

    @Test
    public void shouldReturn400WhenReservaAutomaticaIfNoPersona() {
        Optional espacioOptional = mock(Optional.class);
        Optional personaOptional = mock(Optional.class);

        when(personaService.getPersonaById("correo")).thenReturn(personaOptional);
        when(personaOptional.isEmpty()).thenReturn(true);

        when(tokenParser.extractEmail(any())).thenReturn("correo");

        ResponseEntity result = reservaController.reservaAutomatica("docencia", null, null, null, null, null,"Bearer correo");

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getStatusCode().value());
    }
}