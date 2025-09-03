package com.isifr.ManageEquipement.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@Entity
@ToString(exclude = {"salle", "materiels"}) // Exclure les relations bidirectionnelles
public class Equipement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    @ManyToOne
    @JoinColumn(name = "unite_id")
    private Unite unite;
    @ManyToOne
    @JoinColumn(name = "salle_id")
    private Salle salle;
    @OneToMany
    private List<Materiel> materiels;
    @OneToOne
    @JoinColumn(name = "demande_id")
    private Demande demande;
}
