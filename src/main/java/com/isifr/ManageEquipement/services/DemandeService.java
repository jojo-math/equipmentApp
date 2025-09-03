package com.isifr.ManageEquipement.services;

import com.isifr.ManageEquipement.entities.Demande;
import com.isifr.ManageEquipement.entities.Utilisateur;

import java.util.List;
import java.util.Optional;

public interface DemandeService {
    List<Demande> findAll();
    Optional<Demande> findById(Long id);
    Demande save(Demande demande, Utilisateur utilisateur);
    Demande update(Demande demande);
    Demande validate(Demande demande);
    Demande refuse(Demande demande);
    void delete(Long id);
    int dmValides();
}