package com.carrentalmanager.strategy;

import com.carrentalmanager.model.Rental;

import java.util.List;
import java.util.Comparator;

/**
 * Strategia di ordinamento per i noleggi in base alla data di fine (decrescente)
 *
 * Mostra prima i noleggi che terminano più tardi
 * 
 * Fa parte del pattern Strategy per l’ordinamento dinamico
 * 
 */
public class SortRentalsByEndDateDesc implements SortStrategy<Rental> {

    /**
     * Ordina la lista dei noleggi dalla data di fine più lontana a quella più vicina
     * 
     * Utilizza Stream API e lambda expression
     *
     * @param list lista dei noleggi da ordinare
     */
    @Override
    public void sort(List<Rental> list) {
        List<Rental> sorted = list.stream()
                .sorted(Comparator.comparing(Rental::getEndDate).reversed())
                .toList();

        list.clear();
        list.addAll(sorted);
    }
}
