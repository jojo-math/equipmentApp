package com.isifr.ManageEquipement.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Unite {
    @Id
    private String id;
    private String nom;
    @ManyToOne
    @JoinColumn(name = "entreprise_id")
    private Entreprise entreprise;
    @OneToMany(mappedBy = "unite", cascade = CascadeType.ALL)
    private List<Utilisateur> utilisateurs;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Administrateur administrateur;
}