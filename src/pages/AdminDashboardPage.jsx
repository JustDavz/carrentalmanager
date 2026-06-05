/*
  Questa è la pagina della DASHBOARD AMMINISTRATORE.

  È accessibile solo agli utenti con ruolo "admin" e permette di:
  - gestire le prenotazioni dei clienti (approvare, rifiutare con motivo, chiudere), presenta anche un filtro per stato;
  - gestire il parco auto con un CRUD completo come creare, modificare e eliminare auto.

  Concetti principali usati:
  - protezione della rotta in base al ruolo dell'utente;
  - "Gestione stati" delle prenotazioni (le azioni disponibili dipendono dallo stato);
  - un unico form riutilizzato sia per creare sia per modificare un'auto;
  - validazione dei dati
*/

import { useState, useEffect } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { useNavigate } from 'react-router-dom'
import { Alert } from 'react-bootstrap'

import { fetchBookings, approveBooking, rejectBooking, closeBooking } from '../redux/slices/bookingsSlice'
import { fetchCars, createCar, updateCar, deleteCar } from '../redux/slices/carsSlice'
import { fetchServices } from '../redux/slices/servicesSlice'

import dashboardHero from '../assets/images/lamborghini.jpg'


function AdminDashboardPage() {
  const dispatch = useDispatch()
  const navigate = useNavigate()

  /*
    Dati letti dallo store:
    - user: per verificare che sia un admin;
    - bookings: l'elenco delle prenotazioni con relativi stato di caricamento ed errore;
    - cars: l'elenco delle auto con caricamento ed errore;
    - services: i servizi accessori scelti dall'utente
  */
  const user = useSelector((state) => state.auth.user)
  const { items: bookings, loading: bookingsLoading, error: bookingsError } = useSelector((state) => state.bookings)
  const { items: cars, loading: carsLoading, error: carsError } = useSelector((state) => state.cars)
  const { items: services } = useSelector((state) => state.services)

  // Scheda attualmente attiva 'bookings' (prenotazioni) o 'cars' (gestione auto).
  const [activeTab, setActiveTab] = useState('bookings')

  // Filtro delle prenotazioni per stato ('' = nessun filtro, mostra tutte).
  const [statusFilter, setStatusFilter] = useState('')

  /*
    Stato per il rifiuto di una prenotazione con motivazione:
    - rejectId: id della prenotazione che si sta rifiutando;
    - rejectReason: testo del motivo;
    - rejectError: messaggio di errore se il motivo non viene compilato.
  */
  const [rejectId, setRejectId] = useState(null)
  const [rejectReason, setRejectReason] = useState('')
  const [rejectError, setRejectError] = useState('')

  /*
    Stato per il form delle auto usato sia per creare sia per modificare:
    - showCarForm: se il form è visibile;
    - editingCar: l'auto in modalità modifica;
    - carForm: i valori dei campi del form sono tutti controllati;
    - carErrors: gli errori di validazione del form.
  */
  const [showCarForm, setShowCarForm] = useState(false)
  const [editingCar, setEditingCar] = useState(null)
  const [carForm, setCarForm] = useState({
    brand: '',
    model: '',
    year: '',
    category: '',
    transmission: '',
    fuel: '',
    seats: '',
    pricePerDay: '',
    licensePlate: '',
    image: '',
    description: '',
  })
  const [carErrors, setCarErrors] = useState({})

  // Protezione dell'accesso in base al ruolo
  useEffect(() => {
    if (!user) {
      navigate('/login')
    } else if (user.role !== 'admin') {
      navigate('/')
    }
  }, [user, navigate])

  /*
    Caricamento dei dati necessari alla dashboard

    Avviene solo se l'utente è un admin e si caricano prenotazioni, auto e servizi.
  */
  useEffect(() => {
    if (user && user.role === 'admin') {
      dispatch(fetchBookings())
      dispatch(fetchCars())
      dispatch(fetchServices())
    }
  }, [dispatch, user])

  /*
    Se non è admin non renderizziamo nulla.
    È una seconda barriera di sicurezza che oltre al redirect dell'effetto: evita di mostrare per un istante
    contenuti riservati prima che la navigazione avvenga.
  */
  if (!user || user.role !== 'admin') return null


  //GESTIONE PRENOTAZIONI

  // Applica il filtro per stato se è impostato mostra solo quello altrimenti tutte le prenotazioni
  const filteredBookings = statusFilter
    ? bookings.filter((b) => b.status === statusFilter)
    : bookings

  /* Trova l'auto corrispondente a un id per mostrarne nome e immagine in tabella */
  const getCarById = (carId) => cars.find((c) => String(c.id) === String(carId))

  /*
    Dato l'elenco di id dei servizi scelti in una prenotazione, restituisce i loro nomi.
    map() trasforma ogni id nel nome del servizio corrispondente; filter(Boolean)
    scarta gli eventuali valori vuoti come servizi non trovati.
  */
  const getServiceNames = (serviceIds) => {
    if (!serviceIds || serviceIds.length === 0) return []
    return serviceIds
      .map((sid) => services.find((s) => String(s.id) === String(sid))?.name)
      .filter(Boolean)
  }

  // Approva una prenotazione inviando l'azione a Redux.
  const handleApprove = (id) => {
    dispatch(approveBooking(id))
  }

  /* Conferma il rifiuto di una prenotazione */
  const handleRejectSubmit = () => {
    if (!rejectReason) {
      setRejectError('Il motivo del rifiuto è obbligatorio')
      return
    }
    dispatch(rejectBooking({ id: rejectId, reason: rejectReason }))
    setRejectId(null)
    setRejectReason('')
    setRejectError('')
  }

  // Chiude una prenotazione
  const handleClose = (id) => {
    dispatch(closeBooking(id))
  }

  /*
    AUTO (CRUD)
    Handler generico per tutti i campi del form auto.
    Usa e.target.name per capire quale campo aggiornare scrivendo solo quello e lasciando invariati gli altri ([name] è una chiave dinamica).
  */
  const handleCarFormChange = (e) => {
    const { name, value } = e.target
    setCarForm({ ...carForm, [name]: value })
  }

  /* Apre il form di inserimento per CREARE una nuova auto */
  const handleNewCar = () => {
    setEditingCar(null)
    setCarForm({
      brand: '',
      model: '',
      year: '',
      category: '',
      transmission: '',
      fuel: '',
      seats: '',
      pricePerDay: '',
      licensePlate: '',
      image: '',
      description: '',
    })
    setCarErrors({})
    setShowCarForm(true)
  }

  // Apre il form per MODIFICARE un'auto esistente
  const handleEditCar = (car) => {
    setEditingCar(car)
    setCarForm({
      brand: car.brand,
      model: car.model,
      year: String(car.year),
      category: car.category,
      transmission: car.transmission,
      fuel: car.fuel,
      seats: String(car.seats),
      pricePerDay: String(car.pricePerDay),
      licensePlate: car.licensePlate || '',
      image: car.image || '',
      description: car.description || '',
    })
    setCarErrors({})
    setShowCarForm(true)
  }

  // Elimina un'auto
  const handleDeleteCar = (id) => {
    if (window.confirm('Sei sicuro di voler eliminare questa auto?')) {
      dispatch(deleteCar(id))
    }
  }

  // Validazione e salvataggio del form auto inoltre vengono gestiti tutti i controlli come ad es.formato targa italiano ecc.
  const handleCarFormSubmit = (e) => {
    e.preventDefault()
    const newErrors = {}

    if (!carForm.brand) {
      newErrors.brand = 'Brand obbligatorio'
    }
    if (!carForm.model) {
      newErrors.model = 'Modello obbligatorio'
    }
    if (!carForm.year) {
      newErrors.year = 'Anno obbligatorio'
    } else if (!/^\d{4}$/.test(carForm.year)) {
      // l'anno deve essere composto esattamente da 4 cifre
      newErrors.year = 'Anno non valido'
    }
    if (!carForm.category) {
      newErrors.category = 'Categoria obbligatoria'
    }
    if (!carForm.transmission) {
      newErrors.transmission = 'Trasmissione obbligatoria'
    }
    if (!carForm.fuel) {
      newErrors.fuel = 'Carburante obbligatorio'
    }
    if (!carForm.seats) {
      newErrors.seats = 'Posti obbligatori'
    }
    if (!carForm.pricePerDay) {
      newErrors.pricePerDay = 'Prezzo obbligatorio'
    } else if (isNaN(carForm.pricePerDay) || Number(carForm.pricePerDay) <= 0) {
      // il prezzo deve essere un numero maggiore di zero
      newErrors.pricePerDay = 'Prezzo non valido'
    }
    if (!carForm.licensePlate) {
      newErrors.licensePlate = 'Targa obbligatoria'
    } else if (!/^[A-Z]{2}\d{3}[A-Z]{2}$/i.test(carForm.licensePlate.replace(/\s/g, ''))) {
      // formato targa italiana: due lettere, tre cifre, due lettere ad es. AB123CD
      newErrors.licensePlate = 'Targa non valida (es. AB123CD)'
    }

    setCarErrors(newErrors)

    if (Object.keys(newErrors).length === 0) {
      // Prepara l'oggetto da inviare convertendo i campi numerici da stringa a numero (year, seats, pricePerDay) e normalizzando la targa in maiuscolo senza spazi
      const carData = {
        brand: carForm.brand,
        model: carForm.model,
        year: Number(carForm.year),
        category: carForm.category,
        transmission: carForm.transmission,
        fuel: carForm.fuel,
        seats: Number(carForm.seats),
        pricePerDay: Number(carForm.pricePerDay),
        licensePlate: carForm.licensePlate.toUpperCase().replace(/\s/g, ''),
        image: carForm.image,
        description: carForm.description,
      }

      if (editingCar) {
        // se c'è un'auto in modifica, aggiorna quella esistente
        dispatch(updateCar({ id: editingCar.id, car: carData }))
      } else {
        // altrimenti crea una nuova auto
        dispatch(createCar(carData))
      }

      setShowCarForm(false)
      setEditingCar(null)
    }
  }

  // Mappa ogni stato al colore del badge e alla relativa etichetta
  const statusColors = {
    'IN_ATTESA': 'warning',
    'IN_LAVORAZIONE': 'info',
    'APPROVATO': 'success',
    'RIFIUTATO': 'danger',
    'CHIUSO': 'secondary',
  }
  const statusLabels = {
    'IN_ATTESA': 'In attesa',
    'IN_LAVORAZIONE': 'In lavorazione',
    'APPROVATO': 'Approvato',
    'RIFIUTATO': 'Rifiutato',
    'CHIUSO': 'Chiuso',
  }

  return (
    <div className="bookings-page">

      {/* Hero della dashboard */}
      <section
        className="bookings-hero"
        style={{ backgroundImage: `url(${dashboardHero})` }}
      >
        <div className="bookings-hero-overlay">
          <div className="container">
            <p className="bookings-hero-eyebrow">Pannello di gestione</p>
            <h1 className="bookings-hero-title">Dashboard Admin</h1>
            <p className="bookings-hero-subtitle">
              Gestisci le prenotazioni dei clienti e il parco auto.
            </p>
          </div>
        </div>
      </section>

      <div className="container py-5">

        {/* Schede di navigazione tra "Prenotazioni" e "Gestione Auto" */}
        <ul className="nav nav-tabs justify-content-center mb-4">
          <li className="nav-item">
            <button
              className={`nav-link ${activeTab === 'bookings' ? 'active' : ''}`}
              onClick={() => setActiveTab('bookings')}
            >
              Prenotazioni
            </button>
          </li>
          <li className="nav-item">
            <button
              className={`nav-link ${activeTab === 'cars' ? 'active' : ''}`}
              onClick={() => setActiveTab('cars')}
            >
              Gestione Auto
            </button>
          </li>
        </ul>

      {/* SCHEDA PRENOTAZIONI */}
      {activeTab === 'bookings' && (
        <div>
          {/* Filtro per stato delle prenotazioni */}
          <div className="mb-4">
            <label className="form-label">Filtra per stato</label>
            <select
              className="form-select"
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
            >
              <option value="">Tutti</option>
              <option value="IN_ATTESA">In attesa</option>
              <option value="IN_LAVORAZIONE">In lavorazione</option>
              <option value="APPROVATO">Approvato</option>
              <option value="RIFIUTATO">Rifiutato</option>
              <option value="CHIUSO">Chiuso</option>
            </select>
          </div>

          {/* Stati di caricamento, errore e lista vuota */}
          {bookingsLoading && <p>Caricamento...</p>}
          {bookingsError && <p className="text-danger">{bookingsError}</p>}

          {!bookingsLoading && !bookingsError && filteredBookings.length === 0 && (
            <p className="text-secondary">Nessuna prenotazione trovata.</p>
          )}

          {/* Tabella delle prenotazioni */}
          {!bookingsLoading && !bookingsError && filteredBookings.length > 0 && (
            <div className="table-responsive">
              <table className="table table-hover">
                <thead className="table-dark">
                  <tr>
                    <th>ID Noleggio</th>
                    <th>Cliente</th>
                    <th>Data prenotazione</th>
                    <th>Auto</th>
                    <th>Date</th>
                    <th>Totale</th>
                    <th>Servizi</th>
                    <th>Pagamento</th>
                    <th>Stato</th>
                    <th>Azioni</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredBookings.map((booking) => {
                    // Recupera l'auto della prenotazione per mostrare immagine e nome
                    const car = getCarById(booking.carId)
                    return (
                    <tr key={booking.id}>
                      {/* Identificativo auto */}
                      <td>#{String(booking.id).replace(/[^a-zA-Z0-9]/g, '').slice(-8)}</td>
                      {/* Nome e cognome del cliente presi dai dati di fatturazione */}
                      <td>
                        {booking.billingInfo
                          ? `${booking.billingInfo.firstName} ${booking.billingInfo.lastName}`
                          : `Utente #${booking.userId}`}
                      </td>
                      <td>{booking.createdAt ? new Date(booking.createdAt).toLocaleDateString('it-IT') : '—'}</td>
                      {/* Auto con miniatura (immagine) e nome se l'auto non esiste più mostra un fallback */}
                      <td>
                        {car ? (
                          <div className="d-flex align-items-center gap-2">
                            <img
                              src={`/images/cars/${car.image}`}
                              alt={`${car.brand} ${car.model}`}
                              width="48"
                              height="32"
                              style={{ objectFit: 'cover', borderRadius: '4px' }}
                            />
                            <span>{car.brand} {car.model}</span>
                          </div>
                        ) : (
                          <span className="text-secondary">Auto non disponibile</span>
                        )}
                      </td>
                      <td>{booking.startDate} → {booking.endDate}</td>
                      <td>€{booking.totalPrice}</td>
                      {/* Servizi scelti dal cliente con id convertiti nei nomi tramite getServiceNames */}
                      <td>
                        {getServiceNames(booking.selectedServices).length > 0 ? (
                          <small>{getServiceNames(booking.selectedServices).join(', ')}</small>
                        ) : (
                          <small className="text-secondary">Nessuno</small>
                        )}
                      </td>
                      <td>{booking.paymentMethod}</td>
                      {/* Badge di stato */}
                      <td>
                        <span className={`badge bg-${statusColors[booking.status]}`}>
                          {statusLabels[booking.status]}
                        </span>
                      </td>
                      {/*
                        Azioni disponibili in base allo stato:
                        - in attesa / in lavorazione si può Approvare o Rifiutare;
                        - approvata si può Chiudere;
                        - rifiutata / chiusa nessuna azione
                      */}
                      <td>
                        {(booking.status === 'IN_ATTESA' || booking.status === 'IN_LAVORAZIONE') && (
                          <>
                            <button
                              className="btn btn-success btn-sm me-1"
                              onClick={() => handleApprove(booking.id)}
                            >
                              Approva
                            </button>
                            <button
                              className="btn btn-danger btn-sm"
                              onClick={() => setRejectId(booking.id)}
                            >
                              Rifiuta
                            </button>
                          </>
                        )}
                        {booking.status === 'APPROVATO' && (
                          <button
                            className="btn btn-secondary btn-sm"
                            onClick={() => handleClose(booking.id)}
                          >
                            Chiudi
                          </button>
                        )}
                        {(booking.status === 'RIFIUTATO' || booking.status === 'CHIUSO') && (
                          <small className="text-secondary">Nessuna azione</small>
                        )}
                      </td>
                    </tr>
                    )
                  })}
                </tbody>
              </table>
            </div>
          )}

          {/* Riquadro per il rifiuto con motivo */}
          {rejectId && (
            <div className="card p-3 mt-3">
              <h5>Motivo del rifiuto per la prenotazione #{String(rejectId).replace(/[^a-zA-Z0-9]/g, '').slice(-8)}</h5>
              <div className="mb-3">
                <textarea
                  className="form-control"
                  rows="3"
                  placeholder="Inserisci il motivo del rifiuto..."
                  value={rejectReason}
                  onChange={(e) => setRejectReason(e.target.value)}
                ></textarea>
                {rejectError && <Alert variant="danger" className="mt-2 py-2">{rejectError}</Alert>}
              </div>
              <div>
                <button className="btn btn-danger me-2" onClick={handleRejectSubmit}>
                  Conferma rifiuto
                </button>
                <button
                  className="btn btn-outline-secondary"
                  onClick={() => { setRejectId(null); setRejectReason(''); setRejectError('') }}
                >
                  Annulla
                </button>
              </div>
            </div>
          )}
        </div>
      )}

      {/* SCHEDA GESTIONE AUTO */}
      {activeTab === 'cars' && (
        <div>
          {/* Pulsante per aprire il form di creazione di una nuova auto */}
          <button className="btn btn-primary mb-4" onClick={handleNewCar}>
            + Aggiungi auto
          </button>

          {carsLoading && <p>Caricamento...</p>}
          {carsError && <p className="text-danger">{carsError}</p>}

          {/* Form di creazione/modifica auto */}
          {showCarForm && (
            <div className="card p-4 mb-4">
              <h4 className="mb-3">{editingCar ? 'Modifica auto' : 'Nuova auto'}</h4>

              <form onSubmit={handleCarFormSubmit}>

                {/* Brand, Modello, Targa */}
                <div className="row">
                  <div className="col-md-4 mb-3">
                    <label className="form-label">Brand</label>
                    <input
                      type="text"
                      name="brand"
                      className="form-control"
                      value={carForm.brand}
                      onChange={handleCarFormChange}
                    />
                    {carErrors.brand && <Alert variant="danger" className="mt-2 py-2">{carErrors.brand}</Alert>}
                  </div>
                  <div className="col-md-4 mb-3">
                    <label className="form-label">Modello</label>
                    <input
                      type="text"
                      name="model"
                      className="form-control"
                      value={carForm.model}
                      onChange={handleCarFormChange}
                    />
                    {carErrors.model && <Alert variant="danger" className="mt-2 py-2">{carErrors.model}</Alert>}
                  </div>
                  <div className="col-md-4 mb-3">
                    <label className="form-label">Targa</label>
                    <input
                      type="text"
                      name="licensePlate"
                      className="form-control text-uppercase"
                      placeholder="es. AB123CD"
                      maxLength="7"
                      value={carForm.licensePlate}
                      onChange={handleCarFormChange}
                    />
                    {carErrors.licensePlate && <Alert variant="danger" className="mt-2 py-2">{carErrors.licensePlate}</Alert>}
                  </div>
                </div>

                {/* Anno, Categoria, Trasmissione */}
                <div className="row">
                  <div className="col-md-4 mb-3">
                    <label className="form-label">Anno</label>
                    <input
                      type="text"
                      name="year"
                      className="form-control"
                      value={carForm.year}
                      onChange={handleCarFormChange}
                    />
                    {carErrors.year && <Alert variant="danger" className="mt-2 py-2">{carErrors.year}</Alert>}
                  </div>
                  <div className="col-md-4 mb-3">
                    <label className="form-label">Categoria</label>
                    <select
                      name="category"
                      className="form-select"
                      value={carForm.category}
                      onChange={handleCarFormChange}
                    >
                      <option value="">Seleziona...</option>
                      <option value="Basic">Basic</option>
                      <option value="Premium">Premium</option>
                      <option value="Luxury">Luxury</option>
                    </select>
                    {carErrors.category && <Alert variant="danger" className="mt-2 py-2">{carErrors.category}</Alert>}
                  </div>
                  <div className="col-md-4 mb-3">
                    <label className="form-label">Trasmissione</label>
                    <select
                      name="transmission"
                      className="form-select"
                      value={carForm.transmission}
                      onChange={handleCarFormChange}
                    >
                      <option value="">Seleziona...</option>
                      <option value="Automatica">Automatica</option>
                      <option value="Manuale">Manuale</option>
                    </select>
                    {carErrors.transmission && <Alert variant="danger" className="mt-2 py-2">{carErrors.transmission}</Alert>}
                  </div>
                </div>

                {/* Carburante, Posti, Prezzo */}
                <div className="row">
                  <div className="col-md-4 mb-3">
                    <label className="form-label">Carburante</label>
                    <select
                      name="fuel"
                      className="form-select"
                      value={carForm.fuel}
                      onChange={handleCarFormChange}
                    >
                      <option value="">Seleziona...</option>
                      <option value="Benzina">Benzina</option>
                      <option value="Diesel">Diesel</option>
                      <option value="Elettrico">Elettrico</option>
                      <option value="Ibrido">Ibrido</option>
                    </select>
                    {carErrors.fuel && <Alert variant="danger" className="mt-2 py-2">{carErrors.fuel}</Alert>}
                  </div>
                  <div className="col-md-4 mb-3">
                    <label className="form-label">Posti</label>
                    <input
                      type="number"
                      name="seats"
                      className="form-control"
                      value={carForm.seats}
                      onChange={handleCarFormChange}
                    />
                    {carErrors.seats && <Alert variant="danger" className="mt-2 py-2">{carErrors.seats}</Alert>}
                  </div>
                  <div className="col-md-4 mb-3">
                    <label className="form-label">Prezzo al giorno (€)</label>
                    <input
                      type="number"
                      name="pricePerDay"
                      className="form-control"
                      value={carForm.pricePerDay}
                      onChange={handleCarFormChange}
                    />
                    {carErrors.pricePerDay && <Alert variant="danger" className="mt-2 py-2">{carErrors.pricePerDay}</Alert>}
                  </div>
                </div>

                {/* Nome del file immagine, con anteprima sotto */}
                <div className="mb-3">
                  <label className="form-label">Nome file immagine</label>
                  <input
                    type="text"
                    name="image"
                    className="form-control"
                    placeholder="es. audi-rs3.jpg"
                    value={carForm.image}
                    onChange={handleCarFormChange}
                  />
                  {/* Anteprima dell'immagine mostrata solo se è stato inserito un nome file */}
                  {carForm.image && (
                    <img
                      src={`/images/cars/${carForm.image}`}
                      alt="Anteprima auto"
                      className="mt-2 rounded"
                      style={{ width: '180px', height: '120px', objectFit: 'cover' }}
                    />
                  )}
                </div>

                <div className="mb-3">
                  <label className="form-label">Descrizione</label>
                  <textarea
                    name="description"
                    className="form-control"
                    rows="3"
                    value={carForm.description}
                    onChange={handleCarFormChange}
                  ></textarea>
                </div>

                {/* Pulsante salva e annulla */}
                <div>
                  <button type="submit" className="btn btn-primary me-2">
                    {editingCar ? 'Salva modifiche' : 'Crea auto'}
                  </button>
                  <button
                    type="button"
                    className="btn btn-outline-secondary"
                    onClick={() => { setShowCarForm(false); setEditingCar(null) }}
                  >
                    Annulla
                  </button>
                </div>
              </form>
            </div>
          )}

          {/* Tabella del parco auto */}
          {!carsLoading && !carsError && cars.length > 0 && (
            <div className="table-responsive">
              <table className="table table-hover">
                <thead className="table-dark">
                  <tr>
                    <th>ID Auto</th>
                    <th>Immagine</th>
                    <th>Brand</th>
                    <th>Modello</th>
                    <th>Categoria</th>
                    <th>Anno</th>
                    <th>Targa</th>
                    <th>Prezzo/giorno</th>
                    <th>Azioni</th>
                  </tr>
                </thead>
                <tbody>
                  {cars.map((car, index) => (
                    <tr key={car.id}>
                      <td>{car.id}</td>
                      {/* Miniatura dell'auto */}
                      <td>
                        <img
                          src={`/images/cars/${car.image}`}
                          alt={`${car.brand} ${car.model}`}
                          width="56"
                          height="38"
                          style={{ objectFit: 'cover', borderRadius: '4px' }}
                        />
                      </td>
                      <td>{car.brand}</td>
                      <td>{car.model}</td>
                      <td>{car.category}</td>
                      <td>{car.year}</td>
                      <td>{car.licensePlate || '—'}</td>
                      <td>€{car.pricePerDay}</td>
                      {/* Azioni: Modifica apre il form precompilato, Elimina rimuove l'auto */}
                      <td>
                        <button
                          className="btn btn-secondary btn-sm me-1"
                          onClick={() => handleEditCar(car)}
                        >
                          Modifica
                        </button>
                        <button
                          className="btn btn-danger btn-sm"
                          onClick={() => handleDeleteCar(car.id)}
                        >
                          Elimina
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      )}

      </div>
    </div>
  )
}


export default AdminDashboardPage