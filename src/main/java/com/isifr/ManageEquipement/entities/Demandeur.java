package com.isifr.ManageEquipement.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Demandeur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private String email;

    @OneToMany
    private List<Demande> demandes;
}
