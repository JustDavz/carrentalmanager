package com.carrentalmanager.gui;

import com.carrentalmanager.composite.*;
import com.carrentalmanager.model.Car;

import javax.swing.*;

 /**
 * CarGroupBrowser è una finestra GUI (JFrame) che consente la visualizzazione gerarchica delle auto suddivise per categoria
 * 
 * Utilizza il Composite Pattern per mostrare la struttura ad albero dei gruppi e delle auto
 */

public class CarGroupBrowser extends JFrame {

    /**
     * Costruttore che inizializza la finestra per la visualizzazione elle auto per categoria.
     *
     * @param rootGroup Il gruppo radice della gerarchia di auto
     */
    public CarGroupBrowser(CarGroup rootGroup) {
        setTitle("Auto per Categoria");  // Titolo finestra
        setSize(500, 400);        // Dimensioni finestra
        setLocationRelativeTo(null);         // Centra la finestra sullo schermo

        JTextArea displayArea = new JTextArea(); // Area di testo per la visualizzazione
        displayArea.setEditable(false); // L’utente non può modificare il contenuto

        JScrollPane scrollPane = new JScrollPane(displayArea); // Scroll in caso di contenuto lungo
        add(scrollPane); // Aggiunta del pannello scrollabile al frame

        // Costruisce la struttura da mostrare
        StringBuilder builder = new StringBuilder();
        appendGroupInfo(rootGroup, builder, "");  // Popola il builder con info ricorsive
        displayArea.setText(builder.toString());  // Mostra il risultato nella JTextArea

        setVisible(true);  // Mostra la finestra
    }

    /**
     * Metodo ricorsivo per costruire la rappresentazione testuale della gerarchia di auto
     * 
     * Mostra nome del gruppo, disponibilità e dettagli delle auto singole
     *
     * @param group   Gruppo da elaborare
     * @param builder Accumulatore testuale
     * @param indent  Rientro grafico per simulare la gerarchia
     */

    private void appendGroupInfo(CarGroup group, StringBuilder builder, String indent) {
        // Mostra il nome della categoria e il numero di auto disponibili
        builder.append(indent)
                .append("Categoria: ")
                .append(group.getName())
                .append(" - Vetture disponibili: ")
                .append(group.countAvailableCars())
                .append("\n");

        // Itera sui figli del gruppo possono essere gruppi o auto singole
        for (CarComponent component : group.getChildren()) {
            if (component instanceof CarGroup carGroup) {
                // Chiamata ricorsiva per i sottogruppi
                appendGroupInfo(carGroup, builder, indent + "  ");
            } else if (component instanceof CarLeaf leaf && leaf.getCar() != null) {
                // Stampa info dell'auto singola
                Car car = leaf.getCar();
                builder.append(indent)
                        .append("  - ")
                        .append(car.getBrand()).append(" ")
                        .append(car.getModel()).append(" (")
                        .append(car.getPlateNumber()).append(")")
                        .append("  - ")
                        .append(car.isAvailable() ? " Disponibile per il noleggio" : " Non disponibile per il noleggio.")
                        .append("\n");
            }
        }
    }
}
