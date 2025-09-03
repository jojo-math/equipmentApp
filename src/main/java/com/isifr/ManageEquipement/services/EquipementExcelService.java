package com.isifr.ManageEquipement.services;

import  com.isifr.ManageEquipement.entities.Equipement;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

public interface EquipementExcelService {

    ByteArrayInputStream exportToExcel(List<Equipement> equipements) throws IOException;

    void importFromExcel(MultipartFile file) throws IOException;
}
