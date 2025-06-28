package com.carrentalmanager.strategy;

import com.carrentalmanager.model.Rental;
import java.util.logging.Logger;

/**
 * Strategia di calcolo del prezzo per i noleggi con categoria auto "Luxury"
 * 
 * Implementa l'interfaccia PricingStrategy (Pattern Strategy)
 *
 * Rispetto ad altre strategie esempio "Basic", il costo giornaliero per i servizi extra l'assicurazione Kasko è più alto
 * 
 */
public class LuxuryRentalStrategy implements PricingStrategy {

    // Logger per registrare errori o messaggi durante il calcolo
    private static final Logger logger = Logger.getLogger(LuxuryRentalStrategy.class.getName());

    // Costi giornalieri aggiuntivi per i servizi extra
    private static final double KASKO_COST_PER_DAY = 200.0; // Kasko più costoso per veicoli di lusso
    private static final double ROADSIDE_COST_PER_DAY = 50.0; // Soccorso stradale uguale alle altre strategie

    /**
     * Calcola il prezzo totale di un noleggio con categoria auto "Luxury"
     * Viene considerato:
     * - prezzo base dell'auto moltiplicato per i giorni di noleggio
     * - eventuali costi extra come assicurazione Kasko e Soccorso stradale
     *
     * @param rental il noleggio per cui calcolare il prezzo
     * @return il Prezzo finale del noleggio
     * 
     */
    @Override
    public double calculatePrice(Rental rental) {
        try {
            // Se il noleggio o l'auto non sono validi, restituisci 0 e avvisa con un log
            if (rental == null || rental.getCar() == null) {
                logger.warning("Rental o auto non validi. Prezzo impostato a 0.");
                return 0.0;
            }

            // Prezzo base = prezzo giornaliero * numero giorni
            double base = rental.getCar().getDailyPrice() * rental.getRentalDays();
            double extra = 0;

            // Aggiungi costo assicurazione Kasko se selezionato
            if (rental.isKaskoInsurance()) {
                extra += KASKO_COST_PER_DAY * rental.getRentalDays();
            }

            // Aggiungi costo Soccorso stradale se selezionato
            if (rental.isRoadsideAssistance()) {
                extra += ROADSIDE_COST_PER_DAY * rental.getRentalDays();
            }

            // Prezzo totale = base + extra
            return base + extra;

        } catch (Exception e) {
            // In caso di errore, logga il messaggio e restituisci 0
            logger.severe("Errore nel calcolo del prezzo Luxury: " + e.getMessage());
            return 0.0;
        }
    }
}
