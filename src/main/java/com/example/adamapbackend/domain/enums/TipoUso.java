package com.example.adamapbackend.domain.enums;

import lombok.Getter;

public enum TipoUso {
    DOCENCIA("docencia"),
    INVESTIGACION("investigacion"),
    GESTION("gestion"),
    OTROS("otros");

    @Getter
    private final String tipoUso;

    TipoUso(String tipoUso) {
        this.tipoUso = tipoUso;
    }

    public static TipoUso of(final String tipoUso) {
        for(TipoUso t : TipoUso.values()){
            if(t.tipoUso.equals(tipoUso)){
                return t;
            }
        }
        return null;
    }
}
