package com.isifr.ManageEquipement.services;

import com.isifr.ManageEquipement.entities.*;
import com.isifr.ManageEquipement.repositories.AdministrateurRepository;
import com.isifr.ManageEquipement.repositories.DemandeRepository;
import com.isifr.ManageEquipement.repositories.SalleRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

@Service
public class SalleServiceImpl implements SalleService {
    @Autowired
    private SalleRepository salleRepository;
    @Autowired
    private DemandeRepository demandeRepository;
    @Autowired
    private DemandeService demandeService;
    @Autowired
    private AdministrateurRepository administrateurRepository;
    @Override
    public Salle save(Salle salle, Administrateur administrateur) {
        if (salle.getNom() == null || salle.getNom().isEmpty()) {
            throw new IllegalArgumentException("Le nom de la salle ne peut pas être vide");
        }
        if (salle.getEtat() == null) {
            salle.setEtat(EtatSalle.DISPONIBLE);
        } else if (!EnumSet.allOf(EtatSalle.class).contains(salle.getEtat())) {
            throw new IllegalArgumentException("État de la salle invalide : " + salle.getEtat());
        }
        salle.setAdministrateur(administrateur);
//        Salle savedSalle = salleRepository.save(salle);
//
//        // Forcer l'initialisation de la collection utilisateurs
//        Hibernate.initialize(savedSalle.getAdministrateur().getUtilisateurs());

        return salleRepository.save(salle);
    }

    @Override
    public Optional<Salle> findById(Long id) {
        return salleRepository.findById(id);
    }

    @Override
    public List<Salle> findAll() {
        return salleRepository.findAll();
    }
    @Override
    public List<String> findByNomContainingIgnoreCase(String term) {
        return salleRepository.findByNomContainingIgnoreCase(term)
                .stream()
                .map(Salle::getNom)
                .toList();
    }

    @Override
    public List<Salle> findByMateriel(Materiel materiel) {
        return List.of();
    }

    @Override
    public List<Salle> findByEquipement(Equipement equipment) {
        return List.of();
    }

    @Override
    public Salle update(Salle salle) {
        if (!salleRepository.existsById(salle.getId())) {
            throw new IllegalArgumentException("La salle avec l'ID " + salle.getId() + " n'existe pas");
        }
        return salleRepository.save(salle);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Salle salle = salleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Salle introuvable avec l'ID " + id));

        // Dissocier de l'administrateur
        if (salle.getAdministrateur() != null) {
            Administrateur administrateur = salle.getAdministrateur();
            salle.setAdministrateur(null);
            administrateurRepository.save(administrateur);
        }

        // Charger et dissocier toutes les demandes associées
        List<Demande> demandes = demandeRepository.findBySalle(salle);
        for (Demande demande : demandes) {
            demande.setSalle(null);
            demandeRepository.save(demande);
        }

        // Vider la collection pour éviter les incohérences
        if (salle.getDemandes() != null) {
            salle.getDemandes().clear();
        }

        // Supprimer la salle
        salleRepository.deleteById(id);
    }

    @Override
    public int ndSallesEquip() {
        int cpt = 0;
        for(Salle salle : salleRepository.findAll()) {
            if(salle.getEtat().equals(EtatSalle.OCCUPEE)) {
                cpt++;
            }
        }
        return cpt;
    }
}
