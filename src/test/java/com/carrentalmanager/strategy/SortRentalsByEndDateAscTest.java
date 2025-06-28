package com.carrentalmanager.strategy;

import com.carrentalmanager.model.*;
import com.carrentalmanager.builder.RentalBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

/** Test unitario per la strategia SortRentalsByEndDateAsc, che ordina i noleggi in base alla data di fine in ordine crescente */
class SortRentalsByEndDateAscTest {

    private static final Logger LOGGER = Logger.getLogger(SortRentalsByEndDateAscTest.class.getName());

    private SortRentalsByEndDateAsc sortStrategy;
    private List<Rental> rentals;

    /**
     * Inizializza una lista di noleggi con date di fine differenti
     * 
     * Tutti i noleggi fanno riferimento allo stesso cliente ma a veicoli diversi
     */
    @BeforeEach
    void setUp() {

        sortStrategy = new SortRentalsByEndDateAsc();

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
    }

    /** Verifica che i noleggi siano ordinati correttamente per data di fine in ordine crescente */
    @Test
    void testSortByEndDateAscending() {
        LOGGER.info("Esecuzione test: ordinamento per data di fine crescente");
        sortStrategy.sort(rentals);

        assertEquals(LocalDate.of(2025, 6, 12), rentals.get(0).getEndDate(), "Il primo noleggio dovrebbe terminare il 12 giugno");
        assertEquals(LocalDate.of(2025, 6, 15), rentals.get(1).getEndDate(), "Il secondo noleggio dovrebbe terminare il 15 giugno");
        assertEquals(LocalDate.of(2025, 6, 20), rentals.get(2).getEndDate(), "Il terzo noleggio dovrebbe terminare il 20 giugno");

        LOGGER.info("Test completato: ordinamento per data di fine corretto");
    }
}
