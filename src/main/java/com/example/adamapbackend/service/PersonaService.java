package com.example.adamapbackend.service;

import com.example.adamapbackend.domain.Persona;
import com.example.adamapbackend.domain.repositories.PersonaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PersonaService {
    @Autowired
    PersonaRepository personaRepository;

    public Optional<Persona> getPersonaById(String id){
        return personaRepository.findById(id);
    }

    public void guardarPersona(Persona persona) {
        personaRepository.save(persona);
    }

    public List<String> getNotificaciones(Persona persona) {
        return persona.getNotificaciones();
    }
}
