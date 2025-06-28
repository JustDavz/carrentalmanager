package com.carrentalmanager.utils;

import com.carrentalmanager.model.Customer;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test unitari per la classe CustomerFileManager
 * Verifica il corretto salvataggio e lettura dei dati auto da un file CSV
 */
class CustomerFileManagerTest {

    private static final Logger LOGGER = Logger.getLogger(CustomerFileManagerTest.class.getName());
    private static final String CUSTOMER_FILE = "data/customer_filemanager_test.csv"; // File isolato per i test

    /**
     * Eseguito prima di ogni test.
     * Crea la directory, svuota il file CSV e imposta il file manager sul file di test
     */
    @BeforeEach
    void setUp() {
        try {
            Files.createDirectories(Paths.get("data"));
            LOGGER.info("Cartella 'data/' creata o già esistente.");
        } catch (IOException e) {
            fail("Errore nella creazione della cartella data/: " + e.getMessage());
        }

        clearFile(CUSTOMER_FILE);
        CustomerFileManager.getInstance().setFilePath(CUSTOMER_FILE);
        LOGGER.info("Percorso file impostato a: " + CUSTOMER_FILE);
    }

    /** Metodo di utilità per svuotare il file CSV */
    private void clearFile(String path) {
        try {
            File file = new File(path);
            file.getParentFile().mkdirs(); // Crea la cartella genitore se non esiste
            FileWriter writer = new FileWriter(file);
            writer.write(""); // Sovrascrive il contenuto
            writer.close();
            LOGGER.info("File svuotato: " + path);
        } catch (IOException e) {
            fail("Errore nella pulizia del file: " + path + " - " + e.getMessage());
        }
    }

    /** Test singolo cliente: Verifica che un singolo cliente venga salvato e letto nel file csv */
    @Test
    void testSaveAndLoadCustomer() {
        LOGGER.info("Esecuzione test: Verifica che un singolo cliente venga salvato e letto nel file csv");

        Customer customer = new Customer(
                "CNCDVD97C31H501R",
                "David",
                "Conocchioli",
                "RM00000010",
                "david.conocchioli@gmail.com",
                "3471115938"
        );

        CustomerFileManager.getInstance().saveCustomer(customer);
        LOGGER.info("Cliente salvato: " + customer);

        List<Customer> customers = new ArrayList<>();
        CustomerFileManager.getInstance().loadCustomers(customers);
        LOGGER.info("Clienti caricati: " + customers.size());

        assertEquals(1, customers.size(), "Dovrebbe esserci un solo cliente salvato");

        Customer loaded = customers.get(0);
        assertEquals("David", loaded.getFirstName());
        assertEquals("Conocchioli", loaded.getLastName());
        assertEquals("CNCDVD97C31H501R", loaded.getID());
        assertEquals("RM00000010", loaded.getLicenseNumber());
        assertEquals("david.conocchioli@gmail.com", loaded.getEmail());
        assertEquals("3471115938", loaded.getPhone());
    }

    /** Test clienti multipli: Verifica che più clienti vengano salvati e poi letti correttamente */
    @Test
    void testSaveMultipleCustomers() {
        LOGGER.info("Esecuzione test: Verifica che più clienti vengono salvati e poi letti nel file csv");

        List<Customer> testCustomers = List.of(
                new Customer("RSSMRA80A01H501U", "Mario", "Rossi", "RM12345678", "mario.rossi@gmail.com", "3400000001"),
                new Customer("VRDLUI85C41F205R", "Luisa", "Verdi", "MI65432122", "luisa.verdi@gmail.com", "3400000002"),
                new Customer("BNCGLI90T70L219E", "Giulia", "Bianchi", "TO78912355", "giulia.bianchi@gmail.com", "3400000003")
        );

        for (Customer customer : testCustomers) {
            CustomerFileManager.getInstance().saveCustomer(customer);
            LOGGER.info("Cliente salvato: " + customer);
        }

        List<Customer> loadedCustomers = new ArrayList<>();
        CustomerFileManager.getInstance().loadCustomers(loadedCustomers);
        LOGGER.info("Clienti caricati dal file: " + loadedCustomers.size());

        assertEquals(3, loadedCustomers.size(), "Dovrebbero essere caricati 3 clienti");

        Customer giulia = loadedCustomers.get(2);
        assertEquals("Giulia", giulia.getFirstName());
        assertEquals("Bianchi", giulia.getLastName());
        assertEquals("BNCGLI90T70L219E", giulia.getID());
    }
}
