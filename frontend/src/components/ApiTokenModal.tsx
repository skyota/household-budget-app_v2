import { useEffect, useState } from 'react'
import { getApiToken } from '../api/auth'

interface Props {
  onClose: () => void
}

export default function ApiTokenModal({ onClose }: Props) {
  const [token, setToken] = useState<string | null>(null)
  const [copied, setCopied] = useState(false)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    getApiToken()
      .then((res) => setToken(res.token))
      .catch(() => setError('トークンの取得に失敗しました'))
  }, [])

  async function handleCopy() {
    if (!token) return
    await navigator.clipboard.writeText(token)
    setCopied(true)
    setTimeout(() => setCopied(false), 2000)
  }

  return (
    <div className="fixed inset-0 z-50 flex items-end justify-center" onClick={onClose}>
      <div className="absolute inset-0 bg-black/40" />
      <div
        className="relative w-full max-w-lg bg-canvas rounded-t-2xl px-5 pt-6 pb-10"
        onClick={(e) => e.stopPropagation()}
      >
        <div className="flex items-center justify-between mb-5">
          <h2 className="text-[21px] font-semibold text-ink tracking-tight">ショートカット連携</h2>
          <button
            onClick={onClose}
            className="w-8 h-8 flex items-center justify-center rounded-full bg-canvas-parchment text-ink-muted"
          >
            <svg width="14" height="14" viewBox="0 0 14 14" fill="none">
              <path d="M1 1l12 12M13 1L1 13" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" />
            </svg>
          </button>
        </div>

        <p className="text-[14px] text-ink-muted mb-4 leading-relaxed">
          iPhoneのショートカットアプリからAPIトークンを使って支出を直接記録できます。
          このトークンは他人に教えないでください。
        </p>

        {error && <p className="text-[14px] text-red-500 mb-4">{error}</p>}

        {token ? (
          <>
            <div className="bg-canvas-parchment rounded-lg px-4 py-3 mb-4 break-all">
              <p className="text-[13px] font-mono text-ink">{token}</p>
            </div>
            <button
              onClick={handleCopy}
              className="w-full h-11 rounded-pill bg-primary text-white text-[17px] font-sans active:scale-95 transition-transform"
            >
              {copied ? 'コピーしました！' : 'トークンをコピー'}
            </button>
          </>
        ) : !error ? (
          <div className="flex justify-center py-4">
            <div className="w-6 h-6 border-2 border-primary border-t-transparent rounded-full animate-spin" />
          </div>
        ) : null}

        <div className="mt-5 bg-canvas-parchment rounded-lg px-4 py-3">
          <p className="text-[13px] text-ink-muted font-semibold mb-2">ショートカットの設定方法</p>
          <ol className="text-[13px] text-ink-muted space-y-1 list-decimal list-inside leading-relaxed">
            <li>ショートカットアプリ → 新規ショートカット</li>
            <li>「入力を要求」→ 数字</li>
            <li>「URLの内容を取得」を追加</li>
            <li>URL: <span className="font-mono">https://household-budget-app-v2.onrender.com/quick/expense</span></li>
            <li>方法: POST / ヘッダー: Authorization: Bearer &lt;トークン&gt;</li>
            <li>本文(JSON): {`{"price": [入力した数字]}`}</li>
          </ol>
        </div>
      </div>
    </div>
  )
}
