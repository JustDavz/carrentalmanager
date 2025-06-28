package com.carrentalmanager.strategy;

import com.carrentalmanager.model.Rental;
import java.util.logging.Logger;

/**
 * Strategia di prezzo per i noleggi con categoria auto "Premium"
 * 
 * Applica un prezzo giornaliero e costi extra specifici per assicurazione Kasko e Soccorso Stradale
 * 
 * Implementa l'interfaccia PricingStrategy (Pattern Strategy)
 */
public class PremiumRentalStrategy implements PricingStrategy {

    private static final Logger logger = Logger.getLogger(PremiumRentalStrategy.class.getName());

    // Costi extra per i noleggi con categoria auto Premium
    private static final double KASKO_COST_PER_DAY = 200.0;
    private static final double ROADSIDE_COST_PER_DAY = 50.0;

    /**
     * Calcola il prezzo totale del noleggio con categoria auto Premium
     * 
     * @param rental il noleggio per cui calcolare il prezzo
     * @return prezzo complessivo, oppure 0 in caso di errore
     */
    @Override
    public double calculatePrice(Rental rental) {
        try {
            if (rental == null || rental.getCar() == null) {
                logger.warning("Noleggio o auto non validi. Prezzo impostato a 0.");
                return 0.0;
            }

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
            logger.severe("Errore nel calcolo del prezzo Premium: " + e.getMessage());
            return 0.0;
        }
    }
}
