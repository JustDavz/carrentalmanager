package com.carrentalmanager.composite;

import com.carrentalmanager.model.Car;
import com.carrentalmanager.model.CarCategory;
import com.carrentalmanager.model.CarFuel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test unitari per il pattern Composite (CarGroup, CarLeaf)
 * 
 * Verifica la struttura gerarchica, il conteggio delle auto disponibili e la gestione delle eccezioni e modifiche dinamiche
 */
class CarCompositeTest {

    private static final Logger LOGGER = Logger.getLogger(CarCompositeTest.class.getName());

    private Car car1;
    private Car car2;
    private Car car3;
    private CarLeaf leaf1;
    private CarLeaf leaf2;
    private CarLeaf leaf3;
    private CarGroup rootGroup;
    private CarGroup luxuryGroup;

    /**
     * Crea una struttura ad albero:
     * - rootGroup contiene leaf1 e luxuryGroup
     * - luxuryGroup contiene leaf2 e leaf3
     */
    @BeforeEach
    void setup() {
        car1 = new Car("AB123CD", "Fiat", "Panda", CarCategory.BASIC, CarFuel.HYBRID, 90.0, true);
        car2 = new Car("EF456GH", "Lamborghini", "Urus", CarCategory.LUXURY, CarFuel.BENZINA, 1200.0, false);
        car3 = new Car("XY789ZT", "Ferrari", "Roma", CarCategory.LUXURY, CarFuel.BENZINA, 1500.0, true);

        leaf1 = new CarLeaf(car1);
        leaf2 = new CarLeaf(car2);
        leaf3 = new CarLeaf(car3);

        luxuryGroup = new CarGroup("Luxury");
        luxuryGroup.addChild(leaf2);
        luxuryGroup.addChild(leaf3);

        rootGroup = new CarGroup("Tutte le auto");
        rootGroup.addChild(leaf1);
        rootGroup.addChild(luxuryGroup);

        LOGGER.info("Struttura Composite inizializzata");
    }

    /**
     * Verifica il numero totale di auto disponibili nella gerarchia.
     * Numero di auto atteso: 2 (Fiat Panda e Ferrari).
     */
    @Test
    void testAvailabilityCountRootGroup() {
        LOGGER.info("Verifica disponibilità auto nel gruppo principale");
        int available = rootGroup.countAvailableCars();
        LOGGER.info("Auto disponibili nella rootGroup: " + available);
        assertEquals(2, available, "Devono risultare 2 auto disponibili quindi Panda e Ferrari");
    }

    /** Verifica che le foglie (CarLeaf) restituiscano correttamente 1 o 0 in base alla disponibilità */
    @Test
    void testLeafAvailabilityCount() {
        LOGGER.info("Verifica disponibilità singole foglie (auto singola)");
        assertEquals(1, leaf1.countAvailableCars());
        assertEquals(0, leaf2.countAvailableCars());
        assertEquals(1, leaf3.countAvailableCars());
    }

    /** Verifica l'aggiunta e la rimozione dinamica di una foglia nel gruppo radice */
    @Test
    void testAddAndRemoveLeaf() {
        Car newCar = new Car("MN123OP", "Audi", "RS6", CarCategory.PREMIUM, CarFuel.BENZINA, 800.0, true);
        CarLeaf newLeaf = new CarLeaf(newCar);

        LOGGER.info("Aggiunta nuova auto: Audi RS6");
        rootGroup.addChild(newLeaf);
        assertEquals(3, rootGroup.countAvailableCars(), "Dopo l'aggiunta la disponibilità deve esssere 3");

        LOGGER.info("Rimozione auto: Audi RS6");
        rootGroup.removeChild(newLeaf);
        assertEquals(2, rootGroup.countAvailableCars(), "Dopo la rimozione deve essere 2");
    }

    /** Verifica che una foglia non possa accettare addChild e removeChild sollevando l'eccezione UnsupportedOperationException */
    @Test
    void testUnsupportedOperationInLeaf() {
        LOGGER.info("Verifica operazioni non permesse su CarLeaf");
        assertThrows(UnsupportedOperationException.class, () -> leaf1.addChild(leaf2));
        assertThrows(UnsupportedOperationException.class, () -> leaf1.removeChild(leaf3));
    }

    /** Verifica il conteggio delle auto disponibili in una struttura con sottogruppi annidati */
    @Test
    void testGroupStructureNestedCount() {
        LOGGER.info("Test con sottogruppo aggiuntivo");
        CarGroup subGroup = new CarGroup("Subgroup");
        Car car4 = new Car("ZZ999ZZ", "Maserati", "Ghibli", CarCategory.LUXURY, CarFuel.BENZINA, 1000.0, true);
        CarLeaf leaf4 = new CarLeaf(car4);
        subGroup.addChild(leaf4);
        luxuryGroup.addChild(subGroup);

        int totalAvailable = rootGroup.countAvailableCars();
        LOGGER.info("Totale vetture disponibili con sottogruppo: " + totalAvailable);
        assertEquals(3, totalAvailable, "Devono risultare 3 auto disponibili");
    }
}
