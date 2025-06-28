package com.carrentalmanager.model;

import com.carrentalmanager.builder.RentalBuilder;
import com.carrentalmanager.strategy.PremiumRentalStrategy;
import com.carrentalmanager.strategy.PricingStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di test unitari per la classe Rental
 * 
 * Verifica builder, servizi extra, strategia di prezzo e chiusura noleggio
 */
class RentalTest {

    private static final Logger LOGGER = Logger.getLogger(RentalTest.class.getName());

    private Customer customer;
    private Car car;
    private Rental rental;
    private PricingStrategy strategy;

    /** Inizializza un noleggio con auto categoria Premium con assicurazione Kasko, Soccorso stradale e strategia prezzo */
    @BeforeEach
    void setUp() {
        customer = new Customer(
                "CNCDVD97C3H501R",
                "David",
                "Conocchioli",
                "RM00000001",
                "david.conocchioli@gmail.com",
                "3471115938"
        );

        car = new Car(
                "HA001HA",
                "Audi",
                "RS3",
                CarCategory.PREMIUM,
                CarFuel.BENZINA,
                500.0,
                true
        );

        strategy = new PremiumRentalStrategy();

        rental = new RentalBuilder(customer, car,
                LocalDate.of(2025, 6, 21),
                LocalDate.of(2025, 6, 23))
                .kasko(true)
                .roadside(true)
                .costStrategy(strategy)
                .build();

        LOGGER.info("Noleggio inizializzato: " + rental);
    }

    /** Verifica i dettagli del noleggio */
    @Test
    void testRentalDetails() {
        LOGGER.info("Verifica dettagli del noleggio");
        assertEquals(customer, rental.getCustomer(), "Cliente inserito non corretto");
        assertEquals(car, rental.getCar(), "Auto inserita non corretta");
        assertEquals(LocalDate.of(2025, 6, 21), rental.getStartDate(), "Data inizio errata");
        assertEquals(LocalDate.of(2025, 6, 23), rental.getEndDate(), "Data fine errata");
        assertTrue(rental.isKaskoInsurance(), "Assicurazione Kasko non impostata");
        assertTrue(rental.isRoadsideAssistance(), "Assistenza stradale non impostata");
        assertEquals(RentalStatus.APERTO, rental.getStatus(), "Stato iniziale errato");
        LOGGER.info("Dettagli noleggio verificati");
    }

    /** Verifica il calcolo dei giorni del noleggio */
    @Test
    void testRentalDays() {
        LOGGER.info("Verifica calcolo giorni di noleggio");
        assertEquals(3, rental.getRentalDays(), "Numero di giorni calcolato in modo errato");
        LOGGER.info("Giorni di noleggio calcolati correttamente: " + rental.getRentalDays());
    }

    /** Verifica il calcolo del prezzo totale utilizzando la strategia premium */
    @Test
    void testCalculateTotalPrice() {
        LOGGER.info("Verifica calcolo prezzo totale con strategia Premium");
        double expected = strategy.calculatePrice(rental);
        double actual = rental.calculateTotalPrice();
        assertEquals(expected, actual, 0.001, "Prezzo calcolato in modo errato");
        LOGGER.info("Prezzo calcolato correttamente: " + actual + " €");
    }

    /** Verifica l'aggiornamento delle diponibilità auto alla chiusura del noleggio */
    @Test
    void testCloseRentalUpdatesCarAvailability() {
        LOGGER.info("Verifica aggiornamento disponibilità auto alla chiusura noleggio");
        assertFalse(car.isAvailable(), "L'auto dovrebbe essere inizialmente non disponibile");
        rental.setStatus(RentalStatus.CHIUSO);
        assertEquals(RentalStatus.CHIUSO, rental.getStatus(), "Stato del noleggio non aggiornato");
        assertTrue(car.isAvailable(), "L'auto non è tornata disponibile dopo la chiusura");
        LOGGER.info("Auto disponibile dopo la chiusura del noleggio");
    }

    /** Verifica il contenuto del metodo toString() */
    @Test
    void testToStringContainsCorrectInfo() {
        LOGGER.info("Verifica contenuto del metodo toString()");
        String result = rental.toString();
        assertTrue(result.contains("David Conocchioli"), "Nome cliente assente");
        assertTrue(result.contains("Audi"), "Marca auto assente");
        assertTrue(result.contains("RS3"), "Modello auto assente");
        assertTrue(result.contains("2025-06-21"), "Data inizio assente");
        assertTrue(result.contains("2025-06-23"), "Data fine assente");
        LOGGER.info("Metodo toString contiene tutte le informazioni essenziali");
    }
}
