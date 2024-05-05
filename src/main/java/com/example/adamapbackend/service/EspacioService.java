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

    /*
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

     */

    public List<Espacio> getEspacios(Integer planta, TipoEspacio categoria, Integer numMaxOcupantes){
        List<Espacio> espacios = null;

        if(categoria.toString() != null){
            espacios = espacioRepository.findAll().stream().filter(espacio -> espacio.getTipoEspacioParaReserva().equals(categoria)).toList();
        }

        if (planta != null) {
            if(espacios != null){
                //si ya habia filtros, se aplican más
                espacios.stream().filter(espacio -> espacio.getPlanta() == planta).toList();
            }else{
                //si no hay filtros, aplico la primera vez filtro
                espacios = espacioRepository.findAll().stream().filter(espacio -> espacio.getPlanta() == planta).toList();
            }
        }

        if (numMaxOcupantes != null) {
            if(espacios != null){
                //si ya habia filtros, se aplican más
                espacios.stream().filter(espacio -> espacio.getNumMaxPersonas() >= numMaxOcupantes).toList();
            }else{
                //si no hay filtros, aplico la primera vez filtro
                espacios = espacioRepository.findAll().stream().filter(espacio -> espacio.getNumMaxPersonas() >= numMaxOcupantes).toList();
            }
        }

        if(espacios != null){
            return espacios;
        }
        return espacioRepository.findAll();

        /*
        if(planta != null || categoria.toString() != null || numMaxOcupantes != null){
            return espacioRepository.findAll().stream().filter(espacio -> espacio.getTipoEspacioParaReserva().equals(categoria)).toList()
                    .stream().filter(espacio -> espacio.getNumMaxPersonas() >= numMaxOcupantes).toList()
                        .stream().filter(espacio -> espacio.getPlanta() == planta).toList();
        }
        return espacioRepository.findAll();

         */
    }

}
