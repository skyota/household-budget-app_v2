import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { login } from '../api/auth'
import { useAuth } from '../context/AuthContext'

export default function LoginPage() {
  const navigate = useNavigate()
  const { setUserId } = useAuth()
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(false)

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    setError(null)
    setLoading(true)
    try {
      const data = await login(username, password)
      setUserId(data.id)
      navigate('/input')
    } catch (err) {
      setError(err instanceof Error ? err.message : 'ログインに失敗しました')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen bg-parchment flex flex-col items-center justify-center px-5">
      <div className="w-full max-w-sm">
        <h1 className="font-display font-semibold text-[34px] tracking-tight text-ink text-center mb-2">
          1s-kakeibo
        </h1>
        <p className="text-center text-ink-muted text-[14px] mb-10">ログイン</p>

        <form onSubmit={handleSubmit} className="bg-canvas rounded-card p-6 flex flex-col gap-4">
          {error && (
            <p className="text-red-500 text-[14px] text-center">{error}</p>
          )}

          <div className="flex flex-col gap-1">
            <label className="text-[12px] text-ink-muted font-sans">ユーザー名</label>
            <input
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
              className="h-11 px-4 rounded-pill border border-hairline bg-canvas text-[17px] text-ink font-sans outline-none focus:border-primary transition-colors"
            />
          </div>

          <div className="flex flex-col gap-1">
            <label className="text-[12px] text-ink-muted font-sans">パスワード</label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              className="h-11 px-4 rounded-pill border border-hairline bg-canvas text-[17px] text-ink font-sans outline-none focus:border-primary transition-colors"
            />
          </div>

          <button
            type="submit"
            disabled={loading}
            className="mt-2 h-11 rounded-pill bg-primary text-white text-[17px] font-sans font-normal active:scale-95 transition-transform disabled:opacity-60"
          >
            {loading ? 'ログイン中...' : 'ログイン'}
          </button>
        </form>

        <p className="text-center text-[14px] text-ink-muted mt-6">
          アカウントをお持ちでないですか？{' '}
          <Link to="/register" className="text-primary">
            新規登録
          </Link>
        </p>
      </div>
    </div>
  )
}
