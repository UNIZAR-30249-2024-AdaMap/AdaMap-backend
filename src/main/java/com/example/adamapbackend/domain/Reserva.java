package com.example.adamapbackend.domain;

import java.util.List;

public record Reserva(List<Espacio> espacios, Integer numAsistentes, String descripcion, PeriodoReserva periodoReserva) {}
