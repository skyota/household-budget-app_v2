import { useEffect, useRef, useState } from 'react'
import { listExpenses, createExpense, deleteExpense, updateExpense } from '../api/expenses'
import { listCategories } from '../api/categories'
import MonthSelector from '../components/MonthSelector'
import ExpenseEditModal from '../components/ExpenseEditModal'
import CategoryGauge from '../components/CategoryGauge'
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

const now = new Date()

export default function HistoryPage() {
  const [year, setYear] = useState(now.getFullYear())
  const [month, setMonth] = useState(now.getMonth() + 1)
  // F-1: Undo クロージャが古い year/month を参照しないよう ref で最新値を保持
  const yearRef = useRef(now.getFullYear())
  const monthRef = useRef(now.getMonth() + 1)

  const [categories, setCategories] = useState<Category[]>([])
  const [chartExpenses, setChartExpenses] = useState<ExpenseItem[]>([])
  const [expenses, setExpenses] = useState<ExpenseItem[]>([])
  const [totalCount, setTotalCount] = useState(0)
  const [currentPage, setCurrentPage] = useState(1)
  const [totalPages, setTotalPages] = useState(0)
  const [loading, setLoading] = useState(false)
  const [loadingMore, setLoadingMore] = useState(false)
  const [editingExpense, setEditingExpense] = useState<ExpenseItem | null>(null)
  const [toast, setToast] = useState<ToastConfig | null>(null)
  const [toastKey, setToastKey] = useState(0)
  const originalExpenseRef = useRef<ExpenseItem | null>(null)

  function showToast(config: ToastConfig) {
    setToast(config)
    setToastKey((k) => k + 1)
  }

  async function handleDelete(item: ExpenseItem) {
    setExpenses((prev) => prev.filter((e) => e.id !== item.id))
    setTotalCount((prev) => prev - 1)
    try {
      await deleteExpense(item.id)
    } catch {
      setExpenses((prev) =>
        [...prev, item].sort(
          (a, b) => b.expenseDate.localeCompare(a.expenseDate) || b.id - a.id
        )
      )
      setTotalCount((prev) => prev + 1)
      return
    }

    showToast({
      message: '削除しました',
      onUndo: async () => {
        try {
          await createExpense({
            title: item.title,
            price: item.price,
            expenseDate: item.expenseDate,
            categoryId: item.categoryId,
            memo: item.memo,
          })
          // F-1: ref で最新の year/month を参照
          const res = await listExpenses(yearRef.current, monthRef.current, 1)
          setExpenses(res.expenses)
          setTotalCount(res.totalCount)
          setTotalPages(res.totalPages)
          setCurrentPage(1)
        } catch { /* ignore */ }
        setToast(null)
      },
      onDismiss: () => setToast(null),
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
    showToast({
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
        } catch { /* ignore */ } finally {
          setToast(null)
        }
      },
      onDismiss: () => setToast(null),
    })
  }

  useEffect(() => {
    listCategories().then(setCategories).catch(() => {})
  }, [])

  useEffect(() => {
    setToast(null)
    setExpenses([])
    setTotalCount(0)
    setCurrentPage(1)
    setLoading(true)
    // F-1: ref を月切り替えと同期させる
    yearRef.current = year
    monthRef.current = month
    listExpenses(year, month, 1)
      .then((res) => {
        setExpenses(res.expenses)
        setTotalCount(res.totalCount)
        setTotalPages(res.totalPages)
        setCurrentPage(1)
      })
      .catch(() => {})
      .finally(() => setLoading(false))
    listExpenses(year, month, 1, 200)
      .then((res) => setChartExpenses(res.expenses))
      .catch(() => {})
  }, [year, month])

  async function loadMore() {
    // F-2: 月切り替え後に古い月のデータが混入しないよう、取得時点の年月をキャプチャして検証
    const capturedYear = yearRef.current
    const capturedMonth = monthRef.current
    const nextPage = currentPage + 1
    setLoadingMore(true)
    try {
      const res = await listExpenses(capturedYear, capturedMonth, nextPage)
      if (yearRef.current === capturedYear && monthRef.current === capturedMonth) {
        setExpenses((prev) => [...prev, ...res.expenses])
        setCurrentPage(nextPage)
        setTotalPages(res.totalPages)
      }
    } finally {
      setLoadingMore(false)
    }
  }

  function spentForCategory(categoryId: number): number {
    return chartExpenses
      .filter((e) => e.categoryId === categoryId)
      .reduce((sum, e) => sum + e.price, 0)
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

      {/* カテゴリ別予算ゲージ */}
      {categories.filter((cat) => !cat.isSystem).length > 0 && (
        <section className="mt-4 mb-10 bg-canvas rounded-card border border-hairline p-5">
          <h2 className="font-display font-semibold text-[17px] tracking-tight text-ink mb-1">
            {year}年{month}月の支出
          </h2>
          <p className="text-[14px] text-ink-muted mb-4">
            合計: ¥{chartExpenses.reduce((s, e) => s + e.price, 0).toLocaleString()}
          </p>
          <div className="grid grid-cols-2 gap-2">
            {categories.filter((cat) => !cat.isSystem).map((cat, i) => (
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

      {!loading && expenses.length === 0 && (
        <div className="text-center py-16">
          <p className="text-[17px] text-ink-muted">{year}年{month}月の支出はありません</p>
        </div>
      )}

      {!loading && expenses.length > 0 && (
        <>
          <div className="mb-4 flex justify-between items-center">
            {/* F-5: サーバー側の総件数を表示 */}
            <span className="text-[14px] text-ink-muted">{totalCount}件</span>
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
                            onClick={() => void handleDelete(item)}
                            className="flex-none w-8 h-8 flex items-center justify-center rounded-full text-ink-muted hover:bg-red-50 hover:text-red-500 transition-colors"
                            aria-label="削除"
                          >
                            <svg width="14" height="14" viewBox="0 0 14 14" fill="none">
                              <path d="M1 3.5h12M5 3.5V2h4v1.5M3 3.5l1 8.5h6l1-8.5" stroke="currentColor" strokeWidth="1.3" strokeLinecap="round" strokeLinejoin="round" />
                            </svg>
                          </button>
                        </div>

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
          key={toastKey}
          message={toast.message}
          onUndo={toast.onUndo}
          onDismiss={toast.onDismiss}
        />
      )}
    </div>
  )
}
