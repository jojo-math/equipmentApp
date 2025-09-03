package com.isifr.ManageEquipement.services;

import com.isifr.ManageEquipement.entities.Entreprise;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface EntrepriseService {
    Entreprise save(Entreprise entreprise);
    Entreprise findById(Long id);
    List<Entreprise> findAll();
    List<Entreprise> findByNomContainingIgnoreCase(String nom);
    Entreprise update(Entreprise entreprise);
    @Transactional
    void delete(Long id);
}
