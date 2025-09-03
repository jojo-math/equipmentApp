package com.isifr.ManageEquipement.services;

import com.isifr.ManageEquipement.entities.Demandeur;
import com.isifr.ManageEquipement.repositories.DemandeurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DemandeurServiceImpl implements DemandeurService {

    @Autowired
    private DemandeurRepository demandeurRepository;

    @Override
    public List<Demandeur> findAll() {
        return demandeurRepository.findAll();
    }

    @Override
    public Demandeur findById(Long id) {
        return demandeurRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Demandeur non trouvé"));
    }

    @Override
    public Demandeur save(Demandeur demandeur) {
        if (demandeur.getNom() == null || demandeur.getNom().isEmpty()) {
            throw new IllegalArgumentException("Le nom du demandeur ne peut pas être vide");
        }

        return demandeurRepository.save(demandeur);
    }

    @Override
    public Demandeur update(Demandeur demandeur) {
        if (!demandeurRepository.existsById(demandeur.getId())) {
            throw new IllegalArgumentException("Le demandeur avec l'ID " + demandeur.getId() + " n'existe pas");
        }
        return demandeurRepository.save(demandeur);
    }

    @Override
    public void delete(Long id) {
        if (!demandeurRepository.existsById(id)) {
            throw new IllegalArgumentException("Le demandeur avec l'ID " + id + " n'existe pas");
        }
        demandeurRepository.deleteById(id);
    }
}