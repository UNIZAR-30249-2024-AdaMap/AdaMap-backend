package com.example.adamapbackend.domain;

import com.example.adamapbackend.domain.enums.Departamento;
import com.example.adamapbackend.domain.enums.Rol;
import com.example.adamapbackend.domain.enums.TipoEspacio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class EspacioTest {
    Persona persona = mock(Persona.class);
    PropietarioEspacio propietarioEspacio = mock(PropietarioEspacio.class);
    Horario horario = mock(Horario.class);
    TipoEspacio tipoEspacio = mock(TipoEspacio.class);

    @Test
    public void shouldBeReservablePorEstudiante(){
        when(persona.getRoles()).thenReturn(List.of(Rol.ESTUDIANTE));
        when(propietarioEspacio.isEINA()).thenReturn(true);

        Espacio espacio = new Espacio();
        espacio.cambiarTipoEspacio(TipoEspacio.SALA_COMUN);
        espacio.cambiarReservabilidad();
        assertTrue(espacio.esReservablePorElUsuario(persona));
    }

    @ParameterizedTest
    @MethodSource("espaciosNoEstudiante")
    public void shouldNotBeReservablePorEstudiante(TipoEspacio tipoEspacio, boolean isEina){
        when(persona.getRoles()).thenReturn(List.of(Rol.ESTUDIANTE));
        when(propietarioEspacio.isEINA()).thenReturn(isEina);

        Espacio espacio = new Espacio();
        espacio.cambiarTipoEspacio(tipoEspacio);
        espacio.cambiarReservabilidad();
        assertFalse(espacio.esReservablePorElUsuario(persona));
    }

    @ParameterizedTest
    @MethodSource("espaciosConserjeTecnico")
    public void shouldBeReservablePorConserje(TipoEspacio tipoEspacio){
        when(persona.getRoles()).thenReturn(List.of(Rol.CONSERJE));
        when(propietarioEspacio.isEINA()).thenReturn(true);

        Espacio espacio = new Espacio();
        espacio.cambiarTipoEspacio(tipoEspacio);
        espacio.cambiarReservabilidad();
        assertTrue(espacio.esReservablePorElUsuario(persona));
    }

    @Test
    public void shouldNotBeReservablePorConserje(){
        when(persona.getRoles()).thenReturn(List.of(Rol.CONSERJE));
        when(propietarioEspacio.isEINA()).thenReturn(false);

        Espacio espacio = new Espacio();
        espacio.cambiarTipoEspacio(TipoEspacio.DESPACHO);
        espacio.cambiarReservabilidad();
        assertFalse(espacio.esReservablePorElUsuario(persona));
    }

    @ParameterizedTest
    @MethodSource("espaciosConserjeTecnico")
    public void shouldBeReservablePorTecnico(TipoEspacio tipoEspacio){
        when(persona.getRoles()).thenReturn(List.of(Rol.TECNICO_LABORATORIO));
        when(propietarioEspacio.isEINA()).thenReturn(true);

        Espacio espacio = new Espacio();
        espacio.cambiarTipoEspacio(tipoEspacio);
        espacio.cambiarReservabilidad();
        assertTrue(espacio.esReservablePorElUsuario(persona));
    }

    @Test
    public void shouldNotBeReservablePorTecnico(){
        when(persona.getRoles()).thenReturn(List.of(Rol.TECNICO_LABORATORIO));
        when(propietarioEspacio.isEINA()).thenReturn(false);

        Espacio espacio = new Espacio();
        espacio.cambiarTipoEspacio(TipoEspacio.DESPACHO);
        espacio.cambiarReservabilidad();
        assertFalse(espacio.esReservablePorElUsuario(persona));
    }

    public static Stream<Arguments> espaciosNoEstudiante() {
        return Stream.of(
                Arguments.of(TipoEspacio.DESPACHO, false),
                Arguments.of(TipoEspacio.AULA, true),
                Arguments.of(TipoEspacio.LABORATORIO, true),
                Arguments.of(TipoEspacio.SEMINARIO, true)
        );
    }

    public static Stream<Arguments> espaciosConserjeTecnico() {
        return Stream.of(
                Arguments.of(TipoEspacio.SALA_COMUN),
                Arguments.of(TipoEspacio.AULA),
                Arguments.of(TipoEspacio.LABORATORIO),
                Arguments.of(TipoEspacio.SEMINARIO)
        );
    }
}