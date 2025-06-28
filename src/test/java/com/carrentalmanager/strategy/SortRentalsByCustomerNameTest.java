package com.carrentalmanager.strategy;

import com.carrentalmanager.model.*;
import com.carrentalmanager.builder.RentalBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

/** Test unitario per la strategia di ordinamento dei noleggi in base al nome completo del cliente */
class SortRentalsByCustomerNameTest {

    private static final Logger LOGGER = Logger.getLogger(SortRentalsByCustomerNameTest.class.getName());

    private List<Rental> rentals;
    private SortRentalsByCustomerName sortStrategy;

    /** Crea una lista di noleggi con clienti in ordine casuale */
    @BeforeEach
    void setUp() {
        sortStrategy = new SortRentalsByCustomerName();

        Car car1 = new Car("AA000AA", "Fiat", "500", CarCategory.BASIC, CarFuel.BENZINA, 100.0, true);
        Car car2 = new Car("BB111BB", "Audi", "Q8", CarCategory.PREMIUM, CarFuel.DIESEL, 300.0, true);
        Car car3 = new Car("CC222CC", "Tesla", "Model 3", CarCategory.LUXURY, CarFuel.ELETTRICO, 400.0, true);

        Customer customer1 = new Customer("CNCDVD97C31H501R", "David", "Conocchioli", "RM12345678", "david.conocchioli@gmail.com", "3471115938");
        Customer customer2 = new Customer("BNCNNA88A01F205Y", "Anna", "Bianchi", "MI65432188", "anna.bianchi@gmail.com", "3397654321");
        Customer customer3 = new Customer("VRDMRC90C10Z404P", "Marco", "Verdi", "TO11223333", "marco.verdi@gmail.com", "3289876543");

        Rental rental1 = new RentalBuilder(customer1, car1, LocalDate.of(2025, 7, 1), LocalDate.of(2025, 7, 3))
            .status(RentalStatus.APERTO)
            .costStrategy(new BasicRentalStrategy())
            .build();

        Rental rental2 = new RentalBuilder(customer2, car2, LocalDate.of(2025, 7, 2), LocalDate.of(2025, 7, 4))
            .status(RentalStatus.APERTO)
            .costStrategy(new PremiumRentalStrategy())
            .build();

        Rental rental3 = new RentalBuilder(customer3, car3, LocalDate.of(2025, 7, 3), LocalDate.of(2025, 7, 5))
            .status(RentalStatus.APERTO)
            .costStrategy(new LuxuryRentalStrategy())
            .build();

        rentals = new ArrayList<>(Arrays.asList(rental1, rental2, rental3));
        LOGGER.info("3 noleggi creati con clienti in ordine casuale.");
    }

    /** Verifica che i noleggi vengano ordinati correttamente in base al nome del cliente */
    @Test
    void testSortByCustomerName() {
        LOGGER.info("Esecuzione Test: ordinamento noleggi per nome cliente.");

        sortStrategy.sort(rentals);

        List<String> sortedNames = rentals.stream()
            .map(r -> r.getCustomer().getFullName())
            .toList();

        List<String> expected = Arrays.asList("Anna Bianchi", "David Conocchioli", "Marco Verdi");

        LOGGER.info("Nomi ordinati: " + sortedNames);
        assertEquals(expected, sortedNames, "L'ordinamento dei nomi clienti non è corretto");
        LOGGER.info("Test completato: ordinamento per nome cliente corretto");
    }
}
