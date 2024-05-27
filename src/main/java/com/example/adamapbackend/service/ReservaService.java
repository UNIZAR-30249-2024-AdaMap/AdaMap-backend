package com.example.adamapbackend.service;

import com.example.adamapbackend.domain.Espacio;
import com.example.adamapbackend.domain.Persona;
import com.example.adamapbackend.domain.Reserva;
import com.example.adamapbackend.domain.enums.TipoUso;
import com.example.adamapbackend.domain.repositories.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class ReservaService {
    ReservaRepository reservaRepository;
    EspacioService espacioService;
    PersonaService personaService;

    @Autowired
    ReservaService(ReservaRepository reservaRepository, EspacioService espacioService, PersonaService personaService) {
        this.reservaRepository = reservaRepository;
        this.espacioService = espacioService;
        this.personaService = personaService;
    }

    public Optional<Reserva> getReservaById(UUID id){
        return reservaRepository.findById(id);
    }

    public void checkEspacios(List<Espacio> espacios, Date fecha, String horaInicio, Integer duracion, Persona persona){
        // Convertir fecha y horaInicio a LocalDate y LocalTime para manipulación
        LocalDate fechaReserva = fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalTime horaInicioNewReserva = LocalTime.parse(horaInicio, DateTimeFormatter.ofPattern("HH:mm"));
        LocalDateTime horarioInicioNewReserva = LocalDateTime.of(fechaReserva, horaInicioNewReserva);
        LocalDateTime horarioFinNewReserva = horarioInicioNewReserva.plusMinutes(duracion);

        // Obtener las reservas que coinciden en la fecha especificada
        Set<Espacio> espacioSet = reservaRepository.findAll().stream()
                .filter(reserva -> {
                    LocalDate fechaReservaExistente = reserva.getFecha().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    if (!fechaReservaExistente.equals(fechaReserva)) {
                        return false;
                    }

                    LocalTime horaInicioReserva = LocalTime.parse(reserva.getHoraInicio(), DateTimeFormatter.ofPattern("HH:mm"));
                    LocalDateTime horarioInicioReserva = LocalDateTime.of(fechaReserva, horaInicioReserva);
                    LocalDateTime horarioFinReserva = horarioInicioReserva.plusMinutes(reserva.getDuracion());

                    // Verificar si los horarios se solapan
                    return horarioInicioNewReserva.isBefore(horarioFinReserva) && horarioFinNewReserva.isAfter(horarioInicioReserva);
                })
                .map(Reserva::getEspacios)
                .flatMap(List::stream)
                .collect(Collectors.toSet());

        // Filtrar espacios ya reservados y no disponibles
        List<Espacio> espaciosYaReservados = espacios.stream()
                .filter(espacio -> espacioSet.contains(espacio) || !espacio.esReservablePorElUsuario(persona)
                        || !espacio.isHorarioDisponible(horaInicio, duracion, fecha))
                .collect(Collectors.toList());

        // Lanza una excepción si hay espacios no disponibles
        if (!espaciosYaReservados.isEmpty()) {
            throw new IllegalArgumentException("Uno o más espacios no están disponibles en el momento de la reserva");
        }
    }

    public List<Reserva> reservasVivas() {

        return reservaRepository.findAll().stream().filter(reserva -> {
            if(reserva.getFecha().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isAfter(LocalDate.now()))
                return true;

            if(reserva.getFecha().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isBefore(LocalDate.now()))
                return false;

            LocalTime horaInicio = LocalTime.parse(reserva.getHoraInicio(), DateTimeFormatter.ofPattern("HH:mm"));
            return LocalDateTime.of(LocalDate.now(), horaInicio).plusMinutes(reserva.getDuracion()).isAfter(LocalDateTime.now());
        }).toList();
    }

    public void eliminarReserva(UUID id) {
        reservaRepository.deleteById(id);
    }

    public void updateReservasPorPorcentajeEspacios(Espacio espacio) {
        reservaRepository.findAll().stream()
                .filter( reserva -> {
                    //HORA INICIO RESERVA ES POSTERIOR AL MOMENTO ACTUAL
                    LocalTime horaInicioReserva = LocalTime.parse(reserva.getHoraInicio(), DateTimeFormatter.ofPattern("HH:mm"));
                    LocalDateTime horarioInicioReserva = LocalDateTime.of(reserva.getFecha().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), horaInicioReserva);

                    return horarioInicioReserva.isAfter(LocalDateTime.now());
                })
                .filter(reserva -> reserva.getEspacios().contains(espacio))
                .forEach(reserva -> {
                    if (!reserva.checkCapacidad()) {

                        Optional<Reserva> reservaABorrar = reservaRepository.findById(reserva.getIdReserva());

                        if (reservaABorrar.isEmpty())
                            throw new IllegalArgumentException("Error al borrar la reserva.");

                        Persona personaReserva = reservaABorrar.get().getPersona();
                        personaReserva.addNotificacion("Eliminada reserva con id:" + reservaABorrar.get().getIdReserva().toString());

                        eliminarReserva(reservaABorrar.get().getIdReserva());

                        personaService.guardarPersona(personaReserva);

                    }
                });
    }

    public void guardarReserva(Reserva reserva) {
        reservaRepository.save(reserva);
    }

    public Reserva reservaAutomatica(TipoUso tipoUsoReserva, Integer numAsistentes, String horaInicio, Integer duracion, String descripcion, Date fecha, Persona persona) {
        Set<Espacio> espacioSet = reservaRepository.findAll().stream()
                .filter( reserva -> {
                    LocalTime horaInicioReserva = LocalTime.parse(reserva.getHoraInicio(), DateTimeFormatter.ofPattern("HH:mm"));
                    LocalTime horaInicioNewReserva = LocalTime.parse(horaInicio, DateTimeFormatter.ofPattern("HH:mm"));

                    LocalDateTime horarioInicioReserva = LocalDateTime.of(fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), horaInicioReserva);
                    LocalDateTime horarioFinReserva = horarioInicioReserva.plusMinutes(reserva.getDuracion());

                    LocalDateTime horarioInicioNewReserva = LocalDateTime.of(fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), horaInicioNewReserva);
                    LocalDateTime horarioFinNewReserva = horarioInicioNewReserva.plusMinutes(duracion);

                    //true si comparte horario total o parcialmente
                    return horarioInicioNewReserva.isBefore(horarioFinReserva) && horarioInicioNewReserva.isAfter(horarioInicioReserva) ||
                            horarioFinNewReserva.isBefore(horarioFinReserva) && horarioFinNewReserva.isAfter(horarioInicioReserva);
                })
                .map(Reserva::getEspacios)
                .flatMap(List::stream)
                //eliminar elementos repetidos
                .collect(Collectors.toSet());


        List<Espacio> espaciosLibres = espacioService.getEspacios(null, null, null)
                .stream()
                .filter(espacio -> !espacioSet.contains(espacio) && espacio.esReservablePorElUsuario(persona)
                        && espacio.isHorarioDisponible(horaInicio, duracion, fecha))
                .toList();

        if(espaciosLibres.isEmpty())
            throw new IllegalArgumentException("No hay espacios dispinibles para la reserva automática.");

        Optional<Espacio> espacioParaReserva = espaciosLibres.stream().filter(espacio -> espacio.getMaxPersonasParaReserva() >= numAsistentes).findFirst();

        Integer numPersonasFaltan = numAsistentes;
        if (espacioParaReserva.isEmpty()) {
            List<Espacio> espaciosParaReserva = new ArrayList<>();

            for(Espacio espacio : espaciosLibres) {
                if (numPersonasFaltan <= 0) break;

                espaciosParaReserva.add(espacio);
                numPersonasFaltan -= espacio.getMaxPersonasParaReserva();
            }
            return new Reserva(espaciosParaReserva, numAsistentes, descripcion, fecha, duracion, horaInicio, tipoUsoReserva, persona);
        }

        return new Reserva(List.of(espacioParaReserva.get()), numAsistentes, descripcion, fecha, duracion, horaInicio, tipoUsoReserva, persona);
    }
}
