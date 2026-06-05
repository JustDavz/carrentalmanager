/*
  Questo è lo SLICE REDUX delle prenotazioni 

  Gestisce sia le prenotazioni salvate sul backend (caricamento, creazione, cambio di stato,
  eliminazione) sia la "bozza" di prenotazione in corso che fa da ponte tra la pagina di
  dettaglio auto e il checkout.

  Concetti principali usati:
  - createAsyncThunk per le chiamate al backend con i tre stati pending/fulfilled/rejected;
  - reducer sincroni (setCurrentBooking / clearCurrentBooking) per gestire la bozza;
  - "Stati del noleggio" delle prenotazioni: IN_ATTESA / IN_LAVORAZIONE, APPROVATO o RIFIUTATO, CHIUSO.

*/

// import dei tool di Redux Toolkit per creare slice e thunk asincroni
import { createSlice, createAsyncThunk } from '@reduxjs/toolkit'

// URL del backend JSON Server
const BASE_URL = 'http://localhost:3001'


// thunk asincrono per recuperare tutte le prenotazioni (vista dell'admin)
export const fetchBookings = createAsyncThunk(
  'bookings/fetchBookings',
  async () => {
    const response = await fetch(`${BASE_URL}/bookings`)
    if (!response.ok) throw new Error('Errore caricamento prenotazioni')
    return response.json()
  }
)


// thunk asincrono per creare una nuova prenotazione al checkout
export const createBooking = createAsyncThunk(
  'bookings/createBooking',
  async (booking) => {
    // POST crea la prenotazione; vengono aggiunti automaticamente due campi:
    // rejectionReason vuoto e createdAt timestamp di creazione utile per ordinarle
    const response = await fetch(`${BASE_URL}/bookings`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        ...booking,
        rejectionReason: null,
        createdAt: new Date().toISOString(),
      }),
    })
    if (!response.ok) throw new Error('Errore creazione prenotazione')
    return response.json()
  }
)


// thunk asincrono per l'approvazione di una prenotazione (admin)
export const approveBooking = createAsyncThunk(
  'bookings/approveBooking',
  async (id) => {
    // PATCH: cambio solo lo stato in APPROVATO e azzero un eventuale motivo di rifiuto
    const response = await fetch(`${BASE_URL}/bookings/${id}`, {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ status: 'APPROVATO', rejectionReason: null }),
    })
    if (!response.ok) throw new Error('Errore approvazione prenotazione')
    return response.json()
  }
)


// thunk asincrono per il rifiuto di una prenotazione (admin) con motivazione
export const rejectBooking = createAsyncThunk(
  'bookings/rejectBooking',
  async ({ id, reason }) => {
    // stato RIFIUTATO + salvataggio della motivazione del rifiuto
    const response = await fetch(`${BASE_URL}/bookings/${id}`, {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ status: 'RIFIUTATO', rejectionReason: reason }),
    })
    if (!response.ok) throw new Error('Errore rifiuto prenotazione')
    return response.json()
  }
)


// thunk asincrono per la chiusura di una prenotazione completata (admin)
export const closeBooking = createAsyncThunk(
  'bookings/closeBooking',
  async (id) => {
    // PATCH: porto lo stato a CHIUSO (noleggio concluso)
    const response = await fetch(`${BASE_URL}/bookings/${id}`, {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ status: 'CHIUSO' }),
    })
    if (!response.ok) throw new Error('Errore chiusura prenotazione')
    return response.json()
  }
)


// thunk asincrono per eliminare una prenotazione (annullamento da parte dell'utente)
export const deleteBooking = createAsyncThunk(
  'bookings/deleteBooking',
  async (id) => {
    const response = await fetch(`${BASE_URL}/bookings/${id}`, {
      method: 'DELETE',
    })
    if (!response.ok) throw new Error('Errore eliminazione prenotazione')
    // restituisce l'id della prenotazione al reducer
    return id
  }
)


// definizione dello slice bookings
const bookingsSlice = createSlice({
  name: 'bookings',
  initialState: {
    items: [],              // lista delle prenotazioni caricate (admin o utente)
    currentBooking: null,   // prenotazione in corso di compilazione (dal dettaglio al checkout)
    loading: false,
    error: null,
  },
  reducers: {
    // azione sincrona: salva i dati della prenotazione in corso prima del checkout
    setCurrentBooking: (state, action) => {
      state.currentBooking = action.payload
    },
    // azione sincrona: pulisce la prenotazione in corso (dopo conferma o annullamento)
    clearCurrentBooking: (state) => {
      state.currentBooking = null
    },
  },
  extraReducers: (builder) => {
    builder
      // FETCH ALL (admin): pending = caricamento, fulfilled = salvo la lista, rejected = errore
      .addCase(fetchBookings.pending, (state) => {
        state.loading = true
        state.error = null
      })
      .addCase(fetchBookings.fulfilled, (state, action) => {
        state.loading = false
        state.items = action.payload
      })
      .addCase(fetchBookings.rejected, (state, action) => {
        state.loading = false
        state.error = action.error.message
      })
      // CREATE: aggiunge la nuova prenotazione in coda e pulisce la "bozza" (currentBooking)
      .addCase(createBooking.pending, (state) => {
        state.loading = true
        state.error = null
      })
      .addCase(createBooking.fulfilled, (state, action) => {
        state.loading = false
        state.items.push(action.payload)
        state.currentBooking = null
      })
      .addCase(createBooking.rejected, (state, action) => {
        state.loading = false
        state.error = action.error.message
      })
      // APPROVE: trova la prenotazione nella lista tramite il suo id e la sostituisce con quella aggiornata
      .addCase(approveBooking.fulfilled, (state, action) => {
        const index = state.items.findIndex((b) => b.id === action.payload.id)
        if (index !== -1) state.items[index] = action.payload
      })
      // REJECT: stesso pattern di approve (sostituisce la prenotazione aggiornata)
      .addCase(rejectBooking.fulfilled, (state, action) => {
        const index = state.items.findIndex((b) => b.id === action.payload.id)
        if (index !== -1) state.items[index] = action.payload
      })
      // CLOSE: stesso pattern
      .addCase(closeBooking.fulfilled, (state, action) => {
        const index = state.items.findIndex((b) => b.id === action.payload.id)
        if (index !== -1) state.items[index] = action.payload
      })
      // DELETE: vengono tenute tutte le prenotazioni tranne quella con l'id eliminato (filter)
      .addCase(deleteBooking.fulfilled, (state, action) => {
        state.items = state.items.filter((b) => b.id !== action.payload)
      })
  },
})


// esportazione dei 2 reducer sincroni (gestione della bozza)
export const { setCurrentBooking, clearCurrentBooking } = bookingsSlice.actions
// esportazione il reducer generale (usato dallo store)
export default bookingsSlice.reducer