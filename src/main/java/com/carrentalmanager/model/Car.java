package com.carrentalmanager.model;

/**
 * Classe che rappresenta un'automobile disponibile nel sistema di noleggio
 * 
 * Contiene tutte le informazioni utili per la gestione di un'automobile,
 * come targa, marca, modello, categoria, tipo di carburante, prezzo e disponibilità
 */
public class Car {

    private String plateNumber;
    private String brand;
    private String model;
    private CarCategory carCategory;
    private CarFuel fuelType;
    private double dailyPrice;
    private boolean isAvailable;

    /**
     * Costruttore che inizializza tutti i campi dell'automobile
     */
    public Car(String plateNumber, String brand, String model, CarCategory carCategory, CarFuel fuelType, double dailyPrice, boolean isAvailable) {
        this.plateNumber = plateNumber;
        this.brand = brand;
        this.model = model;
        this.carCategory = carCategory;
        this.fuelType = fuelType;
        this.dailyPrice = dailyPrice;
        this.isAvailable = isAvailable;
    }

    /** Metodi Getter e Setter */

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }
  
    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public CarCategory getCarCategory() {
        return carCategory;
    }

    public void setCarCategory(CarCategory carCategory) {
        this.carCategory = carCategory;
    }

    public CarFuel getFuelType() {
        return fuelType;
    }

    public void setFuelType(CarFuel fuelType) {
        this.fuelType = fuelType;
    }

    public double getDailyPrice() {
        return dailyPrice;
    }

    public void setDailyPrice(double dailyPrice) {
        this.dailyPrice = dailyPrice;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    /** Restituisce una rappresentazione leggibile dell'oggetto Car */
    @Override
    public String toString() {
        return "Targa: " + plateNumber + ", " +
               "Marca: " + brand + ", " +
               "Modello: " + model + ", " +
               "Categoria: " + carCategory.getLabel() + ", " +
               "Carburante: " + fuelType + ", " +
               "Prezzo giornaliero: " + dailyPrice + " €";
    }
}