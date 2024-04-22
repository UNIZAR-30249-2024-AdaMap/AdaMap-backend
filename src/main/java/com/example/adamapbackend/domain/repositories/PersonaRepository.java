package com.example.adamapbackend.domain.repositories;

import com.example.adamapbackend.domain.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PersonaRepository extends JpaRepository<Persona, String> {
}