package com.isifr.ManageEquipement.controllers.view;

import com.isifr.ManageEquipement.entities.Administrateur;
import com.isifr.ManageEquipement.entities.Materiel;
import com.isifr.ManageEquipement.services.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@Controller
public class MaterielController {
    static class Cpt {
        private Integer qte;
        public void setQte(Integer qte) { this.qte = qte; }
        public Integer getQte() { return qte; }
    }

    @Autowired
    private MaterielService materielService;
    @Autowired
    private UniteService uniteService;
    @Autowired
    private MaterielExcelServiceImpl materielExcelService;
    @Autowired
    private CategorieService categorieService;
    @Autowired
    private EquipementService equipementService;

    @GetMapping("/materiels")
    public String materiels(Model model, @ModelAttribute Cpt cpt,
                            @RequestParam(required = false) Integer qte,
                            HttpSession session,
                            @RequestParam(required = false) String id,
                            @RequestParam(required = false) String nom,
                            @RequestParam(required = false) String categorieNom,
                            @RequestParam(required = false) String description,
                            @RequestParam(required = false) String uniteNom,
                            @RequestParam(required = false) String salleNom,
                            @RequestParam(required = false) String equipementNom) {
        try {
            Administrateur currentUser = (Administrateur) session.getAttribute("currentUser");
            String userRole = (String) session.getAttribute("userRole");
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("userRole", userRole);
            model.addAttribute("materiel", new Materiel());
            model.addAttribute("stock", cpt);

            // Appliquer les filtres ou retourner tous les matériels si aucun filtre n'est spécifié
            List<Materiel> materiels = materielService.findByCriteria(id, nom, categorieNom, description,
                    uniteNom, salleNom, equipementNom);
            model.addAttribute("materiels", materiels);

            model.addAttribute("categories", categorieService.findAll());
            model.addAttribute("unites", uniteService.findAll());
            model.addAttribute("equipements", equipementService.findAll());

            // Ajouter les valeurs des filtres pour les réafficher dans le formulaire
            model.addAttribute("filterId", id);
            model.addAttribute("filterNom", nom);
            model.addAttribute("filterCategorieNom", categorieNom);
            model.addAttribute("filterDescription", description);
            model.addAttribute("filterUniteNom", uniteNom);
            model.addAttribute("filterSalleNom", salleNom);
            model.addAttribute("filterEquipementNom", equipementNom);

            return "gestion-materiels";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors du chargement des matériels : " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/materiels/save")
    public String save(@ModelAttribute Materiel materiel, @ModelAttribute Cpt cpt, Model model, HttpSession session) {
        Administrateur currentUser = (Administrateur) session.getAttribute("currentUser");
        String userRole = (String) session.getAttribute("userRole");
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("userRole", userRole);
        try {
            if (cpt.getQte() == null || cpt.getQte() <= 0) {
                model.addAttribute("error", "La quantité doit être supérieure à 0");
                return "gestion-materiels";
            }
            materielService.saveAll(materiel, cpt.getQte());
            System.out.println(currentUser.getId() + " " + currentUser.getUsername());
            return "redirect:/materiels";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("materiel", materiel);
            model.addAttribute("stock", cpt);
            model.addAttribute("materiels", materielService.findAll());
            model.addAttribute("categories", categorieService.findAll());
            return "gestion-materiels";
        }
    }

    @PostMapping("/materiels/update")
    public String update(@ModelAttribute Materiel materiel, Model model) {
        try {
            materielService.update(materiel);
            return "redirect:/materiels";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("materiel", materiel);
            model.addAttribute("materiels", materielService.findAll());
            model.addAttribute("categories", categorieService.findAll());
            return "gestion-materiels";
        }
    }

    @PostMapping("/materiels/delete")
    public String delete(@RequestParam String id, Model model) {
        try {
            materielService.delete(id);
            return "redirect:/materiels";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("materiel", new Materiel());
            model.addAttribute("materiels", materielService.findAll());
            model.addAttribute("categories", categorieService.findAll());
            return "gestion-materiels";
        }
    }

    @GetMapping("/materiels/export")
    public ResponseEntity<InputStreamResource> exportMateriels() throws IOException {
        List<Materiel> materiels = materielService.findAll();
        ByteArrayInputStream in = materielExcelService.exportToExcel(materiels);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=Materiels.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }

    @PostMapping("/materiels/import")
    public String importMateriel(@RequestParam("file") MultipartFile file, Model model) {
        try {
            if (file.isEmpty()) {
                model.addAttribute("error", "Veuillez sélectionner un fichier Excel non vide.");
                return "gestion-materiels";
            }
            materielExcelService.importFromExcel(file);
            model.addAttribute("success", "Matériels importés avec succès !");
        } catch (IOException e) {
            model.addAttribute("error", "Erreur lors de l'importation : " + e.getMessage());
        } catch (Exception e) {
            model.addAttribute("error", "Une erreur inattendue s'est produite : " + e.getMessage());
        }
        model.addAttribute("materiel", new Materiel());
        model.addAttribute("materiels", materielService.findAll());
        model.addAttribute("categories", categorieService.findAll());
        return "redirect:/materiels";
    }
}