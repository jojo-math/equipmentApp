package com.isifr.ManageEquipement.repositories;

import com.isifr.ManageEquipement.entities.Salle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SalleRepository extends JpaRepository<Salle, Long> {
    Optional<Salle> findByNom(String nom);
    List<Salle> findByNomContainingIgnoreCase(String nom);
}
