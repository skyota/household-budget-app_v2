import { Link, useLocation } from 'react-router-dom'

interface NavItem {
  path: string
  label: string
  icon: React.ReactNode
  slot: 'left' | 'right'
}

interface CenterItem {
  path: string
  label: string
  icon: React.ReactNode
}

const NAV_ITEMS: NavItem[] = [
  {
    path: '/history',
    label: '支払い履歴',
    slot: 'left',
    icon: (
      <svg width="22" height="22" viewBox="0 0 22 22" fill="none">
        <path
          d="M3 6h16M3 11h16M3 16h10"
          stroke="currentColor"
          strokeWidth="1.5"
          strokeLinecap="round"
        />
      </svg>
    ),
  },
]

const CENTER_ITEM: CenterItem = {
  path: '/input',
  label: '入力',
  icon: (
    <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
      <path
        d="M12 5v14M5 12h14"
        stroke="currentColor"
        strokeWidth="2"
        strokeLinecap="round"
      />
    </svg>
  ),
}

export default function GlobalNav() {
  const location = useLocation()

  const isActive = (path: string) => location.pathname === path

  return (
    <nav className="fixed bottom-0 left-1/2 -translate-x-1/2 w-full max-w-lg z-50 bg-canvas border-t border-hairline">
      <div className="relative flex items-end h-16 px-4">
        {/* 左エリア */}
        <div className="flex-1 flex justify-start">
          {NAV_ITEMS.filter((item) => item.slot === 'left').map((item) => (
            <Link
              key={item.path}
              to={item.path}
              className={`flex flex-col items-center gap-1 pt-2 pb-2 px-3 transition-colors ${
                isActive(item.path) ? 'text-primary' : 'text-ink-muted'
              }`}
            >
              {item.icon}
              <span className="text-[11px] font-sans leading-none">{item.label}</span>
            </Link>
          ))}
        </div>

        {/* 中央：浮き上がり円形ボタン */}
        <div className="flex-none flex flex-col items-center" style={{ marginBottom: '6px' }}>
          <Link
            to={CENTER_ITEM.path}
            className="flex flex-col items-center justify-center w-14 h-14 rounded-full transition-all active:scale-95 bg-primary text-white"
            style={{ boxShadow: '0 4px 16px rgba(0, 102, 204, 0.4)' }}
          >
            {CENTER_ITEM.icon}
          </Link>
          <span
            className={`text-[11px] font-sans leading-none mt-1 ${
              isActive(CENTER_ITEM.path) ? 'text-primary' : 'text-ink-muted'
            }`}
          >
            支出を記録
          </span>
        </div>

        {/* 右エリア（将来の拡張用） */}
        <div className="flex-1 flex justify-end">
          {NAV_ITEMS.filter((item) => item.slot === 'right').map((item) => (
            <Link
              key={item.path}
              to={item.path}
              className={`flex flex-col items-center gap-1 pt-2 pb-2 px-3 transition-colors ${
                isActive(item.path) ? 'text-primary' : 'text-ink-muted'
              }`}
            >
              {item.icon}
              <span className="text-[11px] font-sans leading-none">{item.label}</span>
            </Link>
          ))}
        </div>
      </div>

      {/* iOS ホームインジケーター用の余白 */}
      <div className="h-safe-area-inset-bottom" />
    </nav>
  )
}
