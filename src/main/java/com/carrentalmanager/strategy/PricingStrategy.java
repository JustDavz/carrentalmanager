package com.carrentalmanager.strategy;

import com.carrentalmanager.model.Rental;

/**
 * Interfaccia per la strategia di calcolo del prezzo del noleggio
 * 
 * Fa parte del pattern Strategy e consente di applicare logiche di pricing differenti
 * (es. Basic, Premium, Luxury) a seconda della categoria del veicolo o dei servizi extra
 */
public interface PricingStrategy {

    /**
     * Calcola il prezzo totale del noleggio in base ai parametri forniti
     * 
     * @return il prezzo complessivo comprensivo di eventuali extra
     */
    double calculatePrice(Rental rental);
}
