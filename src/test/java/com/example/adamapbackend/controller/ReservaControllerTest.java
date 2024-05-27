package com.example.adamapbackend.controller;


import com.example.adamapbackend.controller.dto.CreateReserva;
import com.example.adamapbackend.domain.Espacio;
import com.example.adamapbackend.domain.Persona;
import com.example.adamapbackend.domain.Reserva;
import com.example.adamapbackend.domain.enums.Rol;
import com.example.adamapbackend.service.EspacioService;
import com.example.adamapbackend.service.PersonaService;
import com.example.adamapbackend.service.ReservaService;
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
    PersonaService personaService = mock(PersonaService.class);
    Reserva reserva = mock(Reserva.class);
    Persona persona = mock(Persona.class);
    Espacio espacio = mock(Espacio.class);
    ReservaController reservaController = new ReservaController(reservaService, espacioService, personaService);

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


        when(espacioService.getEspacioById(any())).thenReturn(espacioOptional);
        when(espacioOptional.isPresent()).thenReturn(true);
        when(espacioOptional.get()).thenReturn(espacio);
        doNothing().when(espacio).checkHorario(any(), any(), any());
        when(espacio.esReservablePorElUsuario(any())).thenReturn(true);
        when(espacio.getMaxPersonasParaReserva()).thenReturn(100);

        doNothing().when(reservaService).checkEspacios(any(), any(), any(), any(), any());

        CreateReserva createReserva = new CreateReserva();
        createReserva.setEspacios(List.of("idEspacio"));
        createReserva.setTipoUso("docencia");
        createReserva.setNumAsistentes(10);
        createReserva.setHoraInicio("10:00");
        createReserva.setDuracion(120);
        createReserva.setDescripcion(null);
        createReserva.setFecha(new Date());

        ResponseEntity result = reservaController.buscarEspacios(createReserva, "Bearer correo");

        verify(reservaService, times(1)).guardarReserva(any());

        assertEquals(HttpStatus.OK.value(), result.getStatusCode().value());
    }

    @Test
    public void shouldReturn400WhenCreateReservaIfNoParamsOk() {
        CreateReserva createReserva = new CreateReserva();
        createReserva.setEspacios(List.of());
        ResponseEntity result = reservaController.buscarEspacios(createReserva, null);
        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getStatusCode().value());
    }

    @Test
    public void shouldReturn400WhenCreateReservaIfNoPersona() {
        Optional espacioOptional = mock(Optional.class);
        Optional personaOptional = mock(Optional.class);

        when(personaService.getPersonaById("correo")).thenReturn(personaOptional);
        when(personaOptional.isEmpty()).thenReturn(true);

        when(espacioService.getEspacioById(any())).thenReturn(espacioOptional);
        when(espacioOptional.isPresent()).thenReturn(true);
        when(espacioOptional.get()).thenReturn(espacio);

        CreateReserva createReserva = new CreateReserva();
        createReserva.setEspacios(List.of("idEspacio"));
        createReserva.setTipoUso("docencia");
        createReserva.setNumAsistentes(10);
        createReserva.setHoraInicio("10:00");
        createReserva.setDuracion(120);
        createReserva.setDescripcion(null);
        createReserva.setFecha(new Date());

        ResponseEntity result = reservaController.buscarEspacios(createReserva, "Bearer correo");

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getStatusCode().value());
    }


    @Test
    public void shouldReturnReservasVivas() {
        Optional adminOptional = mock(Optional.class);

        when(personaService.getPersonaById("admin")).thenReturn(adminOptional);

        when(adminOptional.isEmpty()).thenReturn(false);
        when(adminOptional.get()).thenReturn(persona);

        when(persona.isAdmin()).thenReturn(true);

        when(reservaService.reservasVivas()).thenReturn(List.of(reserva));

        ResponseEntity<List<Reserva>> response = reservaController.verReservasVivas("Bearer admin");

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertEquals(List.of(reserva), response.getBody());

        verify(reservaService, times(1)).reservasVivas();
    }

    @Test
    public void shouldReturn400WhenNoAdminUserReservasVivas() {
        Optional adminOptional = mock(Optional.class);

        when(personaService.getPersonaById("admin")).thenReturn(adminOptional);

        when(adminOptional.isEmpty()).thenReturn(true);

        ResponseEntity<List<Reserva>> response = reservaController.verReservasVivas("Bearer admin");

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
    }

    @Test
    public void shouldReturn401WhenUserIsNotAdminReservasVivas() {
        Optional adminOptional = mock(Optional.class);

        when(personaService.getPersonaById("admin")).thenReturn(adminOptional);

        when(adminOptional.isEmpty()).thenReturn(false);
        when(adminOptional.get()).thenReturn(persona);

        when(persona.isAdmin()).thenReturn(false);

        ResponseEntity<List<Reserva>> response = reservaController.verReservasVivas("Bearer admin");

        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusCode().value());
    }

    @Test
    public void shouldEliminarReserva() {
        Optional adminOptional = mock(Optional.class);
        Optional reservaOptional = mock(Optional.class);

        when(personaService.getPersonaById("admin")).thenReturn(adminOptional);

        when(adminOptional.isEmpty()).thenReturn(false);
        when(adminOptional.get()).thenReturn(persona);

        when(reservaService.getReservaById(any())).thenReturn(reservaOptional);

        when(reservaOptional.isEmpty()).thenReturn(false);
        when(reservaOptional.get()).thenReturn(reserva);

        when(reserva.getPersona()).thenReturn(persona);

        when(persona.isAdmin()).thenReturn(true);

        ResponseEntity<Reserva> response = reservaController.eliminarReserva(UUID.randomUUID().toString(), "Bearer admin");

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());

        verify(reservaService, times(1)).eliminarReserva(any());
    }

    @Test
    public void shouldReturn400WhenNoAdminUserEliminarReserva() {
        Optional adminOptional = mock(Optional.class);

        when(personaService.getPersonaById("admin")).thenReturn(adminOptional);

        when(adminOptional.isEmpty()).thenReturn(true);

        ResponseEntity<Reserva> response = reservaController.eliminarReserva(UUID.randomUUID().toString(), "Bearer admin");

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
    }

    @Test
    public void shouldReturn401WhenUserIsNotAdminEliminarReserva() {
        Optional adminOptional = mock(Optional.class);

        when(personaService.getPersonaById("admin")).thenReturn(adminOptional);

        when(adminOptional.isEmpty()).thenReturn(false);
        when(adminOptional.get()).thenReturn(persona);

        when(persona.isAdmin()).thenReturn(false);

        ResponseEntity<Reserva> response = reservaController.eliminarReserva(UUID.randomUUID().toString(), "Bearer admin");

        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusCode().value());
    }

    @Test
    public void shouldCreateReservaAutomatica() {
        Optional espacioOptional = mock(Optional.class);
        Optional personaOptional = mock(Optional.class);

        when(personaService.getPersonaById("correo")).thenReturn(personaOptional);
        when(personaOptional.isEmpty()).thenReturn(false);
        when(personaOptional.get()).thenReturn(persona);

        when(reservaService.reservaAutomatica(any(), any(), any(), any(), any(), any(),any())).thenReturn(reserva);

        CreateReserva createReserva = new CreateReserva();
        createReserva.setTipoUso("docencia");

        ResponseEntity result = reservaController.reservaAutomatica(createReserva,"Bearer correo");

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

        CreateReserva createReserva = new CreateReserva();
        createReserva.setTipoUso("docencia");

        ResponseEntity result = reservaController.reservaAutomatica(createReserva,"Bearer correo");

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getStatusCode().value());
    }
}