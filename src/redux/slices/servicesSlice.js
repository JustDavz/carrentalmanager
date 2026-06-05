/*
  Questo è lo SLICE REDUX dei servizi accessori

  Gestisce l'elenco dei servizi opzionali (es. Assicurazione Kasko, Soccorso Stradale ecc.)
  che l'utente può aggiungere alla prenotazione durante il checkout.
*/

// import dei tool di Redux Toolkit per creare slice e thunk asincroni
import { createSlice, createAsyncThunk } from '@reduxjs/toolkit'

// URL del backend JSON Server
const BASE_URL = 'http://localhost:3001'


// thunk asincrono per recuperare la lista dei servizi accessori (Kasko, Soccorso ecc.)
export const fetchServices = createAsyncThunk(
  'services/fetchServices',
  async () => {
    const response = await fetch(`${BASE_URL}/services`)
    if (!response.ok) throw new Error('Errore caricamento servizi')
    return response.json()
  }
)


// definizione dello slice services
const servicesSlice = createSlice({
  name: 'services',
  initialState: {
    items: [],       // lista dei servizi accessori
    loading: false,  // true durante il caricamento
    error: null,     // messaggio di errore
  },
  // reducers vuoto: i servizi sono in sola lettura non serve nessuna azione sincrona
  reducers: {},
  // gestione dei 3 stati del thunk (pending = caricamento, fulfilled = dati, rejected = errore)
  extraReducers: (builder) => {
    builder
      .addCase(fetchServices.pending, (state) => {
        state.loading = true
        state.error = null
      })
      .addCase(fetchServices.fulfilled, (state, action) => {
        state.loading = false
        state.items = action.payload
      })
      .addCase(fetchServices.rejected, (state, action) => {
        state.loading = false
        state.error = action.error.message
      })
  },
})


// esportazione del reducer (usato dallo store)
export default servicesSlice.reducer