/*
  Questa è la pagina dei CONTATTI

  Funzionalità principali:
  - mostra una hero introduttiva e le informazioni delle tre sedi;
  - presenta un form di contatto controllato con validazione;
  - all'invio valido mostra un messaggio di conferma e svuota i campi
*/

// useState per gestire i valori dei campi del form
import { useState } from 'react'

// Alert di React Bootstrap per i messaggi di errore e di successo
import { Alert } from 'react-bootstrap'

// immagine di sfondo per l'hero della pagina
import contactHero from '../assets/images/lamborghini.jpg'

// icone delle card di contatto come file SVG
import pinIcon from '../assets/icons/contact-pin.svg'
import phoneIcon from '../assets/icons/contact-phone.svg'


// Pagina contatti: hero, informazioni di contatto e form controllato con validazione
function ContactPage() {
  // Uno stato per ogni campo del form
  const [name, setName] = useState('')
  const [email, setEmail] = useState('')
  const [subject, setSubject] = useState('')
  const [message, setMessage] = useState('')

  // Oggetto che raccoglie gli errori di validazione e flag che segnala l'invio riuscito
  const [errors, setErrors] = useState({})
  const [submitted, setSubmitted] = useState(false)

  // Validazione i campi 
  const handleSubmit = (e) => {
    // Blocca il ricaricamento della pagina che il browser farebbe di default
    e.preventDefault()
    const newErrors = {}

    if (!name.trim()) {
      newErrors.name = 'Il nome è obbligatorio'
    } else if (name.trim().length < 2) {
      newErrors.name = 'Il nome deve avere almeno 2 caratteri'
    }

    if (!email.trim()) {
      newErrors.email = 'L\'email è obbligatoria'
    } else if (!/\S+@\S+\.\S+/.test(email)) {
      newErrors.email = 'Inserisci un\'email valida'
    }

    if (!subject.trim()) {
      newErrors.subject = 'L\'oggetto è obbligatorio'
    }

    setErrors(newErrors)

    // Se non ci sono errori mostra il messaggio di successo e svuota i campi
    if (Object.keys(newErrors).length === 0) {
      setSubmitted(true)
      setName('')
      setEmail('')
      setSubject('')
      setMessage('')
    }
  }

  return (
    <div className="contact-page">

      {/* Hero */}
      <section
        className="contact-hero"
        style={{ backgroundImage: `url(${contactHero})` }}
      >
        <div className="contact-hero-overlay">
          <div className="container">
            <p className="contact-hero-eyebrow">CarRentalManager</p>
            <h1 className="contact-hero-title">Contattaci</h1>
            <p className="contact-hero-subtitle">
              Hai domande sul noleggio o sulle nostre vetture? Scrivici, ti
              risponderemo il prima possibile.
            </p>
          </div>
        </div>
      </section>

      <div className="container py-5">
        <div className="row g-5">

          {/* Sedi e contatti */}
          <div className="col-lg-5">
            <div className="contact-info">

              <div className="contact-sede-card">
                <h3 className="contact-sede-card-city">Roma Centro</h3>
                <p className="contact-sede-card-row">
                  <img src={pinIcon} alt="" width="18" height="18" />
                  <span>Via del Corso, 120 — 00186 ROMA (RM)</span>
                </p>
                <p className="contact-sede-card-row">
                  <img src={phoneIcon} alt="" width="18" height="18" />
                  <a href="tel:+390612345678">+39 06 1234567</a>
                </p>
                <p className="contact-sede-card-hours">
                  Lun - Ven 09:00 - 19:00 · Sabato 09:00 - 13:00 · Domenica <span className="contact-sede-card-closed">Chiuso</span>
                </p>
              </div>

              <div className="contact-sede-card">
                <h3 className="contact-sede-card-city">Roma Nord</h3>
                <p className="contact-sede-card-row">
                  <img src={pinIcon} alt="" width="18" height="18" />
                  <span>Via Cassia, 410 — 00189 ROMA (RM)</span>
                </p>
                <p className="contact-sede-card-row">
                  <img src={phoneIcon} alt="" width="18" height="18" />
                  <a href="tel:+390676543210">+39 06 7654321</a>
                </p>
                <p className="contact-sede-card-hours">
                  Lun - Ven 08:30 - 20:00 · Sabato 09:00 - 18:00 · Domenica <span className="contact-sede-card-closed">Chiuso</span>
                </p>
              </div>

              <div className="contact-sede-card">
                <h3 className="contact-sede-card-city">Roma Sud</h3>
                <p className="contact-sede-card-row">
                  <img src={pinIcon} alt="" width="18" height="18" />
                  <span>Via Cristoforo Colombo, 285 — 00147 ROMA (RM)</span>
                </p>
                <p className="contact-sede-card-row">
                  <img src={phoneIcon} alt="" width="18" height="18" />
                  <a href="tel:+390655443322">+39 06 5544332</a>
                </p>
                <p className="contact-sede-card-hours">
                  Lun - Ven 09:00 - 19:30 · Sabato 09:00 - 13:00 · Domenica <span className="contact-sede-card-closed">Chiuso</span>
                </p>
              </div>

            </div>
          </div>

          {/* Form di contatto */}
          <div className="col-lg-7">
            <section className="contact-form-section">
              <div className="contact-form-section-heading">
                <p className="contact-form-section-eyebrow">SCRIVICI</p>
                <h2 className="contact-form-section-title">Inviaci un messaggio</h2>
                <p className="contact-form-section-subtitle">
                  Compila il modulo e ti ricontatteremo al più presto.
                </p>
              </div>

              {/* Messaggio di conferma mostrato dopo l'invio riuscito */}
              {submitted && (
                <Alert variant="success" onClose={() => setSubmitted(false)} dismissible>
                  Messaggio inviato con successo! Ti risponderemo a breve.
                </Alert>
              )}

              <form className="contact-form" onSubmit={handleSubmit}>

                {/* Campo nome */}
                <div className="mb-3">
                  <label className="form-label">Nome e cognome</label>
                  <input
                    type="text"
                    className="form-control"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                  />
                  {errors.name && (
                    <Alert variant="danger" className="mt-2 py-2">{errors.name}</Alert>
                  )}
                </div>

                {/* Campo email */}
                <div className="mb-3">
                  <label className="form-label">Email</label>
                  <input
                    type="email"
                    className="form-control"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                  />
                  {errors.email && (
                    <Alert variant="danger" className="mt-2 py-2">{errors.email}</Alert>
                  )}
                </div>

                {/* Campo oggetto */}
                <div className="mb-3">
                  <label className="form-label">Oggetto</label>
                  <input
                    type="text"
                    className="form-control"
                    value={subject}
                    onChange={(e) => setSubject(e.target.value)}
                  />
                  {errors.subject && (
                    <Alert variant="danger" className="mt-2 py-2">{errors.subject}</Alert>
                  )}
                </div>

                {/* Campo messaggio */}
                <div className="mb-3">
                  <label className="form-label">Messaggio</label>
                  <textarea
                    className="form-control"
                    rows="5"
                    value={message}
                    onChange={(e) => setMessage(e.target.value)}
                  ></textarea>
                </div>

                {/* Pulsante di invio */}
                <button type="submit" className="btn btn-primary btn-lg w-100">
                  Invia messaggio
                </button>

              </form>
            </section>
          </div>

        </div>
      </div>

    </div>
  )
}

export default ContactPage