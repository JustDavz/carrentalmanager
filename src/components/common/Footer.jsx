//Questo componente rappresenta il FOOTER riutilizzato in tutte le pagine

import { Link } from 'react-router-dom'

// Importazione del logo e icone usate nel footer
import logoUrl from '../../assets/logo-carrentalmanager.svg'
import instagramIcon from '../../assets/icons/instagram.svg'
import facebookIcon from '../../assets/icons/facebook.svg'
import pinIcon from '../../assets/icons/pin-location.svg'
import phoneIcon from '../../assets/icons/phone.svg'


function Footer() {

  /*
    Anno corrente, ricavato dalla data di sistema.
    Usato nel testo del copyright in basso: così l'anno si aggiorna da solo e non viene modificato manualmente ogni anno.
  */
  const currentYear = new Date().getFullYear()

  // Elenco delle sedi
  const sedi = [
    {
      city: 'Roma Centro',
      address: 'Via del Corso, 120 — 00186 ROMA (RM)',
      phone: '+39 06 1234567',
      phoneHref: 'tel:+390612345678',
      hours: [
        { label: 'Lun - Ven', value: '09:00 - 19:00' },
        { label: 'Sabato', value: '09:00 - 13:00' },
        { label: 'Domenica', value: 'Chiuso', muted: true },
      ],
    },
    {
      city: 'Roma Nord',
      address: 'Via Cassia, 410 — 00189 ROMA (RM)',
      phone: '+39 06 7654321',
      phoneHref: 'tel:+390676543210',
      hours: [
        { label: 'Lun - Ven', value: '08:30 - 20:00' },
        { label: 'Sabato', value: '09:00 - 18:00' },
        { label: 'Domenica', value: 'Chiuso', muted: true },
      ],
    },
    {
      city: 'Roma Sud',
      address: 'Via Cristoforo Colombo, 285 — 00147 ROMA (RM)',
      phone: '+39 06 5544332',
      phoneHref: 'tel:+390655443322',
      hours: [
        { label: 'Lun - Ven', value: '09:00 - 19:30' },
        { label: 'Sabato', value: '09:00 - 13:00' },
        { label: 'Domenica', value: 'Chiuso', muted: true },
      ],
    },
  ]

  return (
    <footer className="crm-footer">
      <div className="container">

        <div className="row crm-footer-main">

          {/* Logo e dati aziendali */}
          <div className="col-lg-4 col-md-12 mb-5">
            <Link to="/" className="crm-footer-brand">
              <img src={logoUrl} alt="CarRentalManager" className="crm-footer-logo" />
            </Link>

            <p className="crm-footer-legal-name">Progetto CarRentalManager</p>

            <div className="crm-footer-legal">
              <p>Sede Legale: Via del Corso, 120 — 00186 ROMA (RM)</p>
              <p>P.IVA / C.F.: 12345678901</p>
              <p>Cap. Soc.: € 1.000.000</p>
              <p>Registro Imprese di Roma n. 12345678901 REA n. RM - 1234567</p>
            </div>
          </div>

          {/* Elenco delle sedi con orari e indirizzi */}
          <div className="col-lg-5 col-md-7 mb-5">
            <h5 className="crm-footer-title">Sedi</h5>

            {sedi.map((sede) => (
              <div key={sede.city} className="crm-footer-sede">
                <p className="crm-footer-sede-city">{sede.city}</p>
                <p className="crm-footer-sede-row">
                  <span className="crm-footer-sede-icon">
                    <img src={pinIcon} alt="" width="14" height="14" />
                  </span>
                  <span>{sede.address}</span>
                </p>

                <p className="crm-footer-sede-row">
                  <span className="crm-footer-sede-icon">
                    <img src={phoneIcon} alt="" width="14" height="14" />
                  </span>
                  <a href={sede.phoneHref} className="crm-footer-link">{sede.phone}</a>
                </p>

                <p className="crm-footer-sede-row crm-footer-sede-hours">
                  {sede.hours.map((h, i) => (
                    <span key={h.label}>
                      {i > 0 && <span className="crm-footer-sede-sep"> • </span>}
                      <span>{h.label} </span>
                      <span className={h.muted ? 'crm-footer-muted' : ''}>{h.value}</span>
                    </span>
                  ))}
                </p>
              </div>
            ))}
          </div>

          {/* Link di navigazione interni e contatti */}
          <div className="col-lg-3 col-md-5 mb-5">
            <h5 className="crm-footer-title">Esplora</h5>
            <ul className="crm-footer-list crm-footer-list--mb">
              <li><Link to="/" className="crm-footer-link">Home</Link></li>
              <li><Link to="/auto" className="crm-footer-link">Catalogo</Link></li>
              <li><Link to="/contatti" className="crm-footer-link">Contatti</Link></li>
            </ul>

            <h5 className="crm-footer-title">Contatti</h5>
            <ul className="crm-footer-list">
              <li>
                <a href="mailto:info@carrentalmanager.it" className="crm-footer-link">
                  info@carrentalmanager.it
                </a>
              </li>
            </ul>
          </div>

        </div>

        <div className="crm-footer-divider"></div>

        {/* Barra finale copyright e social networks */}
        <div className="crm-footer-bottom">
          <p className="crm-footer-copy">
            © {currentYear} CarRentalManager — Progetto di Frontend realizzato da David Conocchioli
          </p>

           <div className="crm-footer-social">
            <a
              href="https://instagram.com"
              target="_blank"
              rel="noopener noreferrer"
              className="crm-footer-social-link"
              aria-label="Instagram"
            >
              <img src={instagramIcon} alt="Instagram" width="18" height="18" />
            </a>

            <a
              href="https://facebook.com"
              target="_blank"
              rel="noopener noreferrer"
              className="crm-footer-social-link"
              aria-label="Facebook"
            >
              <img src={facebookIcon} alt="Facebook" width="18" height="18" />
            </a>
          </div>
        </div>

      </div>
    </footer>
  )
}

// Esportazione del componente. In questo modo il Footer può essere importato e utilizzato in altre parti dell'applicazione
export default Footer