package com.isifr.ManageEquipement.services;

import com.isifr.ManageEquipement.entities.*;
import com.isifr.ManageEquipement.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EquipementServiceImpl implements EquipementService {

    @Autowired
    private EquipementRepository equipementRepository;
    @Autowired
    private SalleRepository salleRepository;
    @Autowired
    private DemandeRepository demandeRepository;
    @Autowired
    private MaterielRepository materielRepository;

    @Override
    public List<Equipement> findAll() {
        return equipementRepository.findAll();
    }
    @Override
    public List<String> findByNomContainingIgnoreCase(String term) {
        return equipementRepository.findByNomContainingIgnoreCase(term)
                .stream()
                .map(Equipement::getNom)
                .toList();
    }

    @Override
    public Optional<Equipement> findById(Long id) {
        return equipementRepository.findById(id);
    }

    @Override
    public Equipement save(Equipement equipement, Unite unite) {
        if (equipement.getNom() == null || equipement.getNom().isEmpty()) {
            throw new IllegalArgumentException("Le nom de l'équipement ne peut pas être vide");
        }
        equipement.setUnite(unite);
        // Initialiser la liste des matériels si elle est null
        if (equipement.getMateriels() == null) {
            equipement.setMateriels(new ArrayList<>());
        }
        // Définir l'unité à partir du premier matériel uniquement si la liste n'est pas vide
        if (!equipement.getMateriels().isEmpty()) {
            equipement.setUnite(equipement.getMateriels().get(0).getUnite());
            for (Materiel materiel : equipement.getMateriels()) {
                materiel.setEquipement(equipement);
            }
        }
        return equipementRepository.save(equipement);
    }

    @Override
    public Equipement update(Equipement equipement) {
        if (!equipementRepository.existsById(equipement.getId())) {
            throw new IllegalArgumentException("L'équipement avec l'ID " + equipement.getId() + " n'existe pas");
        }
        // Conserver l'unité existante si la liste des matériels est vide ou null
        if (equipement.getMateriels() != null && !equipement.getMateriels().isEmpty()) {
            equipement.setUnite(equipement.getMateriels().get(0).getUnite());
            for (Materiel materiel : equipement.getMateriels()) {
                materiel.setEquipement(equipement);
            }
        }
        return equipementRepository.save(equipement);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Equipement equipement = equipementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Équipement introuvable avec l'ID " + id));

        // Dissocier des entités liées
        if (equipement.getSalle() != null) {
            Salle salle = equipement.getSalle();
            equipement.setSalle(null);
            salleRepository.save(salle);
        }

        if (equipement.getUnite() != null) {
            equipement.setUnite(null); // Pas besoin de sauvegarder Unite si pas de liste d'équipements
        }

        if (equipement.getDemande() != null) {
            Demande demande = equipement.getDemande();
            demande.setEquipement(null); // Si Demande a une référence à Equipement
            equipement.setDemande(null);
            demandeRepository.save(demande);
        }

        // Dissocier les matériels
        if (equipement.getMateriels() != null) {
            for (Materiel materiel : equipement.getMateriels()) {
                materiel.setEquipement(null);
                materielRepository.save(materiel);
            }
            equipement.getMateriels().clear();
        }

        // Supprimer l'équipement
        equipementRepository.deleteById(id);
    }
}