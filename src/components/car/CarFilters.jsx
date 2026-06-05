/*
  Questo componente gestisce i FILTRI del catalogo auto

  Funzionalità principali:
  - permette di filtrare le auto per ricerca, date di disponibilità, marca, categoria, carburante e trasmissione;
  - Riceve i valori dal componente padre (CatalogPage) e gli comunica ogni modifica tramite le callback onFilterChange e onDatesChange.
  - mostra il pulsante di reset solo quando almeno un filtro è attivo
*/

import DatePicker, { registerLocale } from 'react-datepicker'
import { it } from 'date-fns/locale/it'

// Registra la lingua italiana per i calendari (nomi di giorni e mesi in italiano)
registerLocale('it', it)


/*
  COMPONENTE CarFilters

  Props ricevute dal padre (CatalogPage):
  - filters: oggetto con i filtri testuali/select attivi (search, brand, category, fuel, transmission);
  - onFilterChange: funzione per comunicare al padre i filtri aggiornati;
  - selectedDates: oggetto con le date scelte ({ startDate, endDate });
  - onDatesChange: funzione per comunicare al padre le date aggiornate;
  - brands: elenco delle marche disponibili usato per la select Marca.
*/
function CarFilters({ filters, onFilterChange, selectedDates, onDatesChange, brands = [] }) {

  // Gestisce il cambio di un filtro testuale o di una select
  const handleChange = (e) => {
    const { name, value } = e.target
    onFilterChange({ ...filters, [name]: value })
  }

  // Converte un oggetto Date nel formato stringa 'YYYY-MM-DD'
  const formatDate = (date) => {
    if (!date) return ''
    const y = date.getFullYear()
    const m = String(date.getMonth() + 1).padStart(2, '0')
    const d = String(date.getDate()).padStart(2, '0')
    return `${y}-${m}-${d}`
  }

  // Aggiornano la data di ritiro e di riconsegna
  const handleStartChange = (date) => {
    onDatesChange({ ...selectedDates, startDate: formatDate(date) })
  }

  const handleEndChange = (date) => {
    onDatesChange({ ...selectedDates, endDate: formatDate(date) })
  }

  // Azzera tutti i filtri e le date
  const handleReset = () => {
    onFilterChange({ search: '', brand: '', category: '', fuel: '', transmission: '' })
    onDatesChange({ startDate: '', endDate: '' })
  }

  // Riconverte le stringhe salvate nello stato in oggetti Date
  const startDateObj = selectedDates.startDate ? new Date(selectedDates.startDate) : null
  const endDateObj = selectedDates.endDate ? new Date(selectedDates.endDate) : null

  // Data odierna azzerata a mezzanotte
  const today = new Date()
  today.setHours(0, 0, 0, 0)

  // Conta quanti filtri sono attualmente attivi
  const activeFiltersCount = [
    filters.search,
    filters.brand,
    filters.category,
    filters.fuel,
    filters.transmission,
    selectedDates.startDate,
    selectedDates.endDate,
  ].filter(Boolean).length

  return (
    <aside className="car-filters">
      {/* SIDEBAR DEDICATA AI FILTRI */}
      
      <div className="car-filters-header">
        <h5 className="car-filters-title">Filtri</h5>
        {activeFiltersCount > 0 && (
          <button type="button" className="car-filters-reset" onClick={handleReset}>
            Reset ({activeFiltersCount})
          </button>
        )}
      </div>

      {/* RICERCA TESTUALE */}

      <div className="car-filters-group">
        <label className="car-filters-group-label">Cerca</label>
        <input
          type="text"
          name="search"
          className="form-control"
          placeholder="Brand o modello..."
          value={filters.search}
          onChange={handleChange}
        />
      </div>

      {/* FILTRO DISPONIBILITÀ TRAMITE DATE con calendario */}
      <div className="car-filters-group">
        <label className="car-filters-group-label">Disponibilità</label>

        <div className="car-filters-field">
          <label className="car-filters-sublabel">Data ritiro</label>
          <DatePicker
            selected={startDateObj}
            onChange={handleStartChange}
            selectsStart
            startDate={startDateObj}
            endDate={endDateObj}
            minDate={today}
            dateFormat="dd/MM/yyyy"
            placeholderText="Seleziona data"
            locale="it"
            useWeekdaysShort
            className="form-control"
            isClearable
          />
        </div>

        <div className="car-filters-field">
          <label className="car-filters-sublabel">Data riconsegna</label>
          <DatePicker
            selected={endDateObj}
            onChange={handleEndChange}
            selectsEnd
            startDate={startDateObj}
            endDate={endDateObj}
            minDate={startDateObj || today}
            dateFormat="dd/MM/yyyy"
            placeholderText="Seleziona data"
            locale="it"
            useWeekdaysShort
            className="form-control"
            isClearable
          />
        </div>
      </div>

      {/* FILTRO BRAND */ }
      {brands.length > 0 && (
        <div className="car-filters-group">
          <label className="car-filters-group-label">Marca</label>
          <select
            name="brand"
            className="form-select"
            value={filters.brand}
            onChange={handleChange}
          >
            <option value="">Tutte le marche</option>
            {brands.map((b) => (
              <option key={b} value={b}>{b}</option>
            ))}
          </select>
        </div>
      )}

      {/* FILTRO CATEGORIA DI NOLEGGIO */}
      <div className="car-filters-group">
        <label className="car-filters-group-label">Categoria</label>
        <select
          name="category"
          className="form-select"
          value={filters.category}
          onChange={handleChange}
        >
          <option value="">Tutte</option>
          <option value="Basic">Basic</option>
          <option value="Premium">Premium</option>
          <option value="Luxury">Luxury</option>
        </select>
      </div>

      {/* FILTRO CARBURANTE */}
      <div className="car-filters-group">
        <label className="car-filters-group-label">Carburante</label>
        <select
          name="fuel"
          className="form-select"
          value={filters.fuel}
          onChange={handleChange}
        >
          <option value="">Tutti</option>
          <option value="Benzina">Benzina</option>
          <option value="Diesel">Diesel</option>
          <option value="Elettrico">Elettrico</option>
          <option value="Ibrido">Ibrido</option>
        </select>
      </div>

      {/* FILTRO TRASMISSIONE */}
      <div className="car-filters-group">
        <label className="car-filters-group-label">Trasmissione</label>
        <select
          name="transmission"
          className="form-select"
          value={filters.transmission}
          onChange={handleChange}
        >
          <option value="">Tutte</option>
          <option value="Automatica">Automatica</option>
          <option value="Manuale">Manuale</option>
        </select>
      </div>

    </aside>
  )
}

// Esportazione del componente. In questo modo CarFilters può essere importato e utilizzato in altre parti dell'applicazione
export default CarFilters