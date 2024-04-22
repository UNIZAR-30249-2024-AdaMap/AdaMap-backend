package com.example.adamapbackend.domain;

import javax.persistence.Embeddable;

@Embeddable
public class Horario {
    String horarioLunes;
    String horarioMartes;
    String horarioMiercoles;
    String horarioJueves;
    String horarioViernes;
    String horarioSabado;
    String horarioDomingo;

    public String getByDay(Integer day) {
        return switch (day) {
            case 0 -> horarioDomingo;
            case 1 -> horarioLunes;
            case 2 -> horarioMartes;
            case 3 -> horarioMiercoles;
            case 4 -> horarioJueves;
            case 5 -> horarioViernes;
            default -> horarioSabado;
        };
    }
}
