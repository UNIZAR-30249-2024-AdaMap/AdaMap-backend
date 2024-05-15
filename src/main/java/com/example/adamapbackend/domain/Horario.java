package com.example.adamapbackend.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.Embeddable;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Horario {
    private String horarioLunes;
    private String horarioMartes;
    private String horarioMiercoles;
    private String horarioJueves;
    private String horarioViernes;
    private String horarioSabado;
    private String horarioDomingo;

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
