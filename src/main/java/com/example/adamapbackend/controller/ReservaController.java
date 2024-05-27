package com.example.adamapbackend.controller;

import com.example.adamapbackend.controller.dto.CreateReserva;
import com.example.adamapbackend.controller.dto.CreateUser;
import com.example.adamapbackend.domain.Espacio;
import com.example.adamapbackend.domain.Persona;
import com.example.adamapbackend.domain.Reserva;
import com.example.adamapbackend.domain.enums.TipoUso;
import com.example.adamapbackend.service.EspacioService;
import com.example.adamapbackend.service.PersonaService;
import com.example.adamapbackend.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {
    private final ReservaService reservaService;
    private final EspacioService espacioService;
    private final PersonaService personaService;
    @Autowired
    public ReservaController(ReservaService reservaService, EspacioService espacioService, PersonaService personaService) {
        this.reservaService = reservaService;
        this.espacioService = espacioService;
        this.personaService = personaService;
    }

    @GetMapping("/notificaciones")
    public ResponseEntity<List<String>> mostrarNotificaciones(@RequestHeader("Authorization") String tokenHeader) {

        //  RECOGER PERSONA DE LA BBDD
        String email = tokenHeader.replace("Bearer ", "");

        Optional<Persona> persona = personaService.getPersonaById(email);

        if (persona.isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        List<String> notificaciones = new ArrayList<>(persona.get().getNotificaciones());

        return ResponseEntity.ok(notificaciones);
    }

    @DeleteMapping("/notificaciones")
    public ResponseEntity<List<String>> eliminarNotificaciones(@RequestHeader("Authorization") String tokenHeader) {

        //  RECOGER PERSONA DE LA BBDD
        String email = tokenHeader.replace("Bearer ", "");

        Optional<Persona> persona = personaService.getPersonaById(email);

        if (persona.isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        persona.get().deleteNotificaciones();

        personaService.guardarPersona(persona.get());

        return ResponseEntity.ok().build();
    }



    @PostMapping("/reservar")
    public ResponseEntity<Reserva> buscarEspacios(
            @RequestBody CreateReserva createReserva,
            @RequestHeader("Authorization") String tokenHeader
    ) {

        if (createReserva.getEspacios().isEmpty() || createReserva.getTipoUso() == null || createReserva.getNumAsistentes() == null || createReserva.getHoraInicio() == null || createReserva.getDuracion() == null || createReserva.getFecha() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        TipoUso tipoUsoReserva = TipoUso.of(createReserva.getTipoUso());

        List<Espacio> espaciosList = createReserva.getEspacios().stream()
               .map(espacioService::getEspacioById)
               .filter(Optional::isPresent)
               .map(Optional::get)
               .toList();

        //  RECOGER PERSONA DE LA BBDD
        String email = tokenHeader.replace("Bearer ", "");

        Optional<Persona> persona = personaService.getPersonaById(email);

        if (persona.isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        reservaService.checkEspacios(espaciosList, createReserva.getFecha(), createReserva.getHoraInicio(), createReserva.getDuracion(), persona.get());

        Reserva reserva = new Reserva(espaciosList, createReserva.getNumAsistentes(), createReserva.getDescripcion(), createReserva.getFecha(), createReserva.getDuracion(), createReserva.getHoraInicio(), tipoUsoReserva, persona.get());

        reservaService.guardarReserva(reserva);

        return ResponseEntity.ok(reserva);
    }


    @GetMapping("/reservasVivas")
    public ResponseEntity<List<Reserva>> verReservasVivas(@RequestHeader("Authorization") String tokenHeader) {
        //  RECOGER PERSONA DE LA BBDD Y CHECK ES GERENTE
        String email = tokenHeader.replace("Bearer ", "");

        Optional<Persona> admin = personaService.getPersonaById(email);

        if (admin.isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        if (!admin.get().isAdmin())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        List<Reserva> reservasVivasList = reservaService.reservasVivas();
        return ResponseEntity.ok(reservasVivasList);
    }

    @DeleteMapping("/eliminarReserva/{id}")
    public ResponseEntity<Reserva> eliminarReserva(@PathVariable String id, @RequestHeader("Authorization") String tokenHeader) {

        //  RECOGER PERSONA DE LA BBDD Y CHECK ES GERENTE
        String email = tokenHeader.replace("Bearer ", "");

        Optional<Persona> admin = personaService.getPersonaById(email);

        if (admin.isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        if (!admin.get().isAdmin())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Optional<Reserva> reserva = reservaService.getReservaById(UUID.fromString(id));

        if (reserva.isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        Persona personaReserva = reserva.get().getPersona();
        personaReserva.addNotificacion("Eliminada reserva con id:" + UUID.fromString(id).toString());

        reservaService.eliminarReserva(UUID.fromString(id));
        personaService.guardarPersona(personaReserva);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/reservarAutomatica")
    public ResponseEntity<Reserva> reservaAutomatica(
            @RequestBody CreateReserva createReserva,
            @RequestHeader("Authorization") String tokenHeader
    ) {
        TipoUso tipoUsoReserva = TipoUso.of(createReserva.getTipoUso());

        //  RECOGER PERSONA DE LA BBDD
        String email = tokenHeader.replace("Bearer ", "");

        Optional<Persona> persona = personaService.getPersonaById(email);

        if (persona.isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        Reserva reserva = reservaService.reservaAutomatica(tipoUsoReserva, createReserva.getNumAsistentes(), createReserva.getHoraInicio(), createReserva.getDuracion(), createReserva.getDescripcion(), createReserva.getFecha(), persona.get());

        reservaService.guardarReserva(reserva);
        return ResponseEntity.ok(reserva);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reserva> buscarReservaPorId(@PathVariable String id) {
        Optional<Reserva> reserva = reservaService.getReservaById(UUID.fromString(id));

        if (reserva.isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        return ResponseEntity.ok(reserva.get());
    }
}
