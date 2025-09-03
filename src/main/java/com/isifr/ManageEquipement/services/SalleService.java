package com.isifr.ManageEquipement.services;

import com.isifr.ManageEquipement.entities.Administrateur;
import com.isifr.ManageEquipement.entities.Equipement;
import com.isifr.ManageEquipement.entities.Materiel;
import com.isifr.ManageEquipement.entities.Salle;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface SalleService {
    Salle save(Salle salle, Administrateur administrateur);
    Optional<Salle> findById(Long id);
    List<Salle> findAll();
    List<String> findByNomContainingIgnoreCase(String term);
    List<Salle> findByMateriel(Materiel materiel);
    List<Salle> findByEquipement(Equipement equipment);
    Salle update(Salle salle);
    @Transactional
    void delete(Long id);
    int ndSallesEquip();
}
