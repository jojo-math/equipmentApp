package com.isifr.ManageEquipement.controllers.view;

import com.isifr.ManageEquipement.entities.Administrateur;
import com.isifr.ManageEquipement.entities.Unite;
import com.isifr.ManageEquipement.services.EntrepriseService;
import com.isifr.ManageEquipement.services.UniteService;
import com.isifr.ManageEquipement.services.UtilisateurService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class UniteController {

    @Autowired
    private UniteService uniteService;

    @Autowired
    private UtilisateurService utilisateurService;
    @Autowired
    private EntrepriseService entrepriseService;

    @GetMapping("/unites")
    public String gestion(Model model, HttpSession session,
                          @RequestParam(required = false) String id,
                          @RequestParam(required = false) String nom,
                          @RequestParam(required = false) String entrepriseNom,
                          @RequestParam(required = false) String username) {
        try {
            Administrateur currentUser = (Administrateur) session.getAttribute("currentUser");
            String userRole = (String) session.getAttribute("userRole");
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("userRole", userRole);
            model.addAttribute("unite", new Unite());
            model.addAttribute("utilisateurs", utilisateurService.findAll());
            model.addAttribute("entreprises", entrepriseService.findAll());

            // Appliquer les filtres ou retourner toutes les unités si aucun filtre
            List<Unite> unites = uniteService.findByCriteria(id, nom, entrepriseNom, username);
            model.addAttribute("unites", unites);

            // Ajouter les valeurs des filtres pour les réafficher
            model.addAttribute("filterId", id);
            model.addAttribute("filterNom", nom);
            model.addAttribute("filterEntrepriseNom", entrepriseNom);
            model.addAttribute("filterUsername", username);

            return "gestion-unites";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors du chargement des unités : " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/unites/save")
    public String save(@ModelAttribute Unite unite, Model model, HttpSession session) {
        Administrateur currentUser = (Administrateur) session.getAttribute("currentUser");
        String userRole = (String) session.getAttribute("userRole");
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("userRole", userRole);
        try {
            uniteService.save(unite, currentUser);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("unite", unite);
            model.addAttribute("unites", uniteService.findAll());
            model.addAttribute("utilisateurs", utilisateurService.findAll());
            return "gestion-unites";
        }
        System.out.println(currentUser.getId() + " " + currentUser.getUsername());
        return "redirect:/unites";
    }

    @PostMapping("/unites/update")
    public String update(@ModelAttribute Unite unite, Model model) {
        try {
            uniteService.update(unite);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("unite", unite);
            model.addAttribute("unites", uniteService.findAll());
            model.addAttribute("utilisateurs", utilisateurService.findAll());
            return "gestion-unites";
        }
        return "redirect:/unites";
    }

    @PostMapping("/unites/delete")
    public String delete(@ModelAttribute Unite unite, Model model) {
        try {
            uniteService.delete(unite.getId());
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("unite", new Unite());
            model.addAttribute("unites", uniteService.findAll());
            model.addAttribute("utilisateurs", utilisateurService.findAll());
            return "gestion-unites";
        }
        return "redirect:/unites";
    }
}