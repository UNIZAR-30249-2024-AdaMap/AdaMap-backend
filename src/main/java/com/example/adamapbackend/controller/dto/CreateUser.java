package com.example.adamapbackend.controller.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Getter
@NoArgsConstructor
@Setter
public class CreateUser {
    String nombre;
    String correo;
    String departamento;
    List<String> roles;
}
