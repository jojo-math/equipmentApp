package com.isifr.ManageEquipement.entities;

import com.isifr.ManageEquipement.repositories.EquipementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class EquipementConverter implements Converter<String, Equipement> {
    @Autowired
    private EquipementRepository equipementRepository;

    @Override
    public Equipement convert(String id) {
        try {
            Long equipementId = Long.parseLong(id);
            return equipementRepository.findById(equipementId)
                    .orElse(null);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
