package com.carrentalmanager.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di test per la classe Customer
 * 
 * Verifica costruttore, metodi getter/setter e metodi utility come getFullName() e toString()
 */
class CustomerTest {

    private static final Logger LOGGER = Logger.getLogger(CustomerTest.class.getName());

    private Customer customer;

    /** Inizializza un oggetto Customer prima di ogni test */
    @BeforeEach
    void setUp() {
        customer = new Customer(
                "CNCDVD97C31H501R",
                "David",
                "Conocchioli",
                "RM00000001",
                "david.conocchioli@gmail.com",
                "3471115938"
        );
        LOGGER.info("Oggetto Customer inizializzato con valori: " + customer);
    }

    /** Verifica che i getter restituiscano i valori impostati nel costruttore */
    @Test
    void testConstructorAndGetters() {
        LOGGER.info("Esecuzione test: verifica getter su oggetto Customer");
        assertEquals("CNCDVD97C31H501R", customer.getID(), "Codice fiscale errato");
        assertEquals("David", customer.getFirstName(), "Nome errato");
        assertEquals("Conocchioli", customer.getLastName(), "Cognome errato");
        assertEquals("RM00000001", customer.getLicenseNumber(), "Numero patente errato");
        assertEquals("david.conocchioli@gmail.com", customer.getEmail(), "Email errata");
        assertEquals("3471115938", customer.getPhone(), "Numero di telefono errato");
        LOGGER.info("Test completato: tutti i getter funzionano correttamente");
    }

    /** Verifica che i setter aggiornino correttamente i valori */
    @Test
    void testSetters() {
        LOGGER.info("Esecuzione test: aggiornamento valori tramite setter");
        customer.setID("XYZ9876543");
        customer.setFirstName("Luca");
        customer.setLastName("Bianchi");
        customer.setLicenseNumber("PA01236789");
        customer.setEmail("luca.bianchi@gmail.com");
        customer.setPhone("3317652200");

        assertEquals("XYZ9876543", customer.getID(), "Codice fiscale non aggiornato");
        assertEquals("Luca", customer.getFirstName(), "Nome non aggiornato");
        assertEquals("Bianchi", customer.getLastName(), "Cognome non aggiornato");
        assertEquals("PA01236789", customer.getLicenseNumber(), "Numero patente non aggiornato");
        assertEquals("luca.bianchi@gmail.com", customer.getEmail(), "Email non aggiornata");
        assertEquals("3317652200", customer.getPhone(), "Telefono non aggiornato");
        LOGGER.info("Test completato: tutti i setter aggiornano correttamente i campi");
    }

    /** Verifica il metodo getFullName */
    @Test
    void testGetFullName() {
        LOGGER.info("Esecuzione test: verifica metodo getFullName.");
        assertEquals("David Conocchioli", customer.getFullName(), "Il nome completo non è corretto");
        LOGGER.info("Test completato: getFullName restituisce il valore atteso.");
    }

    /** Verifica che toString() contenga le principali informazioni del cliente */
    @Test
    void testToStringContainsAllFields() {
        LOGGER.info("Esecuzionme test: verifica metodo toString().");
        String output = customer.toString();
        assertTrue(output.contains("David Conocchioli"), "Nome completo assente in toStrin.");
        assertTrue(output.contains("Codice Fiscale: CNCDVD97C31H501R"), "Codice fiscale assente in toString");
        assertTrue(output.contains("Patente: RM00000001"), "Numero patente assente in toString");
        LOGGER.info("Test completato: toString() contiene tutte le informazioni principali");
    }
}
