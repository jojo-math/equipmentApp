package com.isifr.ManageEquipement.repositories;

import com.isifr.ManageEquipement.entities.Entreprise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EntrepriseRepository extends JpaRepository<Entreprise, Long> {
    List<Entreprise> findByNomContainingIgnoreCase(String nom);
}