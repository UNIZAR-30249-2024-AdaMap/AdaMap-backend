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

    @Autowired
    ReservaService(ReservaRepository reservaRepository, EspacioService espacioService) {
        this.reservaRepository = reservaRepository;
        this.espacioService = espacioService;
    }

    public Optional<Reserva> getReservaById(UUID id){
        return reservaRepository.findById(id);
    }

    public void checkEspacios(List<Espacio> espacios, Date fecha, String horaInicio, Integer duracion){
        Set<Espacio> espacioSet = reservaRepository.findAll().stream()
                .filter( reserva -> {
                    if(reserva.getFecha().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isAfter(LocalDate.now()) ||
                            reserva.getFecha().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isBefore(LocalDate.now()))
                        return false;

                    LocalTime horaInicioReserva = LocalTime.parse(reserva.getHoraInicio(), DateTimeFormatter.ofPattern("HH:mm"));
                    LocalTime horaInicioNewReserva = LocalTime.parse(horaInicio, DateTimeFormatter.ofPattern("HH:mm"));

                    LocalDateTime horarioInicioReserva = LocalDateTime.of(LocalDate.now(), horaInicioReserva);
                    LocalDateTime horarioFinReserva = horarioInicioReserva.plusMinutes(reserva.getDuracion());

                    LocalDateTime horarioInicioNewReserva = LocalDateTime.of(LocalDate.now(), horaInicioNewReserva);
                    LocalDateTime horarioFinNewReserva = horarioInicioNewReserva.plusMinutes(duracion);

                    //true si comparte horario total o parcialmente
                    return horarioInicioNewReserva.isBefore(horarioFinReserva) && horarioInicioNewReserva.isAfter(horarioInicioReserva) ||
                            horarioFinNewReserva.isBefore(horarioFinReserva) && horarioFinNewReserva.isAfter(horarioInicioReserva);
                })
                .map(Reserva::getEspacios)
                .flatMap(List::stream)
                //eliminar elementos repetidos
                .collect(Collectors.toSet());

        List<Espacio> espaciosYaReservados = espacios.stream()
                .filter(espacio ->
                        espacioSet.contains(espacio)).toList();


        if (!espaciosYaReservados.isEmpty()) throw new IllegalArgumentException("Uno o más espacios ya están reservados en el momento de la reserva");
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
                        reservaRepository.deleteById(reserva.getIdReserva());
                        // TODO: INFORMAR AL USUARIO DEL BORRADO DE SU RESERVA
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
                            && espacio.getReservable() && espacio.isHorarioDisponible(horaInicio, duracion, fecha))
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
