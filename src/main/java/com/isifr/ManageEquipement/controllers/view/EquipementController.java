package com.isifr.ManageEquipement.controllers.view;

import com.isifr.ManageEquipement.entities.Administrateur;
import com.isifr.ManageEquipement.entities.Equipement;
import com.isifr.ManageEquipement.services.EquipementExcelService;
import com.isifr.ManageEquipement.services.EquipementService;
import com.isifr.ManageEquipement.services.MaterielService;
import com.isifr.ManageEquipement.services.UniteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/api")
public class EquipementController {

    @Autowired
    private EquipementService equipementService;

    @Autowired
    private EquipementExcelService equipementExcelService;

    @Autowired
    private MaterielService materielService;
    @Autowired
    private UniteService uniteService;

    @GetMapping("/equipements")
    public String gestion(Model model, HttpSession session) {
        Administrateur currentUser = (Administrateur) session.getAttribute("currentUser");
        String userRole = (String) session.getAttribute("userRole");
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("userRole", userRole);
        model.addAttribute("equipement", new Equipement());
        model.addAttribute("equipements", equipementService.findAll());
        model.addAttribute("materiels", materielService.findAll());
        model.addAttribute("unites", uniteService.findAll());
        return "gestion-equipements";
    }

    @PostMapping("/equipements/save")
    public String save(@ModelAttribute Equipement equipement, Model model, HttpSession session) {
        Administrateur currentUser = (Administrateur) session.getAttribute("currentUser");
        String userRole = (String) session.getAttribute("userRole");
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("userRole", userRole);
        equipementService.save(equipement,currentUser.getUnite());
        return "redirect:/api/equipements";
    }

    @PostMapping("/equipements/update")
    public String update(@ModelAttribute Equipement equipement, Model model) {
        equipementService.update(equipement);
        return "redirect:/api/equipements";
    }

    @PostMapping("/equipements/delete")
    public String delete(@ModelAttribute Equipement equipement, Model model) {
        equipementService.delete(equipement.getId());
        return "redirect:/api/equipements";
    }

    @GetMapping("/equipements/export")
    public ResponseEntity<InputStreamResource> exportEquipements() throws IOException {
        List<Equipement> equipements = equipementService.findAll();
        ByteArrayInputStream in = equipementExcelService.exportToExcel(equipements);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=Equipements.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }

    @PostMapping("/equipements/import")
    public String importEquipement(@RequestParam("file") MultipartFile file, Model model) {
        try {
            if (file.isEmpty()) {
                model.addAttribute("error", "Veuillez sélectionner un fichier Excel non vide.");
                model.addAttribute("equipement", new Equipement());
                model.addAttribute("equipements", equipementService.findAll());
                model.addAttribute("materiels", materielService.findAll());
                return "gestion-equipements";
            }

            equipementExcelService.importFromExcel(file);
            model.addAttribute("success", "Équipements importés avec succès !");
        } catch (IOException e) {
            model.addAttribute("error", "Erreur lors de l'importation : " + e.getMessage());
        } catch (Exception e) {
            model.addAttribute("error", "Une erreur inattendue s'est produite : " + e.getMessage());
        }
        model.addAttribute("equipement", new Equipement());
        model.addAttribute("equipements", equipementService.findAll());
        model.addAttribute("materiels", materielService.findAll());
        return "redirect:/api/equipements";
    }
}