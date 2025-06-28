package com.carrentalmanager.gui;

import com.carrentalmanager.factory.CustomerFactory;
import com.carrentalmanager.model.Customer;
import com.carrentalmanager.utils.FileManager;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * CustomerForm rappresenta una finestra modale (JDialog) per l’inserimento dei dati anagrafici di un nuovo cliente
 * 
 * L'interfaccia consente all'utente di compilare i dati anagrafici di un cliente ID(Codice Fiscale), nome, cognome, patente, email e telefono)
 * 
 * I dati vengono validati tramite CustomerFactory. Se validi, il cliente viene salvato su file (FileManager) e aggiunto alla lista in memoria
*/

public class CustomerForm extends JDialog {

    /**
     * Costruttore che crea e mostra il form per inserire un nuovo cliente
     * 
     * @param parent finestra principale da cui viene aperto il form
     * @param customers lista dei clienti a cui aggiungere il nuovo cliente se valido
    */

    public CustomerForm(JFrame parent, List<Customer> customers) {
        super(parent, "Inserisci Cliente", true); 
        setSize(400, 400);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(7, 2, 10, 10)); 

        // Campi di input
        JTextField idField = new JTextField(); // Codice fiscale
        JTextField firstNameField = new JTextField(); // Nome
        JTextField lastNameField = new JTextField(); // Cognome
        JTextField licenseField = new JTextField(); // Numero patente
        JTextField emailField = new JTextField(); // Email
        JTextField phoneField = new JTextField(); // Telefono

        // Pulsanti Salva e Annulla
        JButton saveButton = new JButton("Salva");
        JButton cancelButton = new JButton("Annulla");

        // Etichette e campi inseriti nel pannello
        add(new JLabel("Codice Fiscale:"));
        add(idField);
        add(new JLabel("Nome:"));
        add(firstNameField);
        add(new JLabel("Cognome:"));
        add(lastNameField);
        add(new JLabel("Numero Patente:"));
        add(licenseField);
        add(new JLabel("Email:"));
        add(emailField);
        add(new JLabel("Telefono:"));
        add(phoneField);
        add(saveButton);
        add(cancelButton);

        /**
         * Listener del pulsante "Salva"
         * 
         * Crea un oggetto Customer usando i dati del form, se è valido, lo salva su file e lista
         */
        saveButton.addActionListener(e -> {
            try {
                Customer customer = CustomerFactory.createCustomer(
                        idField.getText().trim(),
                        firstNameField.getText().trim(),
                        lastNameField.getText().trim(),
                        licenseField.getText().trim(),
                        emailField.getText().trim(),
                        phoneField.getText().trim()
                );

                if (customer != null) {
                    FileManager.getInstance().saveCustomer(customer); // Salvataggio su file
                    customers.add(customer); // Aggiunta alla lista
                    JOptionPane.showMessageDialog(this, "Cliente salvato con successo!");
                    dispose(); // Chiusura finestra
                }
            } catch (IllegalArgumentException ex) {
                // Messaggio di errore se qualcosa non è valido
                JOptionPane.showMessageDialog(this,
                        "Errore: " + ex.getMessage(),
                        "Errore di validazione",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        // Listener Annulla
        cancelButton.addActionListener(e -> dispose());

        // Mostra la finestra
        setVisible(true);
    }
}
