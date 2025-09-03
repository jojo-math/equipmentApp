package com.isifr.ManageEquipement.controllers.view;

import com.isifr.ManageEquipement.entities.Administrateur;
import com.isifr.ManageEquipement.entities.Demande;
import com.isifr.ManageEquipement.entities.Utilisateur;
import com.isifr.ManageEquipement.services.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UtilisateurController {

    @Autowired
    private UtilisateurService utilisateurService;

    @Autowired
    private UniteService uniteService;
    @Autowired
    private DemandeService demandeService;
    @Autowired
    private MaterielService materielService;
    @Autowired
    private SalleService salleService;
    @Autowired
    private EquipementService equipementService;

    @GetMapping("/utilisateurs/dashboardAdmin")
    public String dashboardAdmin(Model model, HttpSession session) {
        model.addAttribute("utilisateurs", utilisateurService.findAll());
        model.addAttribute("demandes", demandeService.findAll());
        model.addAttribute("materiels",materielService.findAll());
        model.addAttribute("nbreDmVal",demandeService.dmValides());
        model.addAttribute("salles",salleService.findAll());
        model.addAttribute("equipements",equipementService.findAll());
        model.addAttribute("unites",uniteService.findAll());
        model.addAttribute("ndSallesEquip",salleService.ndSallesEquip());
//        model.addAttribute("demande", new Demande());
        Administrateur currentUser = (Administrateur) session.getAttribute("currentUser");
        String userRole = (String) session.getAttribute("userRole");
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("userRole", userRole);
        return "dashboardAdmin0";
    }
    @GetMapping("/utilisateurs/dashboardUser")
    public String dashboardUser(Model model, HttpSession session) {
        model.addAttribute("utilisateurs", utilisateurService.findAll());
        model.addAttribute("salles",salleService.findAll());
        model.addAttribute("equipements",equipementService.findAll());
        model.addAttribute("materiels",materielService.findAll());
        model.addAttribute("unites",uniteService.findAll());
        model.addAttribute("ndSallesEquip",salleService.ndSallesEquip());
        model.addAttribute("nbreDmVal",demandeService.dmValides());
        model.addAttribute("demandes", demandeService.findAll());
        Utilisateur currentUser = (Utilisateur) session.getAttribute("currentUser");
        String userRole = (String) session.getAttribute("userRole");
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("userRole", userRole);
        return "dashboardUser0";
    }
    @GetMapping("/utilisateurs/gestion")
    public String gestion(Model model, HttpSession session) {
        Administrateur currentUser = (Administrateur) session.getAttribute("currentUser");
        String userRole = (String) session.getAttribute("userRole");
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("userRole", userRole);
        model.addAttribute("utilisateur", new Utilisateur());
        model.addAttribute("utilisateurs", utilisateurService.findAll());
        model.addAttribute("unites", uniteService.findAll());
        return "gestion-utilisateurs";
    }

    @PostMapping("/utilisateurs/save")
    public String save(@ModelAttribute Utilisateur utilisateur, Model model, HttpSession session) {
        Administrateur currentUser = (Administrateur) session.getAttribute("currentUser");
        String userRole = (String) session.getAttribute("userRole");
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("userRole", userRole);
        System.out.println(currentUser.getId() + " " + currentUser.getUsername());
        try {
            utilisateurService.save(utilisateur, currentUser);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("utilisateur", utilisateur);
            model.addAttribute("utilisateurs", utilisateurService.findAll());
            model.addAttribute("unites", uniteService.findAll());
            return "gestion-utilisateurs";
        }
        return "redirect:/utilisateurs/gestion";
    }

    @PostMapping("/utilisateurs/update")
    public String update(@ModelAttribute Utilisateur utilisateur, Model model) {
        try {
            utilisateurService.update(utilisateur);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("utilisateur", utilisateur);
            model.addAttribute("utilisateurs", utilisateurService.findAll());
            model.addAttribute("unites", uniteService.findAll());
            return "gestion-utilisateurs";
        }
        return "redirect:/utilisateurs/gestion";
    }

    @PostMapping("/utilisateurs/delete")
    public String delete(@ModelAttribute Utilisateur utilisateur, Model model) {
        try {
            utilisateurService.delete(utilisateur.getId());
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("utilisateur", new Utilisateur());
            model.addAttribute("utilisateurs", utilisateurService.findAll());
            model.addAttribute("unites", uniteService.findAll());
            return "gestion-utilisateurs";
        }
        return "redirect:/utilisateurs/gestion";
    }
    @PostMapping("/demandes/validate")
    public String validate(@RequestParam Long id, Model model) {
        try {
            Demande demande = demandeService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Demande introuvable avec l'ID " + id));
            demandeService.validate(demande);
            return "redirect:/utilisateurs/dashboardAdmin";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/demandes?error";
        }
    }
    @PostMapping("/demandes/refuse")
    public String refuse(@RequestParam Long id, Model model) {
        try {
            Demande demande = demandeService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Demande introuvable avec l'ID " + id));
            demandeService.refuse(demande);
            return "redirect:/utilisateurs/dashboardAdmin";
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/demandes?error";
        }
    }
}