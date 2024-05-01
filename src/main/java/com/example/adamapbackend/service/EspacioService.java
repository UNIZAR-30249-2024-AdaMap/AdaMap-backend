package com.example.adamapbackend.service;

import com.example.adamapbackend.domain.Espacio;
import com.example.adamapbackend.domain.Persona;
import com.example.adamapbackend.domain.Reserva;
import com.example.adamapbackend.domain.enums.TipoEspacio;
import com.example.adamapbackend.domain.repositories.EspacioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EspacioService {
    @Autowired
    EspacioRepository espacioRepository;

    public Optional<Espacio> getEspacioById(String id){
        return espacioRepository.findById(id);
    }

    public List<Espacio> getEspacioByCategoria(TipoEspacio categoria){
        return espacioRepository.findAll().stream().filter(espacio -> espacio.getTipoEspacioParaReserva().equals(categoria)).toList();
    }

    public List<Espacio> getEspaciosByOcupantes(Integer numMaxOcupantes){
        return espacioRepository.findAll().stream().filter(espacio -> espacio.getNumMaxPersonas() >= numMaxOcupantes).toList();
    }

    public List<Espacio> getEspaciosByPlanta(Integer planta){
        //TODO: DE DONDE SACAMOS LA PLANTA¿??
        //return espacioRepository.findAll().stream().filter(espacio -> espacio.getNumMaxPersonas() >= numMaxOcupantes).toList();
        return espacioRepository.findAll().stream().filter(espacio -> espacio.getPlanta() == planta).toList();
    }


    @Transactional
    public Reserva reservarEspacio(Persona persona, List<Reserva> reservasTodas, Reserva reserva) throws Exception {
        //ver si esta dispponible reservar el espacio ese dia y esas horas
        comprobarDiaNoReservable(reserva.getFecha());
        List<Reserva> reservaList = new ArrayList<>();
        int totalAsistentesPermitidos = 0;
        if(persona!= null){ // Comprueba permisos de ese rol
            for(UUID idEspacio: reserva.getIdEspacios()){
                Espacio espacio = espacioRepository.getById(idEspacio);
                if(espacio == null) throw new Exception("El espacio que se quiere reservar no existe");
                if(espacio.getReservabilidad() == null) throw new Exception("El espacio que se quiere reservar no tiene reservabilidad");
                espacio.aptoParaReservar(persona);
                totalAsistentesPermitidos+=espacio.getTotalAsistentesPermitidos();
                List<Reserva> contienenEspacio = reservasTodas.stream()
                        .filter(reserva1 -> reserva1.getIdEspacios().stream()
                                .anyMatch(reserva.getIdEspacios()::contains))
                        .toList();
                reservaList.addAll(contienenEspacio); //añadimos reservas que tienen los mismo espacios (falta que sea la misma fecha)
            }
        }
        if(!reservaCorrecta(reservaList,reserva)){
            throw new Exception("Ya existe una reserva en el horario introducido");
        }
        if(totalAsistentesPermitidos<reserva.getNumOcupantes()){
            throw new Exception("Se supera el numero máximo de asistentes de los espacios seleccionados siendo "+
                    totalAsistentesPermitidos + " el total de asistentes permitidos y "+reserva.getNumOcupantes()
                    +" el numero de asistentes de la reserva.");
        }
        return reserva;
    }
}
