package com.isifr.ManageEquipement.controllers.api;

import com.isifr.ManageEquipement.entities.*;
import com.isifr.ManageEquipement.services.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class MaterielRestController {

    @Autowired
    private CategorieService categorieService;
    @Autowired
    private UniteService uniteService;
    @Autowired
    private SalleService salleService;
    @Autowired
    private EquipementService equipementService;
    @Autowired
    private MaterielService materielService;

    // Endpoint pour les suggestions de salles
    @GetMapping("/api/salles/suggestions")
    public List<String> getSalleSuggestions(@RequestParam(required = false) String term) {
        if (term == null || term.trim().isEmpty()) {
            return salleService.findAll().stream()
                    .map(Salle::getNom)
                    .toList();
        }
        return salleService.findByNomContainingIgnoreCase(term);
    }

    // Endpoint pour les suggestions de catégories
    @GetMapping("/api/categories/suggestions")
    public List<String> getCategorieSuggestions(@RequestParam(required = false) String term) {
        if (term == null || term.trim().isEmpty()) {
            return categorieService.findAll().stream()
                    .map(Categorie::getNom)
                    .toList();
        }
        return categorieService.findByNomContainingIgnoreCase(term);
    }

    // Endpoint pour les suggestions d'unités
    @GetMapping("/api/unites/suggestions")
    public List<String> getUniteSuggestions(@RequestParam(required = false) String term) {
        if (term == null || term.trim().isEmpty()) {
            return uniteService.findAll().stream()
                    .map(Unite::getNom)
                    .toList();
        }
        return uniteService.findByNomContainingIgnoreCaseString(term);
    }

    // Endpoint pour les suggestions d'équipements
    @GetMapping("/api/equipements/suggestions")
    public List<String> getEquipementSuggestions(@RequestParam(required = false) String term) {
        if (term == null || term.trim().isEmpty()) {
            return equipementService.findAll().stream()
                    .map(Equipement::getNom)
                    .toList();
        }
        return equipementService.findByNomContainingIgnoreCase(term);
    }

    // Endpoint pour les suggestions d'ID de matériels
    @GetMapping("/api/materiels/id-suggestions")
    public List<String> getIdSuggestions(@RequestParam(required = false) String term) {
        if (term == null || term.trim().isEmpty()) {
            return materielService.findAll().stream()
                    .map(Materiel::getId)
                    .toList();
        }
        return materielService.findByIdContaining(term);
    }

    // Endpoint pour les suggestions de noms de matériels
    @GetMapping("/api/materiels/nom-suggestions")
    public List<String> getNomSuggestions(@RequestParam(required = false) String term) {
        if (term == null || term.trim().isEmpty()) {
            return materielService.findAll().stream()
                    .map(Materiel::getNom)
                    .toList();
        }
        return materielService.findByNomContainingIgnoreCase(term);
    }

    // Endpoint pour les suggestions de descriptions de matériels
    @GetMapping("/api/materiels/description-suggestions")
    public List<String> getDescriptionSuggestions(@RequestParam(required = false) String term) {
        if (term == null || term.trim().isEmpty()) {
            return materielService.findAll().stream()
                    .map(Materiel::getDescription)
                    .filter(desc -> desc != null)
                    .toList();
        }
        return materielService.findByDescriptionContainingIgnoreCase(term);
    }

}
