# CarRentalManager

## Panoramica dell'applicazione e funzionalità

**CarRentalManager** è un’applicazione sviluppata in Java SE per la gestione di autonoleggi. L’interfaccia grafica, realizzata con Swing, permette di inserire e gestire clienti, veicoli e noleggi (apertura e chiusura) e consultare tutti i dati. L’architettura è basata su diversi design pattern, che garantiscono modularità e un’organizzazione chiara del codice. Tutti i dati vengono salvati in file CSV, garantendo una persistenza semplice e immediata, senza ricorrere a un database.

### Funzionalità principali

- Inserimento di clienti, auto e noleggi tramite form con validazione dei dati.
- Visualizzazione e navigazione dei dati tramite interfacce dedicate, realizzate con il pattern Iterator, che consente di scorrere clienti, auto e noleggi uno alla volta senza accedere direttamente alle liste.
- Gestione completa dei noleggi, con possibilità di chiusura e aggiornamento dello stato.
- Raggruppamento delle auto per categoria (Basic, Premium, Luxury) tramite struttura gerarchica ad albero, utilizzando il pattern Composite con le classi CarGroup e CarLeaf.
- Calcolo dinamico del prezzo del noleggio in base alla categoria dell’auto e ai servizi extra selezionabili (es. polizza Kasko, soccorso stradale). Se non vengono selezionati i servizi extra, il costo si basa sul prezzo base dell’auto moltiplicato per i giorni di noleggio.
- Ordinamento dinamico dei noleggi per: nome del cliente, data di inizio, data di fine, stato del noleggio e prezzo.
- Salvataggio automatico e persistente dei dati su file CSV.
- Caricamento automatico dei dati all’avvio dell'applicazione dai file CSV.
- Log automatico degli eventi principali del sistema (es. creazione e chiusura noleggio, errori), gestito in modo centralizzato tramite `java.util.logging` 
- Gestione robusta delle eccezioni tramite Exception Shielding, con messaggi chiari all’utente.
- Interfaccia grafica realizzata con Java Swing.


## Tecnologie e pattern utilizzati

### Tecnologie

- **Java 17 o superiore**

- **Maven** –> Gestisce dipendenze, build e test unitari del progetto.

- **Swing (javax.swing)** – Utilizzato per realizzare l’interfaccia grafica (package `gui`), incluse le classi `MainGUI`, `CustomerForm`, `CarForm`, `RentalForm` e i browsers per la navigazione dei dati. 

- **Java I/O su file CSV** – Le classi contenute nel package `com.carrentalmanager.utils` (`FileManager`, `CarFileManager`, `CustomerFileManager`, `RentalFileManager`) utilizzano le API `java.io` per leggere e scrivere in modo efficiente i dati nei file CSV (`customers.csv`, `cars.csv`, `rentals.csv`), garantendo la persistenza delle informazioni senza ricorrere a un database.

- **Logging API (`java.util.logging`)** – Gestione centralizzata dei log per l'intero sistema. Il logger è utilizzato principalmente nella classe `RentalLogger` (package `observer`), ma compare anche in altre parti dell'applicazione per tracciare operazioni, errori e eventi rilevanti.

- **Collections & Generics** – Usati in tutte le classi per la gestione di liste generiche (`List<Customer>`, `List<Car>`, `List<Rental>`) nei form, browsers e file manager.

- **Stream API** – Usata insieme allo Strategy Pattern per filtrare e ordinare i noleggi per: data di inizio, data di fine, prezzo e nome cliente.

- **Lambda Expressions** – Utilizzate sia in combinazione con le Stream API per il filtraggio e l’ordinamento, sia in modo indipendente, ad esempio per ottimizzare le chiamate ai logger (es. LOGGER.warning(() -> "...")), migliorando la leggibilità.

- **Multithreading** – Utilizzato nella classe `FileManager` per eseguire il salvataggio dei dati (clienti, auto e noleggi) in modo asincrono tramite un thread separato, migliorando la reattività dell’interfaccia grafica e prevenendo blocchi durante le operazioni di I/O.

- **JUnit5 per il Testing** – I test coprono l’intera build dell’applicazione, incluse le classi `FileManager` e le principali implementazioni dei pattern (`Factory`, `Strategy`, `Composite`, `Iterator`, `Observer`), oltre ai modelli `Customer`, `Car` e `Rental`. Le classi di test, come `CustomerFileManagerTest`, `CarFileManagerTest` e `RentalFileManagerTest`, verificano la correttezza del salvataggio/caricamento da file CSV e della logica applicata ai dati.

### Design Pattern implementati
**Design Pattern** - `Package` - Descrizione dettagliata  

**Factory**  - `factory` - Gli oggetti `Car`, `Customer` e `Rental` vengono creati solo dopo aver superato le validazioni dei dati. 

**Strategy** - `strategy`- Utilizzato per il calcolo dinamico del prezzo dei noleggi in base alla categoria del veicolo e ai servizi extra. Inoltre applicato per l'ordinamento dei noleggi per data, cliente, stato e prezzo.

**Composite** - `composite` - Implementa una struttura gerarchica per la gestione delle auto tramite le classi `CarGroup` (composito) e `CarLeaf` (foglia). Ogni categoria di veicoli (Basic, Premium, Luxury) è rappresentata come un gruppo che contiene le auto disponibili, visualizzate in modo strutturato e gerarchico nell’interfaccia grafica.

**Iterator** - `iterator` -  Implementato per scorrere in modo sicuro le collezioni di clienti, auto e noleggi senza esporre direttamente le liste. Utilizzato nei browsers Swing per navigare tra gli elementi in modo controllato e modulare.

**Observer** - `observer`- Implementato per notificare automaticamente eventi legati ai noleggi. Quando un noleggio viene creato o chiuso, il `RentalLogger` registra automaticamente l’evento nel log, migliorando il tracciamento delle operazioni del sistema.

**Singleton** - `utils` (`FileManager`, `CarFileManager`, `CustomerFileManager` e `RentalFileManager`) - Utilizzato per centralizzare la gestione dei file CSV. Ogni "FileManager" è implementato come Singleton, garantendo un’unica istanza condivisa per accedere e salvare i dati, evitando conflitti e ridondanze.

**Exception Shielding** - `utils`, `gui`, `strategy`, `composite`, `observer` - Le operazioni sensibili come lettura/scrittura dei file, calcolo dei prezzi e aggiornamenti dell’interfaccia grafica sono protette tramite blocchi try/catch, con logging integrato. Gli errori vengono gestiti in modo controllato e comprensibile per l’utente, evitando crash del programma.

**Builder** - `builder` (`RentalBuilder`) - Utilizzato per costruire oggetti `Rental` complessi in modo chiaro e flessibile, gestendo con semplicità numerosi campi opzionali come la strategia di prezzo, i servizi extra e lo stato del noleggio. Evita l’uso di costruttori lunghi e difficili da leggere, migliorando la manutenibilità del codice e riducendo il rischio di errori nella fase di creazione degli oggetti.

## Istruzioni per setup ed esecuzione

### Prerequisiti

- Java Development Kit (JDK) 17 o superiore
- Maven installato e configurato
- IDE consigliato: VS Code, Eclipse, IntelliJ IDEA

### Esecuzione

1. Clona o decomprimi il progetto.
2. Da terminale, entra nella directory:
```bash
  cd carrentalmanager
```
3. Compila con Maven:
```bash
   mvn clean install
```
4. Avvia:
```bash
   mvn exec:java -Dexec.mainClass="com.carrentalmanager.app.Main"
```
In alternativa, esegui `Main.java` da IDE. 

I file `customers.csv`, `cars.csv`, `rentals.csv` verranno creati automaticamente.

### Architettura dei pacchetti
```
src/
├── main/java/com/carrentalmanager/...     # Codice principale
└── test/java/com/carrentalmanager/...     # Test unitari con JUnit
```

### Architettura dettagliata

```
com.carrentalmanager
├── app          - Classe `Main.java` che avvia il programma e gestisce l’inizializzazione.
├── builder      - Contiene la classe `RentalBuilder`, che usa il pattern Builder per creare oggetti `Rental` complessi in modo chiaro e gestire
                    facilmente campi opzionali come strategia di prezzo, servizi extra e stato del noleggio.
├── gui          - Interfaccia grafica Swing (CustomerForm, CarForm, RentalForm, MainGUI, Browsers dedicati per la navigazione e gerarchia).
├── model        - Classi dati principali: `Customer`, `Car` - `Car Category`, 'CarFuel', `Rental` - `RentalStatus`.
├── factory      - Factory per la creazione controllata di `Customer`, `Car`, `Rental`.
├── strategy     - Strategie per il calcolo dinamico del prezzo dei noleggi e per l’ordinamento.
├── composite    - Rappresentazione ad albero delle categorie di auto (`CarGroup`, `CarLeaf`).
├── iterator     - Iteratori personalizzati per la navigazione dei dati.
├── observer     - Implementazione dell’Observer Pattern per il logging degli eventi (es. `RentalLogger`) - per gestire il cambio di stato dei noleggi.
└── utils        - Classi di supporto per la creazione, inserimento e caricamento dei dati nei file CSV: `FileManager`, `CarFileManager`, `CustomerFileManager`, `RentalFileManager`.
```

### Testing
Il progetto include una suite di test unitari con **JUnit 5** organizzata in classi dedicate per ciascun modulo.

I test unitari sono collocati in `src/test/java/com/carrentalmanager` e rispecchiando la stessa struttura nella directory di produzione.

Per compilare ed eseguire i test unitari con **JUnit**:

1. Compila il progetto
```bash
  mvn clean compile
```

2. Esegui tutti i test **JUnit**:
```bash
  mvn test
```

### Diagrammi UML

Nel progetto sono presenti due versioni del diagramma UML:
Diagramma grafico (PDF) - uml/uml.pdf
Diagramma testuale in PlantUML - uml/CarRentalManager.puml

## Limitazioni e sviluppi futuri

### Limitazioni
**Persistenza tramite CSV** - L'applicazione utilizza file CSV per salvare dati relativi a clienti, veicoli e noleggi. Questo approccio è semplice ma poco adatto a scenari reali con grandi volumi di dati. Quindi non è presente un database.

**Assenza di autenticazione** - L'applicazione non implementa alcun sistema di login con ruoli definiti, autenticazione o controllo delle sessioni. Tutte le funzionalità sono disponibili a qualsiasi utente, senza distinzione di ruoli o permessi.

**Mancanza di notifiche in tempo reale** - Non sono inclusi meccanismi per aggiornamenti automatici sullo stato dei noleggi o delle auto (es. notifiche quando un’auto torna disponibile).

**Filtri e ricerche limitate** - L’ordinamento dinamico dei noleggi è gestito tramite il pattern Strategy, ma non è presente un sistema di filtraggio combinato o ricerche avanzate sui dati.


### Sviluppi futuri

**Migrazione a database relazionale** - Sostituzione del sistema attuale basato su CSV con un database relazionale (es. MySQL) per garantire scalabilità, integrità referenziale e accesso concorrente ai dati.

**Sistema di autenticazione e autorizzazione** - Implementazione di un sistema di login con ruoli definiti (es. admin, operatore), gestione sicura delle sessioni e protezione delle funzionalità critiche tramite Spring Security.

**Notifiche e aggiornamenti in tempo reale** - Integrazione di WebSocket o polling asincrono per aggiornamenti live sullo stato dei noleggi, cambi di disponibilità dei veicoli e notifiche all’utente.

**Moduli estesi** - Aggiunta di funzionalità avanzate come: Generazione automatica di ricevute e contratti in PDF, Integrazione con sistemi di messaggistica (email/SMS) per conferme e promemoria, Firma digitale dei contratti direttamente in GUI.

**Spring Framework** - Refactoring dell’architettura per utilizzare Spring e Spring Boot, migliorando la gestione delle dipendenze, l’iniezione dei componenti e la configurazione.


## Creatore 
Progetto sviluppato da David Conocchioli per l’esame finale del corso Java OOP - Epicode 2025.


## Note Finali
**CarRentalManager** è stato progettato con un'attenzione particolare alla modularità, estensibilità e sicurezza, integrando i principali design pattern e tecnologie dell'OOP in Java.

Il codice adotta una forma semplificata di architettura **MVC**:
- **Model** - pacchetto `model` (gestione dei dati: clienti, auto, noleggi)  
- **View** - pacchetto `gui` (interfaccia grafica Swing)  
- **Controller/Logic** - logica distribuita tra `app`, `factory`, `strategy`, `observer`, `iterator`, `builder` e `utils`  

Ogni scelta progettuale è documentata nel codice e descritta nel presente README.
