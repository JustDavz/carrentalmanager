package com.carrentalmanager.observer;

import com.carrentalmanager.model.Rental;
import java.util.logging.Logger; 

/**
 * Implementazione dell'interfaccia RentalObserver che registra eventi di noleggio
 * 
 * Utilizza il pattern Observer per tenere traccia dell’apertura e chiusura dei noleggi
 * e logga informazioni utili tramite il sistema di logging standard Java
 */
public class RentalLogger implements RentalObserver {

    // Logger Java per scrivere messaggi nel terminale 
    private static final Logger logger = Logger.getLogger(RentalLogger.class.getName());

    // Numero totale di noleggi chiusi utile per statistiche o report
    private int closedRentalsCount = 0;

    // Numero totale di noleggi aperti utile per statistiche o report
    private int openedRentalsCount = 0;

    /**
     * Metodo invocato automaticamente quando un noleggio viene chiuso
     * 
     * Incrementa il contatore e registra l’evento nel log
     */
    @Override
    public void onRentalClosed(Rental rental) {
        closedRentalsCount++;  // Aumenta il numero di noleggi chiusi

        String message = "Il noleggio per " + rental.getCustomer().getFullName() + " è stato aggiornato a chiuso";
        String countMessage = "Totale noleggi chiusi: " + closedRentalsCount;

        logger.info(message);        // Log dell'evento specifico
        logger.info(countMessage);   // Log del totale aggiornato
    }

     /**
     * Metodo invocato automaticamente quando viene avviato un nuovo noleggio
     * 
     * Incrementa il contatore e registra l’evento nel log
     */
    @Override
    public void onRentalStarted(Rental rental) {
        openedRentalsCount++;  // Aumenta il numero di noleggi aperti

        String message = "Nuovo noleggio registrato per " + rental.getCustomer().getFullName() +
                         ", auto: " + rental.getCar().getBrand() + " " + rental.getCar().getModel();
        String countMessage = "Totale noleggi aperti creati: " + openedRentalsCount;

        logger.info(message);        // Log dell'evento specifico
        logger.info(countMessage);   // Log del totale aggiornato
    }

    /** Restituisce il numero totale di noleggi chiusi e può essere usato ad esempio per test */
    public int getClosedRentalsCount() {
        return closedRentalsCount;
    }

    /** Restituisce il numero totale di noleggi aperti e può essere usato ad esempio per test */
    public int getOpenedRentalsCount() {
        return openedRentalsCount;
    }
}
