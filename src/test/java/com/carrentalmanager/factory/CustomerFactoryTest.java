package com.carrentalmanager.factory;

import com.carrentalmanager.model.Customer;
import org.junit.jupiter.api.Test;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di test unitari per la CustomerFactory
 * 
 * Verifica la validazione dei dati nella creazione di un nuovo cliente
 */
class CustomerFactoryTest {

    private final Logger logger = Logger.getLogger(getClass().getName());

    /** Verifica che la creazione di un cliente con dati validi restituisce un oggetto Customer non nullo */
    @Test
    void testValidCustomerCreation() {
        Customer customer = CustomerFactory.createCustomer(
                "CNCDVD97C31H501R", "David", "Conocchioli", "RM00000010", "david.conocchioli@gmail.com", "3471115938");

        assertNotNull(customer);
        assertEquals("David", customer.getFirstName());
        logger.info("Cliente creato correttamente");
    }

    /** Verifica che venga restituito null per un codice fiscale inserito non valido */
    @Test
    void testInvalidID() {
        Customer customer = CustomerFactory.createCustomer(
                "ABCDEFGHILMNOPQRSTUZ0123456789", "David", "Conocchioli", "RM00000010", "david.conocchioli@gmail.com", "3471115938");

        assertNull(customer);
        logger.info("Creazione fallita per inserimento codice fiscale non valido");
    }

    /** Verifica che un nome contenente caratteri speciali venga rifiutato */
    @Test
    void testFirstName() {
        logger.info("Esecuzione test: Nome");
        Customer customer = CustomerFactory.createCustomer(
                "CNCDVD97C31H501R", "D@vid!", "Conocchioli", "RM00000010", "david.conocchioli@gmail.com", "3471115938");

        assertNull(customer);
        logger.info("Creazione fallita per inserimento nome non valido");
    }

    /** Verifica che un cognome con caratteri speciali venga rifiutato */
    @Test
    void testLastName() {
        logger.info("Esecuzione test: cognome non valido");
        Customer customer = CustomerFactory.createCustomer(
                "CNCDVD97C31H501R", "David", "Conocc#hioli", "RM00000010", "david.conocchioli@gmail.com", "3471115938");

        assertNull(customer);
        logger.info("Creazione fallita per inserimento cognome non valido");
    }

    /** Verifica che un'email non valida venga rifiutata */
    @Test
    void testInvalidEmailReturnsNull() {
        logger.info("Esecuzione test: formato email non valido");
        Customer customer = CustomerFactory.createCustomer(
                "CNCDVD97C31H501R", "David", "Conocchioli", "RM00000010", "xxx", "3471115938");

        assertNull(customer);
        logger.info("Creazione fallita per inserimento formato email non valido");
    }

    /** Verifica che tutti i campi vuoti portano al fallimento della creazione */
    @Test
    void testEmptyFields() {
        logger.info("Esecuzione test: campi vuoti");
        Customer customer = CustomerFactory.createCustomer("", "", "", "", "", "");

        assertNull(customer);
        logger.info("Creazione fallita per i campi vuoti");
    }

    /** Verifica che un numero di telefono contenente lettere venga rifiutato */
    @Test
    void testInvalidPhoneNumber() {
        logger.info("Esecuzione test: formato numero di telefono non valido");
        Customer customer = CustomerFactory.createCustomer(
                "CNCDVD97C31H501R", "David", "Conocchioli", "RM00000010", "david.conocchioli@gmail.com", "123ABC");

        assertNull(customer);
        logger.info("Creazione fallita per il numero di telefono non valido");
    }

    /** Verifica che un numero di patente troppo corto venga rifiutato */
    @Test
    void testShortLicenseNumber() {
        logger.info("Esecuzione test: formato numero patente corto");
        Customer customer = CustomerFactory.createCustomer(
                "CNCDVD97C31H501R", "David", "Conocchioli", "RM1", "david.conocchioli@gmail.com", "3471115938");

        assertNull(customer);
        logger.info("Creazione fallita per il numero di patente troppo corto");
    }
}
