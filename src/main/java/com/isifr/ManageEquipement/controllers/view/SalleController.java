package com.isifr.ManageEquipement.controllers.view;

import com.isifr.ManageEquipement.entities.*;
import com.isifr.ManageEquipement.services.EquipementService;
import com.isifr.ManageEquipement.services.MaterielService;
import com.isifr.ManageEquipement.services.SalleService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.beans.PropertyEditorSupport;

@Controller
public class SalleController {
    @Autowired
    private EquipementService equipementService;
    @Autowired
    private MaterielService materielService;
    @Autowired
    private SalleService salleService;
    @Autowired
    private EquipementConverter equipementConverter;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Equipement.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                Equipement equipement = equipementConverter.convert(text);
                setValue(equipement);
            }
        });
    }


        @GetMapping("/salles")
    public String salles(Model model, HttpSession session) {
        model.addAttribute("salle", new Salle());
        model.addAttribute("salles", salleService.findAll());
        Administrateur currentUser = (Administrateur) session.getAttribute("currentUser");
        String userRole = (String) session.getAttribute("userRole");
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("userRole", userRole);
        return "gestion-salles";
    }

    @PostMapping("/salles/save")
    public String save(@ModelAttribute Salle salle, Model model, HttpSession session) {
        Administrateur currentUser = (Administrateur) session.getAttribute("currentUser");
        String userRole = (String) session.getAttribute("userRole");
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("userRole", userRole);
        salleService.save(salle, currentUser);
        System.out.println(currentUser.getId() + " " + currentUser.getUsername());
        System.out.println("Salle soumise : " + salle);
        return "redirect:/salles";
    }
    @PostMapping("/salles/update")
    public String update(@ModelAttribute Salle salle, Model model) {
        salleService.update(salle);
        return "redirect:/salles";
    }
    @PostMapping("/salles/delete")
    public String delete(@RequestParam Long id, Model model) {
        try {
            Salle salle = salleService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Demande introuvable avec l'ID " + id));
            salleService.delete(salle.getId());
            return "redirect:/salles";
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/salles?error";
        }
    }
}
