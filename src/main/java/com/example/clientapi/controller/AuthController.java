package com.example.clientapi.controller;

import com.example.clientapi.dto.auth.AdminRegisterRequest;
import com.example.clientapi.dto.auth.AuthResponse;
import com.example.clientapi.dto.auth.LoginRequest;
import com.example.clientapi.dto.auth.RegisterRequest;
import com.example.clientapi.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Contrôleur pour la gestion de l'authentification.
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "API pour l'authentification et l'inscription")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    /**
     * Connexion utilisateur.
     */
    @PostMapping("/login")
    @Operation(summary = "Connexion utilisateur", description = "Authentifie un utilisateur et retourne un token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Connexion réussie"),
            @ApiResponse(responseCode = "401", description = "Identifiants invalides"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public ResponseEntity<AuthResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info("Requête de connexion reçue pour l'email: {}", loginRequest.getEmail());

        AuthResponse authResponse = authService.authenticateUser(loginRequest);

        logger.info("Connexion réussie pour l'utilisateur: {}", loginRequest.getEmail());
        return ResponseEntity.ok(authResponse);
    }

    /**
     * Inscription client (publique).
     */
    @PostMapping("/register")
    @Operation(summary = "Inscription client", description = "Inscrit un nouveau client dans le système")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inscription réussie"),
            @ApiResponse(responseCode = "409", description = "Email déjà existant"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public ResponseEntity<AuthResponse> registerClient(@Valid @RequestBody RegisterRequest registerRequest) {
        logger.info("Requête d'inscription client reçue pour l'email: {}", registerRequest.getEmail());

        AuthResponse authResponse = authService.registerClient(registerRequest);

        logger.info("Inscription client réussie pour l'email: {}", registerRequest.getEmail());
        return ResponseEntity.ok(authResponse);
    }

    /**
     * Inscription administrateur (réservée aux admins).
     */
    @PostMapping("/register/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Inscription administrateur",
            description = "Inscrit un nouvel administrateur (réservé aux administrateurs existants)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inscription admin réussie"),
            @ApiResponse(responseCode = "409", description = "Email déjà existant"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "403", description = "Accès refusé - Rôle admin requis")
    })
    public ResponseEntity<AuthResponse> registerAdmin(@Valid @RequestBody AdminRegisterRequest registerRequest) {
        logger.info("Requête d'inscription admin reçue pour l'email: {}", registerRequest.getEmail());

        AuthResponse authResponse = authService.registerAdmin(registerRequest);

        logger.info("Inscription admin réussie pour l'email: {}", registerRequest.getEmail());
        return ResponseEntity.ok(authResponse);
    }

    /**
     * Vérification de la disponibilité d'un email.
     */
    @GetMapping("/check-email/{email}")
    @Operation(summary = "Vérifier la disponibilité d'un email",
            description = "Vérifie si un email est déjà utilisé")
    public ResponseEntity<Map<String, Boolean>> checkEmailAvailability(@PathVariable String email) {
        logger.debug("Vérification de la disponibilité de l'email: {}", email);

        boolean exists = authService.emailExists(email);
        Map<String, Boolean> response = new HashMap<>();
        response.put("available", !exists);
        response.put("exists", exists);

        return ResponseEntity.ok(response);
    }
}