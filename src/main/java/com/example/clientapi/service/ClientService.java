package com.example.clientapi.service;

import com.example.clientapi.dto.ClientDto;
import com.example.clientapi.dto.CreateClientDto;
import com.example.clientapi.dto.UpdateClientDto;
import com.example.clientapi.entity.ClientStatus;
import com.example.clientapi.entity.ClientType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Interface du service de gestion des clients.
 *
 * Définit les opérations métier disponibles pour la gestion des clients.
 */
public interface ClientService {

    /**
     * Crée un nouveau client.
     *
     * @param createClientDto Données du client à créer
     * @return Client créé
     * @throws EmailAlreadyExistsException si l'email existe déjà
     */
    ClientDto createClient(CreateClientDto createClientDto);

    /**
     * Récupère un client par son ID.
     *
     * @param id ID du client
     * @return Client trouvé
     * @throws ClientNotFoundException si le client n'existe pas
     */
    ClientDto getClientById(Long id);

    /**
     * Récupère un client par son email.
     *
     * @param email Email du client
     * @return Client trouvé
     * @throws ClientNotFoundException si le client n'existe pas
     */
    ClientDto getClientByEmail(String email);

    /**
     * Récupère tous les clients avec pagination.
     *
     * @param pageable Informations de pagination
     * @return Page de clients
     */
    Page<ClientDto> getAllClients(Pageable pageable);

    /**
     * Met à jour un client existant.
     *
     * @param id ID du client à mettre à jour
     * @param updateClientDto Nouvelles données du client
     * @return Client mis à jour
     * @throws ClientNotFoundException si le client n'existe pas
     * @throws EmailAlreadyExistsException si le nouvel email existe déjà
     */
    ClientDto updateClient(Long id, UpdateClientDto updateClientDto);

    /**
     * Supprime un client.
     *
     * @param id ID du client à supprimer
     * @throws ClientNotFoundException si le client n'existe pas
     */
    void deleteClient(Long id);

    /**
     * Recherche les clients par statut.
     *
     * @param status Statut recherché
     * @param pageable Informations de pagination
     * @return Page de clients avec ce statut
     */
    Page<ClientDto> getClientsByStatus(ClientStatus status, Pageable pageable);

    /**
     * Recherche les clients par type.
     *
     * @param type Type recherché
     * @param pageable Informations de pagination
     * @return Page de clients de ce type
     */
    Page<ClientDto> getClientsByType(ClientType type, Pageable pageable);

    /**
     * Recherche globale dans les clients.
     *
     * @param searchTerm Terme de recherche
     * @param pageable Informations de pagination
     * @return Page de clients correspondants
     */
    Page<ClientDto> searchClients(String searchTerm, Pageable pageable);

    /**
     * Active un client (change son statut en ACTIVE).
     *
     * @param id ID du client
     * @return Client activé
     * @throws ClientNotFoundException si le client n'existe pas
     */
    ClientDto activateClient(Long id);

    /**
     * Désactive un client (change son statut en INACTIVE).
     *
     * @param id ID du client
     * @return Client désactivé
     * @throws ClientNotFoundException si le client n'existe pas
     */
    ClientDto deactivateClient(Long id);

    /**
     * Vérifie si un email existe déjà.
     *
     * @param email Email à vérifier
     * @return true si l'email existe
     */
    boolean emailExists(String email);

    /**
     * Compte le nombre total de clients.
     *
     * @return Nombre total de clients
     */
    long countClients();

    /**
     * Compte le nombre de clients par statut.
     *
     * @param status Statut à compter
     * @return Nombre de clients avec ce statut
     */
    long countClientsByStatus(ClientStatus status);
}
