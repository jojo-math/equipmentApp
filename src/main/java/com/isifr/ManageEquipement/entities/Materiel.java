package com.isifr.ManageEquipement.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import jakarta.persistence.*;
import lombok.ToString;

import java.util.List;

@Data
@Entity
@ToString(exclude = {"categorie", "unite", "salle", "demande"}) // Exclure les relations du toString
public class Materiel {
    @Id
    private String id;
    private String nom;
    private String description;
    @Enumerated(EnumType.STRING)
    private EtatMateriel etat;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categorie_id")
    @JsonBackReference // Ignorer lors de la sérialisation
    private Categorie categorie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unite_id")
    @JsonBackReference
    private Unite unite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salle_id")
    @JsonBackReference
    private Salle salle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "demande_id")
    @JsonBackReference
    private Demande demande;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipement_id")
    @JsonBackReference
    private Equipement equipement;
}