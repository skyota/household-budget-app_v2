import { createContext, useContext, useEffect, useState } from 'react'
import type { ReactNode } from 'react'
import { getMe } from '../api/auth'

interface AuthContextValue {
  userId: string | null
  loading: boolean
  setUserId: (id: string | null) => void
}

const AuthContext = createContext<AuthContextValue>({
  userId: null,
  loading: true,
  setUserId: () => {},
})

export function AuthProvider({ children }: { children: ReactNode }) {
  const [userId, setUserId] = useState<string | null>(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    getMe()
      .then((data) => setUserId(data.id))
      .catch(() => setUserId(null))
      .finally(() => setLoading(false))
  }, [])

  return (
    <AuthContext.Provider value={{ userId, loading, setUserId }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  return useContext(AuthContext)
}
