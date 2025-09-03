package com.isifr.ManageEquipement.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Administrateur extends Utilisateur{
    @OneToMany
    private List<Utilisateur> utilisateurs;
    @OneToMany
    private List<Unite> unites;
    @OneToMany
    private List<Salle> salles;
}
