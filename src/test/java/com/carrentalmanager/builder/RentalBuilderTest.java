package com.carrentalmanager.builder;

import com.carrentalmanager.model.*;
import com.carrentalmanager.strategy.PremiumRentalStrategy;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

/** Classe di test unitario per verificare la corretta costruzione di un oggetto Rental tramite il RentalBuilder, incluso l'uso della strategia PremiumRentalStrategy */
class RentalBuilderTest {

    // Logger per tracciare il flusso del test
    private static final Logger LOGGER = Logger.getLogger(RentalBuilderTest.class.getName());

    /** Verifica che il builder crea correttamente un oggetto Rental con tutti i campi impostati, inclusi servizi extra, strategia di costo, stato e prezzo */
    @Test
    void testRentalBuilderPremiumStrategy() {

        // Inizializza un cliente
        Customer customer = new Customer("CNCDVD97C31H501R", "David", "Conocchioli", "RM00000001", "david.conocchioli@gmail.com", "3471115938");
        LOGGER.info("Cliente inizializzato");

        // Inizializza un'auto di categoria PREMIUM
        Car car = new Car("HA999HC", "Audi", "RS3", CarCategory.PREMIUM, CarFuel.BENZINA, 800.0, true);
        LOGGER.info("Auto inizializzata");

        // Date del noleggio
        LocalDate start = LocalDate.of(2025, 7, 1);
        LocalDate end = LocalDate.of(2025, 7, 10);
        LOGGER.info("Date del noleggio impostate");

        // Prezzo atteso impostato manualmente
        double expectedPrice = 800.0;

        // Costruzione del noleggio
        Rental rental = new RentalBuilder(customer, car, start, end)
                .kasko(true)
                .roadside(true)
                .costStrategy(new PremiumRentalStrategy())
                .price(expectedPrice)
                .status(RentalStatus.APERTO)
                .build();
        LOGGER.info("Oggetto Rental costruito correttamente");

        // Asserzioni con log
        assertEquals(customer, rental.getCustomer(), "Il cliente deve corrispondere");
        LOGGER.info("Cliente verificato");

        assertEquals(car, rental.getCar(), "L'auto deve corrispondere");
        LOGGER.info("Auto verificata");

        assertEquals(start, rental.getStartDate(), "La data di inizio deve essere corretta");
        assertEquals(end, rental.getEndDate(), "La data di fine deve essere corretta");
        LOGGER.info("Date verificate");

        assertTrue(rental.isKaskoInsurance(), "L'assicurazione Kasko deve essere attiva");
        assertTrue(rental.isRoadsideAssistance(), "L'assistenza stradale deve essere attiva");
        LOGGER.info("Servizi extra verificati");

        assertEquals(expectedPrice, rental.getPrice(), "Il prezzo deve corrispondere");
        assertEquals(RentalStatus.APERTO, rental.getStatus(), "Lo stato del noleggio deve essere APERTO");
        assertTrue(rental.getCostStrategy() instanceof PremiumRentalStrategy, "La strategia deve essere Premium");
        LOGGER.info("Prezzo, stato e strategia verificati");
    }
}
