package com.isifr.ManageEquipement.services;

import  com.isifr.ManageEquipement.entities.Materiel;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

public interface MaterielExcelService {

    ByteArrayInputStream exportToExcel(List<Materiel> materiels) throws IOException;

    void importFromExcel(MultipartFile file) throws IOException;
}