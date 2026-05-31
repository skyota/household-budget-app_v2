import type { ReactNode } from 'react'
import GlobalNav from './GlobalNav'

interface Props {
  children: ReactNode
}

export default function AuthLayout({ children }: Props) {
  return (
    <div className="min-h-screen bg-parchment flex justify-center">
      <div className="w-full max-w-lg bg-canvas min-h-screen flex flex-col shadow-[0_0_40px_rgba(0,0,0,0.06)]">
        {/* ヘッダー */}
        <header className="flex items-center px-5 h-12 border-b border-hairline flex-shrink-0">
          <span className="font-display font-semibold text-[17px] tracking-tight text-ink">
            {/* ロゴ（後で変更可能） */}
            1s-kakeibo
          </span>
        </header>

        {/* メインコンテンツ */}
        <main className="flex-1 overflow-y-auto pb-24">
          {children}
        </main>

        <GlobalNav />
      </div>
    </div>
  )
}
