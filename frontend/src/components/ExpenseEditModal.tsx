import { useEffect, useState } from 'react'
import { listCategories } from '../api/categories'
import { updateExpense } from '../api/expenses'
import type { Category, ExpenseItem } from '../types'

interface Props {
  expense: ExpenseItem
  onClose: () => void
  onUpdated: (updated: ExpenseItem) => void
}

export default function ExpenseEditModal({ expense, onClose, onUpdated }: Props) {
  const [isClosing, setIsClosing] = useState(false)

  const [date, setDate] = useState(expense.expenseDate)
  const [title, setTitle] = useState(expense.title)
  const [price, setPrice] = useState(String(expense.price))
  const [categoryId, setCategoryId] = useState<number | null>(expense.categoryId)
  const [memo, setMemo] = useState(expense.memo ?? '')
  const [categories, setCategories] = useState<Category[]>([])
  const [error, setError] = useState<string | null>(null)
  const [saving, setSaving] = useState(false)

  useEffect(() => {
    listCategories().then(setCategories).catch(() => {})
  }, [])

  function requestClose() {
    setIsClosing(true)
  }

  async function handleSave() {
    const priceNum = parseInt(price, 10)
    if (!priceNum || priceNum < 1) {
      setError('金額は1以上の整数を入力してください')
      return
    }
    if (!title.trim()) {
      setError('タイトルは必須です')
      return
    }
    setError(null)
    setSaving(true)
    try {
      await updateExpense(expense.id, {
        title: title.trim(),
        price: priceNum,
        expenseDate: date,
        categoryId,
        memo: memo || null,
      })
      const updatedCategory = categories.find((c) => c.id === categoryId)
      onUpdated({
        ...expense,
        title: title.trim(),
        price: priceNum,
        expenseDate: date,
        categoryId,
        categoryName: updatedCategory?.name ?? null,
        memo: memo || null,
      })
      requestClose()
    } catch (err) {
      setError(err instanceof Error ? err.message : '更新に失敗しました')
    } finally {
      setSaving(false)
    }
  }

  return (
    <div
      className={`fixed inset-0 z-[60] flex items-end justify-center ${isClosing ? 'modal-fade-out' : 'modal-fade-in'}`}
      style={{ backgroundColor: 'rgba(0,0,0,0.4)' }}
      onClick={(e) => { if (e.target === e.currentTarget) requestClose() }}
    >
      <div
        className={`bg-canvas w-full max-w-lg rounded-t-[24px] max-h-[90vh] flex flex-col ${isClosing ? 'modal-slide-down' : 'modal-slide-up'}`}
        onAnimationEnd={() => { if (isClosing) onClose() }}
      >
        {/* ヘッダー */}
        <div className="flex items-center justify-between px-5 pt-5 pb-3 border-b border-hairline flex-shrink-0">
          <h2 className="font-display font-semibold text-[17px] tracking-tight text-ink">支出を編集</h2>
          <button
            onClick={requestClose}
            className="w-8 h-8 flex items-center justify-center rounded-full text-ink-muted hover:bg-parchment transition-colors"
          >
            <svg width="14" height="14" viewBox="0 0 14 14" fill="none">
              <path d="M1 1l12 12M13 1L1 13" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" />
            </svg>
          </button>
        </div>

        {/* フォーム */}
        <div className="overflow-y-auto flex-1 px-5 py-4 flex flex-col gap-4">
          {error && <p className="text-red-500 text-[14px]">{error}</p>}

          <div className="flex flex-col gap-1">
            <label className="text-[12px] text-ink-muted">日付</label>
            <input
              type="date"
              value={date}
              onChange={(e) => setDate(e.target.value)}
              className="h-11 px-4 rounded-pill border border-hairline text-[17px] text-ink outline-none focus:border-primary transition-colors"
            />
          </div>

          <div className="flex flex-col gap-1">
            <label className="text-[12px] text-ink-muted">タイトル <span className="text-red-400">*</span></label>
            <input
              type="text"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              className="h-11 px-4 rounded-pill border border-hairline text-[17px] text-ink outline-none focus:border-primary transition-colors"
            />
          </div>

          <div className="flex flex-col gap-1">
            <label className="text-[12px] text-ink-muted">金額（円） <span className="text-red-400">*</span></label>
            <input
              type="number"
              value={price}
              onChange={(e) => setPrice(e.target.value)}
              min={1}
              className="h-11 px-4 rounded-pill border border-hairline text-[17px] text-ink outline-none focus:border-primary transition-colors"
            />
          </div>

          <div className="flex flex-col gap-2">
            <label className="text-[12px] text-ink-muted">カテゴリ</label>
            <div className="flex flex-wrap gap-2">
              {categories.map((cat) => (
                <button
                  key={cat.id}
                  type="button"
                  onClick={() => setCategoryId(cat.id === categoryId ? null : cat.id)}
                  className={`px-4 py-2 rounded-pill text-[14px] border transition-colors ${
                    categoryId === cat.id
                      ? 'bg-primary text-white border-primary'
                      : 'bg-canvas text-ink border-hairline'
                  }`}
                >
                  {cat.name}
                </button>
              ))}
            </div>
          </div>

          <div className="flex flex-col gap-1">
            <label className="text-[12px] text-ink-muted">メモ（任意）</label>
            <textarea
              value={memo}
              onChange={(e) => setMemo(e.target.value)}
              rows={2}
              className="px-4 py-3 rounded-[14px] border border-hairline text-[17px] text-ink outline-none focus:border-primary transition-colors resize-none"
            />
          </div>

          <button
            type="button"
            onClick={handleSave}
            disabled={saving}
            className="h-11 rounded-pill bg-primary text-white text-[17px] font-sans active:scale-95 transition-transform disabled:opacity-60"
          >
            {saving ? '保存中...' : '保存する'}
          </button>

          <div className="h-2" />
        </div>
      </div>
    </div>
  )
}
