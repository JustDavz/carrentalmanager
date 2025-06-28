package com.carrentalmanager.composite;

/**
 * Classe astratta che rappresenta un elemento della gerarchia di auto secondo il pattern Composite.
 * Può rappresentare sia una singola auto (CarLeaf) che un gruppo di auto (CarGroup)
 * 
 * Permette di gestire in modo uniforme categorie e sottocategorie,
 * supportando operazioni come visualizzazione e conteggio delle auto disponibili
 */

public abstract class CarComponent {

    /**
     * Aggiunge un componente figlio (auto o gruppo di auto)
     * 
     * Implementato solo da CarGroup. Se chiamato su CarLeaf, lancia un'eccezione
    */
    public void addChild(CarComponent component) {
        throw new UnsupportedOperationException("Operazione non supportata.");
    }

    /**
     * Rimuove un componente figlio (auto o gruppo)
     * 
     * Implementato solo in CarGroup. Se chiamato su CarLeaf, lancia un'eccezione
     */
    public void removeChild(CarComponent component) {
        throw new UnsupportedOperationException("Operazione non supportata.");
    }

    /**
     * Restituisce il numero di auto disponibili.
     * 
     * Se l'istanza è una CarLeaf e l'auto è disponibile, restituisce 1, altrimenti 0
     * 
     * Se è un CarGroup, somma ricorsivamente le auto disponibili nei figli
     */
    public int countAvailableCars() {
        throw new UnsupportedOperationException("Operazione non supportata.");
    }

    /**
     * Visualizza le informazioni del componente
     * 
     * Se è una singola auto: mostra marca, modello, targa e disponibilità
     * 
     * Se è un gruppo, mostra il nome della categoria e richiama il metodo printDetails() su ogni figlio
     */
    public void printDetails() {
        throw new UnsupportedOperationException("Operazione non supportata.");
    }
}
