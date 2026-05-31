import { useEffect, useState } from 'react'
import { getApiToken, regenerateApiToken } from '../api/auth'

const SHORTCUT_URL = 'https://www.icloud.com/shortcuts/b6bf13da7e224db2924ee8911d862a09'

export default function MyPage() {
  const [token, setToken] = useState<string | null>(null)
  const [copied, setCopied] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [regenerating, setRegenerating] = useState(false)
  const [showConfirm, setShowConfirm] = useState(false)

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

  async function handleRegenerate() {
    setRegenerating(true)
    setError(null)
    try {
      const res = await regenerateApiToken()
      setToken(res.token)
      setShowConfirm(false)
    } catch {
      setError('トークンの再生成に失敗しました')
    } finally {
      setRegenerating(false)
    }
  }

  return (
    <div className="px-5 pt-6 pb-32 max-w-lg mx-auto">
      <h1 className="text-[28px] font-semibold text-ink tracking-tight mb-8">マイページ</h1>

      {/* ショートカット連携セクション */}
      <section className="mb-8">
        <h2 className="text-[17px] font-semibold text-ink mb-1">ショートカット連携</h2>
        <p className="text-[14px] text-ink-muted mb-4 leading-relaxed">
          iPhoneのショートカットアプリから支出をブラウザを開かずに記録できます。
        </p>

        {error && (
          <p className="text-[14px] text-red-500 mb-4">{error}</p>
        )}

        {/* APIトークン表示 */}
        <div className="bg-canvas-parchment rounded-lg px-4 py-3 mb-3">
          <p className="text-[12px] text-ink-muted mb-1 font-semibold">APIトークン</p>
          {token ? (
            <p className="text-[13px] font-mono text-ink break-all">{token}</p>
          ) : !error ? (
            <div className="flex justify-center py-2">
              <div className="w-5 h-5 border-2 border-primary border-t-transparent rounded-full animate-spin" />
            </div>
          ) : null}
        </div>

        <button
          onClick={handleCopy}
          disabled={!token}
          className="w-full h-11 rounded-pill bg-primary text-white text-[17px] font-sans active:scale-95 transition-transform mb-3 disabled:opacity-40"
        >
          {copied ? 'コピーしました！' : 'トークンをコピー'}
        </button>

        {/* 再生成 */}
        {!showConfirm ? (
          <button
            onClick={() => setShowConfirm(true)}
            disabled={!token}
            className="w-full h-11 rounded-pill border border-hairline bg-canvas text-ink text-[17px] font-sans active:scale-95 transition-transform disabled:opacity-40"
          >
            トークンを再生成
          </button>
        ) : (
          <div className="bg-canvas-parchment rounded-lg px-4 py-4">
            <p className="text-[14px] text-ink mb-3">
              再生成すると古いトークンは使えなくなります。ショートカットアプリの設定も更新が必要です。本当に再生成しますか？
            </p>
            <div className="flex gap-3">
              <button
                onClick={() => setShowConfirm(false)}
                className="flex-1 h-10 rounded-pill border border-hairline bg-canvas text-ink text-[14px] font-sans active:scale-95 transition-transform"
              >
                キャンセル
              </button>
              <button
                onClick={handleRegenerate}
                disabled={regenerating}
                className="flex-1 h-10 rounded-pill bg-red-500 text-white text-[14px] font-sans active:scale-95 transition-transform disabled:opacity-40"
              >
                {regenerating ? '再生成中...' : '再生成する'}
              </button>
            </div>
          </div>
        )}

        {/* 注意書き */}
        <div className="mt-4 bg-canvas-parchment rounded-lg px-4 py-3">
          <p className="text-[13px] text-ink-muted font-semibold mb-1">⚠️ セキュリティについて</p>
          <ul className="text-[13px] text-ink-muted space-y-1 list-disc list-inside leading-relaxed">
            <li>トークンは他人に教えないでください</li>
            <li>定期的にトークンを再生成することを推奨します</li>
            <li>漏れた場合はすぐに再生成してください</li>
          </ul>
        </div>

        {/* ショートカット設定方法 */}
        <div className="mt-4 bg-canvas-parchment rounded-lg px-4 py-3">
          <p className="text-[13px] text-ink-muted font-semibold mb-2">ショートカットの使い方</p>
          <ol className="text-[13px] text-ink-muted space-y-2 list-decimal list-inside leading-relaxed">
            <li>
              下のボタンからショートカットを入手する
            </li>
            <li>
              ショートカット内の{' '}
              <span className="font-mono bg-canvas rounded px-1">&lt;トークン&gt;</span>{' '}
              と書かれた箇所（2か所）を、上のAPIトークンに書き換える
            </li>
            <li>ショートカットを実行してカテゴリ・日付・タイトル・金額を入力する</li>
          </ol>
          <a
            href={SHORTCUT_URL}
            target="_blank"
            rel="noopener noreferrer"
            className="mt-4 flex items-center justify-center w-full h-11 rounded-pill bg-primary text-white text-[17px] font-sans active:scale-95 transition-transform"
          >
            ショートカットを入手する
          </a>
        </div>
      </section>
    </div>
  )
}
