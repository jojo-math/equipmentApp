package com.isifr.ManageEquipement.controllers.api;

import com.isifr.ManageEquipement.entities.Entreprise;
import com.isifr.ManageEquipement.entities.Unite;
import com.isifr.ManageEquipement.entities.Utilisateur;
import com.isifr.ManageEquipement.services.EntrepriseService;
import com.isifr.ManageEquipement.services.UniteService;
import com.isifr.ManageEquipement.services.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class UniteRestController {
    @Autowired
    private UniteService uniteService;
    @Autowired
    private UtilisateurService utilisateurService;
    @Autowired
    private EntrepriseService entrepriseService;
    // Endpoint pour les suggestions d'ID d'unités
    @GetMapping("/api/unites/id-suggestions")
    public List<String> getIdSuggestions(@RequestParam(required = false) String term) {
        if (term == null || term.trim().isEmpty()) {
            return uniteService.findAll().stream()
                    .map(Unite::getId)
                    .collect(Collectors.toList());
        }
        return uniteService.findAll().stream()
                .map(Unite::getId)
                .filter(id -> id.contains(term))
                .collect(Collectors.toList());
    }

    // Endpoint pour les suggestions de noms d'unités
    @GetMapping("/api/unites/nom-suggestions")
    public List<String> getNomSuggestions(@RequestParam(required = false) String term) {
        if (term == null || term.trim().isEmpty()) {
            return uniteService.findAll().stream()
                    .map(Unite::getNom)
                    .filter(nom -> nom != null)
                    .collect(Collectors.toList());
        }
        return uniteService.findByNomContainingIgnoreCase(term)
                .stream()
                .map(Unite::getNom)
                .filter(nom -> nom != null)
                .collect(Collectors.toList());
    }

    // Endpoint pour les suggestions de noms d'entreprises
    @GetMapping("/api/entreprises/suggestions")
    public List<String> getEntrepriseSuggestions(@RequestParam(required = false) String term) {
        if (term == null || term.trim().isEmpty()) {
            return entrepriseService.findAll().stream()
                    .map(Entreprise::getNom)
                    .collect(Collectors.toList());
        }
        return entrepriseService.findByNomContainingIgnoreCase(term)
                .stream()
                .map(Entreprise::getNom)
                .collect(Collectors.toList());
    }

    // Endpoint pour les suggestions de usernames
    @GetMapping("/api/utilisateurs/suggestions")
    public List<String> getUsernameSuggestions(@RequestParam(required = false) String term) {
        if (term == null || term.trim().isEmpty()) {
            return utilisateurService.findAll().stream()
                    .map(Utilisateur::getUsername)
                    .collect(Collectors.toList());
        }
        return utilisateurService.findByUsernameContainingIgnoreCase(term)
                .stream()
                .map(Utilisateur::getUsername)
                .collect(Collectors.toList());
    }
}
