/*
  Questa è la pagina di DETTAGLIO di una singola auto

  Funzionalità principali:
  - legge l'id dell'auto dall'URL e carica i dati da Redux;
  - mostra immagine, descrizione, specifiche tecniche e prezzo;
  - permette di scegliere le date di noleggio e il punto di ritiro, impedendo la scelta di date già occupate da altre prenotazioni;
  - alla conferma salva la "bozza" di prenotazione e porta l'utente al checkout.

  Concetti principali usati:
  - routing dinamico (l'id arriva dall'URL tramite useParams);
  - cleanup in useEffect per azzerare l'auto selezionata all'uscita dalla pagina;
  - controllo delle date occupate sia nel calendario sia in fase di validazione.
*/

import { useState, useEffect } from 'react'
import { useParams, useNavigate, useLocation, Link } from 'react-router-dom'
import { useDispatch, useSelector } from 'react-redux'
import { Alert } from 'react-bootstrap'
import DatePicker, { registerLocale } from 'react-datepicker'
import { it } from 'date-fns/locale/it'

import { fetchCarById, resetSelectedCar } from '../redux/slices/carsSlice'
import { fetchBookings, setCurrentBooking } from '../redux/slices/bookingsSlice'

// Importa icone delle specifiche auto
import typeIcon from '../assets/icons/type.svg'
import fuelIcon from '../assets/icons/fuel.svg'
import transmissionIcon from '../assets/icons/trasmission.svg'
import registrationIcon from '../assets/icons/registration.svg'
import seatsIcon from '../assets/icons/seats.svg'

import detailHero from '../assets/images/lamborghini.jpg'

// Registra la lingua italiana per il calendario (DatePicker)
registerLocale('it', it)

// Punti di ritiro disponibili
const pickupLocations = [
  { value: 'roma-centro', label: 'Roma Centro' },
  { value: 'roma-nord', label: 'Roma Nord' },
  { value: 'roma-sud', label: 'Roma Sud' },
]


function CarDetailPage() {
  /*
    useParams legge i parametri dinamici della rotta.
    Qui si estrae "id", cioè l'identificativo dell'auto presente nell'URL
  */
  const { id } = useParams()
  const dispatch = useDispatch()
  const navigate = useNavigate()
  const location = useLocation() // usato per leggere eventuali date passate dalla ricerca in home

  // Auto attualmente selezionata con stato di caricamento ed errore
  const { selectedCar, loading, error } = useSelector((state) => state.cars)

  // Tutte le prenotazioni: servono per capire quali date sono già occupate per questa auto
  const { items: allBookings } = useSelector((state) => state.bookings)

  /*
    Stati locali del form di prenotazione:
    - startDate / endDate: date scelte nel DatePicker;
    - pickupLocation: punto di ritiro selezionato;
    - errors: errori di validazione.
  */
  const [startDate, setStartDate] = useState(null)
  const [endDate, setEndDate] = useState(null)
  const [pickupLocation, setPickupLocation] = useState('')
  const [errors, setErrors] = useState({})

  // Carica l'auto in base all'id e tutte le prenotazioni
  useEffect(() => {
    dispatch(fetchCarById(id))
    dispatch(fetchBookings())

    return () => {
      dispatch(resetSelectedCar())
    }
  }, [dispatch, id])

  /*
    Precompila le date se si arriva dalla barra di ricerca della home,
    dove le date scelte vengono passate tramite lo state della rotta (location.state).
    Le stringhe vengono riconvertite in oggetti Date per il DatePicker.
  */
  useEffect(() => {
    if (location.state) {
      if (location.state.startDate) setStartDate(new Date(location.state.startDate))
      if (location.state.endDate) setEndDate(new Date(location.state.endDate))
    }
  }, [location.state])

  // Stati di prenotazione che "occupano" effettivamente le date
  const activeStatuses = ['IN_ATTESA', 'IN_LAVORAZIONE', 'APPROVATO', 'CHIUSO']

  // Prenotazioni attive di questa auto
  const activeBookings = allBookings.filter(
    (b) => String(b.carId) === String(id) && activeStatuses.includes(b.status)
  )

  /*
      Intervalli di date già occupate nel formato richiesto dal DatePicker e verranno passati a excludeDateIntervals per
      barrare i giorni sul calendario quando l'auto non è disponibile.
      Vengono barrati tutti i giorni della prenotazione dal ritiro alla riconsegna inclusi quindi l'auto torna disponibile
      dal giorno successivo.
      Le date sono normalizzate a mezzanotte locale per evitare errori nel confronto e nella visualizzazione dei giorni sul calendario.
  */
  const excludedIntervals = activeBookings.map((booking) => {
    const [sy, sm, sd] = booking.startDate.split('-').map(Number)
    const start = new Date(sy, sm - 1, sd, 0, 0, 0, 0)

    const [ey, em, ed] = booking.endDate.split('-').map(Number)
    const end = new Date(ey, em - 1, ed, 0, 0, 0, 0)

    return { start, end }
  })

  // Converte un oggetto Date nella stringa 'YYYY-MM-DD' usata per salvare e confrontare le date
  const formatDate = (date) => {
    if (!date) return ''
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    return `${year}-${month}-${day}`
  }

  // Verifica se le date scelte si sovrappongono a una prenotazione esistente
  const hasDateOverlap = (start, end) => {
    if (!start || !end) return false
    const startStr = formatDate(start)
    const endStr = formatDate(end)

    return activeBookings.some((booking) => {
      return startStr <= booking.endDate && endStr >= booking.startDate
    })
  }

  // Calcola il numero di giorni tra le due date
  const calculateDays = (start, end) => {
    if (!start || !end) return 0
    const diffMs = end.getTime() - start.getTime()
    return Math.ceil(diffMs / (1000 * 60 * 60 * 24))
  }

  // Data minima selezionabile: oggi che viene azzerata a mezzanotte
  const today = new Date()
  today.setHours(0, 0, 0, 0)

  /*
    Valida il form e se è tutto corretto salva la bozza di prenotazione.

    Controlli effettuati:
    - data di ritiro presente e non nel passato;
    - data di riconsegna presente e successiva al ritiro;
    - punto di ritiro selezionato;
    - assenza di sovrapposizioni con prenotazioni esistenti.
  */
  const handleSubmit = (e) => {
    e.preventDefault()
    const newErrors = {}

    if (!startDate) {
      newErrors.startDate = 'Data di ritiro obbligatoria'
    } else if (startDate < today) {
      newErrors.startDate = 'La data di ritiro non può essere nel passato'
    }

    if (!endDate) {
      newErrors.endDate = 'Data di riconsegna obbligatoria'
    } else if (endDate <= startDate) {
      newErrors.endDate = 'La data di riconsegna deve essere dopo il ritiro'
    }

    if (!pickupLocation) {
      newErrors.pickupLocation = 'Seleziona un punto di ritiro/riconsegna'
    }

    // controllo sovrapposizione solo se le due date sono valide tra loro
    if (startDate && endDate && endDate > startDate) {
      if (hasDateOverlap(startDate, endDate)) {
        newErrors.dates = 'Le date selezionate si sovrappongono a una prenotazione esistente'
      }
    }

    setErrors(newErrors)

    if (Object.keys(newErrors).length === 0) {
      const days = calculateDays(startDate, endDate)

      /*
        Salva nello store di Redux la "bozza" di prenotazione (currentBooking) con tutti i dati
        utili al checkout: dati dell'auto, date, giorni, prezzo base e punto di ritiro.
        Il prezzo base è giorni x prezzo giornaliero; i servizi extra si aggiungono dopo.
        pickupLocationLabel ricava l'etichetta leggibile dal valore selezionato.
      */
      dispatch(setCurrentBooking({
        carId: selectedCar.id,
        carBrand: selectedCar.brand,
        carModel: selectedCar.model,
        carCategory: selectedCar.category,
        carImage: selectedCar.image,
        pricePerDay: selectedCar.pricePerDay,
        startDate: formatDate(startDate),
        endDate: formatDate(endDate),
        days,
        basePrice: days * selectedCar.pricePerDay,
        pickupLocation,
        pickupLocationLabel: pickupLocations.find((l) => l.value === pickupLocation)?.label || '',
      }))

      // reindirizza l'utente alla pagina di checkout
      navigate('/checkout')
    }
  }

  /*
    Stati di uscita anticipata (early return):
    mentre l'auto carica, in caso di errore o se l'auto non esiste,
    si mostra un messaggio dedicato senza renderizzare il resto della pagina.
  */
  if (loading) {
    return <div className="container py-5"><p>Caricamento...</p></div>
  }

  if (error) {
    return <div className="container py-5"><p className="text-danger">{error}</p></div>
  }

  if (!selectedCar) {
    return <div className="container py-5"><p>Auto non trovata.</p></div>
  }

  // Giorni e flag per decidere se mostrare il riepilogo prezzo quindi date valide e non sovrapposte
  const days = calculateDays(startDate, endDate)
  const showPrice = startDate && endDate && endDate > startDate && !hasDateOverlap(startDate, endDate)

  return (
    <div className="catalog-page">

      {/* Hero con breadcrumb percorso di navigazione e nome dell'auto */}
      <section
        className="catalog-hero"
        style={{ backgroundImage: `url(${detailHero})` }}
      >
        <div className="catalog-hero-overlay">
          <div className="container">
            {/* breadcrumb link al catalogo e nome auto corrente */}
            <p className="catalog-hero-eyebrow">
              <Link to="/auto" className="text-white text-decoration-none">Parco Auto</Link>
              {' › '}
              {selectedCar.brand} {selectedCar.model}
            </p>
            <h1 className="catalog-hero-title">
              {selectedCar.brand} {selectedCar.model}
            </h1>
          </div>
        </div>
      </section>

      <div className="container py-5">
        <div className="row">

          {/* Immagine dell'auto */}
          <div className="col-md-6 mb-4">
            <img
              src={`/images/cars/${selectedCar.image}`}
              className="img-fluid rounded"
              alt={`${selectedCar.brand} ${selectedCar.model}`}
            />
          </div>

          {/* Descrizione, specifiche, prezzo e form di prenotazione */}
          <div className="col-md-6">

            <h2>{selectedCar.brand} {selectedCar.model}</h2>
            <p className="text-secondary">{selectedCar.description}</p>

          {/* Griglia delle specifiche tecniche dell'auto */}
          <div className="car-specs-grid">

            <div className="car-specs-grid-item">
              <span className="car-specs-grid-icon">
                <img src={typeIcon} alt="" />
              </span>
              <div className="car-specs-grid-text">
                <span className="car-specs-grid-label">Categoria</span>
                <span className="car-specs-grid-value">{selectedCar.category}</span>
              </div>
            </div>

            <div className="car-specs-grid-item">
              <span className="car-specs-grid-icon">
                <img src={registrationIcon} alt="" />
              </span>
              <div className="car-specs-grid-text">
                <span className="car-specs-grid-label">Anno</span>
                <span className="car-specs-grid-value">{selectedCar.year}</span>
              </div>
            </div>

            <div className="car-specs-grid-item">
              <span className="car-specs-grid-icon">
                <img src={fuelIcon} alt="" />
              </span>
              <div className="car-specs-grid-text">
                <span className="car-specs-grid-label">Carburante</span>
                <span className="car-specs-grid-value">{selectedCar.fuel}</span>
              </div>
            </div>

            <div className="car-specs-grid-item">
              <span className="car-specs-grid-icon">
                <img src={transmissionIcon} alt="" />
              </span>
              <div className="car-specs-grid-text">
                <span className="car-specs-grid-label">Trasmissione</span>
                <span className="car-specs-grid-value">{selectedCar.transmission}</span>
              </div>
            </div>

            <div className="car-specs-grid-item">
              <span className="car-specs-grid-icon">
                <img src={seatsIcon} alt="" />
              </span>
              <div className="car-specs-grid-text">
                <span className="car-specs-grid-label">Posti</span>
                <span className="car-specs-grid-value">{selectedCar.seats}</span>
              </div>
            </div>

          </div>

          {/* Prezzo al giorno dell'auto */}
          <h3 className="text-primary mb-4">
            €{selectedCar.pricePerDay} <small className="text-secondary">/giorno</small>
          </h3>

          {/* Avviso mostrato solo se ci sono date occupate */}
          {activeBookings.length > 0 && (
            <p className="small text-secondary mb-3">
              Le date barrate nel calendario non sono disponibili.
            </p>
          )}

          {/* Form di prenotazione */}
          <form onSubmit={handleSubmit}>

            {/* Errore relativo alla sovrapposizione delle date */}
            {errors.dates && <Alert variant="danger">{errors.dates}</Alert>}

            <div className="row mb-3">
              {/* Calendario data ritiro */}
              <div className="col-6">
                <label className="form-label">Data ritiro</label>
                <DatePicker
                  selected={startDate}
                  onChange={(date) => setStartDate(date)}
                  selectsStart
                  startDate={startDate}
                  endDate={endDate}
                  minDate={today}
                  excludeDateIntervals={excludedIntervals}
                  dateFormat="dd/MM/yyyy"
                  placeholderText="Seleziona data"
                  locale="it"
                  useWeekdaysShort
                  className="form-control"
                />
                {errors.startDate && <Alert variant="danger" className="mt-2 py-2">{errors.startDate}</Alert>}
              </div>

              {/* Calendario data riconsegna */}
              <div className="col-6">
                <label className="form-label">Data riconsegna</label>
                <DatePicker
                  selected={endDate}
                  onChange={(date) => setEndDate(date)}
                  selectsEnd
                  startDate={startDate}
                  endDate={endDate}
                  minDate={startDate || today}
                  excludeDateIntervals={excludedIntervals}
                  dateFormat="dd/MM/yyyy"
                  placeholderText="Seleziona data"
                  locale="it"
                  useWeekdaysShort
                  className="form-control"
                />
                {errors.endDate && <Alert variant="danger" className="mt-2 py-2">{errors.endDate}</Alert>}
              </div>
            </div>

            {/* Select del punto di ritiro */}
            <div className="mb-3">
              <label className="form-label">Punto di ritiro/riconsegna</label>
              <select
                className="form-select"
                value={pickupLocation}
                onChange={(e) => setPickupLocation(e.target.value)}
              >
                <option value="">Seleziona dove ritirare/riconsegnare l'auto</option>
                {pickupLocations.map((loc) => (
                  <option key={loc.value} value={loc.value}>{loc.label}</option>
                ))}
              </select>
              {errors.pickupLocation && (
                <Alert variant="danger" className="mt-2 py-2">{errors.pickupLocation}</Alert>
              )}
            </div>

            {/*
              Riepilogo del prezzo, mostrato solo quando le date sono valide e libere.
              Indica giorni x prezzo/giorno = totale base esclusi i servizi extra che sono selezionabili nel checkout.
            */}
            {showPrice && (
              <div className="alert alert-info mb-3">
                <strong>{days} giorni</strong> ×
                €{selectedCar.pricePerDay}/giorno =
                <strong> €{days * selectedCar.pricePerDay}</strong>
                <small className="d-block text-secondary mt-1">
                  Servizi aggiuntivi selezionabili al checkout
                </small>
              </div>
            )}

            <button type="submit" className="btn btn-primary btn-lg w-100">
              Noleggia
            </button>

          </form>

        </div>
      </div>
      </div>
    </div>
  )
}


export default CarDetailPage
