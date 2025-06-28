package com.carrentalmanager.composite;

import com.carrentalmanager.model.Car;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * CarLeaf rappresenta una singola auto all'interno della gerarchia Composite
 * 
 * È una "foglia" dell'albero: non può contenere altri componenti
 * 
 * Implementa le operazioni definite in CarComponent per visualizzare i dettagli dell'auto
 * e verificarne la disponibilità per il noleggio
 */
public class CarLeaf extends CarComponent {

    // Logger per registrare informazioni e errori relativi alla singola auto
    private static final Logger logger = Logger.getLogger(CarLeaf.class.getName());

    // Riferimento all'oggetto Car associato contenente: marca, modello, targa e stato
    private final Car car;

    /**
     * Costruttore che associa un oggetto Car a questa foglia
     * 
     * L'oggetto Car da rappresentare nel Composite
     */
    public CarLeaf(Car car) {
        this.car = car;
    }

    /**
     * Restituisce 1 se l'auto è disponibile, 0 altrimenti
     * 
     * Utilizzato dai gruppi (CarGroup) per il calcolo ricorsivo delle auto disponibili
     */
    @Override
    public int countAvailableCars() {
        return (car != null && car.isAvailable()) ? 1 : 0;
    }

    /**
     * Stampa le informazioni dell'auto: marca, modello, targa e disponibilità
     * 
     * Se l'oggetto Car è nullo, registra un messaggio di warning nel logger
     * 
     * Altrimenti registra e stampa i dettagli del veicolo.
     */
    @Override
    public void printDetails() {
        if (car == null) {
            logger.warning("Tentativo di visualizzare un'auto nulla.");
            return;
        }

        if (logger.isLoggable(Level.INFO)) {
            logger.log(Level.INFO, () -> String.format(" %s %s (Targa: %s)", car.getBrand(), car.getModel(), car.getPlateNumber()));
        }
    }

    /**
     * Restituisce l'oggetto Car associato a questa foglia
     * 
     * @return L'istanza di Car associata
     */
    public Car getCar() {
        return car;
    }
}
