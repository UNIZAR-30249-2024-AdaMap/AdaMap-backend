package com.example.adamapbackend.domain;

import java.util.List;

public record Espacio(Integer idEspacio, Integer numMaxPersonas, Boolean reservable, Integer tamano, String horario, List<String> asignadoA) {}
