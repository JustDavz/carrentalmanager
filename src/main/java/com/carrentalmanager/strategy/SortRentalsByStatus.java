package com.carrentalmanager.strategy;

import com.carrentalmanager.model.Rental;
import com.carrentalmanager.model.RentalStatus;

import java.util.List;

/**
 * Strategia di ordinamento per i noleggi in base allo stato
 *
 * I noleggi con stato APERTO vengono visualizzati prima di quelli CHIUSI
 * 
 * Utilizza Stream API per maggiore leggibilità.
 */
public class SortRentalsByStatus implements SortStrategy<Rental> {

    /**
     * Ordina la lista dei noleggi mettendo prima quelli APERTI (APERTO), seguiti da quelli CHIUSI (CHIUSO), usando Stream API
     *
     * @param list lista dei noleggi da ordinare
     */
    @Override
    public void sort(List<Rental> list) {
        List<Rental> sorted = list.stream()
                .sorted((rental1, rental2) -> {
                    boolean rental1Closed = rental1.getStatus() == RentalStatus.CHIUSO;
                    boolean rental2Closed = rental2.getStatus() == RentalStatus.CHIUSO;
                    return Boolean.compare(rental1Closed, rental2Closed);
                })
                .toList();

        list.clear();
        list.addAll(sorted);
    }
}
