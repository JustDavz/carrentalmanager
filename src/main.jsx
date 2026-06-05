import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { Provider } from 'react-redux'

// CSS di Bootstrap
import 'bootstrap/dist/css/bootstrap.min.css'

// store Redux
import { store } from './redux/store.js'

// componente principale
import App from './App.jsx'

// stili personalizzati
import './index.css'


// entry point dell'applicazione
createRoot(document.getElementById('root')).render(
  <StrictMode>
    <Provider store={store}>
      <App />
    </Provider>
  </StrictMode>
)