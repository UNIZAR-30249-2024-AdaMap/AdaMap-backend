package com.example.adamapbackend.domain.enums;

import lombok.Getter;

import java.io.Serializable;

@Getter
public enum Rol implements Serializable {
    ESTUDIANTE("estudiante"),
    INVESTIGADOR_CONTRATADO("investigador_contratado"),
    DOCENTE_INVESTIGADOR("docente_investigador"),
    CONSERJE("conserje"),
    TECNICO_LABORATORIO("tecnico_laboratorio"),
    GERENTE("gerente");

    private final String rol;

    Rol(String rol) {
        this.rol = rol;
    }

    public static Rol of(final String rol) {
        for(Rol t : Rol.values()){
            if(t.rol.equals(rol)){
                return t;
            }
        }
        return null;
    }
}
