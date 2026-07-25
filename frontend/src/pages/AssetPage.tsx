import { useState, useEffect } from 'react'
import {
  PieChart, Pie, Cell, Tooltip, ResponsiveContainer,
  AreaChart, Area, XAxis, YAxis,
} from 'recharts'
import { getAssetSummary } from '../api/assets'
import { listInvestRecords } from '../api/investRecords'
import { listCashBalances } from '../api/cashBalances'
import { listIncomeRecords } from '../api/incomeRecords'
import { listAllExpenses } from '../api/expenses'
import type { AssetSummary, InvestRecord, CashBalance, ExpenseItem } from '../types'
import type { IncomeRecord } from '../api/incomeRecords'

type Tab = 'total' | 'trend'
type Period = '6m' | '1y' | 'all'

const PIE_COLORS = ['#0066cc', '#34c759']
const TREND_COLORS = { cash: '#0066cc', nisa: '#34c759' }

function fmt(n: number) {
  return n.toLocaleString('ja-JP')
}

function buildCombinedTrendData(
  investRecords: InvestRecord[],
  cashBalances: CashBalance[],
  incomeRecords: IncomeRecord[],
  expenses: ExpenseItem[],
  period: Period
) {
  const now = new Date()
  const sortedInvest = [...investRecords].sort((a, b) => a.investDate.localeCompare(b.investDate))
  const sortedAnchors = [...cashBalances].sort((a, b) => a.balanceDate.localeCompare(b.balanceDate))

  let startDate: Date
  if (period === '6m') {
    startDate = new Date(now.getFullYear(), now.getMonth() - 5, 1)
  } else if (period === '1y') {
    startDate = new Date(now.getFullYear() - 1, now.getMonth() + 1, 1)
  } else {
    const candidates: Date[] = []
    if (sortedInvest.length > 0) candidates.push(new Date(sortedInvest[0].investDate))
    if (sortedAnchors.length > 0) candidates.push(new Date(sortedAnchors[0].balanceDate))
    if (candidates.length === 0) return []
    const earliest = candidates.reduce((a, b) => (a < b ? a : b))
    startDate = new Date(earliest.getFullYear(), earliest.getMonth(), 1)
  }

  const months: string[] = []
  const d = new Date(startDate)
  while (
    d.getFullYear() < now.getFullYear() ||
    (d.getFullYear() === now.getFullYear() && d.getMonth() <= now.getMonth())
  ) {
    months.push(`${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}`)
    d.setMonth(d.getMonth() + 1)
  }

  if (months.length === 0) return []

  const startPrefix = months[0]
  let nisaCumulative = sortedInvest
    .filter((r) => r.investDate < startPrefix + '-01')
    .reduce((sum, r) => sum + r.amount, 0)

  return months.map((month) => {
    const [y, m] = month.split('-').map(Number)
    const nextMonthStr =
      m === 12
        ? `${y + 1}-01-01`
        : `${y}-${String(m + 1).padStart(2, '0')}-01`

    // NISA元本（累計）
    const nisaAdded = sortedInvest
      .filter((r) => r.investDate.startsWith(month))
      .reduce((sum, r) => sum + r.amount, 0)
    nisaCumulative += nisaAdded

    // 現金残高: 基準点(anchor)から月末までの収支を計算
    const anchor = sortedAnchors.filter((a) => a.balanceDate < nextMonthStr).slice(-1)[0]
    let cashBalance: number | null = null
    if (anchor) {
      const B = anchor.amount
      const I = incomeRecords
        .filter((r) => r.incomeDate >= anchor.balanceDate && r.incomeDate < nextMonthStr)
        .reduce((sum, r) => sum + r.amount, 0)
      const E = expenses
        .filter((r) => r.expenseDate >= anchor.balanceDate && r.expenseDate < nextMonthStr)
        .reduce((sum, r) => sum + r.price, 0)
      const V = investRecords
        .filter(
          (r) =>
            r.investDate >= anchor.balanceDate &&
            r.investDate < nextMonthStr &&
            r.investType !== 'INITIAL'
        )
        .reduce((sum, r) => sum + r.amount, 0)
      cashBalance = B + I - E - V
    }

    const mn = parseInt(month.slice(5, 7))
    const yr = month.slice(2, 4)
    const label = period === 'all' && mn === 1 ? `'${yr}年` : `${mn}月`

    return { month, label, nisaTotal: nisaCumulative, cashBalance }
  })
}

export default function AssetPage() {
  const [tab, setTab] = useState<Tab>('total')
  const [summary, setSummary] = useState<AssetSummary | null>(null)
  const [investRecords, setInvestRecords] = useState<InvestRecord[]>([])
  const [cashBalances, setCashBalances] = useState<CashBalance[]>([])
  const [incomeRecords, setIncomeRecords] = useState<IncomeRecord[]>([])
  const [expenses, setExpenses] = useState<ExpenseItem[]>([])
  const [period, setPeriod] = useState<Period>('1y')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    Promise.all([
      getAssetSummary(),
      listInvestRecords(),
      listCashBalances(),
      listIncomeRecords(),
      listAllExpenses(),
    ])
      .then(([s, inv, cb, inc, exp]) => {
        setSummary(s)
        setInvestRecords(inv.data)
        setCashBalances(cb.data)
        setIncomeRecords(inc.data)
        setExpenses(exp.expenses)
      })
      .catch(() => setError('データの取得に失敗しました'))
      .finally(() => setLoading(false))
  }, [])

  const pieData = summary
    ? [
        { name: '現金残高', value: Math.max(0, summary.cashBalance) },
        { name: 'NISA元本', value: summary.nisaPrincipal },
      ]
    : []

  const combinedData = buildCombinedTrendData(
    investRecords,
    cashBalances,
    incomeRecords,
    expenses,
    period
  )
  const hasChartData =
    combinedData.length > 1 &&
    (combinedData.some((d) => d.nisaTotal > 0) ||
      combinedData.some((d) => d.cashBalance !== null))

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="w-8 h-8 border-2 border-primary border-t-transparent rounded-full animate-spin" />
      </div>
    )
  }

  if (error) {
    return (
      <div className="px-5 pt-10 text-center">
        <p className="text-[14px] text-ink-muted">{error}</p>
      </div>
    )
  }

  return (
    <div className="px-5 pt-6 pb-32 max-w-lg mx-auto">
      <h1 className="text-[28px] font-semibold text-ink tracking-tight mb-6">資産</h1>

      {/* タブ */}
      <div className="flex bg-parchment rounded-pill p-1 mb-6">
        {([['total', '総資産'], ['trend', '資産推移']] as [Tab, string][]).map(([key, label]) => (
          <button
            key={key}
            onClick={() => setTab(key)}
            className={`flex-1 h-8 rounded-pill text-[14px] font-sans transition-all ${
              tab === key
                ? 'bg-canvas text-ink font-semibold shadow-sm'
                : 'text-ink-muted'
            }`}
          >
            {label}
          </button>
        ))}
      </div>

      {tab === 'total' && summary && (
        <div>
          {/* 総資産額 */}
          <div className="text-center mb-6">
            <p className="text-[14px] text-ink-muted mb-1">総資産</p>
            <p className="text-[40px] font-semibold text-ink tracking-tight">
              {fmt(summary.totalAssets)}
              <span className="text-[20px] font-normal ml-1">円</span>
            </p>
          </div>

          {/* 円グラフ */}
          {summary.totalAssets > 0 ? (
            <div className="mb-6 relative">
              <ResponsiveContainer width="100%" height={340}>
                <PieChart>
                  <Pie
                    data={pieData}
                    cx="50%"
                    cy="50%"
                    innerRadius={100}
                    outerRadius={145}
                    paddingAngle={2}
                    dataKey="value"
                  >
                    {pieData.map((_, i) => (
                      <Cell key={i} fill={PIE_COLORS[i]} />
                    ))}
                  </Pie>
                  <Tooltip formatter={(value: number) => [`${fmt(value)}円`, '']} />
                </PieChart>
              </ResponsiveContainer>
              {/* 中央テキスト */}
              <div className="absolute inset-0 flex flex-col items-center justify-center pointer-events-none gap-1.5">
                <div className="flex items-center gap-1.5">
                  <div className="w-3 h-3 rounded-full" style={{ backgroundColor: PIE_COLORS[0] }} />
                  <span className="text-[14px] text-ink-muted">現金残高</span>
                </div>
                <span className="text-[22px] font-semibold text-ink leading-tight">{fmt(summary.cashBalance)}円</span>
                <div className="w-16 h-px bg-hairline my-1.5" />
                <div className="flex items-center gap-1.5">
                  <div className="w-3 h-3 rounded-full" style={{ backgroundColor: PIE_COLORS[1] }} />
                  <span className="text-[14px] text-ink-muted">NISA元本</span>
                </div>
                <span className="text-[22px] font-semibold text-ink leading-tight">{fmt(summary.nisaPrincipal)}円</span>
              </div>
            </div>
          ) : (
            <div className="bg-parchment rounded-card p-6 text-center mb-6">
              <p className="text-[14px] text-ink-muted">
                マイページで現金残高を登録すると資産が表示されます
              </p>
            </div>
          )}
        </div>
      )}

      {tab === 'trend' && (
        <div>
          {/* 期間選択 */}
          <div className="flex gap-2 mb-6">
            {([['6m', '6ヶ月'], ['1y', '1年'], ['all', '全期間']] as [Period, string][]).map(([key, label]) => (
              <button
                key={key}
                onClick={() => setPeriod(key)}
                className={`flex-1 h-9 rounded-pill text-[14px] font-sans border transition-all ${
                  period === key
                    ? 'bg-primary text-white border-primary'
                    : 'bg-canvas text-ink-muted border-hairline'
                }`}
              >
                {label}
              </button>
            ))}
          </div>

          {/* サマリー */}
          <div className="flex justify-around mb-6">
            <div className="text-center">
              <div className="flex items-center justify-center gap-1.5 mb-1">
                <div className="w-2.5 h-2.5 rounded-full" style={{ backgroundColor: TREND_COLORS.cash }} />
                <p className="text-[13px] text-ink-muted">現金残高</p>
              </div>
              <p className="text-[26px] font-semibold text-ink tracking-tight">
                {summary ? fmt(summary.cashBalance) : '-'}
                <span className="text-[15px] font-normal ml-1">円</span>
              </p>
            </div>
            <div className="w-px bg-hairline" />
            <div className="text-center">
              <div className="flex items-center justify-center gap-1.5 mb-1">
                <div className="w-2.5 h-2.5 rounded-full" style={{ backgroundColor: TREND_COLORS.nisa }} />
                <p className="text-[13px] text-ink-muted">NISA元本（累計）</p>
              </div>
              <p className="text-[26px] font-semibold text-ink tracking-tight">
                {fmt(investRecords.reduce((s, r) => s + r.amount, 0))}
                <span className="text-[15px] font-normal ml-1">円</span>
              </p>
            </div>
          </div>

          {/* 推移グラフ */}
          {hasChartData ? (
            <ResponsiveContainer width="100%" height={220}>
              <AreaChart data={combinedData} margin={{ top: 4, right: 4, bottom: 0, left: 0 }}>
                <defs>
                  <linearGradient id="nisaGrad" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor={TREND_COLORS.nisa} stopOpacity={0.15} />
                    <stop offset="95%" stopColor={TREND_COLORS.nisa} stopOpacity={0} />
                  </linearGradient>
                  <linearGradient id="cashGrad" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor={TREND_COLORS.cash} stopOpacity={0.15} />
                    <stop offset="95%" stopColor={TREND_COLORS.cash} stopOpacity={0} />
                  </linearGradient>
                </defs>
                <XAxis
                  dataKey="label"
                  tick={{ fontSize: 11, fill: '#7a7a7a' }}
                  tickLine={false}
                  axisLine={false}
                  interval="preserveStartEnd"
                />
                <YAxis
                  tick={{ fontSize: 11, fill: '#7a7a7a' }}
                  tickLine={false}
                  axisLine={false}
                  tickFormatter={(v) => `${Math.round(v / 10000)}万`}
                  width={36}
                />
                <Tooltip
                  formatter={(value: number, name: string) => [
                    `${fmt(value)}円`,
                    name === 'nisaTotal' ? 'NISA元本' : '現金残高',
                  ]}
                  labelFormatter={(label) => `${label}`}
                />
                <Area
                  type="monotone"
                  dataKey="nisaTotal"
                  stroke={TREND_COLORS.nisa}
                  strokeWidth={2}
                  fill="url(#nisaGrad)"
                  dot={false}
                  connectNulls
                />
                <Area
                  type="monotone"
                  dataKey="cashBalance"
                  stroke={TREND_COLORS.cash}
                  strokeWidth={2}
                  fill="url(#cashGrad)"
                  dot={false}
                  connectNulls
                />
              </AreaChart>
            </ResponsiveContainer>
          ) : (
            <div className="bg-parchment rounded-card p-6 text-center">
              <p className="text-[14px] text-ink-muted">
                マイページで投資履歴または現金残高を登録すると推移が表示されます
              </p>
            </div>
          )}
        </div>
      )}
    </div>
  )
}
