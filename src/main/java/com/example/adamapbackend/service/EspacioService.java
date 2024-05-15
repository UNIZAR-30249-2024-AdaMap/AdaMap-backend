package com.example.adamapbackend.service;

import com.example.adamapbackend.domain.Espacio;
import com.example.adamapbackend.domain.enums.TipoEspacio;
import com.example.adamapbackend.domain.repositories.EspacioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EspacioService {
    EspacioRepository espacioRepository;
    @Autowired
    EspacioService(EspacioRepository espacioRepository) {
        this.espacioRepository = espacioRepository;
    }

    public Optional<Espacio> getEspacioById(String id){
        return espacioRepository.findById(id);
    }


    public List<Espacio> getEspacios(Integer planta, TipoEspacio categoria, Integer numMaxOcupantes){
        List<Espacio> espacios = espacioRepository.findAll();

        if(categoria != null){
            espacios = espacios.stream().filter(espacio -> categoria.equals(espacio.getTipoEspacioParaReserva())).toList();
        }

        if (planta != null) {
            espacios = espacios.stream().filter(espacio -> planta.equals(espacio.getPlanta())).toList();
        }

        if (numMaxOcupantes != null) {
            espacios = espacios.stream().filter(espacio -> espacio.getMaxPersonasParaReserva() >= numMaxOcupantes).toList();
        }

        return espacios;
    }

    public void guardarEspacio(Espacio espacio) {
        espacioRepository.save(espacio);
    }
}
