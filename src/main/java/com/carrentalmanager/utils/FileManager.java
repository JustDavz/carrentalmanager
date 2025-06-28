package com.carrentalmanager.utils;

import com.carrentalmanager.model.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Gestore Singleton centralizzato per inizializzare file CSV e salvare/caricare dati
 * 
 * Incapsula l'accesso a CustomerFileManager, CarFileManager e RentalFileManager
 * 
 * Mantiene il supporto per il salvataggio asincrono di tutti i dati
 */
@SuppressWarnings("all")
public class FileManager {

    private static final Logger LOGGER = Logger.getLogger(FileManager.class.getName());

    // Percorsi dei file CSV usati
    private static final String[] REQUIRED_FILES = {
        "data/customers.csv",
        "data/cars.csv",
        "data/rentals.csv"
    };

    // Singleton con inizializzazione thread-safe
    private FileManager() {}

    private static class Holder {
        private static final FileManager INSTANCE = new FileManager();
    }

    public static FileManager getInstance() {
        return Holder.INSTANCE;
    }

    /** Crea i file richiesti se non esistono già compresi i percorsi */
    public void initializeFiles() {
        for (String path : REQUIRED_FILES) {
            File file = new File(path);
            try {
                File dir = file.getParentFile();
                if (dir != null && !dir.exists()) dir.mkdirs();

                if (file.createNewFile()) {
                    LOGGER.info(() -> "File creato: " + path);
                } else {
                    LOGGER.fine(() -> "File già esistente: " + path);
                }
            } catch (IOException e) {
                LOGGER.warning(() -> "Errore nella creazione del file: " + path + " - " + e.getMessage());
            }
        }
    }

    // Sezione Salvataggio Asincrono

    /** Salva clienti, auto e noleggi in modo asincrono attraverso thread separato */
    public void saveAllDataAsync(List<Customer> customers, List<Car> cars, List<Rental> rentals) {
        Runnable task = () -> {
            try {
                CustomerFileManager.getInstance().saveCustomers(customers);
                CarFileManager.getInstance().saveCars(cars);
                RentalFileManager.getInstance().saveAllRentals(rentals);
                LOGGER.info(() -> "Salvataggio completato in background.");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Errore durante il salvataggio asincrono", e);
            }
        };
        new Thread(task, "SaveDataThread").start();
    }

    // Gestione Clienti

    public void saveCustomer(Customer customer) {
        CustomerFileManager.getInstance().saveCustomer(customer);
    }

    public void saveCustomers(List<Customer> customers) {
        CustomerFileManager.getInstance().saveCustomers(customers);
    }

    public void loadCustomers(List<Customer> customers) {
        CustomerFileManager.getInstance().loadCustomers(customers);
    }

    public void setCustomerFile(String path) {
        CustomerFileManager.getInstance().setFilePath(path);
    }

    // Gestione Auto

    public void saveCar(Car car) {
        CarFileManager.getInstance().saveCar(car);
    }

    public void saveCars(List<Car> cars) {
        CarFileManager.getInstance().saveCars(cars);
    }

    public void loadCars(List<Car> cars) {
        CarFileManager.getInstance().loadCars(cars);
    }

    public void updateCar(Car car) {
        CarFileManager.getInstance().updateCar(car);
    }

    public void setCarFile(String path) {
        CarFileManager.getInstance().setFilePath(path);
    }

    // Gestione Noleggi

    public void saveRental(Rental rental) {
        RentalFileManager.getInstance().saveRental(rental);
    }

    public void saveAllRentals(List<Rental> rentals) {
        RentalFileManager.getInstance().saveAllRentals(rentals);
    }

    public void loadRentals(List<Rental> rentals, List<Car> cars) {
        RentalFileManager.getInstance().loadRentals(rentals, cars);
    }

    public void setRentalFile(String path) {
        RentalFileManager.getInstance().setFilePath(path);
    }
}
