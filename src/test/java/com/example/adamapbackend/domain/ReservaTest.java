package com.example.adamapbackend.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ReservaTest {
    Espacio espacio = mock(Espacio.class);

    @Test
    public void shouldCreateReserva() {
        doNothing().when(espacio).checkHorario(any(), any(), any());
        when(espacio.esReservablePorElUsuario(any())).thenReturn(true);
        when(espacio.getMaxPersonasParaReserva()).thenReturn(100);
        assertDoesNotThrow(() -> new Reserva(List.of(espacio),10, null, null, null, null, null, null));
    }

    @Test
    public void shouldNotCreateReservaWhenEspaciosIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> new Reserva(List.of(), null, null, null, null, null, null, null));
    }

    @Test
    public void shouldNotCreateReservaWhenEspaciosIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new Reserva(null, null, null, null, null, null, null, null));
    }

    @Test
    public void shouldNotCreateReservaWhenEspacioIsNotAvailable() {
        doThrow(IllegalArgumentException.class).when(espacio).checkHorario(any(), any(), any());
        assertThrows(IllegalArgumentException.class, () -> new Reserva(List.of(espacio),null, null, null, null, null, null, null));
    }

    @Test
    public void shouldNotCreateReservaWhenEspacioIsNotReservable() {
        when(espacio.esReservablePorElUsuario(any())).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> new Reserva(List.of(espacio), null, null, null, null, null, null, null));
    }

    @Test
    public void shouldNotCreateReservaWhenCheckCapacidadReturnsFalse() {
        when(espacio.esReservablePorElUsuario(any())).thenReturn(true);
        when(espacio.getMaxPersonasParaReserva()).thenReturn(10);
        assertThrows(IllegalArgumentException.class, () -> new Reserva(List.of(espacio), 100, null, null, null, null, null, null));

    }
}