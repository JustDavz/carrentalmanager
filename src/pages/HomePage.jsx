/*
  Questa è la HOME PAGE del sito web

  Funzionalità principali:
  - hero con video di sfondo e una barra di ricerca attraverso marca, modello, categoria, date e pulsante mostra in tempo reale quante auto sono disponibili;
  - sezione con le tre categorie (Sportive/Luxury, Premium, Basic) che linkano al catalogo filtrato;
  - sezione "Auto in Evidenza" mostrata come carosello su desktop e su mobile;
  - sezione "Come Funziona" con i quattro passaggi del noleggio.
*/

import { useState, useEffect } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { Link, useNavigate } from 'react-router-dom'
import DatePicker, { registerLocale } from 'react-datepicker'
import { it } from 'date-fns/locale/it'
import Carousel from 'react-bootstrap/Carousel'

import { fetchCars } from '../redux/slices/carsSlice'
import { fetchBookings } from '../redux/slices/bookingsSlice'
import CarCard from '../components/car/CarCard'

import heroVideo from '../assets/videos/audi.mp4'
import luxuryImg from '../assets/images/lamborghini.jpg'
import premiumImg from '../assets/images/audi-rsq8.jpg'
import basicImg from '../assets/images/smart.jpg'
import arrowRightIcon from '../assets/icons/arrow-right-white.svg'

// Registra la lingua italiana per i calendari
registerLocale('it', it)


function HomePage() {
  const dispatch = useDispatch()
  const navigate = useNavigate()

  // Auto e prenotazioni dallo store di Redux, le prenotazioni servono per calcolare la disponibilità
  const { items, loading, error } = useSelector((state) => state.cars)
  const { items: bookings } = useSelector((state) => state.bookings)

  // Carica auto e prenotazioni
  useEffect(() => {
    dispatch(fetchCars())
    dispatch(fetchBookings())
  }, [dispatch])

  // Dichiarazione campi controllati della barra di ricerca nella hero
  const [searchBrand, setSearchBrand] = useState('')
  const [searchModel, setSearchModel] = useState('')
  const [searchCategory, setSearchCategory] = useState('')
  const [startDate, setStartDate] = useState(null)
  const [endDate, setEndDate] = useState(null)

  // Vengono presentate le marche delle auto tramite una select (Set rimuove i duplicati, spread riconverte in array, sort ordina)
  const uniqueBrands = [...new Set(items.map((car) => car.brand))].sort()

  //  Definisce la data odierna usata come prima data selezionabile nei calendari
  const today = new Date()
  today.setHours(0, 0, 0, 0)

  // Converte le date nella stringa 'YYYY-MM-DD'
  const formatDate = (date) => {
    if (!date) return ''
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    return `${year}-${month}-${day}`
  }

  // Stati di prenotazione dei noleggi che occupano effettivamente le date
  const activeStatuses = ['IN_ATTESA', 'IN_LAVORAZIONE', 'APPROVATO', 'CHIUSO']

  // Verifica se un'auto è disponibile nelle date scelte dall'utente
  const isCarAvailable = (carId) => {
    if (!startDate || !endDate) return true
    const start = formatDate(startDate)
    const end = formatDate(endDate)
    const carBookings = bookings.filter(
      (booking) =>
        String(booking.carId) === String(carId) &&
        activeStatuses.includes(booking.status)
    )
    return !carBookings.some(
      (booking) => start <= booking.endDate && end >= booking.startDate
    )
  }

  // Conta quante auto rispettano i filtri della hero e sono libere nelle date scelte dall'utente per il noleggio.
  const availableCount = items.filter((car) => {
    if (searchBrand && car.brand !== searchBrand) return false
    if (searchCategory && car.category !== searchCategory) return false
    if (searchModel && !car.model.toLowerCase().includes(searchModel.toLowerCase())) return false
    if (!isCarAvailable(car.id)) return false
    return true
  }).length

  /*
    Invio della ricerca che reindirizza l'utente al catalogo passando i filtri scelti tramite lo state della rotta.
    Il catalogo (Parco Auto) li leggerà e li applicherà come filtri iniziali.
  */
  const handleSearch = (e) => {
    e.preventDefault()
    navigate('/auto', {
      state: {
        search: searchModel,
        brand: searchBrand,
        category: searchCategory,
        startDate: formatDate(startDate),
        endDate: formatDate(endDate),
      }
    })
  }

  // Indice della slide corrente e definizione dello stato responsive
  const [carouselIndex, setCarouselIndex] = useState(0)
  const [isMobile, setIsMobile] = useState(() => {
    if (typeof window === 'undefined') return false
    return window.innerWidth < 576
  })
  const [cardsPerSlide, setCardsPerSlide] = useState(() => {
    if (typeof window === 'undefined') return 4
    if (window.innerWidth >= 992) return 4
    if (window.innerWidth >= 576) return 2
    return 1
  })

  // Prende le prime 8 auto da mostrare in evidenza
  const featuredCars = items.slice(0, 8)

  // Suddivide le auto in evidenza in gruppi (chunk) della dimensione di una slide
  const carouselChunks = []
  for (let i = 0; i < featuredCars.length; i += cardsPerSlide) {
    carouselChunks.push(featuredCars.slice(i, i + cardsPerSlide))
  }

  // Va alla slide precedente
  const handlePrev = () => {
    setCarouselIndex((prev) => Math.max(0, prev - 1))
  }
  // Va alla slide successiva
  const handleNext = () => {
    setCarouselIndex((prev) => Math.min(carouselChunks.length - 1, prev + 1))
  }

  //Aggiorna isMobile e il numero di card per slide quando la finestra viene ridimensionata.
  useEffect(() => {
    const handleResize = () => {
      const w = window.innerWidth
      setIsMobile((prev) => {
        const next = w < 576
        return prev !== next ? next : prev
      })
      setCardsPerSlide((prev) => {
        const next = w >= 992 ? 4 : w >= 576 ? 2 : 1
        return prev !== next ? next : prev
      })
    }
    window.addEventListener('resize', handleResize)
    return () => window.removeEventListener('resize', handleResize)
  }, [])

  // Se cambia il numero di card per slide ad es. ruotando il dispositivo, riparte dalla prima slide
  useEffect(() => {
    setCarouselIndex(0)
  }, [cardsPerSlide])


  return (
    <div>

      {/* sezione HERO */}

      <div className="hero-section">
        <video className="hero-video" src={heroVideo}
          autoPlay
          muted
          loop
          playsInline
          preload="auto"
        />

        <div className="hero-overlay">
          <div className="container">

            <h1 className="display-3 text-center text-white mb-2">
              Noleggia la tua Auto
            </h1>

            {/* Il numero di veicoli è dinamico: mostra quante auto ci sono in catalogo */}
            <p className="lead text-center text-white mb-5">
              Scegli tra oltre {items.length} veicoli disponibili. Prenota in pochi click.
            </p>

            {/* Barra di ricerca*/}
            <form onSubmit={handleSearch} className="search-form p-4">

              <div className="row g-3">

                <div className="col-md-2">
                  <label className="form-label">Marca</label>
                  <select
                    className="form-select"
                    value={searchBrand}
                    onChange={(e) => setSearchBrand(e.target.value)}
                  >
                    <option value="">Tutte le marche</option>
                    {uniqueBrands.map((brand) => (
                      <option key={brand} value={brand}>{brand}</option>
                    ))}
                  </select>
                </div>

                <div className="col-md-2">
                  <label className="form-label">Modello</label>
                  <input
                    type="text"
                    className="form-control"
                    placeholder="es. RS3, Golf, 500..."
                    value={searchModel}
                    onChange={(e) => setSearchModel(e.target.value)}
                  />
                </div>

                <div className="col-md-2">
                  <label className="form-label">Categoria</label>
                  <select
                    className="form-select"
                    value={searchCategory}
                    onChange={(e) => setSearchCategory(e.target.value)}
                  >
                    <option value="">Tutte le categorie</option>
                    <option value="Basic">Basic</option>
                    <option value="Premium">Premium</option>
                    <option value="Luxury">Luxury</option>
                  </select>
                </div>

                <div className="col-md-2">
                  <label className="form-label">Data ritiro</label>
                  <DatePicker
                    selected={startDate}
                    onChange={(date) => setStartDate(date)}
                    selectsStart
                    startDate={startDate}
                    endDate={endDate}
                    minDate={today}
                    dateFormat="dd/MM/yyyy"
                    placeholderText="Seleziona data"
                    locale="it"
                    useWeekdaysShort
                    className="form-control"
                  />
                </div>

                <div className="col-md-2">
                  <label className="form-label">Data riconsegna</label>
                  <DatePicker
                    selected={endDate}
                    onChange={(date) => setEndDate(date)}
                    selectsEnd
                    startDate={startDate}
                    endDate={endDate}
                    minDate={startDate || today}
                    dateFormat="dd/MM/yyyy"
                    placeholderText="Seleziona data"
                    locale="it"
                    useWeekdaysShort
                    className="form-control"
                  />
                </div>

                {/* Pulsante di ricerca: il testo mostra il conteggio aggiornato in tempo reale della auto disponibili */}
                <div className="col-md-2 d-flex align-items-end">
                  <button type="submit" className="btn btn-primary btn-lg w-100">
                    {availableCount} {availableCount === 1 ? 'Auto Disponibile' : 'Auto Disponibili'}
                  </button>
                </div>

              </div>

            </form>

          </div>
        </div>
      </div>


      {/* sezione Categorie */}
      <section className="categories-section">
        <div className="categories-grid">

          <Link to="/auto" state={{ category: 'Luxury' }} className="category-tile">
            <img
              className="category-tile-img"
              src={luxuryImg}
              alt="Luxury"
              loading="lazy"
            />
            <div className="category-tile-content">
              <h3 className="category-tile-title">SPORTIVE</h3>
              <span className="category-tile-cta">
                <span>Scopri di più</span>
                <span className="category-tile-arrow" aria-hidden="true">
                  <img src={arrowRightIcon} alt="" width="16" height="16" />
                </span>
              </span>
            </div>
          </Link>

          <Link to="/auto" state={{ category: 'Premium' }} className="category-tile">
            <img
              className="category-tile-img"
              src={premiumImg}
              alt="premium"
              loading="lazy"
            />
            <div className="category-tile-content">
              <h3 className="category-tile-title">PREMIUM</h3>
              <span className="category-tile-cta">
                <span>Scopri di più</span>
                <span className="category-tile-arrow" aria-hidden="true">
                  <img src={arrowRightIcon} alt="" width="16" height="16" />
                </span>
              </span>
            </div>
          </Link>

          <Link to="/auto" state={{ category: 'Basic' }} className="category-tile">
            <img
              className="category-tile-img"
              src={basicImg}
              alt="Basic"
              loading="lazy"
            />
            <div className="category-tile-content">
              <h3 className="category-tile-title">BASIC</h3>
              <span className="category-tile-cta">
                <span>Scopri di più</span>
                <span className="category-tile-arrow" aria-hidden="true">
                  <img src={arrowRightIcon} alt="" width="16" height="16" />
                </span>
              </span>
            </div>
          </Link>

        </div>
      </section>


      {/* Sezione Auto in Evidenza */}

      <div className="container py-5">

        <div className="featured-header">
          <h2 className="mb-0">Auto in Evidenza</h2>
          {carouselChunks.length > 1 && (
            <div className="featured-carousel-controls">

              <button
                type="button"
                className="featured-arrow"
                onClick={handlePrev}
                disabled={carouselIndex === 0}
                aria-label="Precedente"
              >
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                  <line x1="19" y1="12" x2="5" y2="12" />
                  <polyline points="12 19 5 12 12 5" />
                </svg>
              </button>

              <button
                type="button"
                className="featured-arrow"
                onClick={handleNext}
                disabled={carouselIndex === carouselChunks.length - 1}
                aria-label="Successivo"
              >
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                  <line x1="5" y1="12" x2="19" y2="12" />
                  <polyline points="12 5 19 12 12 19" />
                </svg>
              </button>

            </div>
          )}
        </div>

        {loading && <p className="text-center">Caricamento...</p>}

        {error && <p className="text-center text-danger">{error}</p>}

        {/* Carosello di Bootstrap */}
        {!loading && !error && carouselChunks.length > 0 && (
          <Carousel
            activeIndex={carouselIndex}
            onSelect={setCarouselIndex}
            controls={false}
            indicators={false}
            interval={null}
            touch
            className="featured-carousel"
          >
            {carouselChunks.map((chunk, idx) => (
              <Carousel.Item key={idx}>
                <div className="row">
                  {chunk.map((car) => (
                    <div
                      key={car.id}
                      className={cardsPerSlide === 4 ? 'col-lg-3 mb-4' : cardsPerSlide === 2 ? 'col-sm-6 mb-4' : 'col-12 mb-4'}
                    >
                      <CarCard car={car} />
                    </div>
                  ))}
                </div>
              </Carousel.Item>
            ))}
          </Carousel>
        )}

        <div className="text-center mt-3">
          <Link to="/auto" className="btn btn-outline-primary btn-lg">
            Vedi tutte le auto
          </Link>
        </div>
      </div>


      {/* sezione Come Funziona il noleggio */}
      <div className="bg-light py-5 how-it-works">
        <div className="container">

          <h2 className="text-center mb-5">Come Funziona</h2>

          <div className="row text-center">

            <div className="col-md-3 mb-4">
              <div className="how-step-number">1</div>
              <h5>Scegli l'auto</h5>
              <p className="text-secondary">Sfoglia il catalogo e trova l'auto perfetta</p>
            </div>

            <div className="col-md-3 mb-4">
              <div className="how-step-number">2</div>
              <h5>Seleziona le date</h5>
              <p className="text-secondary">Indica quando vuoi ritirare e riconsegnare</p>
            </div>

            <div className="col-md-3 mb-4">
              <div className="how-step-number">3</div>
              <h5>Prenota online</h5>
              <p className="text-secondary">Completa il checkout in pochi passaggi</p>
            </div>

            <div className="col-md-3 mb-4">
              <div className="how-step-number">4</div>
              <h5>Ritira l'auto</h5>
              <p className="text-secondary">Ritira l'auto in una delle 3 nostre sedi</p>
            </div>

          </div>
        </div>
      </div>

    </div>
  )
}

export default HomePage