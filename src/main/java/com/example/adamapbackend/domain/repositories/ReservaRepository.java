package com.example.adamapbackend.domain.repositories;

import com.example.adamapbackend.domain.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface ReservaRepository extends JpaRepository<Reserva, UUID> {
}