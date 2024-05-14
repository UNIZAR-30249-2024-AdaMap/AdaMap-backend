package com.example.adamapbackend.domain;

//import javax.persistence.Embeddable;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Horario {
    @Column(name = "horario_lunes")
    private String horarioLunes;
    @Column(name = "horario_martes")
    private String horarioMartes;
    @Column(name = "horario_miercoles")
    private String horarioMiercoles;
    @Column(name = "horario_jueves")
    private String horarioJueves;
    @Column(name = "horario_viernes")
    private String horarioViernes;
    @Column(name = "horario_sabado")
    private String horarioSabado;
    @Column(name = "horario_domingo")
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
