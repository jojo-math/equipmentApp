package com.isifr.ManageEquipement.controllers.view;


import com.isifr.ManageEquipement.entities.Administrateur;
import com.isifr.ManageEquipement.entities.Utilisateur;
import com.isifr.ManageEquipement.repositories.AdministrateurRepository;
import com.isifr.ManageEquipement.repositories.UtilisateurRepository;
import com.isifr.ManageEquipement.services.UtilisateurService;
import com.isifr.ManageEquipement.security.JwtUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;

@Controller
public class LoginController {

    @Autowired
    private UtilisateurService utilisateurService;
    @Autowired
    private AdministrateurRepository administrateurRepository;
    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String loginPage() {
        return "login-form";
    }

    @GetMapping("/")
    public String index(Model model) {
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, HttpSession session, Model model) {
        // Rechercher l'utilisateur par username
        Utilisateur utilisateur = utilisateurService.findByUsername(username)
                .orElse(null);

        if (utilisateur == null || !passwordEncoder.matches(password, utilisateur.getPassword())) {
            return "redirect:/login?error";
        }

        // Déterminer si l'utilisateur est un Administrateur
        Administrateur administrateur = null;
        if (administrateurRepository.existsById(utilisateur.getId())) {
            administrateur = administrateurRepository.findById(utilisateur.getId())
                    .orElseThrow(() -> new IllegalStateException("Administrateur introuvable"));
        }

        // Déterminer le rôle
        String role = administrateur != null ? "ADMIN" : "USER";

        // Créer les détails de l'utilisateur pour JWT avec le rôle
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
        UserDetails userDetails = new User(username, utilisateur.getPassword(), authorities);
        String token = jwtUtil.generateToken(userDetails);

        // Stocker le token JWT dans la session
        session.setAttribute("jwtToken", token);

        // Stocker l'objet approprié dans la session
        session.setAttribute("currentUser", administrateur != null ? administrateur : utilisateur);
        session.setAttribute("userRole", role);

        // Redirection selon le rôle
        if (role.equals("ADMIN")) {
            return "redirect:/utilisateurs/dashboardAdmin";
        }
        return "redirect:/utilisateurs/dashboardUser";
    }

    // Endpoint pour récupérer les informations de l'utilisateur connecté
    @ModelAttribute("currentUser")
    public Utilisateur getCurrentUser(HttpSession session) {
        return (Utilisateur) session.getAttribute("currentUser");
    }

    @ModelAttribute("userRole")
    public String getUserRole(HttpSession session) {
        return (String) session.getAttribute("userRole");
    }
}