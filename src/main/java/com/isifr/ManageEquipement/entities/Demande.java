package com.isifr.ManageEquipement.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@ToString(exclude = {"materiels"}) // Exclure les relations bidirectionnelles
public class Demande {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String motif;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    @Enumerated(EnumType.STRING)
    private StatutDemande statut;
    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;
    @ManyToOne
    @JoinColumn(name = "demandeur_id")
    private Demandeur demandeur;
    @OneToMany(fetch = FetchType.LAZY)
    private List<Materiel> materiels;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salle_id")
    private Salle salle;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipement_id")
    private Equipement equipement;


}
