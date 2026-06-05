/*
  Questo componente rappresenta la CARD DI UNA SINGOLA AUTO mostrata nel catalogo

  Funzionalità principali:
  - riceve tramite props i dati dell'auto e opzionalmente le date scelte (selectedDates);
  - mostra immagine, marca, modello, caratteristiche tecniche e prezzo al giorno;
  - se le date sono selezionate, calcola e mostra anche il totale per il periodo indicato;
  - contiene un link che porta alla pagina di dettaglio dell'auto passando le date scelte.
*/

import { Link } from 'react-router-dom'

// Importazione delle icone usate per le caratteristiche tecniche dell'auto (categoria, carburante, cambio, anno, posti).
import typeIcon from '../../assets/icons/type.svg'
import fuelIcon from '../../assets/icons/fuel.svg'
import transmissionIcon from '../../assets/icons/trasmission.svg'
import registrationIcon from '../../assets/icons/registration.svg'
import seatsIcon from '../../assets/icons/seats.svg'


/*
  COMPONENTE CarCard

  Props ricevute:
  - car: oggetto con tutti i dati dell'auto (marca, modello, prezzo, immagine, ecc.);
  - selectedDates: oggetto opzionale con le date di ritiro e riconsegna scelte dall'utente.
    Se sono selezionate le date la card mostra anche il totale calcolato per le date selezionate.
*/
function CarCard({ car, selectedDates }) {

  // Calcola il numero di giorni di noleggio in base alle date selezionate
  const calculateDays = () => {
    if (!selectedDates || !selectedDates.startDate || !selectedDates.endDate) return 0
    const startMs = new Date(selectedDates.startDate).getTime()
    const endMs = new Date(selectedDates.endDate).getTime()
    return Math.ceil((endMs - startMs) / (1000 * 60 * 60 * 24))
  }

  // Giorni calcolati e prezzo totale (giorni × prezzo giornaliero dell'auto)
  const days = calculateDays()
  const totalPrice = days * car.pricePerDay

  return (
    // Contenitore della card
    <div className="card h-100 shadow-sm">

      {/* Immagine dell'auto */}
      <img
        src={`/images/cars/${car.image}`}
        className="card-img-top"
        alt={`${car.brand} ${car.model}`}
      />

      {/* Corpo della card */}
      <div className="card-body d-flex flex-column">

        {/* Titolo: marca e modello dell'auto */}
        <h5 className="card-title">{car.brand} {car.model}</h5>

        {/* Lista delle caratteristiche dell'auto */}
        <ul className="car-specs-list">
          <li className="car-specs-list-item">
            <img className="car-specs-list-icon" src={typeIcon} alt="" />
            <span>{car.category}</span>
          </li>
          <li className="car-specs-list-item">
            <img className="car-specs-list-icon" src={fuelIcon} alt="" />
            <span>{car.fuel}</span>
          </li>
          <li className="car-specs-list-item">
            <img className="car-specs-list-icon" src={transmissionIcon} alt="" />
            <span>{car.transmission}</span>
          </li>
          <li className="car-specs-list-item">
            <img className="car-specs-list-icon" src={registrationIcon} alt="" />
            <span>{car.year}</span>
          </li>
          <li className="car-specs-list-item">
            <img className="car-specs-list-icon" src={seatsIcon} alt="" />
            <span>{car.seats} posti</span>
          </li>
        </ul>

        {/* Visualizzazione del prezzo al giorno */}
        <h5 className="text-primary mt-auto mb-1">
          €{car.pricePerDay} <small className="text-secondary">/giorno</small>
        </h5>

        {/* Totale del noleggio */}
        {days > 0 ? (
          <p className="text-success fw-bold mb-3">
            {days} giorni = €{totalPrice} totale
          </p>
        ) : (
          <div className="mb-3"></div>
        )}

        {/* Link alla pagina di dettaglio dell'auto */}
        <Link
          to={`/auto/${car.id}`}
          state={selectedDates}
          className="btn btn-primary"
        >
          Vedi dettagli
        </Link>
      </div>
    </div>
  )
}

// Esportazione del componente. In questo modo CarCard può essere importato e utilizzato in altre parti dell'applicazione.
export default CarCard