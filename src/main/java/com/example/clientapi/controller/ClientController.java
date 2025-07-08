// Fichier: src/main/java/com/example/clientapi/controller/ClientController.java
package com.example.clientapi.controller;

import com.example.clientapi.dto.ClientDto;
import com.example.clientapi.dto.CreateClientDto;
import com.example.clientapi.dto.UpdateClientDto;
import com.example.clientapi.entity.ClientStatus;
import com.example.clientapi.entity.ClientType;
import com.example.clientapi.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Contrôleur REST pour la gestion des clients.
 *
 * Expose les endpoints de l'API REST pour toutes les opérations CRUD
 * et fonctionnalités avancées de gestion des clients.
 */
@RestController
@RequestMapping("/api/v1/clients")
@Tag(name = "Client Management", description = "API pour la gestion des clients PayeTonKawa")
//@CrossOrigin(origins = "*", maxAge = 3600)
public class ClientController {

    private static final Logger logger = LoggerFactory.getLogger(ClientController.class);

    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    /**
     * Crée un nouveau client.
     */
    @PostMapping
    @Operation(summary = "Créer un nouveau client", description = "Crée un nouveau client dans le système")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Client créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "409", description = "Email déjà existant")
    })
    public ResponseEntity<ClientDto> createClient(
            @Valid @RequestBody CreateClientDto createClientDto) {

        logger.info("Requête de création d'un client reçue pour l'email: {}", createClientDto.getEmail());

        ClientDto createdClient = clientService.createClient(createClientDto);

        logger.info("Client créé avec succès. ID: {}", createdClient.getId());
        return new ResponseEntity<>(createdClient, HttpStatus.CREATED);
    }

    /**
     * Récupère un client par son ID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un client par ID", description = "Récupère les détails d'un client spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client trouvé"),
            @ApiResponse(responseCode = "404", description = "Client non trouvé")
    })
    public ResponseEntity<ClientDto> getClientById(
            @Parameter(description = "ID du client") @PathVariable Long id) {

        logger.debug("Requête de récupération du client avec l'ID: {}", id);

        ClientDto client = clientService.getClientById(id);
        return ResponseEntity.ok(client);
    }

    /**
     * Récupère un client par son email.
     */
    @GetMapping("/email/{email}")
    @Operation(summary = "Récupérer un client par email", description = "Récupère un client par son adresse email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client trouvé"),
            @ApiResponse(responseCode = "404", description = "Client non trouvé")
    })
    public ResponseEntity<ClientDto> getClientByEmail(
            @Parameter(description = "Email du client") @PathVariable String email) {

        logger.debug("Requête de récupération du client avec l'email: {}", email);

        ClientDto client = clientService.getClientByEmail(email);
        return ResponseEntity.ok(client);
    }

    /**
     * Récupère tous les clients avec pagination.
     */
    @GetMapping
    @Operation(summary = "Lister tous les clients", description = "Récupère la liste paginée de tous les clients")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des clients récupérée")
    })
    public ResponseEntity<Page<ClientDto>> getAllClients(
            @PageableDefault(size = 20, sort = "lastName") Pageable pageable) {

        logger.debug("Requête de récupération de tous les clients. Page: {}, Taille: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<ClientDto> clients = clientService.getAllClients(pageable);
        return ResponseEntity.ok(clients);
    }

    /**
     * Met à jour un client existant.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un client", description = "Met à jour les informations d'un client existant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client mis à jour avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "404", description = "Client non trouvé"),
            @ApiResponse(responseCode = "409", description = "Email déjà existant")
    })
    public ResponseEntity<ClientDto> updateClient(
            @Parameter(description = "ID du client") @PathVariable Long id,
            @Valid @RequestBody UpdateClientDto updateClientDto) {

        logger.info("Requête de mise à jour du client avec l'ID: {}", id);

        ClientDto updatedClient = clientService.updateClient(id, updateClientDto);

        logger.info("Client mis à jour avec succès. ID: {}", id);
        return ResponseEntity.ok(updatedClient);
    }

    /**
     * Supprime un client.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un client", description = "Supprime un client du système")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Client supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Client non trouvé")
    })
    public ResponseEntity<Void> deleteClient(
            @Parameter(description = "ID du client") @PathVariable Long id) {

        logger.info("Requête de suppression du client avec l'ID: {}", id);

        clientService.deleteClient(id);

        logger.info("Client supprimé avec succès. ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Recherche les clients par statut.
     */
    @GetMapping("/status/{status}")
    @Operation(summary = "Lister les clients par statut", description = "Récupère les clients ayant un statut spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des clients par statut récupérée")
    })
    public ResponseEntity<Page<ClientDto>> getClientsByStatus(
            @Parameter(description = "Statut du client") @PathVariable ClientStatus status,
            @PageableDefault(size = 20, sort = "lastName") Pageable pageable) {

        logger.debug("Requête de récupération des clients avec le statut: {}", status);

        Page<ClientDto> clients = clientService.getClientsByStatus(status, pageable);
        return ResponseEntity.ok(clients);
    }

    /**
     * Recherche les clients par type.
     */
    @GetMapping("/type/{type}")
    @Operation(summary = "Lister les clients par type", description = "Récupère les clients ayant un type spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des clients par type récupérée")
    })
    public ResponseEntity<Page<ClientDto>> getClientsByType(
            @Parameter(description = "Type du client") @PathVariable ClientType type,
            @PageableDefault(size = 20, sort = "lastName") Pageable pageable) {

        logger.debug("Requête de récupération des clients avec le type: {}", type);

        Page<ClientDto> clients = clientService.getClientsByType(type, pageable);
        return ResponseEntity.ok(clients);
    }

    /**
     * Recherche globale dans les clients.
     */
    @GetMapping("/search")
    @Operation(summary = "Rechercher des clients", description = "Recherche globale dans nom, prénom et email des clients")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Résultats de recherche récupérés")
    })
    public ResponseEntity<Page<ClientDto>> searchClients(
            @Parameter(description = "Terme de recherche") @RequestParam String q,
            @PageableDefault(size = 20, sort = "lastName") Pageable pageable) {

        logger.debug("Requête de recherche globale avec le terme: {}", q);

        Page<ClientDto> clients = clientService.searchClients(q, pageable);
        return ResponseEntity.ok(clients);
    }

    /**
     * Active un client.
     */
    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activer un client", description = "Change le statut d'un client en ACTIVE")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client activé avec succès"),
            @ApiResponse(responseCode = "404", description = "Client non trouvé")
    })
    public ResponseEntity<ClientDto> activateClient(
            @Parameter(description = "ID du client") @PathVariable Long id) {

        logger.info("Requête d'activation du client avec l'ID: {}", id);

        ClientDto activatedClient = clientService.activateClient(id);

        logger.info("Client activé avec succès. ID: {}", id);
        return ResponseEntity.ok(activatedClient);
    }

    /**
     * Désactive un client.
     */
    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Désactiver un client", description = "Change le statut d'un client en INACTIVE")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client désactivé avec succès"),
            @ApiResponse(responseCode = "404", description = "Client non trouvé")
    })
    public ResponseEntity<ClientDto> deactivateClient(
            @Parameter(description = "ID du client") @PathVariable Long id) {

        logger.info("Requête de désactivation du client avec l'ID: {}", id);

        ClientDto deactivatedClient = clientService.deactivateClient(id);

        logger.info("Client désactivé avec succès. ID: {}", id);
        return ResponseEntity.ok(deactivatedClient);
    }

    /**
     * Vérifie si un email existe.
     */
    @GetMapping("/email/{email}/exists")
    @Operation(summary = "Vérifier l'existence d'un email", description = "Vérifie si un email existe déjà dans le système")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vérification effectuée")
    })
    public ResponseEntity<Map<String, Boolean>> checkEmailExists(
            @Parameter(description = "Email à vérifier") @PathVariable String email) {

        logger.debug("Vérification de l'existence de l'email: {}", email);

        boolean exists = clientService.emailExists(email);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);

        return ResponseEntity.ok(response);
    }

    /**
     * Récupère les statistiques des clients.
     */
    @GetMapping("/stats")
    @Operation(summary = "Statistiques des clients", description = "Récupère les statistiques générales des clients")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistiques récupérées")
    })
    public ResponseEntity<Map<String, Object>> getClientStats() {

        logger.debug("Requête de récupération des statistiques des clients");

        Map<String, Object> stats = new HashMap<>();
        stats.put("total", clientService.countClients());
        stats.put("active", clientService.countClientsByStatus(ClientStatus.ACTIVE));
        stats.put("inactive", clientService.countClientsByStatus(ClientStatus.INACTIVE));
        stats.put("suspended", clientService.countClientsByStatus(ClientStatus.SUSPENDED));
        stats.put("pending", clientService.countClientsByStatus(ClientStatus.PENDING));

        return ResponseEntity.ok(stats);
    }

    /**
     * Health check endpoint spécifique aux clients.
     */
    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Endpoint de vérification de santé du service clients")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Service opérationnel")
    })
    public ResponseEntity<Map<String, Object>> healthCheck() {

        logger.debug("Health check du service clients");

        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "client-api");
        health.put("timestamp", java.time.LocalDateTime.now());
        health.put("totalClients", clientService.countClients());

        return ResponseEntity.ok(health);
    }
}