package com.isifr.ManageEquipement.services;

import com.isifr.ManageEquipement.entities.*;
import com.isifr.ManageEquipement.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class UniteServiceImpl implements UniteService {

    @Autowired
    private UniteRepository uniteRepository;

    @Autowired
    private UtilisateurService utilisateurService;
    @Autowired
    private EntrepriseRepository entrepriseRepository;
    @Autowired
    private UtilisateurRepository utilisateurRepository;
    @Autowired
    private MaterielService materielService;

    private static final AtomicInteger counter = new AtomicInteger(0);
    @Autowired
    private AdministrateurRepository administrateurRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<Unite> findAll() {
        return uniteRepository.findAll();
    }
    @Override
    public List<Unite> findByNomContainingIgnoreCase(String term) {
        return uniteRepository.findByNomContainingIgnoreCase(term);
    }
    @Override
    public List<String> findByNomContainingIgnoreCaseString(String term) {
        return uniteRepository.findByNomContainingIgnoreCase(term)
                .stream()
                .map(Unite::getNom)
                .toList();
    }

    @Override
    public List<Unite> findByCriteria(String id, String nom, String entrepriseNom, String username) {
        return uniteRepository.findByCriteria(
                id != null && !id.trim().isEmpty() ? id : null,
                nom != null && !nom.trim().isEmpty() ? nom : null,
                entrepriseNom != null && !entrepriseNom.trim().isEmpty() ? entrepriseNom : null,
                username != null && !username.trim().isEmpty() ? username : null
        );
    }

    @Override
    public Unite findById(String id) {
        return uniteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Unité non trouvée"));
    }

    @Override
    @Transactional
    public Unite save(Unite unite, Administrateur administrateur) {
        if (unite.getNom() == null || unite.getNom().isEmpty() ||
                unite.getEntreprise() == null || unite.getEntreprise().getNom().isEmpty()) {
            throw new IllegalArgumentException("Le nom et le nom de l'entreprise ne peuvent pas être vides");
        }
        // Générer l'ID
        generateId(unite);
        unite.setAdministrateur(administrateur);
        // Sauvegarder l'unité d'abord
        Unite savedUnite = uniteRepository.save(unite);
        // Associer les utilisateurs à l'unité
        for(Utilisateur utilisateur : unite.getUtilisateurs()) {
            utilisateur.setUnite(savedUnite);
            utilisateurService.update(utilisateur);
        }
        return savedUnite;
    }

    @Override
    @Transactional
    public Unite update(Unite unite) {
        // Vérifier si l'unité existe
        if (!uniteRepository.existsById(unite.getId())) {
            throw new IllegalArgumentException("L'unité avec l'ID " + unite.getId() + " n'existe pas");
        }

        // Valider les champs obligatoires
        if (unite.getNom() == null || unite.getNom().isEmpty() ||
                unite.getEntreprise() == null || unite.getEntreprise().getNom().isEmpty()) {
            throw new IllegalArgumentException("Le nom et le nom de l'entreprise ne peuvent pas être vides");
        }

        // Charger l'unité existante
        Unite existingUnite = uniteRepository.findById(unite.getId())
                .orElseThrow(() -> new IllegalArgumentException("Unité introuvable"));

        // Mettre à jour les champs de l'unité
        existingUnite.setNom(unite.getNom());
        existingUnite.setEntreprise(unite.getEntreprise());
        // Ajouter d'autres champs à mettre à jour si nécessaire

        // Dissocier les anciens utilisateurs non resélectionnés
        if (existingUnite.getUtilisateurs() != null) {
            List<Utilisateur> utilisateursToRemove = new ArrayList<>(existingUnite.getUtilisateurs());
            for (Utilisateur utilisateur : utilisateursToRemove) {
                // Vérifier si l'utilisateur n'est pas dans la nouvelle liste
                boolean isReselected = unite.getUtilisateurs() != null &&
                        unite.getUtilisateurs().stream()
                                .anyMatch(u -> u.getId().equals(utilisateur.getId()));
                if (!isReselected) {
                    utilisateur.setUnite(null);
                    utilisateurService.update(utilisateur); // Persister la dissociation
                    existingUnite.getUtilisateurs().remove(utilisateur); // Supprimer de la liste
                }
            }
        }

        // Associer les nouveaux utilisateurs
        if (unite.getUtilisateurs() != null) {
            for (Utilisateur utilisateur : unite.getUtilisateurs()) {
                Optional<Utilisateur> existingUtilisateur = utilisateurService.findById(utilisateur.getId());
                if (existingUtilisateur.isPresent()) {
                    Utilisateur managedUtilisateur = existingUtilisateur.get();
                    // Ne pas modifier les champs critiques comme username, email, role
                    managedUtilisateur.setUnite(existingUnite);
                    utilisateurService.update(managedUtilisateur); // Persister l'association
                    if (!existingUnite.getUtilisateurs().contains(managedUtilisateur)) {
                        existingUnite.getUtilisateurs().add(managedUtilisateur); // Maintenir la relation
                    }
                } else {
                    throw new IllegalArgumentException("L'utilisateur avec l'ID " + utilisateur.getId() + " n'existe pas");
                }
            }
        }

        // Sauvegarder l'unité mise à jour
        return uniteRepository.save(existingUnite);
    }

    @Override
    @Transactional
    public void delete(String id) {
        Unite unite = uniteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Unité introuvable avec l'ID " + id));

        // Dissocier de l'entreprise
        if (unite.getEntreprise() != null) {
            Entreprise entreprise = unite.getEntreprise();
            entreprise.getUnites().remove(unite); // Supprimer l'unité de la liste de l'entreprise
            unite.setEntreprise(null);
            entrepriseRepository.save(entreprise);
        }

        // Dissocier des utilisateurs
        if (unite.getUtilisateurs() != null) {
            for (Utilisateur utilisateur : new ArrayList<>(unite.getUtilisateurs())) {
                utilisateur.setUnite(null); // Dissocier l'utilisateur de l'unité
                utilisateurRepository.save(utilisateur);
            }
            unite.getUtilisateurs().clear();
        }
        for(Materiel materiel : materielService.findAll()) {
            if(materiel.getUnite() == unite){
                materiel.setUnite(null);
                materielService.update(materiel);
            }
        }

        // Dissocier de l'administrateur
        if (unite.getAdministrateur() != null) {
            Administrateur administrateur = unite.getAdministrateur();
            // Supposons qu'Administrateur n'a pas de liste d'unités, sinon ajouter administrateur.getUnites().remove(unite)
            unite.setAdministrateur(null);
            administrateurRepository.save(administrateur);
        }

        // Supprimer l'unité
        uniteRepository.deleteById(id);
    }

    private void generateId(Unite unite) {
        String year = String.valueOf(java.time.Year.now().getValue());
        String prefix = unite.getEntreprise().getNom().substring(0, Math.min(3, unite.getEntreprise().getNom().length())).toUpperCase() + '-' +
                unite.getNom().substring(0, 1).toUpperCase();
        int nextNumber = counter.incrementAndGet();
        unite.setId(String.format("%s-%s-%04d", prefix, year, nextNumber));
    }
}