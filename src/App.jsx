/*
  Questo è il COMPONENTE PRINCIPALE dell'applicazione

  È il punto in cui viene impostato il ROUTING: associa ogni indirizzo (URL)
  alla pagina da mostrare e definisce la struttura comune a tutte le pagine
  (navbar in alto, footer in basso, scroll-to-top a ogni cambio pagina).

  Concetti principali usati:
  - BrowserRouter: abilita la navigazione lato client (cambia pagina senza ricaricare il browser);
  - Routes / Route: la "tabella" che collega ogni percorso al rispettivo componente pagina;
  - layout fisso: Navbar, Footer e ScrollToTop stanno FUORI da Routes, quindi restano
    visibili/attivi su ogni pagina, mentre solo il contenuto centrale cambia con la rotta.
*/

import { BrowserRouter, Routes, Route } from 'react-router-dom'

// componenti comuni visibili su tutte le pagine
import Navbar from './components/common/Navbar'
import Footer from './components/common/Footer'
import ScrollToTop from './components/common/ScrollToTop'

// pagine dell'applicazione
import HomePage from './pages/HomePage'
import CatalogPage from './pages/CatalogPage'
import CarDetailPage from './pages/CarDetailPage'
import LoginPage from './pages/LoginPage'
import RegisterPage from './pages/RegisterPage'
import CheckoutPage from './pages/CheckoutPage'
import AdminDashboardPage from './pages/AdminDashboardPage'
import AccountPage from './pages/AccountPage'
import ContactPage from './pages/ContactPage'
import NotFoundPage from './pages/NotFoundPage'


function App() {
  return (
    // BrowserRouter avvolge tutta l'app e fornisce il contesto di navigazione
    <BrowserRouter>
      {/* ScrollToTop e Navbar sono fuori da Routes valgono per ogni pagina */}
      <ScrollToTop />
      <Navbar />

      {/*
        Routes mostra una sola rotta alla volta: quella il cui path corrisponde all'URL.
        Ad ogni Route si associa un percorso (path) e la pagina da renderizzare (element).
      */}
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/auto" element={<CatalogPage />} />

        {/* rotta dinamica: ":id" è un parametro che cambia letto nella pagina con useParams */}
        <Route path="/auto/:id" element={<CarDetailPage />} />

        <Route path="/login" element={<LoginPage />} />
        <Route path="/registrazione" element={<RegisterPage />} />
        <Route path="/checkout" element={<CheckoutPage />} />
        <Route path="/account" element={<AccountPage />} />
        <Route path="/admin" element={<AdminDashboardPage />} />
        <Route path="/contatti" element={<ContactPage />} />

        {/* rotta wildcard: "*" cattura qualsiasi URL non elencato sopra e mostra la 404 */}
        <Route path="*" element={<NotFoundPage />} />
      </Routes>

      {/* Footer fuori da Routes presente in fondo a ogni pagina */}
      <Footer />
    </BrowserRouter>
  )
}

export default App