// Questo componente è un HELPER che riporta la pagina in alto a ogni cambio di rotta altrimenti rimane bloccata in fondo

import { useEffect } from 'react'
import { useLocation } from 'react-router-dom'


function ScrollToTop() {

  // useLocation fornisce l'oggetto che descrive la rotta corrente. Da qui si estrae il pathname cioè il percorso dell'URL (es. "/auto", "/contatti")
  const { pathname } = useLocation()

  // Effetto che riporta lo scroll in alto. Si esegue ogni volta che pathname cambia cioè a ogni cambio di pagina.
  useEffect(() => {
    window.scrollTo(0, 0)
  }, [pathname])

  // Il componente non produce interfaccia quindi restituisce null e il suo unico scopo è eseguire l'effetto sopra
  return null
}

export default ScrollToTop