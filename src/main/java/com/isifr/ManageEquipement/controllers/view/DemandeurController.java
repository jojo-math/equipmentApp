package com.isifr.ManageEquipement.controllers.view;

import com.isifr.ManageEquipement.entities.Administrateur;
import com.isifr.ManageEquipement.entities.Demandeur;
import com.isifr.ManageEquipement.services.DemandeurService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class DemandeurController {

    @Autowired
    private DemandeurService demandeurService;

    @GetMapping("/demandeurs")
    public String gestion(Model model, HttpSession session) {
        Administrateur currentUser = (Administrateur) session.getAttribute("currentUser");
        String userRole = (String) session.getAttribute("userRole");
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("userRole", userRole);
        model.addAttribute("demandeur", new Demandeur());
        model.addAttribute("demandeurs", demandeurService.findAll());
        return "gestion-demandeurs";
    }

    @PostMapping("/demandeurs/save")
    public String save(@ModelAttribute Demandeur demandeur, Model model) {
        try {
            demandeurService.save(demandeur);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("demandeur", demandeur);
            model.addAttribute("demandeurs", demandeurService.findAll());
            return "gestion-demandeurs";
        }
        return "redirect:/demandeurs";
    }

    @PostMapping("/demandeurs/update")
    public String update(@ModelAttribute Demandeur demandeur, Model model) {
        try {
            demandeurService.update(demandeur);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("demandeur", demandeur);
            model.addAttribute("demandeurs", demandeurService.findAll());
            return "gestion-demandeurs";
        }
        return "redirect:/demandeurs";
    }

    @PostMapping("/demandeurs/delete")
    public String delete(@ModelAttribute Demandeur demandeur, Model model) {
        try {
            demandeurService.delete(demandeur.getId());
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("demandeur", new Demandeur());
            model.addAttribute("demandeurs", demandeurService.findAll());
            return "gestion-demandeurs";
        }
        return "redirect:/demandeurs";
    }
}