package com.carrentalmanager.model;

/**
 * Enum (serve per definire delle istanze predefinite) che rappresenta la categoria di un'auto
 * 
 * Utilizzato per distinguere le auto in tre categorie differenti: Basic, Premium e Luxury
 */
public enum CarCategory {
    BASIC("Basic"),
    PREMIUM("Premium"),
    LUXURY("Luxury");

    private final String label;

    CarCategory(String label) {
        this.label = label;
    }

    /** Restituisce l'etichetta leggibile della categoria, usata nell'interfaccia utente */
    public String getLabel() {
        return label;
    }

    /** Restituisce l'etichetta leggibile quando l'oggetto è stampato (es. in JComboBox) */
    @Override
    public String toString() {
        return label;
    }
}