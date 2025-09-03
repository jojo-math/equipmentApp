package com.isifr.ManageEquipement.services;

import com.isifr.ManageEquipement.entities.*;
import com.isifr.ManageEquipement.repositories.DemandeRepository;
import com.isifr.ManageEquipement.repositories.UniteRepository;
import com.isifr.ManageEquipement.repositories.UtilisateurRepository;
import com.isifr.ManageEquipement.repositories.AdministrateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UtilisateurServiceImpl implements UtilisateurService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;
    @Autowired
    private AdministrateurRepository administrateurRepository;
    @Autowired
    private DemandeRepository demandeRepository;
    @Autowired
    private UniteRepository uniteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<Utilisateur> findAll() {
        return utilisateurRepository.findAll();
    }

    @Override
    public List<Utilisateur> findByUsernameContainingIgnoreCase(String username) {
        return utilisateurRepository.findByUsernameContainingIgnoreCase(username);
    }

    @Override
    public Optional<Utilisateur> findById(Long id) {
        return utilisateurRepository.findById(id);
    }

    @Override
    @Transactional
    public Utilisateur save(Utilisateur utilisateur, Administrateur administrateur) {
        if (utilisateur.getUsername() == null || utilisateur.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Le nom d'utilisateur ne peut pas être vide");
        }
        if (utilisateur.getPassword() != null && !utilisateur.getPassword().isEmpty()) {
            utilisateur.setPassword(passwordEncoder.encode(utilisateur.getPassword()));
        }
            utilisateur.setRole("USER");
            utilisateur.setAdministrateur(administrateur);
        return utilisateurRepository.save(utilisateur);
    }

    @Override
    public Optional<Utilisateur> findByUsername(String username) {
        return utilisateurRepository.findByUsername(username);
    }

    @Override
    @Transactional
    public Utilisateur update(Utilisateur utilisateur) {
        Optional<Utilisateur> existingUtilisateur = utilisateurRepository.findById(utilisateur.getId());
        if (!existingUtilisateur.isPresent()) {
            throw new IllegalArgumentException("L'utilisateur avec l'ID " + utilisateur.getId() + " n'existe pas");
        }

        Utilisateur managedUtilisateur = existingUtilisateur.get();
        // Mettre à jour uniquement les champs nécessaires
        managedUtilisateur.setUnite(utilisateur.getUnite());
        // Ne pas modifier username, password, role sauf si explicitement requis
        // Exemple : managedUtilisateur.setEmail(utilisateur.getEmail()); // Si nécessaire

        return utilisateurRepository.save(managedUtilisateur);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable avec l'ID " + id));

        // Dissocier de l'unité
        if (utilisateur.getUnite() != null) {
            Unite unite = utilisateur.getUnite();
            unite.getUtilisateurs().remove(utilisateur); // Supprimer l'utilisateur de la liste de l'unité
            utilisateur.setUnite(null);
            uniteRepository.save(unite);
        }

        // Dissocier de l'administrateur
        if (utilisateur.getAdministrateur() != null) {
            Administrateur administrateur = utilisateur.getAdministrateur();
            // Supposons que Administrateur n'a pas de liste d'utilisateurs, sinon ajouter administrateur.getUtilisateurs().remove(utilisateur)
            utilisateur.setAdministrateur(null);
            administrateurRepository.save(administrateur);
        }

        // Dissocier des matériels
        if (utilisateur.getMateriels() != null) {

            utilisateur.getMateriels().clear();
        }

        // Dissocier des équipements
        if (utilisateur.getEquipements() != null) {

            utilisateur.getEquipements().clear();
        }

        // Dissocier des demandes
        if (utilisateur.getDemandes() != null) {
            for (Demande demande : new ArrayList<>(utilisateur.getDemandes())) {
                demande.setUtilisateur(null); // Dissocier la demande de l'utilisateur
                demandeRepository.save(demande);
            }
            utilisateur.getDemandes().clear();
        }

        // Dissocier des demandeurs
        if (utilisateur.getDemandeurs() != null) {

            utilisateur.getDemandeurs().clear();
        }

        // Supprimer l'utilisateur
        utilisateurRepository.deleteById(id);
    }

    @Override
    public boolean isAdmin(Utilisateur utilisateur) {
        return false;
    }

    @Override
    public boolean containsById(long id) {
        return false;
    }
}