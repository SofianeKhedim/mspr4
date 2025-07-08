package com.example.clientapi.repository;

import com.example.clientapi.entity.Client;
import com.example.clientapi.entity.ClientStatus;
import com.example.clientapi.entity.ClientType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'accès aux données des clients.
 *
 * Interface qui étend JpaRepository pour bénéficier des opérations CRUD
 * automatiques et définit des méthodes de recherche personnalisées.
 */
@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    /**
     * Recherche un client par son email.
     *
     * @param email Email du client
     * @return Optional contenant le client si trouvé
     */
    Optional<Client> findByEmail(String email);

    /**
     * Vérifie si un email existe déjà en base.
     *
     * @param email Email à vérifier
     * @return true si l'email existe, false sinon
     */
    boolean existsByEmail(String email);

    /**
     * Vérifie si un email existe pour un autre client (utile pour les mises à jour).
     *
     * @param email Email à vérifier
     * @param id ID du client à exclure de la vérification
     * @return true si l'email existe pour un autre client
     */
    boolean existsByEmailAndIdNot(String email, Long id);

    /**
     * Recherche les clients par statut.
     *
     * @param status Statut des clients
     * @return Liste des clients avec ce statut
     */
    List<Client> findByStatus(ClientStatus status);

    /**
     * Recherche les clients par type.
     *
     * @param type Type de clients
     * @return Liste des clients de ce type
     */
    List<Client> findByType(ClientType type);

    /**
     * Recherche les clients par statut avec pagination.
     *
     * @param status Statut des clients
     * @param pageable Informations de pagination
     * @return Page de clients avec ce statut
     */
    Page<Client> findByStatus(ClientStatus status, Pageable pageable);

    /**
     * Recherche les clients par type avec pagination.
     *
     * @param type Type de clients
     * @param pageable Informations de pagination
     * @return Page de clients de ce type
     */
    Page<Client> findByType(ClientType type, Pageable pageable);

    /**
     * Recherche les clients par nom ou prénom (recherche partielle).
     *
     * @param firstName Prénom (recherche partielle)
     * @param lastName Nom (recherche partielle)
     * @return Liste des clients correspondants
     */
    @Query("SELECT c FROM Client c WHERE " +
            "LOWER(c.firstName) LIKE LOWER(CONCAT('%', :firstName, '%')) OR " +
            "LOWER(c.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))")
    List<Client> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            @Param("firstName") String firstName,
            @Param("lastName") String lastName
    );

    /**
     * Recherche globale dans nom, prénom et email.
     *
     * @param searchTerm Terme de recherche
     * @param pageable Informations de pagination
     * @return Page de clients correspondants
     */
    @Query("SELECT c FROM Client c WHERE " +
            "LOWER(c.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Client> findBySearchTerm(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Recherche les clients créés après une date donnée.
     *
     * @param date Date de référence
     * @return Liste des clients créés après cette date
     */
    List<Client> findByCreatedAtAfter(LocalDateTime date);

    /**
     * Recherche les clients par ville.
     *
     * @param city Ville de recherche
     * @return Liste des clients de cette ville
     */
    List<Client> findByCityIgnoreCase(String city);

    /**
     * Compte le nombre de clients par statut.
     *
     * @param status Statut à compter
     * @return Nombre de clients avec ce statut
     */
    long countByStatus(ClientStatus status);

    /**
     * Compte le nombre de clients par type.
     *
     * @param type Type à compter
     * @return Nombre de clients de ce type
     */
    long countByType(ClientType type);

    /**
     * Recherche les clients avec plusieurs critères.
     *
     * @param status Statut (optionnel)
     * @param type Type (optionnel)
     * @param city Ville (optionnel)
     * @param pageable Informations de pagination
     * @return Page de clients correspondants
     */
    @Query("SELECT c FROM Client c WHERE " +
            "(:status IS NULL OR c.status = :status) AND " +
            "(:type IS NULL OR c.type = :type) AND " +
            "(:city IS NULL OR LOWER(c.city) = LOWER(:city))")
    Page<Client> findByCriteria(
            @Param("status") ClientStatus status,
            @Param("type") ClientType type,
            @Param("city") String city,
            Pageable pageable
    );
}