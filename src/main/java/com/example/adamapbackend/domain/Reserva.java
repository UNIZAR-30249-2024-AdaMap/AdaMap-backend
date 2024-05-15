package com.example.adamapbackend.domain;

import com.example.adamapbackend.domain.enums.Departamento;
import com.example.adamapbackend.domain.enums.Rol;
import com.example.adamapbackend.domain.enums.TipoEspacio;
import com.example.adamapbackend.domain.enums.TipoUso;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Getter
public class Reserva{
    @Id
    UUID idReserva;
    @OneToMany
    List<Espacio> espacios = new ArrayList<>();
    Integer numAsistentes;
    String descripcion;
    @Enumerated(EnumType.STRING)
    TipoUso tipoUso;
    String horaInicio;
    Integer duracion;
    Date fecha;
    @ManyToOne
    Persona persona;

    public Reserva(List<Espacio> espacios, Integer numAsistentes, String descripcion, Date fecha, Integer duracion, String horaInicio, TipoUso tipoUso, Persona persona) {
        if (espacios == null || espacios.isEmpty())
            throw new IllegalArgumentException("Una reserva debe tener uno o más espacios");

        espacios.forEach(espacio -> {
            espacio.checkHorario(horaInicio, duracion, fecha);
            if (!espacio.esReservablePorElUsuario(persona))
                throw new IllegalArgumentException("Uno o más espacios no son reservables por el usuario");
            this.espacios.add(espacio);
        });

        this.numAsistentes = numAsistentes;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.duracion = duracion;
        this.horaInicio = horaInicio;
        this.tipoUso = tipoUso;
        this.persona = persona;

        if(checkCapacidad())
            throw new IllegalArgumentException("No se puede reservar para más personas de las que permiten los espacios");
    }

    public boolean checkCapacidad() {
        return espacios.stream().mapToInt(Espacio::getMaxPersonasParaReserva).sum() < numAsistentes;
    }

}
