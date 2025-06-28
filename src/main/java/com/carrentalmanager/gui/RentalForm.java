package com.carrentalmanager.gui;

import com.carrentalmanager.model.*;
import com.carrentalmanager.builder.RentalBuilder;
import com.carrentalmanager.strategy.*;
import com.carrentalmanager.utils.FileManager;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * RentalForm rappresenta una finestra modale (JDialog) per la creazione di un nuovo noleggio
 * 
 * L'interfaccia consente di selezionare cliente, auto disponibile, date del noleggio e servizi extra (assicurazione Kasko e Soccorso Stradale)
 * 
 * Applica lo Strategy Pattern per il calcolo del prezzo in base alla categoria dell'auto
 * 
 * Utilizza l’Observer Pattern per notificare l’inizio del noleggio
 * 
 * Salva i dati su file tramite FileManager e aggiorna la disponibilità del veicolo
 */
public class RentalForm extends JDialog {

    // Etichetta titolo per eventuali messaggi di errore
    private static final String ERRORE_TITLE = "Errore";

    // Componenti del form
    private JComboBox<Customer> customerCombo;
    private JComboBox<Car> carCombo;
    private JTextField startDateField;
    private JTextField endDateField;
    private JCheckBox kaskoCheck;
    private JCheckBox roadsideCheck;

    /**
     * Costruttore che crea e mostra il form per l’inserimento di un nuovo noleggio
     *
     * @param parent  finestra principale da cui viene aperto il form
     * @param customers lista dei clienti disponibili
     * @param cars lista delle auto disponibili
     * @param rentals lista dei noleggi su cui aggiungere il nuovo noleggio
     */
    public RentalForm(JFrame parent, List<Customer> customers, List<Car> cars, List<Rental> rentals) {
        super(parent, "Nuovo Noleggio", true); // Titolo della finestra e modalità modale attiva

        // Filtra le auto attualmente disponibili
        List<Car> availableCars = cars.stream().filter(Car::isAvailable).toList();

        // Se nessuna auto è disponibile, mostra un messaggio e chiude la finestra
        if (availableCars.isEmpty()) {
            JOptionPane.showMessageDialog(parent,
                    "Nessun veicolo disponibile al momento per il noleggio. Aggiungi altri veicoli.",
                    "Veicoli non disponibili",
                    JOptionPane.WARNING_MESSAGE);
            dispose();
            return;
        }

        // Configurazione base della finestra
        setSize(500, 400);
        setLayout(new GridLayout(8, 2, 10, 10)); // 8 righe, 2 colonne, spaziatura 10px
        setLocationRelativeTo(parent); // Centra la finestra rispetto al parent

        // Inizializzazione dei campi
        customerCombo = new JComboBox<>(customers.toArray(new Customer[0]));
        carCombo = new JComboBox<>(availableCars.toArray(new Car[0]));
        startDateField = new JTextField("2025-06-21"); // Data inizio default
        endDateField = new JTextField("2025-06-23");   // Data fine default
        kaskoCheck = new JCheckBox("Assicurazione Kasko");
        roadsideCheck = new JCheckBox("Soccorso Stradale");

        // Aggiunta etichette e componenti al form
        add(new JLabel("Cliente:"));
        add(customerCombo);
        add(new JLabel("Auto disponibile:"));
        add(carCombo);
        add(new JLabel("Data Inizio (AAAA-MM-GG):"));
        add(startDateField);
        add(new JLabel("Data Fine (AAAA-MM-GG):"));
        add(endDateField);
        add(new JLabel("Servizi Extra:"));

        // Pannello interno per i checkbox dei servizi extra
        JPanel extrasPanel = new JPanel(new GridLayout(1, 2));
        extrasPanel.add(kaskoCheck);
        extrasPanel.add(roadsideCheck);
        add(extrasPanel);

        // Pulsanti
        JButton confirmButton = new JButton("Conferma Noleggio");
        JButton cancelButton = new JButton("Annulla");

        // Listener pulsante "Conferma"
        confirmButton.addActionListener(e -> saveRental(cars, rentals));

        // Listener pulsante "Annulla"
        cancelButton.addActionListener(e -> dispose());

        // Aggiunta pulsanti al form
        add(confirmButton);
        add(cancelButton);

        // Visualizza la finestra
        setVisible(true);
    }

    /**
     * Metodo privato per validare, creare e salvare un nuovo noleggio
     * 
     * Valida i dati, assegna la strategia di prezzo in base alla categoria, salva su file e aggiorna stato
     *
     * @param cars lista completa delle auto per aggiornare disponibilità
     * @param rentals lista dei noleggi su cui aggiungere quello nuovo
     */
    private void saveRental(List<Car> cars, List<Rental> rentals) {
        try {
            // Recupera cliente e auto selezionati
            Customer selectedCustomer = (Customer) customerCombo.getSelectedItem();
            Car selectedCar = (Car) carCombo.getSelectedItem();

            // Verifica che entrambi siano selezionati
            if (selectedCustomer == null || selectedCar == null) {
                JOptionPane.showMessageDialog(this, "Seleziona cliente e auto.", ERRORE_TITLE, JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Parsing delle date
            LocalDate startDate = LocalDate.parse(startDateField.getText().trim());
            LocalDate endDate = LocalDate.parse(endDateField.getText().trim());

            // Validazione: data di fine non può essere prima della data di inizio
            if (endDate.isBefore(startDate)) {
                JOptionPane.showMessageDialog(this, "La data di fine deve essere successiva alla data di inizio.", ERRORE_TITLE, JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Verifica dei servizi selezionati
            boolean kasko = kaskoCheck.isSelected();
            boolean roadside = roadsideCheck.isSelected();

            // Creazione oggetto Rental tramite Builder
            Rental rental = new RentalBuilder(selectedCustomer, selectedCar, startDate, endDate)
                    .kasko(kasko)
                    .roadside(roadside)
                    .build();

            // Strategia di prezzo in base alla categoria del veicolo
            PricingStrategy strategy;
            switch (selectedCar.getCarCategory()) {
                case PREMIUM -> strategy = new PremiumRentalStrategy();
                case LUXURY -> strategy = new LuxuryRentalStrategy();
                case BASIC -> strategy = new BasicRentalStrategy();
                default -> {
                    JOptionPane.showMessageDialog(this, "Categoria auto non riconosciuta.", ERRORE_TITLE, JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // Applicazione strategia e calcolo del prezzo finale
            rental.setCostStrategy(strategy);
            rental.setPrice(rental.calculateTotalPrice());

            // Impostazione stato e disponibilità
            rental.setStatus(RentalStatus.APERTO);
            selectedCar.setAvailable(false);

            // Aggiunta alla lista e notifica observer
            rentals.add(rental);
            rental.notifyRentalStarted();

            // Salvataggio dati su file
            FileManager.getInstance().saveRental(rental);
            FileManager.getInstance().saveCars(cars);

            // Messaggio di conferma
            JOptionPane.showMessageDialog(this, "Noleggio salvato con successo!", "Successo", JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (Exception e) {
            // Gestione errori generici
            JOptionPane.showMessageDialog(this, "Errore: " + e.getMessage(), ERRORE_TITLE, JOptionPane.ERROR_MESSAGE);
        }
    }
}
