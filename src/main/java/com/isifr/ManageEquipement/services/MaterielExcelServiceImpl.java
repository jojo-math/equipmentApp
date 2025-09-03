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
import java.util.Iterator;
import java.util.List;

@Service
public class MaterielExcelServiceImpl implements MaterielExcelService {

    @Autowired
    private MaterielService materielService;

    @Autowired
    private MaterielRepository materielRepository;
    @Autowired
    private CategorieRepository categorieRepository;
    @Autowired
    private UniteRepository uniteRepository;
    @Autowired
    private SalleRepository salleRepository;
    @Autowired
    private EquipementRepository equipementRepository;
    @Autowired
    private DemandeRepository demandeRepository;

    @Override
    public ByteArrayInputStream exportToExcel(List<Materiel> materiels) throws IOException {
        String[] columns = {"ID", "Nom","Etat", "Catégorie","ID unite", "Salle", "Description"};

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Materiels");

            // Créer l'en-tête
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }

            // Remplir les données
            int rowIdx = 1;
            for (Materiel materiel : materiels) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(materiel.getId());
                row.createCell(1).setCellValue(materiel.getNom());
                row.createCell(2).setCellValue(materiel.getEtat() != null ? String.valueOf(materiel.getEtat()) : "");
                row.createCell(3).setCellValue(materiel.getCategorie() != null ? materiel.getCategorie().getNom() : "");
                row.createCell(4).setCellValue(materiel.getUnite() != null ? String.valueOf(materiel.getUnite().getId()) : "");
                row.createCell(5).setCellValue(materiel.getSalle() != null ? materiel.getSalle().getNom() : "");
                row.createCell(6).setCellValue(materiel.getDescription() != null ? materiel.getDescription() : "");
                row.createCell(7).setCellValue(materiel.getEquipement() != null ? String.valueOf(materiel.getEquipement().getId()) : "");
                row.createCell(8).setCellValue(materiel.getDemande() != null ? String.valueOf(materiel.getDemande().getId()) : "");
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
            if (rows.hasNext()) rows.next();

            // Lire chaque ligne
            while (rows.hasNext()) {
                try {
                    Row row = rows.next();
                    Materiel materiel = new Materiel();
                    // Récupérer le numéro de série = Récupérer l'ID
                    Cell numSerieCell = row.getCell(0);
                    String numSerie = numSerieCell != null ? numSerieCell.getStringCellValue() : null;
                    if (numSerie == null || numSerie.trim().isEmpty()) {
                        logger.warn("Ligne ignorée : numéro de série vide ou null");
                        continue;
                    }
                    materiel.setId(numSerie);

                    // Récupérer le nom
                    Cell nomCell = row.getCell(1);
                    String nom = nomCell != null ? nomCell.getStringCellValue() : null;
                    if (nom == null || nom.trim().isEmpty()) {
                        logger.warn("Ligne ignorée : nom vide ou null");
                        continue;
                    }
                    materiel.setNom(nom);
                    // Récupérer l'état
                    Cell etatCell = row.getCell(2);
                    EtatMateriel etat = null;
                    if (etatCell != null && etatCell.getCellType() == CellType.STRING) {
                        String etatValue = etatCell.getStringCellValue();
                        try {
                            etat = EtatMateriel.fromLabel(etatValue);
                        } catch (IllegalArgumentException e) {
                            logger.warn("Ligne ignorée : état invalide {}", etatValue);
                            continue;
                        }
                    }
                    if (etat == null) {
                        logger.warn("Ligne ignorée : état vide ou null");
                        continue;
                    }
                    materiel.setEtat(etat);

                    // Récupérer la catégorie
                    Cell catCell = row.getCell(3);
                    Categorie categorie = null;
                    if (catCell != null && catCell.getCellType() == CellType.STRING) {
                        String categorieNom = catCell.getStringCellValue();
                        if (categorieNom != null && !categorieNom.trim().isEmpty()) {
                            categorie = categorieRepository.findByNom(categorieNom)
                                    .orElse(null);
                            if (categorie == null) {
                                logger.warn("Catégorie non trouvée : {}, enregistrée comme null", categorieNom);
                            }
                        }
                    }
                    materiel.setCategorie(categorie);

                    // Récupérer l'unité
                    Cell userCell = row.getCell(4);
                    Unite unite = null;
                    if (userCell != null) {
                        Long uniteId = null;
                        if (userCell.getCellType() == CellType.NUMERIC) {
                            uniteId = (long) userCell.getNumericCellValue();
                        } else if (userCell.getCellType() == CellType.STRING) {
                            try {
                                uniteId = Long.parseLong(userCell.getStringCellValue());
                            } catch (NumberFormatException e) {
                                logger.warn("ID utilisateur invalide : {}, enregistré comme null", userCell.getStringCellValue());
                            }
                        }
                        if (uniteId != null) {
                            unite = uniteRepository.findById(String.valueOf(uniteId))
                                    .orElse(null);
                            if (unite == null) {
                                logger.warn("Utilisateur non trouvé : {}, enregistré comme null", uniteId);
                            }
                        }
                    }
                    materiel.setUnite(unite);

                    // Récupérer la salle
                    Cell salCell = row.getCell(5);
                    Salle salle = null;
                    if (salCell != null && salCell.getCellType() == CellType.STRING) {
                        String salleNom = salCell.getStringCellValue();
                        if (salleNom != null && !salleNom.trim().isEmpty()) {
                            salle = salleRepository.findByNom(salleNom)
                                    .orElse(null);
                            if (salle == null) {
                                logger.warn("Catégorie non trouvée : {}, enregistrée comme null", salleNom);
                            }
                        }
                    }
                    materiel.setSalle(salle);
                    // Récupérer le description
                    Cell descCell = row.getCell(6);
                    String desc = descCell != null ? descCell.getStringCellValue() : null;
                    if (desc == null || desc.trim().isEmpty()) {
                        logger.warn("Ligne ignorée : description vide ou null");
                        continue;
                    }
                    materiel.setDescription(desc);

                    // Récupérer l'equipement
                    Cell equipCell = row.getCell(7);
                    Equipement equipement = null;
                    if (equipCell != null) {
                        Long equipId = null;
                        if (equipCell.getCellType() == CellType.NUMERIC) {
                            equipId = (long) equipCell.getNumericCellValue();
                        } else if (equipCell.getCellType() == CellType.STRING) {
                            try {
                                equipId = Long.parseLong(userCell.getStringCellValue());
                            } catch (NumberFormatException e) {
                                logger.warn("ID utilisateur invalide : {}, enregistré comme null", equipCell.getStringCellValue());
                            }
                        }
                        if (equipId != null) {
                            equipement = equipementRepository.findById(equipId)
                                    .orElse(null);
                            if (equipement == null) {
                                logger.warn("Equipement non trouvé : {}, enregistré comme null", equipement);
                            }
                        }
                    }
                    materiel.setEquipement(equipement);

                    // Récupérer la demande
                    Cell dmCell = row.getCell(8);
                    Demande demande = null;
                    if (dmCell != null) {
                        Long dmId = null;
                        if (dmCell.getCellType() == CellType.NUMERIC) {
                            dmId = (long) dmCell.getNumericCellValue();
                        } else if (dmCell.getCellType() == CellType.STRING) {
                            try {
                                dmId = Long.parseLong(dmCell.getStringCellValue());
                            } catch (NumberFormatException e) {
                                logger.warn("ID utilisateur invalide : {}, enregistré comme null", dmCell.getStringCellValue());
                            }
                        }
                        if (dmId != null) {
                            demande = demandeRepository.findById(dmId)
                                    .orElse(null);
                            if (demande == null) {
                                logger.warn("Utilisateur non trouvé : {}, enregistré comme null", dmId);
                            }
                        }
                    }
                    materiel.setDemande(demande);

                    // Vérifier si le matériel existe déjà
                    if (materielRepository.existsById(numSerie)) {
                        materiel.setId(numSerie);
                        materielService.update(materiel);
                    } else {
                        materielService.save(materiel);
                    }
                } catch (Exception e) {
                    logger.error("Erreur lors du traitement de la ligne : {}", e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new IOException("Erreur lors de la lecture du fichier Excel : " + e.getMessage(), e);
        }
    }
}
