package com.example.adamapbackend.controller;

import com.example.adamapbackend.domain.Persona;
import com.example.adamapbackend.domain.enums.Departamento;
import com.example.adamapbackend.domain.enums.Rol;
import com.example.adamapbackend.service.PersonaService;
import com.example.adamapbackend.token.TokenParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/personas")
public class PersonaController {

    private final PersonaService personaService;
    private final TokenParser tokenParser;

    @Autowired
    public PersonaController(PersonaService personaService, TokenParser tokenParser) {
        this.personaService = personaService;
        this.tokenParser = tokenParser;
    }

    @GetMapping("/login")
    public ResponseEntity<String> loginUser(@RequestParam String correo) {
        Optional<Persona> persona = personaService.getPersonaById(correo);

        if (persona.isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        return ResponseEntity.ok(persona.get().getCorreo());
    }

    @PutMapping("/edit/{correo}/rol/{rol}")
    public ResponseEntity<Persona> cambiarRol(@PathVariable String correo, @PathVariable String rol, @RequestHeader("Authorization") String tokenHeader) {

        //  RECOGER PERSONA DE LA BBDD Y CHECK ES GERENTE
        String jwtToken = tokenHeader.replace("Bearer ", "");
        String email = tokenParser.extractEmail(jwtToken);

        Optional<Persona> admin = personaService.getPersonaById(email);

        if (admin.isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        if (!admin.get().isAdmin())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Rol rolNuevo = Rol.of(rol);
        Optional<Persona> persona = personaService.getPersonaById(correo);

        if (persona.isEmpty() || rolNuevo == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        Persona personaAeditar = persona.get();
        personaAeditar.addRol(rolNuevo);

        personaService.guardarPersona(personaAeditar);

        return ResponseEntity.ok(personaAeditar);
    }

    @PutMapping("/edit/{correo}/departamento/{departamento}")
    public ResponseEntity<Persona> cambiarDepartamento(@PathVariable String correo, @PathVariable String departamento, @RequestHeader("Authorization") String tokenHeader) {

        //  RECOGER PERSONA DE LA BBDD Y CHECK ES GERENTE
        String jwtToken = tokenHeader.replace("Bearer ", "");
        String email = tokenParser.extractEmail(jwtToken);

        Optional<Persona> admin = personaService.getPersonaById(email);

        if (admin.isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        if (!admin.get().isAdmin())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Departamento departamentoNuevo = Departamento.of(departamento);
        Optional<Persona> persona = personaService.getPersonaById(correo);

        if (persona.isEmpty() || departamentoNuevo == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        Persona personaAeditar = persona.get();
        personaAeditar.updateDepartamento(departamentoNuevo);

        personaService.guardarPersona(personaAeditar);

        return ResponseEntity.ok(personaAeditar);
    }

    @PostMapping("/create")
    public ResponseEntity<Persona> anyadirPersona(
            @RequestBody String nombre,
            @RequestBody String correo,
            @RequestBody (required = false) String departamento,
            @RequestBody List<String> roles,
            @RequestHeader("Authorization") String tokenHeader) {

        //  RECOGER PERSONA DE LA BBDD Y CHECK ES GERENTE
        String jwtToken = tokenHeader.replace("Bearer ", "");
        String email = tokenParser.extractEmail(jwtToken);

        Optional<Persona> admin = personaService.getPersonaById(email);

        if (admin.isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        if (!admin.get().isAdmin())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Departamento departamentoNuevo = Departamento.of(departamento);
        
        List<Rol> rolList = roles.stream().map(Rol::of).toList();

        Persona persona = new Persona(correo, nombre, departamentoNuevo, rolList);

        personaService.guardarPersona(persona);

        return ResponseEntity.ok(persona);
    }
}
