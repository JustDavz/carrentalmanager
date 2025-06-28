package com.carrentalmanager.strategy;

import com.carrentalmanager.model.Rental;
import java.util.logging.Logger;

/**
 * Strategia di calcolo del prezzo per i noleggi con categoria auto "Basic"
 * 
 * Calcola il prezzo in base al numero di giorni, al prezzo giornaliero del veicolo
 * e ai costi aggiuntivi per servizi extra assicurazione Kasko e soccorso stradale
 * 
 * Implementa l'interfaccia PricingStrategy (Pattern Strategy)
 */
public class BasicRentalStrategy implements PricingStrategy {

    // Costi giornalieri dei servizi extra
    private static final double KASKO_COST_PER_DAY = 100.0;
    private static final double ROADSIDE_COST_PER_DAY = 50.0;

    // Logger per la registrazione di eventuali problemi
    private static final Logger logger = Logger.getLogger(BasicRentalStrategy.class.getName());

    /**
     * Calcola il prezzo totale del noleggio
     *
     * @param rental il noleggio per cui calcolare il prezzo
     * @return il prezzo totale, oppure 0 in caso di errore
     */
    @Override
    public double calculatePrice(Rental rental) {
        try {
            long days = rental.getRentalDays();
            double basePrice = rental.getCar().getDailyPrice() * days;
            double extraCost = 0;

            if (rental.isKaskoInsurance()) {
                extraCost += KASKO_COST_PER_DAY * days;
            }

            if (rental.isRoadsideAssistance()) {
                extraCost += ROADSIDE_COST_PER_DAY * days;
            }

            return basePrice + extraCost;
        } catch (Exception e) {
            logger.severe("Errore durante il calcolo del prezzo: " + e.getMessage());
            return 0.0;
        }
    }
}
