package com.carrentalmanager.utils;

import com.carrentalmanager.model.Customer;

import java.io.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Gestore Singleton per la gestione dei clienti tramite file CSV
 * 
 * Centralizza tutte le operazioni di salvataggio e caricamento
 * 
 * Ogni cliente è registrato in formato:
 * ID (Codice fiscale), Nome, Cognome, Numero Patente, Email, Telefono
 */
@SuppressWarnings("all")
public class CustomerFileManager {

    // Logger per registrare operazioni ed errori I/O
    private static final Logger LOGGER = Logger.getLogger(CustomerFileManager.class.getName());

    // Percorso del file CSV dei clienti
    private String customerFilePath = "data/customers.csv";

    // Costruttore privato per impedire istanziazione esterna (Singleton)
    private CustomerFileManager() {}

    /** Inner static class per garantire un Singleton lazy e thread-safe */
    private static class Holder {
        private static final CustomerFileManager INSTANCE = new CustomerFileManager();
    }

    /** Restituisce l'unica istanza della classe CustomerFileManager */
    public static CustomerFileManager getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * Imposta un nuovo percorso per il file clienti
     * 
     * Utile per test o ambienti multipli
     */
    public void setFilePath(String path) {
        this.customerFilePath = path;
    }

    /**
     * Salva un nuovo cliente in coda al file CSV
     * 
     * I dati vengono serializzati in formato CSV con separatore virgola
     */
    public void saveCustomer(Customer customer) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(customerFilePath, true))) {
            writer.write(String.join(",",
                    customer.getID(),
                    customer.getFirstName(),
                    customer.getLastName(),
                    customer.getLicenseNumber(),
                    customer.getEmail(),
                    customer.getPhone()));
            writer.newLine();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Errore durante il salvataggio del cliente", e);
        }
    }

    /**
     * Sovrascrive completamente il file CSV con una nuova lista di clienti
     * 
     * Utile in fase di aggiornamento o rimozione
     */
    public void saveCustomers(List<Customer> customerList) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(customerFilePath))) {
            for (Customer customer : customerList) {
                writer.write(String.join(",",
                        customer.getID(),
                        customer.getFirstName(),
                        customer.getLastName(),
                        customer.getLicenseNumber(),
                        customer.getEmail(),
                        customer.getPhone()));
                writer.newLine();
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Errore durante il salvataggio della lista clienti", e);
        }
    }

    /**
     * Carica i clienti presenti nel file CSV nella lista fornita 
     * 
     * La lista viene svuotata prima del caricamento per evitare duplicazioni
     * 
     * Se il file non esiste, l’operazione viene ignorata senza errori
     */
    public void loadCustomers(List<Customer> customerList) {
        customerList.clear();
        File file = new File(customerFilePath);

        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",", -1); // Supporta campi vuoti
                if (data.length == 6) {
                    Customer customer = new Customer(
                            data[0], // ID
                            data[1], // Nome
                            data[2], // Cognome
                            data[3], // Numero patente
                            data[4], // Email
                            data[5]  // Telefono
                    );
                    customerList.add(customer);
                } else {
                    LOGGER.warning("Riga ignorata per formato errato: " + line);
                }
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Errore durante il caricamento dei clienti", e);
        }
    }
}
