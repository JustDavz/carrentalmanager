package com.carrentalmanager.gui;

import com.carrentalmanager.model.Car;
import com.carrentalmanager.iterator.CarCollection;

import javax.swing.*;
import java.awt.*;
import java.util.Iterator;

/**
 * CarBrowser è una finestra GUI che consente all’utente di navigare tra le auto disponibili utilizzando il pattern Iterator tramite CarCollection
 * 
 * Mostra le informazioni di ogni auto in modo sequenziale
 */
public class CarBrowser extends JFrame {
    
    // Iteratore per scorrere la lista di auto
    private final transient Iterator<Car> iterator;

    // Area di testo per mostrare i dettagli dell'auto corrente
    private final JTextArea displayArea;

    // Auto attualmente visualizzata
    private transient Car currentCar;

    /**
     * Costruttore che inizializza la finestra e carica la prima auto disponibile
     *
     * @param carCollection raccolta di auto da esplorare
     */
    public CarBrowser(CarCollection carCollection) {
        this.iterator = carCollection.createIterator();

        setTitle("Navigatore Auto");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        displayArea = new JTextArea();
        displayArea.setEditable(false);
        add(new JScrollPane(displayArea), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton nextButton = new JButton("Avanti >>");
        buttonPanel.add(nextButton);
        add(buttonPanel, BorderLayout.SOUTH);

        nextButton.addActionListener(e -> showNext());

        // Mostra la prima auto se è presente
        if (iterator.hasNext()) {
            currentCar = iterator.next();
            displayCar(currentCar);
        } else {
            displayArea.setText("Nessuna auto disponibile.");
        }

        setVisible(true);
    }

    /**
     * Visualizza le informazioni dettagliate di un’auto nel displayCar()
     *
     * @param car auto da visualizzare
     */
    private void displayCar(Car car) {
        String info = String.format("""
        Targa: %s
        Marca: %s
        Modello: %s
        Prezzo al Giorno: %.2f €
        Disponibilità: %s
        """,
        car.getPlateNumber(),
        car.getBrand(),
        car.getModel(),
        car.getDailyPrice(),
        car.isAvailable() ? "Sì" : "No"
        );
        displayArea.setText(info);

    }

     /**
     * Mostra la prossima auto nella lista, se disponibile
     * 
     * Altrimenti appare la notifica che si è raggiunta la fine dell’elenco
     */
    private void showNext() {
        if (iterator.hasNext()) {
            currentCar = iterator.next();
            displayCar(currentCar);
        } else {
            JOptionPane.showMessageDialog(this, "Hai raggiunto l’ultima auto.");
        }
    }
}
