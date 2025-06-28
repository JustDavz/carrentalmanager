package com.carrentalmanager.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.carrentalmanager.observer.RentalObserver;
import com.carrentalmanager.strategy.PricingStrategy;

/**
 * Classe che rappresenta un noleggio auto
 * 
 * Contiene dati come cliente, auto, date, servizi extra, stato e strategia di prezzo
 */
public class Rental {

    // Lista di observer per notificare eventi (pattern Observer)
    private static final List<RentalObserver> observers = new ArrayList<>();

    // Dati principali del noleggio
    private Customer customer;
    private Car car;
    private LocalDate startDate;
    private LocalDate endDate;
    private double price;

    // Strategia di prezzo (pattern Strategy)
    private PricingStrategy costStrategy;

    // Servizi extra
    private boolean kaskoInsurance;
    private boolean roadsideAssistance;

    // Stato del noleggio (APERTO o CHIUSO)
    private RentalStatus status;

    // Costruttore usato dal Builder: inizializza i dati principali
    public Rental(Customer customer, Car car, LocalDate startDate, LocalDate endDate, boolean kaskoInsurance, boolean roadsideAssistance) {
        this.customer = customer;
        this.car = car;
        this.startDate = startDate;
        this.endDate = endDate;
        this.kaskoInsurance = kaskoInsurance;
        this.roadsideAssistance = roadsideAssistance;
        this.price = 0;
        this.status = RentalStatus.APERTO;
    }

    /**  Metodi Getter e Setter */

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public PricingStrategy getCostStrategy() {
        return costStrategy;
    }

    public void setCostStrategy(PricingStrategy costStrategy) {
        this.costStrategy = costStrategy;
    }

    public boolean isKaskoInsurance() {
        return kaskoInsurance;
    }

    public void setKaskoInsurance(boolean kaskoInsurance) {
        this.kaskoInsurance = kaskoInsurance;
    }

    public boolean isRoadsideAssistance() {
        return roadsideAssistance;
    }

    public void setRoadsideAssistance(boolean roadsideAssistance) {
        this.roadsideAssistance = roadsideAssistance;
    }

    public RentalStatus getStatus() {
        return status;
    }

    /** Imposta lo stato del noleggio e aggiorna la disponibilità del veicolo */
    public void setStatus(RentalStatus status) {
        this.status = status;
        updateCarAvailability();

        if (status == RentalStatus.CHIUSO) {
            notifyRentalClosed();
        }
    }

    /** Calcola la durata del noleggio in giorni */
    public long getRentalDays() {
        return endDate.toEpochDay() - startDate.toEpochDay() + 1;
    }

    /** Calcola il prezzo totale applicando la strategia scelta */
    public double calculateTotalPrice() {
        if (costStrategy != null) {
            return costStrategy.calculatePrice(this);
        }
        return 0;
    }

    /** Aggiorna la disponibilità dell’auto in base allo stato del noleggio */
    private void updateCarAvailability() {
        boolean shouldBeAvailable = (this.status != RentalStatus.APERTO);
        car.setAvailable(shouldBeAvailable);
    }

    // Observer Pattern

    /** Notifica l'avvio del noleggio agli observer */
    public void notifyRentalStarted() {
        for (RentalObserver observer : observers) {
            observer.onRentalStarted(this);
        }
    }

    /** Notifica la chiusura del noleggio agli observer */
    private void notifyRentalClosed() {
        for (RentalObserver observer : observers) {
            observer.onRentalClosed(this);
        }
    }

    /** Aggiunge un observer per monitorare gli eventi del noleggio */
    public static void addObserver(RentalObserver observer) {
        observers.add(observer);
    }

    /** Restituisce una rappresentazione leggibile del cliente */
    @Override
    public String toString() {
        return "Rental: " + customer.getFullName() +
               ", Brand: " + car.getBrand() +
               ", Model: " + car.getModel() +
               " from " + startDate + " to " + endDate +
               ", Base price: " + price +
               ", Total: " + calculateTotalPrice();
    }
}
