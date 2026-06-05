/*
  Questa è la pagina del CATALOGO auto

  Funzionalità principali:
  - mostra l'elenco delle auto con una barra di filtri laterale
  - filtro per disponibilità nelle date scelte escludendo le auto già prenotate;
  - divide i risultati in pagine attraverso la paginazione;
  - riceve eventuali filtri iniziali dalla home o dal menu tramite lo state della rotta.

*/

import { useState, useEffect } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { useLocation } from 'react-router-dom'

import { fetchCars } from '../redux/slices/carsSlice'
import { fetchBookings } from '../redux/slices/bookingsSlice'
import CarCard from '../components/car/CarCard'
import CarFilters from '../components/car/CarFilters'

import catalogHero from '../assets/images/lamborghini.jpg'


// Numero di auto mostrate per pagina usato nei calcoli della paginazione mediante una costante generale
const CARS_PER_PAGE = 9


function CatalogPage() {
  const dispatch = useDispatch()
  const location = useLocation() // serve per leggere i filtri passati da altre pagine

  // Auto e prenotazioni dallo store. Le prenotazioni servono per il filtro di disponibilità
  const { items, loading, error } = useSelector((state) => state.cars)
  const { items: bookings } = useSelector((state) => state.bookings)

  // Stato dei filtri attivi
  const [filters, setFilters] = useState({
    search: '',
    brand: '',
    category: '',
    fuel: '',
    transmission: '',
  })

  // Date selezionate usate per il filtro di disponibilità.
  const [selectedDates, setSelectedDates] = useState({
    startDate: '',
    endDate: '',
  })

  // Pagina corrente della paginazione
  const [currentPage, setCurrentPage] = useState(1)

  // Apertura della barra filtri su mobile attraverso un pulsante e su desktop è sempre visibile
  const [mobileFiltersOpen, setMobileFiltersOpen] = useState(false)

  // Carica auto e prenotazioni una volta sola
  useEffect(() => {
    dispatch(fetchCars())
    dispatch(fetchBookings())
  }, [dispatch])

  // Applica i filtri ricevuti da un'altra pagina esempio dalla homepage che ha un form di "pre-ricerca"
  useEffect(() => {
    if (location.state) {
      setFilters((prev) => ({
        ...prev,
        search: location.state.search || '',
        brand: location.state.brand || '',
        category: location.state.category || '',
      }))
      setSelectedDates({
        startDate: location.state.startDate || '',
        endDate: location.state.endDate || '',
      })
    } else {
      setFilters({
        search: '',
        brand: '',
        category: '',
        fuel: '',
        transmission: '',
      })
      setSelectedDates({ startDate: '', endDate: '' })
    }
  }, [location.key])

  /*
    Quando cambiano filtri o date, si torna alla prima pagina, senza questa funzionalità, applicando un filtro si potrebbe restare su una pagina che non esiste più
  */
  useEffect(() => {
    setCurrentPage(1)
  }, [filters, selectedDates])

  // A ogni cambio pagina riporta lo scroll in alto
  useEffect(() => {
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }, [currentPage])

  /*
    Marche per la select dei filtri.
    new Set rimuove i duplicati, lo spread lo riconverte in array, sort lo ordina.
  */
  const uniqueBrands = [...new Set(items.map((car) => car.brand))].sort()

  // Stati di prenotazione che rendono un'auto non disponibile quindi occupata
  const activeStatuses = ['IN_ATTESA', 'IN_LAVORAZIONE', 'APPROVATO', 'CHIUSO']


  // Verifica se un'auto è disponibile nelle date selezionate
  const isCarAvailable = (carId) => {
    if (!selectedDates.startDate || !selectedDates.endDate) return true

    const carBookings = bookings.filter(
      (b) => String(b.carId) === String(carId) && activeStatuses.includes(b.status)
    )

    return !carBookings.some((booking) => {
      return selectedDates.startDate <= booking.endDate &&
        selectedDates.endDate >= booking.startDate
    })
  }

  // Applica in cascata tutti i filtri alle auto
  const filteredCars = items.filter((car) => {
    if (filters.search) {
      const search = filters.search.toLowerCase()
      const match = car.brand.toLowerCase().includes(search) ||
        car.model.toLowerCase().includes(search)
      if (!match) return false
    }
    if (filters.brand && car.brand !== filters.brand) return false
    if (filters.category && car.category !== filters.category) return false
    if (filters.fuel && car.fuel !== filters.fuel) return false
    if (filters.transmission && car.transmission !== filters.transmission) return false
    if (!isCarAvailable(car.id)) return false
    return true
  })

  /*
    Calcoli della paginazione:
    - totalPages: numero di pagine;
    - startIndex: indice da cui parte la pagina corrente;
    - paginatedCars: auto da mostrare in questa pagina.
  */
  const totalPages = Math.ceil(filteredCars.length / CARS_PER_PAGE)
  const startIndex = (currentPage - 1) * CARS_PER_PAGE
  const paginatedCars = filteredCars.slice(startIndex, startIndex + CARS_PER_PAGE)

  return (
    <div className="catalog-page">

      {/* HERO della pagina catalogo */}
      <section
        className="catalog-hero"
        style={{ backgroundImage: `url(${catalogHero})` }}
      >
        <div className="catalog-hero-overlay">
          <div className="container">
            <p className="catalog-hero-eyebrow">CarRentalManager</p>
            <h1 className="catalog-hero-title">Parco Auto</h1>
            <p className="catalog-hero-subtitle">
              Noleggio auto Basic, Premium e Luxury.
              Scegli la vettura che fa per te tra le nostre proposte.
            </p>
          </div>
        </div>
      </section>

      {/* CONTENUTO: filtri e griglia auto */}
      <div className="container py-5">

        <div className="catalog-header">

          {/* Pulsante che apre/chiude la barra filtri su mobile. Su desktop la barra è sempre mostrata quindi questo pulsante è nascosto */}
          <button
            type="button"
            className="btn btn-outline-primary catalog-mobile-toggle d-lg-none"
            onClick={() => setMobileFiltersOpen(!mobileFiltersOpen)}
          >
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
              <polygon points="22 3 2 3 10 12.46 10 19 14 21 14 12.46 22 3" />
            </svg>
            {mobileFiltersOpen ? 'Chiudi filtri' : 'Mostra filtri'}
          </button>
        </div>

        <div className="row g-4">

          {/* Barra dei filtri (CarFilters) */}
          <div className={`col-lg-3 catalog-sidebar ${mobileFiltersOpen ? 'is-open' : ''}`}>
            <CarFilters
              filters={filters}
              onFilterChange={setFilters}
              selectedDates={selectedDates}
              onDatesChange={setSelectedDates}
              brands={uniqueBrands}
            />
          </div>

          <div className="col-lg-9">

            {/* Conteggio dei risultati */}
            {!loading && !error && (
              <p className="text-secondary text-filtered-cars mb-0">
                {filteredCars.length} {filteredCars.length === 1 ? 'auto disponibile' : 'auto disponibili'}
              </p>
            )}

            {/* Stati di caricamento e errore */}
            {loading && <p className="text-center">Caricamento...</p>}
            {error && <p className="text-center text-danger">{error}</p>}

            {/* Nessun risultato con i filtri attuali */}
            {!loading && !error && filteredCars.length === 0 && (
              <div className="catalog-empty">
                <p className="mb-2">Nessuna auto disponibile con i filtri selezionati.</p>
              </div>
            )}

            {/* Griglia delle auto della pagina corrente e paginazione */}
            {!loading && !error && filteredCars.length > 0 && (
              <>
                <div className="row g-4">
                  {/* Vengono mostrate solo le auto della pagina corrente (paginatedCars) */}
                  {paginatedCars.map((car) => (
                    <div key={car.id} className="col-xl-4 col-md-6 col-12">
                      <CarCard car={car} selectedDates={selectedDates} />
                    </div>
                  ))}
                </div>

                {/* Paginazione: viene mostrata solo se c'è più di una pagina */}
                {totalPages > 1 && (
                  <nav className="mt-4">
                    <ul className="pagination justify-content-center">
                      {/* "Precedente" viene disabilitato sulla prima pagina */}
                      <li className={`page-item ${currentPage === 1 ? 'disabled' : ''}`}>
                        <button
                          className="page-link"
                          onClick={() => setCurrentPage(currentPage - 1)}
                        >
                          Precedente
                        </button>
                      </li>

                      {/* Numeri di pagina */}
                      {Array.from({ length: totalPages }, (_, i) => i + 1).map((page) => (
                        <li
                          key={page}
                          className={`page-item ${currentPage === page ? 'active' : ''}`}
                        >
                          <button
                            className="page-link"
                            onClick={() => setCurrentPage(page)}
                          >
                            {page}
                          </button>
                        </li>
                      ))}

                      {/* "Successiva" viene disabilitato sull'ultima pagina */}
                      <li className={`page-item ${currentPage === totalPages ? 'disabled' : ''}`}>
                        <button
                          className="page-link"
                          onClick={() => setCurrentPage(currentPage + 1)}
                        >
                          Successiva
                        </button>
                      </li>
                    </ul>
                  </nav>
                )}
              </>
            )}

          </div>
        </div>

      </div>
    </div>
  )
}


export default CatalogPage