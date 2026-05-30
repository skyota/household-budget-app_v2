import { useEffect, useRef } from 'react'

interface Props {
  message: string
  onUndo: () => void
  onDismiss: () => void
  duration?: number
}

export default function Toast({ message, onUndo, onDismiss, duration = 2000 }: Props) {
  const timerRef = useRef<ReturnType<typeof setTimeout> | null>(null)

  useEffect(() => {
    timerRef.current = setTimeout(onDismiss, duration)
    return () => {
      if (timerRef.current) clearTimeout(timerRef.current)
    }
  }, [onDismiss, duration])

  function handleUndo() {
    if (timerRef.current) clearTimeout(timerRef.current)
    onUndo()
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
        <button
          onClick={handleUndo}
          className="text-[15px] font-sans ml-4 flex-none active:opacity-70 transition-opacity"
          style={{ color: '#2997ff' }}
        >
          元に戻す
        </button>
      </div>
    </div>
  )
}
