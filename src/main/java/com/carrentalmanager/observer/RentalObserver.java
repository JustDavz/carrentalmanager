package com.carrentalmanager.observer;

import com.carrentalmanager.model.Rental;

/**
 * Interfaccia che definisce i metodi di notifica per gli osservatori di eventi di noleggio
 * 
 * Fa parte del pattern Observer: gli oggetti che la implementano possono essere notificati
 * quando un noleggio viene avviato o chiuso
 */
public interface RentalObserver {

    /** Viene notificato quando un noleggio viene chiuso */
    void onRentalClosed(Rental rental);

    /** Viene notificato quando un noleggio viene avviato */
    void onRentalStarted(Rental rental);
}
