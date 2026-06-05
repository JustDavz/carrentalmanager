/*
  Questo è lo SLICE REDUX dell'autenticazione (auth)

  Gestisce tutto ciò che riguarda l'utente come la login, registrazione, aggiornamento del profilo
  e logout, oltre allo stato relativo (utente corrente, caricamento e errori)

  Concetti principali usati:
  - createSlice: crea subito lo stato iniziale, i reducer e le action;
  - createAsyncThunk: crea azioni asincrone (per le chiamate al backend) che generano
    automaticamente i tre stati pending / fulfilled / rejected;
  - persistenza del login nel localStorage così l'utente resta loggato dopo un refresh.

  L'autenticazione qui è simulata (la password viene confrontata lato client) perché il backend è JSON Server e non un vero sistema di login.
*/

// import dei tool di Redux Toolkit per creare slice e thunk asincroni
import { createSlice, createAsyncThunk } from '@reduxjs/toolkit'

// URL del backend JSON Server
const BASE_URL = 'http://localhost:3001'


// thunk asincrono per il login dell'utente
export const loginUser = createAsyncThunk(
  'auth/loginUser',
  async ({ email, password }) => {
    // viene ricercato l'utente per email
    const response = await fetch(`${BASE_URL}/users?email=${encodeURIComponent(email)}`)
    if (!response.ok) throw new Error('Errore di rete')

    // converte la risposta in JSON
    const users = await response.json()
    // confronto della password lato client prendendo il primo utente con quella email registrata
    const found = users.find((u) => u.password === password)
    if (!found) throw new Error('Email o password errati')

    // salva l'utente nel localStorage per mantenere il login anche dopo il refresh
    localStorage.setItem('user', JSON.stringify(found))
    // il valore restituito diventa action.payload nello stato "fulfilled"
    return found
  }
)


// thunk asincrono per la registrazione di un nuovo utente
export const registerUser = createAsyncThunk(
  'auth/registerUser',
  async (userData) => {
    // prima controllo se esiste già un utente con la stessa email
    const checkResponse = await fetch(`${BASE_URL}/users?email=${userData.email}`)
    const existing = await checkResponse.json()
    if (existing.length > 0) throw new Error('Email già registrata')

    // recupero TUTTI gli utenti per calcolare il prossimo id progressivo
    const allResponse = await fetch(`${BASE_URL}/users`)
    const allUsers = await allResponse.json()

    // trovo l'id numerico più alto e aggiungo 1 (gli id non numerici vengono ignorati)
    const maxId = allUsers.reduce((max, u) => {
      const n = Number(u.id)
      return Number.isInteger(n) && n > max ? n : max
    }, 0)
    const newId = String(maxId + 1)

    // creo il nuovo utente con id progressivo e ruolo di default "user"
    const response = await fetch(`${BASE_URL}/users`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ ...userData, id: newId, role: 'user' }),
    })
    if (!response.ok) throw new Error('Errore nella registrazione')

    // salvo il nuovo utente in localStorage e lo restituisco
    const newUser = await response.json()
    localStorage.setItem('user', JSON.stringify(newUser))
    return newUser
  }
)


// thunk asincrono per aggiornare i dati del profilo utente come ad es. dati di fatturazione
export const updateProfile = createAsyncThunk(
  'auth/updateProfile',
  async ({ id, userData }, { rejectWithValue }) => {
    try {
      // Aggiorna solo i campi inviati, lasciando invariati gli altri dati dell'utente
      const response = await fetch(`${BASE_URL}/users/${id}`, {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(userData),
      })

      if (!response.ok) {
        throw new Error('Errore nell\'aggiornamento del profilo')
      }

      const data = await response.json()
      return data
    } catch (error) {
      // rejectWithValue restituisce un messaggio d'errore come action.payload
      return rejectWithValue(error.message)
    }
  }
)

/* recupero dell'eventuale utente già salvato dal localStorage all'avvio dell'applicazione che facendo 
   refresh della pagina l'utente risulta ancora loggato */
const userFromStorage = localStorage.getItem('user')
const initialUser = userFromStorage ? JSON.parse(userFromStorage) : null


// definizione dello slice auth
const authSlice = createSlice({
  name: 'auth',
  initialState: {
    user: initialUser, // utente corrente (ripreso dal localStorage)
    loading: false,    // true durante una chiamata asincrona
    error: null,       // messaggio di errore da mostrare
  },
  reducers: {
    // logout sincrono: svuota lo state e il localStorage
    logout: (state) => {
      state.user = null
      localStorage.removeItem('user')
    },
  },
  // extraReducers gestiscono i 3 stati dei thunk asincroni pending, fulfilled e rejected
  extraReducers: (builder) => {
    builder
      // LOGIN: pending = caricamento in corso, fulfilled = utente salvato, rejected = errore
      .addCase(loginUser.pending, (state) => {
        state.loading = true
        state.error = null
      })
      .addCase(loginUser.fulfilled, (state, action) => {
        state.loading = false
        state.user = action.payload
      })
      .addCase(loginUser.rejected, (state, action) => {
        state.loading = false
        state.error = action.error.message
      })
      // REGISTER: stessa logica del login
      .addCase(registerUser.pending, (state) => {
        state.loading = true
        state.error = null
      })
      .addCase(registerUser.fulfilled, (state, action) => {
        state.loading = false
        state.user = action.payload
      })
      .addCase(registerUser.rejected, (state, action) => {
        state.loading = false
        state.error = action.error.message
      })
      // UPDATE PROFILE: al successo si aggiorna l'utente sia nello state sia nel localStorage
      .addCase(updateProfile.fulfilled, (state, action) => {
        state.loading = false
        state.user = action.payload
        localStorage.setItem('user', JSON.stringify(action.payload))
      })
  },
})


// esportazione dell'action logout sincrona e il reducer usato dallo store
export const { logout } = authSlice.actions
export default authSlice.reducer