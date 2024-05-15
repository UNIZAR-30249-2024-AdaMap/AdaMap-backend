package com.example.adamapbackend.domain;

import com.example.adamapbackend.domain.enums.Departamento;
import com.example.adamapbackend.domain.enums.Rol;
import com.example.adamapbackend.domain.enums.TipoEspacio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class EspacioTest {
    Persona persona = mock(Persona.class);
    PropietarioEspacio propietarioEspacio = mock(PropietarioEspacio.class);
    Horario horario = mock(Horario.class);

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

    @ParameterizedTest
    @MethodSource("espaciosDocenteInvestigadorGerente")
    public void shouldBeReservablePorDocente(TipoEspacio tipoEspacio){
        when(persona.getRoles()).thenReturn(List.of(Rol.DOCENTE_INVESTIGADOR));
        when(persona.getDepartamento()).thenReturn(Departamento.DIIS);

        if (!tipoEspacio.equals(TipoEspacio.DESPACHO) && !tipoEspacio.equals(TipoEspacio.LABORATORIO))
            when(propietarioEspacio.isEINA()).thenReturn(true);
        else
            when(propietarioEspacio.isDepartamento()).thenReturn(true);

        when(propietarioEspacio.getPropietario()).thenReturn(List.of(Departamento.DIIS.getDepartamento()));

        Espacio espacio = new Espacio();
        espacio.cambiarTipoEspacio(tipoEspacio);
        espacio.updatePropietario(propietarioEspacio);
        espacio.cambiarReservabilidad();
        assertTrue(espacio.esReservablePorElUsuario(persona));
    }

    @ParameterizedTest
    @MethodSource("espaciosNoDocenteInvestigador")
    public void shouldNotBeReservablePorDocenteNoDepartamento(TipoEspacio tipoEspacio){
        when(persona.getRoles()).thenReturn(List.of(Rol.DOCENTE_INVESTIGADOR));
        when(propietarioEspacio.isDepartamento()).thenReturn(false);

        Espacio espacio = new Espacio();
        espacio.cambiarTipoEspacio(tipoEspacio);
        espacio.updatePropietario(propietarioEspacio);
        espacio.cambiarReservabilidad();
        assertFalse(espacio.esReservablePorElUsuario(persona));
    }

    @ParameterizedTest
    @MethodSource("espaciosNoDocenteInvestigador")
    public void shouldNotBeReservablePorDocenteNoMismoDepartamento(TipoEspacio tipoEspacio){
        when(persona.getRoles()).thenReturn(List.of(Rol.DOCENTE_INVESTIGADOR));
        when(persona.getDepartamento()).thenReturn(Departamento.DIIS);
        when(propietarioEspacio.isDepartamento()).thenReturn(true);
        when(propietarioEspacio.getPropietario()).thenReturn(List.of(Departamento.DIEC.getDepartamento()));

        Espacio espacio = new Espacio();
        espacio.cambiarTipoEspacio(tipoEspacio);
        espacio.updatePropietario(propietarioEspacio);
        espacio.cambiarReservabilidad();
        assertFalse(espacio.esReservablePorElUsuario(persona));
    }

    @ParameterizedTest
    @MethodSource("espaciosDocenteInvestigadorGerente")
    public void shouldBeReservablePorInvestigador(TipoEspacio tipoEspacio){
        when(persona.getRoles()).thenReturn(List.of(Rol.INVESTIGADOR_CONTRATADO));
        when(persona.getDepartamento()).thenReturn(Departamento.DIIS);

        if (!tipoEspacio.equals(TipoEspacio.DESPACHO) && !tipoEspacio.equals(TipoEspacio.LABORATORIO))
            when(propietarioEspacio.isEINA()).thenReturn(true);
        else
            when(propietarioEspacio.isDepartamento()).thenReturn(true);

        when(propietarioEspacio.getPropietario()).thenReturn(List.of(Departamento.DIIS.getDepartamento()));

        Espacio espacio = new Espacio();
        espacio.cambiarTipoEspacio(tipoEspacio);
        espacio.updatePropietario(propietarioEspacio);
        espacio.cambiarReservabilidad();
        assertTrue(espacio.esReservablePorElUsuario(persona));
    }

    @ParameterizedTest
    @MethodSource("espaciosNoDocenteInvestigador")
    public void shouldNotBeReservablePorInvestigadorNoDepartamento(TipoEspacio tipoEspacio){
        when(persona.getRoles()).thenReturn(List.of(Rol.INVESTIGADOR_CONTRATADO));
        when(propietarioEspacio.isDepartamento()).thenReturn(false);

        Espacio espacio = new Espacio();
        espacio.cambiarTipoEspacio(tipoEspacio);
        espacio.updatePropietario(propietarioEspacio);
        espacio.cambiarReservabilidad();
        assertFalse(espacio.esReservablePorElUsuario(persona));
    }

    @ParameterizedTest
    @MethodSource("espaciosNoDocenteInvestigador")
    public void shouldNotBeReservablePorInvestigadorNoMismoDepartamento(TipoEspacio tipoEspacio){
        when(persona.getRoles()).thenReturn(List.of(Rol.INVESTIGADOR_CONTRATADO));
        when(persona.getDepartamento()).thenReturn(Departamento.DIIS);
        when(propietarioEspacio.isDepartamento()).thenReturn(true);
        when(propietarioEspacio.getPropietario()).thenReturn(List.of(Departamento.DIEC.getDepartamento()));

        Espacio espacio = new Espacio();
        espacio.cambiarTipoEspacio(tipoEspacio);
        espacio.updatePropietario(propietarioEspacio);
        espacio.cambiarReservabilidad();
        assertFalse(espacio.esReservablePorElUsuario(persona));
    }

    @ParameterizedTest
    @MethodSource("espaciosDocenteInvestigadorGerente")
    public void shouldBeReservablePorGerente(TipoEspacio tipoEspacio) {
        when(persona.getRoles()).thenReturn(List.of(Rol.GERENTE));

        Espacio espacio = new Espacio();
        espacio.cambiarTipoEspacio(tipoEspacio);
        espacio.cambiarReservabilidad();
        assertTrue(espacio.esReservablePorElUsuario(persona));
    }

    @Test
    public void shouldBeDisponibleHorario() {
        when(horario.getByDay(any())).thenReturn("08:00-20:00");

        Espacio espacio = new Espacio();
        espacio.cambiarHorario(horario);
        assertTrue(espacio.isHorarioDisponible("10:00", 120, new Date()));

    }

    @ParameterizedTest
    @MethodSource("horariosFail")
    public void shouldNotBeDisponibleHorario(String horarioValue) {
        when(horario.getByDay(any())).thenReturn(horarioValue);

        Espacio espacio = new Espacio();
        espacio.cambiarHorario(horario);
        assertFalse(espacio.isHorarioDisponible("10:00", 120, new Date()));
    }

    @Test
    public void shouldCheckHorario() {
        when(horario.getByDay(any())).thenReturn("08:00-20:00");

        Espacio espacio = new Espacio();
        espacio.cambiarHorario(horario);
        assertDoesNotThrow(() -> espacio.checkHorario("10:00", 120, new Date()));

    }

    @ParameterizedTest
    @MethodSource("horariosFail")
    public void shouldThrowExceptionWhenCheckHorario(String horarioValue) {
        when(horario.getByDay(any())).thenReturn(horarioValue);

        Espacio espacio = new Espacio();
        espacio.cambiarHorario(horario);
        assertThrows(IllegalArgumentException.class, () -> espacio.checkHorario("10:00", 120, new Date()));
    }

    @Test
    public void shouldUpdatePropietarioAulaSalaComun() {
        when(propietarioEspacio.isEINA()).thenReturn(true);

        Espacio aula = new Espacio();
        Espacio salaComun = new Espacio();
        aula.cambiarTipoEspacio(TipoEspacio.AULA);
        salaComun.cambiarTipoEspacio(TipoEspacio.SALA_COMUN);
        assertDoesNotThrow(() -> {
            aula.updatePropietario(propietarioEspacio);
            salaComun.updatePropietario(propietarioEspacio);
        });
    }

    @Test
    public void shouldThrowExceptionWhenUpdatePropietarioAulaSalaComun() {
        when(propietarioEspacio.isEINA()).thenReturn(false);

        Espacio aula = new Espacio();
        Espacio salaComun = new Espacio();
        aula.cambiarTipoEspacio(TipoEspacio.AULA);
        salaComun.cambiarTipoEspacio(TipoEspacio.SALA_COMUN);
        assertThrows(IllegalArgumentException.class, () -> {
            aula.updatePropietario(propietarioEspacio);
            salaComun.updatePropietario(propietarioEspacio);
        });
    }

    @Test
    public void shouldUpdatePropietarioSeminarioLaboratorio() {
        when(propietarioEspacio.isPersonas()).thenReturn(false);

        Espacio seminario = new Espacio();
        Espacio laboratorio = new Espacio();
        seminario.cambiarTipoEspacio(TipoEspacio.SEMINARIO);
        laboratorio.cambiarTipoEspacio(TipoEspacio.LABORATORIO);
        assertDoesNotThrow(() -> {
            seminario.updatePropietario(propietarioEspacio);
            laboratorio.updatePropietario(propietarioEspacio);
        });
    }

    @Test
    public void shouldThrowExceptionWhenUpdatePropietarioSeminarioLaboratorio() {
        when(propietarioEspacio.isPersonas()).thenReturn(true);

        Espacio seminario = new Espacio();
        Espacio laboratorio = new Espacio();
        seminario.cambiarTipoEspacio(TipoEspacio.SEMINARIO);
        laboratorio.cambiarTipoEspacio(TipoEspacio.LABORATORIO);
        assertThrows(IllegalArgumentException.class, () -> {
            seminario.updatePropietario(propietarioEspacio);
            laboratorio.updatePropietario(propietarioEspacio);
        });
    }

    @Test
    public void shouldUpdatePropietarioDespacho() {
        when(propietarioEspacio.isEINA()).thenReturn(false);

        Espacio despacho = new Espacio();
        despacho.cambiarTipoEspacio(TipoEspacio.DESPACHO);
        assertDoesNotThrow(() -> despacho.updatePropietario(propietarioEspacio));
    }

    @Test
    public void shouldThrowExceptionWhenUpdatePropietarioDespacho() {
        when(propietarioEspacio.isEINA()).thenReturn(true);

        Espacio despacho = new Espacio();
        despacho.cambiarTipoEspacio(TipoEspacio.DESPACHO);
        assertThrows(IllegalArgumentException.class, () -> despacho.updatePropietario(propietarioEspacio));
    }

    @Test
    public void shouldCreateEspacio() {
        when(propietarioEspacio.isEINA()).thenReturn(true);
        assertDoesNotThrow(() -> new Espacio(TipoEspacio.AULA, 10, true, 100D, horario, propietarioEspacio, 100));
    }

    @Test
    public void shouldThrowExceptionWhenTipoEspacioNullAtCreateEspacio() {
        assertThrows(IllegalArgumentException.class, () -> new Espacio(null, 10, true, 100D, horario, propietarioEspacio, 100));
    }

    @Test
    public void shouldThrowExceptionWhenTamanoLessThanZeroAtCreateEspacio() {
        assertThrows(IllegalArgumentException.class, () -> new Espacio(TipoEspacio.AULA, 10, true, 0D, horario, propietarioEspacio, 100));
    }

    @Test
    public void shouldThrowExceptionWhenPorcentajeUsoLessThanZeroAtCreateEspacio() {
        assertThrows(IllegalArgumentException.class, () -> new Espacio(TipoEspacio.AULA, 10, true, 100D, horario, propietarioEspacio, 0));
    }

    @Test
    public void shouldThrowExceptionWhenDespachoIsReservableAtCreateEspacio() {
        when(propietarioEspacio.isPersonas()).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> new Espacio(TipoEspacio.DESPACHO, 10, true, 100D, horario, propietarioEspacio, 0));
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

    public static Stream<Arguments> espaciosDocenteInvestigadorGerente() {
        return Stream.of(
                Arguments.of(TipoEspacio.SALA_COMUN),
                Arguments.of(TipoEspacio.AULA),
                Arguments.of(TipoEspacio.LABORATORIO),
                Arguments.of(TipoEspacio.SEMINARIO),
                Arguments.of(TipoEspacio.DESPACHO)
        );
    }

    public static Stream<Arguments> espaciosNoDocenteInvestigador() {
        return Stream.of(
                Arguments.of(TipoEspacio.LABORATORIO),
                Arguments.of(TipoEspacio.DESPACHO)
        );
    }

    public static Stream<Arguments> horariosFail() {
        return Stream.of(
                Arguments.of("06:00-08:00"),
                Arguments.of("12:00-14:00"),
                Arguments.of("09:00-11:00")
        );
    }
}