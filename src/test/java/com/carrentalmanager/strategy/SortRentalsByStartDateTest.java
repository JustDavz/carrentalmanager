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
 * Classe di test unitario per la strategia SortRentalsByStartDate
 * Verifica che i noleggi vengano ordinati correttamente in ordine crescente di data di inizio
 */
class SortRentalsByStartDateTest {

    private static final Logger LOGGER = Logger.getLogger(SortRentalsByStartDateTest.class.getName());

    private List<Rental> rentals;
    private SortRentalsByStartDate sortStrategy;

    /** Inizializza la strategia di ordinamento e crea una lista di noleggi con date di inizio differenti e disordinate */
    @BeforeEach
    void setUp() {

        sortStrategy = new SortRentalsByStartDate();

        Customer customer = new Customer(
            "CNCDVD97C31H501R", "David", "Conocchioli",
            "RM00000001", "david.conocchioli@gmail.com", "3471115938"
        );

        Car car1 = new Car("AB123CD", "Fiat", "500", CarCategory.BASIC, CarFuel.BENZINA, 100.0, true);
        Car car2 = new Car("EF456GH", "BMW", "320i", CarCategory.PREMIUM, CarFuel.DIESEL, 200.0, true);
        Car car3 = new Car("IJ789KL", "Tesla", "Model 3", CarCategory.LUXURY, CarFuel.ELETTRICO, 300.0, true);

        Rental rental1 = new RentalBuilder(customer, car1, LocalDate.of(2025, 7, 10), LocalDate.of(2025, 7, 12))
            .costStrategy(new BasicRentalStrategy())
            .status(RentalStatus.APERTO)
            .build();

        Rental rental2 = new RentalBuilder(customer, car2, LocalDate.of(2025, 6, 15), LocalDate.of(2025, 6, 20))
            .costStrategy(new PremiumRentalStrategy())
            .status(RentalStatus.APERTO)
            .build();

        Rental rental3 = new RentalBuilder(customer, car3, LocalDate.of(2025, 5, 5), LocalDate.of(2025, 5, 10))
            .costStrategy(new LuxuryRentalStrategy())
            .status(RentalStatus.APERTO)
            .build();

        rentals = new ArrayList<>(Arrays.asList(rental1, rental2, rental3));

        LOGGER.info("3 noleggi disordinati per data di inizio");
    }

    /** Verifica che i noleggi siano ordinati correttamente in base alla data di inizio in ordine crescente */
    @Test
    void testSortByStartDateAscending() {
        LOGGER.info("Esecuzione test: ordinamento noleggi per data di inizio crescente");

        sortStrategy.sort(rentals);

        LocalDate firstDate = rentals.get(0).getStartDate();
        LocalDate secondDate = rentals.get(1).getStartDate();
        LocalDate thirdDate = rentals.get(2).getStartDate();

        LOGGER.info(() -> "Ordine risultante: " + firstDate + ", " + secondDate + ", " + thirdDate);

        assertEquals(LocalDate.of(2025, 5, 5), firstDate, "Il primo noleggio non ha la data di inizio più bassa");
        assertEquals(LocalDate.of(2025, 6, 15), secondDate, "Il secondo noleggio non ha la seconda data corretta");
        assertEquals(LocalDate.of(2025, 7, 10), thirdDate, "Il terzo noleggio non ha la data di inizio più alta");

        LOGGER.info("Test completato: ordinamento per data di inizio verificato con successo");
    }
}
