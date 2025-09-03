package com.isifr.ManageEquipement.services;

import com.isifr.ManageEquipement.entities.Categorie;
import com.isifr.ManageEquipement.entities.Materiel;
import com.isifr.ManageEquipement.entities.Unite;
import com.isifr.ManageEquipement.entities.Utilisateur;
import com.isifr.ManageEquipement.repositories.CategorieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategorieServiceImpl implements CategorieService {
    @Autowired
    private CategorieRepository categorieRepository;
    @Autowired
    private MaterielService materielService;

    @Override
    public List<Categorie> findAll() {
        return categorieRepository.findAll();
    }
    @Override
    public List<String> findByNomContainingIgnoreCase(String term) {
        return categorieRepository.findByNomContainingIgnoreCase(term)
                .stream()
                .map(Categorie::getNom)
                .toList();
    }

    @Override
    public Optional<Categorie> findById(Long id) {
        return categorieRepository.findById(id);
    }

    @Override
    @Transactional
    public Categorie save(Categorie categorie) {
        if (categorie.getNom() == null || categorie.getNom().isEmpty()) {
            throw new IllegalArgumentException("Le nom de la catégorie ne peut pas être vide");
        }
        return categorieRepository.save(categorie);
    }

    @Override
    @Transactional
    public Categorie update(Categorie categorie) {
        if (!categorieRepository.existsById(categorie.getId())) {
            throw new IllegalArgumentException("La catégorie avec l'ID " + categorie.getId() + " n'existe pas");
        }
        if (categorie.getNom() == null || categorie.getNom().isEmpty()) {
            throw new IllegalArgumentException("Le nom de la catégorie ne peut pas être vide");
        }

        Categorie existingCategorie = categorieRepository.findById(categorie.getId())
                .orElseThrow(() -> new IllegalArgumentException("Catégorie introuvable"));

        // Update fields
        existingCategorie.setNom(categorie.getNom());

        // Update Materiel associations
        if (existingCategorie.getMateriels() != null) {
            List<Materiel> materielsToRemove = new ArrayList<>(existingCategorie.getMateriels());
            for (Materiel materiel : materielsToRemove) {
                boolean isReselected = categorie.getMateriels() != null &&
                        categorie.getMateriels().stream()
                                .anyMatch(m -> m.getId().equals(materiel.getId()));
                if (!isReselected) {
                    existingCategorie.removeMateriel(materiel);
                    materielService.update(materiel);
                }
            }
        }

        if (categorie.getMateriels() != null) {
            for (Materiel materiel : categorie.getMateriels()) {
                Optional<Materiel> existingMateriel = materielService.findById(materiel.getId());
                if (existingMateriel.isPresent()) {
                    Materiel managedMateriel = existingMateriel.get();
                    if (!existingCategorie.getMateriels().contains(managedMateriel)) {
                        existingCategorie.addMateriel(managedMateriel);
                        materielService.update(managedMateriel);
                    }
                } else {
                    throw new IllegalArgumentException("Le matériel avec l'ID " + materiel.getId() + " n'existe pas");
                }
            }
        }

        return categorieRepository.save(existingCategorie);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Categorie categorie = categorieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Catégorie introuvable avec l'ID " + id));

        // Dissocier les matériels
        if (categorie.getMateriels() != null) {
            for (Materiel materiel : new ArrayList<>(categorie.getMateriels())) {
                materiel.setCategorie(null);
                categorie.removeMateriel(materiel); // Utilise la méthode helper pour gérer la relation bidirectionnelle
                materielService.delete(materiel.getId());
            }
        }

        // Supprimer la catégorie
        categorieRepository.deleteById(id);
    }
}