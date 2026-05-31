import { createContext, useContext, useEffect, useState } from 'react'
import type { ReactNode } from 'react'
import { getMe } from '../api/auth'
import { NetworkError } from '../api/client'

interface AuthContextValue {
  userId: string | null
  loading: boolean
  networkError: boolean
  setUserId: (id: string | null) => void
}

const AuthContext = createContext<AuthContextValue>({
  userId: null,
  loading: true,
  networkError: false,
  setUserId: () => {},
})

export function AuthProvider({ children }: { children: ReactNode }) {
  const [userId, setUserId] = useState<string | null>(null)
  const [loading, setLoading] = useState(true)
  const [networkError, setNetworkError] = useState(false)

  useEffect(() => {
    getMe()
      .then((data) => setUserId(data.id))
      .catch((err) => {
        if (err instanceof NetworkError) {
          setNetworkError(true)
        }
        setUserId(null)
      })
      .finally(() => setLoading(false))
  }, [])

  // セッション期限切れ（401）をどのページからでも検知してログアウト
  useEffect(() => {
    function handleUnauthorized() {
      setUserId(null)
    }
    window.addEventListener('kakeibo:unauthorized', handleUnauthorized)
    return () => window.removeEventListener('kakeibo:unauthorized', handleUnauthorized)
  }, [])

  return (
    <AuthContext.Provider value={{ userId, loading, networkError, setUserId }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  return useContext(AuthContext)
}
