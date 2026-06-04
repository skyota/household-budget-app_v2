import { useState, useEffect } from 'react'
import {
  PieChart, Pie, Cell, Tooltip, ResponsiveContainer,
  AreaChart, Area, XAxis, YAxis,
} from 'recharts'
import { getAssetSummary } from '../api/assets'
import { listInvestRecords } from '../api/investRecords'
import type { AssetSummary, InvestRecord } from '../types'

type Tab = 'total' | 'trend'
type Period = '6m' | '1y' | 'all'

const PIE_COLORS = ['#0066cc', '#34c759']

function fmt(n: number) {
  return n.toLocaleString('ja-JP')
}

function buildTrendData(records: InvestRecord[], period: Period) {
  if (records.length === 0) return []

  const sorted = [...records].sort((a, b) => a.investDate.localeCompare(b.investDate))
  const now = new Date()

  let startDate: Date
  if (period === '6m') {
    startDate = new Date(now.getFullYear(), now.getMonth() - 5, 1)
  } else if (period === '1y') {
    startDate = new Date(now.getFullYear() - 1, now.getMonth() + 1, 1)
  } else {
    const first = new Date(sorted[0].investDate)
    startDate = new Date(first.getFullYear(), first.getMonth(), 1)
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
  let cumulative = sorted
    .filter((r) => r.investDate < startPrefix + '-01')
    .reduce((sum, r) => sum + r.amount, 0)

  return months.map((month) => {
    const added = sorted
      .filter((r) => r.investDate.startsWith(month))
      .reduce((sum, r) => sum + r.amount, 0)
    cumulative += added
    const m = parseInt(month.slice(5, 7))
    const y = month.slice(2, 4)
    const label = period === 'all' && m === 1 ? `'${y}年` : `${m}月`
    return { month, label, total: cumulative }
  })
}

export default function AssetPage() {
  const [tab, setTab] = useState<Tab>('total')
  const [summary, setSummary] = useState<AssetSummary | null>(null)
  const [investRecords, setInvestRecords] = useState<InvestRecord[]>([])
  const [period, setPeriod] = useState<Period>('1y')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    Promise.all([getAssetSummary(), listInvestRecords()])
      .then(([s, r]) => {
        setSummary(s)
        setInvestRecords(r.data)
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

  const trendData = buildTrendData(investRecords, period)

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
        {([['total', '総資産'], ['trend', '投資推移']] as [Tab, string][]).map(([key, label]) => (
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

          {/* 現在の元本 */}
          {investRecords.length > 0 && (
            <div className="text-center mb-6">
              <p className="text-[14px] text-ink-muted mb-1">NISA元本（累計）</p>
              <p className="text-[34px] font-semibold text-ink tracking-tight">
                {fmt(investRecords.reduce((s, r) => s + r.amount, 0))}
                <span className="text-[18px] font-normal ml-1">円</span>
              </p>
            </div>
          )}

          {/* 推移グラフ */}
          {trendData.length > 1 ? (
            <ResponsiveContainer width="100%" height={220}>
              <AreaChart data={trendData} margin={{ top: 4, right: 4, bottom: 0, left: 0 }}>
                <defs>
                  <linearGradient id="investGrad" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#0066cc" stopOpacity={0.15} />
                    <stop offset="95%" stopColor="#0066cc" stopOpacity={0} />
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
                  formatter={(value: number) => [`${fmt(value)}円`, 'NISA元本']}
                  labelFormatter={(label) => `${label}`}
                />
                <Area
                  type="monotone"
                  dataKey="total"
                  stroke="#0066cc"
                  strokeWidth={2}
                  fill="url(#investGrad)"
                  dot={false}
                />
              </AreaChart>
            </ResponsiveContainer>
          ) : (
            <div className="bg-parchment rounded-card p-6 text-center">
              <p className="text-[14px] text-ink-muted">
                マイページで投資履歴を登録すると推移が表示されます
              </p>
            </div>
          )}
        </div>
      )}
    </div>
  )
}
