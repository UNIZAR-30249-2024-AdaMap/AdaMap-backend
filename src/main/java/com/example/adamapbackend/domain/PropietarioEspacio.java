package com.example.adamapbackend.domain;

import com.example.adamapbackend.domain.enums.Departamento;
import com.example.adamapbackend.domain.enums.Rol;
import lombok.Getter;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Embeddable
public class PropietarioEspacio {
    @Transient
    private boolean isEINA;
    @Transient
    private boolean isDepartamento;
    @Transient
    private boolean isPersonas;

    @ElementCollection(targetClass = String.class)
    List<String> propietario;

    public PropietarioEspacio(Departamento departamento) {
        propietario = List.of(departamento.getDepartamento());
        isDepartamento = true;
    }

    public PropietarioEspacio(List<Persona> personas) {

        List<Persona> personasConRolInvestigadorODocente = personas.stream()
                .filter(persona ->
                        persona.getRoles().contains(Rol.INVESTIGADOR_CONTRATADO) ||
                                persona.getRoles().contains(Rol.DOCENTE_INVESTIGADOR)
                )
                .toList();

        if (personasConRolInvestigadorODocente.isEmpty() || personasConRolInvestigadorODocente.size() != personas.size())
            throw new IllegalArgumentException("Las personas deben tener como rol investigador o docente");

        propietario = personas.stream().map(Persona::getCorreo).toList();
        isPersonas = true;
    }

    public PropietarioEspacio() {
        propietario = List.of("EINA");
        isEINA = true;
    }
}
