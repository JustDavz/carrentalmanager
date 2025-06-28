package com.carrentalmanager.factory;

import com.carrentalmanager.model.Car;
import com.carrentalmanager.model.CarCategory;
import com.carrentalmanager.model.CarFuel;

import javax.swing.*;

/**
 * CarFactory applica il Factory Pattern per creare oggetti Car validi partendo dai dati inseriti dall’utente
 * 
 * Valida ogni campo come: targa, marca, modello, categoria, carburante, prezzo
 * 
 * Converte i valori testuali negli enum appropriati e gestisce errori tramite l’Exception Shielding Pattern
 * 
 * Se tutti i dati sono corretti, restituisce un oggetto Car; altrimenti mostra un messaggio di errore all’utente e restituisce null.
 */

public class CarFactory {

    /**
     * Costruttore privato per impedire l'istanza diretta della factory
     * 
     * La classe è una utility pura composta solo da metodi statici
     */
    private CarFactory() {}

    /**
     * Crea un nuovo cliente dopo aver validato tutti i dati inseriti
     * 
     * Se qualcosa è inserita in modo errato, mostra un messaggio di errore all’utente e restituisce null
     */
    public static Car createCar(String plateNumber, String brand, String model, String category, String fuelType, String priceText, boolean isAvailable) {
        try {
            // Verifica che nessun campo sia null
            if (plateNumber == null || brand == null || model == null || category == null || fuelType == null || priceText == null) {
                throw new IllegalArgumentException("Nessun campo può essere null.");
            }

            // Verifica che nessun campo sia vuoto o solo spazi
            if (plateNumber.isBlank() || brand.isBlank() || model.isBlank() || category.isBlank() || fuelType.isBlank() || priceText.isBlank()) {
                throw new IllegalArgumentException("Tutti i campi devono essere compilati.");
            }

            // Controllo sul formato della targa che deve essere ad es. AB123CD
            if (!plateNumber.matches("^[A-Z]{2}\\d{3}[A-Z]{2}$")) {
                throw new IllegalArgumentException("Formato targa non valido. (es: AB123CD)");
            }

            // Controllo che la marca sia formata da almeno due lettere o spazi
            if (!brand.matches("^[A-Za-zÀ-ÿ\\s]{2,}$")) {
                throw new IllegalArgumentException("Marca non valida. Usa solo lettere e spazi (min. 2 caratteri).");
            }

            // Verifica che il modello non contenga caratteri speciali non ammessi
            if (!model.matches("^[A-Za-z0-9\\s\\-]{2,}$")) {
                throw new IllegalArgumentException("Modello non valido. Evita caratteri speciali.");
            }

            // Conversione e validazione categoria
            CarCategory carCategory = validateCategory(category);

            // Conversione e validazione carburante
            CarFuel fuel = validateFuel(fuelType);

            // Validazione e conversione del prezzo
            double dailyPrice = parseAndValidatePrice(priceText);

            // Se tutto è valido crea e restituisce l’oggetto Car
            return new Car(plateNumber, brand, model, carCategory, fuel, dailyPrice, isAvailable);

        } catch (IllegalArgumentException e) {
            // Errore controllato che mostra un messaggio specifico
            JOptionPane.showMessageDialog(null, e.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
            return null;
        } catch (Exception e) {
            // Errore imprevisto che mostra un messaggio generico
            JOptionPane.showMessageDialog(null, "Errore imprevisto nella creazione dell’auto.", "Errore critico", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    /**
     * Valida la categoria inserita e la converte in enum CarCategory
     * 
     * Accetta solo categorie Basic, Premium o Luxury
     */
    private static CarCategory validateCategory(String category) {
        try {
            return CarCategory.valueOf(category.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Categoria inserita non valida. Scegli tra: Basic, Premium, Luxury.");
        }
    }

    /**
     * Valida il carburante inserito e lo converte in enum CarFuel
     * 
     * Accetta solo quelli definiti quindi: Benzina, Diesel, Elettrico, Hybrid e Gas
     */
    private static CarFuel validateFuel(String fuelType) {
        try {
            return CarFuel.valueOf(fuelType.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Tipo carburante inserito non valido. Scegli tra: Benzina, Diesel, Elettrico, Hybrid, Gas.");
        }
    }

    /**
     * Valida e converte il prezzo giornaliero da stringa a double
     * 
     * Il prezzo deve essere un numero valido e maggiore di zero
     */
    private static double parseAndValidatePrice(String priceText) {
        try {
            double price = Double.parseDouble(priceText);
            if (price <= 0) {
                throw new IllegalArgumentException("Il prezzo giornaliero deve essere maggiore di zero.");
            }
            return price;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Il prezzo deve essere un numero valido.");
        }
    }
}
