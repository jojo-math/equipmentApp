package com.isifr.ManageEquipement.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Utilisateur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private String email;
    private String role;

    @ManyToOne
    @JoinColumn(name = "unite_id")
    private Unite unite;
    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Administrateur administrateur;

    @OneToMany
    private List<Materiel> materiels;

    @OneToMany
    private List<Equipement> equipements;

    @OneToMany
    private List<Demande> demandes;

    @OneToMany
    private List<Demandeur> demandeurs;
}