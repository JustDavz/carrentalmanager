package com.carrentalmanager.strategy;

import com.carrentalmanager.model.*;
import com.carrentalmanager.builder.RentalBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test unitari per la strategia di prezzo PremiumRentalStrategy.
 * Verifica il corretto calcolo del costo totale per un noleggio PREMIUM in tutte le combinazioni possibili di servizi extra.
 */
class PremiumRentalStrategyTest {

    private static final Logger LOGGER = Logger.getLogger(PremiumRentalStrategyTest.class.getName());

    private Customer customer;
    private Car car;
    private Rental rental;
    private PremiumRentalStrategy strategy;

    /** Inizializzazione di noleggio PREMIUM per 3 giorni inclusi, con assicurazione kasko e assistenza stradale attivi */
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

        car = new Car(
                "GD123GD",
                "Audi",
                "A6",
                CarCategory.PREMIUM,
                CarFuel.DIESEL,
                250.0,
                true
        );

        strategy = new PremiumRentalStrategy();

        rental = new RentalBuilder(customer, car,
                LocalDate.of(2025, 6, 25),
                LocalDate.of(2025, 6, 27)) // 3 giorni inclusi
                .kasko(true)
                .roadside(true)
                .costStrategy(strategy)
                .status(RentalStatus.APERTO)
                .build();

        LOGGER.info("Noleggio PREMIUM di 3 giorni con kasko e assistenza attivi");
    }

    /** Verifica il prezzo totale con kasko e assistenza stradale attivi */
    @Test
    void testCalculateTotalPriceWithExtras() {
        LOGGER.info("Test: costo totale con kasko e assistenza stradale attivi");

        double expectedBase = 250.0 * 3;
        double expectedKasko = 200.0 * 3;
        double expectedRoadside = 50.0 * 3;
        double expectedTotal = expectedBase + expectedKasko + expectedRoadside;

        double actualTotal = strategy.calculatePrice(rental);

        LOGGER.info("Prezzo atteso: " + expectedTotal + " €, Prezzo calcolato: " + actualTotal + " €");
        assertEquals(expectedTotal, actualTotal, 0.001, "Prezzo totale errato con tutti i servizi extra attivi");
    }

    /** Verifica il prezzo totale senza alcun servizio extra attivo */
    @Test
    void testCalculateTotalPriceWithoutExtras() {
        LOGGER.info("Test: costo totale senza servizi extra");

        rental.setKaskoInsurance(false);
        rental.setRoadsideAssistance(false);

        double expectedBase = 250.0 * 3;
        double actualTotal = strategy.calculatePrice(rental);

        LOGGER.info("Prezzo atteso: " + expectedBase + " €, Prezzo calcolato: " + actualTotal + " €");
        assertEquals(expectedBase, actualTotal, 0.001, "Prezzo totale errato senza servizi extra");
    }

    /** Verifica il prezzo totale con solo assicurazione kasko attiva */
    @Test
    void testCalculateTotalPriceOnlyKasko() {
        LOGGER.info("Test: costo totale con solo assicurazione kasko attiva");

        rental.setKaskoInsurance(true);
        rental.setRoadsideAssistance(false);

        double expected = (250.0 + 200.0) * 3;
        double actual = strategy.calculatePrice(rental);

        LOGGER.info("Prezzo atteso: " + expected + " €, Prezzo calcolato: " + actual + " €");
        assertEquals(expected, actual, 0.001, "Prezzo totale errato con solo kasko attivo");
    }

    /** Verifica il prezzo totale con solo assistenza stradale attiva */
    @Test
    void testCalculateTotalPriceOnlyRoadside() {
        LOGGER.info("Test: costo totale con solo assistenza stradale attiva");

        rental.setKaskoInsurance(false);
        rental.setRoadsideAssistance(true);

        double expected = (250.0 + 50.0) * 3;
        double actual = strategy.calculatePrice(rental);

        LOGGER.info("Prezzo atteso: " + expected + " €, Prezzo calcolato: " + actual + " €");
        assertEquals(expected, actual, 0.001, "Prezzo totale errato con solo assistenza stradale attiva");
    }
}
