package com.example.adamapbackend.domain.enums;

import lombok.Getter;

@Getter
public enum TipoEspacio {
    AULA("AULA"),
    SEMINARIO("SEMINARIO"),
    LABORATORIO("LABORATORIO"),
    DESPACHO("DESPACHO"),
    SALA_COMUN("SALA_COMUN");

    private final String tipoEspacio;

    TipoEspacio(String tipoEspacio) {
        this.tipoEspacio = tipoEspacio;
    }

    public static TipoEspacio of(final String tipoEspacio) {
        for(TipoEspacio t : TipoEspacio.values()){
            if(t.tipoEspacio.equals(tipoEspacio)){
                return t;
            }
        }
        return null;
    }
}
