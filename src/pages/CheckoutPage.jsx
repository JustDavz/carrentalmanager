/*
  Questa è la pagina di CHECKOUT organizzata come un wizard a 4 step:
  1) Accesso con modalità di login o registrazione
  2) Dati di fatturazione e patente
  3) Servizi accessori opzionali
  4) Pagamento attraverso carta o bonifico

  A lato è sempre visibile un riepilogo "sticky" con auto, date e totale come se fosse un carrello in un e-commerce.

  Concetti principali usati:
  - un unico stato "step" controlla quale schermata mostrare;
  - i form di login e registrazione vengono riutilizzati qui con skipRedirect, così l'utente resta nel checkout invece di essere reindirizzato in alre pagine;
  - il prezzo dei servizi dipende dalla categoria dell'auto;
  - alla conferma si crea la prenotazione e si salvano i dati nel profilo dell'utente.
*/

import { useState, useEffect } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { useNavigate } from 'react-router-dom'
import { Alert } from 'react-bootstrap'

import { fetchServices } from '../redux/slices/servicesSlice'
import { createBooking, clearCurrentBooking } from '../redux/slices/bookingsSlice'
import { updateProfile } from '../redux/slices/authSlice'
import LoginForm from '../components/auth/LoginForm'
import RegisterForm from '../components/auth/RegisterForm'

import checkIcon from '../assets/icons/check.svg'
import checkoutHero from '../assets/images/lamborghini.jpg'


/* Definizione degli step del wizard */
const STEPS = [
  { id: 1, label: 'Accesso' },
  { id: 2, label: 'Dati' },
  { id: 3, label: 'Servizi' },
  { id: 4, label: 'Pagamento' },
]


function CheckoutPage() {
  const dispatch = useDispatch()
  const navigate = useNavigate()

  /*
    Dati letti dallo store:
    - user: per sapere se l'utente è loggato e far saltare lo step 1;
    - currentBooking: la "bozza" di prenotazione creata nella pagina di dettaglio;
    - services: i servizi accessori disponibili;
    - bookingLoading: stato di caricamento durante la creazione della prenotazione.
  */
  const user = useSelector((state) => state.auth.user)
  const { currentBooking } = useSelector((state) => state.bookings)
  const { items: services } = useSelector((state) => state.services)

  // Step corrente del wizard da 1 a 4
  const [step, setStep] = useState(1)

  // Nello step 1 si decide se mostrare il form di login o di registrazione
  const [showRegister, setShowRegister] = useState(false)

  // Dati di fatturazione e patente raccolti nello step 2
  const [billingInfo, setBillingInfo] = useState({
    firstName: '',
    lastName: '',
    fiscalCode: '',
    address: '',
    city: '',
    zipCode: '',
    licenseNumber: '',
    licenseIssueDate: '',
    licenseExpiryDate: '',
  })
  const [billingErrors, setBillingErrors] = useState({})

  // Id dei servizi accessori selezionati nello step 3
  const [selectedServices, setSelectedServices] = useState([])

  // Metodo di pagamento e dati della carta step 4
  const [paymentMethod, setPaymentMethod] = useState('')
  const [cardInfo, setCardInfo] = useState({
    number: '',
    holder: '',
    expiry: '',
    cvv: '',
  })
  const [paymentErrors, setPaymentErrors] = useState({})

  // Diventa true quando la prenotazione è stata confermata e mostra la schermata finale
  const [bookingConfirmed, setBookingConfirmed] = useState(false)

  // Carica i servizi accessori
  useEffect(() => {
    dispatch(fetchServices())
  }, [dispatch])

  /*
    Se l'utente è già loggato salta lo step 1 di accesso e va direttamente allo step 2
    Lo step 1 serve solo a chi non è autenticato.
  */
  useEffect(() => {
    if (user && step === 1) {
      setStep(2)
    }
  }, [user, step])

  // Precompila i dati di fatturazione UNA SOLA VOLTA quando l'utente è disponibile
  useEffect(() => {
    if (user && user.billingInfo) {
      setBillingInfo((prev) => ({
        ...prev,
        firstName: user.billingInfo.firstName || user.firstName || '',
        lastName: user.billingInfo.lastName || user.lastName || '',
        fiscalCode: user.billingInfo.fiscalCode || '',
        address: user.billingInfo.address || '',
        city: user.billingInfo.city || '',
        zipCode: user.billingInfo.zipCode || '',
        licenseNumber: user.billingInfo.licenseNumber || '',
        licenseIssueDate: user.billingInfo.licenseIssueDate || '',
        licenseExpiryDate: user.billingInfo.licenseExpiryDate || '',
      }))
    } else if (user) {
      setBillingInfo((prev) => ({
        ...prev,
        firstName: user.firstName || '',
        lastName: user.lastName || '',
      }))
    }
  }, [user])

  // Aggiunge o rimuove un servizio dalla selezione
  const handleServiceToggle = (serviceId) => {
    if (selectedServices.includes(serviceId)) {
      setSelectedServices(selectedServices.filter((id) => id !== serviceId))
    } else {
      setSelectedServices([...selectedServices, serviceId])
    }
  }

  // Tabella dei prezzi dei servizi per ogni servizio il prezzo varia in base alla categoria dell'auto, espresso in €/giorno
  const servicePricing = {
    '1': { 'Basic': 100, 'Premium': 200, 'Luxury': 300 }, // Assicurazione KASKO
    '2': { 'Basic': 50, 'Premium': 50, 'Luxury': 50 },    // Soccorso Stradale Premium
    '3': { 'Basic': 20, 'Premium': 50, 'Luxury': 250 },   // Conducente Aggiuntivo
    '4': { 'Basic': 10, 'Premium': 10, 'Luxury': 10 },    // Seggiolino Bambino
  }

  // Restituisce il prezzo di un servizio per la categoria dell'auto corrente
  const getServicePrice = (serviceId) => {
    const category = currentBooking?.carCategory
    return servicePricing[serviceId]?.[category] || 0
  }

  // Calcola il prezzo totale: prezzo base dell'auto + somma dei servizi
  const calculateTotal = () => {
    if (!currentBooking) return 0

    const servicesTotal = selectedServices.reduce((acc, serviceId) => {
      return acc + getServicePrice(serviceId) * currentBooking.days
    }, 0)

    return currentBooking.basePrice + servicesTotal
  }

  // Validazione dei dati di fatturazione e patente step 2
  const handleBillingSubmit = (e) => {
    e.preventDefault()
    const newErrors = {}

    if (!billingInfo.firstName) newErrors.firstName = 'Nome obbligatorio'
    if (!billingInfo.lastName) newErrors.lastName = 'Cognome obbligatorio'

    if (!billingInfo.fiscalCode) {
      newErrors.fiscalCode = 'Codice fiscale obbligatorio'
    } else if (!/^[A-Z0-9]{16}$/i.test(billingInfo.fiscalCode)) {
      newErrors.fiscalCode = 'Codice fiscale non valido (16 caratteri)'
    }

    if (!billingInfo.address) newErrors.address = 'Indirizzo obbligatorio'
    if (!billingInfo.city) newErrors.city = 'Città obbligatoria'

    if (!billingInfo.zipCode) {
      newErrors.zipCode = 'CAP obbligatorio'
    } else if (!/^\d{5}$/.test(billingInfo.zipCode)) {
      newErrors.zipCode = 'CAP non valido (5 cifre)'
    }

    // validazione patente con numero in formato accettabile (6-15 tra lettere e cifre)
    if (!billingInfo.licenseNumber) {
      newErrors.licenseNumber = 'Numero patente obbligatorio'
    } else if (!/^[A-Z0-9]{6,15}$/i.test(billingInfo.licenseNumber.replace(/\s/g, ''))) {
      newErrors.licenseNumber = 'Numero patente non valido'
    }

    if (!billingInfo.licenseIssueDate) {
      newErrors.licenseIssueDate = 'Data di rilascio obbligatoria'
    }

    // la patente non deve essere scaduta
    if (!billingInfo.licenseExpiryDate) {
      newErrors.licenseExpiryDate = 'Data di scadenza obbligatoria'
    } else {
      const today = new Date()
      today.setHours(0, 0, 0, 0)
      const expiry = new Date(billingInfo.licenseExpiryDate)
      if (expiry < today) {
        newErrors.licenseExpiryDate = 'La patente è scaduta'
      }
    }

    // coerenza tra le due date: la scadenza deve essere dopo il rilascio
    if (billingInfo.licenseIssueDate && billingInfo.licenseExpiryDate) {
      const issue = new Date(billingInfo.licenseIssueDate)
      const expiry = new Date(billingInfo.licenseExpiryDate)
      if (expiry <= issue) {
        newErrors.licenseExpiryDate = 'La scadenza deve essere dopo il rilascio'
      }
    }

    setBillingErrors(newErrors)

    if (Object.keys(newErrors).length === 0) {
      setStep(3)
    }
  }

  /*
    Validazione del pagamento step 4, se tutto è corretto crea la prenotazione

    Se il metodo di pagamento è "carta di credito" valida anche i dati della carta (numero a 16 cifre,
    intestatario, scadenza MM/YY, CVV a 3 cifre). Lo stato iniziale della prenotazione
    dipende dal pagamento: con carta di credito è IN_LAVORAZIONE e con bonifico IN_ATTESA.
  */
  const handlePaymentSubmit = async (e) => {
    e.preventDefault()
    const newErrors = {}

    if (!paymentMethod) {
      newErrors.paymentMethod = 'Seleziona un metodo di pagamento'
    }

    if (paymentMethod === 'carta') {
      const digits = cardInfo.number.replace(/\s/g, '') // vengono tolti gli spazi per contare le cifre

      if (!digits) {
        newErrors.cardNumber = 'Numero carta obbligatorio'
      } else if (!/^\d{16}$/.test(digits)) {
        newErrors.cardNumber = 'Il numero deve avere 16 cifre'
      }

      if (!cardInfo.holder.trim()) {
        newErrors.cardHolder = 'Intestatario obbligatorio'
      }

      if (!cardInfo.expiry) {
        newErrors.cardExpiry = 'Scadenza obbligatoria'
      } else if (!/^(0[1-9]|1[0-2])\/\d{2}$/.test(cardInfo.expiry)) {
        // formato MM/YY con mese valido (01-33)
        newErrors.cardExpiry = 'Formato MM/YY non valido'
      }

      if (!cardInfo.cvv) {
        newErrors.cardCvv = 'CVV obbligatorio'
      } else if (!/^\d{3}$/.test(cardInfo.cvv)) {
        newErrors.cardCvv = 'Il CVV deve avere 3 cifre'
      }
    }

    setPaymentErrors(newErrors)

    if (Object.keys(newErrors).length === 0) {
      // stato iniziale diverso a seconda del metodo di pagamento
      const status = paymentMethod === 'carta' ? 'IN_LAVORAZIONE' : 'IN_ATTESA'

      try {
        // Crea la prenotazione con tutti i dati raccolti
        await dispatch(createBooking({
          userId: user.id,
          carId: currentBooking.carId,
          startDate: currentBooking.startDate,
          endDate: currentBooking.endDate,
          totalPrice: calculateTotal(),
          selectedServices,
          paymentMethod,
          status,
          billingInfo,
          pickupLocation: currentBooking.pickupLocation,
          pickupLocationLabel: currentBooking.pickupLocationLabel,
        })).unwrap()

        /*
          Salva nel profilo i dati di fatturazione e patente appena inseriti, 
          così la in un noleggio futuro saranno già precompilati e visibili nell'account.
        */
        await dispatch(updateProfile({
          id: user.id,
          userData: { billingInfo },
        })).unwrap()

        setBookingConfirmed(true)      // mostra la schermata di conferma
        dispatch(clearCurrentBooking()) // svuota la bozza in vista della prenotazione creata
      } catch (err) {
        // eventuali errori sono gestiti da Redux
      }
    }
  }

  /*
    Handler unico per i campi di fatturazione usa il "name" del campo per aggiornare
    la proprietà corrispondente in billingInfo.
  */
  const handleBillingChange = (e) => {
    const { name, value } = e.target
    setBillingInfo({ ...billingInfo, [name]: value })
  }

  /*
    Handler dei campi carta di credito con formattazione automatica mentre si scrive:
    - numero: solo cifre, massimo 16 con uno spazio ogni 4;
    - scadenza: solo cifre, massimo 4 con "/" inserita dopo le prime 2 (MM/YY);
    - cvv: solo cifre massimo 3;
    - intestatario: trasformato in maiuscolo.
  */
  const handleCardChange = (e) => {
    let { name, value } = e.target

    if (name === 'number') {
      value = value.replace(/\D/g, '').slice(0, 16)
      value = value.replace(/(\d{4})(?=\d)/g, '$1 ')
    } else if (name === 'expiry') {
      value = value.replace(/\D/g, '').slice(0, 4)
      if (value.length >= 3) {
        value = value.slice(0, 2) + '/' + value.slice(2)
      }
    } else if (name === 'cvv') {
      value = value.replace(/\D/g, '').slice(0, 3)
    } else if (name === 'holder') {
      value = value.toUpperCase()
    }

    setCardInfo({ ...cardInfo, [name]: value })
  }

  /*
    Schermata di conferma che viene mostrata quando la prenotazione è stata creata.
    Il messaggio varia in base al metodo di pagamento.
  */
  if (bookingConfirmed) {
    return (
      <div className="container py-5">
        <div className="booking-success">
          <div className="booking-success-icon">
            <img src={checkIcon} alt="" width="72" height="72" />
          </div>
          <h2 className="booking-success-title">Prenotazione confermata!</h2>
          <p className="booking-success-text">
            La tua prenotazione è stata inviata con successo.
          </p>
          <p className="booking-success-hint">
            {paymentMethod === 'carta'
              ? 'Il pagamento con carta è in fase di verifica.'
              : 'Effettua il bonifico per confermare la prenotazione.'}
          </p>
          <button
            className="btn btn-primary mt-4"
            onClick={() => navigate('/account')}
          >
            Vedi le mie prenotazioni
          </button>
        </div>
      </div>
    )
  }

  /*
    Se non esiste una bozza di prenotazione (currentBooking) significa che si è arrivati
    al checkout senza aver scelto un'auto: si invita a tornare al catalogo.
  */
  if (!currentBooking) {
    return (
      <div className="container py-5 text-center">
        <h3>Nessuna prenotazione in corso</h3>
        <p className="text-secondary">Seleziona un'auto dal catalogo per iniziare.</p>
        <button className="btn btn-primary" onClick={() => navigate('/auto')}>
          Vai al catalogo
        </button>
      </div>
    )
  }

  // Totale complessivo
  const totalPrice = calculateTotal()
  const servicesTotal = totalPrice - currentBooking.basePrice

  return (
    <div className="checkout-page">

      {/* Hero del checkout*/}
      <section
        className="catalog-hero"
        style={{ backgroundImage: `url(${checkoutHero})` }}
      >
        <div className="catalog-hero-overlay">
          <div className="container">
            <p className="catalog-hero-eyebrow">Prenotazione</p>
            <h1 className="catalog-hero-title">Completa la prenotazione</h1>
            <p className="catalog-hero-subtitle">
              {currentBooking.carBrand} {currentBooking.carModel} · pochi passi e avrai finito.
            </p>
          </div>
        </div>
      </section>

      <div className="container py-5">

        {/* Barra di avanzamento */}
        <div className="checkout-stepper">
          {STEPS.map((s, idx) => {
            const isDone = step > s.id
            const isActive = step === s.id
            return (
              <div key={s.id} className="checkout-stepper-item">
                <div className={`checkout-stepper-circle ${isDone ? 'is-done' : ''} ${isActive ? 'is-active' : ''}`}>
                  {isDone ? (
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3" strokeLinecap="round" strokeLinejoin="round">
                      <polyline points="20 6 9 17 4 12" />
                    </svg>
                  ) : (
                    s.id
                  )}
                </div>
                <span className={`checkout-stepper-label ${isActive ? 'is-active' : ''}`}>{s.label}</span>
                {idx < STEPS.length - 1 && (
                  <div className={`checkout-stepper-line ${isDone ? 'is-done' : ''}`}></div>
                )}
              </div>
            )
          })}
        </div>

        <div className="row g-4 mt-2">

          {/* Form dello step corrente */}
          <div className="col-lg-8">
            <div className="checkout-card">

              {/* STEP 1 - ACCESSO */}
              {step === 1 && (
                <>
                  <h3 className="checkout-card-title">Accedi per continuare</h3>
                  <p className="text-secondary mb-4">
                    Hai bisogno di un account per completare la prenotazione.
                  </p>

                  {!showRegister ? (
                    <>
                      <LoginForm skipRedirect />
                      <p className="text-center mt-3 mb-0">
                        Non hai un account?{' '}
                        <button
                          type="button"
                          className="btn btn-link p-0 align-baseline"
                          onClick={() => setShowRegister(true)}
                        >
                          Registrati
                        </button>
                      </p>
                    </>
                  ) : (
                    <>
                      <RegisterForm skipRedirect />
                      <p className="text-center mt-3 mb-0">
                        Hai già un account?{' '}
                        <button
                          type="button"
                          className="btn btn-link p-0 align-baseline"
                          onClick={() => setShowRegister(false)}
                        >
                          Accedi
                        </button>
                      </p>
                    </>
                  )}
                </>
              )}

              {/* STEP 2 — Dati di fatturazione e patente */}
              {step === 2 && (
                <>
                  <h3 className="checkout-card-title">Dati personali</h3>
                  <p className="text-secondary mb-4">Questi dati saranno usati per la fattura e il contratto di noleggio.</p>

                  <form onSubmit={handleBillingSubmit}>

                    <div className="checkout-section-title">Fatturazione</div>

                    <div className="row mb-3">
                      <div className="col-md-6">
                        <label className="form-label">Nome</label>
                        <input
                          type="text"
                          name="firstName"
                          className="form-control"
                          value={billingInfo.firstName}
                          onChange={handleBillingChange}
                        />
                        {billingErrors.firstName && <small className="text-danger">{billingErrors.firstName}</small>}
                      </div>
                      <div className="col-md-6">
                        <label className="form-label">Cognome</label>
                        <input
                          type="text"
                          name="lastName"
                          className="form-control"
                          value={billingInfo.lastName}
                          onChange={handleBillingChange}
                        />
                        {billingErrors.lastName && <small className="text-danger">{billingErrors.lastName}</small>}
                      </div>
                    </div>

                    <div className="mb-3">
                      <label className="form-label">Codice fiscale</label>
                      <input
                        type="text"
                        name="fiscalCode"
                        className="form-control text-uppercase"
                        maxLength="16"
                        value={billingInfo.fiscalCode}
                        onChange={handleBillingChange}
                      />
                      {billingErrors.fiscalCode && <small className="text-danger">{billingErrors.fiscalCode}</small>}
                    </div>

                    <div className="mb-3">
                      <label className="form-label">Indirizzo</label>
                      <input
                        type="text"
                        name="address"
                        className="form-control"
                        value={billingInfo.address}
                        onChange={handleBillingChange}
                      />
                      {billingErrors.address && <small className="text-danger">{billingErrors.address}</small>}
                    </div>

                    <div className="row mb-4">
                      <div className="col-md-8">
                        <label className="form-label">Città</label>
                        <input
                          type="text"
                          name="city"
                          className="form-control"
                          value={billingInfo.city}
                          onChange={handleBillingChange}
                        />
                        {billingErrors.city && <small className="text-danger">{billingErrors.city}</small>}
                      </div>
                      <div className="col-md-4">
                        <label className="form-label">CAP</label>
                        <input
                          type="text"
                          name="zipCode"
                          className="form-control"
                          maxLength="5"
                          value={billingInfo.zipCode}
                          onChange={handleBillingChange}
                        />
                        {billingErrors.zipCode && <small className="text-danger">{billingErrors.zipCode}</small>}
                      </div>
                    </div>

                    <div className="checkout-section-title">Patente di guida</div>

                    <div className="mb-3">
                      <label className="form-label">Numero patente</label>
                      <input
                        type="text"
                        name="licenseNumber"
                        className="form-control text-uppercase"
                        placeholder="es. RM1234567X"
                        value={billingInfo.licenseNumber}
                        onChange={handleBillingChange}
                      />
                      {billingErrors.licenseNumber && <small className="text-danger">{billingErrors.licenseNumber}</small>}
                    </div>

                    <div className="row mb-4">
                      <div className="col-md-6">
                        <label className="form-label">Data di rilascio</label>
                        <input
                          type="date"
                          name="licenseIssueDate"
                          className="form-control"
                          value={billingInfo.licenseIssueDate}
                          onChange={handleBillingChange}
                          max={new Date().toISOString().split('T')[0]}
                        />
                        {billingErrors.licenseIssueDate && <small className="text-danger">{billingErrors.licenseIssueDate}</small>}
                      </div>
                      <div className="col-md-6">
                        <label className="form-label">Data di scadenza</label>
                        <input
                          type="date"
                          name="licenseExpiryDate"
                          className="form-control"
                          value={billingInfo.licenseExpiryDate}
                          onChange={handleBillingChange}
                          min={new Date().toISOString().split('T')[0]}
                        />
                        {billingErrors.licenseExpiryDate && <small className="text-danger">{billingErrors.licenseExpiryDate}</small>}
                      </div>
                    </div>

                    <div className="d-flex justify-content-end">
                      <button type="submit" className="btn btn-primary btn-lg">
                        Continua
                      </button>
                    </div>

                  </form>
                </>
              )}

              {/* STEP 3 — Servizi accessori */}
              {step === 3 && (
                <>
                  <h3 className="checkout-card-title">Servizi accessori</h3>
                  <p className="text-secondary mb-4">Aggiungi extra opzionali alla tua prenotazione.</p>

                  {services.length === 0 ? (
                    <p className="text-secondary">Nessun servizio disponibile.</p>
                  ) : (
                    <div className="checkout-services">
                      {services.map((service) => {
                        const isSelected = selectedServices.includes(service.id)
                        return (
                          <label
                            key={service.id}
                            className={`checkout-service ${isSelected ? 'is-selected' : ''}`}
                          >
                            <input
                              type="checkbox"
                              checked={isSelected}
                              onChange={() => handleServiceToggle(service.id)}
                              className="checkout-service-checkbox"
                            />
                            <div className="checkout-service-content">
                              <div className="checkout-service-name">{service.name}</div>
                              {service.description && (
                                <div className="checkout-service-desc">{service.description}</div>
                              )}
                            </div>
                            <div className="checkout-service-price">
                              +€{getServicePrice(service.id)}<small className="text-secondary">/giorno</small>
                            </div>
                          </label>
                        )
                      })}
                    </div>
                  )}

                  {/* Navigazione tra gli steps */}
                  <div className="d-flex justify-content-between mt-4">
                    <button type="button" className="btn btn-outline-secondary" onClick={() => setStep(2)}>
                      ← Indietro
                    </button>
                    <button type="button" className="btn btn-primary btn-lg" onClick={() => setStep(4)}>
                      Continua
                    </button>
                  </div>
                </>
              )}

              {/* STEP 4 — Pagamento */}
              {step === 4 && (
                <>
                  <h3 className="checkout-card-title">Pagamento</h3>
                  <p className="text-secondary mb-4">Scegli il metodo di pagamento per finalizzare la prenotazione.</p>

                  <form onSubmit={handlePaymentSubmit}>

                    {/* Scelta del metodo di pagamento tramite radio (carta di credito o bonifico) */}
                    <div className="checkout-payment-methods">

                      <label className={`checkout-payment-method ${paymentMethod === 'carta' ? 'is-selected' : ''}`}>
                        <input
                          type="radio"
                          name="paymentMethod"
                          value="carta"
                          checked={paymentMethod === 'carta'}
                          onChange={(e) => setPaymentMethod(e.target.value)}
                          className="checkout-payment-method-radio"
                        />
                        <div className="checkout-payment-method-content">
                          <div className="checkout-payment-method-title">Carta di credito</div>
                          <div className="checkout-payment-method-desc">Pagamento immediato, conferma rapida</div>
                        </div>
                      </label>

                      <label className={`checkout-payment-method ${paymentMethod === 'bonifico' ? 'is-selected' : ''}`}>
                        <input
                          type="radio"
                          name="paymentMethod"
                          value="bonifico"
                          checked={paymentMethod === 'bonifico'}
                          onChange={(e) => setPaymentMethod(e.target.value)}
                          className="checkout-payment-method-radio"
                        />
                        <div className="checkout-payment-method-content">
                          <div className="checkout-payment-method-title">Bonifico bancario</div>
                          <div className="checkout-payment-method-desc">Conferma entro 2 giorni lavorativi</div>
                        </div>
                      </label>

                    </div>

                    {paymentErrors.paymentMethod && (
                      <Alert variant="danger" className="py-2">{paymentErrors.paymentMethod}</Alert>
                    )}

                    {/* Form dei dati carta di credito */}
                    {paymentMethod === 'carta' && (
                      <div className="checkout-card-form">

                        <h6 className="checkout-card-form-title">Dati carta</h6>

                        <div className="mb-3">
                          <label className="form-label">Numero carta</label>
                          <input
                            type="text"
                            name="number"
                            inputMode="numeric"
                            autoComplete="cc-number"
                            placeholder="1234 5678 9012 3456"
                            className="form-control"
                            value={cardInfo.number}
                            onChange={handleCardChange}
                          />
                          {paymentErrors.cardNumber && <small className="text-danger">{paymentErrors.cardNumber}</small>}
                        </div>

                        <div className="mb-3">
                          <label className="form-label">Intestatario</label>
                          <input
                            type="text"
                            name="holder"
                            autoComplete="cc-name"
                            placeholder="DAVID CONOCCHIOLI"
                            className="form-control"
                            value={cardInfo.holder}
                            onChange={handleCardChange}
                          />
                          {paymentErrors.cardHolder && <small className="text-danger">{paymentErrors.cardHolder}</small>}
                        </div>

                        <div className="row">
                          <div className="col-md-6 mb-3">
                            <label className="form-label">Scadenza</label>
                            <input
                              type="text"
                              name="expiry"
                              inputMode="numeric"
                              autoComplete="cc-exp"
                              placeholder="MM/YY"
                              className="form-control"
                              value={cardInfo.expiry}
                              onChange={handleCardChange}
                            />
                            {paymentErrors.cardExpiry && <small className="text-danger">{paymentErrors.cardExpiry}</small>}
                          </div>
                          <div className="col-md-6 mb-3">
                            <label className="form-label">CVV</label>
                            <input
                              type="text"
                              name="cvv"
                              inputMode="numeric"
                              autoComplete="cc-csc"
                              placeholder="123"
                              className="form-control"
                              value={cardInfo.cvv}
                              onChange={handleCardChange}
                            />
                            {paymentErrors.cardCvv && <small className="text-danger">{paymentErrors.cardCvv}</small>}
                          </div>
                        </div>

                      </div>
                    )}

                    {/* Navigazione */}
                    <div className="d-flex justify-content-between mt-4">
                      <button type="button" className="btn btn-outline-secondary" onClick={() => setStep(3)}>Indietro</button>
                      <button type="submit" className="btn btn-primary btn-lg">Conferma e Paga</button>
                    </div>

                  </form>
                </>
              )}

            </div>
          </div>

          {/* Riepilogo "sticky" */}
          <div className="col-lg-4">
            <div className="checkout-summary">

              <h5 className="checkout-summary-title">Riepilogo</h5>

              {/* Auto scelta con immagine, nome e prezzo al giorno */}
              <div className="checkout-summary-car">
              {currentBooking.carImage && (
                <img
                  src={`/images/cars/${currentBooking.carImage}`}
                  alt={`${currentBooking.carBrand} ${currentBooking.carModel}`}
                  className="checkout-summary-car-img"
                />
              )}
              <div>
                <div className="checkout-summary-car-name">
                  {currentBooking.carBrand} {currentBooking.carModel}
                </div>
                <div className="checkout-summary-car-price">
                  €{currentBooking.pricePerDay}/giorno
                </div>
              </div>
            </div>

              <div className="checkout-summary-divider"></div>

              {/* Dettagli del periodo di noleggio */}
              <ul className="checkout-summary-list">
                <li>
                  <span>Ritiro</span>
                  <span>{currentBooking.startDate}</span>
                </li>
                <li>
                  <span>Riconsegna</span>
                  <span>{currentBooking.endDate}</span>
                </li>
                <li>
                  <span>Durata</span>
                  <span>{currentBooking.days} {currentBooking.days === 1 ? 'giorno' : 'giorni'}</span>
                </li>
                {/* Appare la sede indicata */}
                {currentBooking.pickupLocationLabel && (
                  <li>
                    <span>Sede ritiro</span>
                    <span>{currentBooking.pickupLocationLabel}</span>
                  </li>
                )}
              </ul>

              <div className="checkout-summary-divider"></div>

              {/* Dettaglio dei costi: noleggio base e i servizi accessori */}
              <ul className="checkout-summary-list">
                <li>
                  <span>Noleggio ({currentBooking.days} g)</span>
                  <span>€{currentBooking.basePrice}</span>
                </li>
                {servicesTotal > 0 && (
                  <li>
                    <span>Servizi accessori</span>
                    <span>€{servicesTotal}</span>
                  </li>
                )}
              </ul>

              {/* Totale finale */}
              <div className="checkout-summary-total">
                <span>Totale</span>
                <span>€{totalPrice}</span>
              </div>

            </div>
          </div>

        </div>

      </div>
    </div>
  )
}


export default CheckoutPage