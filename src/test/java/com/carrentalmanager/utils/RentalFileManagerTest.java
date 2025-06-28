package com.carrentalmanager.utils;

import com.carrentalmanager.model.*;
import com.carrentalmanager.builder.RentalBuilder;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test unitari per la classe RentalFileManager
 * Verifica il corretto salvataggio e lettura dei noleggi da un file CSV
 */
class RentalFileManagerTest {

    private static final Logger LOGGER = Logger.getLogger(RentalFileManagerTest.class.getName());
    private static final String RENTAL_FILE = "data/rental_filemanager_test.csv";

    /**
     * Eseguito prima di ogni test.
     * Crea la directory, svuota il file CSV e imposta il file manager sul file di test
     */
    @BeforeEach
    void setUp() {
        try {
            Files.createDirectories(Paths.get("data"));
            LOGGER.info("Cartella 'data/' creata o già esistente.");
        } catch (IOException e) {
            fail("Errore nella creazione della cartella data/: " + e.getMessage());
        }

        clearFile(RENTAL_FILE);
        RentalFileManager.getInstance().setFilePath(RENTAL_FILE);
    }

    /** Metodo di utilità per svuotare il contenuto del file CSV di test */
    private void clearFile(String path) {
        try {
            File file = new File(path);
            file.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(file)) {
                writer.write("");
            }
            LOGGER.info("File di test svuotato: " + path);
        } catch (IOException e) {
            fail("Errore nella pulizia del file: " + path);
        }
    }

    /** Test singolo noleggio: verifica che un singolo noleggio venga salvato e letto nel file csv */
    @Test
    void testSaveAndLoadRental() {
        LOGGER.info("Esecuzione test: salvataggio e lettura di un singolo noleggio.");

        Customer customer = new Customer("CNCDVD97C31H501R", "David", "Conocchioli", "RM00000010", "david.conocchioli@gmail.com", "3471115938");
        Car car = new Car("HA777HA", "Audi", "RS3", CarCategory.PREMIUM, CarFuel.BENZINA, 500.0, true);

        Rental rental = new RentalBuilder(customer, car, LocalDate.now(), LocalDate.now().plusDays(3))
                .kasko(true)
                .roadside(false)
                .build();

        RentalFileManager.getInstance().saveRental(rental);

        List<Car> cars = new ArrayList<>();
        cars.add(car);

        List<Rental> rentals = new ArrayList<>();
        RentalFileManager.getInstance().loadRentals(rentals, cars);

        assertEquals(1, rentals.size(), "Deve esserci un solo noleggio caricato");

        Rental loaded = rentals.get(0);
        assertEquals("David", loaded.getCustomer().getFirstName());
        assertEquals("HA777HA", loaded.getCar().getPlateNumber());
        assertEquals(RentalStatus.APERTO, loaded.getStatus());

        LOGGER.info("Test completato con successo: noleggio caricato correttamente.");
    }

    /** Test di noleggi multipli: verifica che più noleggi vengano salvati e letti nel file csv */
    @Test
    void testSaveMultipleRentals() {
        LOGGER.info("Esecuzione test: salvataggio e caricamento di più noleggi.");

        // Auto e clienti di test
        Car car1 = new Car("AA111AA", "Fiat", "Panda", CarCategory.BASIC, CarFuel.BENZINA, 30.0, true);
        Car car2 = new Car("BB222BB", "BMW", "X1", CarCategory.PREMIUM, CarFuel.DIESEL, 70.0, true);

        Customer cust1 = new Customer("RSSMRA80A01H501U", "Mario", "Rossi", "RM00009001", "mari.rossu@gmail.com", "3400000001");
        Customer cust2 = new Customer("BNCGLI90T70L219E", "Giulia", "Bianchi", "MI00022293", "elisa.bianchi@gmail.com", "3400000002");

        // Costruzione dei noleggi
        Rental rental1 = new RentalBuilder(cust1, car1, LocalDate.now(), LocalDate.now().plusDays(2))
                .kasko(false)
                .roadside(true)
                .build();

        Rental rental2 = new RentalBuilder(cust2, car2, LocalDate.now().plusDays(1), LocalDate.now().plusDays(4))
                .kasko(true)
                .roadside(true)
                .build();

        // Salvataggio
        RentalFileManager.getInstance().saveRental(rental1);
        RentalFileManager.getInstance().saveRental(rental2);

        // Lista auto disponibile
        List<Car> cars = List.of(car1, car2);

        // Caricamento noleggi
        List<Rental> loadedRentals = new ArrayList<>();
        RentalFileManager.getInstance().loadRentals(loadedRentals, new ArrayList<>(cars));

        assertEquals(2, loadedRentals.size(), "Devono essere caricati 2 noleggi");

        Rental r2 = loadedRentals.get(1);
        assertEquals("Giulia", r2.getCustomer().getFirstName());
        assertEquals("BMW", r2.getCar().getBrand());
        assertTrue(r2.isKaskoInsurance());
        assertEquals(RentalStatus.APERTO, r2.getStatus());

        LOGGER.info("Test completato con successo: caricati correttamente più noleggi");
    }
}
