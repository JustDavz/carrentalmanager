package com.carrentalmanager.gui;

import com.carrentalmanager.model.*;
import com.carrentalmanager.utils.FileManager;
import com.carrentalmanager.composite.*;
import com.carrentalmanager.iterator.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
* Classe MainGUI: rappresenta la GUI principale dell'applicazione "Car Rental Manager"
* Consente:
* - Inserimento di clienti, auto e noleggi
* - Navigazione tra i dati (Iterator Pattern)
* - Gestione dei noleggi
* - Visualizzazione delle auto per categoria (Composite Pattern)
* - Visualizzazione statistiche generali
* - Salvataggio automatico dei dati in file CSV alla chiusura tramite il FileMagaer 
*/
public class MainGUI extends JFrame {

    /**
     * Costruttore GUI principale
     *
     * @param customers lista dei clienti 
     * @param cars lista delle auto 
     * @param rentals lista dei noleggi
     */
    public MainGUI(List<Customer> customers, List<Car> cars, List<Rental> rentals) {
        // Impostazioni di base della finestra
        setTitle("Car Rental Manager");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(500, 450);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Pannelli principali
        JPanel mainPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        JPanel actionPanel = new JPanel(new GridLayout(2, 2, 10, 10));     // Sezione azioni per l'inserimento dati
        JPanel navigationPanel = new JPanel(new GridLayout(1, 3, 10, 10)); // Sezione navigazione
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // Sezione inferiore

        // Pulsanti azioni principali
        JButton addCustomerButton = new JButton("Inserisci Cliente");
        JButton addCarButton = new JButton("Inserisci Auto");
        JButton addRentalButton = new JButton("Inserisci Noleggio");
        JButton manageRentalsButton = new JButton("Gestione Noleggi");

        actionPanel.add(addCustomerButton);
        actionPanel.add(addCarButton);
        actionPanel.add(addRentalButton);
        actionPanel.add(manageRentalsButton);

        // Pulsanti di navigazione per navigare tra i dati
        JButton browseCustomersButton = new JButton("Sfoglia Clienti");
        JButton browseCarsButton = new JButton("Sfoglia Auto");
        JButton browseRentalsButton = new JButton("Sfoglia Noleggi");

        navigationPanel.add(browseCustomersButton);
        navigationPanel.add(browseCarsButton);
        navigationPanel.add(browseRentalsButton);

        // Pulsanti inferiori 
        JButton browseByCategoryButton = new JButton("Sfoglia per Categoria"); // Composite pattern
        JButton viewDataButton = new JButton("Visualizza Statistiche");
        JButton exitButton = new JButton("Esci");

        bottomPanel.add(browseByCategoryButton);
        bottomPanel.add(viewDataButton);
        bottomPanel.add(exitButton);

        // Aggiunta dei pannelli al pannello principale
        mainPanel.add(actionPanel);
        mainPanel.add(navigationPanel);
        mainPanel.add(bottomPanel);
        add(mainPanel, BorderLayout.CENTER);

        // Azioni dei pulsanti principali
        addCustomerButton.addActionListener(e -> new CustomerForm(this, customers));
        addCarButton.addActionListener(e -> new CarForm(this, cars));
        addRentalButton.addActionListener(e -> new RentalForm(this, customers, cars, rentals));
        manageRentalsButton.addActionListener(e -> new RentalManagerForm(this, rentals, cars));

        // Azioni di navigazione (Iterator Pattern)
        browseCustomersButton.addActionListener(e -> new CustomerBrowser(new CustomerCollection(customers)));
        browseCarsButton.addActionListener(e -> new CarBrowser(new CarCollection(cars)));
        browseRentalsButton.addActionListener(e -> new RentalBrowser(new RentalCollection(rentals)));

        // Visualizzazione auto per categoria (Composite Pattern)
        browseByCategoryButton.addActionListener(e -> openCarGroupBrowser(cars));

        // Visualizzazione statistiche
        viewDataButton.addActionListener(e -> {
            long availableCars = cars.stream().filter(Car::isAvailable).count();
            long unavailableCars = cars.size() - availableCars;
            long openRentals = rentals.stream().filter(r -> r.getStatus() == RentalStatus.APERTO).count();
            long closedRentals = rentals.stream().filter(r -> r.getStatus() == RentalStatus.CHIUSO).count();

            String message = getStatisticsMessage(
                    customers.size(), cars.size(), availableCars, unavailableCars, openRentals, closedRentals
            );
            JOptionPane.showMessageDialog(this, message, "Statistiche Sistema", JOptionPane.INFORMATION_MESSAGE);
        });

        // Chiusura applicazione
        exitButton.addActionListener(e -> System.exit(0));

        // Salvataggio automatico dei dati alla chiusura
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                FileManager.getInstance().saveAllDataAsync(customers, cars, rentals);
                dispose();
                System.exit(0);
            }
        });

        setVisible(true); // Mostra la GUI
    }

    /**
     * Metodo per aprire il browser delle auto organizzate per categoria
     * 
     * Costruisce la struttura Composite (CarGroup con CarLeaf)
     *
     * @param cars lista di tutte le auto
     */
    private void openCarGroupBrowser(List<Car> cars) {
        CarGroup rootGroup = new CarGroup("Tutte le Categorie");

        for (CarCategory category : CarCategory.values()) {
            CarGroup group = new CarGroup(category.getLabel());
            List<Car> filtered = cars.stream()
                    .filter(c -> c.getCarCategory() == category)
                    .toList();
            for (Car car : filtered) {
                group.addChild(new CarLeaf(car));
            }
            rootGroup.addChild(group);
        }

        new CarGroupBrowser(rootGroup);
    }

    /**
     * Metodo che genera il messaggio di statistiche generali
     *
     * @param totalCustomers numero clienti
     * @param totalCars numero totale di auto
     * @param available auto disponibili
     * @param unavailable auto non disponibili
     * @param open noleggi aperti
     * @param closed noleggi chiusi
     * @return stringa formattata con i dati
     */
    private String getStatisticsMessage(int totalCustomers, int totalCars, long available, long unavailable, long open, long closed) {
        return """
            Statistiche attuali:

            Clienti totali: %d
            Vetture totali: %d
            Vetture disponibili: %d
            Vetture occupate: %d
            Noleggi attivi: %d
            Noleggi chiusi: %d
            """.formatted(totalCustomers, totalCars, available, unavailable, open, closed);
    }
}
