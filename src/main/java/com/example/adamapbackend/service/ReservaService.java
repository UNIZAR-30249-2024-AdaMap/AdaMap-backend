package com.example.adamapbackend.service;

import com.example.adamapbackend.domain.Espacio;
import com.example.adamapbackend.domain.Reserva;
import com.example.adamapbackend.domain.repositories.EspacioRepository;
import com.example.adamapbackend.domain.repositories.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class ReservaService {
    @Autowired
    ReservaRepository reservaRepository;

    public Optional<Reserva> getReservaById(UUID id){
        return reservaRepository.findById(id);
    }

    public void checkEspacios(List<Espacio> espacios, Date fecha, String horaInicio, Integer duracion){
        Set<Espacio> espacioSet = reservaRepository.findAll().stream()
                .filter( reserva -> {
                    if(reserva.getFecha().after(new Date()) || reserva.getFecha().before(new Date()))
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

        List<Espacio> espaciosYaReservados = espacios.stream().filter(espacioSet::contains).toList();


        if (!espaciosYaReservados.isEmpty()) throw new IllegalArgumentException("Uno o más espacios ya están reservados en el momento de la reserva");
    }

    public List<Reserva> reservasVivas() {

        return reservaRepository.findAll().stream().filter( reserva -> {
            if(reserva.getFecha().after(new Date()))
                return true;

            if(reserva.getFecha().before(new Date()))
                return false;

            LocalTime horaInicio = LocalTime.parse(reserva.getHoraInicio(), DateTimeFormatter.ofPattern("HH:mm"));
            return LocalDateTime.of(LocalDate.now(), horaInicio).plusMinutes(reserva.getDuracion()).isAfter(LocalDateTime.now());
        }).toList();
    }

    public void eliminarReserva(UUID id) {
        reservaRepository.deleteById(id);
    }
}
