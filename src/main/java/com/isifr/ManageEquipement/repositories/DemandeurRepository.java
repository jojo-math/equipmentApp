package com.isifr.ManageEquipement.repositories;

import com.isifr.ManageEquipement.entities.Demandeur;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DemandeurRepository extends JpaRepository<Demandeur, Long> {
}
