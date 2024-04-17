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
}
