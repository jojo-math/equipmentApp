package com.isifr.ManageEquipement.repositories;

import com.isifr.ManageEquipement.entities.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByUsername(String username);
    List<Utilisateur> findByUsernameContainingIgnoreCase(String username);
}
