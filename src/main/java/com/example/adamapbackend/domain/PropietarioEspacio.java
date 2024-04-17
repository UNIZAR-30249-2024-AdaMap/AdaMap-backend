package com.example.adamapbackend.domain;

import com.example.adamapbackend.domain.enums.Departamento;
import com.example.adamapbackend.domain.enums.Rol;
import com.example.adamapbackend.domain.enums.TipoEspacio;

import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class PropietarioEspacio {
    @Transient
    boolean isEINA;
    @Transient
    boolean isDepartamento;
    @Transient
    boolean isPersonas;

    @ElementCollection(targetClass = String.class)
    List<String> propietario;

    public PropietarioEspacio(Departamento departamento) {
        propietario = new ArrayList<>(List.of(departamento.getDepartamento()));
        isDepartamento = true;
    }

    public PropietarioEspacio(List<Persona> personas) throws IllegalAccessException {

        List<Persona> personasConRolInvestigadorODocente = personas.stream()
                .filter(persona ->
                        persona.getRoles().contains(Rol.INVESTIGADOR_CONTRATADO) ||
                                persona.getRoles().contains(Rol.DOCENTE_INVESTIGADOR)
                )
                .toList();

        if (personasConRolInvestigadorODocente.isEmpty() || personasConRolInvestigadorODocente.size() != personas.size())
            throw new IllegalAccessException("Las personas deben tener como rol investigador o docente");

        propietario = personas.stream().map(Persona::getCorreo).toList();
        isPersonas = true;
    }

    public PropietarioEspacio() {
        propietario = List.of("EINA");
        isEINA = true;
    }
}
