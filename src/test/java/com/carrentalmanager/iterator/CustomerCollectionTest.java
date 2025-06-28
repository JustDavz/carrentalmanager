package com.carrentalmanager.iterator;

import com.carrentalmanager.model.Customer;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di test unitario per il pattern Iterator applicato alla collezione di clienti
 * 
 * Verifica che l'iteratore restituisca i clienti nell'ordine corretto
 */
class CustomerCollectionTest {

    private static final Logger LOGGER = Logger.getLogger(CustomerCollectionTest.class.getName());

    /** Verifica il corretto funzionamento dell'iteratore per CustomerCollection */
    @Test
    void testCustomerIterator() {

        // Crea due clienti
        Customer customer1 = new Customer("CNCDVD97C31H501R", "David", "Conocchioli", "RM12345678", "david.conocchioli@mail.com", "3471115938");
        Customer customer2 = new Customer("RSSLCA90C52F205Z", "Alice", "Rossi", "RM99999999", "alice.rossi@gmail.com", "3334445566");

        LOGGER.info("Clienti creati: " + customer1 + ", " + customer2);

        // Inizializza la collezione
        CustomerCollection collection = new CustomerCollection(Arrays.asList(customer1, customer2));
        Iterator<Customer> iterator = collection.createIterator();
        LOGGER.info("Iteratore creato con 2 clienti");

        // Verifica primo cliente
        assertTrue(iterator.hasNext(), "L'iteratore deve avere almeno un elemento");
        Customer first = iterator.next();
        assertEquals(customer1, first, "Il primo cliente deve essere customer1");
        LOGGER.info("Primo cliente iterato correttamente: " + first);

        // Verifica secondo cliente
        assertTrue(iterator.hasNext(), "L'iteratore deve avere un secondo elemento");
        Customer second = iterator.next();
        assertEquals(customer2, second, "Il secondo cliente deve essere customer2");
        LOGGER.info("Secondo cliente iterato correttamente: " + second);

        // Verifica che non ci siano altri elementi
        assertFalse(iterator.hasNext(), "Non dovrebbero esserci altri clienti");
        LOGGER.info("Fine iterazione: nessun altro cliente presente");

    }
}
