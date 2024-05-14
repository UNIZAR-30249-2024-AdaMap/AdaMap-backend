package com.example.adamapbackend.service;

import com.example.adamapbackend.domain.Espacio;
import com.example.adamapbackend.domain.Reserva;
import com.example.adamapbackend.domain.repositories.ReservaRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReservaServiceTest {
    private static ReservaRepository reservaRepository = mock(ReservaRepository.class);
    private static EspacioService espacioService = mock(EspacioService.class);
    private static ReservaService reservaService = new ReservaService(reservaRepository, espacioService);
    private static Reserva reservaNoCoincide = mock(Reserva.class);
    private static Reserva reservaCoincide = mock(Reserva.class);
    private static Reserva reservaNoViva = mock(Reserva.class);
    private static Reserva reservaViva1 = mock(Reserva.class);
    private static Reserva reservaViva2 = mock(Reserva.class);
    private static Espacio espacioReservaCoincide = mock(Espacio.class);
    private static Espacio espacioAReservar = mock(Espacio.class);

    @Test
    public void shouldWorksWhenCheckEspacios() {
        when(reservaNoCoincide.getFecha()).thenReturn(new Date());
        when(reservaNoCoincide.getHoraInicio()).thenReturn("10:00");
        when(reservaNoCoincide.getDuracion()).thenReturn(120);
        when(reservaNoCoincide.getEspacios()).thenReturn(List.of(espacioAReservar));

        when(reservaCoincide.getFecha()).thenReturn(new Date());
        when(reservaCoincide.getHoraInicio()).thenReturn("16:00");
        when(reservaCoincide.getDuracion()).thenReturn(120);
        when(reservaCoincide.getEspacios()).thenReturn(List.of(espacioReservaCoincide));

        when(reservaRepository.findAll()).thenReturn(List.of(reservaNoCoincide, reservaCoincide));

        assertDoesNotThrow(() -> reservaService.checkEspacios(List.of(espacioAReservar), new Date(), "15:00", 120));
    }

    @Test
    public void shouldThrowExceptionWhenCheckEspacios() {
        when(reservaNoCoincide.getFecha()).thenReturn(new Date());
        when(reservaNoCoincide.getHoraInicio()).thenReturn("10:00");
        when(reservaNoCoincide.getDuracion()).thenReturn(120);
        when(reservaNoCoincide.getEspacios()).thenReturn(List.of(espacioAReservar));

        when(reservaCoincide.getFecha()).thenReturn(new Date());
        when(reservaCoincide.getHoraInicio()).thenReturn("16:00");
        when(reservaCoincide.getDuracion()).thenReturn(120);
        when(reservaCoincide.getEspacios()).thenReturn(List.of(espacioAReservar));

        when(reservaRepository.findAll()).thenReturn(List.of(reservaNoCoincide, reservaCoincide));

        assertThrows(IllegalArgumentException.class, () -> reservaService.checkEspacios(List.of(espacioAReservar), new Date(), "15:00", 120));
    }

    @Test
    public void shouldReturnReservasVivas() {
        when(reservaViva1.getFecha()).thenReturn(Date.from(LocalDate.now().plusDays(2).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        when(reservaViva2.getFecha()).thenReturn(new Date());
        when(reservaViva2.getHoraInicio()).thenReturn("23:59");
        when(reservaViva2.getDuracion()).thenReturn(1);

        when(reservaNoViva.getFecha()).thenReturn(Date.from(LocalDate.now().plusDays(-2).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        when(reservaRepository.findAll()).thenReturn(List.of(reservaNoViva, reservaViva1, reservaViva2));

        List<Reserva> result = reservaService.reservasVivas();

        assertEquals(List.of(reservaViva1, reservaViva2), result);


    }

    @Test
    public void shouldReturnEmptyListWhenNoReservasVivas() {
        when(reservaNoViva.getFecha()).thenReturn(Date.from(LocalDate.now().plusDays(-2).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        when(reservaRepository.findAll()).thenReturn(List.of(reservaNoViva));

        List<Reserva> result = reservaService.reservasVivas();

        assertEquals(List.of(), result);
    }

    @Test
    public void shouldDeleteReservaWhenUpdateReservasPorPorcentajeUso() {
        when(reservaViva1.getFecha()).thenReturn(Date.from(LocalDate.now().plusDays(2).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        when(reservaViva1.getHoraInicio()).thenReturn("10:00");
        when(reservaViva1.getEspacios()).thenReturn(List.of(espacioAReservar));
        when(reservaViva1.checkCapacidad()).thenReturn(false);
        when(reservaViva1.getIdReserva()).thenReturn(UUID.randomUUID());

        when(reservaRepository.findAll()).thenReturn(List.of(reservaViva1));

        reservaService.updateReservasPorPorcentajeEspacios(espacioAReservar);

        verify(reservaRepository, times(1)).deleteById(any());
        reset(reservaRepository);
    }

    @Test
    public void shouldNotDeleteReservaWhenUpdateReservasPorPorcentajeUso() {
        when(reservaViva1.getFecha()).thenReturn(Date.from(LocalDate.now().plusDays(2).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        when(reservaViva1.getHoraInicio()).thenReturn("10:00");
        when(reservaViva1.getEspacios()).thenReturn(List.of(espacioReservaCoincide));
        when(reservaViva1.checkCapacidad()).thenReturn(false);
        when(reservaViva1.getIdReserva()).thenReturn(UUID.randomUUID());

        when(reservaRepository.findAll()).thenReturn(List.of(reservaViva1));

        reservaService.updateReservasPorPorcentajeEspacios(espacioAReservar);

        verify(reservaRepository, never()).deleteById(any());
    }
}