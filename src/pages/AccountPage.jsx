/*
  Questa è la pagina dell'AREA RISERVATA "Il mio account".

  Mostra una hero di benvenuto e due schede:
  - "I miei dati": form per modificare il profilo utente;
  - "Le mie prenotazioni": elenco delle prenotazioni dell'utente e filtro per stato.
  Se l'utente è loggato con admin appare una terza scheda che porta al pannello di gestione.
*/

import { useState, useEffect } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { useNavigate, useSearchParams } from 'react-router-dom'
import { Alert } from 'react-bootstrap'

import { updateProfile } from '../redux/slices/authSlice'
import { fetchBookings } from '../redux/slices/bookingsSlice'
import { fetchCars } from '../redux/slices/carsSlice'

import accountHero from '../assets/images/lamborghini.jpg'


function AccountPage() {
  const dispatch = useDispatch()
  const navigate = useNavigate()

  // Dall'area auth dello store viene letto l'utente loggato e l'eventuale errore di salvataggio
  const { user, error } = useSelector((state) => state.auth)

  // Prenotazioni e auto: servono nella scheda "Le mie prenotazioni"
  // bookingsLoading indica se le prenotazioni sono ancora in caricamento
  const { items: bookings, loading: bookingsLoading } = useSelector((state) => state.bookings)
  const { items: cars } = useSelector((state) => state.cars)

  // Gestione della scheda attiva (tab) sincronizzata con l'URL
  const [searchParams, setSearchParams] = useSearchParams()
  const [activeTab, setActiveTab] = useState(
    searchParams.get('tab') === 'dati' ? 'dati' : 'prenotazioni'
  )

  // Cambia scheda aggiornando sia lo stato locale sia il parametro "tab" nell'URL
  const changeTab = (tab) => {
    setActiveTab(tab)
    setSearchParams({ tab })
  }

  // Stati dei campi del form "I miei dati"
  const [firstName, setFirstName] = useState('')
  const [lastName, setLastName] = useState('')
  const [email, setEmail] = useState('')
  const [phone, setPhone] = useState('')
  const [licenseNumber, setLicenseNumber] = useState('')
  const [licenseIssueDate, setLicenseIssueDate] = useState('')
  const [licenseExpiryDate, setLicenseExpiryDate] = useState('')
  const [fiscalCode, setFiscalCode] = useState('')
  const [address, setAddress] = useState('')
  const [city, setCity] = useState('')
  const [zipCode, setZipCode] = useState('')
  const [errors, setErrors] = useState({})     // errori di validazione del form
  const [success, setSuccess] = useState(false) // true dopo un salvataggio riuscito mostra l'avviso in colorazione verde

  // Filtro per stato nella scheda prenotazioni ('TUTTE' = nessun filtro)
  const [activeFilter, setActiveFilter] = useState('TUTTE')

  /*
    Protezione della pagina

    Se non c'è un utente loggato, l'utente viene reindirizzato alla login.
    L'effetto si riesegue se cambia user ad esempio dopo un logout.
  */
  useEffect(() => {
    if (!user) {
      navigate('/login')
    }
  }, [user, navigate])

  // Mantiene la scheda attiva scelta allineata all'URL
  useEffect(() => {
    const tab = searchParams.get('tab')
    if (tab === 'dati' || tab === 'prenotazioni') {
      setActiveTab(tab)
    }
  }, [searchParams])

  // Precompila i campi del form con i dati dell'utente
  useEffect(() => {
    if (user) {
      setFirstName(user.firstName || '')
      setLastName(user.lastName || '')
      setEmail(user.email || '')
      setPhone(user.phone || '')
      setLicenseNumber(user.billingInfo?.licenseNumber || '')
      setLicenseIssueDate(user.billingInfo?.licenseIssueDate || '')
      setLicenseExpiryDate(user.billingInfo?.licenseExpiryDate || '')
      setFiscalCode(user.billingInfo?.fiscalCode || '')
      setAddress(user.billingInfo?.address || '')
      setCity(user.billingInfo?.city || '')
      setZipCode(user.billingInfo?.zipCode || '')
    }
  }, [user])

  // Carica prenotazioni e auto quando l'utente è disponibile
  useEffect(() => {
    if (user) {
      dispatch(fetchBookings())
      dispatch(fetchCars())
    }
  }, [dispatch, user])

  // Se non c'è ancora un utente non renderizziamo nulla
  if (!user) return null

  /*
    handleSubmit: valida i campi e se tutto è corretto aggiorna il profilo.
    È una funzione async perché attende il completamento dell'azione updateProfile.
  */
  const handleSubmit = async (e) => {
    e.preventDefault() // evita il ricaricamento della pagina
    const newErrors = {}

    // Validazione dei campi obbligatori nome, cognome e di formato email e telefono
    if (!firstName) {
      newErrors.firstName = 'Nome obbligatorio'
    }
    if (!lastName) {
      newErrors.lastName = 'Cognome obbligatorio'
    }
    if (!email) {
      newErrors.email = 'Email obbligatoria'
    } else if (!/\S+@\S+\.\S+/.test(email)) {
      newErrors.email = 'Email non valida'
    }
    if (!phone) {
      newErrors.phone = 'Telefono obbligatorio'
    } else if (!/^3\d{9}$/.test(phone)) {
      // formato cellulare italiano: inizia con 3 ed è composto da 10 cifre
      newErrors.phone = 'Numero di telefono cellulare non valido (10 cifre e inizia per 3)'
    }

    setErrors(newErrors)
    setSuccess(false) // azzero l'eventuale avviso di successo precedente

    if (Object.keys(newErrors).length === 0) {
      try {
        // invia l'aggiornamento del profilo
        await dispatch(updateProfile({
          id: user.id,
          userData: {
            firstName,
            lastName,
            email,
            phone,
            billingInfo: {
              ...user.billingInfo,
              fiscalCode,
              address,
              city,
              zipCode,
              licenseNumber,
              licenseIssueDate,
              licenseExpiryDate,
            },
          },
        })).unwrap()
        setSuccess(true) // mostra l'avviso verde di conferma
      } catch (err) {
        // in caso di errore viene gestito da Redux e mostrato tramite "error"
      }
    }
  }

  // SCHEDA PRENOTAZIONI AUTOMOBILI

  // Prenotazioni per l'utente che ha loggato ordinate dalla più recente alla meno recente
  const myBookings = bookings
    .filter((booking) => String(booking.userId) === String(user.id))
    .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))

  // Trova l'auto collegata a una prenotazione tramite il suo id
  const getCarById = (carId) => cars.find((c) => String(c.id) === String(carId))

  // Accorcia l'id della prenotazione agli ultimi 8 caratteri alfanumerici per mostrarlo in modo compatto
  const formatBookingId = (id) => String(id).replace(/[^a-zA-Z0-9]/g, '').slice(-8)

  // Mappa ogni stato al colore al badge corrispondente
  const statusVariants = {
    'IN_ATTESA': 'warning',
    'IN_LAVORAZIONE': 'info',
    'APPROVATO': 'success',
    'RIFIUTATO': 'danger',
    'CHIUSO': 'secondary',
  }

  // Mappa ogni stato alla sua etichetta
  const statusLabels = {
    'IN_ATTESA': 'In attesa',
    'IN_LAVORAZIONE': 'In lavorazione',
    'APPROVATO': 'Approvata',
    'RIFIUTATO': 'Rifiutata',
    'CHIUSO': 'Chiusa',
  }

  // Schede del filtro per stato mostrate sopra l'elenco come tab
  const filterTabs = [
    { value: 'TUTTE', label: 'Tutte' },
    { value: 'IN_ATTESA', label: 'In attesa' },
    { value: 'IN_LAVORAZIONE', label: 'In lavorazione' },
    { value: 'APPROVATO', label: 'Approvate' },
    { value: 'RIFIUTATO', label: 'Rifiutate' },
    { value: 'CHIUSO', label: 'Chiuse' },
  ]

  // Conta le prenotazioni di un certo stato o tutte per il badge sui filtri
  const countByStatus = (status) => {
    if (status === 'TUTTE') return myBookings.length
    return myBookings.filter((b) => b.status === status).length
  }

  // Applica il filtro selezionato: se è 'TUTTE' mostra tutto altrimenti solo lo stato scelto
  const filteredBookings = activeFilter === 'TUTTE'
    ? myBookings
    : myBookings.filter((b) => b.status === activeFilter)

  // Converte una data in formato (YYYY-MM-DD) nel formato italiano (GG/MM/AAAA)
  const formatDate = (dateStr) => {
    if (!dateStr) return ''
    const [y, m, d] = dateStr.split('-')
    return `${d}/${m}/${y}`
  }

  // Calcola il numero di giorni tra ritiro e riconsegna
  const calcDays = (start, end) => {
    if (!start || !end) return 0
    const diffMs = new Date(end).getTime() - new Date(start).getTime()
    return Math.ceil(diffMs / (1000 * 60 * 60 * 24))
  }

  return (
    <div className="bookings-page">

      {/* Hero con il saluto personalizzato */}
      <section
        className="bookings-hero"
        style={{ backgroundImage: `url(${accountHero})` }}
      >
        <div className="bookings-hero-overlay">
          <div className="container">
            <p className="bookings-hero-eyebrow">Area personale</p>
            <h1 className="bookings-hero-title">
              Benvenuto, {user.firstName} {user.lastName}
            </h1>
            <p className="bookings-hero-subtitle">
              Gestisci i tuoi dati e tieni sotto controllo le tue prenotazioni.
            </p>
          </div>
        </div>
      </section>

      <div className="container py-5">

        {/* Schede principali (tab) */}
        <ul className="nav nav-tabs justify-content-center mb-4">
          <li className="nav-item">
            <button
              className={`nav-link ${activeTab === 'prenotazioni' ? 'active' : ''}`}
              onClick={() => changeTab('prenotazioni')}
            >
              Le mie prenotazioni
            </button>
          </li>
          <li className="nav-item">
            <button
              className={`nav-link ${activeTab === 'dati' ? 'active' : ''}`}
              onClick={() => changeTab('dati')}
            >
              I miei dati
            </button>
          </li>

          {/* Scheda visibile solo agli admin che reindirizza alla dashboard di gestione */}
          {user.role === 'admin' && (
            <li className="nav-item">
              <button
                className="nav-link"
                onClick={() => navigate('/admin')}
              >
                Pannello gestione
              </button>
            </li>
          )}
        </ul>

        {/* TAB "I MIEI DATI" */}
        {activeTab === 'dati' && (
          <div className="row justify-content-center">
            <div className="col-md-8 col-lg-6">

              {/* Avviso verde mostrato dopo un salvataggio riuscito */}
              {success && (
                <Alert variant="success" onClose={() => setSuccess(false)} dismissible>
                  Profilo aggiornato con successo!
                </Alert>
              )}

              {/* Errore globale proveniente da Redux ad es. fallimento del salvataggio */}
              {error && <Alert variant="danger">{error}</Alert>}

              <form onSubmit={handleSubmit}>

                {/* Nome e Cognome */}
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

                {/* Email */}
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

                {/* Telefono */}
                <div className="mb-3">
                  <label className="form-label">Telefono</label>
                  <input
                    type="tel"
                    className="form-control"
                    placeholder="es. 3331234567"
                    value={phone}
                    onChange={(e) => setPhone(e.target.value)}
                  />
                  {errors.phone && <Alert variant="danger" className="mt-2 py-2">{errors.phone}</Alert>}
                </div>

                <hr className="my-4" />
                <h6 className="text-secondary mb-3">Indirizzo e fatturazione</h6>

                {/* I campi codice fiscale, indirizzo, città, CAP che sono opzionali */}
                <div className="mb-3">
                  <label className="form-label">Codice fiscale</label>
                  <input
                    type="text"
                    className="form-control"
                    placeholder="es. RSSMRA85M01H501Z"
                    value={fiscalCode}
                    onChange={(e) => setFiscalCode(e.target.value)}
                  />
                </div>

                <div className="mb-3">
                  <label className="form-label">Indirizzo</label>
                  <input
                    type="text"
                    className="form-control"
                    placeholder="es. Via Roma 10"
                    value={address}
                    onChange={(e) => setAddress(e.target.value)}
                  />
                </div>

                {/* Città e CAP */}
                <div className="row">
                  <div className="col-md-8 mb-3">
                    <label className="form-label">Città</label>
                    <input
                      type="text"
                      className="form-control"
                      placeholder="es. Roma"
                      value={city}
                      onChange={(e) => setCity(e.target.value)}
                    />
                  </div>

                  <div className="col-md-4 mb-3">
                    <label className="form-label">CAP</label>
                    <input
                      type="text"
                      className="form-control"
                      placeholder="es. 00100"
                      value={zipCode}
                      onChange={(e) => setZipCode(e.target.value)}
                    />
                  </div>
                </div>

                <hr className="my-4" />
                <h6 className="text-secondary mb-3">Patente di guida</h6>

                {/* Dati patente: numero e date di rilascio/scadenza */}
                <div className="mb-3">
                  <label className="form-label">Numero patente</label>
                  <input
                    type="text"
                    className="form-control"
                    placeholder="es. RM1234567A"
                    value={licenseNumber}
                    onChange={(e) => setLicenseNumber(e.target.value)}
                  />
                </div>

                <div className="row">
                  <div className="col-md-6 mb-3">
                    <label className="form-label">Data di rilascio</label>
                    <input
                      type="date"
                      className="form-control"
                      value={licenseIssueDate}
                      onChange={(e) => setLicenseIssueDate(e.target.value)}
                    />
                  </div>

                  <div className="col-md-6 mb-3">
                    <label className="form-label">Data di scadenza</label>
                    <input
                      type="date"
                      className="form-control"
                      value={licenseExpiryDate}
                      onChange={(e) => setLicenseExpiryDate(e.target.value)}
                    />
                  </div>
                </div>

                {/* Riepilogo dell'account per capire il ruolo e id dell'utente */}
                <div className="card p-3 mb-3 bg-light">
                  <small className="text-secondary">
                    <strong>Ruolo:</strong> {user.role === 'admin' ? 'Amministratore' : 'Utente'}
                  </small>
                  <small className="text-secondary d-block">
                    <strong>ID:</strong> {user.id}
                  </small>
                </div>

                <button type="submit" className="btn btn-primary w-100">Salva modifiche</button>

              </form>
            </div>
          </div>
        )}

        {/* TAB "LE MIE PRENOTAZIONI" */}
        {activeTab === 'prenotazioni' && (
          <div>

            {/* Messaggio di caricamento mentre le prenotazioni arrivano dal backend */}
            {bookingsLoading && <p className="text-center">Caricamento...</p>}

            {/* Caso che l'utente non ha nessuna prenotazione */}
            {!bookingsLoading && myBookings.length === 0 && (
              <div className="bookings-empty">
                <h4>Nessuna prenotazione</h4>
                <p className="text-secondary mb-3">
                  Non hai ancora effettuato prenotazioni. Prenota ora.
                </p>
                <button className="btn btn-primary btn-lg" onClick={() => navigate('/auto')}>
                  Scopri le nostre Auto
                </button>
              </div>
            )}

            {/* Caso in cui ci sono prenotazioni e mostra filtri ed elenco */}
            {!bookingsLoading && myBookings.length > 0 && (
              <>
                {/* Filtro per stato */}
                <div className="bookings-tabs" role="tablist">
                  {filterTabs.map((tab) => {
                    const count = countByStatus(tab.value)
                    const isActive = activeFilter === tab.value
                    return (
                      <button
                        key={tab.value}
                        type="button"
                        role="tab"
                        aria-selected={isActive}
                        className={`bookings-tab ${isActive ? 'is-active' : ''}`}
                        onClick={() => setActiveFilter(tab.value)}
                      >
                        {tab.label}
                        {count > 0 && <span className="bookings-tab-count">{count}</span>}
                      </button>
                    )
                  })}
                </div>

                {/* Messaggio quando il filtro selezionato non ha prenotazioni */}
                {filteredBookings.length === 0 && (
                  <div className="bookings-empty bookings-empty--small">
                    <p className="mb-0 text-secondary">
                      Nessuna prenotazione in stato "{statusLabels[activeFilter] || activeFilter}".
                    </p>
                  </div>
                )}

                {/* Elenco delle prenotazioni filtrate */}
                <div className="bookings-list">
                  {filteredBookings.map((booking) => {
                    const car = getCarById(booking.carId)
                    const days = calcDays(booking.startDate, booking.endDate)
                    const variant = statusVariants[booking.status] || 'secondary'

                    return (
                      <article key={booking.id} className="booking-card">

                        {/* Immagine dell'auto */}
                        <div className="booking-card-image">
                          {car ? (
                            <img src={`/images/cars/${car.image}`} alt={`${car.brand} ${car.model}`} />
                          ) : (
                            <div className="booking-card-image-placeholder">
                              <span>Auto non disponibile</span>
                            </div>
                          )}
                        </div>

                        <div className="booking-card-content">

                          {/* Intestazione */}
                          <div className="booking-card-head">
                            <div>
                              <p className="booking-card-id">Prenotazione #{formatBookingId(booking.id)}</p>
                              <h3 className="booking-card-title">
                                {car ? `${car.brand} ${car.model}` : 'Auto non trovata'}
                              </h3>
                              {car?.category && (
                                <p className="booking-card-category">{car.category}</p>
                              )}
                            </div>
                            <span className={`booking-card-status booking-card-status--${variant}`}>
                              {statusLabels[booking.status] || booking.status}
                            </span>
                          </div>

                          {/* Dettagli */}
                          <div className="booking-card-grid">
                            <div className="booking-card-field">
                              <span className="booking-card-field-label">Periodo</span>
                              <span className="booking-card-field-value">
                                {formatDate(booking.startDate)} → {formatDate(booking.endDate)}
                              </span>
                              <span className="booking-card-field-meta">
                                {days} {days === 1 ? 'giorno' : 'giorni'}
                              </span>
                            </div>

                            <div className="booking-card-field">
                              <span className="booking-card-field-label">Pagamento</span>
                              <span className="booking-card-field-value text-capitalize">{booking.paymentMethod}</span>
                            </div>

                            <div className="booking-card-field">
                              <span className="booking-card-field-label">Totale</span>
                              <span className="booking-card-field-value booking-card-field-value--price">
                                €{booking.totalPrice}
                              </span>
                            </div>
                          </div>

                          {/* Motivo del rifiuto nel caso admin rifiuta il noleggio es. pagamento non ricevuto l'utente lo visualizza nella card */}
                          {booking.rejectionReason && (
                            <div className="booking-card-rejection">
                              <strong>Motivo rifiuto:</strong> {booking.rejectionReason}
                            </div>
                          )}

                          {/* Data di creazione della prenotazione */}
                          <div className="booking-card-footer">
                            <small className="text-secondary">
                              Creata il {new Date(booking.createdAt).toLocaleDateString('it-IT')}
                            </small>
                          </div>

                        </div>
                      </article>
                    )
                  })}
                </div>
              </>
            )}
          </div>
        )}

      </div>
    </div>
  )
}


export default AccountPage