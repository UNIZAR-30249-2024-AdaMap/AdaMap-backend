package com.example.adamapbackend.service;

import com.example.adamapbackend.domain.Espacio;
import com.example.adamapbackend.domain.enums.TipoEspacio;
import com.example.adamapbackend.domain.repositories.EspacioRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EspacioServiceTest {
    private static Espacio espacioCategoria = mock(Espacio.class);
    private static Espacio espacioPlanta = mock(Espacio.class);
    private static Espacio espacioMaxOcupantes = mock(Espacio.class);
    private static Espacio espacio = mock(Espacio.class);

    private static EspacioRepository espacioRepository = mock(EspacioRepository.class);
    private static List<Espacio> espacioList;

    private static EspacioService espacioService;

    @BeforeAll
    public static void setUp() {
        when(espacioCategoria.getTipoEspacioParaReserva()).thenReturn(TipoEspacio.AULA);
        when(espacioPlanta.getPlanta()).thenReturn(2);
        when(espacioMaxOcupantes.getMaxPersonasParaReserva()).thenReturn(20);

        when(espacio.getTipoEspacioParaReserva()).thenReturn(TipoEspacio.LABORATORIO);
        when(espacio.getPlanta()).thenReturn(1);
        when(espacio.getMaxPersonasParaReserva()).thenReturn(50);

        espacioList = List.of(espacio, espacioCategoria, espacioPlanta, espacioMaxOcupantes);

        when(espacioRepository.findAll()).thenReturn(espacioList);

        espacioService = new EspacioService(espacioRepository);
    }

    @Test
    public void shouldReturnAllEspaciosWhenNoFilters() {
        List<Espacio> result = espacioService.getEspacios(null, null, null);

        assertEquals(espacioList, result);
    }

    @Test
    public void shouldReturnEspacioCategoria() {
        List<Espacio> result = espacioService.getEspacios(null, TipoEspacio.AULA, null);

        assertEquals(List.of(espacioCategoria), result);
    }

    @Test
    public void shouldReturnEspacioPlanta() {
        List<Espacio> result = espacioService.getEspacios(2, null, null);

        assertEquals(List.of(espacioPlanta), result);
    }

    @Test
    public void shouldReturnEspacioMaxOcupantes() {
        when(espacio.getMaxPersonasParaReserva()).thenReturn(10);
        List<Espacio> result = espacioService.getEspacios(null, null, 15);

        assertEquals(List.of(espacioMaxOcupantes), result);
    }
    @Test
    public void shouldReturnEspacio() {
        List<Espacio> result = espacioService.getEspacios(1, TipoEspacio.LABORATORIO, 40);

        assertEquals(List.of(espacio), result);
    }
}