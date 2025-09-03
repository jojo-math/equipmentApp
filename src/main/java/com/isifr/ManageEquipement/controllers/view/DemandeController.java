package com.isifr.ManageEquipement.controllers.view;

import com.isifr.ManageEquipement.entities.Demande;
import com.isifr.ManageEquipement.entities.StatutDemande;
import com.isifr.ManageEquipement.entities.Utilisateur;
import com.isifr.ManageEquipement.repositories.AdministrateurRepository;
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
public class DemandeController {

    @Autowired
    private DemandeService demandeService;

    @Autowired
    private DemandeurService demandeurService;

    @Autowired
    private EquipementService equipementService;

    @Autowired
    private UtilisateurService utilisateurService;
    @Autowired
    private MaterielService materielService;
    @Autowired
    private SalleService salleService;
    @Autowired
    private AdministrateurRepository administrateurRepository;

    @GetMapping("/demandes")
    public String gestion(Model model, HttpSession session) {
        Utilisateur currentUser = (Utilisateur) session.getAttribute("currentUser");
        String userRole = (String) session.getAttribute("userRole");
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("userRole", userRole);
        model.addAttribute("demande", new Demande());
        model.addAttribute("demandes", demandeService.findAll());
        model.addAttribute("demandeurs", demandeurService.findAll());
        model.addAttribute("equipements", equipementService.findAll());
        model.addAttribute("materiels", materielService.findAll());
        model.addAttribute("salles", salleService.findAll());
        model.addAttribute("administrateurs", administrateurRepository.findAll());
        model.addAttribute("statuts", StatutDemande.values());
        return "gestion-demandes";
    }

    @PostMapping("/demandes/save")
    public String save(@ModelAttribute Demande demande, Model model, HttpSession session) {
        // Récupérer l'utilisateur connecté
        Utilisateur currentUser = (Utilisateur) session.getAttribute("currentUser");
        String userRole = (String) session.getAttribute("userRole");
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("userRole", userRole);
        demandeService.save(demande,currentUser);
        System.out.println(currentUser.getId() + " " + currentUser.getUsername());
        return "redirect:/demandes";
    }

    @PostMapping("/demandes/update")
    public String update(@ModelAttribute Demande demande, Model model) {
        demandeService.update(demande);
        return "redirect:/demandes";
    }

    @PostMapping("/demandes/delete")
    public String delete(@RequestParam Long id, Model model) {
        try {
            demandeService.delete(id);
            return "redirect:/demandes?success=Demande+supprimée+avec+succès";
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/demandes?error";
        }
    }
}