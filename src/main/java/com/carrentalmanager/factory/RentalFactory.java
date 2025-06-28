package com.carrentalmanager.factory;

import com.carrentalmanager.model.*;
import com.carrentalmanager.builder.RentalBuilder;

import javax.swing.*;
import java.time.LocalDate;
import java.util.logging.Logger;

/**
 * RentalFactory applica il pattern Factory per creare oggetti Rental(noleggio) validi partendo dai dati inseriti dall’utente
 * 
 * Integra il Builder Pattern per configurare il noleggio con servizi opzionali come assicurazione Kasko e assistenza stradale
 * 
 * Se i dati sono corretti, restituisce un oggetto Rental altrimenti mostra un messaggio di errore all’utente e annulla la creazione
 * 
 * Utilizza l’Exception Shielding Pattern per intercettare e gestire in modo sicuro gli errori
 */

public class RentalFactory {

    // Logger per tracciare errori e informazioni durante la creazione del noleggio
    private static final Logger logger = Logger.getLogger(RentalFactory.class.getName());

    /**
     * Costruttore privato per impedire l'istanza diretta della factory
     * 
     * La classe è una utility pura composta solo da metodi statici
     */       
    private RentalFactory() {}

    /**
     * Crea un nuovo noleggio dopo aver verificato la validità dei dati
     * 
     * Se qualcosa è inserita in modo errato, mostra un messaggio d’errore e restituisce null
     */
    public static Rental createRental(Customer customer, Car car, LocalDate startDate, LocalDate endDate, boolean kasko, boolean roadside) {
        try {
            // Verifica che nessun campo sia null
            if (customer == null || car == null || startDate == null || endDate == null) {
                throw new IllegalArgumentException("Tutti i campi obbligatori devono essere compilati.");
            }

            // Verifica che la data di fine non sia precedente alla data di inizio
            if (endDate.isBefore(startDate)) {
                throw new IllegalArgumentException("La data di fine non può precedere quella di inizio.");
            }

            // Controlla che l’auto sia effettivamente disponibile
            if (!car.isAvailable()) {
                throw new IllegalArgumentException("L'auto selezionata non è disponibile.");
            }

            // Creazione del noleggio con il Builder Pattern, specificando anche i servizi extra se tutti i campi sono corretti
            return new RentalBuilder(customer, car, startDate, endDate).kasko(kasko).roadside(roadside).build();

        } catch (IllegalArgumentException e) {
            // Errore controllato che mostra un messaggio specifico
            logger.warning("Errore durante la creazione del noleggio: " + e.getMessage());
            JOptionPane.showMessageDialog(null, e.getMessage(), "Errore di validazione", JOptionPane.ERROR_MESSAGE);
            return null;

        } catch (Exception e) {
            // Errore imprevisto che mostra un messaggio generico
            logger.severe("Errore imprevisto nella creazione del noleggio: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Si è verificato un errore durante la creazione del noleggio.", "Errore critico", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}
