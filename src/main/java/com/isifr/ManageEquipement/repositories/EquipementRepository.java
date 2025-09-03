package com.isifr.ManageEquipement.repositories;

import com.isifr.ManageEquipement.entities.Demande;
import com.isifr.ManageEquipement.entities.Equipement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EquipementRepository extends JpaRepository<Equipement, Long> {
    List<Equipement> findByDemande(Demande demande);
    List<Equipement> findByNomContainingIgnoreCase(String nom);
}
