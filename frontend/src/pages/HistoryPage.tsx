import { useEffect, useRef, useState } from 'react'
import { listExpenses, deleteExpense, updateExpense } from '../api/expenses'
import { listCategories } from '../api/categories'
import MonthSelector from '../components/MonthSelector'
import ExpenseEditModal from '../components/ExpenseEditModal'
import Toast from '../components/Toast'
import { getCategoryColor } from '../utils/categoryColors'
import type { Category, ExpenseItem } from '../types'

interface ToastConfig {
  message: string
  onUndo: () => void
  onDismiss: () => void
}

function groupByDate(expenses: ExpenseItem[]): Map<string, ExpenseItem[]> {
  const map = new Map<string, ExpenseItem[]>()
  for (const e of expenses) {
    const key = e.expenseDate
    if (!map.has(key)) map.set(key, [])
    map.get(key)!.push(e)
  }
  return map
}

function formatDate(dateStr: string): string {
  const d = new Date(dateStr + 'T00:00:00')
  const days = ['日', '月', '火', '水', '木', '金', '土']
  return `${d.getMonth() + 1}月${d.getDate()}日（${days[d.getDay()]}）`
}

export default function HistoryPage() {
  const now = new Date()
  const [year, setYear] = useState(now.getFullYear())
  const [month, setMonth] = useState(now.getMonth() + 1)

  const [categories, setCategories] = useState<Category[]>([])
  const [expenses, setExpenses] = useState<ExpenseItem[]>([])
  const [currentPage, setCurrentPage] = useState(1)
  const [totalPages, setTotalPages] = useState(0)
  const [loading, setLoading] = useState(false)
  const [loadingMore, setLoadingMore] = useState(false)
  const [editingExpense, setEditingExpense] = useState<ExpenseItem | null>(null)
  const [toast, setToast] = useState<ToastConfig | null>(null)
  const originalExpenseRef = useRef<ExpenseItem | null>(null)
  function handleDelete(item: ExpenseItem) {
    // UI から即時除去
    setExpenses((prev) => prev.filter((e) => e.id !== item.id))

    setToast({
      message: '削除しました',
      onUndo: () => {
        // 実際の DELETE はまだ呼んでいないので UI に戻すだけ
        setExpenses((prev) =>
          [...prev, item].sort(
            (a, b) => b.expenseDate.localeCompare(a.expenseDate) || b.id - a.id
          )
        )
        setToast(null)
      },
      onDismiss: async () => {
        // トースト消滅時に初めて DELETE を実行
        try { await deleteExpense(item.id) } catch { /* ignore */ }
        setToast(null)
      },
    })
  }

  function openEdit(item: ExpenseItem) {
    originalExpenseRef.current = item
    setEditingExpense(item)
  }

  function handleUpdated(updated: ExpenseItem) {
    setExpenses((prev) => prev.map((e) => (e.id === updated.id ? updated : e)))
    setEditingExpense(null)

    const original = originalExpenseRef.current!
    setToast({
      message: '更新しました',
      onUndo: async () => {
        try {
          await updateExpense(original.id, {
            title: original.title,
            price: original.price,
            expenseDate: original.expenseDate,
            categoryId: original.categoryId,
            memo: original.memo,
          })
          setExpenses((prev) => prev.map((e) => (e.id === original.id ? original : e)))
        } catch { /* ignore */ }
        setToast(null)
      },
      onDismiss: () => setToast(null),
    })
  }

  useEffect(() => {
    listCategories().then(setCategories).catch(() => {})
  }, [])

  useEffect(() => {
    setExpenses([])
    setCurrentPage(1)
    setLoading(true)
    listExpenses(year, month, 1)
      .then((res) => {
        setExpenses(res.expenses)
        setTotalPages(res.totalPages)
        setCurrentPage(1)
      })
      .catch(() => {})
      .finally(() => setLoading(false))
  }, [year, month])

  async function loadMore() {
    const nextPage = currentPage + 1
    setLoadingMore(true)
    try {
      const res = await listExpenses(year, month, nextPage)
      setExpenses((prev) => [...prev, ...res.expenses])
      setCurrentPage(nextPage)
      setTotalPages(res.totalPages)
    } finally {
      setLoadingMore(false)
    }
  }

  const categoryOrderedIds = categories.map((c) => c.id)
  const grouped = groupByDate(expenses)
  const sortedDates = Array.from(grouped.keys()).sort((a, b) => b.localeCompare(a))
  const totalAmount = expenses.reduce((sum, e) => sum + e.price, 0)

  return (
    <div className="px-5 py-4">
      <MonthSelector year={year} month={month} onChange={(y, m) => { setYear(y); setMonth(m) }} />

      {loading && (
        <div className="flex justify-center py-12">
          <div className="w-6 h-6 border-2 border-primary border-t-transparent rounded-full animate-spin" />
        </div>
      )}

      {!loading && expenses.length === 0 && (
        <div className="text-center py-16">
          <p className="text-[17px] text-ink-muted">{year}年{month}月の支出はありません</p>
        </div>
      )}

      {!loading && expenses.length > 0 && (
        <>
          <div className="mb-4 flex justify-between items-center">
            <span className="text-[14px] text-ink-muted">{expenses.length}件</span>
            <span className="font-display font-semibold text-[17px] text-ink">
              合計 ¥{totalAmount.toLocaleString()}
            </span>
          </div>

          <div className="flex flex-col gap-4">
            {sortedDates.map((dateStr) => {
              const items = grouped.get(dateStr)!
              const dayTotal = items.reduce((s, e) => s + e.price, 0)
              return (
                <div key={dateStr}>
                  <div className="flex justify-between items-center mb-2 px-1">
                    <span className="text-[14px] font-semibold text-ink">{formatDate(dateStr)}</span>
                    <span className="text-[14px] text-ink-muted">¥{dayTotal.toLocaleString()}</span>
                  </div>
                  <div className="bg-canvas rounded-card border border-hairline overflow-hidden">
                    {items.map((item, idx) => (
                      <div
                        key={item.id}
                        className={`flex flex-col px-4 py-3 ${
                          idx < items.length - 1 ? 'border-b border-hairline' : ''
                        }`}
                      >
                        {/* バッジ・タイトル・金額・操作ボタン */}
                        <div className="flex items-center gap-2">
                          {(() => {
                            const color = getCategoryColor(item.categoryId, categoryOrderedIds)
                            return (
                              <span
                                className="flex-none px-2 py-1 rounded-[6px] text-[11px] max-w-[72px] truncate"
                                style={color
                                  ? { backgroundColor: `${color}20`, color }
                                  : { backgroundColor: '#f5f5f7', color: '#7a7a7a' }
                                }
                              >
                                {item.categoryName ?? '未分類'}
                              </span>
                            )
                          })()}
                          <p className="flex-1 min-w-0 text-[17px] text-ink font-sans leading-snug truncate">{item.title}</p>
                          <span className="flex-none text-[17px] font-semibold text-ink">
                            ¥{item.price.toLocaleString()}
                          </span>
                          <button
                            onClick={() => openEdit(item)}
                            className="flex-none w-8 h-8 flex items-center justify-center rounded-full text-ink-muted hover:bg-parchment transition-colors"
                            aria-label="編集"
                          >
                            <svg width="15" height="15" viewBox="0 0 15 15" fill="none">
                              <path d="M10.5 1.5l3 3L4.5 13.5H1.5v-3L10.5 1.5z" stroke="currentColor" strokeWidth="1.3" strokeLinecap="round" strokeLinejoin="round" />
                            </svg>
                          </button>
                          <button
                            onClick={() => handleDelete(item)}
                            className="flex-none w-8 h-8 flex items-center justify-center rounded-full text-ink-muted hover:bg-red-50 hover:text-red-500 transition-colors"
                            aria-label="削除"
                          >
                            <svg width="14" height="14" viewBox="0 0 14 14" fill="none">
                              <path d="M1 3.5h12M5 3.5V2h4v1.5M3 3.5l1 8.5h6l1-8.5" stroke="currentColor" strokeWidth="1.3" strokeLinecap="round" strokeLinejoin="round" />
                            </svg>
                          </button>
                        </div>

                        {/* メモ（タイトルに揃えて左揃え） */}
                        {item.memo && (
                          <div className="flex gap-2 mt-0.5">
                            <span className="flex-none px-2 py-1 max-w-[72px] invisible select-none" aria-hidden="true">
                              {item.categoryName ?? '未分類'}
                            </span>
                            <p className="flex-1 min-w-0 text-[12px] text-ink-muted truncate">{item.memo}</p>
                          </div>
                        )}
                      </div>
                    ))}
                  </div>
                </div>
              )
            })}
          </div>

          {currentPage < totalPages && (
            <div className="flex justify-center mt-6">
              <button
                onClick={loadMore}
                disabled={loadingMore}
                className="px-8 h-11 rounded-pill border border-primary text-primary text-[17px] font-sans active:scale-95 transition-transform disabled:opacity-60"
              >
                {loadingMore ? '読み込み中...' : 'もっと見る'}
              </button>
            </div>
          )}
        </>
      )}

      {editingExpense && (
        <ExpenseEditModal
          expense={editingExpense}
          onClose={() => setEditingExpense(null)}
          onUpdated={handleUpdated}
        />
      )}

      {toast && (
        <Toast
          message={toast.message}
          onUndo={toast.onUndo}
          onDismiss={toast.onDismiss}
        />
      )}
    </div>
  )
}
