import { useCallback, useEffect, useRef, useState } from 'react'
import { listCategories } from '../api/categories'
import { listExpenses, createExpense, deleteExpense } from '../api/expenses'
import MonthSelector from '../components/MonthSelector'
import CategoryModal from '../components/CategoryModal'
import CategoryGauge from '../components/CategoryGauge'
import Toast from '../components/Toast'
import type { Category, ExpenseItem } from '../types'

function todayString() {
  return new Date().toISOString().slice(0, 10)
}


export default function InputPage() {
  const now = new Date()
  const [year, setYear] = useState(now.getFullYear())
  const [month, setMonth] = useState(now.getMonth() + 1)
  // F-3: handleUndo のクロージャ問題を避けるため最新値を ref で保持
  const yearRef = useRef(now.getFullYear())
  const monthRef = useRef(now.getMonth() + 1)

  const [categories, setCategories] = useState<Category[]>([])
  const [chartExpenses, setChartExpenses] = useState<ExpenseItem[]>([])
  const [showCategoryModal, setShowCategoryModal] = useState(false)

  const [date, setDate] = useState(todayString())
  const [title, setTitle] = useState('')
  const [price, setPrice] = useState('')
  const [categoryId, setCategoryId] = useState<number | null>(null)
  const [memo, setMemo] = useState('')
  const [submitError, setSubmitError] = useState<string | null>(null)
  const [submitting, setSubmitting] = useState(false)
  const [toastExpenseId, setToastExpenseId] = useState<number | null>(null)

  useEffect(() => {
    listCategories().then(setCategories).catch(() => {})
  }, [])

  useEffect(() => {
    listExpenses(year, month, 1, 200)
      .then((res) => setChartExpenses(res.expenses))
      .catch(() => {})
  }, [year, month])

  function handleMonthChange(y: number, m: number) {
    setYear(y)
    setMonth(m)
    yearRef.current = y
    monthRef.current = m
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    const priceNum = parseInt(price, 10)
    if (!priceNum || priceNum < 1) {
      setSubmitError('金額は1以上の整数を入力してください')
      return
    }
    setSubmitError(null)
    setSubmitting(true)
    try {
      const created = await createExpense({
        title,
        price: priceNum,
        expenseDate: date,
        categoryId,
        memo: memo || null,
      })
      setTitle('')
      setPrice('')
      setCategoryId(null)
      setMemo('')
      setDate(todayString())
      setToastExpenseId(created.id)
      listExpenses(year, month, 1, 200)
        .then((res) => setChartExpenses(res.expenses))
        .catch(() => {})
    } catch (err) {
      setSubmitError(err instanceof Error ? err.message : '登録に失敗しました')
    } finally {
      setSubmitting(false)
    }
  }

  const handleToastDismiss = useCallback(() => setToastExpenseId(null), [])

  async function handleUndo() {
    if (toastExpenseId === null) return
    setToastExpenseId(null)
    try {
      await deleteExpense(toastExpenseId)
      // F-3: ref で最新の year/month を参照（クロージャの古い値を使わない）
      listExpenses(yearRef.current, monthRef.current, 1, 200)
        .then((res) => setChartExpenses(res.expenses))
        .catch(() => {})
    } catch {
      // 削除失敗は無視
    }
  }

  const totalAmount = chartExpenses.reduce((sum, e) => sum + e.price, 0)

  function spentForCategory(categoryId: number): number {
    return chartExpenses
      .filter((e) => e.categoryId === categoryId)
      .reduce((sum, e) => sum + e.price, 0)
  }

  return (
    <div className="px-5 py-4">
      <MonthSelector year={year} month={month} onChange={handleMonthChange} />

      {/* 支出入力フォーム */}
      <section className="mt-4">
        <form onSubmit={handleSubmit} className="bg-canvas rounded-card border border-hairline p-5 flex flex-col gap-4">
          <h2 className="font-display font-semibold text-[17px] tracking-tight text-ink">支出を記録</h2>

          {submitError && (
            <p className="text-red-500 text-[14px]">{submitError}</p>
          )}

          {/* 日付 */}
          <div className="flex flex-col gap-1">
            <label className="text-[12px] text-ink-muted">日付</label>
            <input
              type="date"
              value={date}
              onChange={(e) => setDate(e.target.value)}
              required
              className="h-11 px-4 rounded-pill border border-hairline text-[17px] text-ink outline-none focus:border-primary transition-colors"
            />
          </div>

          {/* タイトル */}
          <div className="flex flex-col gap-1">
            <label className="text-[12px] text-ink-muted">タイトル <span className="text-red-400">*</span></label>
            <input
              type="text"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              required
              placeholder="例：コーヒー"
              className="h-11 px-4 rounded-pill border border-hairline text-[17px] text-ink outline-none focus:border-primary transition-colors"
            />
          </div>

          {/* 金額 */}
          <div className="flex flex-col gap-1">
            <label className="text-[12px] text-ink-muted">金額（円） <span className="text-red-400">*</span></label>
            <input
              type="number"
              value={price}
              onChange={(e) => setPrice(e.target.value)}
              required
              min={1}
              placeholder="例：500"
              className="h-11 px-4 rounded-pill border border-hairline text-[17px] text-ink outline-none focus:border-primary transition-colors"
            />
          </div>

          {/* カテゴリ */}
          <div className="flex flex-col gap-2">
            <div className="flex items-center justify-between">
              <label className="text-[12px] text-ink-muted">カテゴリ</label>
              <button
                type="button"
                onClick={() => setShowCategoryModal(true)}
                className="text-[12px] text-primary flex items-center gap-1"
              >
                <svg width="12" height="12" viewBox="0 0 12 12" fill="none">
                  <path d="M6 1v10M1 6h10" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" />
                </svg>
                追加・編集
              </button>
            </div>
            <div className="flex flex-wrap gap-2">
              {categories.map((cat) => (
                <button
                  key={cat.id}
                  type="button"
                  onClick={() => setCategoryId(cat.id === categoryId ? null : cat.id)}
                  className={`px-4 py-2 rounded-pill text-[14px] transition-colors border ${
                    categoryId === cat.id
                      ? 'bg-primary text-white border-primary'
                      : 'bg-canvas text-ink border-hairline hover:border-primary'
                  }`}
                >
                  {cat.name}
                </button>
              ))}
              {categories.length === 0 && (
                <p className="text-[14px] text-ink-muted">
                  カテゴリがありません。「管理」から追加してください。
                </p>
              )}
            </div>
          </div>

          {/* メモ */}
          <div className="flex flex-col gap-1">
            <label className="text-[12px] text-ink-muted">メモ（任意）</label>
            <textarea
              value={memo}
              onChange={(e) => setMemo(e.target.value)}
              rows={2}
              placeholder="メモを入力..."
              className="px-4 py-3 rounded-[14px] border border-hairline text-[17px] text-ink outline-none focus:border-primary transition-colors resize-none"
            />
          </div>

          <button
            type="submit"
            disabled={submitting}
            className="h-11 rounded-pill bg-primary text-white text-[17px] font-sans active:scale-95 transition-transform disabled:opacity-60"
          >
            {submitting ? '登録中...' : '登録する'}
          </button>
        </form>
      </section>

      {/* カテゴリ別予算ゲージ */}
      {categories.length > 0 && (
        <section className="mt-6 bg-canvas rounded-card border border-hairline p-5">
          <h2 className="font-display font-semibold text-[17px] tracking-tight text-ink mb-1">
            {year}年{month}月の支出
          </h2>
          <p className="text-[14px] text-ink-muted mb-4">
            合計: ¥{totalAmount.toLocaleString()}
          </p>
          <div className="grid grid-cols-2 gap-2">
            {categories.map((cat, i) => (
              <CategoryGauge
                key={cat.id}
                category={cat}
                spent={spentForCategory(cat.id)}
                colorIndex={i}
              />
            ))}
          </div>
        </section>
      )}

      {categories.length === 0 && (
        <section className="mt-6 bg-canvas rounded-card border border-hairline p-5 text-center">
          <p className="text-[14px] text-ink-muted">カテゴリがありません。追加・編集から登録してください。</p>
        </section>
      )}

      {showCategoryModal && (
        <CategoryModal
          categories={categories}
          onClose={() => setShowCategoryModal(false)}
          onUpdate={(updated) => setCategories(updated)}
        />
      )}

      {toastExpenseId !== null && (
        <Toast message="登録が完了しました" onUndo={handleUndo} onDismiss={handleToastDismiss} />
      )}
    </div>
  )
}
