package com.carrentalmanager.model;

/**
 * Classe che rappresenta un cliente del sistema di noleggio auto
 * 
 * Contiene le informazioni anagrafiche e di contatto
 * 
 * Il campo 'id' rappresenta il Codice Fiscale ed è utilizzato come identificativo univoco
 */
public class Customer {

    private String id;            // Codice Fiscale (ID univoco)
    private String firstName;     // Nome 
    private String lastName;      // Cognome 
    private String licenseNumber; // Numero della patente di guida
    private String email;         // Indirizzo email
    private String phone;         // Numero di telefono

    /**
     * Costruttore principale
     * 
     * Inizializza un nuovo cliente con tutti i suoi dati
     */
    public Customer(String id, String firstName, String lastName, String licenseNumber, String email, String phone) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.licenseNumber = licenseNumber;
        this.email = email;
        this.phone = phone;
    }

    /** Metodi Getter e Setter */

    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    /** Metodo che restituisce il nome completo del cliente */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /** Restituisce una rappresentazione leggibile del cliente */
    @Override
    public String toString() {
        return getFullName() + " (Codice Fiscale: " + id + ", Patente: " + licenseNumber + ")";
    }
}
