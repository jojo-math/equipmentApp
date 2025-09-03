package com.isifr.ManageEquipement.repositories;

import com.isifr.ManageEquipement.entities.Materiel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MaterielRepository extends JpaRepository<Materiel, String>, JpaSpecificationExecutor<Materiel> {
    Optional<Materiel> findByNom(String nom);
    List<Materiel> findAllByNom(String nom);
    List<Materiel> findByNomContainingIgnoreCase(String nom);
    List<Materiel> findByIdContaining(String id);
    List<Materiel> findByDescriptionContainingIgnoreCase(String description);

    @Query(value = """
        SELECT m.*
        FROM materiel m
        LEFT JOIN categorie c ON m.categorie_id = c.id
        LEFT JOIN unite u ON m.unite_id = u.id
        LEFT JOIN salle s ON m.salle_id = s.id
        LEFT JOIN equipement e ON m.equipement_id = e.id
        WHERE (:id IS NULL OR m.id LIKE CONCAT('%', :id, '%'))
        AND (:nom IS NULL OR LOWER(m.nom) LIKE CONCAT('%', LOWER(:nom), '%'))
        AND (:categorieNom IS NULL OR LOWER(c.nom) LIKE CONCAT('%', LOWER(:categorieNom), '%'))
        AND (:description IS NULL OR LOWER(m.description) LIKE CONCAT('%', LOWER(:description), '%'))
        AND (:uniteNom IS NULL OR LOWER(u.nom) LIKE CONCAT('%', LOWER(:uniteNom), '%'))
        AND (:salleNom IS NULL OR LOWER(s.nom) LIKE CONCAT('%', LOWER(:salleNom), '%'))
        AND (:equipementNom IS NULL OR LOWER(e.nom) LIKE CONCAT('%', LOWER(:equipementNom), '%'))
        """, nativeQuery = true)
    List<Materiel> findByCriteria(
            @Param("id") String id,
            @Param("nom") String nom,
            @Param("categorieNom") String categorieNom,
            @Param("description") String description,
            @Param("uniteNom") String uniteNom,
            @Param("salleNom") String salleNom,
            @Param("equipementNom") String equipementNom
    );
}