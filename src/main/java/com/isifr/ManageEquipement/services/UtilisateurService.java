package com.isifr.ManageEquipement.services;

import com.isifr.ManageEquipement.entities.Administrateur;
import com.isifr.ManageEquipement.entities.Utilisateur;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface UtilisateurService {
    List<Utilisateur> findAll();
    Optional<Utilisateur> findByUsername(String username);
    List<Utilisateur> findByUsernameContainingIgnoreCase(String username);
    Optional<Utilisateur> findById(Long id);

    Utilisateur save(Utilisateur utilisateur, Administrateur administrateur);
    Utilisateur update(Utilisateur utilisateur);

    @Transactional
    void delete(Long id);

    boolean isAdmin(Utilisateur utilisateur);
    boolean containsById(long id);
}