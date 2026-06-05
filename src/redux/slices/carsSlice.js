/*
  Questo è lo SLICE REDUX delle auto

  Gestisce l'elenco completo delle auto, l'auto selezionata nella pagina di dettaglio,
  e il CRUD completo (creazione, modifica, eliminazione) usato dalla dashboard admin.

  Concetti principali usati:
  - createAsyncThunk per le chiamate al backend (con i tre stati pending/fulfilled/rejected);
  - un reducer sincrono (resetSelectedCar) usato come cleanup quando si lascia la pagina dettaglio;
  - i diversi metodi HTTP: POST per creare, PUT per modificare, DELETE per eliminare.
*/

// import dei tool di Redux Toolkit per creare slice e thunk asincroni
import { createSlice, createAsyncThunk } from '@reduxjs/toolkit'

// URL del backend JSON Server
const BASE_URL = 'http://localhost:3001'


// thunk asincrono per recuperare tutte le auto dal backend
export const fetchCars = createAsyncThunk(
  'cars/fetchCars',
  async () => {
    const response = await fetch(`${BASE_URL}/cars`)
    if (!response.ok) throw new Error('Errore caricamento auto')
    return response.json()
  }
)


// thunk asincrono per recuperare una singola auto tramite id (usato nella pagina dettaglio)
export const fetchCarById = createAsyncThunk(
  'cars/fetchCarById',
  async (id) => {
    // l'id viene inserito direttamente nell'URL per chiedere quella specifica auto
    const response = await fetch(`${BASE_URL}/cars/${id}`)
    if (!response.ok) throw new Error('Auto non trovata')
    return response.json()
  }
)


// thunk asincrono per creare una nuova auto (admin)
export const createCar = createAsyncThunk(
  'cars/createCar',
  async (car, { getState }) => {
    // Prende le auto già presenti nello store per calcolare il prossimo id
    const { items } = getState().cars

    // Trova l'id numerico più alto e aggiungo 1 così l'id è progressivo
    const maxId = items.reduce((max, c) => {
      const n = Number(c.id)
      return Number.isInteger(n) && n > max ? n : max
    }, 0)
    const newId = String(maxId + 1)

    // POST con l'id deciso così il JSON Server non ne genera uno casuale
    const response = await fetch(`${BASE_URL}/cars`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ ...car, id: newId }),
    })
    if (!response.ok) throw new Error('Errore creazione auto')
    return response.json()
  }
)


// thunk asincrono per modificare un'auto esistente (admin)
export const updateCar = createAsyncThunk(
  'cars/updateCar',
  async ({ id, car }) => {
    // PUT sostituisce l'intera auto con i dati inviati (il form admin invia tutti i campi)
    const response = await fetch(`${BASE_URL}/cars/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(car),
    })
    if (!response.ok) throw new Error('Errore modifica auto')
    return response.json()
  }
)


// thunk asincrono per eliminare un'auto (admin)
export const deleteCar = createAsyncThunk(
  'cars/deleteCar',
  async (id) => {
    const response = await fetch(`${BASE_URL}/cars/${id}`, {
      method: 'DELETE',
    })
    if (!response.ok) throw new Error('Errore eliminazione auto')
    // restituisco l'id così il reducer sa quale auto rimuovere dalla lista
    return id
  }
)


// definizione dello slice cars
const carsSlice = createSlice({
  name: 'cars',
  initialState: {
    items: [],          // lista di tutte le auto
    selectedCar: null,  // auto attualmente visualizzata nella pagina dettaglio
    loading: false,
    error: null,
  },
  reducers: {
    // resetta l'auto selezionata (usato come cleanup nel CarDetailPage)
    resetSelectedCar: (state) => {
      state.selectedCar = null
    },
  },
  // gestione dei 3 stati dei thunk asincroni
  extraReducers: (builder) => {
    builder
      // FETCH ALL: pending = caricamento, fulfilled = salvo l'elenco, rejected = errore
      .addCase(fetchCars.pending, (state) => {
        state.loading = true
        state.error = null
      })
      .addCase(fetchCars.fulfilled, (state, action) => {
        state.loading = false
        state.items = action.payload
      })
      .addCase(fetchCars.rejected, (state, action) => {
        state.loading = false
        state.error = action.error.message
      })
      // FETCH BY ID
      .addCase(fetchCarById.pending, (state) => {
        state.loading = true
        state.error = null
        // resetta l'auto selezionata per evitare che venga mostrata quella vecchia mentre viene caricata quella nuova
        state.selectedCar = null
      })
      .addCase(fetchCarById.fulfilled, (state, action) => {
        state.loading = false
        state.selectedCar = action.payload
      })
      .addCase(fetchCarById.rejected, (state, action) => {
        state.loading = false
        state.error = action.error.message
      })
      // CREATE: aggiunge la nuova auto in coda alla lista (push)
      .addCase(createCar.fulfilled, (state, action) => {
        state.items.push(action.payload)
      })
      // UPDATE: trova l'auto nella lista tramite id e la sostituisce con quella aggiornata
      .addCase(updateCar.fulfilled, (state, action) => {
        const index = state.items.findIndex((c) => c.id === action.payload.id)
        if (index !== -1) state.items[index] = action.payload
      })
      // DELETE: vengono tenute tutte le auto tranne quella eliminata (filter)
      .addCase(deleteCar.fulfilled, (state, action) => {
        state.items = state.items.filter((c) => c.id !== action.payload)
      })
  },
})


// esportozione dell'action sincrona (resetSelectedCar) e il reducer (usato dallo store)
export const { resetSelectedCar } = carsSlice.actions
export default carsSlice.reducer