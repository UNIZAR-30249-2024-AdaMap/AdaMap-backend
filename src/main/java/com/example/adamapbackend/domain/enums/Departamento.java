package com.example.adamapbackend.domain.enums;

import lombok.Getter;

public enum Departamento {
    //  Departamento de Informática e ingeniería de sistemas
    DIIS("diis"),
    //  Departamento de Ingeniería electrónica y comunicaciones
    DIEC("diec");

    @Getter
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
