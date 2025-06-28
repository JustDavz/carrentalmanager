package com.carrentalmanager.factory;

import com.carrentalmanager.model.Car;
import com.carrentalmanager.model.CarFuel;
import org.junit.jupiter.api.Test;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di test unitario per la CarFactory
 * 
 * Verifica che la creazione delle auto è consentita solo con parametri validi
 */
class CarFactoryTest {

    private final Logger logger = Logger.getLogger(getClass().getName());

    /** Verifica che la factory crei correttamente un oggetto Car con input validi */
    @Test
    void testValidCarCreation() {
        logger.info("Esecuzione test: creazione auto valida");
        Car car = CarFactory.createCar("GG000HG", "Audi", "RS3", "Premium", "Benzina", "600.0", true);

        assertNotNull(car);
        assertEquals("GG000HG", car.getPlateNumber());
        assertEquals("Audi", car.getBrand());
        assertEquals("RS3", car.getModel());
        assertEquals(CarFuel.BENZINA, car.getFuelType());
        assertTrue(car.isAvailable());
        logger.info("Auto creata correttamente");
    }

    /**
     * Verifica che venga restituito null se la targa non rispetta il formato italiano corretto.
     * Es. "123HGGH" formsto tanga non valido
     */
    @Test
    void testInvalidPlateFormat() {
        logger.info("Esecuzione test: formato targa non valido");
        Car car = CarFactory.createCar("123HGGH", "Audi", "RS3", "Premium", "Benzina", "600.0", true);
        assertNull(car);
        logger.info("Creazione fallita per formato targa inserito non valido");
    }

    /** Verifica che venga restituito null se il prezzo è negativo */
    @Test
    void testInvalidPrice() {
        logger.info("Esecuzione test: prezzo negativo");
        Car car = CarFactory.createCar("GG000HG", "Audi", "RS3", "Premium", "Benzina", "-10", true);
        assertNull(car);
        logger.info("Creazione fallita per prezzo inserito negativo");
    }

    /**
     * Verifica che venga restituito null se il carburante non è tra quelli inseriti
     * Es. se viene inserita Acqua
     */
    @Test
    void testInvalidFuelType() {
        logger.info("Esecuzione test: carburante non valido");
        Car car = CarFactory.createCar("GG000HG", "Audi", "RS3", "Premium", "Acqua", "600.0", true);
        assertNull(car);
        logger.info("Creazione fallita per carburante inserito non valido");
    }
}
