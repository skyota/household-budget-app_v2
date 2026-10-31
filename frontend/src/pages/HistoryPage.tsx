import { useEffect, useRef, useState } from 'react'
import { listExpenses, createExpense, deleteExpense, updateExpense } from '../api/expenses'
import { listCategories } from '../api/categories'
import { listFixedExpenses, listFixedExpenseHistory } from '../api/fixedExpenses'
import { listIncomeSettings } from '../api/incomeSettings'
import { getInvestSetting } from '../api/investSettings'
import { getLivingBudget } from '../api/livingBudget'
import { PieChart, Pie, Cell, Tooltip, ResponsiveContainer, BarChart, Bar, XAxis, YAxis, CartesianGrid } from 'recharts'
import MonthSelector from '../components/MonthSelector'
import ExpenseEditModal from '../components/ExpenseEditModal'
import Toast from '../components/Toast'
import { getCategoryColor } from '../utils/categoryColors'
import type { Category, ExpenseItem } from '../types'

type HistoryTab = 'monthly' | 'yearly'
const OVERVIEW_COLORS = { LIVING: '#f59e0b', UTILITIES: '#0ea5e9', SPECIAL: '#8b5cf6', FIXED: '#64748b' }

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
  const [tab, setTab] = useState<HistoryTab>('monthly')
  const [annualYear, setAnnualYear] = useState(now.getFullYear())
  const [annualExpenses, setAnnualExpenses] = useState<ExpenseItem[]>([])
  const [fixedExpenseTotal, setFixedExpenseTotal] = useState(0)
  const [monthlyIncomeTotal, setMonthlyIncomeTotal] = useState(0)
  const [monthlyInvestAmount, setMonthlyInvestAmount] = useState(0)
  const [livingBudget, setLivingBudget] = useState(65000)
  const [annualFixedExpenseHistory, setAnnualFixedExpenseHistory] = useState<Record<number, number>>({})
  const [monthlyFixedExpenseHistory, setMonthlyFixedExpenseHistory] = useState<Record<number, number>>({})
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
    listFixedExpenses().then((res) => setFixedExpenseTotal(res.data.reduce((sum, item) => sum + item.price, 0))).catch(() => {})
    listIncomeSettings().then((res) => setMonthlyIncomeTotal(res.data.reduce((sum, item) => sum + item.amount, 0))).catch(() => {})
    getInvestSetting().then((setting) => setMonthlyInvestAmount(setting.amount)).catch(() => setMonthlyInvestAmount(0))
    getLivingBudget().then((budget) => setLivingBudget(budget.amount)).catch(() => {})
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
    listExpenses(year, month, 1, 1000)
      .then((res) => setChartExpenses(res.expenses))
      .catch(() => {})
  }, [year, month])

  useEffect(() => {
    Promise.all(Array.from({ length: 12 }, (_, index) => listExpenses(annualYear, index + 1, 1, 1000)))
      .then((results) => setAnnualExpenses(results.flatMap((result) => result.expenses)))
      .catch(() => setAnnualExpenses([]))
    Promise.all([
      listFixedExpenseHistory(annualYear),
      listFixedExpenses(),
    ]).then(([history, settings]) => {
      const snapshotMap = Object.fromEntries(history.map((item) => [item.month, item.total]))
      const currentTotal = settings.data.reduce((sum, item) => sum + item.price, 0)
      if (annualYear === now.getFullYear()) {
        for (let m = now.getMonth() + 1; m <= 12; m += 1) snapshotMap[m] = currentTotal
      }
      setAnnualFixedExpenseHistory(snapshotMap)
    }).catch(() => setAnnualFixedExpenseHistory({}))
  }, [annualYear])

  useEffect(() => {
    listFixedExpenseHistory(year)
      .then((history) => setMonthlyFixedExpenseHistory(Object.fromEntries(history.map((item) => [item.month, item.total]))))
      .catch(() => setMonthlyFixedExpenseHistory({}))
  }, [year])

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

  const categoryOrderedIds = categories.map((c) => c.id)
  const grouped = groupByDate(expenses)
  const sortedDates = Array.from(grouped.keys()).sort((a, b) => b.localeCompare(a))
  const totalAmount = expenses.reduce((sum, e) => sum + e.price, 0)
  const visibleCategories = categories.filter((category) => !category.isSystem)
  const selectedMonthIsPast = year < now.getFullYear() || (year === now.getFullYear() && month < now.getMonth() + 1)
  const selectedMonthFixedTotal = monthlyFixedExpenseHistory[month] ?? (selectedMonthIsPast ? 0 : fixedExpenseTotal)
  const groupTotals = visibleCategories.map((category) => ({
    id: category.id,
    name: category.name,
    value: chartExpenses.filter((expense) => expense.categoryId === category.id).reduce((sum, expense) => sum + expense.price, 0),
    color: getCategoryColor(category.id, categoryOrderedIds) ?? '#94a3b8',
  })).filter((item) => item.value > 0)
  const livingTotal = visibleCategories.filter((category) => category.groupType === 'LIVING').reduce((sum, category) => sum + chartExpenses.filter((expense) => expense.categoryId === category.id).reduce((s, expense) => s + expense.price, 0), 0)
  const livingPieData = [...groupTotals.filter((item) => visibleCategories.find((category) => category.id === item.id)?.groupType === 'LIVING')]
  if (livingTotal < livingBudget) livingPieData.push({ id: -1, name: '残り予算', value: livingBudget - livingTotal, color: '#e5e7eb' })
  const overviewData = [
    { name: '生活費', value: visibleCategories.filter((category) => category.groupType === 'LIVING').reduce((sum, category) => sum + chartExpenses.filter((expense) => expense.categoryId === category.id).reduce((s, expense) => s + expense.price, 0), 0), color: OVERVIEW_COLORS.LIVING },
    { name: '光熱費', value: visibleCategories.filter((category) => category.groupType === 'UTILITIES').reduce((sum, category) => sum + chartExpenses.filter((expense) => expense.categoryId === category.id).reduce((s, expense) => s + expense.price, 0), 0), color: OVERVIEW_COLORS.UTILITIES },
    { name: '特別費', value: visibleCategories.filter((category) => category.groupType === 'SPECIAL').reduce((sum, category) => sum + chartExpenses.filter((expense) => expense.categoryId === category.id).reduce((s, expense) => s + expense.price, 0), 0), color: OVERVIEW_COLORS.SPECIAL },
    { name: '固定費', value: selectedMonthFixedTotal, color: OVERVIEW_COLORS.FIXED },
  ]
  const spendableAmount = Math.max(0, monthlyIncomeTotal - monthlyInvestAmount)
  const overviewSpent = overviewData.reduce((sum, item) => sum + item.value, 0)
  const remainingAmount = Math.max(0, spendableAmount - overviewSpent)
  const overviewPieData = [
    ...overviewData,
    ...(remainingAmount > 0 ? [{ name: '残り', value: remainingAmount, color: '#e5e7eb' }] : []),
  ]
  const annualData = Array.from({ length: 12 }, (_, index) => {
    const m = index + 1
    const monthExpenses = annualExpenses.filter((expense) => Number(expense.expenseDate.slice(5, 7)) === m)
    const isPastMonth = annualYear < now.getFullYear() || (annualYear === now.getFullYear() && m < now.getMonth() + 1)
    const totals: Record<string, string | number | null> = { month: `${m}月`, LIVING: 0, UTILITIES: 0, SPECIAL: 0, FIXED: annualFixedExpenseHistory[m] ?? (isPastMonth ? null : fixedExpenseTotal) }
    for (const category of visibleCategories) {
      const total = monthExpenses.filter((expense) => expense.categoryId === category.id).reduce((sum, expense) => sum + expense.price, 0)
      if (category.groupType === 'LIVING') totals.LIVING = Number(totals.LIVING) + total
      if (category.groupType === 'UTILITIES') totals.UTILITIES = Number(totals.UTILITIES) + total
      if (category.groupType === 'SPECIAL') totals.SPECIAL = Number(totals.SPECIAL) + total
    }
    return totals
  })

  return (
    <div className="px-5 py-4">
      {tab === 'monthly' && <MonthSelector year={year} month={month} onChange={(y, m) => { setYear(y); setMonth(m) }} />}

      <div className="flex bg-parchment rounded-pill p-1 mt-4 mb-5">
        {([['monthly', '支払い履歴'], ['yearly', '年間推移']] as [HistoryTab, string][]).map(([key, label]) => (
          <button key={key} onClick={() => setTab(key)} className={`flex-1 h-9 rounded-pill text-[14px] ${tab === key ? 'bg-canvas text-ink font-semibold shadow-sm' : 'text-ink-muted'}`}>{label}</button>
        ))}
      </div>

      {tab === 'monthly' && <div className="space-y-5 mb-6">
        <section className="bg-canvas rounded-card border border-hairline p-4">
          <h2 className="font-semibold text-[17px] text-ink">生活費の内訳</h2>
          <p className="text-[13px] text-ink-muted">予算 ¥{livingBudget.toLocaleString()} に対する今月の利用額</p>
          <div className="relative h-[270px]">
            <ResponsiveContainer width="100%" height="100%"><PieChart>
              <Pie data={livingPieData} dataKey="value" nameKey="name" cx="50%" cy="50%" innerRadius={75} outerRadius={118} paddingAngle={0} startAngle={90} endAngle={-270}>
                {livingPieData.map((item) => <Cell key={item.id} fill={item.color} />)}
              </Pie><Tooltip formatter={(value: number) => `¥${value.toLocaleString()}`} />
            </PieChart></ResponsiveContainer>
            <div className="absolute inset-0 flex flex-col items-center justify-center pointer-events-none">
              <span className="text-[12px] text-ink-muted">生活費</span>
              <strong className="text-[21px] text-ink">¥{livingTotal.toLocaleString()}</strong>
              <span className={`text-[12px] ${livingTotal > livingBudget ? 'text-red-500' : 'text-ink-muted'}`}>{livingTotal > livingBudget ? `予算超過 ¥${(livingTotal - livingBudget).toLocaleString()}` : `残り ¥${(livingBudget - livingTotal).toLocaleString()}`}</span>
            </div>
          </div>
          <div className="grid grid-cols-2 gap-x-3 gap-y-2">
            {livingPieData.filter((item) => item.id !== -1).map((item) => <div key={item.id} className="flex items-center gap-2 text-[13px]"><span className="w-2.5 h-2.5 rounded-full" style={{ background: item.color }} /><span className="truncate flex-1">{item.name}</span><span>¥{item.value.toLocaleString()}</span></div>)}
          </div>
        </section>
        <section className="bg-canvas rounded-card border border-hairline p-4">
          <h2 className="font-semibold text-[17px] text-ink">今月の支出全体</h2>
          <p className="text-[13px] text-ink-muted">投資後に使える予定額: ¥{spendableAmount.toLocaleString()}</p>
          {selectedMonthIsPast && monthlyFixedExpenseHistory[month] === undefined && <p className="text-[12px] text-ink-muted">この月の固定費設定履歴はありません</p>}
          <div className="relative h-[270px]">
            <ResponsiveContainer width="100%" height="100%"><PieChart>
              <Pie data={overviewPieData} dataKey="value" nameKey="name" cx="50%" cy="50%" innerRadius={75} outerRadius={118} paddingAngle={0} startAngle={90} endAngle={-270}>
                {overviewPieData.map((item) => <Cell key={item.name} fill={item.color} />)}
              </Pie><Tooltip formatter={(value: number) => `¥${value.toLocaleString()}`} />
            </PieChart></ResponsiveContainer>
            <div className="absolute inset-0 flex flex-col items-center justify-center pointer-events-none"><span className="text-[12px] text-ink-muted">支出合計</span><strong className="text-[21px] text-ink">¥{overviewSpent.toLocaleString()}</strong><span className={`text-[12px] ${overviewSpent > spendableAmount ? 'text-red-500' : 'text-ink-muted'}`}>{overviewSpent > spendableAmount ? `予定額超過 ¥${(overviewSpent - spendableAmount).toLocaleString()}` : `残り ¥${remainingAmount.toLocaleString()}`}</span></div>
          </div>
          <div className="grid grid-cols-2 gap-x-3 gap-y-2">{overviewPieData.map((item) => <div key={item.name} className="flex items-center gap-2 text-[13px]"><span className="w-2.5 h-2.5 rounded-full" style={{ background: item.color }} /><span className="flex-1">{item.name}</span><span>¥{item.value.toLocaleString()}</span></div>)}</div>
        </section>
      </div>}

      {tab === 'yearly' && <section className="bg-canvas rounded-card border border-hairline p-4 mb-6">
        <div className="flex justify-between items-center mb-4"><h2 className="font-semibold text-[17px] text-ink">年間支出</h2><select value={annualYear} onChange={(e) => setAnnualYear(Number(e.target.value))} className="border border-hairline rounded-lg bg-canvas px-3 py-2 text-[14px]">{Array.from({ length: 8 }, (_, i) => now.getFullYear() - 5 + i).map((y) => <option key={y} value={y}>{y}年</option>)}</select></div>
        <div className="space-y-5">
          {([
            ['LIVING', '生活費', OVERVIEW_COLORS.LIVING],
            ['UTILITIES', '光熱費', OVERVIEW_COLORS.UTILITIES],
            ['SPECIAL', '特別費', OVERVIEW_COLORS.SPECIAL],
            ['FIXED', '固定費', OVERVIEW_COLORS.FIXED],
          ] as const).map(([key, label, color]) => (
            <div key={key}>
              <h3 className="text-[14px] font-semibold text-ink mb-1">{label}</h3>
              <ResponsiveContainer width="100%" height={210}><BarChart data={annualData} margin={{ top: 4, right: 8, left: 0, bottom: 0 }}><CartesianGrid strokeDasharray="3 3" /><XAxis dataKey="month" tick={{ fontSize: 11 }} /><YAxis tickFormatter={(value) => `${Math.round(value / 10000)}万`} tick={{ fontSize: 11 }} width={38} /><Tooltip formatter={(value: number) => `¥${value.toLocaleString()}`} /><Bar dataKey={key} name={label} fill={color} /></BarChart></ResponsiveContainer>
            </div>
          ))}
        </div>
        <p className="text-[12px] text-ink-muted mt-2">月別履歴がない移行前の月は空欄です。設定額の変更は変更月から反映されます。</p>
      </section>}

      {loading && (
        <div className="flex justify-center py-12">
          <div className="w-6 h-6 border-2 border-primary border-t-transparent rounded-full animate-spin" />
        </div>
      )}

      {tab === 'monthly' && !loading && expenses.length === 0 && (
        <div className="text-center py-16">
          <p className="text-[17px] text-ink-muted">{year}年{month}月の支出はありません</p>
        </div>
      )}

      {tab === 'monthly' && !loading && expenses.length > 0 && (
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
