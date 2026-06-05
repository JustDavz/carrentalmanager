/*
  Questa è la PAGINA DI LOGIN.

  È un componente di solo layout la logica di autenticazione vera si trova dentro il componente LoginForm
  che viene integrato in questa pagina

*/

import { Link } from 'react-router-dom'

import LoginForm from '../components/auth/LoginForm'
import authImg from '../assets/images/lamborghini.jpg'


function LoginPage() {
  return (
    <div className="auth-page">

      <div className="auth-page-form-side">

        <Link to="/" className="auth-page-back">
          ← Torna alla home
        </Link>

        <div className="auth-page-form-wrapper auth-page-form-wrapper--wide">

          <div className="auth-page-heading">
            <p className="auth-page-eyebrow">CarRentalManager</p>
            <h1 className="auth-page-title">Bentornato</h1>
            <p className="auth-page-subtitle">
              Accedi al tuo account per gestire prenotazioni e offerte riservate.
            </p>
          </div>

          <LoginForm />

          <p className="auth-page-switch">
            Non hai un account?{' '}
            <Link to="/registrazione">Registrati</Link>
          </p>

        </div>
      </div>

      <div
        className="auth-page-image-side"
        style={{ backgroundImage: `url(${authImg})` }}
        aria-hidden="true"
      ></div>

    </div>
  )
}


export default LoginPage