// Questa è la PAGINA 404 (pagina non trovata)

import { Link } from 'react-router-dom'

// immagine di sfondo in full-width
import notFoundHero from '../assets/images/lamborghini.jpg'


// pagina 404 mostrata per qualsiasi rotta non riconosciuta
function NotFoundPage() {
  return (
    <section
      className="notfound-page"
      style={{ backgroundImage: `url(${notFoundHero})` }}
    >
      <div className="notfound-page-overlay">
        <div className="notfound-page-content">

          <h1 className="notfound-page-code">404</h1>

          <h2 className="notfound-page-title">Oh no, sembra che ci siamo persi!</h2>
          <p className="notfound-page-text">
            La pagina che stai cercando non esiste o è stata spostata.
          </p>

          <div className="notfound-page-actions">
            <Link to="/" className="btn btn-primary btn-lg">
              Torna alla Home
            </Link>
            <Link to="/auto" className="btn btn-outline-light btn-lg">
              Vai al catalogo
            </Link>
          </div>

        </div>
      </div>
    </section>
  )
}


export default NotFoundPage