package com.example.adamapbackend.domain;


import com.example.adamapbackend.domain.enums.Departamento;
import com.example.adamapbackend.domain.enums.Rol;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PropietarioEspacioTest {
    @Test
    public void shouldCreateEinaAsPropietario() {
        PropietarioEspacio propietarioEspacio = new PropietarioEspacio();

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
        Persona persona = new Persona("investigador@unizar.es", "Investigador", Departamento.DIIS, List.of(Rol.INVESTIGADOR_CONTRATADO));
        PropietarioEspacio propietarioEspacio = new PropietarioEspacio(List.of(persona));

        assertTrue(propietarioEspacio.isPersonas());
        assertFalse(propietarioEspacio.isEINA());
        assertFalse(propietarioEspacio.isDepartamento());
        assertEquals(propietarioEspacio.getPropietario(), List.of(persona.getCorreo()));
    }

    @Test
    public void shouldThrowExceptionWhenPersonaIsNotInvestigadorODocente() {
        Persona persona = new Persona("gerente@unizar.es", "Gerente", null, List.of(Rol.GERENTE));

        assertThrows(IllegalArgumentException.class, () -> new PropietarioEspacio(List.of(persona)));
    }
}