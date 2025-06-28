package com.carrentalmanager.observer;

import com.carrentalmanager.model.*;
import com.carrentalmanager.builder.RentalBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di test unitari per il componente observer RentalLogger
 * 
 * Verifica che vengano tracciati correttamente i conteggi dei noleggi aperti e chiusi
 */
class RentalLoggerTest {

    private static final Logger LOGGER = Logger.getLogger(RentalLoggerTest.class.getName());

    private RentalLogger rentalLogger;
    private Rental sampleRental;

    /** Inizializza un RentalLogger e un noleggio */
    @BeforeEach
    void setUp() {
        rentalLogger = new RentalLogger();

        Customer customer = new Customer(
                "CNCDVD97C31H501R",
                "David",
                "Conocchioli",
                "RM00000001",
                "david.conocchioli@gmail.com",
                "3471115938"
        );

        Car car = new Car(
                "AB123CD",
                "Fiat",
                "500",
                CarCategory.BASIC,
                CarFuel.BENZINA,
                100.0,
                true
        );

        sampleRental = new RentalBuilder(customer, car,
                LocalDate.of(2025, 6, 22),
                LocalDate.of(2025, 6, 24))
                .status(RentalStatus.APERTO)
                .build();

        LOGGER.info("RentalLogger e noleggio di esempio inizializzati");
    }

    /** Verifica che il conteggio dei noleggi aperti aumenti correttamente */
    @Test
    void testOnRentalStartedIncrementsCounter() {
        LOGGER.info("Esecuzione test: incremento conteggio noleggi aperti");
        rentalLogger.onRentalStarted(sampleRental);
        rentalLogger.onRentalStarted(sampleRental);
        rentalLogger.onRentalStarted(sampleRental);

        int expected = 3;
        int actual = rentalLogger.getOpenedRentalsCount();

        LOGGER.info("Noleggi aperti notificati: " + actual);
        assertEquals(expected, actual, "Il conteggio dei noleggi aperti non è corretto");
        LOGGER.info("Test completato: conteggio noleggi aperti corretto");
    }

    /** Verifica che il conteggio dei noleggi chiusi aumenti correttamente. */
    @Test
    void testOnRentalClosedIncrementsCounter() {
        LOGGER.info("Esecuzione test: incremento conteggio noleggi chiusi");
        rentalLogger.onRentalClosed(sampleRental);
        rentalLogger.onRentalClosed(sampleRental);

        int expected = 2;
        int actual = rentalLogger.getClosedRentalsCount();

        LOGGER.info("Noleggi chiusi notificati: " + actual);
        assertEquals(expected, actual, "Il conteggio dei noleggi chiusi non è corretto");
        LOGGER.info("Test completato: conteggio noleggi chiusi corretto");
    }
}
