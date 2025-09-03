package com.isifr.ManageEquipement.services;

import com.isifr.ManageEquipement.entities.Equipement;
import com.isifr.ManageEquipement.entities.Unite;

import java.util.List;
import java.util.Optional;

public interface EquipementService {
    List<Equipement> findAll();
    List<String> findByNomContainingIgnoreCase(String term);
    Optional<Equipement> findById(Long id);
    Equipement save(Equipement equipement, Unite unite);
    Equipement update(Equipement equipement);
    void delete(Long id);
}
