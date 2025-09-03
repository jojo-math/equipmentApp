package com.isifr.ManageEquipement.services;

import com.isifr.ManageEquipement.entities.Entreprise;
import com.isifr.ManageEquipement.entities.Unite;
import com.isifr.ManageEquipement.repositories.EntrepriseRepository;
import com.isifr.ManageEquipement.repositories.UniteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class EntrepriseServiceImpl implements EntrepriseService {
    @Autowired
    private EntrepriseRepository entrepriseRepository;
    @Autowired
    private UniteRepository uniteRepository;
    @Override
    public Entreprise save(Entreprise entreprise) {
        if (entreprise.getNom() == null || entreprise.getNom().isEmpty()) {
            throw new IllegalArgumentException("Le nom de l'entreprise ne peut pas être vide");
        }
        for (Unite unite : entreprise.getUnites()) {
            unite.setEntreprise(entreprise);
        }

        return entrepriseRepository.save(entreprise);
    }

    @Override
    public Entreprise findById(Long id) {
        return entrepriseRepository.findById(id).get();
    }

    @Override
    public List<Entreprise> findAll() {
        return entrepriseRepository.findAll();
    }

    @Override
    public List<Entreprise> findByNomContainingIgnoreCase(String nom) {
        return entrepriseRepository.findByNomContainingIgnoreCase(nom);
    }

    @Override
    public Entreprise update(Entreprise entreprise) {
        if (!entrepriseRepository.existsById(entreprise.getId())) {
            throw new IllegalArgumentException("L'entreprise avec l'ID " + entreprise.getId() + " n'existe pas");
        }
        for (Unite unite : entreprise.getUnites()) {
            unite.setEntreprise(entreprise);
        }
        return entrepriseRepository.save(entreprise);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        Entreprise entreprise = entrepriseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Entreprise introuvable avec l'ID " + id));

        // Dissocier les unités
        if (entreprise.getUnites() != null) {
            for (Unite unite : new ArrayList<>(entreprise.getUnites())) {
                unite.setEntreprise(null); // Dissocier l'unité de l'entreprise
                uniteRepository.delete(unite);
            }
            entreprise.getUnites().clear();
        }

        // Supprimer l'entreprise
        entrepriseRepository.deleteById(id);
    }
}
