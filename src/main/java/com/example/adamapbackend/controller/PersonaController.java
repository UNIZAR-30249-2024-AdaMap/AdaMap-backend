package com.example.adamapbackend.controller;

import com.example.adamapbackend.controller.dto.CreateUser;
import com.example.adamapbackend.domain.Persona;
import com.example.adamapbackend.domain.enums.Departamento;
import com.example.adamapbackend.domain.enums.Rol;
import com.example.adamapbackend.service.PersonaService;
import com.example.adamapbackend.token.TokenParser;
import lombok.NoArgsConstructor;
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

        return ResponseEntity.ok(tokenParser.generateToken(persona.get().getCorreo()));
    }

    @GetMapping
    public ResponseEntity<Persona> getUser(@RequestHeader("Authorization") String id) {
        String jwtToken = id.replace("Bearer ", "");
        String email = tokenParser.extractEmail(jwtToken);

        Optional<Persona> persona = personaService.getPersonaById(email);

        if (persona.isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        return ResponseEntity.ok(persona.get());
    }

    @PutMapping("/edit/{correo}/rol")
    public ResponseEntity<Persona> cambiarRol(@PathVariable String correo, @RequestBody List<String> roles, @RequestHeader("Authorization") String tokenHeader) {

        //  RECOGER PERSONA DE LA BBDD Y CHECK ES GERENTE
        String jwtToken = tokenHeader.replace("Bearer ", "");
        String email = tokenParser.extractEmail(jwtToken);

        Optional<Persona> admin = personaService.getPersonaById(email);

        if (admin.isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        if (!admin.get().isAdmin())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        List<Rol> rolesNuevos = roles.stream().map(Rol::of).toList();
        Optional<Persona> persona = personaService.getPersonaById(correo);

        if (persona.isEmpty() || rolesNuevos.isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        Persona personaAeditar = persona.get();
        personaAeditar.updateRoles(rolesNuevos);

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

        if (persona.isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        Persona personaAeditar = persona.get();
        personaAeditar.updateDepartamento(departamentoNuevo);

        personaService.guardarPersona(personaAeditar);

        return ResponseEntity.ok(personaAeditar);
    }

    @PostMapping("/create")
    public ResponseEntity<Persona> anyadirPersona(
            @RequestBody CreateUser usuario,
            @RequestHeader("Authorization") String tokenHeader) {

        //  RECOGER PERSONA DE LA BBDD Y CHECK ES GERENTE
        String jwtToken = tokenHeader.replace("Bearer ", "");
        String email = tokenParser.extractEmail(jwtToken);

        Optional<Persona> admin = personaService.getPersonaById(email);

        if (admin.isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        if (!admin.get().isAdmin())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Departamento departamentoNuevo = Departamento.of(usuario.getDepartamento());
        
        List<Rol> rolList = usuario.getRoles().stream().map(Rol::of).toList();

        Persona persona = new Persona(usuario.getCorreo(), usuario.getNombre(), departamentoNuevo, rolList);

        personaService.guardarPersona(persona);

        return ResponseEntity.ok(persona);
    }
}
