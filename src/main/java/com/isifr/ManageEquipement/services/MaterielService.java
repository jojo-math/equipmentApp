package com.isifr.ManageEquipement.services;

import com.isifr.ManageEquipement.entities.Materiel;
import com.isifr.ManageEquipement.entities.Unite;

import java.util.List;
import java.util.Optional;

public interface MaterielService {
    List<Materiel> findAll();
    List<String> findByIdContaining(String term);
    List<String> findByNomContainingIgnoreCase(String term);
    List<String> findByDescriptionContainingIgnoreCase(String term);
    List<Materiel> findByCriteria(String id, String nom, String categorieNom, String description,
                                  String uniteNom, String salleNom, String equipementNom);
    Optional<Materiel> findById(String id);
    void save(Materiel materiel);
    void saveAll(Materiel materiel, Integer quantite);
    Materiel update(Materiel materiel);
    void delete(String id);

}
