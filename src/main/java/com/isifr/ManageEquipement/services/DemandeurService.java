package com.isifr.ManageEquipement.services;

import com.isifr.ManageEquipement.entities.Demandeur;

import java.util.List;

public interface DemandeurService {
    List<Demandeur> findAll();
    Demandeur findById(Long id);
    Demandeur save(Demandeur demandeur);
    Demandeur update(Demandeur demandeur);
    void delete(Long id);
}