package com.isifr.ManageEquipement.controllers.view;

import com.isifr.ManageEquipement.entities.Entreprise;
import com.isifr.ManageEquipement.entities.Utilisateur;
import com.isifr.ManageEquipement.services.EntrepriseService;
import com.isifr.ManageEquipement.services.UniteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class EntrepriseController {
    @Autowired
    private EntrepriseService entrepriseService;
    @Autowired
    private UniteService uniteService;

    @GetMapping("/entreprises")
    public String entreprises(Model model, HttpSession session) {
        model.addAttribute("entreprises", entrepriseService.findAll());
        model.addAttribute("unites", uniteService.findAll());
        model.addAttribute("entreprise", new Entreprise());
        Utilisateur currentUser = (Utilisateur) session.getAttribute("currentUser");
        String userRole = (String) session.getAttribute("userRole");
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("userRole", userRole);
        return "gestion-entreprises";
    }
    @PostMapping("/entreprises/save")
    public String saveEntreprise(@ModelAttribute("entreprise") Entreprise entreprise) {
        entrepriseService.save(entreprise);
        return "redirect:/entreprises";
    }
    @PostMapping("/entreprises/update")
    public String updateEntreprise(@ModelAttribute Entreprise entreprise, Model model) {
        entrepriseService.update(entreprise);
        return "redirect:/entreprises";
    }
    @PostMapping("/entreprises/delete")
    public String deleteEntreprise(@ModelAttribute Entreprise entreprise, Model model) {
        entrepriseService.delete(entreprise.getId());
        return "redirect:/entreprises";
    }
}
