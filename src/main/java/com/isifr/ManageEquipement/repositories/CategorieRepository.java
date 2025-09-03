package com.isifr.ManageEquipement.repositories;

import com.isifr.ManageEquipement.entities.Categorie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface CategorieRepository extends JpaRepository<Categorie, Long> {
    Optional<Categorie> findByNom(String nom);
    List<Categorie> findByNomContainingIgnoreCase(String nom);
}
