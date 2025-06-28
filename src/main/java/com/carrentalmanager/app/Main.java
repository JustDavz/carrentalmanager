// Package principale dell'applicazione che contiene la classe di avvio Main
package com.carrentalmanager.app;

// Importazione della GUI principale dell'applicazione
import com.carrentalmanager.gui.MainGUI;

// Importazione delle classi modello che rappresentano i dati principali (auto, clienti, noleggi)
import com.carrentalmanager.model.Car;
import com.carrentalmanager.model.Customer;
import com.carrentalmanager.model.Rental;

// Importazione dell'observer che registra gli eventi di apertura e chiusura dei noleggi
import com.carrentalmanager.observer.RentalLogger;

// Importazione della classe responsabile della gestione dei file CSV
import com.carrentalmanager.utils.FileManager;

// Importazione delle utility Swing per avviare la GUI in modo thread-safe
import javax.swing.*;

// Importazione delle classi per la gestione di collezioni dinamiche
import java.util.ArrayList;
import java.util.List;


public class Main {
    public static void main(String[] args) {
        // Recupera l'istanza singleton del FileManager, per garantire accesso centralizzato ai file
        FileManager fileManager = FileManager.getInstance();

        // Crea i file CSV necessari se non sono già presenti
        fileManager.initializeFiles();

        // Inizializza le liste che conterranno i dati caricati da file
        List<Customer> customers = new ArrayList<>();
        List<Car> cars = new ArrayList<>();
        List<Rental> rentals = new ArrayList<>();

        // Carica i dati dai file CSV e li inserisce nelle liste corrispondenti
        fileManager.loadCustomers(customers);
        fileManager.loadCars(cars);
        fileManager.loadRentals(rentals, cars);

        // Registra un observer per tracciare eventi di apertura e chiusura noleggi
        Rental.addObserver(new RentalLogger());

        // Avvia l'interfaccia grafica in modo thread-safe passando i dati caricati
        SwingUtilities.invokeLater(() -> new MainGUI(customers, cars, rentals));
    }
}
