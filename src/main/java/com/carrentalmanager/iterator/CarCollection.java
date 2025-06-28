package com.carrentalmanager.iterator;

import com.carrentalmanager.model.Car;
import java.util.Iterator;
import java.util.List;

/**
 * CarCollection incapsula una lista di oggetti Car e fornisce un iteratore per scorrerli
 * 
 * Applica il pattern Iterator per nascondere la struttura interna della collezione
 */
public class CarCollection {

    // Lista di auto da iterare
    private final List<Car> cars;

    /**
     * Costruttore che inizializza la collezione con una lista di auto
     *
     * @param cars Lista di oggetti Car da gestire all'interno della collezione
     */
    public CarCollection(List<Car> cars) {
        this.cars = cars;
    }

    /**
     * Crea un iteratore per scorrere le auto contenute nella collezione
     * 
     * Nasconde la struttura interna (List<Car>) e consente di navigare tra le auto senza esporre direttamente la lista
     *
     * @return Iterator<Car> per visualizzare ogni auto
     */
    public Iterator<Car> createIterator() {
        return cars.iterator();
    }
}
