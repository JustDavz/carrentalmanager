package com.carrentalmanager.strategy;

import com.carrentalmanager.model.Rental;

import java.util.Comparator;
import java.util.List;

/**
 * Strategia di ordinamento per i noleggi
 *
 * Ordina la lista di noleggi in base al nome completo del cliente (formato da nome e cognome)
 *
 * Fa parte del pattern Strategy, permettendo di modificare dinamicamente il criterio di ordinamento
 * 
 */
public class SortRentalsByCustomerName implements SortStrategy<Rental> {

    /**
     * Ordina i noleggi in base al nome completo del cliente, usando Stream API e lambda expression
     *
     * @param rentals lista dei noleggi da ordinare
     */
    @Override
    public void sort(List<Rental> rentals) {
        List<Rental> sorted = rentals.stream()
                .sorted(Comparator.comparing(r -> r.getCustomer().getFullName()))
                .toList();

        rentals.clear();
        rentals.addAll(sorted);
    }
}
