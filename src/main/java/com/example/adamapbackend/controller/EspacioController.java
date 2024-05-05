package com.example.adamapbackend.controller;

import com.example.adamapbackend.domain.Espacio;
import com.example.adamapbackend.domain.Horario;
import com.example.adamapbackend.domain.Persona;
import com.example.adamapbackend.domain.PropietarioEspacio;
import com.example.adamapbackend.domain.Reserva;
import com.example.adamapbackend.domain.enums.Departamento;
import com.example.adamapbackend.domain.enums.TipoEspacio;
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
    @Autowired
    public EspacioController(EspacioService espacioService) {
        this.espacioService = espacioService;
    }

    @GetMapping("/{id}")
    public Optional<Espacio> buscarReservaPorId(@PathVariable String id) {
        return espacioService.getEspacioById(id);
    }

    @GetMapping("/buscar")
    public List<Espacio> buscarEspacios(
            @RequestParam(required = false) Integer planta,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) Integer ocupantes) {

        TipoEspacio tipoEspacio = TipoEspacio.of(categoria);

        return espacioService.getEspacios(planta, tipoEspacio, ocupantes);
    }


    @PutMapping("/edit/{id}/reservabilidad")
    public ResponseEntity<Espacio> cambiarReservabilidad(@PathVariable String id) {

        //TODO: RECOGER PERSONA DE LA BBDD Y CHECK ES GERENTE

        Optional<Espacio> espacio = espacioService.getEspacioById(id);

        if (espacio.isEmpty())
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        Espacio espacioAEditar = espacio.get();
        espacioAEditar.cambiarReservabilidad();

        //TODO: GUARDAR EN BBDD JAJAJAJAJAJAJAJAJAJA

        return ResponseEntity.ok(espacioAEditar);
    }

    @PutMapping("/edit/{id}/categoria/{categoria}")
    public ResponseEntity<Espacio> cambiarCategoria(@PathVariable String id, @PathVariable String categoria) {

        //TODO: RECOGER PERSONA DE LA BBDD Y CHECK ES GERENTE

        TipoEspacio tipoEspacio = TipoEspacio.of(categoria);

        Optional<Espacio> espacio = espacioService.getEspacioById(id);

        if (espacio.isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        Espacio espacioAEditar = espacio.get();
        espacioAEditar.cambiarTipoEspacio(tipoEspacio);

        //TODO: GUARDAR EN BBDD

        return ResponseEntity.ok(espacioAEditar);
    }

    @PutMapping("/edit/{id}/horario")
    public ResponseEntity<Espacio> cambiarHorario(@PathVariable String id, @RequestBody Horario horario) {

        //TODO: RECOGER PERSONA DE LA BBDD Y CHECK ES GERENTE

        Optional<Espacio> espacio = espacioService.getEspacioById(id);

        if (espacio.isEmpty() || horario == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        Espacio espacioAEditar = espacio.get();
        espacioAEditar.cambiarHorario(horario);

        //TODO: GUARDAR EN BBDD

        return ResponseEntity.ok(espacioAEditar);
    }

    @PutMapping("/edit/{id}/propietario/EINA")
    public ResponseEntity<Espacio> cambiarPropietarioENIA(@PathVariable String id) {

        //TODO: RECOGER PERSONA DE LA BBDD Y CHECK ES GERENTE

        Optional<Espacio> espacio = espacioService.getEspacioById(id);

        if (espacio.isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        Espacio espacioAEditar = espacio.get();
        espacioAEditar.updatePropietario(new PropietarioEspacio());

        //TODO: GUARDAR EN BBDD

        return ResponseEntity.ok(espacioAEditar);
    }

    @PutMapping("/edit/{id}/propietario/departamento/{departamento}")
    public ResponseEntity<Espacio> cambiarPropietarioDepartamento(@PathVariable String id, @PathVariable String departamento) {

        //TODO: RECOGER PERSONA DE LA BBDD Y CHECK ES GERENTE

        Departamento departamentoNuevo = Departamento.of(departamento);


        Optional<Espacio> espacio = espacioService.getEspacioById(id);

        if (espacio.isEmpty() || departamentoNuevo == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        Espacio espacioAEditar = espacio.get();
        espacioAEditar.updatePropietario(new PropietarioEspacio(departamentoNuevo));

        //TODO: GUARDAR EN BBDD

        return ResponseEntity.ok(espacioAEditar);
    }

    @PutMapping("/edit/{id}/propietario/personas")
    public ResponseEntity<Espacio> cambiarPropietarioPersonas(@PathVariable String id, @RequestBody List<String> personas) {

        //TODO: RECOGER PERSONA DE LA BBDD Y CHECK ES GERENTE

        //TODO: RECOGER PERSONAS DE LA BBDD
        List<Persona> personasList = List.of();

        Optional<Espacio> espacio = espacioService.getEspacioById(id);

        if (espacio.isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        Espacio espacioAEditar = espacio.get();
        espacioAEditar.updatePropietario(new PropietarioEspacio(personasList));

        //TODO: GUARDAR EN BBDD

        return ResponseEntity.ok(espacioAEditar);
    }

    @PutMapping("/edit/{id}/porcentaje/{porcentaje}")
    public ResponseEntity<Espacio> cambiarPorcentajeUso(@PathVariable String id, @PathVariable Integer porcentaje) {

        //TODO: RECOGER PERSONA DE LA BBDD Y CHECK ES GERENTE

        Optional<Espacio> espacio = espacioService.getEspacioById(id);
        if(espacio.isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        Espacio espacioAEditar = espacio.get();
        espacioAEditar.cambiarPorcentajeUso(porcentaje);

        //TODO: GUARDAR EN BBDD
        
        //TODO: CHECK RESRVAS CUYA HORA INICIO SEA POSTERIOR A LA ACTUAL

        return ResponseEntity.ok(espacioAEditar);
    }
}
