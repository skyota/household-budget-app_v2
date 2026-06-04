import { useEffect, useRef } from 'react'

interface Props {
  message: string
  onUndo?: () => void
  onDismiss: () => void
  duration?: number
}

export default function Toast({ message, onUndo, onDismiss, duration = 2000 }: Props) {
  const timerRef = useRef<ReturnType<typeof setTimeout> | null>(null)
  const onDismissRef = useRef(onDismiss)
  const onUndoRef = useRef(onUndo)

  // コールバックの最新参照を保持（タイマーはリセットしない）
  useEffect(() => { onDismissRef.current = onDismiss }, [onDismiss])
  useEffect(() => { onUndoRef.current = onUndo }, [onUndo])

  // タイマーはマウント時に1回だけ設定（duration は変化しない前提）
  useEffect(() => {
    timerRef.current = setTimeout(() => onDismissRef.current(), duration)
    return () => {
      if (timerRef.current) clearTimeout(timerRef.current)
    }
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  function handleUndo() {
    if (timerRef.current) clearTimeout(timerRef.current)
    onUndoRef.current?.()
  }

  return (
    <div
      className="fixed left-4 right-4 z-[55] toast-enter"
      style={{ bottom: '84px' }}
    >
      <div
        className="flex items-center justify-between px-4 py-3 rounded-[14px] text-white"
        style={{
          backgroundColor: '#272729',
          boxShadow: '0 4px 20px rgba(0,0,0,0.25)',
        }}
      >
        <span className="text-[15px] font-sans">{message}</span>
        {onUndo && (
          <button
            onClick={handleUndo}
            className="text-[15px] font-sans ml-4 flex-none active:opacity-70 transition-opacity"
            style={{ color: '#2997ff' }}
          >
            元に戻す
          </button>
        )}
      </div>
    </div>
  )
}
