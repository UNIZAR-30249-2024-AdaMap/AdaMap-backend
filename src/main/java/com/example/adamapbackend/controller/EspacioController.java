package com.example.adamapbackend.controller;

import com.example.adamapbackend.domain.Espacio;
import com.example.adamapbackend.domain.Horario;
import com.example.adamapbackend.domain.Persona;
import com.example.adamapbackend.domain.PropietarioEspacio;
import com.example.adamapbackend.domain.enums.Departamento;
import com.example.adamapbackend.domain.enums.TipoEspacio;
import com.example.adamapbackend.service.PersonaService;
import com.example.adamapbackend.service.ReservaService;
import com.example.adamapbackend.token.TokenParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.adamapbackend.service.EspacioService;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/espacios")
public class EspacioController {

    private final EspacioService espacioService;
    private final TokenParser tokenParser;
    private final PersonaService personaService;
    private final ReservaService reservaService;
    @Autowired
    public EspacioController(EspacioService espacioService, TokenParser tokenParser, PersonaService personaService, ReservaService reservaService) {
        this.espacioService = espacioService;
        this.tokenParser = tokenParser;
        this.personaService = personaService;
        this.reservaService = reservaService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Espacio> buscarEspacioPorId(@PathVariable String id) {
        Optional<Espacio> espacio = espacioService.getEspacioById(id);

        if (espacio.isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        return ResponseEntity.ok(espacio.get());
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Espacio>> buscarEspacios(
            @RequestParam(required = false) Integer planta,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) Integer ocupantes) {

        TipoEspacio tipoEspacio = TipoEspacio.of(categoria);

        return ResponseEntity.ok(espacioService.getEspacios(planta, tipoEspacio, ocupantes));
    }


    @PutMapping("/edit/{id}/reservabilidad")
    public ResponseEntity<Espacio> cambiarReservabilidad(@PathVariable String id, @RequestHeader("Authorization") String tokenHeader) {

        //  RECOGER PERSONA DE LA BBDD Y CHECK ES GERENTE
        String jwtToken = tokenHeader.replace("Bearer ", "");
        String email = tokenParser.extractEmail(jwtToken);

        Optional<Persona> admin = personaService.getPersonaById(email);

        if (admin.isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        if (!admin.get().isAdmin())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Optional<Espacio> espacio = espacioService.getEspacioById(id);

        if (espacio.isEmpty())
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        Espacio espacioAEditar = espacio.get();
        espacioAEditar.cambiarReservabilidad();

        espacioService.guardarEspacio(espacioAEditar);

        return ResponseEntity.ok(espacioAEditar);
    }

    @PutMapping("/edit/{id}/categoria/{categoria}")
    public ResponseEntity<Espacio> cambiarCategoria(@PathVariable String id, @PathVariable String categoria, @RequestHeader("Authorization") String tokenHeader) {

        //  RECOGER PERSONA DE LA BBDD Y CHECK ES GERENTE
        String jwtToken = tokenHeader.replace("Bearer ", "");
        String email = tokenParser.extractEmail(jwtToken);

        Optional<Persona> admin = personaService.getPersonaById(email);

        if (admin.isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        if (!admin.get().isAdmin())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        TipoEspacio tipoEspacio = TipoEspacio.of(categoria);

        Optional<Espacio> espacio = espacioService.getEspacioById(id);

        if (espacio.isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        Espacio espacioAEditar = espacio.get();
        espacioAEditar.cambiarTipoEspacio(tipoEspacio);

        espacioService.guardarEspacio(espacioAEditar);

        return ResponseEntity.ok(espacioAEditar);
    }

    @PutMapping("/edit/{id}/horario")
    public ResponseEntity<Espacio> cambiarHorario(@PathVariable String id, @RequestBody Horario horario, @RequestHeader("Authorization") String tokenHeader) {

        //  RECOGER PERSONA DE LA BBDD Y CHECK ES GERENTE
        String jwtToken = tokenHeader.replace("Bearer ", "");
        String email = tokenParser.extractEmail(jwtToken);

        Optional<Persona> admin = personaService.getPersonaById(email);

        if (admin.isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        if (!admin.get().isAdmin())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Optional<Espacio> espacio = espacioService.getEspacioById(id);

        if (espacio.isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        Espacio espacioAEditar = espacio.get();
        espacioAEditar.cambiarHorario(horario);

        espacioService.guardarEspacio(espacioAEditar);

        return ResponseEntity.ok(espacioAEditar);
    }

    @PutMapping("/edit/{id}/propietario/EINA")
    public ResponseEntity<Espacio> cambiarPropietarioENIA(@PathVariable String id, @RequestHeader("Authorization") String tokenHeader) {

        //  RECOGER PERSONA DE LA BBDD Y CHECK ES GERENTE
        String jwtToken = tokenHeader.replace("Bearer ", "");
        String email = tokenParser.extractEmail(jwtToken);

        Optional<Persona> admin = personaService.getPersonaById(email);

        if (admin.isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        if (!admin.get().isAdmin())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Optional<Espacio> espacio = espacioService.getEspacioById(id);

        if (espacio.isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        Espacio espacioAEditar = espacio.get();
        espacioAEditar.updatePropietario(new PropietarioEspacio());

        espacioService.guardarEspacio(espacioAEditar);

        return ResponseEntity.ok(espacioAEditar);
    }

    @PutMapping("/edit/{id}/propietario/departamento/{departamento}")
    public ResponseEntity<Espacio> cambiarPropietarioDepartamento(@PathVariable String id, @PathVariable String departamento, @RequestHeader("Authorization") String tokenHeader) {

        //  RECOGER PERSONA DE LA BBDD Y CHECK ES GERENTE
        String jwtToken = tokenHeader.replace("Bearer ", "");
        String email = tokenParser.extractEmail(jwtToken);

        Optional<Persona> admin = personaService.getPersonaById(email);

        if (admin.isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        if (!admin.get().isAdmin())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Departamento departamentoNuevo = Departamento.of(departamento);


        Optional<Espacio> espacio = espacioService.getEspacioById(id);

        if (espacio.isEmpty() || departamentoNuevo == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        Espacio espacioAEditar = espacio.get();
        espacioAEditar.updatePropietario(new PropietarioEspacio(departamentoNuevo));

        espacioService.guardarEspacio(espacioAEditar);

        return ResponseEntity.ok(espacioAEditar);
    }

    @PutMapping("/edit/{id}/propietario/personas")
    public ResponseEntity<Espacio> cambiarPropietarioPersonas(@PathVariable String id, @RequestBody List<String> personas, @RequestHeader("Authorization") String tokenHeader) {

        //  RECOGER PERSONA DE LA BBDD Y CHECK ES GERENTE
        String jwtToken = tokenHeader.replace("Bearer ", "");
        String email = tokenParser.extractEmail(jwtToken);

        Optional<Persona> admin = personaService.getPersonaById(email);

        if (admin.isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        if (!admin.get().isAdmin())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        //  RECOGER PERSONAS DE LA BBDD
        List<Persona> personasList = personas.stream().map(personaService::getPersonaById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        Optional<Espacio> espacio = espacioService.getEspacioById(id);

        if (espacio.isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        Espacio espacioAEditar = espacio.get();
        espacioAEditar.updatePropietario(new PropietarioEspacio(personasList));

        espacioService.guardarEspacio(espacioAEditar);

        return ResponseEntity.ok(espacioAEditar);
    }

    @PutMapping("/edit/{id}/porcentaje/{porcentaje}")
    public ResponseEntity<Espacio> cambiarPorcentajeUso(@PathVariable String id, @PathVariable Integer porcentaje, @RequestHeader("Authorization") String tokenHeader) {

        //  RECOGER PERSONA DE LA BBDD Y CHECK ES GERENTE
        String jwtToken = tokenHeader.replace("Bearer ", "");
        String email = tokenParser.extractEmail(jwtToken);

        Optional<Persona> admin = personaService.getPersonaById(email);

        if (admin.isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        if (!admin.get().isAdmin())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Optional<Espacio> espacio = espacioService.getEspacioById(id);
        if(espacio.isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        Espacio espacioAEditar = espacio.get();
        espacioAEditar.cambiarPorcentajeUso(porcentaje);

        espacioService.guardarEspacio(espacioAEditar);

        reservaService.updateReservasPorPorcentajeEspacios(espacioAEditar);

        return ResponseEntity.ok(espacioAEditar);
    }
}
