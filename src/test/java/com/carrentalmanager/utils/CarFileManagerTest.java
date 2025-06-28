package com.carrentalmanager.utils;

import com.carrentalmanager.model.Car;
import com.carrentalmanager.model.CarCategory;
import com.carrentalmanager.model.CarFuel;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test unitari per la classe CarFileManager
 * Verifica il corretto salvataggio e lettura dei dati auto da un file CSV
 */
class CarFileManagerTest {

    private static final Logger LOGGER = Logger.getLogger(CarFileManagerTest.class.getName());
    private static final String CAR_FILE = "data/car_filemanager_test.csv"; // Percorso test isolato

    /**
     * Inizializzazione: crea la directory di test e imposta il file temporaneo
     * 
     * Svuota il file prima di ogni test per evitare conflitti
     */
    @BeforeEach
    void setUp() {
        try {
            Files.createDirectories(Paths.get("data"));
            LOGGER.info("Cartella 'data/' creata o già esistente.");
        } catch (IOException e) {
            fail("Errore nella creazione della cartella data/: " + e.getMessage());
        }

        clearFile(CAR_FILE);
        CarFileManager.getInstance().setFilePath(CAR_FILE);
    }

    /** Metodo di utilità per svuotare il file */
    private void clearFile(String path) {
        try {
            File file = new File(path);
            file.getParentFile().mkdirs(); // Crea la directory genitore se non esiste
            FileWriter writer = new FileWriter(file);
            writer.write(""); // Svuota il contenuto
            writer.close();
            LOGGER.info("File svuotato: " + path);
        } catch (IOException e) {
            fail("Errore nella pulizia del file: " + path + " - " + e.getMessage());
        }
    }

    /** Test singola auto: verifica che una sinogla auto venga correttamente salvata e poi letti dal file */
    @Test
    void testSaveAndLoadCar() {
        LOGGER.info("Esecuzione test: verifica che una singola auto viene salvata nel file csv e poi letti");

        Car car = new Car("HA777HA", "Audi", "RS3", CarCategory.PREMIUM, CarFuel.BENZINA, 500.0, true);

        CarFileManager.getInstance().saveCar(car);
        LOGGER.info("Auto salvata: " + car);

        List<Car> cars = new ArrayList<>();
        CarFileManager.getInstance().loadCars(cars);
        LOGGER.info("Auto caricate dal file: " + cars.size());

        assertEquals(1, cars.size(), "Dovrebbe esserci una sola auto salvata");
        Car loaded = cars.get(0);
        assertEquals("HA777HA", loaded.getPlateNumber());
        assertEquals("Audi", loaded.getBrand());
        assertEquals("RS3", loaded.getModel());
        assertEquals(CarCategory.PREMIUM, loaded.getCarCategory());
        assertEquals(CarFuel.BENZINA, loaded.getFuelType());
        assertEquals(500.0, loaded.getDailyPrice());
        assertTrue(loaded.isAvailable());
    }

    /** Test auto multiple: verifica il salvataggio e il caricamento di più auto */
    @Test
    void testSaveMultipleCars() {
        LOGGER.info("Esecuzione test: verifica che un guppo di auto vengono salvate e letti nel file csv");

        List<Car> testCars = List.of(
            new Car("AA111AA", "Fiat", "Panda", CarCategory.BASIC, CarFuel.BENZINA, 35.0, true),
            new Car("BB222BB", "BMW", "320d", CarCategory.PREMIUM, CarFuel.DIESEL, 60.0, true),
            new Car("CC333CC", "Tesla", "Model 3", CarCategory.LUXURY, CarFuel.ELETTRICO, 120.0, false)
        );

        for (Car car : testCars) {
            CarFileManager.getInstance().saveCar(car);
            LOGGER.info("Auto salvata: " + car);
        }

        List<Car> loadedCars = new ArrayList<>();
        CarFileManager.getInstance().loadCars(loadedCars);
        LOGGER.info("Totale auto caricate: " + loadedCars.size());

        assertEquals(3, loadedCars.size(), "Dovrebbero essere caricate 3 auto");

        Car tesla = loadedCars.get(2);
        assertEquals("Tesla", tesla.getBrand());
        assertEquals("Model 3", tesla.getModel());
        assertEquals(CarCategory.LUXURY, tesla.getCarCategory());
        assertEquals(CarFuel.ELETTRICO, tesla.getFuelType());
        assertFalse(tesla.isAvailable());
    }
}
