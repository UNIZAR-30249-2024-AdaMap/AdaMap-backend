package com.example.adamapbackend.domain;

import com.example.adamapbackend.domain.enums.Departamento;
import com.example.adamapbackend.domain.enums.Rol;
import lombok.Getter;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Embeddable
@NoArgsConstructor
public class PropietarioEspacio {
    public boolean isEINA() {
        return propietario.size() == 1 && propietario.get(0).equals("EINA");
    }

    public boolean isDepartamento() {
        return propietario.size() == 1 && Departamento.of(propietario.get(0)) != null;
    }

    public boolean isPersonas() {
        return !isEINA() && !isDepartamento();
    }

    @Transient
    private boolean isEINA;
    @Transient
    private boolean isDepartamento;
    @Transient
    private boolean isPersonas;

    @Getter
    @ElementCollection(targetClass = String.class)
    List<String> propietario;

    public PropietarioEspacio(Departamento departamento) {
        propietario = new ArrayList<>(Arrays.asList(departamento.getDepartamento()));
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

        propietario = new ArrayList<>(personas.stream().map(Persona::getCorreo).toList());
        isPersonas = true;
    }

    public PropietarioEspacio(String eina) {
        propietario = new ArrayList<>(Arrays.asList(eina));
        isEINA = true;
    }
}
