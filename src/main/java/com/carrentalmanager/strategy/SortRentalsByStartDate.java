package com.carrentalmanager.strategy;

import com.carrentalmanager.model.Rental;

import java.util.List;

/**
 * Strategia di ordinamento per i noleggi in base alla data di inizio
 *
 * Ordina i noleggi dal meno recente al più recente, usando la startDate
 * 
 * Implementa il pattern Strategy e utilizza Stream API
 * 
 */
public class SortRentalsByStartDate implements SortStrategy<Rental> {

    /**
     * Ordina la lista dei noleggi in ordine crescente di data di inizio
     * 
     * Implementa il pattern Strategy e utilizza Stream API
     *
     * @list lista dei noleggi da ordinare
     */
    @Override
    public void sort(List<Rental> list) {
        List<Rental> sorted = list.stream()
                .sorted((rental1, rental2) -> rental1.getStartDate().compareTo(rental2.getStartDate()))
                .toList(); 

        list.clear();
        list.addAll(sorted);
    }
}
