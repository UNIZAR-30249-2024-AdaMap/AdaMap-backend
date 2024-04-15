package com.example.adamapbackend.domain.enums;

import lombok.Getter;

public enum TipoEspacio {
    AULA("aula"),
    SEMINARIO("seminario"),
    LABORATORIO("laboratorio"),
    DESPACHO("despacho"),
    SALA_COMUN("sala_comun");

    @Getter
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
