# CarRentalManager 

## Panoramica dell'applicazione

**CarRentalManager** è un progetto sviluppato in React per simulare una piattaforma di noleggio auto.

L'obiettivo dell'applicazione è permettere ad un utente di cercare un'auto, controllarne la disponibilità, scegliere le date di noleggio, completare una prenotazione e consultare le proprie prenotazioni.

Il progetto utilizza **JSON Server** come backend simulato, così da gestire utenti, auto, servizi e prenotazioni senza creare un backend reale.

Per la gestione dello stato globale viene utilizzato **Redux Toolkit**, mentre la navigazione tra le pagine è gestita tramite **React Router**.

### Funzionalità principali

- Homepage con sezione hero con video di sfondo e form di ricerca.
- Catalogo auto con filtri per marca, categoria, carburante, trasmissione e disponibilità per date.
- Scheda dettaglio auto con informazioni del veicolo, prezzo giornaliero e luogo di ritiro.
- Calcolo automatico dei giorni di noleggio, dei servizi extra e del prezzo totale.
- Registrazione e login utente con validazione dei campi.
- Salvataggio dell'utente nel `localStorage` dopo il login.
- Gestione del profilo utente: dei dati di fatturazione e della patente.
- Checkout per confermare la prenotazione e aggiungere eventuali servizi accessori.
- Pagamento simulato tramite carta di credito o bonifico bancario.
- Area account con riepilogo delle prenotazioni e modifica dei dati personali.
- Dashboard admin per visualizzare e gestire auto e prenotazioni.
- Gestione degli stati delle prenotazioni: `IN_ATTESA`, `IN_LAVORAZIONE`, `APPROVATO`, `RIFIUTATO` e `CHIUSO`.
- Possibilità per l'admin di approvare, rifiutare o chiudere una prenotazione.
- Navbar responsive.
- Footer con sedi, recapiti, link principali e social.
- Layout responsive.
- Backend locale simulato tramite `db.json` con JSON Server.

## Tecnologie e librerie utilizzate

### Tecnologie principali

- **React** - Libreria principale utilizzata per costruire l'interfaccia utente tramite componenti riutilizzabili.
- **Vite** - Tool di sviluppo e build utilizzato per avviare rapidamente il progetto React e generare la versione di produzione.
- **JavaScript ES Modules** - Il progetto usa `"type": "module"` nel `package.json`, quindi importa ed esporta moduli con sintassi `import/export`.
- **Redux Toolkit** - Utilizzato per organizzare lo stato globale dell'applicazione tramite slice e thunk asincroni.
- **React Redux** - Permette ai componenti React di leggere dati dallo store e inviare azioni tramite `useSelector` e `useDispatch`.
- **React Router DOM** - Gestisce la navigazione tra le pagine dell'applicazione senza ricaricare il browser.
- **Bootstrap** - Framework CSS utilizzato per la grafica generale dell'applicazione come ad esempio griglie, pulsanti, form e classi responsive.
- **React Bootstrap** - Componenti Bootstrap integrati in React.
- **React DatePicker** - Componente utilizzato per selezionare data di ritiro e data di riconsegna.
- **date-fns** - Libreria utilizzata per la localizzazione italiana del DatePicker (utilizzato nel calendario di prenotazione auto).
- **JSON Server** - Backend locale simulato che espone API REST partendo dal file `db.json`.
- **Concurrently** - Permette di avviare contemporaneamente frontend Vite e backend JSON Server con un solo comando (npm run start).

## Istruzioni per setup ed esecuzione

### Prerequisiti

- **Node.js** installato.
- **npm** installato.
- IDE consigliato: **Visual Studio Code**.
- Terminale di sistema.

### Installazione

1. Clonare o decomprimere il progetto.

2. Entrare nella cartella principale del progetto:

```bash
cd carrentalmanager
```

3. Installare tutte le dipendenze presenti nel `package.json`:

```bash
npm install
```

### Avvio completo del progetto

Il progetto prevede frontend React e backend JSON Server.

Per avviare entrambi contemporaneamente:

```bash
npm run start
```

Questo comando esegue in parallelo:

```bash
npm run server
npm run dev
```

- Il frontend React viene avviato con Vite.
- Il backend JSON Server viene avviato sulla porta `3001`.

### Avvio separato

Per avviare solo il frontend:

```bash
npm run dev
```

Per avviare solo il backend JSON Server:

```bash
npm run server
```

Il backend espone i dati del file `db.json` su:

```bash
http://localhost:3001
```

## Script disponibili

| Comando | Descrizione |
|---|---|
| `npm run dev` | Avvia il frontend React con Vite. |
| `npm run server` | Avvia JSON Server sulla porta `3001`. |
| `npm run start` | Avvia contemporaneamente backend e frontend. |
| `npm run build` | Genera la build di produzione. |
| `npm run preview` | Visualizza in locale la build prodotta. |

## Backend simulato con JSON Server

Il file `db.json` contiene i dati principali dell'applicazione e simula un backend REST locale.


## Credenziali di test

### Account ruolo Admin

```text
Email: admin@carrentalmanager.it
Password: admin123
```

### Account ruolo Utente

```text
Email: david.conocchioli@gmail.com
Password: utente123
```

## Architettura del progetto

### Struttura generale

```text
carrentalmanager/
├── public/                     # File pubblici
├── src/                        # Codice sorgente React
├── db.json                     # Database locale usato da JSON Server
├── index.html                  # Entry HTML dell'applicazione
├── package.json                # Script e dipendenze del progetto
├── package-lock.json           # Versioni bloccate delle dipendenze
├── vite.config.js              # Configurazione Vite
└── eslint.config.js            # Configurazione ESLint
```

### Struttura `src`

```text
src/
├── assets/
│   ├── icons/                  # Icone SVG usate in card, navbar, footer e UI
│   ├── images/                 # Immagini generali e immagini delle auto
│   ├── videos/                 # Video di sfondo della homepage
│   └── logo-carrentalmanager.svg
│
├── components/
│   ├── auth/
│   │   ├── LoginForm.jsx       # Form di login con validazione
│   │   └── RegisterForm.jsx    # Form di registrazione con validazione
│   │
│   ├── car/
│   │   ├── CarCard.jsx         # Card riutilizzabile per mostrare una singola auto
│   │   └── CarFilters.jsx      # Sidebar filtri del catalogo auto
│   │
│   └── common/
│       ├── Footer.jsx          # Footer comune a tutte le pagine
│       ├── Navbar.jsx          # Navbar responsive con menu desktop e mobile
│       └── ScrollToTop.jsx     # Riporta la pagina in alto al cambio rotta
│
├── pages/
│   ├── AccountPage.jsx         # Area account utente (dati + prenotazioni)
│   ├── AdminDashboardPage.jsx  # Dashboard amministrativa
│   ├── CarDetailPage.jsx       # Pagina dettaglio auto
│   ├── CatalogPage.jsx         # Catalogo con filtri e lista auto
│   ├── CheckoutPage.jsx        # Conferma e completamento prenotazione
│   ├── ContactPage.jsx         # Pagina contatti
│   ├── HomePage.jsx            # Homepage principale
│   ├── LoginPage.jsx           # Pagina accesso
│   ├── NotFoundPage.jsx        # Pagina 404
│   └── RegisterPage.jsx        # Pagina registrazione
│
├── redux/
│   ├── slices/
│   │   ├── authSlice.js        # Login, registrazione, profilo e logout
│   │   ├── bookingsSlice.js    # Prenotazioni: creazione, approvazione, rifiuto, chiusura, eliminazione e bozza
│   │   ├── carsSlice.js        # Recupero, creazione, modifica ed eliminazione auto
│   │   └── servicesSlice.js    # Recupero dei servizi extra
│   └── store.js                # Configurazione dello store Redux
│
├── App.jsx                     # Definizione delle rotte principali
├── App.css                     # Stili principali dell'applicazione
├── index.css                   # Stili globali
└── main.jsx                    # Entry point React
```

## Gestione dello stato con Redux

Il progetto utilizza Redux Toolkit per separare la logica dei dati dalla logica grafica dei componenti.

### Slice principali

**authSlice**
- Gestisce login, registrazione, aggiornamento profilo e logout.
- Mantiene l'utente loggato anche dopo il refresh tramite `localStorage`.
- Espone thunk asincroni come `loginUser`, `registerUser` e `updateProfile`.

**carsSlice**
- Gestisce la lista delle auto e l'auto selezionata nel dettaglio.
- Permette di recuperare tutte le auto o una singola auto tramite id.
- Include azioni admin per creare, modificare ed eliminare automobili.

**bookingsSlice**
- Gestisce la creazione, la visualizzazione e l'eliminazione delle prenotazioni.
- Recupera tutte le prenotazioni con `fetchBookings`; il filtro per utente o per auto viene poi applicato lato client nei componenti (es. l'area account filtra le prenotazioni dell'utente loggato, le pagine auto filtrano per disponibilità).
- Include funzioni admin per approvare, rifiutare e chiudere le prenotazioni.
- Gestisce inoltre `currentBooking`, la "bozza" di prenotazione che collega la pagina di dettaglio auto al checkout, tramite le azioni sincrone `setCurrentBooking` e `clearCurrentBooking`.

**servicesSlice**
- Recupera i servizi accessori disponibili per il noleggio.
- I servizi possono aumentare il prezzo totale della prenotazione.

## Struttura delle componenti

Il progetto è organizzato in componenti riutilizzabili

### Componenti auth

- `LoginForm.jsx`: gestisce il form di login, la validazione dei campi e l'invio dell'azione di login.
- `RegisterForm.jsx`: gestisce il form di registrazione e la validazione dei dati inseriti dall'utente.

### Componenti car

- `CarCard.jsx`: mostra le informazioni principali di una singola auto nel catalogo.
- `CarFilters.jsx`: gestisce i filtri del catalogo auto, come: marca, categoria, carburante, trasmissione e disponibilità.

### Componenti common

- `Navbar.jsx`: gestisce la navigazione principale
- `Footer.jsx`: mostra sedi, recapiti, link principali e social.
- `ScrollToTop.jsx`: riporta automaticamente la pagina in alto quando cambia la rotta.


## Descrizione Pagine principali

### HomePage.jsx

La homepage è la pagina principale dell'applicazione web. Contiene una hero con video di sfondo, titolo, sottotitolo e form di ricerca. Il form permette all'utente di prefiltrare le auto in base a marca, modello, categoria e date di noleggio.

La pagina mostra anche le categorie principali, una sezione con auto in evidenza e una sezione informativa che spiega il funzionamento del servizio di noleggio.

### CatalogPage.jsx

La pagina catalogo visualizza tutte le auto disponibili e permette all'utente di applicare filtri combinati. I filtri includono ricerca testuale, date di disponibilità, marca, categoria, carburante e trasmissione. I risultati sono paginati.

### CarDetailPage.jsx

La pagina dettaglio mostra tutte le informazioni relative a una singola auto, recuperata tramite il parametro dinamico `/auto/:id`. L'utente può selezionare date e punto di ritiro e procedere al checkout. Le date già occupate da altre prenotazioni vengono escluse nel calendario.

### CheckoutPage.jsx

La pagina checkout guida l'utente in un wizard a quattro steps: accesso/registrazione, dati di fatturazione e patente, scelta dei servizi accessori e pagamento.

### AccountPage.jsx

L'area account permette all'utente di consultare e modificare i propri dati di profilo (tab "I miei dati") e di visualizzare lo storico delle prenotazioni con filtro per stato (tab "Le mie prenotazioni"). Per gli utenti admin compare anche la tab di accesso al pannello di gestione del noleggio.

### AdminDashboardPage.jsx

La dashboard admin permette la gestione amministrativa delle auto e delle prenotazioni. L'admin può creare, modificare ed eliminare auto tramite form e gestire gli stati delle prenotazioni con approvazioni, rifiuti e chiusure.

## Logica di disponibilità delle auto

Il sistema controlla la disponibilità delle auto confrontando le date selezionate dall'utente con le prenotazioni già presenti in `db.json`.

Una prenotazione blocca la disponibilità dell'auto quando il suo stato è considerato attivo ad esempio:

```text
IN_ATTESA
IN_LAVORAZIONE
APPROVATO
CHIUSO
```

Un'auto risulta occupata se l'intervallo delle date selezionate dall'utente si sovrappone a una prenotazione già registrata.


## Validazione dei form

Il progetto utilizza la validazione nei form principali di tutti i campi con il relativo formato e gli errori vengono mostrati sotto il relativo campo.

## Ruoli utente

Il sistema distingue principalmente due tipologie di utente:

- **user** - Può consultare il catalogo, registrarsi, accedere, prenotare auto e vedere le proprie prenotazioni.
- **admin** - Può accedere alla dashboard amministrativa e gestire auto e prenotazioni.

## Limitazioni e sviluppi futuri

### Limitazioni

**Backend simulato** - Il progetto utilizza JSON Server e `db.json`, quindi non dispone di un vero backend con database relazionale.

**Password non cifrate** - Nel backend simulato le password sono salvate in chiaro, soluzione accettabile solo per sviluppo o demo locale.

**Autenticazione semplificata** - Il login si basa su chiamate a JSON Server e salvataggio dell'utente nel `localStorage`, senza token JWT o sessioni sicure.

**Assenza di pagamenti reali** - Il checkout salva la prenotazione, ma non integra un gateway di pagamento reale.

**Persistenza locale** - I dati sono salvati in `db.json`; la soluzione non è adatta a scenari reali multiutente o ad alto traffico.

### Sviluppi futuri

**Backend reale** - Sostituire JSON Server con un backend sviluppato con Node.js, Spring Boot o altra tecnologia server-side.

**Database relazionale** - Integrare MySQL, PostgreSQL o MongoDB per gestire utenti, auto, servizi e prenotazioni in modo scalabile.

**Autenticazione sicura** - Implementare JWT, password cifrate, refresh token e protezione delle rotte private.

**Pagamento online** - Integrare Stripe, PayPal o altro gateway per completare il pagamento reale della prenotazione.

**Notifiche email** - Inviare conferme di prenotazione, aggiornamenti di stato e promemoria all'utente.

**Upload immagini admin** - Consentire all'amministratore di caricare immagini auto direttamente dalla dashboard attraverso una gallery.

**Miglioramento sicurezza admin** - Proteggere in modo più robusto le pagine amministrative e separare i permessi per ruolo.

## Creatore

Progetto sviluppato da **David Conocchioli** per l'esame finale del corso Front-End Programming - Epicode.

