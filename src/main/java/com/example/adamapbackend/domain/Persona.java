package com.example.adamapbackend.domain;

import com.example.adamapbackend.domain.enums.Departamento;
import com.example.adamapbackend.domain.enums.Rol;

import java.util.List;

public record Persona(String correo, String nombre, Departamento departamento, List<Rol> roles) {}
