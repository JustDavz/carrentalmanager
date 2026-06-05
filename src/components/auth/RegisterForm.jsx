/*
  Questo componente gestisce il FORM DI REGISTRAZIONE dell'applicazione.

  Funzionalità principali:
  - usa useState per memorizzare i valori dei campi (nome, cognome, email, telefono, password e conferma password) e per gestire gli errori di validazione del form;
  - usa useSelector per leggere dallo store Redux lo stato dell'utente e gli eventuali errori;
  - usa useDispatch per inviare l'azione di registrazione a Redux;
  - usa useNavigate per reindirizzare l'utente dopo la registrazione all'area riservata;
  - usa useEffect per reindirizzare l'utente quando la registrazione è andata a buon fine;
  - valida tutti i campi (compreso il controllo che le due password coincidano) prima di inviare i dati.
*/

import { useState, useEffect } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { useNavigate } from 'react-router-dom'
import { Alert } from 'react-bootstrap'

import { registerUser } from '../../redux/slices/authSlice'


/*
  COMPONENTE RegisterForm

  Props ricevute:
  - skipRedirect: quando è attiva, il form non reindirizza l'utente dopo la registrazione.
    Serve nel checkout, dove si vuole che l'utente resti sullo step corrente invece di essere reindirizzato in un'altra pagina.
*/
function RegisterForm({ skipRedirect }) {

  /*
    Stati locali dei campi del form.
    Ogni campo ha il proprio stato controllato: il valore mostrato nell'input è sempre quello salvato e viene aggiornato ogni volta che si digita qualcosa.
    confirmPassword serve solo a far ridigitare la password per verificare la password inserita.
  */
  const [firstName, setFirstName] = useState('')
  const [lastName, setLastName] = useState('')
  const [email, setEmail] = useState('')
  const [phone, setPhone] = useState('')
  const [password, setPassword] = useState('')
  const [confirmPassword, setConfirmPassword] = useState('')

  /*
    Stato locale degli errori.
    errors è un oggetto che contiene eventuali errori dei campi del form.
    setErrors aggiorna gli errori dopo la validazione.
  */
  const [errors, setErrors] = useState({})

  /*
    dispatch è la funzione di Redux che permette di inviare un'azione allo store
    In questo componente viene usata per chiamare registerUser cioè l'azione asincrona che crea il nuovo utente.
  */
  const dispatch = useDispatch()

  /*
    navigate permette di spostare l'utente verso un'altra rotta
    Dopo la registrazione viene usato per reindirizzare l'utente nell'area riservata in account.
  */
  const navigate = useNavigate()

  /*
    useSelector legge lo stato auth dallo store Redux.
    Da state.auth si estrae:
    - user: contiene i dati dell'utente se la registrazione è avvenuta con successo;
    - error: contiene un eventuale errore restituito dalla registrazione (esempio: l'email è già registrata).
  */
  const { user, error } = useSelector((state) => state.auth)

  /*
    useEffect viene eseguito quando cambiano user, navigate o skipRedirect
    Il reindirizzamento si trova qui dentro e non nella funzione di invio, perché
    user non è disponibile subito dopo il click: si popola solo quando la registrazione è andata a buon fine.
    Se user esiste, la registrazione è riuscita e l'utente viene reindirizzato all'area riservata account.
    Se invece skipRedirect è attiva come nel checkout il reindirizzamento non avviene.
  */
  useEffect(() => {
    if (user && !skipRedirect) {
      navigate('/account')
    }
  }, [user, navigate, skipRedirect])

  /*
    handleSubmit gestisce l'invio del form.
    Viene eseguita quando l'utente clicca su "Registrati" oppure preme il tasto invio dentro il form.
  */
  const handleSubmit = (e) => {

    /*
      preventDefault blocca il comportamento standard del form HTML e senza questa istruzione, il browser ricaricherebbe la pagina
      quando il form viene inviato.
    */
    e.preventDefault()

    // Viene creato un oggetto vuoto dove si inseriscono gli eventuali errori trovati durante la validazione del form
    const newErrors = {}

    // Controllo nome e cognome che devono essere presenti e obbligatori
    if (!firstName) {
      newErrors.firstName = 'Nome obbligatorio'
    }
    if (!lastName) {
      newErrors.lastName = 'Cognome obbligatorio'
    }

    // Controllo del campo email. Prima verifica che sia presente, poi che abbia un formato valido (es. prova@prova.it).
    if (!email) {
      newErrors.email = 'Email obbligatoria'
    } else if (!/\S+@\S+\.\S+/.test(email)) {
      newErrors.email = 'Email non valida'
    }

    /*
      Controllo del campo telefono.
      Oltre a verificare che il numero di telefono è inserito, viene controllato il formato e verifica ache l'espressione /^3\d{9}$/ che impone il formato
      del numero di telefono cellulare italiano: che inizia con 3 e deve essere composto da 10 cifre.
    */
    if (!phone) {
      newErrors.phone = 'Telefono obbligatorio'
    } else if (!/^3\d{9}$/.test(phone)) {
      newErrors.phone = 'Numero non valido (10 cifre, inizia per 3)'
    }

    // Controllo del campo password che deve essere lunga almeno 6 caratteri
    if (!password) {
      newErrors.password = 'Password obbligatoria'
    } else if (password.length < 6) {
      newErrors.password = 'La password deve avere almeno 6 caratteri'
    }

    // Controllo della conferma password. Deve essere presente e deve coincidere con la password digitata nel campo passsord
    if (!confirmPassword) {
      newErrors.confirmPassword = 'Conferma la password'
    } else if (password !== confirmPassword) {
      newErrors.confirmPassword = 'Le password non coincidono'
    }

    // Si aggiorna lo stato errors. In questo modo React mostra a schermo gli eventuali messaggi di errore sotto i campi del form
    setErrors(newErrors)

    // Se non ci sono errori, vengono invaita i dati a Redux per creare l'utente
    if (Object.keys(newErrors).length === 0) {
      dispatch(registerUser({ firstName, lastName, email, phone, password }))
    }
  }

  // Il return contiene il JSX del componente che permette di scrivere una struttura simile all'HTML direttamente dentro JavaScript
  return (

    /*
      Form di registrazione. onSubmit collega il form alla funzione handleSubmit.
      Quando il form viene inviato, viene eseguita la validazione e se i dati sono corretti parte la registrazione.
    */
    <form onSubmit={handleSubmit}>

      {/* Se esiste un errore globale proveniente da Redux, viene mostrato un Alert rosso (ad esempio: email già registrata). */}
      {error && <Alert variant="danger">{error}</Alert>}

      {/* Campi Nome e Cognome */}
      <div className="row">
  
        <div className="col-md-6 mb-3">
          <label className="form-label">Nome</label>
          <input
            type="text"
            className="form-control"
            value={firstName}
            onChange={(e) => setFirstName(e.target.value)}
          />
          {errors.firstName && <Alert variant="danger" className="mt-2 py-2">{errors.firstName}</Alert>}
        </div>

        <div className="col-md-6 mb-3">
          <label className="form-label">Cognome</label>
          <input
            type="text"
            className="form-control"
            value={lastName}
            onChange={(e) => setLastName(e.target.value)}
          />
          {errors.lastName && <Alert variant="danger" className="mt-2 py-2">{errors.lastName}</Alert>}
        </div>
      </div>

      {/* Campo Email */}
      <div className="mb-3">
        <label className="form-label">Email</label>
        <input
          type="email"
          className="form-control"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />
        {errors.email && <Alert variant="danger" className="mt-2 py-2">{errors.email}</Alert>}
      </div>

      {/* Campo Telefono */}
      <div className="mb-3">
        <label className="form-label">Telefono</label>
        <input
          type="tel"
          className="form-control"
          value={phone}
          onChange={(e) => setPhone(e.target.value)}
        />
        {errors.phone && <Alert variant="danger" className="mt-2 py-2">{errors.phone}</Alert>}
      </div>

      {/* Riga con i campi Password e conferma password */}
      <div className="row">

        {/* Campo password */}
        <div className="col-md-6 mb-3">
          <label className="form-label">Password</label>
          <input
            type="password"
            className="form-control"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
          {errors.password && <Alert variant="danger" className="mt-2 py-2">{errors.password}</Alert>}
        </div>

        {/* Campo conferma password */}
        <div className="col-md-6 mb-3">
          <label className="form-label">Conferma password</label>
          <input
            type="password"
            className="form-control"
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
          />
          {errors.confirmPassword && <Alert variant="danger" className="mt-2 py-2">{errors.confirmPassword}</Alert>}
        </div>
      </div>

      {/* Pulsante di invio del form */}
      <button type="submit" className="btn btn-primary w-100">Registrati</button>
    </form>
  )
}

// Esportazione del componente. In questo modo RegisterForm può essere importato e utilizzato in altre parti dell'applicazione.
export default RegisterForm