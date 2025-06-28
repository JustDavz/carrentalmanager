package com.carrentalmanager.factory;

import com.carrentalmanager.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di test unitari per la RentalFactory.
 * Verifica la corretta creazione dei noleggi e la validazione dei dati in input.
 */
class RentalFactoryTest {

    private Customer customer;
    private Car car;
    private final Logger logger = Logger.getLogger(getClass().getName());

    /** Inizializza un cliente e un'auto prima di ogni test. */
    @BeforeEach
    void setup() {
        customer = new Customer(
                "CNCDVD97C31H501R",
                "David",
                "Conocchioli",
                "RM00000010",
                "david.conocchioli@gmail.com",
                "3471115938");

        car = new Car(
                "GG000HG",
                "Audi",
                "RS3",
                CarCategory.PREMIUM,
                CarFuel.BENZINA,
                500.0,
                true); // Auto disponibile per il noleggio
    }

    /** Verifica la creazione di un noleggio valido. */
    @Test
    void testValidRentalCreation() {
        logger.info("Esecuzione test: creazione noleggio valido.");
        Rental rental = RentalFactory.createRental(
                customer,
                car,
                LocalDate.now(),
                LocalDate.now().plusDays(3),
                true,
                true);

        assertNotNull(rental, "Il noleggio è stato creato correttamente");
        assertEquals(customer, rental.getCustomer());
        assertEquals(car, rental.getCar());
        assertTrue(rental.isKaskoInsurance());
        assertTrue(rental.isRoadsideAssistance());
        logger.info("Noleggio creato con successo con assicurazione e assistenza strdale attive");
    }

    /** Verifica che non sia possibile creare un noleggio con data di fine prima della data di inizio */
    @Test
    void testEndDateBeforeStart() {
        logger.info("Esecuzione test: data di fine noleggio precedente alla data di inizio");
        Rental rental = RentalFactory.createRental(
                customer,
                car,
                LocalDate.now(),
                LocalDate.now().minusDays(1), // data fine < inizio
                false,
                false);

        assertNull(rental, "Impossibile creare il noleggio: la data di fine è precedente alla data di inizio");
        logger.info("Impossibile creare il noleggio: per data di fine non valida");
    }

    /** Verifica che non sia possibile noleggiare un'auto non disponibile */
    @Test
    void testUnavailableCar() {
        logger.info("Esecuzione test: tentativo di noleggiare un'auto non disponibile");
        car.setAvailable(false); // Imposta auto come non disponibile

        Rental rental = RentalFactory.createRental(
                customer,
                car,
                LocalDate.now(),
                LocalDate.now().plusDays(2),
                false,
                false);

        assertNull(rental, "Impossibile creare il noleggio: l'auto non è disponibile");
        logger.info("Impossibile creare il noleggio: l'auto non è disponibile per il noleggio");
    }

    /** Verifica che parametri nulli restituiscano null e non creino il noleggio */
    @Test
    void testNullParameters() {
        logger.info("Esecuzione test: parametri nulli nella creazione del noleggio");
        Rental rental = RentalFactory.createRental(
                null,
                null,
                null,
                null,
                false,
                false);

        assertNull(rental, "Impossibile creare il noleggio: se i parametri sono nulli");
        logger.info("Impossibile creare il noleggio: per parametri nulli");
    }

    /** Verifica che un noleggio con stessa data di inizio e fine sia comunque valido */
    @Test
    void testSameStartAndEndDateRentalIsValid() {
        logger.info("Esecuzione test: noleggio con stessa data di inizio e fine");
        LocalDate today = LocalDate.now();

        Rental rental = RentalFactory.createRental(
                customer,
                car,
                today,
                today, // stessa data
                false,
                false);

        assertNotNull(rental, "Il noleggio è valido anche se inizia e finisce lo stesso giorno");
        assertEquals(today, rental.getStartDate());
        assertEquals(today, rental.getEndDate());
        logger.info("Noleggio creato con successo con stessa data di inizio e fine");
    }

    /** Verifica che l'inizializzazione senza servizi extra funzioni correttamente */
    @Test
    void testRentalWithOptionalServicesFalse() {
        logger.info("Esecuzione test: noleggio senza assicurazione e senza assistenza stradale");
        Rental rental = RentalFactory.createRental(
                customer,
                car,
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                false,
                false);

        assertNotNull(rental, "Il noleggio è valido senza servizi opzionali");
        assertFalse(rental.isKaskoInsurance(), "L'assicurazione Kasko è disattivata");
        assertFalse(rental.isRoadsideAssistance(), "L'assistenza stradale è disattivata");
        logger.info("Noleggio creato con successo senza servizi aggiuntivi");
    }
}
