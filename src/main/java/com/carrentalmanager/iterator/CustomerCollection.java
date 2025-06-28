package com.carrentalmanager.iterator;

import com.carrentalmanager.model.Customer;
import java.util.Iterator;
import java.util.List;

/**
 * CustomerCollection incapsula una lista di clienti e fornisce un iteratore per scorrerli
 * 
 * Applica il pattern Iterator per nascondere la struttura interna della collezione
 */
public class CustomerCollection {

    // Lista interna dei clienti
    private final List<Customer> customers;

    /**
     * Costruttore che inizializza la collezione con una lista di clienti
     *
     * @param customer Lista di oggetti Customer da gestire
     */
    public CustomerCollection(List<Customer> customers) {
        this.customers = customers;
    }

    /**
     * Crea un iteratore per scorrere tra i clienti contenuti nella collezione
     * 
     * Nasconde la struttura interna (List<Customer>) e consente di navigare tra i clienti
     * 
     * senza esporre direttamente la lista
     *
     * @return Iterator<Customer> per visualizzare ogni cliente
     */
    public Iterator<Customer> createIterator() {
        return customers.iterator();
    }
}
