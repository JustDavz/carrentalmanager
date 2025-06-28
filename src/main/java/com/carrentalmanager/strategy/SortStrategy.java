package com.carrentalmanager.strategy;

import java.util.List;

/**
 * Interfaccia generica per strategie di ordinamento
 * 
 * Fa parte del pattern Strategy e consente di definire criteri di ordinamento intercambiabili per oggetti Rental
 *
 * @param <T> Il tipo di oggetto su cui applicare l'ordinamento
 */
public interface SortStrategy<T> {

    /**
     * Ordina una lista di oggetti secondo una strategia definita
     *
     * @param list lista di oggetti da ordinare
     */
    void sort(List<T> list);
}
