package com.isifr.ManageEquipement.controllers.view;

import com.isifr.ManageEquipement.entities.Administrateur;
import com.isifr.ManageEquipement.entities.Categorie;
import com.isifr.ManageEquipement.services.CategorieService;
import com.isifr.ManageEquipement.services.MaterielService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CategorieController {
    @Autowired
    private CategorieService categorieService;
    @Autowired
    private MaterielService materielService;

    @GetMapping("/categories")
    public String categories(Model model, HttpSession session) {
        Administrateur currentUser = (Administrateur) session.getAttribute("currentUser");
        String userRole = (String) session.getAttribute("userRole");
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("userRole", userRole);
        model.addAttribute("categorie", new Categorie());
        model.addAttribute("categories", categorieService.findAll());
        model.addAttribute("materiels", materielService.findAll());
        return "gestion-categories";
    }

    @PostMapping("/categories/save")
    public String save(@ModelAttribute Categorie categorie, Model model) {
        try {
            categorieService.save(categorie);
            return "redirect:/categories";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("categorie", categorie);
            model.addAttribute("categories", categorieService.findAll());
            model.addAttribute("materiels", materielService.findAll());
            return "gestion-categories";
        }
    }

    @PostMapping("/categories/update")
    public String update(@ModelAttribute Categorie categorie, Model model) {
        try {
            categorieService.update(categorie);
            return "redirect:/categories";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("categorie", categorie);
            model.addAttribute("categories", categorieService.findAll());
            model.addAttribute("materiels", materielService.findAll());
            return "gestion-categories";
        }
    }

    @PostMapping("/categories/delete")
    public String delete(@RequestParam Long id, Model model) {
        try {
            categorieService.delete(id);
            return "redirect:/categories";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("categorie", new Categorie());
            model.addAttribute("categories", categorieService.findAll());
            model.addAttribute("materiels", materielService.findAll());
            return "gestion-categories";
        }
    }
}