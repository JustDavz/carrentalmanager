package com.carrentalmanager.utils;

import com.carrentalmanager.model.*;
import com.carrentalmanager.builder.RentalBuilder;

import java.io.*;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Gestore Singleton per la gestione dei noleggi tramite file CSV
 * 
 * Utilizza il Builder Pattern per ricostruire i noleggi in memoria
 * 
 * Ogni riga è composta da 12 campi: info cliente, info auto, date, servizi extra e stato
 */
@SuppressWarnings("all")
public class RentalFileManager {

    // Logger per eventi e tracciamento errori I/O
    private static final Logger LOGGER = Logger.getLogger(RentalFileManager.class.getName());

    // Percorso predefinito del file dei noleggi
    private String rentalFilePath = "data/rentals.csv";

    // Costruttore privato per garantire Singleton
    private RentalFileManager() {}

    /**
     * Inner static class per istanziazione lazy e thread-safe
     */
    private static class Holder {
        private static final RentalFileManager INSTANCE = new RentalFileManager();
    }

    /**
     * Restituisce l’unica istanza del gestore noleggi
     */
    public static RentalFileManager getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * Permette di impostare un percorso alternativo per il file CSV
     * 
     * Utile per ambienti di test o percorsi personalizzati
     */
    public void setFilePath(String path) {
        this.rentalFilePath = path;
    }

    /**
     * Salva un singolo noleggio in append nel file CSV
     * 
     * Se il noleggio è attivo, imposta l’auto come non disponibile
     */
    public void saveRental(Rental rental) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(rentalFilePath, true))) {
            writer.write(String.join(",",
                    rental.getCustomer().getID(),
                    rental.getCustomer().getFirstName(),
                    rental.getCustomer().getLastName(),
                    rental.getCar().getPlateNumber(),
                    rental.getCar().getBrand(),
                    rental.getCar().getModel(),
                    rental.getStartDate().toString(),
                    rental.getEndDate().toString(),
                    String.valueOf(rental.getPrice()),
                    String.valueOf(rental.isKaskoInsurance()),
                    String.valueOf(rental.isRoadsideAssistance()),
                    rental.getStatus().name()));
            writer.newLine();

            // Marca l'auto come occupata se il noleggio è ancora aperto
            if (rental.getStatus() == RentalStatus.APERTO) {
                rental.getCar().setAvailable(false);
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Errore durante il salvataggio del noleggio", e);
        }
    }

    /**
     * Carica tutti i noleggi presenti nel file CSV
     * 
     * Pulisce la lista esistente e aggiorna la disponibilità delle auto in base allo stato
     * 
     * Utilizza il Builder Pattern per costruire ogni noleggio
     */
    public void loadRentals(List<Rental> rentalList, List<Car> carList) {
        rentalList.clear();

        // Imposta tutte le auto come disponibili all’avvio
        for (Car car : carList) {
            car.setAvailable(true);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(rentalFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",", -1); // CSV robusto anche con campi vuoti

                if (data.length == 12) {
                    // Costruisce un cliente minimale per il noleggio
                    Customer customer = new Customer(data[0], data[1], data[2], "", "", "");

                    // Ricerca dell’auto tramite targa
                    Car car = findCarByPlate(carList, data[3]);
                    if (car == null) continue; // Salta se l’auto non esiste

                    // Ricostruzione del noleggio tramite Builder
                    Rental rental = new RentalBuilder(customer, car,
                            LocalDate.parse(data[6]),
                            LocalDate.parse(data[7]))
                            .kasko(Boolean.parseBoolean(data[9]))
                            .roadside(Boolean.parseBoolean(data[10]))
                            .build();

                    rental.setPrice(Double.parseDouble(data[8]));
                    rental.setStatus(RentalStatus.valueOf(data[11]));

                    rentalList.add(rental);

                    // Se il noleggio è attivo, marca l’auto come occupata
                    if (rental.getStatus() == RentalStatus.APERTO) {
                        car.setAvailable(false);
                    }
                } else {
                    LOGGER.warning("Riga CSV ignorata per formato errato: " + line);
                }
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Errore durante il caricamento dei noleggi", e);
        }
    }

    /**
     * Sovrascrive l’intero file CSV con una lista aggiornata di noleggi
     * 
     * Ogni elemento viene convertito in formato CSV e scritto riga per riga
     */
    public void saveAllRentals(List<Rental> rentals) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(rentalFilePath))) {
            for (Rental rental : rentals) {
                writer.write(String.join(",",
                        rental.getCustomer().getID(),
                        rental.getCustomer().getFirstName(),
                        rental.getCustomer().getLastName(),
                        rental.getCar().getPlateNumber(),
                        rental.getCar().getBrand(),
                        rental.getCar().getModel(),
                        rental.getStartDate().toString(),
                        rental.getEndDate().toString(),
                        String.valueOf(rental.getPrice()),
                        String.valueOf(rental.isKaskoInsurance()),
                        String.valueOf(rental.isRoadsideAssistance()),
                        rental.getStatus().name()));
                writer.newLine();
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Errore durante il salvataggio dell’elenco noleggi", e);
        }
    }

    /**
     * Cerca un'auto nella lista fornita utilizzando la targa
     * 
     * Se non viene trovata, restituisce null
     */
    private Car findCarByPlate(List<Car> carList, String plate) {
        for (Car car : carList) {
            if (car.getPlateNumber().equals(plate)) {
                return car;
            }
        }
        return null;
    }
}
