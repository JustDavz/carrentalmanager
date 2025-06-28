package com.carrentalmanager.factory;

import com.carrentalmanager.model.Customer;

import javax.swing.*;
import java.util.logging.Logger;

/**
 * CustomerFactory applica il Factory Pattern per creare oggetti Customer validi partendo dai dati inseriti dall’utente
 * 
 * Valida tutti i campi obbligatori: id (codice fiscale), nome, cognome, numero patente, email e numero di telefono
 * 
 * Se i dati sono validi, restituisce un oggetto Customer. In caso contrario, mostra un messaggio all’utente
 * e annulla la creazione dell’oggetto
 * 
 * Utilizza l’Exception Shielding Pattern per intercettare e gestire in modo sicuro gli errori
 */

public class CustomerFactory {

    // Logger per tracciare errori e informazioni durante la creazione del cliente
    private static final Logger logger = Logger.getLogger(CustomerFactory.class.getName());

    /**
     * Costruttore privato per impedire l'istanza diretta della factory
     * 
     * La classe è una utility pura composta solo da metodi statici
     */
    private CustomerFactory() {}

    /**
     * Crea un nuovo cliente dopo aver validato tutti i dati inseriti
     * 
     * Se qualcosa è inserita in modo errato, mostra un messaggio di errore all’utente e restituisce null
     */
    public static Customer createCustomer(String id, String firstName, String lastName, String licenseNumber, String email, String phone) {
        try {
            
            // Verifica che nessun campo sia null
            if (id == null || firstName == null || lastName == null || licenseNumber == null || email == null || phone == null) {
                throw new IllegalArgumentException("Nessun campo può essere null.");
            }

            // Verifica che nessun campo sia vuoto o solo spazi
            if (id.isBlank() || firstName.isBlank() || lastName.isBlank() || licenseNumber.isBlank() || email.isBlank() || phone.isBlank()) {
                throw new IllegalArgumentException("Tutti i campi devono essere compilati.");
            }

            // Controllo sul formato del codice fiscale che deve essere composto di 16 caratterri alfanumerici
            id = id.toUpperCase();
            if (!id.matches("^[A-Z0-9]{16}$")) {
                throw new IllegalArgumentException("Codice fiscale non valido. Deve essere composto da 16 caratteri alfanumerici.");
            }

            // Controllo sull'inserimento del nome e non sono ammessi caratteri speciali
            if (!firstName.matches("^[A-Za-zÀ-ÿ\\s]{2,}$")) {
                throw new IllegalArgumentException("Nome non valido. Inserisci solo lettere e spazi.");
            }

            // Controllo sull'inserimento del cognome e non sono ammessi caratteri speciali
            if (!lastName.matches("^[A-Za-zÀ-ÿ\\s]{2,}$")) {
                throw new IllegalArgumentException("Cognome non valido. Inserisci solo lettere e spazi.");
            }

            // Controllo sul formato del numero di patente che deve essere composto da 8-16 caratteri
            licenseNumber = licenseNumber.toUpperCase();
            if (!licenseNumber.matches("^[A-Z0-9]{8,16}$")) {
                throw new IllegalArgumentException("Numero patente non valido. Deve essere composto da 8 a 16 caratteri.)");
            }

            // Controllo sul formato dell'email es. info@example.com
            if (!email.matches("^[\\w\\.-]+@[\\w\\.-]+\\.[a-zA-Z]{2,6}$")) {
                throw new IllegalArgumentException("Formato email non valido. Per favore inserisci un formato valido.");
            }

            // Controllo sul formato del numero di telefono
            if (!phone.matches("^\\d{8,15}$")) {
                throw new IllegalArgumentException("Numero di telefono non valido. De essere composto di solo cifre, da 8 a 15.");
            }

            // Se tutto è valido crea e restituisce l’oggetto Customer
            return new Customer(id, firstName, lastName, licenseNumber, email, phone);

        } catch (IllegalArgumentException e) {
            // Errore controllato che mostra un messaggio specifico
            logger.warning("Errore di validazione cliente: " + e.getMessage());
            JOptionPane.showMessageDialog(null, e.getMessage(), "Errore di validazione", JOptionPane.ERROR_MESSAGE);
            return null;

        } catch (Exception e) {
            // Errore imprevisto che mostra un messaggio generico
            logger.severe("Errore imprevisto nella creazione del cliente: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Si è verificato un errore durante la creazione del cliente.", "Errore critico", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}
