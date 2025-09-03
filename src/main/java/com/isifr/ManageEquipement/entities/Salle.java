package com.isifr.ManageEquipement.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Salle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private EtatSalle etat;
    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Administrateur administrateur;
    @OneToMany
    private List<Demande> demandes;
}
