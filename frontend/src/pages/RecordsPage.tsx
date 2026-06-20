import { useCallback, useEffect, useState } from 'react'
import Toast from '../components/Toast'
import {
  listIncomeRecords, createIncomeRecord, deleteIncomeRecord,
} from '../api/incomeRecords'
import type { IncomeRecord } from '../api/incomeRecords'
import {
  listInvestRecords, createInvestRecord, deleteInvestRecord,
} from '../api/investRecords'
import type { InvestRecord } from '../types'

function todayString() {
  return new Date().toISOString().slice(0, 10)
}

function fmt(n: number) {
  return n.toLocaleString('ja-JP')
}

export default function RecordsPage() {
  const [toast, setToast] = useState<{ message: string; onUndo: () => void } | null>(null)
  const [toastKey, setToastKey] = useState(0)

  function showToast(message: string, onUndo: () => void) {
    setToast({ message, onUndo })
    setToastKey((k) => k + 1)
  }
  const dismissToast = useCallback(() => setToast(null), [])

  // ── 臨時収入 ──
  const [incomeRecords, setIncomeRecords] = useState<IncomeRecord[]>([])
  const [showIrForm, setShowIrForm] = useState(false)
  const [irTitle, setIrTitle] = useState('')
  const [irAmount, setIrAmount] = useState('')
  const [irDate, setIrDate] = useState(todayString())
  const [irMemo, setIrMemo] = useState('')
  const [irSubmitting, setIrSubmitting] = useState(false)
  const [irError, setIrError] = useState<string | null>(null)
  const [irDeleteId, setIrDeleteId] = useState<number | null>(null)

  // ── 投資履歴 ──
  const [investRecords, setInvestRecords] = useState<InvestRecord[]>([])
  const [showInvRecForm, setShowInvRecForm] = useState(false)
  const [invRecAmount, setInvRecAmount] = useState('')
  const [invRecDate, setInvRecDate] = useState(todayString())
  const [invRecType, setInvRecType] = useState<'INITIAL' | 'SPOT'>('INITIAL')
  const [invRecMemo, setInvRecMemo] = useState('')
  const [invRecSubmitting, setInvRecSubmitting] = useState(false)
  const [invRecError, setInvRecError] = useState<string | null>(null)
  const [invRecDeleteId, setInvRecDeleteId] = useState<number | null>(null)

  useEffect(() => {
    listIncomeRecords().then((r) => setIncomeRecords(r.data)).catch(() => {})
    listInvestRecords().then((r) => setInvestRecords(r.data)).catch(() => {})
  }, [])

  // ── 臨時収入ハンドラ ──
  async function handleIrSubmit() {
    const amount = parseInt(irAmount)
    if (!irTitle.trim()) { setIrError('タイトルを入力してください'); return }
    if (isNaN(amount) || amount <= 0) { setIrError('金額を正しく入力してください'); return }
    if (!irDate) { setIrError('日付を入力してください'); return }
    setIrSubmitting(true); setIrError(null)
    try {
      const created = await createIncomeRecord({ title: irTitle, amount, incomeDate: irDate, memo: irMemo || null, isRegular: false })
      setIncomeRecords((prev) => [created, ...prev])
      setShowIrForm(false); setIrTitle(''); setIrAmount(''); setIrMemo('')
    } catch (e: unknown) {
      setIrError(e instanceof Error ? e.message : '登録に失敗しました')
    } finally {
      setIrSubmitting(false)
    }
  }

  async function handleIrDelete(id: number) {
    const item = incomeRecords.find((r) => r.id === id)
    if (!item) return
    setIncomeRecords((prev) => prev.filter((r) => r.id !== id))
    setIrDeleteId(null)
    try {
      await deleteIncomeRecord(id)
      showToast('臨時収入を削除しました', async () => {
        try {
          const restored = await createIncomeRecord({ title: item.title, amount: item.amount, incomeDate: item.incomeDate, memo: item.memo, isRegular: item.isRegular })
          setIncomeRecords((prev) => [restored, ...prev])
        } catch { /* ignore */ } finally { setToast(null) }
      })
    } catch {
      setIncomeRecords((prev) => [item, ...prev])
    }
  }

  // ── 投資履歴ハンドラ ──
  async function handleInvRecSubmit() {
    const amount = parseInt(invRecAmount)
    if (isNaN(amount) || amount <= 0) { setInvRecError('金額を正しく入力してください'); return }
    if (!invRecDate) { setInvRecError('日付を入力してください'); return }
    setInvRecSubmitting(true); setInvRecError(null)
    try {
      const created = await createInvestRecord({ amount, investDate: invRecDate, investType: invRecType, memo: invRecMemo || null })
      setInvestRecords((prev) => [created, ...prev])
      setShowInvRecForm(false); setInvRecAmount(''); setInvRecMemo('')
    } catch (e: unknown) {
      setInvRecError(e instanceof Error ? e.message : '登録に失敗しました')
    } finally {
      setInvRecSubmitting(false)
    }
  }

  async function handleInvRecDelete(id: number) {
    const item = investRecords.find((r) => r.id === id)
    if (!item) return
    setInvestRecords((prev) => prev.filter((r) => r.id !== id))
    setInvRecDeleteId(null)
    try {
      await deleteInvestRecord(id)
      showToast('投資履歴を削除しました', async () => {
        try {
          const restored = await createInvestRecord({ amount: item.amount, investDate: item.investDate, investType: item.investType, memo: item.memo })
          setInvestRecords((prev) => [restored, ...prev])
        } catch { /* ignore */ } finally { setToast(null) }
      })
    } catch {
      setInvestRecords((prev) => [item, ...prev])
    }
  }

  const inputClass = 'w-full max-w-full min-w-0 h-11 rounded-lg border border-hairline bg-canvas px-3 text-[17px] text-ink placeholder-ink-muted focus:outline-none focus:border-primary'
  const dateInputClass = `${inputClass} [&::-webkit-date-and-time-value]:text-left appearance-none`

  function submitBtn(label: string, loading: boolean, onClick: () => void) {
    return (
      <button
        onClick={onClick}
        disabled={loading}
        className="flex-1 h-10 rounded-pill bg-primary text-white text-[14px] active:scale-95 transition-transform disabled:opacity-60"
      >
        {loading ? '登録中...' : label}
      </button>
    )
  }

  return (
    <div className="px-5 pt-6 pb-32 max-w-lg mx-auto overflow-x-hidden">
      <h1 className="text-[28px] font-semibold text-ink tracking-tight mb-8">記録</h1>

      {/* ── 臨時収入 ── */}
      <section className="mb-8">
        <div className="flex items-center justify-between mb-3">
          <h2 className="text-[17px] font-semibold text-ink">臨時収入</h2>
          <button
            onClick={() => { setShowIrForm(!showIrForm); setIrError(null) }}
            className="text-[14px] text-primary font-semibold active:opacity-60"
          >
            {showIrForm ? '－ 閉じる' : '＋ 追加'}
          </button>
        </div>
        <p className="text-[13px] text-ink-muted mb-3 leading-relaxed">
          ボーナス・副業収入などの臨時収入を記録します。
        </p>

        {showIrForm && (
          <div className="bg-parchment rounded-card px-4 py-4 mb-3 space-y-3 overflow-hidden">
            <input className={inputClass} type="text" placeholder="タイトル（例：ボーナス）" value={irTitle} onChange={(e) => setIrTitle(e.target.value)} />
            <input className={inputClass} type="number" placeholder="金額（円）" value={irAmount} onChange={(e) => setIrAmount(e.target.value)} />
            <input className={dateInputClass} type="date" value={irDate} onChange={(e) => setIrDate(e.target.value)} />
            <input className={inputClass} type="text" placeholder="メモ（任意）" value={irMemo} onChange={(e) => setIrMemo(e.target.value)} />
            {irError && <p className="text-[13px] text-red-500">{irError}</p>}
            <div className="flex gap-2">
              <button onClick={() => setShowIrForm(false)} className="flex-1 h-10 rounded-pill border border-hairline text-ink text-[14px] active:scale-95 transition-transform">キャンセル</button>
              {submitBtn('登録する', irSubmitting, handleIrSubmit)}
            </div>
          </div>
        )}

        {incomeRecords.length === 0 ? (
          <p className="text-[14px] text-ink-muted text-center py-4">臨時収入がありません</p>
        ) : (
          <div className="space-y-2">
            {incomeRecords.slice(0, 10).map((r) => (
              <div key={r.id} className="bg-parchment rounded-lg px-4 py-3">
                {irDeleteId === r.id ? (
                  <div className="flex items-center justify-between gap-3">
                    <p className="text-[13px] text-ink">削除しますか？</p>
                    <div className="flex gap-2">
                      <button onClick={() => setIrDeleteId(null)} className="text-[13px] text-ink-muted px-3 py-1 rounded-pill border border-hairline">キャンセル</button>
                      <button onClick={() => handleIrDelete(r.id)} className="text-[13px] text-white bg-red-500 px-3 py-1 rounded-pill">削除</button>
                    </div>
                  </div>
                ) : (
                  <div className="flex items-center justify-between">
                    <div>
                      <p className="text-[17px] text-ink">{r.title}</p>
                      <p className="text-[12px] text-ink-muted">{r.incomeDate}　{fmt(r.amount)}円{r.memo ? `　${r.memo}` : ''}</p>
                    </div>
                    <button onClick={() => setIrDeleteId(r.id)} className="text-[13px] text-ink-muted px-2 py-1">削除</button>
                  </div>
                )}
              </div>
            ))}
            {incomeRecords.length > 10 && (
              <p className="text-[13px] text-ink-muted text-center pt-2">他 {incomeRecords.length - 10} 件</p>
            )}
          </div>
        )}
      </section>

      {/* ── 投資履歴 ── */}
      <section className="mb-8">
        <div className="flex items-center justify-between mb-3">
          <h2 className="text-[17px] font-semibold text-ink">投資履歴</h2>
          <button
            onClick={() => { setShowInvRecForm(!showInvRecForm); setInvRecError(null) }}
            className="text-[14px] text-primary font-semibold active:opacity-60"
          >
            {showInvRecForm ? '－ 閉じる' : '＋ 追加（スポット）'}
          </button>
        </div>
        <p className="text-[13px] text-ink-muted mb-3 leading-relaxed">
          初期元本や一時的な追加投資を記録します。
        </p>

        {showInvRecForm && (
          <div className="bg-parchment rounded-card px-4 py-4 mb-3 space-y-3 overflow-hidden">
            <select className={inputClass} value={invRecType} onChange={(e) => setInvRecType(e.target.value as 'INITIAL' | 'SPOT')}>
              <option value="INITIAL">初期元本（アプリ開始前のNISA残高）</option>
              <option value="SPOT">スポット追加</option>
            </select>
            <input className={inputClass} type="number" placeholder="投資金額（円）" value={invRecAmount} onChange={(e) => setInvRecAmount(e.target.value)} />
            <input className={dateInputClass} type="date" value={invRecDate} onChange={(e) => setInvRecDate(e.target.value)} />
            <input className={inputClass} type="text" placeholder="メモ（任意）" value={invRecMemo} onChange={(e) => setInvRecMemo(e.target.value)} />
            {invRecError && <p className="text-[13px] text-red-500">{invRecError}</p>}
            <div className="flex gap-2">
              <button onClick={() => setShowInvRecForm(false)} className="flex-1 h-10 rounded-pill border border-hairline text-ink text-[14px] active:scale-95 transition-transform">キャンセル</button>
              {submitBtn('登録する', invRecSubmitting, handleInvRecSubmit)}
            </div>
          </div>
        )}

        {investRecords.length === 0 ? (
          <p className="text-[14px] text-ink-muted text-center py-4">投資履歴がありません</p>
        ) : (
          <div className="space-y-2">
            {investRecords.slice(0, 10).map((r) => (
              <div key={r.id} className="bg-parchment rounded-lg px-4 py-3">
                {invRecDeleteId === r.id ? (
                  <div className="flex items-center justify-between gap-3">
                    <p className="text-[13px] text-ink">削除しますか？</p>
                    <div className="flex gap-2">
                      <button onClick={() => setInvRecDeleteId(null)} className="text-[13px] text-ink-muted px-3 py-1 rounded-pill border border-hairline">キャンセル</button>
                      <button onClick={() => handleInvRecDelete(r.id)} className="text-[13px] text-white bg-red-500 px-3 py-1 rounded-pill">削除</button>
                    </div>
                  </div>
                ) : (
                  <div className="flex items-center justify-between">
                    <div>
                      <p className="text-[17px] font-semibold text-ink">{fmt(r.amount)}円</p>
                      <p className="text-[12px] text-ink-muted">
                        {r.investDate}
                        {{ INITIAL: '初期元本', REGULAR: '月次積立', SPOT: 'スポット' }[r.investType]}
                        {r.memo ? `　${r.memo}` : ''}
                      </p>
                    </div>
                    <button onClick={() => setInvRecDeleteId(r.id)} className="text-[13px] text-ink-muted px-2 py-1">削除</button>
                  </div>
                )}
              </div>
            ))}
            {investRecords.length > 10 && (
              <p className="text-[13px] text-ink-muted text-center pt-2">他 {investRecords.length - 10} 件</p>
            )}
          </div>
        )}
      </section>

      {toast && (
        <Toast key={toastKey} message={toast.message} onUndo={toast.onUndo} onDismiss={dismissToast} />
      )}
    </div>
  )
}
