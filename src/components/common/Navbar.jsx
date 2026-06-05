//Questo componente gestisce la NAVBAR, la barra di navigazione riutilizzata in tutte le pagine

import { useState, useEffect, useRef } from 'react'
import { Link, NavLink, useNavigate } from 'react-router-dom'
import { useSelector, useDispatch } from 'react-redux'

import { logout } from '../../redux/slices/authSlice'
import logoUrl from '../../assets/logo-carrentalmanager.svg'

// Importa icona della freccia usata nei menu a tendina
import chevronIcon from '../../assets/icons/chevron-nav.svg'


// Elenco delle categorie di auto mostrate nel sottomenu "Parco Auto"
const carCategories = [
  { value: 'Basic', label: 'Basic' },
  { value: 'Premium', label: 'Premium' },
  { value: 'Luxury', label: 'Luxury' },
]


function Navbar() {
  const dispatch = useDispatch()
  const navigate = useNavigate()

  // Utente loggato che viene letto dallo store: se è null l'utente non è autenticato
  const user = useSelector((state) => state.auth.user)

  // Stati locali che controllano l'apertura/chiusura dei vari elementi della navbar
  const [scrolled, setScrolled] = useState(false)
  const [userMenuOpen, setUserMenuOpen] = useState(false)
  const [mobileOpen, setMobileOpen] = useState(false)
  const [parcoOpen, setParcoOpen] = useState(false)

  // Riferimento al contenitore del menu utente
  const userMenuRef = useRef(null)

  // Effetto che viene aggiunto alla navbar quando si scrolla
  useEffect(() => {
    const handleScroll = () => setScrolled(window.scrollY > 8)
    handleScroll()
    window.addEventListener('scroll', handleScroll, { passive: true })
    return () => window.removeEventListener('scroll', handleScroll)
  }, [])

  // Effetto che chiude il menu utente quando si clicca al di fuori di esso
  useEffect(() => {
    if (!userMenuOpen) return
    const handleClick = (e) => {
      if (userMenuRef.current && !userMenuRef.current.contains(e.target)) {
        setUserMenuOpen(false)
      }
    }
    document.addEventListener('mousedown', handleClick)
    return () => document.removeEventListener('mousedown', handleClick)
  }, [userMenuOpen])

  // Esegue il logout: chiude i menu, invia l'azione logout a Redux e reindirizza l'utente alla home
  const handleLogout = () => {
    setUserMenuOpen(false)
    setMobileOpen(false)
    dispatch(logout())
    navigate('/')
  }

  // Naviga al catalogo già filtrato per la categoria scelta nel sottomenu
  const handleCategoryClick = (category) => {
    setParcoOpen(false)
    setMobileOpen(false)
    navigate('/auto', { state: { category } })
  }

  // Calcola le iniziali da mostrare nell'avatar in modo che ogni utente invece dell'immagine profilo ha le iniziali (es. David Conocchioli -> DC)
  const getInitials = (u) => {
    if (!u) return ''
    const first = u.firstName?.[0] ?? ''
    const last = u.lastName?.[0] ?? ''
    return (first + last).toUpperCase() || 'U'
  }

  // Chiude il menu mobile e l'accordion delle categorie
  const closeMobile = () => {
    setMobileOpen(false)
    setParcoOpen(false)
  }

  return (
    // Contenitore principale della navbar
    <nav className={`crm-navbar ${scrolled ? 'crm-navbar--scrolled' : ''}`}>
      <div className="container crm-navbar-inner">

        {/* Logo: link alla home */}
        <Link to="/" className="crm-navbar-brand" aria-label="CarRentalManager home">
          <img
            src={logoUrl}
            alt="CarRentalManager"
            className="crm-navbar-logo"
          />
        </Link>

        {/* Pulsante burger per i dispositivi mobile */}
        <button
          className={`crm-burger d-lg-none ${mobileOpen ? 'is-open' : ''}`}
          onClick={() => setMobileOpen(!mobileOpen)}
          aria-label={mobileOpen ? 'Chiudi menu' : 'Apri menu'}
          aria-expanded={mobileOpen}
        >
          <span></span>
          <span></span>
          <span></span>
        </button>

        {/* Link della navabar visibili solo su desktop */}
        <ul className="crm-navbar-links d-none d-lg-flex">
          <li>
            <NavLink
              to="/"
              end
              className={({ isActive }) => `crm-navlink ${isActive ? 'is-active' : ''}`}
            >
              Home
            </NavLink>
          </li>

          <li
            className="crm-navlink-wrapper"
            onMouseEnter={() => setParcoOpen(true)}
            onMouseLeave={() => setParcoOpen(false)}
          >
            <NavLink
              to="/auto"
              end
              className={({ isActive }) => `crm-navlink crm-navlink--has-submenu ${isActive ? 'is-active' : ''}`}
            >
              Parco Auto
            </NavLink>

            {/* Sottomenu mostrato solo quando parcoOpen è true */}
            {parcoOpen && (
              <div className="crm-submenu" role="menu">
                <p className="crm-submenu-label">Tipologia auto</p>
                {/* Le categorie vengono generate dall'array carCategories con map() */}
                {carCategories.map((cat) => (
                  <button
                    key={cat.value}
                    type="button"
                    className="crm-submenu-item"
                    onClick={() => handleCategoryClick(cat.value)}
                    role="menuitem"
                  >
                    {cat.label}
                  </button>
                ))}
                <div className="crm-submenu-divider"></div>
                <Link
                  to="/auto"
                  className="crm-submenu-item crm-submenu-item--all"
                  onClick={() => setParcoOpen(false)}
                  role="menuitem"
                >
                  Vedi tutte le auto
                </Link>
              </div>
            )}
          </li>

          <li>
            <NavLink
              to="/contatti"
              className={({ isActive }) => `crm-navlink ${isActive ? 'is-active' : ''}`}
            >
              Contatti
            </NavLink>
          </li>
        </ul>

        {/* Azioni di destra nella versione desktop */}
        <div className="crm-navbar-actions d-none d-lg-flex">

          {/* Se non c'è un utente loggato vengono visualizzati pulsanti per accedere o registrarsi */}
          {!user && (
            <>
              <Link to="/login" className="crm-navlink">
                Accedi
              </Link>
              <Link to="/registrazione" className="crm-cta">
                Registrati
              </Link>
            </>
          )}

          {/* Se c'è un utente loggato appare il menu utente con avatar e voci del menu */}
          {user && (
            <div className="crm-user" ref={userMenuRef}>
              <button
                type="button"
                className={`crm-user-pill ${userMenuOpen ? 'is-open' : ''}`}
                onClick={() => setUserMenuOpen(!userMenuOpen)}
                aria-expanded={userMenuOpen}
                aria-haspopup="true"
              >
                {/* Avatar con le iniziali dell'utente */}
                <span className="crm-user-avatar" aria-hidden="true">
                  {getInitials(user)}
                </span>
                <span className="crm-user-name">{user.firstName}</span>
                <span className={`crm-user-chevron ${userMenuOpen ? 'is-open' : ''}`}>
                  <img src={chevronIcon} alt="" width="12" height="12" />
                </span>
              </button>

              {/* Tendina del menu utente mostrata solo se userMenuOpen è true */}
              {userMenuOpen && (
                <div className="crm-user-menu" role="menu">
                  <div className="crm-user-menu-header">
                    <span className="crm-user-avatar crm-user-avatar--lg" aria-hidden="true">
                      {getInitials(user)}
                    </span>
                    <div className="crm-user-menu-id">
                      <p className="crm-user-menu-name">
                        {user.firstName} {user.lastName}
                      </p>
                      <p className="crm-user-menu-email">{user.email}</p>
                    </div>
                  </div>

                  <div className="crm-user-menu-divider"></div>

                  {/* Voce visibile solo al ruolo di amministratore che collega alla dashboard di gestione del parco auto e noleggi */}
                  {user.role === 'admin' && (
                    <Link
                      to="/admin"
                      className="crm-user-menu-item"
                      onClick={() => setUserMenuOpen(false)}
                      role="menuitem"
                    >
                      Dashboard Admin
                    </Link>
                  )}

                  <div className="crm-user-menu-divider"></div>

                  {/* Voci nell'area personale attraverso i link con ?tab= che aprono una scheda */}
                  <Link
                    to="/account"
                    className="crm-user-menu-item"
                    onClick={() => setUserMenuOpen(false)}
                    role="menuitem"
                  >
                    Il mio account
                  </Link>

                  <Link
                    to="/account?tab=prenotazioni"
                    className="crm-user-menu-item"
                    onClick={() => setUserMenuOpen(false)}
                    role="menuitem"
                  >
                    Le mie prenotazioni
                  </Link>

                  <Link
                    to="/account?tab=dati"
                    className="crm-user-menu-item"
                    onClick={() => setUserMenuOpen(false)}
                    role="menuitem"
                  >
                    I miei dati
                  </Link>

                  <div className="crm-user-menu-divider"></div>

                  <button
                    type="button"
                    className="crm-user-menu-item crm-user-menu-item--danger"
                    onClick={handleLogout}
                    role="menuitem"
                  >
                    Logout
                  </button>
                </div>
              )}
            </div>
          )}
        </div>

        {/* Menu mobile: il menu completo che appare su schermi piccoli quando mobileOpen è true */}
        {mobileOpen && (
          <div className="crm-mobile-menu d-lg-none">
            <NavLink to="/" end onClick={closeMobile} className="crm-mobile-link">
              Home
            </NavLink>

            {/* All'interno del menu sotto la voce Parco Auto è presente il link principale + pulsante chevron che apre l'accordion delle categorie */}
            <div className="crm-mobile-accordion">
              <NavLink
                to="/auto"
                end
                onClick={closeMobile}
                className="crm-mobile-link crm-mobile-link--with-chevron"
              >
                Parco Auto
              </NavLink>
              <button
                type="button"
                className={`crm-mobile-accordion-toggle ${parcoOpen ? 'is-open' : ''}`}
                onClick={() => setParcoOpen(!parcoOpen)}
                aria-label={parcoOpen ? 'Chiudi categorie' : 'Apri categorie'}
                aria-expanded={parcoOpen}
              >
                <img src={chevronIcon} alt="" width="14" height="14" />
              </button>
            </div>

            {/* Gestisce le sottocategorie del Parco Auto mostrate solo se l'accordion è aperto */}
            {parcoOpen && (
              <div className="crm-mobile-sublinks">
                {carCategories.map((cat) => (
                  <button
                    key={cat.value}
                    type="button"
                    className="crm-mobile-sublink"
                    onClick={() => handleCategoryClick(cat.value)}
                  >
                    {cat.label}
                  </button>
                ))}
              </div>
            )}

            <NavLink to="/contatti" onClick={closeMobile} className="crm-mobile-link">
              Contatti
            </NavLink>

            <div className="crm-mobile-divider"></div>

            {/* Azioni accesso/registrazione se non loggato nel mobile */}
            {!user && (
              <>
                <Link to="/login" onClick={closeMobile} className="crm-mobile-link">
                  Accedi
                </Link>
                <Link to="/registrazione" onClick={closeMobile} className="crm-mobile-link crm-mobile-link--primary">
                  Registrati
                </Link>
              </>
            )}

            {/* menu utente se loggato nel mobile */}
            {user && (
              <>
                <div className="crm-mobile-user">
                  <span className="crm-user-avatar crm-user-avatar--lg" aria-hidden="true">
                    {getInitials(user)}
                  </span>
                  <div>
                    <p className="crm-user-menu-name">
                      {user.firstName} {user.lastName}
                    </p>
                    <p className="crm-user-menu-email">{user.email}</p>
                  </div>
                </div>

                <Link to="/account" onClick={closeMobile} className="crm-mobile-link">
                  Il mio account
                </Link>

                <Link to="/account?tab=prenotazioni" onClick={closeMobile} className="crm-mobile-link">
                  Le mie prenotazioni
                </Link>
                <Link to="/account?tab=dati" onClick={closeMobile} className="crm-mobile-link">
                  I miei dati
                </Link>

                {user.role === 'admin' && (
                  <Link to="/admin" onClick={closeMobile} className="crm-mobile-link">
                    Dashboard Admin
                  </Link>
                )}
                <button
                  type="button"
                  onClick={handleLogout}
                  className="crm-mobile-link crm-mobile-link--danger"
                >
                  Logout
                </button>
              </>
            )}
          </div>
        )}

      </div>
    </nav>
  )
}

// Esportazione del componente. In questo modo la NAVBAR può essere importato e utilizzato in altre parti dell'applicazione
export default Navbar