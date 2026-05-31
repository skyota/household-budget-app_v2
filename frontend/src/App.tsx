import { Navigate, Route, Routes } from 'react-router-dom'
import { useAuth } from './context/AuthContext'
import AuthLayout from './components/AuthLayout'
import LoginPage from './pages/LoginPage'
import RegisterPage from './pages/RegisterPage'
import InputPage from './pages/InputPage'
import HistoryPage from './pages/HistoryPage'
import type { ReactNode } from 'react'

function ProtectedRoute({ children }: { children: ReactNode }) {
  const { userId, loading, networkError } = useAuth()

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-canvas">
        <div className="w-8 h-8 border-2 border-primary border-t-transparent rounded-full animate-spin" />
      </div>
    )
  }

  if (networkError) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-canvas px-5">
        <div className="text-center">
          <p className="text-[17px] text-ink mb-4">サーバーに接続できません</p>
          <p className="text-[14px] text-ink-muted mb-6">接続を確認してページを再読み込みしてください</p>
          <button
            onClick={() => window.location.reload()}
            className="h-11 px-8 rounded-pill bg-primary text-white text-[17px] font-sans active:scale-95 transition-transform"
          >
            再読み込み
          </button>
        </div>
      </div>
    )
  }

  if (!userId) {
    return <Navigate to="/login" replace />
  }

  return <>{children}</>
}

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route
        path="/input"
        element={
          <ProtectedRoute>
            <AuthLayout>
              <InputPage />
            </AuthLayout>
          </ProtectedRoute>
        }
      />
      <Route
        path="/history"
        element={
          <ProtectedRoute>
            <AuthLayout>
              <HistoryPage />
            </AuthLayout>
          </ProtectedRoute>
        }
      />
      <Route path="/" element={<Navigate to="/input" replace />} />
    </Routes>
  )
}
