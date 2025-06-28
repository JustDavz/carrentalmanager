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
 * Classe di test unitario per la strategia di ordinamento SortRentalsByPrice
 * Verifica che i noleggi vengano ordinati correttamente in base al prezzo totale in modo crescente
 */
class SortRentalsByPriceTest {

    private static final Logger LOGGER = Logger.getLogger(SortRentalsByPriceTest.class.getName());

    private SortRentalsByPrice sortStrategy;
    private List<Rental> rentals;


    /**
     * Inizializza la strategia e una lista di noleggi con prezzi differenti
     * 
     * Tutti i noleggi fanno riferimento allo stesso cliente ma a veicoli diversi 
    */
    @BeforeEach
    void setUp() {

        sortStrategy = new SortRentalsByPrice();

        Customer customer = new Customer("CNCDVD97C31H501R", "David", "Conocchioli", "RM00000001", "david.conocchioli@gmail.com", "3471115938");

        Car car1 = new Car("AA111AA", "Fiat", "500", CarCategory.BASIC, CarFuel.BENZINA, 100.0, true);
        Car car2 = new Car("BB222BB", "BMW", "320i", CarCategory.PREMIUM, CarFuel.DIESEL, 200.0, true);
        Car car3 = new Car("CC333CC", "Tesla", "Model 3", CarCategory.LUXURY, CarFuel.ELETTRICO, 300.0, true);

        PricingStrategy basic = new BasicRentalStrategy();
        PricingStrategy premium = new PremiumRentalStrategy();
        PricingStrategy luxury = new LuxuryRentalStrategy();

        Rental rental1 = new RentalBuilder(customer, car1, LocalDate.of(2025, 6, 10), LocalDate.of(2025, 6, 12))
            .kasko(false).roadside(false).costStrategy(basic).status(RentalStatus.APERTO).build();

        Rental rental2 = new RentalBuilder(customer, car2, LocalDate.of(2025, 6, 10), LocalDate.of(2025, 6, 12))
            .kasko(false).roadside(false).costStrategy(premium).status(RentalStatus.APERTO).build();

        Rental rental3 = new RentalBuilder(customer, car3, LocalDate.of(2025, 6, 10), LocalDate.of(2025, 6, 12))
            .kasko(false).roadside(false).costStrategy(luxury).status(RentalStatus.APERTO).build();

        rentals = new ArrayList<>(Arrays.asList(rental3, rental1, rental2));

        LOGGER.info("3 noleggi inseriti con prezzi differenti.");
    }

    /** Verifica che i noleggi vengano ordinati correttamente in ordine crescente secondo il prezzo */
    @Test
    void testSortByPriceAscending() {
        LOGGER.info("Esecuzione test: ordinamento noleggi per prezzo crescente");
        sortStrategy.sort(rentals);

        double price1 = rentals.get(0).getPrice();
        double price2 = rentals.get(1).getPrice();
        double price3 = rentals.get(2).getPrice();

        LOGGER.info(() -> "Prezzi ordinati: " + price1 + ", " + price2 + ", " + price3);

        assertTrue(price1 <= price2, "Il primo prezzo non è minore o uguale al secondo");
        assertTrue(price2 <= price3, "Il secondo prezzo non è minore o uguale al terzo");

        LOGGER.info("Test completato: ordinamento per prezzo corretto");
    }
}
