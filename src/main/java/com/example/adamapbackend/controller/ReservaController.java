package com.example.adamapbackend.controller;

import com.example.adamapbackend.domain.Espacio;
import com.example.adamapbackend.domain.Horario;
import com.example.adamapbackend.domain.Persona;
import com.example.adamapbackend.domain.Reserva;
import com.example.adamapbackend.domain.enums.TipoEspacio;
import com.example.adamapbackend.domain.enums.TipoUso;
import com.example.adamapbackend.service.EspacioService;
import com.example.adamapbackend.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {
    private final ReservaService reservaService;
    private final EspacioService espacioService;
    @Autowired
    public ReservaController(ReservaService reservaService, EspacioService espacioService) {
        this.reservaService = reservaService;
        this.espacioService = espacioService;
    }

    @GetMapping("/{id}")
    public Optional<Reserva> buscarReservaPorId(@PathVariable String id) {
        return reservaService.getReservaById(UUID.fromString(id));
    }

    @GetMapping("/reservar")
    public ResponseEntity<Reserva> buscarEspacios(
            @RequestBody List<String> espacios,
            @RequestBody String tipoUso,
            @RequestBody Integer numAsistentes,
            @RequestBody String horaInicio,
            @RequestBody Integer duracion,
            @RequestBody String descripcion,
            @RequestBody Date fecha
    ) {

        if (espacios.isEmpty() || tipoUso == null || numAsistentes == null || horaInicio == null || duracion == null || fecha == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        TipoUso tipoUsoReserva = TipoUso.of(tipoUso);

        List<Espacio> espaciosList = espacios.stream()
               .map(espacioService::getEspacioById)
               .filter(Optional::isPresent)
               .map(Optional::get)
               .toList();

        reservaService.checkEspacios(espaciosList, fecha, horaInicio, duracion);

        //TODO: RECOGER PERSONA DE LA BBDD

        Reserva reserva = new Reserva(espaciosList, numAsistentes, descripcion, fecha, duracion, horaInicio, tipoUsoReserva, new Persona());

        //TODO: GUARDAR EN BBDD JAJAJAJAJAJAJAJAJAJA

        return ResponseEntity.ok(reserva);
    }


    @GetMapping("/reservasVivas")
    public ResponseEntity<List<Reserva>> verReservasVivas() {
        //TODO: RECOGER PERSONA DE LA BBDD Y CHECK ES GERENTE
        List<Reserva> reservasVivasList = reservaService.reservasVivas();
        return ResponseEntity.ok(reservasVivasList);
    }

    @GetMapping("/eliminarReserva/{id}")
    public ResponseEntity<Reserva> eliminarReserva(@PathVariable String id) {
        //TODO: RECOGER PERSONA DE LA BBDD Y CHECK ES GERENTE
        reservaService.eliminarReserva(UUID.fromString(id));

        //TODO: En ese caso, la aplicación avisará al usuario que la realizó.

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
