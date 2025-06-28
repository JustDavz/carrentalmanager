package com.carrentalmanager.gui;

import com.carrentalmanager.model.*;
import com.carrentalmanager.strategy.*;
import com.carrentalmanager.utils.FileManager;
 
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
 
/**
 * Finestra grafica per la gestione dei noleggi:
 * - Ordinare i noleggi utilizzando diverse strategie (Strategy Pattern)
 * - Visualizzare lo stato attuale di ciascun noleggio
 * - Chiudere un noleggio selezionato e aggiornare la disponibilità dell’auto
 * - Visualizzare il conteggio dei noleggi aperti e chiusi
 */

public class RentalManagerForm extends JDialog {
    
    private final JTable table; // Tabella che mostra i noleggi
    private final DefaultTableModel model; // Modello dati della tabella
    private final JComboBox<String> sortCombo; // Menù a tendina per selezionare la tipologia di ordinamento
    private final JLabel countLabel; // Etichetta che mostra il numero di noleggi aperti e chiusi

 
    /**
     * Costruttore principale del form per la gestione dei noleggi
     *
     * @param parent  Finestra principale (MainGUI)
     * @param rentals Elenco dei noleggi attivi o passati
     * @param cars    Elenco delle auto, utilizzato per aggiornare la disponibilità dopo la chiusura
     */
    
    public RentalManagerForm(JFrame parent, List<Rental> rentals, List<Car> cars) {
         super(parent, "Gestione Noleggi", true);
 
         setSize(900, 500);
         setLayout(new BorderLayout());
         setLocationRelativeTo(parent);
 
         // Dropdown per ordinare i noleggi
         sortCombo = new JComboBox<>(new String[]{
             "Ordina per Data di Inizio",
             "Ordina per Data di Fine (Crescente)",
             "Ordina per Data di Fine (Decrescente)",
             "Ordina per Prezzo",
             "Ordina per Stato",
             "Ordina per Nome Cliente"
         });        
 
         // Applica la strategia selezionata al cambio della comboBox
         sortCombo.addActionListener(e -> {
             SortStrategy<Rental> strategy = switch ((String) sortCombo.getSelectedItem()) {
                 case "Ordina per Prezzo" -> new SortRentalsByPrice();
                 case "Ordina per Stato" -> new SortRentalsByStatus();
                 case "Ordina per Nome Cliente" -> new SortRentalsByCustomerName();
                 case "Ordina per Data di Fine (Crescente)" -> new SortRentalsByEndDateAsc();
                 case "Ordina per Data di Fine (Decrescente)" -> new SortRentalsByEndDateDesc();
                 case "Ordina per Data di Inizio" -> new SortRentalsByStartDate();
                 default -> new SortRentalsByStartDate();
             };
         
             strategy.sort(rentals);
             loadTableData(rentals);
         });
 
         // Pannello superiore con etichetta e comboBox di ordinamento
         JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
         topPanel.add(new JLabel("Ordina noleggi:"));
         topPanel.add(sortCombo);
         add(topPanel, BorderLayout.NORTH);
 
         // Tabella con colonne predefinite
         model = new DefaultTableModel(new String[]{
                 "Cliente", "Auto", "Inizio", "Fine", "Prezzo", "Kasko", "Soccorso", "Stato"
         }, 0);
 
         table = new JTable(model);
         JScrollPane scrollPane = new JScrollPane(table);
         add(scrollPane, BorderLayout.CENTER);
 
         // Pulsante per chiudere un noleggio selezionato
         JButton closeRentalButton = new JButton("Chiudi Noleggio Selezionato");
         closeRentalButton.addActionListener(e -> closeSelectedRental(rentals, cars));
 
         // Etichetta di stato noleggi e pannello inferiore
         countLabel = new JLabel();
         updateCountLabel(rentals);
 
         JPanel bottomPanel = new JPanel(new BorderLayout());
         bottomPanel.add(closeRentalButton, BorderLayout.CENTER);
         bottomPanel.add(countLabel, BorderLayout.SOUTH);
         add(bottomPanel, BorderLayout.SOUTH);
 
         // Caricamento iniziale dei dati nella tabella
         loadTableData(rentals);
 
         setVisible(true);
    }
 
     /**
      * Carica i dati dei noleggi nella tabella
      * @param rentals lista noleggi da visualizzare
    */
    private void loadTableData(List<Rental> rentals) {
        model.setRowCount(0); // Pulisce la tabella
 
        for (Rental rental : rentals) {
            model.addRow(new Object[]{
                    rental.getCustomer().getFullName(),
                    rental.getCar().getBrand() + " " + rental.getCar().getModel(),
                    rental.getStartDate(),
                    rental.getEndDate(),
                    String.format("€%.2f", rental.getPrice()),
                    rental.isKaskoInsurance() ? "Sì" : "No",
                    rental.isRoadsideAssistance() ? "Sì" : "No",
                    rental.getStatus()
             });
         }
 
         updateCountLabel(rentals);
    }
 
    /**
     * Aggiorna l’etichetta che mostra il numero di noleggi aperti e chiusi
     * 
     * @param rentals lista noleggi
    */
    private void updateCountLabel(List<Rental> rentals) {
        long open = rentals.stream().filter(rental -> rental.getStatus() == RentalStatus.APERTO).count();
        long closed = rentals.stream().filter(rental -> rental.getStatus() == RentalStatus.CHIUSO).count();
        countLabel.setText("Noleggi aperti: " + open + "    Noleggi chiusi: " + closed);
    }
 
     /**
      * Chiude il noleggio selezionato e aggiorna lo stato della vettura
      *
      * @param rentals lista dei noleggi
      * @param cars lista delle auto per aggiornare le disponibilità
      */
     private void closeSelectedRental(List<Rental> rentals, List<Car> cars) {
         int selectedRow = table.getSelectedRow();
 
         if (selectedRow == -1) {
             JOptionPane.showMessageDialog(this, "Seleziona un noleggio da chiudere.", "Errore", JOptionPane.ERROR_MESSAGE);
             return;
         }
 
         Rental rental = rentals.get(selectedRow);
 
         // Evita di chiudere due volte lo stesso noleggio
         if (rental.getStatus() == RentalStatus.CHIUSO) {
             JOptionPane.showMessageDialog(this, "Il noleggio è già chiuso.", "Info", JOptionPane.INFORMATION_MESSAGE);
             return;
         }
 
         // Conferma da parte dell'utente
         int confirm = JOptionPane.showConfirmDialog(this, "Confermi la chiusura del noleggio?", "Conferma", JOptionPane.YES_NO_OPTION);
 
         if (confirm == JOptionPane.YES_OPTION) {
             rental.setStatus(RentalStatus.CHIUSO);
             rental.getCar().setAvailable(true); // Rende l’auto nuovamente disponibile
 
             // Salvataggio dei dati aggiornati
             FileManager.getInstance().saveCars(cars);
             FileManager.getInstance().saveAllRentals(rentals);
 
             // Mostra all'utente il totale da pagare
             String customerName = rental.getCustomer().getFullName();
             double totalPrice = rental.getPrice();
 
             JOptionPane.showMessageDialog(this,
                     "Cliente: " + customerName + "\nTotale da pagare: €" + String.format("%.2f", totalPrice),
                     "Pagamento",
                     JOptionPane.INFORMATION_MESSAGE);
 
             loadTableData(rentals); // Aggiorna tabella e contatore
         }
    }
}
 