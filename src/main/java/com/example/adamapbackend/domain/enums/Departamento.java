package com.example.adamapbackend.domain.enums;

import lombok.Getter;

import java.io.Serializable;

@Getter
public enum Departamento implements Serializable {
    //  Departamento de Informática e ingeniería de sistemas
    DIIS("DIIS"),
    //  Departamento de Ingeniería electrónica y comunicaciones
    DIEC("DIEC");

    private final String departamento;

    Departamento(String departamento) {
        this.departamento = departamento;
    }

    public static Departamento of(final String departamento) {
        for(Departamento t : Departamento.values()){
            if(t.departamento.equals(departamento)){
                return t;
            }
        }
        return null;
    }
}
