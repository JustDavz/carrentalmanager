package com.carrentalmanager.iterator;

import com.carrentalmanager.model.Car;
import com.carrentalmanager.model.CarCategory;
import com.carrentalmanager.model.CarFuel;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di test unitario per il pattern Iterator applicato alla collezione di auto
 * 
 * Verifica che l'iteratore restituisca correttamente gli oggetti Car nell'ordine atteso
 */
class CarCollectionTest {

    private static final Logger LOGGER = Logger.getLogger(CarCollectionTest.class.getName());

    /** Verifica il comportamento dell'iteratore della classe CarCollection */
    @Test
    void testCarIterator() {
        // Crea due istanze di Car
        Car car1 = new Car("GB787GB", "Fiat", "Panda", CarCategory.BASIC, CarFuel.HYBRID, 90.0, true);
        Car car2 = new Car("HA123HA", "Volkswagen", "Golf GTI", CarCategory.PREMIUM, CarFuel.DIESEL, 400.0, true);

        LOGGER.info("Auto create: " + car1 + ", " + car2);

        // Inizializza la CarCollection
        CarCollection collection = new CarCollection(Arrays.asList(car1, car2));
        Iterator<Car> iterator = collection.createIterator();
        LOGGER.info("Iteratore creato con 2 auto");

        // Verifica il primo elemento
        assertTrue(iterator.hasNext(), "L'iteratore deve avere almeno un elemento");
        Car first = iterator.next();
        assertEquals(car1, first, "Il primo elemento deve essere car1");
        LOGGER.info("Primo elemento iterato correttamente: " + first);

        // Verifica il secondo elemento
        assertTrue(iterator.hasNext(), "L'iteratore deve avere un secondo elemento");
        Car second = iterator.next();
        assertEquals(car2, second, "Il secondo elemento deve essere car2");
        LOGGER.info("Secondo elemento iterato correttamente: " + second);

        // Verifica che non ci siano altri elementi
        assertFalse(iterator.hasNext(), "Non ci sono altri elementi");
        LOGGER.info("Fine iterazione: nessun altro elemento presente");
    }
}
