package com.carrentalmanager.strategy;

import com.carrentalmanager.model.*;
import com.carrentalmanager.builder.RentalBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test unitario per la strategia SortRentalsByEndDateDesc che ordina i noleggi
 * per data di fine in ordine decrescente dal più recente al meno recente.
 */
class SortRentalsByEndDateDescTest {

    private static final Logger LOGGER = Logger.getLogger(SortRentalsByEndDateDescTest.class.getName());

    private SortRentalsByEndDateDesc sortStrategy;
    private List<Rental> rentals;

    /**
     * Inizializza una lista di noleggi con date di fine differenti
     * 
     * Tutti i noleggi fanno riferimento allo stesso cliente ma a veicoli diversi
     * 
    */
    @BeforeEach
    void setUp() {

        sortStrategy = new SortRentalsByEndDateDesc();

        Customer customer = new Customer(
            "CNCDVD97C31H501R",
            "David",
            "Conocchioli",
            "RM00000001",
            "david.conocchioli@gmail.com",
            "3471115938"
        );

        Car car1 = new Car("AA111AA", "Fiat", "Punto", CarCategory.BASIC, CarFuel.BENZINA, 100.0, true);
        Car car2 = new Car("BB222BB", "Audi", "A3", CarCategory.PREMIUM, CarFuel.DIESEL, 200.0, true);
        Car car3 = new Car("CC333CC", "Tesla", "Model Y", CarCategory.LUXURY, CarFuel.ELETTRICO, 300.0, true);

        Rental rental1 = new RentalBuilder(customer, car1, LocalDate.of(2025, 6, 10), LocalDate.of(2025, 6, 15))
            .status(RentalStatus.APERTO).build();
        Rental rental2 = new RentalBuilder(customer, car2, LocalDate.of(2025, 6, 12), LocalDate.of(2025, 6, 20))
            .status(RentalStatus.APERTO).build();
        Rental rental3 = new RentalBuilder(customer, car3, LocalDate.of(2025, 6, 8), LocalDate.of(2025, 6, 12))
            .status(RentalStatus.APERTO).build();

        rentals = new ArrayList<>(Arrays.asList(rental1, rental2, rental3));

        LOGGER.info("3 noleggi creati con date di fine differenti");
    }

    /** Verifica che i noleggi vengano ordinati correttamente in ordine decrescente secondo la data di fine. */
    @Test
    void testSortByEndDateDescending() {
        LOGGER.info("Esecuzione test: ordinamento per data di fine in ordine decrescente");
        sortStrategy.sort(rentals);

        assertEquals(LocalDate.of(2025, 6, 20), rentals.get(0).getEndDate());
        assertEquals(LocalDate.of(2025, 6, 15), rentals.get(1).getEndDate());
        assertEquals(LocalDate.of(2025, 6, 12), rentals.get(2).getEndDate());
        LOGGER.info("Test completato: ordinamento corretto verificato");
    }
}
