package com.carrentalmanager.iterator;

import com.carrentalmanager.model.Rental;
import java.util.Iterator;
import java.util.List;

/**
 * RentalCollection incapsula una lista di noleggi e fornisce un iteratore per scorrerli
 * 
 * Applica il pattern Iterator per nascondere la struttura interna della collezione
 */
public class RentalCollection {

    // Lista interna dei noleggi
    private final List<Rental> rentals;

    /**
     * Costruttore che inizializza la collezione con una lista di noleggi
     *
     * @param rentals Lista di oggetti Rental da gestire
     */
    public RentalCollection(List<Rental> rentals) {
        this.rentals = rentals;
    }

    /**
     * Crea un iteratore per scorrere i noleggi contenuti nella collezione
     * 
     * Nasconde la struttura interna (List<Rental>) e consente di navigare tra i noleggi senza esporre direttamente la lista
     *
     * @return Iterator<Rental> per visualizzare ogni noleggio
     */
    public Iterator<Rental> createIterator() {
        return rentals.iterator();
    }
}
