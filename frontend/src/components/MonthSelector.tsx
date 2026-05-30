interface Props {
  year: number
  month: number
  onChange: (year: number, month: number) => void
}

export default function MonthSelector({ year, month, onChange }: Props) {
  function prev() {
    if (month === 1) {
      onChange(year - 1, 12)
    } else {
      onChange(year, month - 1)
    }
  }

  function next() {
    if (month === 12) {
      onChange(year + 1, 1)
    } else {
      onChange(year, month + 1)
    }
  }

  return (
    <div className="flex items-center justify-center gap-6 py-3">
      <button
        onClick={prev}
        className="w-9 h-9 flex items-center justify-center rounded-full text-ink hover:bg-parchment transition-colors"
        aria-label="前の月"
      >
        <svg width="8" height="14" viewBox="0 0 8 14" fill="none">
          <path d="M7 1L1 7L7 13" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
        </svg>
      </button>

      <span className="font-display font-semibold text-[17px] tracking-tight text-ink min-w-[120px] text-center">
        {year}年{month}月
      </span>

      <button
        onClick={next}
        className="w-9 h-9 flex items-center justify-center rounded-full text-ink hover:bg-parchment transition-colors"
        aria-label="次の月"
      >
        <svg width="8" height="14" viewBox="0 0 8 14" fill="none">
          <path d="M1 1L7 7L1 13" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
        </svg>
      </button>
    </div>
  )
}
