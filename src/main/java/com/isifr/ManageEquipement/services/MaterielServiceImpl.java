package com.isifr.ManageEquipement.services;

import com.isifr.ManageEquipement.entities.*;
import com.isifr.ManageEquipement.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.isifr.ManageEquipement.entities.Materiel; // Votre entité Materiel
import com.isifr.ManageEquipement.repositories.MaterielRepository; // Votre repository
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

@Service
public class MaterielServiceImpl implements MaterielService {
    @Autowired
    private MaterielRepository materielRepository;
    @Autowired
    private EquipementRepository equipementRepository;
    @Autowired
    private UniteRepository uniteRepository;
    @Autowired
    private DemandeRepository demandeRepository;
    @Autowired
    private CategorieRepository categorieRepository;

    @Override
    public List<Materiel> findAll() {
        return materielRepository.findAll();
    }
    @Override
    public List<String> findByIdContaining(String term) {
        return materielRepository.findByIdContaining(term)
                .stream()
                .map(Materiel::getId)
                .toList();
    }
    @Override
    public List<String> findByNomContainingIgnoreCase(String term) {
        return materielRepository.findByNomContainingIgnoreCase(term)
                .stream()
                .map(Materiel::getNom)
                .toList();
    }
    @Override
    public List<String> findByDescriptionContainingIgnoreCase(String term) {
        return materielRepository.findByDescriptionContainingIgnoreCase(term)
                .stream()
                .map(Materiel::getDescription)
                .filter(desc -> desc != null)
                .toList();
    }
    @Override
    public List<Materiel> findByCriteria(String id, String nom, String categorieNom, String description,
                                         String uniteNom, String salleNom, String equipementNom) {
        return materielRepository.findByCriteria(
                id != null && !id.trim().isEmpty() ? id : null,
                nom != null && !nom.trim().isEmpty() ? nom : null,
                categorieNom != null && !categorieNom.trim().isEmpty() ? categorieNom : null,
                description != null && !description.trim().isEmpty() ? description : null,
                uniteNom != null && !uniteNom.trim().isEmpty() ? uniteNom : null,
                salleNom != null && !salleNom.trim().isEmpty() ? salleNom : null,
                equipementNom != null && !equipementNom.trim().isEmpty() ? equipementNom : null
        );
    }

    @Override
    public Optional<Materiel> findById(String id) {
        return materielRepository.findById(id);
    }

    @Override
    @Transactional
    public void save(Materiel materiel) {
        if (materiel.getNom() == null || materiel.getNom().isEmpty()) {
            throw new IllegalArgumentException("Le nom du matériel ne peut pas être vide");
        }
        if (materiel.getCategorie() == null || !categorieRepository.existsById(materiel.getCategorie().getId())) {
            throw new IllegalArgumentException("La catégorie spécifiée est invalide");
        }

        // Generate ID
        int n = 0;
        for (Materiel m : materielRepository.findAll()) {
            if (m.getNom().substring(0, 1).equalsIgnoreCase(materiel.getNom().substring(0, 1))) {
                n++;
            }
        }
        String year = String.valueOf(java.time.Year.now().getValue());
        String prefix = materiel.getNom().substring(0, 1).toUpperCase();
        materiel.setId(String.format("%s-%s-%04d", prefix, year, (n + 1)));
        materiel.setEtat(EtatMateriel.DISPONIBLE);
        materiel.setUnite(materiel.getUnite());

        // Save the Materiel (Categorie relationship is handled by JPA)
        materielRepository.save(materiel);
    }

    @Override
    @Transactional
    public void saveAll(Materiel materiel, Integer quantite) {
        if (materiel.getNom() == null || materiel.getNom().isEmpty()) {
            throw new IllegalArgumentException("Le nom du matériel ne peut pas être vide");
        }
        if (materiel.getCategorie() == null || !categorieRepository.existsById(materiel.getCategorie().getId())) {
            throw new IllegalArgumentException("La catégorie spécifiée est invalide");
        }
        if (quantite <= 0) {
            throw new IllegalArgumentException("La quantité doit être supérieure à 0");
        }

        // Calculate counter
        int n = 0;
        for (Materiel m : materielRepository.findAll()) {
            if (m.getNom().substring(0, 1).equalsIgnoreCase(materiel.getNom().substring(0, 1))) {
                n++;
            }
        }

        String year = String.valueOf(java.time.Year.now().getValue());
        String prefix = materiel.getNom().substring(0, 1).toUpperCase();

        // Save 'quantite' Materiel instances
        for (int i = 0; i < quantite; i++) {
            Materiel newMateriel = new Materiel();
            newMateriel.setNom(materiel.getNom());
            newMateriel.setDescription(materiel.getDescription());
            newMateriel.setEtat(EtatMateriel.DISPONIBLE);
            newMateriel.setCategorie(materiel.getCategorie());
            newMateriel.setEquipement(null);
            newMateriel.setId(String.format("%s-%s-%04d", prefix, year, (n + 1 + i)));
            newMateriel.setUnite(materiel.getUnite());

            // Save the new Materiel (Categorie relationship is handled by JPA)
            materielRepository.save(newMateriel);
        }
    }

    @Override
    @Transactional
    public Materiel update(Materiel materiel) {
        Optional<Materiel> existingMateriel = materielRepository.findById(materiel.getId());
        if (!existingMateriel.isPresent()) {
            throw new IllegalArgumentException("Le matériel avec l'ID " + materiel.getId() + " n'existe pas");
        }
        Materiel managedMateriel = existingMateriel.get();

        // Update fields
        managedMateriel.setNom(materiel.getNom() != null ? materiel.getNom() : managedMateriel.getNom());
        managedMateriel.setDescription(materiel.getDescription() != null ? materiel.getDescription() : managedMateriel.getDescription());
        managedMateriel.setEtat(materiel.getEtat() != null ? materiel.getEtat() : EtatMateriel.DISPONIBLE);
        if (materiel.getCategorie() != null && categorieRepository.existsById(materiel.getCategorie().getId())) {
            managedMateriel.setCategorie(materiel.getCategorie());
        }

        return materielRepository.save(managedMateriel);
    }

    @Override
    @Transactional
    public void delete(String id) {
        Materiel materiel = materielRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Matériel introuvable avec l'ID " + id));

        // Remove from related entities
        if (materiel.getEquipement() != null) {
            Equipement equipement = materiel.getEquipement();
            equipement.getMateriels().remove(materiel);
            equipementRepository.save(equipement);
        }
        if (materiel.getDemande() != null) {
            Demande demande = materiel.getDemande();
            demande.getMateriels().remove(materiel);
            demandeRepository.save(demande);
        }

        materielRepository.deleteById(id);
    }
}