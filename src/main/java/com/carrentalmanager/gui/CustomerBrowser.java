package com.carrentalmanager.gui;

import com.carrentalmanager.model.Customer;
import com.carrentalmanager.iterator.CustomerCollection;

import javax.swing.*;
import java.awt.*;
import java.util.Iterator;

 /**
 * CustomerBrowser è una GUI che consente di navigare tra i clienti registrati utilizzando il pattern Iterator tramite la classe CustomerCollection
 * 
 * Ogni cliente viene visualizzato con i suoi dati principali
 */
public class CustomerBrowser extends JFrame {

    // Iteratore per la collezione dei clienti
    private final transient Iterator<Customer> iterator;

   // Area di testo per mostrare le informazioni del cliente
    private final JTextArea displayArea;

    // Cliente attualmente mostrato
    private transient Customer currentCustomer;

    /**
    * Costruttore che inizializza la finestra e carica il primo cliente disponibile
    *
    * @param customerCollection raccolta di clienti da esplorare
    */
    public CustomerBrowser(CustomerCollection customerCollection) {
        this.iterator = customerCollection.createIterator();

        setTitle("Navigatore Clienti");
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

        // Mostra il primo cliente se è presente
        if (iterator.hasNext()) {
            currentCustomer = iterator.next();
            displayCustomer(currentCustomer);
        } else {
            displayArea.setText("Nessun cliente disponibile.");
        }

        setVisible(true);
    }

    /**
     * Visualizza le informazioni dettagliate di un singolo cliente
     *
     * @param customer il cliente da mostrare
     */
    private void displayCustomer(Customer customer) {
        String info = String.format("""
        Nome: %s
        Cognome: %s
        Codice Fiscale: %s
        Patente: %s
        Email: %s
        Telefono: %s
        """,
        customer.getFirstName(),
        customer.getLastName(),
        customer.getID(),
        customer.getLicenseNumber(),
        customer.getEmail(),
        customer.getPhone()
        );
        displayArea.setText(info);
    }

    /**
     * Mostra il cliente successivo nell’elenco se è presente
     * 
     * Altrimenti appare la notifica che si è raggiunta la fine dell’elenco
     */
    private void showNext() {
        if (iterator.hasNext()) {
            currentCustomer = iterator.next();
            displayCustomer(currentCustomer);
        } else {
            JOptionPane.showMessageDialog(this, "Hai raggiunto l’ultimo cliente.");
        }
    }
}
