package com.carrentalmanager.builder;

import java.time.LocalDate;

import com.carrentalmanager.model.Car;
import com.carrentalmanager.model.Customer;
import com.carrentalmanager.model.Rental;
import com.carrentalmanager.model.RentalStatus;
import com.carrentalmanager.strategy.PricingStrategy;

/**
 * Utilizzato per costruire oggetti Rental complessi in modo chiaro e flessibile, gestendo con semplicità numerosi campi opzionali come la strategia 
 * di prezzo, i servizi extra e lo stato del noleggio
 *
 * Questo pattern evita l’uso di costruttori lunghi e difficili da leggere, migliorando la manutenibilità del codice e riducendo il rischio di 
 * errori durante l’inizializzazione
 * 
 */
public class RentalBuilder {

    // Cliente che effettua il noleggio
    private final Customer customer;

    // Auto selezionata per il noleggio
    private final Car car;

    // Data di inizio del noleggio
    private final LocalDate startDate;

    // Data di fine del noleggio
    private final LocalDate endDate;

    // Indica se il noleggio include l'assicurazione Kasko (default: false)
    private boolean kasko = false;

    // Indica se il noleggio include l'assistenza stradale (default: false)
    private boolean roadside = false;

    // Strategia di calcolo del prezzo (pattern Strategy)
    private PricingStrategy costStrategy;

    // Prezzo totale del noleggio calcolato o impostato manualmente
    private double price = 0.0;

    // Stato attuale del noleggio (APERTO di default)
    private RentalStatus status = RentalStatus.APERTO;

    /** Inizializza il builder con i campi obbligatori del noleggio */
    public RentalBuilder(Customer customer, Car car, LocalDate startDate, LocalDate endDate) {
        this.customer = customer;
        this.car = car;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /** Imposta l’assicurazione Kasko */
    public RentalBuilder kasko(boolean kasko) {
        this.kasko = kasko;
        return this;
    }

    /** Imposta l’assistenza stradale */
    public RentalBuilder roadside(boolean roadside) {
        this.roadside = roadside;
        return this;
    }

    /** Imposta la strategia di prezzo (pattern Strategy) */
    public RentalBuilder costStrategy(PricingStrategy strategy) {
        this.costStrategy = strategy;
        return this;
    }

    /** Imposta il prezzo base del noleggio */
    public RentalBuilder price(double price) {
        this.price = price;
        return this;
    }

    /** Imposta lo stato del noleggio (APERTO o CHIUSO) */
    public RentalBuilder status(RentalStatus status) {
        this.status = status;
        return this;
    }

    /** Crea un'istanza di Rental con i dati forniti */
    public Rental build() {
        Rental rental = new Rental(customer, car, startDate, endDate, kasko, roadside);
        rental.setCostStrategy(costStrategy);
        rental.setPrice(price);
        rental.setStatus(status);
        return rental;
    }
}
