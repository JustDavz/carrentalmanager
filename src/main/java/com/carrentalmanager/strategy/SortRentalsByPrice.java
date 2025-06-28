package com.carrentalmanager.strategy;

import com.carrentalmanager.model.Rental;

import java.util.Comparator;
import java.util.List;

/**
 * Strategia di ordinamento per i noleggi in base al prezzo
 *
 * Ordina i noleggi dal più economico al più costoso
 * 
 * Implementa il pattern Strategy e utilizza Stream API
 * 
 */
public class SortRentalsByPrice implements SortStrategy<Rental> {

    /**
     * Ordina la lista dei noleggi in base al prezzo (ordine crescente)
     * 
     * Utilizza Stream API e lambda expression per maggiore leggibilità
     *
     * @param rentals lista dei noleggi da ordinare
     */
    @Override
    public void sort(List<Rental> rentals) {
        List<Rental> sorted = rentals.stream()
                .sorted(Comparator.comparingDouble(Rental::getPrice))
                .toList();

        rentals.clear();
        rentals.addAll(sorted);
    }
}
