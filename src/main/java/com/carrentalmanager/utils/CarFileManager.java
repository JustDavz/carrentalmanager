package com.carrentalmanager.utils;

import com.carrentalmanager.model.Car;
import com.carrentalmanager.model.CarFuel;
import com.carrentalmanager.model.CarCategory;

import java.io.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Gestore Singleton per la gestione di veicoli tramite file CSV
 * 
 * Garantisce un'unica istanza e centralizza tutte le operazioni di lettura e scrittura su file.
 */
@SuppressWarnings("all")
public class CarFileManager {

    // Logger per registrare operazioni e gestire eventuali errori IO
    private static final Logger LOGGER = Logger.getLogger(CarFileManager.class.getName());

    // Riferimento all'istanza Singleton
    private static CarFileManager instance;

    // Percorso del file CSV contenente i dati delle auto
    private String carFilePath = "data/cars.csv";

    // Costruttore privato per evitare istanziazioni multiple
    private CarFileManager() {}

    /**
     * Restituisce l'unica istanza di CarFileManager
     * 
     * Se non esiste ancora, viene creata al primo utilizzo
     */
    public static CarFileManager getInstance() {
        if (instance == null) {
            instance = new CarFileManager();
        }
        return instance;
    }

    /**
     * Consente di cambiare il percorso del file CSV utilizzato (utile per testing o ambienti diversi)
     */
    public void setFilePath(String path) {
        this.carFilePath = path;
    }

    /** Apre il file in modalità append e aggiunge una nuova riga con i dati dell'auto, separati da virgola */
    public void saveCar(Car car) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(carFilePath, true))) {
            String record = String.join(",",
                    car.getPlateNumber(),
                    car.getBrand(),
                    car.getModel(),
                    car.getCarCategory().name(),
                    car.getFuelType().name(),
                    Double.toString(car.getDailyPrice()),
                    Boolean.toString(car.isAvailable()));
            writer.write(record);
            writer.newLine();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Errore durante il salvataggio di una singola auto", e);
        }
    }

    /**
     * Sovrascrive completamente il file con la lista fornita
     *
     * Ogni auto diventa una riga nuova nel CSV
     */
    public void saveCars(List<Car> cars) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(carFilePath))) {
            for (Car car : cars) {
                String record = String.join(",",
                        car.getPlateNumber(),
                        car.getBrand(),
                        car.getModel(),
                        car.getCarCategory().name(),
                        car.getFuelType().name(),
                        Double.toString(car.getDailyPrice()),
                        Boolean.toString(car.isAvailable()));
                writer.write(record);
                writer.newLine();
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Errore durante il salvataggio della lista completa di auto", e);
        }
    }

    /**
     * Carica tutte le auto presenti nel file CSV
     * 
     * Pulisce la lista prima di inserirle per evitare duplicazioni
     * 
     * Ogni riga del file viene analizzata: se non contiene esattamente 7 campi, viene ignorata e segnalata tramite log
     */
    public void loadCars(List<Car> carList) {
        carList.clear();

        try (BufferedReader reader = new BufferedReader(new FileReader(carFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",", -1);
                if (data.length != 7) {
                    LOGGER.warning("Riga CSV ignorata per formato errato: " + line);
                    continue;
                }

                try {
                    Car car = new Car(
                        data[0], // Targa
                        data[1], // Marca
                        data[2], // Modello
                        CarCategory.valueOf(data[3]), // Categoria (enum)
                        CarFuel.valueOf(data[4]), // Carburante (enum)
                        Double.parseDouble(data[5]), // Prezzo giornaliero
                        Boolean.parseBoolean(data[6]) // Disponibilità boolean
                    );
                    carList.add(car);
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Errore durante la conversione di una riga CSV: " + line, e);
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Errore durante il caricamento delle auto da file", e);
        }
    }

    /**
     * Aggiorna un'auto esistente identificata dalla targa nel file CSV
     * 
     * Effettua il caricamento completo, sostituzione e riscrittura del file
     */
    public void updateCar(Car updatedCar) {
        try {
            List<Car> cars = new java.util.ArrayList<>();
            loadCars(cars);  // Carica tutte le auto

            for (int i = 0; i < cars.size(); i++) {
                if (cars.get(i).getPlateNumber().equals(updatedCar.getPlateNumber())) {
                    cars.set(i, updatedCar);
                    break;
                }
            }

            saveCars(cars);  // Riscrive su file il nuovo elenco
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'aggiornamento dell'auto con targa: "
                    + updatedCar.getPlateNumber(), e);
        }
    }
}
