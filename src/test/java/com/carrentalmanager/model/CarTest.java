package com.carrentalmanager.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di test unitari per la classe Car
 * 
 * Verifica costruttore, metodi getter/setter e toString()
 */
class CarTest {

    private static final Logger LOGGER = Logger.getLogger(CarTest.class.getName());

    private Car car;

    /** Inizializza un oggetto Car prima di ogni test */
    @BeforeEach
    void setUp() {
        car = new Car("GS961DR", "Audi", "RS3", CarCategory.PREMIUM, CarFuel.BENZINA, 500.0, true);
        LOGGER.info("Oggetto Car inizializzato con dati di esempio: " + car);
    }

    /** Verifica i valori impostati tramite costruttore e restituiti dai getter */
    @Test
    void testConstructorAndGetters() {
        LOGGER.info("Esecuzione test: verifica valori iniziali con getter.");
        
        assertEquals("GS961DR", car.getPlateNumber(), "Targa non corretta.");
        assertEquals("Audi", car.getBrand(), "Marca non corretta.");
        assertEquals("RS3", car.getModel(), "Modello non corretto.");
        assertEquals(CarCategory.PREMIUM, car.getCarCategory(), "Categoria non corretta.");
        assertEquals(CarFuel.BENZINA, car.getFuelType(), "Tipo carburante non corretto.");
        assertEquals(500.0, car.getDailyPrice(), "Prezzo giornaliero errato.");
        assertTrue(car.isAvailable(), "Disponibilità non corretta.");

        LOGGER.info("Test completato: tutti i getter restituiscono i valori corretti.");
    }

    /** Verifica il corretto funzionamento dei metodi setter e la modifica dei valori */
    @Test
    void testSetters() {
        LOGGER.info("Esecuzione test: modifica dei dati tramite setter");

        car.setPlateNumber("HA090HB");
        car.setBrand("FERRARI");
        car.setModel("LaFerrari");
        car.setCarCategory(CarCategory.LUXURY);
        car.setFuelType(CarFuel.BENZINA);
        car.setDailyPrice(1500.0);
        car.setAvailable(false);

        assertEquals("HA090HB", car.getPlateNumber(), "Targa non aggiornata correttamente");
        assertEquals("FERRARI", car.getBrand(), "Marca non aggiornata correttamente");
        assertEquals("LaFerrari", car.getModel(), "Modello non aggiornato correttamente");
        assertEquals(CarCategory.LUXURY, car.getCarCategory(), "Categoria non aggiornata");
        assertEquals(CarFuel.BENZINA, car.getFuelType(), "Tipo carburante non aggiornato");
        assertEquals(1500.0, car.getDailyPrice(), "Prezzo giornaliero non aggiornato");
        assertFalse(car.isAvailable(), "Disponibilità non aggiornata");

        LOGGER.info("Test completato: i setter aggiornano correttamente tutti i campi");
    }

    /** Verifica che il metodo toString() contenga tutti i dati essenziali */
    @Test
    void testToStringContainsAllFields() {
        LOGGER.info("Esecuzione test: verifica del contenuto del metodo toString()");

        String output = car.toString();
        assertTrue(output.contains("Targa: GS961DR"), "Targa non presente nella toString");
        assertTrue(output.contains("Marca: Audi"), "Marca non presente nella toString");
        assertTrue(output.contains("Modello: RS3"), "Modello non presente nella toString");
        assertTrue(output.contains("Categoria: Premium"), "Categoria non presente nella toString");
        assertTrue(output.contains("Carburante: BENZINA"), "Tipo carburante non presente nella toString");
        assertTrue(output.contains("Prezzo giornaliero: 500.0 €"), "Prezzo non presente nella toString");

        LOGGER.info("Test completato: toString() include tutti i campi");
    }
}
