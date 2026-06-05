/*
  Questo componente gestisce il FORM DI LOGIN dell'applicazione

  Le funzionalità principali:
  - useState per memorizzare i valori inseriti nei campi email e password e per gestire gli errori di validazione del form;
  - useSelector per leggere dallo store Redux lo stato dell'utente e gli eventuali errori;
  - useDispatch per inviare l'azione di login a Redux;
  - useNavigate per reindirizzare l'utente dopo il login all'area riservata;
  - useEffect per reindirizzare l'utente dopo che il login è andato a buon fine;
  - validazione dei campi email e password.
*/

import { useState, useEffect } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { useNavigate } from 'react-router-dom'
import { Alert } from 'react-bootstrap'
import { loginUser } from '../../redux/slices/authSlice'


/*
  COMPONENTE LoginForm
  Props ricevute:
  - skipRedirect: quando è attiva, il form non reindirizza l'utente dopo il login. Serve nel checkout, dove si vuole che l'utente resti sullo step corrente
    invece di essere portato in un'altra pagina.
*/
function LoginForm({ skipRedirect }) {

  /*
    Stato locale email definito con useState.
    email è una variabile che contiene il valore inserito dall'utente nel campo email.
    setEmail aggiorna questo valore ogni volta che l'utente digita qualcosa.
  */
  const [email, setEmail] = useState('')

  /*
    Stato locale password definito con useState.
    password è una variabile che contiene il valore scritto dall'utente nel campo password.
    setPassword aggiorna il valore della password.
  */
  const [password, setPassword] = useState('')

  /*
    Stato locale della gestione degli errori

    errors è un oggetto che contiene eventuali errori dei campi del form.
    Esempio: 'Email obbligatoria' oppure 'Password obbligatoria'

    setErrors aggiorna gli errori dopo la validazione.
  */
  const [errors, setErrors] = useState({})

  /*
    dispatch è la funzione di Redux che permette di inviare un'azione allo store di Redux.
    In questo componente viene usata per chiamare loginUser, cioè l'azione asincrona che prova ad autenticare l'utente.
  */
  const dispatch = useDispatch()

  /*
    navigate permette reindirizzare l'utente dopo l'accesso verso un'altra rotta

    Dopo il login viene usato per mandare a seconda della tipologia di account:
    - admin alla pagina /admin; (dashboard amministrazione per gestire i noleggi e il parco auto)
    - utente alla pagina /account. (area riservata per visualizzare i noleggi e modificare i dati)
  */
  const navigate = useNavigate()

  /*
    useSelector legge lo stato auth dallo store Redux

    Da state.auth si estrae:
    - user: contiene i dati dell'utente se il login è avvenuto con successo;
    - error: contiene un eventuale errore restituito dal login.
  */
  const { user, error } = useSelector((state) => state.auth)

  /*
    useEffect viene eseguito quando cambiano user, navigate o skipRedirect.

    Il reindirizzamento si trova qui dentro e non nella funzione di invio, perché
    user non è disponibile subito dopo il click: si popola solo quando la
    chiamata di login è andata a buon fine. L'effetto reagisce a quel cambiamento.

    Se user esiste significa che il login è andato a buon fine.
    Se skipRedirect è false o non presente: l'utente è admin viene portato nella dashboard admin altrimenti viene portato nell'area account.
    Se invece skipRedirect è attiva (come nel checkout) il reindirizzamento non avviene.
  */
  useEffect(() => {
    if (user && !skipRedirect) {
      navigate(user.role === 'admin' ? '/admin' : '/account')
    }
  }, [user, navigate, skipRedirect])

  // handleSubmit gestisce l'invio del form. Questa funzione viene eseguita quando l'utente clicca sul pulsante "Accedi" oppure preme invio nel form.
  const handleSubmit = (e) => {

    /*
      preventDefault blocca il comportamento standard del form HTML.
      Senza questa istruzione, il browser ricaricherebbe la pagina quando il form viene inviato.
    */
    e.preventDefault()

    // Si crea un oggetto vuoto dove si inserisco gli eventuali errori trovati durante la validazione del form
    const newErrors = {}

    /*
      Controllo del campo email

      Se l'email è vuota, viene salvato un errore.
      Altrimenti viene controllato il formato. È un controllo di base della forma (es. prova@prova.it) e serve a intercettare gli errori più evidenti, 
      ma la verifica vera delle credenziali avviene lato server (JSON Server tramite db.json).
    */
    if (!email) {
      newErrors.email = 'Email obbligatoria'
    } else if (!/\S+@\S+\.\S+/.test(email)) {
      newErrors.email = 'Email non valida'
    }

    // Controllo del campo password. Se la password è vuota, viene salvato un errore
    if (!password) {
      newErrors.password = 'Password obbligatoria'
    }

    // Aggiornamento dello stato errors. In questo modo React mostra a schermo gli eventuali messaggi di errore sotto i campi del form
    setErrors(newErrors)

    /*
      Object.keys(newErrors).length controlla quanti errori ci sono.
      Se la lunghezza è 0, significa che non ci sono errori e quindi si possono inviare i dati a Redux.
    */
    if (Object.keys(newErrors).length === 0) {

      /*
        Dispatch del thunk loginUser
        Si invia email e password al sistema di autenticazione. Sarà Redux a gestire il risultato del login.
      */
      dispatch(loginUser({ email, password }))
    }
  }

  /*
    Il return contiene il JSX del componente
    JSX permette di scrivere una struttura simile all'HTML direttamente dentro JavaScript.
  */
  return (

    // Form di Login con i vari campi e validazioni 
    <form onSubmit={handleSubmit}>
      {error && <Alert variant="danger">{error}</Alert>}

      <div className="mb-3">
        {/* Campo Email */}
        <label className="form-label">Email</label>

        <input
          type="email"
          className="form-control"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />

        {errors.email && <Alert variant="danger" className="mt-2 py-2">{errors.email}</Alert>}
      </div>

      {/* Campo inserimento Password */}
      <div className="mb-3">
        <label className="form-label">Password</label>
        <input
          type="password"
          className="form-control"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />

        {errors.password && <Alert variant="danger" className="mt-2 py-2">{errors.password}</Alert>}
      </div>

      <button type="submit" className="btn btn-primary w-100">Accedi</button>

      {/* Blocco con le credenziali di test */}
      <div className="login-demo-credenziali mt-4 p-3 rounded">

        <p className="mb-2 fw-semibold text-center">Credenziali di test</p>

        <p className="login-demo-ruolo">Admin</p>
        <p className="mb-1"><strong>Email:</strong> admin@carrentalmanager.it</p>
        <p className="mb-2"><strong>Password:</strong> admin123</p>

        <p className="login-demo-ruolo">Utente</p>
        <p className="mb-1"><strong>Email:</strong> david.conocchioli@gmail.com</p>
        <p className="mb-0"><strong>Password:</strong> utente123</p>
      </div>
    </form>
  )
}

// Esportazione del componente LoginForm. In questo modo LoginForm può essere importato e utilizzato in altre parti dell'applicazione.
export default LoginForm