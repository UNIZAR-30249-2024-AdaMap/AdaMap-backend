package com.example.adamapbackend.domain;

import com.example.adamapbackend.domain.enums.Departamento;
import com.example.adamapbackend.domain.enums.Rol;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PersonaTest {

    @Test
    public void shouldCreatePersona() {
        assertDoesNotThrow(() -> new Persona("admin@unizar.es", "Administrador", Departamento.DIIS, List.of(Rol.GERENTE, Rol.DOCENTE_INVESTIGADOR)));
    }

    @Test
    public void shouldThrowExceptionWhenRolesIsEmpty(){
        assertThrows(IllegalArgumentException.class, () -> new Persona(null, null, null, List.of()));
    }

    @Test
    public void shouldThrowExceptionWhenRolesIsNull(){
        assertThrows(IllegalArgumentException.class, () -> new Persona(null, null, null, null));
    }

    @ParameterizedTest
    @MethodSource("addRolThrowsInputs")
    public void shouldThrowAtAddRol(List<Rol> roles) {
        assertThrows(IllegalArgumentException.class, () -> {
            Persona persona = new Persona();
            roles.forEach(persona::addRol);
        });
    }

    @ParameterizedTest
    @MethodSource("addRolInputs")
    public void shouldAddRol(List<Rol> roles) {
        Persona persona = new Persona();
        roles.forEach(persona::addRol);

        assertEquals(roles, persona.getRoles());
    }

    @ParameterizedTest
    @MethodSource("updateDepartamentoThrowsInputs")
    public void shouldThrowAtUpdateDepartamento(Persona persona, Departamento departamento, List<Rol> roles) {
        assertThrows(IllegalArgumentException.class, () -> {
            roles.forEach(persona::addRol);
            persona.updateDepartamento(departamento);
        });
    }

    @ParameterizedTest
    @MethodSource("updateDepartamentoInputs")
    public void shouldUpdateDepartamento(Persona persona) {
        persona.updateDepartamento(Departamento.DIIS);

        assertEquals(persona.getDepartamento(), Departamento.DIIS);
    }

    @Test
    public void shouldReturnTrueIfGerente() {
        Persona persona = new Persona();
        persona.addRol(Rol.GERENTE);
        assertTrue(persona.isAdmin());
    }

    @Test
    public void shouldReturnFalseIfNotGerente() {
        Persona persona = new Persona();
        persona.addRol(Rol.DOCENTE_INVESTIGADOR);
        assertFalse(persona.isAdmin());
    }

    public static Stream<Arguments> addRolThrowsInputs() {
        return Stream.of(
                Arguments.of(List.of(Rol.GERENTE, Rol.DOCENTE_INVESTIGADOR, Rol.CONSERJE)),
                Arguments.of(List.of(Rol.CONSERJE, Rol.DOCENTE_INVESTIGADOR))
        );
    }

    public static Stream<Arguments> addRolInputs() {
        Stream<Arguments> dosRoles = Stream.of(
                Arguments.of(List.of(Rol.GERENTE, Rol.DOCENTE_INVESTIGADOR)),
                Arguments.of(List.of(Rol.DOCENTE_INVESTIGADOR, Rol.GERENTE))
        );
        Stream<Arguments> unRol = Arrays.stream(Rol.values()).map(rol -> Arguments.of(List.of(rol)));

        return Stream.concat(unRol, dosRoles);
    }

    public static Stream<Arguments> updateDepartamentoThrowsInputs() {
        return Stream.of(
                Arguments.of(new Persona(), Departamento.DIIS, List.of(Rol.GERENTE)),
                Arguments.of(new Persona(), Departamento.DIIS, List.of(Rol.CONSERJE)),
                Arguments.of(new Persona(), Departamento.DIIS, List.of(Rol.ESTUDIANTE)),
                Arguments.of(new Persona(), null, List.of(Rol.INVESTIGADOR_CONTRATADO)),
                Arguments.of(new Persona(), null, List.of(Rol.DOCENTE_INVESTIGADOR)),
                Arguments.of(new Persona(), null, List.of(Rol.TECNICO_LABORATORIO)),
                Arguments.of(new Persona(), null, List.of(Rol.GERENTE, Rol.DOCENTE_INVESTIGADOR))
        );
    }

    public static Stream<Arguments> updateDepartamentoInputs() {
        return Stream.of(
                Arguments.of(new Persona(null, null, Departamento.DIIS, List.of(Rol.GERENTE, Rol.DOCENTE_INVESTIGADOR))),
                Arguments.of(new Persona(null, null, Departamento.DIIS, List.of(Rol.DOCENTE_INVESTIGADOR))),
                Arguments.of(new Persona(null, null, Departamento.DIIS, List.of(Rol.INVESTIGADOR_CONTRATADO))),
                Arguments.of(new Persona(null, null, Departamento.DIIS, List.of(Rol.TECNICO_LABORATORIO)))
        );
    }
}