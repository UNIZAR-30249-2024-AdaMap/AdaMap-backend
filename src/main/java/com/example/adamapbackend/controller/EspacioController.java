package com.example.adamapbackend.controller;

import com.example.adamapbackend.domain.Espacio;
import com.example.adamapbackend.domain.Reserva;
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

    /*
        @GetMapping("/{categoria}")
        public List<Espacio> buscarReservaPorCategoria(@PathVariable String categoria) {
            TipoEspacio tipoEspacio = TipoEspacio.of(categoria);
            return espacioService.getEspacioByCategoria(tipoEspacio);
        }

        @GetMapping("/{ocupantes}")
        public List<Espacio> buscarReservaPorOcupantes(@PathVariable Integer ocupantes) {
            return espacioService.getEspaciosByOcupantes(ocupantes);
        }

        @GetMapping("/{planta}")
        public List<Espacio> buscarReservaPorPlanta(@PathVariable Integer planta) {
            return espacioService.getEspaciosByPlanta(planta);
        }
    */
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
    public Espacio cambiarHorario(@PathVariable String id) {
        return null;
    }
}
