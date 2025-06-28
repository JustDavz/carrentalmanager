package com.carrentalmanager.iterator;

import com.carrentalmanager.model.*;
import com.carrentalmanager.builder.RentalBuilder;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di test unitario per verificare il corretto funzionamento dell'iteratore
 * applicato alla collezione di noleggi (RentalCollection).
 */
class RentalCollectionTest {

    private static final Logger LOGGER = Logger.getLogger(RentalCollectionTest.class.getName());

    /**
     * Verifica che l’iteratore restituisca correttamente i noleggi nell’ordine di inserimento.
     */
    @Test
    void testRentalIterator() {

        // Crea un cliente
        Customer customer = new Customer(
                "CNCDVD97C31H501R",
                "David",
                "Conocchioli",
                "RM12345678",
                "david.conocchioli@gmail.com",
                "3471115938"
        );

        // Crea un'auto
        Car car = new Car(
                "GG000HG",
                "Audi",
                "RS3",
                CarCategory.PREMIUM,
                CarFuel.BENZINA,
                500.0,
                true
        );

        LOGGER.info("Cliente e auto creati: " + customer + " - " + car);

        // Crea due noleggi con stati differenti
        Rental rental1 = new RentalBuilder(customer, car, LocalDate.now(), LocalDate.now().plusDays(1))
                .status(RentalStatus.APERTO)
                .build();

        Rental rental2 = new RentalBuilder(customer, car, LocalDate.now(), LocalDate.now().plusDays(2))
                .status(RentalStatus.CHIUSO)
                .build();

        LOGGER.info("Noleggi creati: \n1: " + rental1 + "\n2: " + rental2);

        // Inizializza la collezione e ottiene l'iteratore
        RentalCollection collection = new RentalCollection(Arrays.asList(rental1, rental2));
        Iterator<Rental> iterator = collection.createIterator();
        LOGGER.info("Iteratore di noleggi creato");

        // Verifica primo noleggio
        assertTrue(iterator.hasNext(), "Ci deve essere almeno un noleggio");
        Rental first = iterator.next();
        assertEquals(rental1, first, "Il primo noleggio deve essere rental1");
        LOGGER.info("Primo noleggio iterato correttamente: " + first);

        // Verifica secondo noleggio
        assertTrue(iterator.hasNext(), "Ci deve essere un secondo noleggio");
        Rental second = iterator.next();
        assertEquals(rental2, second, "Il secondo noleggio deve essere rental2.");
        LOGGER.info("Secondo noleggio iterato correttamente: " + second);

        // Verifica che non ci siano altri noleggi
        assertFalse(iterator.hasNext(), "Non dovrebbero esserci altri noleggi");
        LOGGER.info("Fine iterazione: nessun altro noleggio presente");

    }
}
