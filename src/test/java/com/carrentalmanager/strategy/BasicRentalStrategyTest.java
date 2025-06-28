package com.carrentalmanager.strategy;

import com.carrentalmanager.model.*;
import com.carrentalmanager.builder.RentalBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test unitari per la strategia di prezzo BasicRentalStrategy
 * Verifica il corretto calcolo del costo totale di un noleggio BASIC con tutte le combinazioni dei servizi extra come assicurazione kasko e assistenza stradale
 */
class BasicRentalStrategyTest {

    private static final Logger LOGGER = Logger.getLogger(BasicRentalStrategyTest.class.getName());

    private Customer customer;
    private Car car;
    private Rental rental;
    private BasicRentalStrategy strategy;

    /** Inizializzazione di un cliente, un'auto BASIC e un noleggio di 3 giorni con entrambi i servizi extra inizialmente attivi. */
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
                "HB006HB",
                "Fiat",
                "PANDA",
                CarCategory.BASIC,
                CarFuel.BENZINA,
                150.0,
                true
        );

        strategy = new BasicRentalStrategy();

        rental = new RentalBuilder(customer, car,
                LocalDate.of(2025, 6, 25),
                LocalDate.of(2025, 6, 27)) // 3 giorni inclusi
                .kasko(true)
                .roadside(true)
                .costStrategy(strategy)
                .status(RentalStatus.APERTO)
                .build();

        LOGGER.info("Noleggio BASIC di 3 giorni con assicurazione kasko e assistenza stradale attivi");
    }

    /** Verifica il costo totale con entrambi i servizi extra attivati */
    @Test
    void testCalculateTotalPriceWithExtras() {
        LOGGER.info("Esecuzione Test: costo totale con assicurazione kasko e assistenza stradale attivi");

        double expectedBase = 150.0 * 3;
        double expectedKasko = 100.0 * 3;
        double expectedRoadside = 50.0 * 3;
        double expectedTotal = expectedBase + expectedKasko + expectedRoadside;

        double actualTotal = strategy.calculatePrice(rental);

        LOGGER.info("Prezzo atteso: " + expectedTotal + " €, Prezzo calcolato: " + actualTotal + " €");
        assertEquals(expectedTotal, actualTotal, 0.001, "Prezzo totale errato con entrambi i servizi attivi");
    }

    /** Verifica il costo totale senza alcun servizio extra */
    @Test
    void testCalculateTotalPriceWithoutExtras() {
        LOGGER.info("Test: costo totale senza servizi extra");

        rental.setKaskoInsurance(false);
        rental.setRoadsideAssistance(false);

        double expectedBase = 150.0 * 3;
        double actualTotal = strategy.calculatePrice(rental);

        LOGGER.info("Prezzo atteso: " + expectedBase + " €, Prezzo calcolato: " + actualTotal + " €");
        assertEquals(expectedBase, actualTotal, 0.001, "Prezzo totale errato senza servizi extra");
    }

    /** Verifica il costo totale con solo assicurazione kasko attiva */
    @Test
    void testCalculateTotalPriceOnlyKasko() {
        LOGGER.info("Esecuzione Test: costo totale con solo assicurazione kasko attiva");

        rental.setKaskoInsurance(true);
        rental.setRoadsideAssistance(false);

        double expected = (150.0 + 100.0) * 3;
        double actual = strategy.calculatePrice(rental);

        LOGGER.info("Prezzo atteso: " + expected + " €, Prezzo calcolato: " + actual + " €");
        assertEquals(expected, actual, 0.001, "Prezzo totale errato con solo assicurazione kasko attiva");
    }

    /** Verifica il costo totale con solo assistenza stradale attiva */
    @Test
    void testCalculateTotalPriceOnlyRoadside() {
        LOGGER.info("Esecuzione Test: costo totale con solo assistenza stradale attiva");

        rental.setKaskoInsurance(false);
        rental.setRoadsideAssistance(true);

        double expected = (150.0 + 50.0) * 3;
        double actual = strategy.calculatePrice(rental);

        LOGGER.info("Prezzo atteso: " + expected + " €, Prezzo calcolato: " + actual + " €");
        assertEquals(expected, actual, 0.001, "Prezzo totale errato con solo assistenza stradale attiva");
    }
}
