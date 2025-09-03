package com.isifr.ManageEquipement.config;

import com.isifr.ManageEquipement.entities.Administrateur;
import com.isifr.ManageEquipement.repositories.AdministrateurRepository;
import com.isifr.ManageEquipement.repositories.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UtilisateurRepository utilisateurRepository;
    @Autowired
    private AdministrateurRepository administrateurRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Pour encoder le mot de passe

    @Override
    public void run(String... args) throws Exception {
        // Vérifier si la BD est vide
        if (administrateurRepository.count() == 0) {
            // Créer un administrateur par défaut
            Administrateur defaultUser = new Administrateur();
            defaultUser.setUsername("root");
            defaultUser.setPassword(passwordEncoder.encode("admin")); // Mot de passe encodé
            defaultUser.setEmail("root@admin.com");
            defaultUser.setUtilisateurs(List.copyOf(utilisateurRepository.findAll()));
            defaultUser.setRole("ADMIN");
            defaultUser.setAdministrateur(defaultUser);
            // Sauvegarder l'administrateur dans la base de données
            administrateurRepository.save(defaultUser);
            utilisateurRepository.save(defaultUser);
            System.out.println("Administrateur par défaut : username=root, password=admin");
            System.out.println("Mot de passe de utilisateur : "+defaultUser.getPassword());
        }

    }
}