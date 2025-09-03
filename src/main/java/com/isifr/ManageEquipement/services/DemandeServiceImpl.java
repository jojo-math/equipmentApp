package com.isifr.ManageEquipement.services;

import com.isifr.ManageEquipement.entities.*;
import com.isifr.ManageEquipement.repositories.DemandeRepository;
import com.isifr.ManageEquipement.repositories.EquipementRepository;
import com.isifr.ManageEquipement.repositories.SalleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

@Service
public class DemandeServiceImpl implements DemandeService {
    @Autowired
    private DemandeRepository demandeRepository;
    @Autowired
    private MaterielService materielService;
    @Autowired
    private SalleRepository salleRepository;
    @Autowired
    private EquipementRepository equipementRepository;

    private static final Logger logger = LoggerFactory.getLogger(DemandeServiceImpl.class);

    @Override
    public List<Demande> findAll() {
        return demandeRepository.findAll();
    }

    @Override
    public Optional<Demande> findById(Long id) {
        return demandeRepository.findById(id);
    }

    @Override
    public Demande save(Demande demande, Utilisateur utilisateur) {
        if (demande.getMotif() == null || demande.getMotif().isEmpty()) {
            throw new IllegalArgumentException("Le motif de la demande ne peut pas être vide");
        }
        if (demande.getStatut() == null) {
            demande.setStatut(StatutDemande.EN_ATTENTE);
        } else if (!EnumSet.allOf(StatutDemande.class).contains(demande.getStatut())) {
            throw new IllegalArgumentException("État de la salle invalide : " + demande.getStatut());
        }
        // Utilisateur qui cree la demande
        demande.setUtilisateur(utilisateur);
        //demande.getEquipement().setSalle(demande.getSalle());
        // Initialiser les listes si elles sont null
        if (demande.getMateriels() == null) {
            demande.setMateriels(new ArrayList<>());
        }
        if(demande.getEquipement() == null) {
            demande.setEquipement(new Equipement());
        }

        // Mettre à jour les relations inverses pour materiels
        System.out.println("Matériels associés : " + demande.getMateriels().size());
        for (Materiel materiel : demande.getMateriels()) {
            materiel.setDemande(demande);
        }

        // Mettre à jour l'équipement
        demande.getEquipement().setDemande(demande);

        return demandeRepository.save(demande);
    }

    @Override
    public Demande update(Demande demande) {
        return demandeRepository.save(demande);
    }

    @Override
    public Demande validate(Demande demande) {
        // Vérifier si l'unité existe
        if (!demandeRepository.existsById(demande.getId())) {
            throw new IllegalArgumentException("La demande avec l'ID " + demande.getId() + " n'existe pas");
        }

        // Valider les champs obligatoires
        if (demande.getMotif() == null || demande.getMotif().isEmpty()) {
            throw new IllegalArgumentException("Le motif de la demande ne peut pas être vides");
        }

        // Charger la demande existante
        Demande existingDemande = demandeRepository.findById(demande.getId())
                .orElseThrow(() -> new IllegalArgumentException("Demande introuvable"));

        // Mettre à jour les champs de l'unité
        existingDemande.setMotif(demande.getMotif());
        existingDemande.setDateDebut(demande.getDateDebut());
        existingDemande.setDateFin(demande.getDateFin());
        existingDemande.setDateFin(demande.getDateFin());
        existingDemande.setDateFin(demande.getDateFin());
        existingDemande.setStatut(StatutDemande.VALIDEE);
        existingDemande.setDemandeur(demande.getDemandeur());
        existingDemande.setEquipement(demande.getEquipement());
        demande.getSalle().setEtat(EtatSalle.OCCUPEE);
        existingDemande.setSalle(demande.getSalle());
        existingDemande.setUtilisateur(demande.getUtilisateur());
        for (Materiel materiel : demande.getMateriels()) {
            materiel.setEtat(EtatMateriel.INDISPONIBLE);
            materiel.setSalle(demande.getSalle());
        }

        // Ajouter d'autres champs à mettre à jour si nécessaire

        // Dissocier les anciens matériels non resélectionnés
        if (existingDemande.getMateriels() != null) {
            List<Materiel> demandesToRemove = new ArrayList<>(existingDemande.getMateriels());
            for (Materiel materiel : demandesToRemove) {
                // Vérifier si le materiel n'est pas dans la nouvelle liste
                boolean isReselected = demande.getMateriels() != null &&
                        demande.getMateriels().stream()
                                .anyMatch(u -> u.getId().equals(materiel.getId()));
                if (!isReselected) {
                    materiel.setDemande(null);
                    materielService.update(materiel); // Persister la dissociation
                    existingDemande.getMateriels().remove(materiel); // Supprimer de la liste
                }
            }
        }

        // Associer les nouveaux utilisateurs
        if (demande.getMateriels() != null) {
            for (Materiel materiel : demande.getMateriels()) {
                Optional<Materiel> existingMateriel = materielService.findById(materiel.getId());
                if (existingMateriel.isPresent()) {
                    Materiel managedMateriel = existingMateriel.get();
                    // Ne pas modifier les champs critiques comme username, email, role
                    managedMateriel.setDemande(existingDemande);
                    materielService.update(managedMateriel); // Persister l'association
                    if (!existingDemande.getMateriels().contains(managedMateriel)) {
                        existingDemande.getMateriels().add(managedMateriel); // Maintenir la relation
                    }
                } else {
                    throw new IllegalArgumentException("La demande avec l'ID " + materiel.getId() + " n'existe pas");
                }
            }
        }

        // Sauvegarder la demande mise à jour
        return demandeRepository.save(existingDemande);
    }
    @Override
    @Transactional
    public Demande refuse(Demande demande) {
        // Vérifier si la demande existe
        if (!demandeRepository.existsById(demande.getId())) {
            throw new IllegalArgumentException("La demande avec l'ID " + demande.getId() + " n'existe pas");
        }

        // Charger la demande existante
        Demande existingDemande = demandeRepository.findById(demande.getId())
                .orElseThrow(() -> new IllegalArgumentException("Demande introuvable"));

        // Vérifier que la demande est en attente
        if (existingDemande.getStatut() != StatutDemande.EN_ATTENTE) {
            throw new IllegalStateException("Seules les demandes en attente peuvent être refusées");
        }

        // Mettre à jour uniquement le statut
        existingDemande.setStatut(StatutDemande.REFUSEE);

        // Sauvegarder la demande mise à jour
        return demandeRepository.save(existingDemande);
    }


    @Override
    @Transactional
    public void delete(Long id) {
        // Vérifier si la demande existe
        Demande demande = demandeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("La demande avec l'ID " + id + " n'existe pas"));

        // Vérifier si la demande peut être supprimée (exemple : pas VALIDEE)
        if (demande.getStatut() == StatutDemande.VALIDEE) {
            throw new IllegalStateException("Impossible de supprimer une demande validée");
        }

        logger.info("Suppression de la demande avec ID: {}", id);

        // Dissocier les équipements qui référencent cette demande
        List<Equipement> equipements = equipementRepository.findByDemande(demande);
        for (Equipement equipement : equipements) {
            equipement.setDemande(null); // Dissocier la demande
            equipementRepository.save(equipement); // Persister la modification
        }

        // Dissocier et mettre à jour les matériels
        if (demande.getMateriels() != null) {
            List<Materiel> materiels = new ArrayList<>(demande.getMateriels());
            for (Materiel materiel : materiels) {
                materiel.setDemande(null); // Dissocier la demande
                materiel.setEtat(EtatMateriel.DISPONIBLE); // Rendre le matériel disponible
                materiel.setSalle(null); // Dissocier la salle
                materielService.update(materiel); // Persister les modifications
            }
            demande.getMateriels().clear(); // Vider la liste
        }

        // Libérer la salle et mettre à jour son état
        if (demande.getSalle() != null) {
            Salle salle = demande.getSalle();
            salle.setEtat(EtatSalle.DISPONIBLE); // Rendre la salle disponible
            salleRepository.save(salle); // Persister la modification
            demande.setSalle(null); // Dissocier la salle
        }

        // Dissocier les relations @ManyToOne
        demande.setUtilisateur(null);
        demande.setDemandeur(null);
        demande.setEquipement(null);

        // Supprimer la demande
        demandeRepository.delete(demande);

        logger.info("Demande avec ID: {} supprimée avec succès", id);
    }

    @Override
    public int dmValides() {
        int cpt = 0;
        for(Demande dm : demandeRepository.findAll()) {
            if(dm.getStatut() == StatutDemande.VALIDEE) {
                cpt++;
            }
        }
        return cpt;
    }
}
