package com.carrentalmanager.composite;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

 /**
 * CarGroup rappresenta un gruppo o una categoria di auto es. "Basic", "Premium", "Luxury"
 * 
 * Implementa il pattern Composite, permettendo di contenere sia auto singole (CarLeaf) sia altri gruppi di auto (CarGroup), 
 * costruendo una gerarchia ad albero
 * 
 * Questo permette di gestire le auto in modo gerarchico attraverso operazioni di:
 * aggiunta, rimozione, conteggio e visualizzazione
 */

public class CarGroup extends CarComponent {

    // Logger per registrare info e errori legati al gruppo
    private static final Logger logger = Logger.getLogger(CarGroup.class.getName());

    // Nome del gruppo, es. "Luxury" oppure "Tutte le Categorie"
    private final String nameGroup;

    // Lista dei componenti figli (possono essere CarLeaf oppure CarGroup)
    private final List<CarComponent> children = new ArrayList<>();

    /**
     * Costruttore che inizializza il nome del gruppo
     * @param nameGroup Nome da assegnare al gruppo
     */
    public CarGroup(String nameGroup) {
        this.nameGroup = nameGroup;
    }

    /**
     * Aggiunge un componente al gruppo
     * 
     * Può essere una singola auto (CarLeaf) oppure un altro gruppo (CarGroup)
     * 
     * Utilizzato per costruire la gerarchia
     * 
     * Registra l'evento tramite il logger
    */
    @Override
    public void addChild(CarComponent component) {
        try {
            children.add(component);
            logger.info(() -> String.format("Componente aggiunto al gruppo '%s'", nameGroup));
        } catch (Exception e) {
            logger.log(Level.SEVERE, String.format("Errore durante l'aggiunta al gruppo '%s'", nameGroup), e);
        }
    }

    /**
     * Rimuove un componente dal gruppo
     * 
     * Può trattarsi di una singola auto o di un intero sottogruppo
     * 
     * Registra l'evento nel logger
    */
    @Override
    public void removeChild(CarComponent component) {
        try {
            children.remove(component);
            logger.info(() -> String.format("Componente rimosso dal gruppo '%s'", nameGroup));
        } catch (Exception e) {
            logger.log(Level.SEVERE, String.format("Errore durante la rimozione dal gruppo '%s'", nameGroup), e);
        }
    }

    /**
     * Calcola ricorsivamente il numero totale di auto disponibili all'interno del gruppo
     * 
     * Somma i valori restituiti da tutti i componenti figli
    */
    @Override
    public int countAvailableCars() {
        int totalCount = 0;
        try {
            for (CarComponent child : children) {
                totalCount += child.countAvailableCars();
            }
            final int count = totalCount;
            logger.info(() -> String.format("Auto disponibili nel gruppo '%s': %d", nameGroup, count));
        } catch (Exception e) {
            logger.log(Level.SEVERE, String.format("Errore nel calcolo delle auto disponibili nel gruppo '%s'", nameGroup), e);
        }
        return totalCount;
    }

    
    /**
     * Visualizza i dettagli del gruppo e dei suoi componenti. Stampa il nome del gruppo e il numero di auto disponibili,
     * poi richiama il metodo printDetails() su ciascun componente figlio
    */
    @Override
    public void printDetails() {
        try {
            int availableCount = countAvailableCars();
            final String groupInfo = String.format("Gruppo: %s - Auto disponibili per il noleggio: %d", nameGroup, availableCount);
            logger.info(() -> groupInfo);

            for (CarComponent component : children) {
                component.printDetails();
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, String.format("Errore durante la visualizzazione del gruppo '%s'", nameGroup), e);
        }
    }

    /**
     * Restituisce il nome assegnato al gruppo
     * 
     * @return Nome del gruppo
     */
    public String getName() {
        return nameGroup;
    }

    /**
     * Restituisce la lista dei componenti figli del gruppo
     * 
     * @return Lista dei componenti figli
     */
    public List<CarComponent> getChildren() {
        return children;
    }
}
