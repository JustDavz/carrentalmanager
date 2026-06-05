/*
  Questo è lo STORE Redux, il contenitore unico di tutto lo stato globale dell'applicazione.

  Mette insieme i reducer dei quattro slice (auth, cars, bookings, services): ognuno gestisce
  una "sezione" dello stato e configureStore li combina in un solo store.

  Concetti principali usati:
  - configureStore: la funzione di Redux Toolkit che crea lo store già configurato
    (include di default strumenti utili come i Redux DevTools e i controlli in sviluppo);
  - la chiave usata qui per ogni reducer è il nome con cui si legge quella sezione nei componenti:
    ad esempio "auth" si legge con useSelector((state) => state.auth).

  Lo store viene poi reso disponibile a tutta l'app dal <Provider store={store}> in main.jsx.
*/

import { configureStore } from '@reduxjs/toolkit'

// import dei reducer di ogni slice
import authReducer from './slices/authSlice'
import carsReducer from './slices/carsSlice'
import bookingsReducer from './slices/bookingsSlice'
import servicesReducer from './slices/servicesSlice'


// configurazione dello store: ogni chiave è una sezione dello stato, gestita dal suo reducer
export const store = configureStore({
  reducer: {
    auth: authReducer,         // state.auth     → utente, login, errori
    cars: carsReducer,         // state.cars     → elenco auto e auto selezionata
    bookings: bookingsReducer, // state.bookings → prenotazioni e bozza corrente
    services: servicesReducer, // state.services → servizi accessori
  },
})