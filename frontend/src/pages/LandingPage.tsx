import { Link, Navigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

function PhoneFrame({ src, alt }: { src: string; alt: string }) {
  return (
    <div className="w-[260px] md:w-[300px] rounded-[3rem] overflow-hidden border-[8px] border-[#1d1d1f] shadow-[rgba(0,0,0,0.22)_3px_5px_30px_0px] mx-auto">
      <img src={src} alt={alt} className="w-full block" />
    </div>
  )
}

export default function LandingPage() {
  const { userId, loading } = useAuth()

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-canvas">
        <div className="w-8 h-8 border-2 border-primary border-t-transparent rounded-full animate-spin" />
      </div>
    )
  }

  if (userId) {
    return <Navigate to="/input" replace />
  }

  return (
    <div className="font-sans text-ink">
      {/* Global Nav */}
      <nav className="fixed top-0 left-0 right-0 z-50 h-11 bg-black flex items-center px-5">
        <div className="flex items-center gap-2 flex-1">
          <img src="/app-icon.png" alt="1s-kakeibo" className="w-6 h-6 rounded-[6px]" />
          <span className="text-white text-[13px] font-semibold tracking-[-0.12px]">
            1s-kakeibo
          </span>
        </div>
        <div className="flex items-center gap-2">
          <Link
            to="/login"
            className="text-white text-[12px] tracking-[-0.12px] px-[14px] py-1.5 rounded-[8px] bg-[#1d1d1f] active:scale-95 transition-transform"
          >
            ログイン
          </Link>
          <Link
            to="/register"
            className="text-white text-[12px] tracking-[-0.12px] px-[14px] py-1.5 rounded-[8px] bg-primary active:scale-95 transition-transform"
          >
            登録
          </Link>
        </div>
      </nav>

      {/* Hero */}
      <section className="bg-canvas pt-28 pb-20 px-5 text-center">
        <div className="max-w-[980px] mx-auto">
          <p className="text-[14px] font-semibold text-primary tracking-[-0.224px] mb-4">
            家計管理アプリ
          </p>
          <h1 className="font-display text-[40px] md:text-[56px] font-semibold leading-[1.07] tracking-[-0.28px] text-ink mb-5">
            1秒で記録。<br />毎月が見える。
          </h1>
          <p className="text-[17px] md:text-[21px] text-[#6e6e73] leading-[1.19] mb-10">
            シンプルな家計管理を、毎日の習慣に。
          </p>
          <div className="flex flex-wrap gap-4 justify-center">
            <Link
              to="/register"
              className="px-[22px] py-[11px] bg-primary text-white rounded-pill text-[17px] leading-[1.47] tracking-[-0.374px] active:scale-95 transition-transform"
            >
              今すぐ始める
            </Link>
            <Link
              to="/login"
              className="px-[22px] py-[11px] border border-primary text-primary rounded-pill text-[17px] leading-[1.47] tracking-[-0.374px] active:scale-95 transition-transform"
            >
              ログイン
            </Link>
          </div>
        </div>
      </section>

      {/* Feature 01: 支出入力 (parchment) */}
      <section className="bg-parchment py-20 px-5">
        <div className="max-w-[980px] mx-auto text-center">
          <p className="text-[14px] font-semibold text-primary tracking-[-0.224px] mb-3">
            Feature 01
          </p>
          <h2 className="font-display text-[28px] md:text-[40px] font-semibold leading-[1.10] text-ink mb-4">
            すぐに記録できる
          </h2>
          <p className="text-[17px] md:text-[21px] text-[#6e6e73] leading-[1.47] max-w-[540px] mx-auto mb-12">
            タイトルと金額を入力するだけ。<br />
            カテゴリを選んで、すぐに保存できます。
          </p>
          <PhoneFrame src="/screenshot-input.png" alt="支出入力画面" />
        </div>
      </section>

      {/* Feature 02: カテゴリ別支出 (dark) */}
      <section className="py-20 px-5" style={{ backgroundColor: '#46464a' }}>
        <div className="max-w-[980px] mx-auto text-center">
          <p className="text-[14px] font-semibold text-primary-on-dark tracking-[-0.224px] mb-3">
            Feature 02
          </p>
          <h2 className="font-display text-[28px] md:text-[40px] font-semibold leading-[1.10] text-white mb-4">
            カテゴリ別に把握
          </h2>
          <p className="text-[17px] md:text-[21px] text-[#cccccc] leading-[1.47] max-w-[540px] mx-auto mb-12">
            カテゴリごとの予算と実績を<br />
            ひと目で確認できます。
          </p>
          <PhoneFrame src="/screenshot-gauge.png" alt="カテゴリ別支出画面" />
        </div>
      </section>

      {/* Feature 03: iOSショートカット (parchment) */}
      <section className="bg-parchment py-20 px-5">
        <div className="max-w-[980px] mx-auto text-center">
          <p className="text-[14px] font-semibold text-primary tracking-[-0.224px] mb-3">
            Feature 03
          </p>
          <h2 className="font-display text-[28px] md:text-[40px] font-semibold leading-[1.10] text-ink mb-4">
            iOSショートカット対応
          </h2>
          <p className="text-[17px] md:text-[21px] text-[#6e6e73] leading-[1.47] max-w-[540px] mx-auto mb-12">
            ホーム画面からワンタップで記録画面を開く。<br />
            毎日の記録をさらにスムーズに。
          </p>
          <div className="flex justify-center">
            <div className="bg-white rounded-card px-8 py-10 shadow-[rgba(0,0,0,0.22)_3px_5px_30px_0px] w-[260px] md:w-[300px]">
              <img
                src="/app-icon.png"
                alt="1s-kakeibo"
                className="w-16 h-16 rounded-[18px] mx-auto mb-5"
              />
              <p className="text-[17px] font-semibold text-ink mb-1 tracking-[-0.374px]">
                1s-kakeibo
              </p>
              <p className="text-[14px] text-ink-muted tracking-[-0.224px]">
                iOSショートカット
              </p>
              <div className="mt-6 pt-6 border-t border-hairline">
                <p className="text-[14px] text-[#6e6e73] leading-[1.6] tracking-[-0.224px]">
                  ホーム画面に追加して<br />
                  いつでもすぐに起動できます
                </p>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="bg-canvas py-20 px-5 text-center">
        <div className="max-w-[980px] mx-auto">
          <h2 className="font-display text-[28px] md:text-[40px] font-semibold leading-[1.10] tracking-[-0.28px] text-ink mb-4">
            今日から始めよう
          </h2>
          <p className="text-[17px] md:text-[21px] text-[#6e6e73] leading-[1.19] mb-10">
            無料で使えます。
          </p>
          <Link
            to="/register"
            className="inline-block px-[28px] py-[14px] bg-primary text-white rounded-pill text-[18px] font-light tracking-[0] active:scale-95 transition-transform"
          >
            今すぐ始める
          </Link>
        </div>
      </section>

      {/* Footer */}
      <footer className="bg-parchment py-16 px-5">
        <div className="max-w-[980px] mx-auto text-center">
          <p className="text-[12px] text-[#7a7a7a] tracking-[-0.12px]">
            © 2026 1s-kakeibo
          </p>
        </div>
      </footer>
    </div>
  )
}
