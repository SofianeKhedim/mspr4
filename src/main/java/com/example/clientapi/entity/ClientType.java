package com.example.clientapi.entity;

/**
 * Énumération représentant le type de client.
 */
public enum ClientType {
    INDIVIDUAL("Particulier"),
    PROFESSIONAL("Professionnel"),
    DISTRIBUTOR("Distributeur");

    private final String displayName;

    ClientType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}