package com.isifr.ManageEquipement.services;

import com.isifr.ManageEquipement.entities.Administrateur;
import com.isifr.ManageEquipement.entities.Unite;

import java.util.List;

public interface UniteService {
    List<Unite> findAll();
    List<Unite> findByNomContainingIgnoreCase(String term);
    List<String> findByNomContainingIgnoreCaseString(String term);
    List<Unite> findByCriteria(String id, String nom, String entrepriseNom, String username);
    Unite findById(String id);
    Unite save(Unite unite, Administrateur administrateur);
    Unite update(Unite unite);
    void delete(String id);
}