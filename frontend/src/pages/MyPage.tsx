import { useCallback, useEffect, useState } from 'react'
import Toast from '../components/Toast'
import { getApiToken, regenerateApiToken } from '../api/auth'
import {
  listFixedExpenses, createFixedExpense, deleteFixedExpense,
} from '../api/fixedExpenses'
import {
  listIncomeSettings, createIncomeSetting, deleteIncomeSetting,
} from '../api/incomeSettings'
import {
  getInvestSetting, createInvestSetting, updateInvestSetting,
} from '../api/investSettings'
import {
  listCashBalances, createCashBalance, deleteCashBalance,
} from '../api/cashBalances'
import type {
  FixedExpense, IncomeSetting, InvestSetting,
  CashBalance, BalanceType,
} from '../types'

const SHORTCUT_URL = 'https://www.icloud.com/shortcuts/b6bf13da7e224db2924ee8911d862a09'

function todayString() {
  return new Date().toISOString().slice(0, 10)
}

function fmt(n: number) {
  return n.toLocaleString('ja-JP')
}

export default function MyPage() {
  // ── トースト ──
  const [toast, setToast] = useState<{ message: string; onUndo: () => void } | null>(null)
  const [toastKey, setToastKey] = useState(0)

  function showToast(message: string, onUndo: () => void) {
    setToast({ message, onUndo })
    setToastKey((k) => k + 1)
  }

  const dismissToast = useCallback(() => setToast(null), [])

  // ── APIトークン ──
  const [token, setToken] = useState<string | null>(null)
  const [copied, setCopied] = useState(false)
  const [tokenError, setTokenError] = useState<string | null>(null)
  const [regenerating, setRegenerating] = useState(false)
  const [showConfirm, setShowConfirm] = useState(false)

  // ── 現金残高 ──
  const [balances, setBalances] = useState<CashBalance[]>([])
  const [showBalanceForm, setShowBalanceForm] = useState(false)
  const [balAmount, setBalAmount] = useState('')
  const [balDate, setBalDate] = useState(todayString())
  const [balType, setBalType] = useState<BalanceType>('INITIAL')
  const [balMemo, setBalMemo] = useState('')
  const [balSubmitting, setBalSubmitting] = useState(false)
  const [balError, setBalError] = useState<string | null>(null)
  const [balDeleteId, setBalDeleteId] = useState<number | null>(null)

  // ── 固定費 ──
  const [fixedExpenses, setFixedExpenses] = useState<FixedExpense[]>([])
  const [showFeForm, setShowFeForm] = useState(false)
  const [feTitle, setFeTitle] = useState('')
  const [fePrice, setFePrice] = useState('')
  const [feDate, setFeDate] = useState('')
  const [feMemo, setFeMemo] = useState('')
  const [feSubmitting, setFeSubmitting] = useState(false)
  const [feError, setFeError] = useState<string | null>(null)
  const [feDeleteId, setFeDeleteId] = useState<number | null>(null)

  // ── 収入設定 ──
  const [incomeSettings, setIncomeSettings] = useState<IncomeSetting[]>([])
  const [showIsForm, setShowIsForm] = useState(false)
  const [isTitle, setIsTitle] = useState('')
  const [isAmount, setIsAmount] = useState('')
  const [isDate, setIsDate] = useState('')
  const [isMemo, setIsMemo] = useState('')
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [isError, setIsError] = useState<string | null>(null)
  const [isDeleteId, setIsDeleteId] = useState<number | null>(null)

  // ── 投資設定 ──
  const [investSetting, setInvestSetting] = useState<InvestSetting | null>(null)
  const [investSettingExists, setInvestSettingExists] = useState(false)
  const [showInvSetForm, setShowInvSetForm] = useState(false)
  const [invSetAmount, setInvSetAmount] = useState('')
  const [invSetDate, setInvSetDate] = useState('')
  const [invSetSubmitting, setInvSetSubmitting] = useState(false)
  const [invSetError, setInvSetError] = useState<string | null>(null)

  useEffect(() => {
    getApiToken().then((r) => setToken(r.token)).catch(() => setTokenError('トークンの取得に失敗しました'))
    listCashBalances().then((r) => setBalances(r.data)).catch(() => {})
    listFixedExpenses().then((r) => setFixedExpenses(r.data)).catch(() => {})
    listIncomeSettings().then((r) => setIncomeSettings(r.data)).catch(() => {})
    getInvestSetting()
      .then((s) => { setInvestSetting(s); setInvestSettingExists(true) })
      .catch(() => setInvestSettingExists(false))
  }, [])

  // ── APIトークン ──
  async function handleCopy() {
    if (!token) return
    await navigator.clipboard.writeText(token)
    setCopied(true)
    setTimeout(() => setCopied(false), 2000)
  }

  async function handleRegenerate() {
    setRegenerating(true)
    try {
      const res = await regenerateApiToken()
      setToken(res.token)
      setShowConfirm(false)
    } catch {
      setTokenError('トークンの再生成に失敗しました')
    } finally {
      setRegenerating(false)
    }
  }

  // ── 現金残高 ──
  async function handleBalSubmit() {
    const amount = parseInt(balAmount)
    if (isNaN(amount) || amount < 0) { setBalError('金額を正しく入力してください'); return }
    if (!balDate) { setBalError('日付を入力してください'); return }
    setBalSubmitting(true); setBalError(null)
    try {
      const created = await createCashBalance({ amount, balanceDate: balDate, balanceType: balType, memo: balMemo || null })
      setBalances((prev) => [created, ...prev])
      setShowBalanceForm(false); setBalAmount(''); setBalMemo('')
    } catch (e: unknown) {
      setBalError(e instanceof Error ? e.message : '登録に失敗しました')
    } finally {
      setBalSubmitting(false)
    }
  }

  async function handleBalDelete(id: number) {
    const item = balances.find((b) => b.id === id)
    if (!item) return
    setBalances((prev) => prev.filter((b) => b.id !== id))
    setBalDeleteId(null)
    try {
      await deleteCashBalance(id)
      showToast('現金残高を削除しました', async () => {
        try {
          const restored = await createCashBalance({ amount: item.amount, balanceDate: item.balanceDate, balanceType: item.balanceType, memo: item.memo })
          setBalances((prev) => [restored, ...prev])
        } catch { /* ignore */ } finally { setToast(null) }
      })
    } catch {
      setBalances((prev) => [item, ...prev])
    }
  }

  // ── 固定費 ──
  async function handleFeSubmit() {
    const price = parseInt(fePrice)
    const date = parseInt(feDate)
    if (!feTitle.trim()) { setFeError('タイトルを入力してください'); return }
    if (isNaN(price) || price < 0) { setFeError('金額を正しく入力してください'); return }
    if (isNaN(date) || date < 1 || date > 31) { setFeError('支払日は1〜31で入力してください'); return }
    setFeSubmitting(true); setFeError(null)
    try {
      const created = await createFixedExpense({ title: feTitle, price, fixedExpenseDate: date, memo: feMemo || null })
      setFixedExpenses((prev) => [...prev, created].sort((a, b) => a.fixedExpenseDate - b.fixedExpenseDate))
      setShowFeForm(false); setFeTitle(''); setFePrice(''); setFeDate(''); setFeMemo('')
    } catch (e: unknown) {
      setFeError(e instanceof Error ? e.message : '登録に失敗しました')
    } finally {
      setFeSubmitting(false)
    }
  }

  async function handleFeDelete(id: number) {
    const item = fixedExpenses.find((f) => f.id === id)
    if (!item) return
    setFixedExpenses((prev) => prev.filter((f) => f.id !== id))
    setFeDeleteId(null)
    try {
      await deleteFixedExpense(id)
      showToast('固定費を削除しました', async () => {
        try {
          const restored = await createFixedExpense({ title: item.title, price: item.price, fixedExpenseDate: item.fixedExpenseDate, memo: item.memo })
          setFixedExpenses((prev) => [...prev, restored].sort((a, b) => a.fixedExpenseDate - b.fixedExpenseDate))
        } catch { /* ignore */ } finally { setToast(null) }
      })
    } catch {
      setFixedExpenses((prev) => [...prev, item].sort((a, b) => a.fixedExpenseDate - b.fixedExpenseDate))
    }
  }

  // ── 収入設定 ──
  async function handleIsSubmit() {
    const amount = parseInt(isAmount)
    const date = parseInt(isDate)
    if (!isTitle.trim()) { setIsError('タイトルを入力してください'); return }
    if (isNaN(amount) || amount < 0) { setIsError('金額を正しく入力してください'); return }
    if (isNaN(date) || date < 1 || date > 31) { setIsError('給料日は1〜31で入力してください'); return }
    setIsSubmitting(true); setIsError(null)
    try {
      const created = await createIncomeSetting({ title: isTitle, amount, incomeDate: date, memo: isMemo || null, isAutoGenerate: true })
      setIncomeSettings((prev) => [...prev, created])
      setShowIsForm(false); setIsTitle(''); setIsAmount(''); setIsDate(''); setIsMemo('')
    } catch (e: unknown) {
      setIsError(e instanceof Error ? e.message : '登録に失敗しました')
    } finally {
      setIsSubmitting(false)
    }
  }

  async function handleIsDelete(id: number) {
    const item = incomeSettings.find((s) => s.id === id)
    if (!item) return
    setIncomeSettings((prev) => prev.filter((s) => s.id !== id))
    setIsDeleteId(null)
    try {
      await deleteIncomeSetting(id)
      showToast('収入設定を削除しました', async () => {
        try {
          const restored = await createIncomeSetting({ title: item.title, amount: item.amount, incomeDate: item.incomeDate, memo: item.memo, isAutoGenerate: item.isAutoGenerate })
          setIncomeSettings((prev) => [...prev, restored])
        } catch { /* ignore */ } finally { setToast(null) }
      })
    } catch {
      setIncomeSettings((prev) => [...prev, item])
    }
  }

  // ── 投資設定 ──
  async function handleInvSetSubmit() {
    const amount = parseInt(invSetAmount)
    const date = parseInt(invSetDate)
    if (isNaN(amount) || amount < 0) { setInvSetError('金額を正しく入力してください'); return }
    if (isNaN(date) || date < 1 || date > 31) { setInvSetError('積立日は1〜31で入力してください'); return }
    setInvSetSubmitting(true); setInvSetError(null)
    try {
      const result = investSettingExists
        ? await updateInvestSetting({ amount, investDate: date })
        : await createInvestSetting({ amount, investDate: date })
      setInvestSetting(result)
      setInvestSettingExists(true)
      setShowInvSetForm(false)
    } catch (e: unknown) {
      setInvSetError(e instanceof Error ? e.message : '保存に失敗しました')
    } finally {
      setInvSetSubmitting(false)
    }
  }

  const sectionTitle = (title: string) => (
    <h2 className="text-[17px] font-semibold text-ink mb-3">{title}</h2>
  )



  const inputClass = 'w-full max-w-full min-w-0 h-11 rounded-lg border border-hairline bg-canvas px-3 text-[17px] text-ink placeholder-ink-muted focus:outline-none focus:border-primary'
  const dateInputClass = `${inputClass} [&::-webkit-date-and-time-value]:text-left appearance-none`
  const submitBtn = (label: string, loading: boolean, onClick: () => void) => (
    <button
      onClick={onClick}
      disabled={loading}
      className="flex-1 h-11 rounded-pill bg-primary text-white text-[17px] font-sans active:scale-95 transition-transform disabled:opacity-40"
    >
      {loading ? '保存中...' : label}
    </button>
  )

  return (
    <div className="px-5 pt-6 pb-32 max-w-lg mx-auto overflow-x-hidden">
      <h1 className="text-[28px] font-semibold text-ink tracking-tight mb-8">マイページ</h1>

      {/* ── 現金残高 ── */}
      <section className="mb-8">
        <div className="flex items-center justify-between mb-3">
          {sectionTitle('現金残高')}
          <button
            onClick={() => { setShowBalanceForm(!showBalanceForm); setBalError(null) }}
            className="text-[14px] text-primary font-semibold active:opacity-60"
          >
            {showBalanceForm ? '－ 閉じる' : '＋ 残高を登録'}
          </button>
        </div>
        <p className="text-[13px] text-ink-muted mb-3 leading-relaxed">
          現在の銀行・現金の合計を登録します。この基準点から収支を加算して現在の残高を計算します。
        </p>

        {showBalanceForm && (
          <div className="bg-parchment rounded-card px-4 py-4 mb-3 space-y-3 overflow-hidden">
            <input className={inputClass} type="number" placeholder="金額（円）" value={balAmount} onChange={(e) => setBalAmount(e.target.value)} />
            <input className={dateInputClass} type="date" value={balDate} onChange={(e) => setBalDate(e.target.value)} />
            <select className={inputClass} value={balType} onChange={(e) => setBalType(e.target.value as BalanceType)}>
              <option value="INITIAL">初期設定</option>
              <option value="ADJUSTMENT">手動調整</option>
            </select>
            <input className={inputClass} type="text" placeholder="メモ（任意）" value={balMemo} onChange={(e) => setBalMemo(e.target.value)} />
            {balError && <p className="text-[13px] text-red-500">{balError}</p>}
            <div className="flex gap-2">
              <button onClick={() => setShowBalanceForm(false)} className="flex-1 h-10 rounded-pill border border-hairline text-ink text-[14px] active:scale-95 transition-transform">キャンセル</button>
              {submitBtn('登録する', balSubmitting, handleBalSubmit)}
            </div>
          </div>
        )}

        {balances.length === 0 ? (
          <p className="text-[14px] text-ink-muted text-center py-4">残高が登録されていません</p>
        ) : (
          <div className="space-y-2">
            {balances.slice(0, 5).map((b) => (
              <div key={b.id} className="bg-parchment rounded-lg px-4 py-3">
                {balDeleteId === b.id ? (
                  <div className="flex items-center justify-between gap-3">
                    <p className="text-[13px] text-ink">削除しますか？</p>
                    <div className="flex gap-2">
                      <button onClick={() => setBalDeleteId(null)} className="text-[13px] text-ink-muted px-3 py-1 rounded-pill border border-hairline">キャンセル</button>
                      <button onClick={() => handleBalDelete(b.id)} className="text-[13px] text-white bg-red-500 px-3 py-1 rounded-pill">削除</button>
                    </div>
                  </div>
                ) : (
                  <div className="flex items-center justify-between">
                    <div>
                      <p className="text-[17px] font-semibold text-ink">{fmt(b.amount)}円</p>
                      <p className="text-[12px] text-ink-muted">{b.balanceDate}　{b.balanceType === 'INITIAL' ? '初期設定' : '手動調整'}{b.memo ? `　${b.memo}` : ''}</p>
                    </div>
                    <button onClick={() => setBalDeleteId(b.id)} className="text-[13px] text-ink-muted px-2 py-1">削除</button>
                  </div>
                )}
              </div>
            ))}
          </div>
        )}
      </section>

      {/* ── 固定費 ── */}
      <section className="mb-8">
        <div className="flex items-center justify-between mb-3">
          {sectionTitle('固定費')}
          <button
            onClick={() => { setShowFeForm(!showFeForm); setFeError(null) }}
            className="text-[14px] text-primary font-semibold active:opacity-60"
          >
            {showFeForm ? '－ 閉じる' : '＋ 追加'}
          </button>
        </div>
        <p className="text-[13px] text-ink-muted mb-3 leading-relaxed">
          毎月かかる固定の支出（家賃・サブスク等）を登録します。
        </p>

        {showFeForm && (
          <div className="bg-parchment rounded-card px-4 py-4 mb-3 space-y-3 overflow-hidden">
            <input className={inputClass} type="text" placeholder="タイトル（例：家賃）" value={feTitle} onChange={(e) => setFeTitle(e.target.value)} />
            <input className={inputClass} type="number" placeholder="金額（円）" value={fePrice} onChange={(e) => setFePrice(e.target.value)} />
            <input className={inputClass} type="number" placeholder="支払日（1〜31）" value={feDate} onChange={(e) => setFeDate(e.target.value)} />
            <input className={inputClass} type="text" placeholder="メモ（任意）" value={feMemo} onChange={(e) => setFeMemo(e.target.value)} />
            {feError && <p className="text-[13px] text-red-500">{feError}</p>}
            <div className="flex gap-2">
              <button onClick={() => setShowFeForm(false)} className="flex-1 h-10 rounded-pill border border-hairline text-ink text-[14px] active:scale-95 transition-transform">キャンセル</button>
              {submitBtn('登録する', feSubmitting, handleFeSubmit)}
            </div>
          </div>
        )}

        {fixedExpenses.length === 0 ? (
          <p className="text-[14px] text-ink-muted text-center py-4">固定費が登録されていません</p>
        ) : (
          <div className="space-y-2">
            {fixedExpenses.map((fe) => (
              <div key={fe.id} className="bg-parchment rounded-lg px-4 py-3">
                {feDeleteId === fe.id ? (
                  <div className="flex items-center justify-between gap-3">
                    <p className="text-[13px] text-ink">削除しますか？</p>
                    <div className="flex gap-2">
                      <button onClick={() => setFeDeleteId(null)} className="text-[13px] text-ink-muted px-3 py-1 rounded-pill border border-hairline">キャンセル</button>
                      <button onClick={() => handleFeDelete(fe.id)} className="text-[13px] text-white bg-red-500 px-3 py-1 rounded-pill">削除</button>
                    </div>
                  </div>
                ) : (
                  <div className="flex items-center justify-between">
                    <div>
                      <p className="text-[17px] text-ink">{fe.title}</p>
                      <p className="text-[12px] text-ink-muted">毎月{fe.fixedExpenseDate}日　{fmt(fe.price)}円{fe.memo ? `　${fe.memo}` : ''}</p>
                    </div>
                    <button onClick={() => setFeDeleteId(fe.id)} className="text-[13px] text-ink-muted px-2 py-1">削除</button>
                  </div>
                )}
              </div>
            ))}
          </div>
        )}
      </section>

      {/* ── 収入設定 ── */}
      <section className="mb-8">
        <div className="flex items-center justify-between mb-3">
          {sectionTitle('収入設定')}
          <button
            onClick={() => { setShowIsForm(!showIsForm); setIsError(null) }}
            className="text-[14px] text-primary font-semibold active:opacity-60"
          >
            {showIsForm ? '－ 閉じる' : '＋ 追加'}
          </button>
        </div>
        <p className="text-[13px] text-ink-muted mb-3 leading-relaxed">
          毎月の定期収入（給与・副業等）を登録します。
        </p>

        {showIsForm && (
          <div className="bg-parchment rounded-card px-4 py-4 mb-3 space-y-3 overflow-hidden">
            <input className={inputClass} type="text" placeholder="タイトル（例：本業の給与）" value={isTitle} onChange={(e) => setIsTitle(e.target.value)} />
            <input className={inputClass} type="number" placeholder="金額（円）" value={isAmount} onChange={(e) => setIsAmount(e.target.value)} />
            <input className={inputClass} type="number" placeholder="給料日（1〜31）" value={isDate} onChange={(e) => setIsDate(e.target.value)} />
            <input className={inputClass} type="text" placeholder="メモ（任意）" value={isMemo} onChange={(e) => setIsMemo(e.target.value)} />
            {isError && <p className="text-[13px] text-red-500">{isError}</p>}
            <div className="flex gap-2">
              <button onClick={() => setShowIsForm(false)} className="flex-1 h-10 rounded-pill border border-hairline text-ink text-[14px] active:scale-95 transition-transform">キャンセル</button>
              {submitBtn('登録する', isSubmitting, handleIsSubmit)}
            </div>
          </div>
        )}

        {incomeSettings.length === 0 ? (
          <p className="text-[14px] text-ink-muted text-center py-4">収入設定が登録されていません</p>
        ) : (
          <div className="space-y-2">
            {incomeSettings.map((s) => (
              <div key={s.id} className="bg-parchment rounded-lg px-4 py-3">
                {isDeleteId === s.id ? (
                  <div className="flex items-center justify-between gap-3">
                    <p className="text-[13px] text-ink">削除しますか？</p>
                    <div className="flex gap-2">
                      <button onClick={() => setIsDeleteId(null)} className="text-[13px] text-ink-muted px-3 py-1 rounded-pill border border-hairline">キャンセル</button>
                      <button onClick={() => handleIsDelete(s.id)} className="text-[13px] text-white bg-red-500 px-3 py-1 rounded-pill">削除</button>
                    </div>
                  </div>
                ) : (
                  <div className="flex items-center justify-between">
                    <div>
                      <p className="text-[17px] text-ink">{s.title}</p>
                      <p className="text-[12px] text-ink-muted">毎月{s.incomeDate}日　{fmt(s.amount)}円{s.memo ? `　${s.memo}` : ''}</p>
                    </div>
                    <button onClick={() => setIsDeleteId(s.id)} className="text-[13px] text-ink-muted px-2 py-1">削除</button>
                  </div>
                )}
              </div>
            ))}
          </div>
        )}
      </section>

      {/* ── 投資設定 ── */}
      <section className="mb-8">
        <div className="flex items-center justify-between mb-3">
          {sectionTitle('投資設定')}
          <button
            onClick={() => {
              if (investSetting) {
                setInvSetAmount(String(investSetting.amount))
                setInvSetDate(String(investSetting.investDate))
              }
              setShowInvSetForm(!showInvSetForm)
              setInvSetError(null)
            }}
            className="text-[14px] text-primary font-semibold active:opacity-60"
          >
            {investSettingExists ? '編集' : '＋ 設定する'}
          </button>
        </div>
        <p className="text-[13px] text-ink-muted mb-3 leading-relaxed">
          毎月のNISA積立額と積立日を設定します。
        </p>

        {showInvSetForm && (
          <div className="bg-parchment rounded-card px-4 py-4 mb-3 space-y-3 overflow-hidden">
            <input className={inputClass} type="number" placeholder="毎月の積立額（円）" value={invSetAmount} onChange={(e) => setInvSetAmount(e.target.value)} />
            <input className={inputClass} type="number" placeholder="積立日（1〜31）" value={invSetDate} onChange={(e) => setInvSetDate(e.target.value)} />
            {invSetError && <p className="text-[13px] text-red-500">{invSetError}</p>}
            <div className="flex gap-2">
              <button onClick={() => setShowInvSetForm(false)} className="flex-1 h-10 rounded-pill border border-hairline text-ink text-[14px] active:scale-95 transition-transform">キャンセル</button>
              {submitBtn('保存する', invSetSubmitting, handleInvSetSubmit)}
            </div>
          </div>
        )}

        {investSetting && !showInvSetForm && (
          <div className="bg-parchment rounded-lg px-4 py-3">
            <p className="text-[17px] font-semibold text-ink">{fmt(investSetting.amount)}円 / 月</p>
            <p className="text-[12px] text-ink-muted">毎月{investSetting.investDate}日に積立</p>
          </div>
        )}

        {!investSetting && !showInvSetForm && (
          <p className="text-[14px] text-ink-muted text-center py-4">投資設定が登録されていません</p>
        )}
      </section>

      {/* ── ショートカット連携 ── */}
      <section className="mb-8">
        <h2 className="text-[17px] font-semibold text-ink mb-1">ショートカット連携</h2>
        <p className="text-[14px] text-ink-muted mb-4 leading-relaxed">
          iPhoneのショートカットアプリから支出をブラウザを開かずに記録できます。
        </p>

        {tokenError && <p className="text-[14px] text-red-500 mb-4">{tokenError}</p>}

        <div className="bg-parchment rounded-lg px-4 py-3 mb-3">
          <p className="text-[12px] text-ink-muted mb-1 font-semibold">APIトークン</p>
          {token ? (
            <p className="text-[13px] font-mono text-ink break-all">{token}</p>
          ) : !tokenError ? (
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

        {!showConfirm ? (
          <button
            onClick={() => setShowConfirm(true)}
            disabled={!token}
            className="w-full h-11 rounded-pill border border-hairline bg-canvas text-ink text-[17px] font-sans active:scale-95 transition-transform disabled:opacity-40"
          >
            トークンを再生成
          </button>
        ) : (
          <div className="bg-parchment rounded-lg px-4 py-4">
            <p className="text-[14px] text-ink mb-3">
              再生成すると古いトークンは使えなくなります。本当に再生成しますか？
            </p>
            <div className="flex gap-3">
              <button onClick={() => setShowConfirm(false)} className="flex-1 h-10 rounded-pill border border-hairline bg-canvas text-ink text-[14px] font-sans active:scale-95 transition-transform">キャンセル</button>
              <button onClick={handleRegenerate} disabled={regenerating} className="flex-1 h-10 rounded-pill bg-red-500 text-white text-[14px] font-sans active:scale-95 transition-transform disabled:opacity-40">
                {regenerating ? '再生成中...' : '再生成する'}
              </button>
            </div>
          </div>
        )}

        <div className="mt-4 bg-parchment rounded-lg px-4 py-3">
          <p className="text-[13px] text-ink-muted font-semibold mb-2">ショートカットの使い方</p>
          <ol className="text-[13px] text-ink-muted space-y-2 list-decimal list-inside leading-relaxed">
            <li>下のボタンからショートカットを入手する</li>
            <li>ショートカット内の <span className="font-mono bg-canvas rounded px-1">&lt;トークン&gt;</span> を上のAPIトークンに書き換える</li>
            <li>ショートカットを実行して支出を入力する</li>
          </ol>
          <a href={SHORTCUT_URL} target="_blank" rel="noopener noreferrer" className="mt-4 flex items-center justify-center w-full h-11 rounded-pill bg-primary text-white text-[17px] font-sans active:scale-95 transition-transform">
            ショートカットを入手する
          </a>
        </div>
      </section>

      {toast && (
        <Toast
          key={toastKey}
          message={toast.message}
          onUndo={toast.onUndo}
          onDismiss={dismissToast}
        />
      )}
    </div>
  )
}
