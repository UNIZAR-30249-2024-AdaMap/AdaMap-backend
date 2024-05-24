package com.example.adamapbackend.domain;


import com.example.adamapbackend.domain.enums.Departamento;
import com.example.adamapbackend.domain.enums.Rol;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PropietarioEspacioTest {
    Persona persona = mock(Persona.class);

    @Test
    public void shouldCreateEinaAsPropietario() {
        PropietarioEspacio propietarioEspacio = new PropietarioEspacio("EINA");

        assertTrue(propietarioEspacio.isEINA());
        assertFalse(propietarioEspacio.isPersonas());
        assertFalse(propietarioEspacio.isDepartamento());
        assertEquals(propietarioEspacio.getPropietario(), List.of("EINA"));
    }

    @Test
    public void shouldCreateDepartamentoAsPropietario() {
        PropietarioEspacio propietarioEspacio = new PropietarioEspacio(Departamento.DIIS);

        assertTrue(propietarioEspacio.isDepartamento());
        assertFalse(propietarioEspacio.isEINA());
        assertFalse(propietarioEspacio.isPersonas());
        assertEquals(propietarioEspacio.getPropietario(), List.of(Departamento.DIIS.getDepartamento()));
    }

    @Test
    public void shouldCreatePersonasAsPropietario() {

        when(persona.getRoles()).thenReturn(List.of(Rol.INVESTIGADOR_CONTRATADO));
        when(persona.getCorreo()).thenReturn("email@unizar.es");
        PropietarioEspacio propietarioEspacio = new PropietarioEspacio(List.of(persona));

        assertTrue(propietarioEspacio.isPersonas());
        assertFalse(propietarioEspacio.isEINA());
        assertFalse(propietarioEspacio.isDepartamento());
        assertEquals(propietarioEspacio.getPropietario(), List.of(persona.getCorreo()));
    }

    @Test
    public void shouldThrowExceptionWhenPersonaIsNotInvestigadorODocente() {
        when(persona.getRoles()).thenReturn(List.of(Rol.GERENTE));

        assertThrows(IllegalArgumentException.class, () -> new PropietarioEspacio(List.of(persona)));
    }
}