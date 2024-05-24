package com.example.adamapbackend.controller.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CreateReserva {
    List<String> espacios;
    String tipoUso;
    Integer numAsistentes;
    String horaInicio;
    Integer duracion;
    String descripcion;
    Date fecha;
}
