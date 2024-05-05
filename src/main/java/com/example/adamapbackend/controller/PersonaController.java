package com.example.adamapbackend.controller;

import com.example.adamapbackend.domain.Persona;
import com.example.adamapbackend.domain.enums.Departamento;
import com.example.adamapbackend.domain.enums.Rol;
import com.example.adamapbackend.service.PersonaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/personas")
public class PersonaController {

    private final PersonaService personaService;

    @Autowired
    public PersonaController(PersonaService personaService) {
        this.personaService = personaService;
    }

    @GetMapping("/login")
    public ResponseEntity<String> loginUser(@RequestParam String correo) {
        Optional<Persona> persona = personaService.getPersonaById(correo);

        if (persona.isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        return ResponseEntity.ok(persona.get().getCorreo());
    }

    @PutMapping("/edit/{correo}/rol/{rol}")
    public ResponseEntity<Persona> cambiarRol(@PathVariable String correo, @PathVariable String rol) {

        //TODO: RECOGER PERSONA DE LA BBDD Y CHECK ES GERENTE

        Rol rolNuevo = Rol.of(rol);
        Optional<Persona> persona = personaService.getPersonaById(correo);

        if (persona.isEmpty() || rolNuevo == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        Persona personaAeditar = persona.get();
        personaAeditar.addRol(rolNuevo);

        //TODO: GUARDAR EN BBDD JAJAJAJAJAJAJAJAJAJA

        return ResponseEntity.ok(personaAeditar);
    }

    @PutMapping("/edit/{correo}/departamento/{departamento}")
    public ResponseEntity<Persona> cambiarDepartamento(@PathVariable String correo, @PathVariable String departamento) {

        //TODO: RECOGER PERSONA DE LA BBDD Y CHECK ES GERENTE

        Departamento departamentoNuevo = Departamento.of(departamento);
        Optional<Persona> persona = personaService.getPersonaById(correo);

        if (persona.isEmpty() || departamentoNuevo == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        Persona personaAeditar = persona.get();
        personaAeditar.updateDepartamento(departamentoNuevo);

        //TODO: GUARDAR EN BBDD JAJAJAJAJAJAJAJAJAJA

        return ResponseEntity.ok(personaAeditar);
    }
}
