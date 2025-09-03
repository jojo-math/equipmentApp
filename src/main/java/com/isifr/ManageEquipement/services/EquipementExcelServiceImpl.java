package com.isifr.ManageEquipement.services;

import com.isifr.ManageEquipement.entities.*;
import com.isifr.ManageEquipement.repositories.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EquipementExcelServiceImpl implements EquipementExcelService {

    @Autowired
    private EquipementService equipementService;

    @Autowired
    private EquipementRepository equipementRepository;
    @Autowired
    private MaterielRepository materielRepository;
    @Autowired
    private SalleRepository salleRepository;
    @Autowired
    private UniteRepository uniteRepository;
    @Autowired
    private DemandeRepository demandeRepository;
    @Autowired
    private CategorieRepository categorieRepository;
    @Autowired
    private MaterielService materielService;

    @Override
    public ByteArrayInputStream exportToExcel(List<Equipement> equipements) throws IOException {
        // Définir les colonnes pour correspondre aux attributs d'Equipement et Materiel
        String[] columns = {
                "Equipement ID", "Equipement Nom", "Unité ID", "Salle Nom", "Demande ID",
                "Matériel ID", "Matériel Nom", "Description", "État", "Catégorie ID", "Matériel Unité ID", "Matériel Salle Nom", "Matériel Demande ID"
        };

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Equipements");

            // Créer l'en-tête
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }

            // Remplir les données
            int rowIdx = 1;
            for (Equipement equipement : equipements) {
                List<Materiel> materiels = equipement.getMateriels();
                if (materiels == null || materiels.isEmpty()) {
                    // Créer une ligne pour l'équipement sans matériels
                    Row row = sheet.createRow(rowIdx++);

                    // Attributs de l'équipement
                    row.createCell(0).setCellValue(equipement.getId() != null ? equipement.getId().toString() : "");
                    row.createCell(1).setCellValue(equipement.getNom() != null ? equipement.getNom() : "");
                    row.createCell(2).setCellValue(equipement.getUnite() != null && equipement.getUnite().getId() != null
                            ? equipement.getUnite().getId().toString() : "");
                    row.createCell(3).setCellValue(equipement.getSalle() != null && equipement.getSalle().getNom() != null
                            ? equipement.getSalle().getNom() : "");
                    row.createCell(4).setCellValue(equipement.getDemande() != null && equipement.getDemande().getId() != null
                            ? equipement.getDemande().getId().toString() : "");

                    // Colonnes des matériels vides
                    for (int i = 5; i < columns.length; i++) {
                        row.createCell(i).setCellValue("");
                    }
                } else {
                    // Créer une ligne par matériel associé à l'équipement
                    for (Materiel materiel : materiels) {
                        Row row = sheet.createRow(rowIdx++);

                        // Attributs de l'équipement
                        row.createCell(0).setCellValue(equipement.getId() != null ? equipement.getId().toString() : "");
                        row.createCell(1).setCellValue(equipement.getNom() != null ? equipement.getNom() : "");
                        row.createCell(2).setCellValue(equipement.getUnite() != null && equipement.getUnite().getId() != null
                                ? equipement.getUnite().getId().toString() : "");
                        row.createCell(3).setCellValue(equipement.getSalle() != null && equipement.getSalle().getNom() != null
                                ? equipement.getSalle().getNom() : "");
                        row.createCell(4).setCellValue(equipement.getDemande() != null && equipement.getDemande().getId() != null
                                ? equipement.getDemande().getId().toString() : "");

                        // Attributs du matériel
                        row.createCell(5).setCellValue(materiel.getId() != null ? materiel.getId() : "");
                        row.createCell(6).setCellValue(materiel.getNom() != null ? materiel.getNom() : "");
                        row.createCell(7).setCellValue(materiel.getDescription() != null ? materiel.getDescription() : "");
                        row.createCell(8).setCellValue(materiel.getEtat() != null ? materiel.getEtat().toString() : "");
                        row.createCell(9).setCellValue(materiel.getCategorie() != null && materiel.getCategorie().getId() != null
                                ? materiel.getCategorie().getId().toString() : "");
                        row.createCell(10).setCellValue(materiel.getUnite() != null && materiel.getUnite().getId() != null
                                ? materiel.getUnite().getId().toString() : "");
                        row.createCell(11).setCellValue(materiel.getSalle() != null && materiel.getSalle().getNom() != null
                                ? materiel.getSalle().getNom() : "");
                        row.createCell(12).setCellValue(materiel.getDemande() != null && materiel.getDemande().getId() != null
                                ? materiel.getDemande().getId().toString() : "");
                    }
                }
            }

            // Ajuster automatiquement la largeur des colonnes
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    @Override
    @Transactional
    public void importFromExcel(MultipartFile file) throws IOException {
        Logger logger = LoggerFactory.getLogger(getClass());
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            // Sauter l'en-tête
            if (!rows.hasNext()) {
                throw new IOException("Le fichier Excel est vide");
            }
            rows.next();

            // Map pour regrouper les matériels par équipement
            Map<Long, Equipement> equipementMap = new HashMap<>();

            // Lire chaque ligne
            while (rows.hasNext()) {
                Row row = rows.next();
                try {
                    // Récupérer l'ID de l'équipement
                    Cell equipementIdCell = row.getCell(0);
                    Long equipementId = null;
                    if (equipementIdCell != null && equipementIdCell.getCellType() == CellType.NUMERIC) {
                        equipementId = (long) equipementIdCell.getNumericCellValue();
                    } else if (equipementIdCell != null && equipementIdCell.getCellType() == CellType.STRING) {
                        try {
                            equipementId = Long.parseLong(equipementIdCell.getStringCellValue());
                        } catch (NumberFormatException e) {
                            logger.warn("ID équipement invalide : {} à la ligne {}", equipementIdCell.getStringCellValue(), row.getRowNum());
                            continue;
                        }
                    }

                    // Récupérer le nom de l'équipement
                    Cell equipementNomCell = row.getCell(1);
                    String equipementNom = equipementNomCell != null ? equipementNomCell.getStringCellValue() : null;
                    if (equipementNom == null || equipementNom.trim().isEmpty()) {
                        logger.warn("Ligne ignorée : nom de l'équipement vide à la ligne {}", row.getRowNum());
                        continue;
                    }

                    // Récupérer l'unité de l'équipement
                    Cell uniteCell = row.getCell(2);
                    Unite unite = null;
                    if (uniteCell != null && uniteCell.getCellType() == CellType.NUMERIC) {
                        Long uniteId = (long) uniteCell.getNumericCellValue();
                        unite = uniteRepository.findById(String.valueOf(uniteId)).orElse(null);
                        if (unite == null) {
                            logger.warn("Unité non trouvée : {} à la ligne {}, enregistré comme null", uniteId, row.getRowNum());
                        }
                    }

                    // Récupérer la salle de l'équipement
                    Cell salleCell = row.getCell(3);
                    Salle salle = null;
                    if (salleCell != null && salleCell.getCellType() == CellType.STRING) {
                        String salleNom = salleCell.getStringCellValue();
                        if (salleNom != null && !salleNom.trim().isEmpty()) {
                            salle = salleRepository.findByNom(salleNom).orElse(null);
                            if (salle == null) {
                                logger.warn("Salle non trouvée : {} à la ligne {}, enregistrée comme null", salleNom, row.getRowNum());
                            }
                        }
                    }

                    // Récupérer la demande de l'équipement
                    Cell demandeCell = row.getCell(4);
                    Demande demande = null;
                    if (demandeCell != null && demandeCell.getCellType() == CellType.NUMERIC) {
                        Long demandeId = (long) demandeCell.getNumericCellValue();
                        demande = demandeRepository.findById(demandeId).orElse(null);
                        if (demande == null) {
                            logger.warn("Demande non trouvée : {} à la ligne {}, enregistrée comme null", demandeId, row.getRowNum());
                        }
                    }

                    // Créer ou récupérer l'équipement
                    Equipement equipement;
                    if (equipementId != null && equipementMap.containsKey(equipementId)) {
                        equipement = equipementMap.get(equipementId);
                    } else {
                        equipement = new Equipement();
                        equipement.setId(equipementId);
                        equipement.setNom(equipementNom);
                        equipement.setUnite(unite);
                        equipement.setSalle(salle);
                        equipement.setDemande(demande);
                        equipement.setMateriels(new ArrayList<>());
                        if (equipementId != null) {
                            equipementMap.put(equipementId, equipement);
                        }
                    }

                    // Récupérer les attributs du matériel
                    Cell materielIdCell = row.getCell(5);
                    String materielId = materielIdCell != null ? materielIdCell.getStringCellValue() : null;

                    if (materielId != null && !materielId.trim().isEmpty()) {
                        Materiel materiel = materielRepository.findById(materielId).orElse(new Materiel());

                        // Récupérer le nom du matériel
                        Cell materielNomCell = row.getCell(6);
                        String materielNom = materielNomCell != null ? materielNomCell.getStringCellValue() : null;
                        if (materielNom == null || materielNom.trim().isEmpty()) {
                            logger.warn("Nom du matériel vide à la ligne {}, matériel ignoré", row.getRowNum());
                            continue;
                        }
                        materiel.setNom(materielNom);

                        // Récupérer la description du matériel
                        Cell descriptionCell = row.getCell(7);
                        String description = descriptionCell != null ? descriptionCell.getStringCellValue() : null;
                        materiel.setDescription(description);

                        // Récupérer l'état du matériel
                        Cell etatCell = row.getCell(8);
                        EtatMateriel etat = null;
                        if (etatCell != null && etatCell.getCellType() == CellType.STRING) {
                            try {
                                etat = EtatMateriel.valueOf(etatCell.getStringCellValue());
                            } catch (IllegalArgumentException e) {
                                logger.warn("État invalide : {} à la ligne {}, défini comme DISPONIBLE", etatCell.getStringCellValue(), row.getRowNum());
                                etat = EtatMateriel.DISPONIBLE;
                            }
                        } else {
                            etat = EtatMateriel.DISPONIBLE;
                        }
                        materiel.setEtat(etat);

                        // Récupérer la catégorie du matériel
                        Cell categorieCell = row.getCell(9);
                        Categorie categorie = null;
                        if (categorieCell != null && categorieCell.getCellType() == CellType.NUMERIC) {
                            Long categorieId = (long) categorieCell.getNumericCellValue();
                            categorie = categorieRepository.findById(categorieId).orElse(null);
                            if (categorie == null) {
                                logger.warn("Catégorie non trouvée : {} à la ligne {}, matériel ignoré", categorieId, row.getRowNum());
                                continue;
                            }
                        } else {
                            logger.warn("Catégorie manquante à la ligne {}, matériel ignoré", row.getRowNum());
                            continue;
                        }
                        materiel.setCategorie(categorie);

                        // Récupérer l'unité du matériel
                        Cell materielUniteCell = row.getCell(10);
                        Unite materielUnite = null;
                        if (materielUniteCell != null && materielUniteCell.getCellType() == CellType.NUMERIC) {
                            Long materielUniteId = (long) materielUniteCell.getNumericCellValue();
                            materielUnite = uniteRepository.findById(String.valueOf(materielUniteId)).orElse(null);
                        }
                        materiel.setUnite(materielUnite);

                        // Récupérer la salle du matériel
                        Cell materielSalleCell = row.getCell(11);
                        Salle materielSalle = null;
                        if (materielSalleCell != null && materielSalleCell.getCellType() == CellType.STRING) {
                            String materielSalleNom = materielSalleCell.getStringCellValue();
                            if (materielSalleNom != null && !materielSalleNom.trim().isEmpty()) {
                                materielSalle = salleRepository.findByNom(materielSalleNom).orElse(null);
                            }
                        }
                        materiel.setSalle(materielSalle);

                        // Récupérer la demande du matériel
                        Cell materielDemandeCell = row.getCell(12);
                        Demande materielDemande = null;
                        if (materielDemandeCell != null && materielDemandeCell.getCellType() == CellType.NUMERIC) {
                            Long materielDemandeId = (long) materielDemandeCell.getNumericCellValue();
                            materielDemande = demandeRepository.findById(materielDemandeId).orElse(null);
                        }
                        materiel.setDemande(materielDemande);

                        // Associer le matériel à l'équipement
                        materiel.setEquipement(equipement);

                        // Sauvegarder ou mettre à jour le matériel
                        if (materielRepository.existsById(materielId)) {
                            materielService.update(materiel);
                        } else {
                            materielService.save(materiel);
                        }

                        equipement.getMateriels().add(materiel);
                    }
                } catch (Exception e) {
                    logger.error("Erreur lors du traitement de la ligne {} : {}", row.getRowNum(), e.getMessage(), e);
                }
            }

            // Sauvegarder ou mettre à jour les équipements
            for (Equipement equipement : equipementMap.values()) {
                try {
                    // S'assurer que la liste des matériels est initialisée
                    if (equipement.getMateriels() == null) {
                        logger.warn("L'équipement {} a une liste de matériels null, initialisation à une liste vide", equipement.getNom());
                        equipement.setMateriels(new ArrayList<>());
                    }
                    logger.debug("Traitement de l'équipement {} avec {} matériels", equipement.getNom(), equipement.getMateriels().size());
                    if (equipement.getId() != null && equipementRepository.existsById(equipement.getId())) {
                        logger.debug("Mise à jour de l'équipement {}", equipement.getNom());
                        equipementService.update(equipement);
                    } else {
                        logger.debug("Sauvegarde de l'équipement {}", equipement.getNom());
                        equipementService.save(equipement, equipement.getUnite());
                    }
                } catch (Exception e) {
                    logger.error("Erreur lors de la sauvegarde de l'équipement {} : {}", equipement.getNom(), e.getMessage(), e);
                }
            }
        } catch (IOException e) {
            throw new IOException("Erreur lors de la lecture du fichier Excel : " + e.getMessage(), e);
        }
    }
}