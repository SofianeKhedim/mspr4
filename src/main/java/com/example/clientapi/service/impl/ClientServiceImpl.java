
package com.example.clientapi.service.impl;

import com.example.clientapi.dto.ClientDto;
import com.example.clientapi.dto.CreateClientDto;
import com.example.clientapi.dto.UpdateClientDto;
import com.example.clientapi.entity.Client;
import com.example.clientapi.entity.ClientStatus;
import com.example.clientapi.entity.ClientType;
import com.example.clientapi.exception.ClientNotFoundException;
import com.example.clientapi.exception.EmailAlreadyExistsException;
import com.example.clientapi.repository.ClientRepository;
import com.example.clientapi.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implémentation du service de gestion des clients.
 *
 * Cette classe contient toute la logique métier pour la gestion des clients,
 * incluant les validations, transformations et appels au repository.
 */
@Service
@Transactional
public class ClientServiceImpl implements ClientService {

    private static final Logger logger = LoggerFactory.getLogger(ClientServiceImpl.class);

    private final ClientRepository clientRepository;

    @Autowired
    public ClientServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public ClientDto createClient(CreateClientDto createClientDto) {
        logger.info("Création d'un nouveau client avec l'email: {}", createClientDto.getEmail());

        // Vérification de l'unicité de l'email
        if (clientRepository.existsByEmail(createClientDto.getEmail())) {
            logger.warn("Tentative de création d'un client avec un email existant: {}", createClientDto.getEmail());
            throw new EmailAlreadyExistsException("Un client avec cet email existe déjà: " + createClientDto.getEmail());
        }

        // Conversion DTO vers entité
        Client client = convertCreateDtoToEntity(createClientDto);

        // Sauvegarde
        Client savedClient = clientRepository.save(client);
        logger.info("Client créé avec succès. ID: {}, Email: {}", savedClient.getId(), savedClient.getEmail());

        // Conversion entité vers DTO de réponse
        return convertEntityToDto(savedClient);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientDto getClientById(Long id) {
        logger.debug("Recherche du client avec l'ID: {}", id);

        Client client = clientRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Client non trouvé avec l'ID: {}", id);
                    return new ClientNotFoundException("Client non trouvé avec l'ID: " + id);
                });

        logger.debug("Client trouvé: {}", client.getEmail());
        return convertEntityToDto(client);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientDto getClientByEmail(String email) {
        logger.debug("Recherche du client avec l'email: {}", email);

        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("Client non trouvé avec l'email: {}", email);
                    return new ClientNotFoundException("Client non trouvé avec l'email: " + email);
                });

        logger.debug("Client trouvé avec l'ID: {}", client.getId());
        return convertEntityToDto(client);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClientDto> getAllClients(Pageable pageable) {
        logger.debug("Récupération de tous les clients. Page: {}, Taille: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<Client> clientsPage = clientRepository.findAll(pageable);
        logger.debug("Nombre de clients trouvés: {}", clientsPage.getTotalElements());

        return clientsPage.map(this::convertEntityToDto);
    }

    @Override
    public ClientDto updateClient(Long id, UpdateClientDto updateClientDto) {
        logger.info("Mise à jour du client avec l'ID: {}", id);

        Client existingClient = clientRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Client non trouvé pour mise à jour avec l'ID: {}", id);
                    return new ClientNotFoundException("Client non trouvé avec l'ID: " + id);
                });

        // Vérification de l'unicité de l'email si modifié
        if (updateClientDto.getEmail() != null &&
                !updateClientDto.getEmail().equals(existingClient.getEmail())) {
            if (clientRepository.existsByEmailAndIdNot(updateClientDto.getEmail(), id)) {
                logger.warn("Tentative de mise à jour avec un email existant: {}", updateClientDto.getEmail());
                throw new EmailAlreadyExistsException("Un client avec cet email existe déjà: " + updateClientDto.getEmail());
            }
        }

        // Mise à jour des champs
        updateEntityFromDto(existingClient, updateClientDto);

        // Sauvegarde
        Client updatedClient = clientRepository.save(existingClient);
        logger.info("Client mis à jour avec succès. ID: {}", updatedClient.getId());

        return convertEntityToDto(updatedClient);
    }

    @Override
    public void deleteClient(Long id) {
        logger.info("Suppression du client avec l'ID: {}", id);

        if (!clientRepository.existsById(id)) {
            logger.warn("Tentative de suppression d'un client inexistant avec l'ID: {}", id);
            throw new ClientNotFoundException("Client non trouvé avec l'ID: " + id);
        }

        clientRepository.deleteById(id);
        logger.info("Client supprimé avec succès. ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClientDto> getClientsByStatus(ClientStatus status, Pageable pageable) {
        logger.debug("Recherche des clients avec le statut: {}", status);

        Page<Client> clientsPage = clientRepository.findByStatus(status, pageable);
        logger.debug("Nombre de clients trouvés avec le statut {}: {}", status, clientsPage.getTotalElements());

        return clientsPage.map(this::convertEntityToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClientDto> getClientsByType(ClientType type, Pageable pageable) {
        logger.debug("Recherche des clients avec le type: {}", type);

        Page<Client> clientsPage = clientRepository.findByType(type, pageable);
        logger.debug("Nombre de clients trouvés avec le type {}: {}", type, clientsPage.getTotalElements());

        return clientsPage.map(this::convertEntityToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClientDto> searchClients(String searchTerm, Pageable pageable) {
        logger.debug("Recherche globale de clients avec le terme: {}", searchTerm);

        Page<Client> clientsPage = clientRepository.findBySearchTerm(searchTerm, pageable);
        logger.debug("Nombre de clients trouvés pour '{}': {}", searchTerm, clientsPage.getTotalElements());

        return clientsPage.map(this::convertEntityToDto);
    }

    @Override
    public ClientDto activateClient(Long id) {
        logger.info("Activation du client avec l'ID: {}", id);

        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException("Client non trouvé avec l'ID: " + id));

        client.setStatus(ClientStatus.ACTIVE);
        Client updatedClient = clientRepository.save(client);

        logger.info("Client activé avec succès. ID: {}", id);
        return convertEntityToDto(updatedClient);
    }

    @Override
    public ClientDto deactivateClient(Long id) {
        logger.info("Désactivation du client avec l'ID: {}", id);

        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException("Client non trouvé avec l'ID: " + id));

        client.setStatus(ClientStatus.INACTIVE);
        Client updatedClient = clientRepository.save(client);

        logger.info("Client désactivé avec succès. ID: {}", id);
        return convertEntityToDto(updatedClient);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        return clientRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public long countClients() {
        return clientRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long countClientsByStatus(ClientStatus status) {
        return clientRepository.countByStatus(status);
    }

    // Méthodes utilitaires de conversion

    private Client convertCreateDtoToEntity(CreateClientDto dto) {
        Client client = new Client();
        client.setFirstName(dto.getFirstName());
        client.setLastName(dto.getLastName());
        client.setEmail(dto.getEmail());
        client.setPhone(dto.getPhone());
        client.setAddress(dto.getAddress());
        client.setCity(dto.getCity());
        client.setPostalCode(dto.getPostalCode());
        client.setCountry(dto.getCountry());
        client.setType(dto.getType());
        client.setStatus(ClientStatus.ACTIVE); // Statut par défaut
        return client;
    }

    private void updateEntityFromDto(Client client, UpdateClientDto dto) {
        if (dto.getFirstName() != null) {
            client.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null) {
            client.setLastName(dto.getLastName());
        }
        if (dto.getEmail() != null) {
            client.setEmail(dto.getEmail());
        }
        if (dto.getPhone() != null) {
            client.setPhone(dto.getPhone());
        }
        if (dto.getAddress() != null) {
            client.setAddress(dto.getAddress());
        }
        if (dto.getCity() != null) {
            client.setCity(dto.getCity());
        }
        if (dto.getPostalCode() != null) {
            client.setPostalCode(dto.getPostalCode());
        }
        if (dto.getCountry() != null) {
            client.setCountry(dto.getCountry());
        }
        if (dto.getStatus() != null) {
            client.setStatus(dto.getStatus());
        }
        if (dto.getType() != null) {
            client.setType(dto.getType());
        }
    }

    private ClientDto convertEntityToDto(Client client) {
        ClientDto dto = new ClientDto();
        dto.setId(client.getId());
        dto.setFirstName(client.getFirstName());
        dto.setLastName(client.getLastName());
        dto.setEmail(client.getEmail());
        dto.setPhone(client.getPhone());
        dto.setAddress(client.getAddress());
        dto.setCity(client.getCity());
        dto.setPostalCode(client.getPostalCode());
        dto.setCountry(client.getCountry());
        dto.setStatus(client.getStatus());
        dto.setType(client.getType());
        dto.setCreatedAt(client.getCreatedAt());
        dto.setUpdatedAt(client.getUpdatedAt());
        return dto;
    }
}