package com.carrentalmanager.gui;

import com.carrentalmanager.factory.CarFactory;
import com.carrentalmanager.model.Car;
import com.carrentalmanager.model.CarFuel;
import com.carrentalmanager.utils.FileManager;

import javax.swing.*;
import java.awt.*;
import java.util.List;

 /**
 * CarForm rappresenta una finestra modale (JDialog) per l’inserimento di una nuova auto
 * 
 * L'interfaccia consente all'utente di compilare i dati della vettura come targa, marca, modello, categoria, carburante e prezzo
 * 
 * I dati vengono validati tramite CarFactory. Se validi, l'auto viene salvata su file (FileManager) e aggiunta alla lista in memoria
 */

public class CarForm extends JDialog {

    /**
    * Costruttore che crea e mostra il form per l’inserimento di un’auto
    *
    * @param parent - finestra principale da cui viene aperto il form
    * @param cars - lista delle auto nel quale aggiungere il nuovo veicolo, se valido
    */

    public CarForm(JFrame parent, List<Car> cars) {
        super(parent, "Inserisci Auto", true); // titolo della finestra e modalità modale attiva
        setSize(450, 500); // dimensioni della finestra
        setLocationRelativeTo(parent); // centra la finestra rispetto al parent

        // Pannello con griglia per i campi del form composto da 7 righe e 2 colonne
        JPanel panel = new JPanel(new GridLayout(7, 2, 0, 0));

        // Campi di input
        JTextField plateField = new JTextField(); // Campo targa
        JTextField brandField = new JTextField(); // Campo marca
        JTextField modelField = new JTextField(); // Campo modello
        JComboBox<String> categoryBox = new JComboBox<>(new String[]{"Basic", "Premium", "Luxury"}); // Selezione categoria
        JComboBox<CarFuel> fuelComboBox = new JComboBox<>(CarFuel.values()); // Selezione carburante
        JTextField priceField = new JTextField(); // Campo prezzo

        // Etichette e campi inseriti nel pannello
        panel.add(new JLabel("Targa es. AB123CD:"));
        panel.add(plateField);
        panel.add(new JLabel("Marca:"));
        panel.add(brandField);
        panel.add(new JLabel("Modello:"));
        panel.add(modelField);
        panel.add(new JLabel("Categoria:"));
        panel.add(categoryBox);
        panel.add(new JLabel("Tipo carburante:"));
        panel.add(fuelComboBox);
        panel.add(new JLabel("Prezzo giornaliero €:"));
        panel.add(priceField);

        // Pulsanti Salva e Annulla
        JButton saveButton = new JButton("Salva");
        JButton cancelButton = new JButton("Annulla");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Aggiunta dei pannelli alla finestra
        add(panel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        /**
         * Listener del pulsante "Salva"
         * 
         * Crea un oggetto Car usando i dati del form, se è valido, lo salva su file e lista
         */
        saveButton.addActionListener(e -> {
            Car car = CarFactory.createCar(
                    plateField.getText().trim().toUpperCase(),
                    brandField.getText().trim(),
                    modelField.getText().trim(),
                    (String) categoryBox.getSelectedItem(),
                    ((CarFuel) fuelComboBox.getSelectedItem()).name(),
                    priceField.getText().trim(),
                    true // L'auto è subito disponibile per poter essere messa a disposizione del noleggio
            );

            if (car != null) {
                cars.add(car); // L'auto viene aggiunta alla lista in memoria
                FileManager.getInstance().saveCar(car); // Salvataggio su file
                JOptionPane.showMessageDialog(this, "Auto salvata con successo!");
                dispose(); // Chiusura finestra
            }
        });

        /**
         * Listener del pulsante "Annulla"
         * 
         * Chiude semplicemente la finestra senza salvare nulla
         */
        cancelButton.addActionListener(e -> dispose());

        // Mostra la finestra
        setVisible(true);
    }
}