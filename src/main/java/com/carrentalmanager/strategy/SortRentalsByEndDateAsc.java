package com.carrentalmanager.strategy;

import com.carrentalmanager.model.Rental;

import java.util.List;
import java.util.Comparator;

/**
 * Strategia di ordinamento per i noleggi in base alla data di fine (in ordine crescente)
 *
 * Vengono mostrati prima i noleggi che terminano prima
 * 
 * Parte del pattern Strategy per l’ordinamento dinamico
 * 
 */
public class SortRentalsByEndDateAsc implements SortStrategy<Rental> {

    /**
     * Ordina la lista dei noleggi dalla data di fine più vicina a quella più lontana
     * 
     * Utilizza Stream API e lambda expression
     *
     * @param list lista dei noleggi da ordinare
     */
    @Override
    public void sort(List<Rental> list) {
        List<Rental> sorted = list.stream()
                .sorted(Comparator.comparing(Rental::getEndDate))
                .toList(); 

        list.clear();
        list.addAll(sorted);
    }
}
