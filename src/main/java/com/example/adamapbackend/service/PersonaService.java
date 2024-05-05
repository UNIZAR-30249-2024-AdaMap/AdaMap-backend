package com.example.adamapbackend.service;

import com.example.adamapbackend.domain.Espacio;
import com.example.adamapbackend.domain.Persona;
import com.example.adamapbackend.domain.repositories.EspacioRepository;
import com.example.adamapbackend.domain.repositories.PersonaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PersonaService {
    @Autowired
    PersonaRepository personaRepository;

    public Optional<Persona> getPersonaById(String id){
        return personaRepository.findById(id);
    }

}
