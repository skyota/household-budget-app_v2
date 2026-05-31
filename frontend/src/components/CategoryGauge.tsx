import { CATEGORY_PALETTE } from '../utils/categoryColors'
import type { Category } from '../types'

interface Props {
  category: Category
  spent: number
  colorIndex: number
}

const VB = 180          // viewBox サイズ（論理座標）
const CX = VB / 2
const CY = VB / 2
const RADIUS = 72
const STROKE = 14
const CIRCUMFERENCE = 2 * Math.PI * RADIUS

export default function CategoryGauge({ category, spent, colorIndex }: Props) {
  const budget = category.budgetAmount
  const isOver = budget > 0 && spent > budget
  const noBudget = budget === 0

  const percent = noBudget
    ? (spent > 0 ? 1 : 0)
    : Math.min(spent / budget, 1)

  const baseColor = CATEGORY_PALETTE[colorIndex % CATEGORY_PALETTE.length]
  const trackColor = '#efefef'
  const fillColor = isOver ? '#ff3b30' : noBudget ? '#8e8e93' : baseColor
  const dashOffset = CIRCUMFERENCE * (1 - percent)

  return (
    <div className="flex flex-col items-center w-full">
      {/* w-full + aspect-square でセル幅いっぱいに正方形表示 */}
      <div className="relative w-full" style={{ aspectRatio: '1 / 1' }}>
        <svg
          viewBox={`0 0 ${VB} ${VB}`}
          className="w-full h-full"
          xmlns="http://www.w3.org/2000/svg"
        >
          {/* トラック */}
          <circle
            cx={CX} cy={CY} r={RADIUS}
            fill="none"
            stroke={trackColor}
            strokeWidth={STROKE}
          />
          {/* 進捗 */}
          {percent > 0 && (
            <circle
              cx={CX} cy={CY} r={RADIUS}
              fill="none"
              stroke={fillColor}
              strokeWidth={STROKE}
              strokeDasharray={CIRCUMFERENCE}
              strokeDashoffset={dashOffset}
              strokeLinecap="butt"
              transform={`rotate(-90 ${CX} ${CY})`}
            />
          )}
        </svg>

        {/* 中央テキスト（絶対配置） */}
        <div className="absolute inset-0 flex flex-col items-center justify-center px-3 gap-1">
          <span className="text-[13px] font-semibold text-ink text-center leading-tight w-full truncate text-center">
            {category.name}
          </span>
          <span
            className="text-[17px] font-semibold leading-tight"
            style={{ color: isOver ? '#ff3b30' : '#1d1d1f' }}
          >
            ¥{spent.toLocaleString()}
          </span>
          {budget > 0 ? (
            <span className="text-[11px] text-ink-muted leading-tight">
              / ¥{budget.toLocaleString()}
            </span>
          ) : (
            <span className="text-[11px] text-ink-muted leading-tight">予算未設定</span>
          )}
        </div>
      </div>

      {isOver && (
        <span className="text-[11px] text-red-500 font-semibold mt-1">予算超過</span>
      )}
    </div>
  )
}
