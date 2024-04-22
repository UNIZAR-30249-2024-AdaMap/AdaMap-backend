package com.example.adamapbackend.domain.repositories;

import com.example.adamapbackend.domain.Espacio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface EspacioRepository extends JpaRepository<Espacio, String> {
}