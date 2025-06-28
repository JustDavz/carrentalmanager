package com.carrentalmanager.gui;

import com.carrentalmanager.model.Rental;
import com.carrentalmanager.iterator.RentalCollection;

import javax.swing.*;
import java.awt.*;
import java.util.Iterator;

/**
 * RentalBrowser è una GUI che consente di navigare tra i noleggi registrati utilizzando il pattern Iterator tramite la classe RentalCollection
 * 
 * Ogni noleggio viene visualizzato con i suoi dettagli principali
 */
public class RentalBrowser extends JFrame {

    // Iteratore per la collezione dei noleggi
    private final transient Iterator<Rental> iterator;

    // Area di testo per mostrare le informazioni del noleggio
    private final JTextArea displayArea;

    // Noleggio attualmente mostrato
    private transient Rental currentRental;

    /**
     * Costruttore che inizializza la finestra e carica il primo noleggio disponibile
     *
     * @param rentalCollection raccolta di noleggi da esplorare
     */
    public RentalBrowser(RentalCollection rentalCollection) {
        this.iterator = rentalCollection.createIterator();

        setTitle("Navigatore Noleggi");
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

        // Mostra il primo noleggio se è presente
        if (iterator.hasNext()) {
            currentRental = iterator.next();
            displayRental(currentRental);
        } else {
            displayArea.setText("Nessun noleggio disponibile.");
        }

        setVisible(true);
    }

    /**
     * Visualizza le informazioni dettagliate di un singolo noleggio
     *
     * @param rental il noleggio da mostrare
     */
    private void displayRental(Rental rental) {
        String info = String.format("""
        Cliente: %s
        Auto: %s %s
        Targa: %s
        Inizio: %s
        Fine: %s
        Totale: %.2f €
        Stato: %s
        """,
        rental.getCustomer().getFullName(),
        rental.getCar().getBrand(),
        rental.getCar().getModel(),
        rental.getCar().getPlateNumber(),
        rental.getStartDate(),
        rental.getEndDate(),
        rental.getPrice(),
        rental.getStatus());
        
        displayArea.setText(info);
    }

    /**
     * Mostra il noleggio successivo nell’elenco se è presente
     * 
     * Altrimenti appare la notifica che si è raggiunta la fine dell’elenco
     */
    private void showNext() {
        if (iterator.hasNext()) {
            currentRental = iterator.next();
            displayRental(currentRental);
        } else {
            JOptionPane.showMessageDialog(this, "Hai raggiunto l’ultimo noleggio.");
        }
    }
}
