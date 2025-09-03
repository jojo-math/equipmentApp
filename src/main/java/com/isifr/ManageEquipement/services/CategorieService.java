package com.isifr.ManageEquipement.services;

import com.isifr.ManageEquipement.entities.Categorie;

import java.util.List;
import java.util.Optional;

public interface CategorieService {
    List<Categorie> findAll();
    List<String> findByNomContainingIgnoreCase(String term);
    Optional<Categorie> findById(Long id);
    Categorie save(Categorie categorie);
    Categorie update(Categorie categorie);
    void delete(Long id);
}
