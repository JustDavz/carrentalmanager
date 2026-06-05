/*
  Questa è la PAGINA DI REGISTRAZIONE

  È un componente di solo layout la logica di creazione dell'account vera 
  si trova dentro il componente RegisterForm che viene integrato in questa pagina

*/

import { Link } from 'react-router-dom'

import RegisterForm from '../components/auth/RegisterForm'
import authImg from '../assets/images/lamborghini.jpg'


function RegisterPage() {
  return (
    <div className="auth-page">

      <div className="auth-page-form-side">

        <Link to="/" className="auth-page-back">
          ← Torna alla home
        </Link>

        <div className="auth-page-form-wrapper auth-page-form-wrapper--wide">

          <div className="auth-page-heading">
            <p className="auth-page-eyebrow">CarRentalManager</p>
            <h1 className="auth-page-title">Crea un account</h1>
            <p className="auth-page-subtitle">
              Registrati per prenotare la tua auto e accedere a vantaggi esclusivi.
            </p>
          </div>

          <RegisterForm />

          <p className="auth-page-switch">
            Hai già un account?{' '}
            <Link to="/login">Accedi</Link>
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


export default RegisterPage