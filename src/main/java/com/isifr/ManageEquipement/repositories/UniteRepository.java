package com.isifr.ManageEquipement.repositories;

import com.isifr.ManageEquipement.entities.Unite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UniteRepository extends JpaRepository<Unite, String> {
    List<Unite> findByNomContainingIgnoreCase(String nom);
    @Query(value = """
        SELECT u.*
        FROM unite u
        LEFT JOIN entreprise e ON u.entreprise_id = e.id
        LEFT JOIN utilisateur ut ON ut.unite_id = u.id
        WHERE (:id IS NULL OR u.id LIKE CONCAT('%', :id, '%'))
        AND (:nom IS NULL OR LOWER(u.nom) LIKE CONCAT('%', LOWER(:nom), '%'))
        AND (:entrepriseNom IS NULL OR LOWER(e.nom) LIKE CONCAT('%', LOWER(:entrepriseNom), '%'))
        AND (:username IS NULL OR LOWER(ut.username) LIKE CONCAT('%', LOWER(:username), '%'))
        GROUP BY u.id, u.nom, u.entreprise_id, u.admin_id
        """, nativeQuery = true)
    List<Unite> findByCriteria(
            @Param("id") String id,
            @Param("nom") String nom,
            @Param("entrepriseNom") String entrepriseNom,
            @Param("username") String username
    );
}
