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
 * Test unitario per la strategia SortRentalsByStatus
 * 
 * Verifica che i noleggi siano ordinati con quelli "APERTO" prima di quelli "CHIUSO"
 */
class SortRentalsByStatusTest {

    private static final Logger LOGGER = Logger.getLogger(SortRentalsByStatusTest.class.getName());

    private List<Rental> rentals;
    private SortRentalsByStatus sortStrategy;

    /** Inizializza la strategia di ordinamento e crea una lista disordinata di noleggi con stati differenti (APERTO, CHIUSO). */
    @BeforeEach
    void setUp() {
        sortStrategy = new SortRentalsByStatus();

        // Cliente
        Customer customer = new Customer("CNCDVD97C31H501R","David","Conocchioli","RM00000001","david.conocchioli@gmail.com","3471115938");

        // Auto
        Car car1 = new Car("AB123CD", "Fiat", "500", CarCategory.BASIC, CarFuel.BENZINA, 100.0, true);
        Car car2 = new Car("EF456GH", "BMW", "320i", CarCategory.PREMIUM, CarFuel.DIESEL, 200.0, true);
        Car car3 = new Car("IJ789KL", "Tesla", "Model 3", CarCategory.LUXURY, CarFuel.ELETTRICO, 300.0, true);

        // Noleggi con stati diversi e ordine casuale
        Rental rental1 = new RentalBuilder(customer, car1,
            LocalDate.of(2025, 6, 10), LocalDate.of(2025, 6, 12))
            .costStrategy(new BasicRentalStrategy())
            .status(RentalStatus.APERTO)
            .build();

        Rental rental2 = new RentalBuilder(customer, car2,
            LocalDate.of(2025, 6, 15), LocalDate.of(2025, 6, 20))
            .costStrategy(new PremiumRentalStrategy())
            .status(RentalStatus.CHIUSO)
            .build();

        Rental rental3 = new RentalBuilder(customer, car3,
            LocalDate.of(2025, 6, 1), LocalDate.of(2025, 6, 5))
            .costStrategy(new LuxuryRentalStrategy())
            .status(RentalStatus.APERTO)
            .build();

        rentals = new ArrayList<>(Arrays.asList(rental2, rental1, rental3));

        LOGGER.info("3 noleggi inseriti: 2 APERTI e 1 CHIUSO.");
    }

    /** Verifica che i noleggi con stato "APERTO" compaiano prima di quelli "CHIUSO" dopo l'ordinamento */
    @Test
    void testSortByStatusOpenFirst() {
        LOGGER.info("Esecuzione test: ordinamento dei noleggi per stato APERTO prima di CHIUSO");

        sortStrategy.sort(rentals);

        LOGGER.info(() -> String.format("Ordine dopo sort: %s, %s, %s",
            rentals.get(0).getStatus(),
            rentals.get(1).getStatus(),
            rentals.get(2).getStatus()
        ));

        assertEquals(RentalStatus.APERTO, rentals.get(0).getStatus(), "Il primo noleggio è APERTO");
        assertEquals(RentalStatus.APERTO, rentals.get(1).getStatus(), "Il secondo noleggio è APERTO");
        assertEquals(RentalStatus.CHIUSO, rentals.get(2).getStatus(), "Il terzo noleggio è CHIUSO");

        LOGGER.info("Test completato: lo stato dei noleggi è stato ordinato correttamente");
    }
}
