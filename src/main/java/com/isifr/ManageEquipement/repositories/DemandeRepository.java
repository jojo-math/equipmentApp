package com.isifr.ManageEquipement.repositories;

import com.isifr.ManageEquipement.entities.Demande;
import com.isifr.ManageEquipement.entities.Salle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DemandeRepository extends JpaRepository<Demande, Long> {
    List<Demande> findBySalle(Salle salle);
}
