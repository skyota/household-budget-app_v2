import { useEffect, useRef, useState } from 'react'
import { createCategory, updateCategory, deleteCategory } from '../api/categories'
import type { Category } from '../types'

interface Props {
  categories: Category[]
  onClose: () => void
  onUpdate: (categories: Category[]) => void
}

export default function CategoryModal({ categories, onClose, onUpdate }: Props) {
  const [name, setName] = useState('')
  const [budgetAmount, setBudgetAmount] = useState('')
  const [editingId, setEditingId] = useState<number | null>(null)
  const [error, setError] = useState<string | null>(null)
  const [saving, setSaving] = useState(false)
  const [isClosing, setIsClosing] = useState(false)
  const nameInputRef = useRef<HTMLInputElement>(null)

  function requestClose() {
    setIsClosing(true)
  }

  useEffect(() => {
    nameInputRef.current?.focus()
  }, [editingId])

  function startEdit(cat: Category) {
    setEditingId(cat.id)
    setName(cat.name)
    setBudgetAmount(String(cat.budgetAmount))
    setError(null)
  }

  function cancelEdit() {
    setEditingId(null)
    setName('')
    setBudgetAmount('')
    setError(null)
  }

  async function handleSave() {
    if (!name.trim()) {
      setError('カテゴリー名は必須です')
      return
    }
    const budget = parseInt(budgetAmount || '0', 10)
    if (isNaN(budget) || budget < 0) {
      setError('予算額は0以上の整数を入力してください')
      return
    }
    setError(null)
    setSaving(true)
    try {
      let updated: Category
      if (editingId !== null) {
        updated = await updateCategory(editingId, name.trim(), budget)
        onUpdate(categories.map((c) => (c.id === editingId ? updated : c)))
      } else {
        updated = await createCategory(name.trim(), budget)
        onUpdate([...categories, updated])
      }
      cancelEdit()
    } catch (err) {
      setError(err instanceof Error ? err.message : '保存に失敗しました')
    } finally {
      setSaving(false)
    }
  }

  async function handleDelete(id: number) {
    if (!window.confirm('このカテゴリーを削除しますか？')) return
    try {
      await deleteCategory(id)
      onUpdate(categories.filter((c) => c.id !== id))
      if (editingId === id) cancelEdit()
    } catch (err) {
      setError(err instanceof Error ? err.message : '削除に失敗しました')
    }
  }

  return (
    <div
      className={`fixed inset-0 z-[60] flex items-end justify-center ${isClosing ? 'modal-fade-out' : 'modal-fade-in'}`}
      style={{ backgroundColor: 'rgba(0,0,0,0.4)' }}
      onClick={(e) => { if (e.target === e.currentTarget) requestClose() }}
    >
      <div
        className={`bg-canvas w-full max-w-lg rounded-t-[24px] max-h-[85vh] flex flex-col ${isClosing ? 'modal-slide-down' : 'modal-slide-up'}`}
        onAnimationEnd={() => { if (isClosing) onClose() }}
      >
        {/* ヘッダー */}
        <div className="flex items-center justify-between px-5 pt-5 pb-3 border-b border-hairline flex-shrink-0">
          <h2 className="font-display font-semibold text-[17px] tracking-tight text-ink">
            カテゴリー管理
          </h2>
          <button
            onClick={requestClose}
            className="w-8 h-8 flex items-center justify-center rounded-full text-ink-muted hover:bg-parchment transition-colors"
          >
            <svg width="14" height="14" viewBox="0 0 14 14" fill="none">
              <path d="M1 1l12 12M13 1L1 13" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" />
            </svg>
          </button>
        </div>

        <div className="overflow-y-auto flex-1">
          {/* カテゴリー一覧 */}
          {categories.filter((cat) => !cat.isSystem).length > 0 && (
            <div className="px-5 py-3">
              <p className="text-[12px] text-ink-muted mb-2">登録済みカテゴリー</p>
              <div className="flex flex-col gap-1">
                {categories.filter((cat) => !cat.isSystem).map((cat) => (
                  <div
                    key={cat.id}
                    className={`flex items-center gap-2 px-3 py-2.5 rounded-[12px] border transition-colors ${
                      editingId === cat.id ? 'border-primary bg-blue-50' : 'border-hairline bg-canvas'
                    }`}
                  >
                    <div className="flex-1 min-w-0">
                      <p className="text-[15px] text-ink font-sans truncate">{cat.name}</p>
                      {cat.budgetAmount > 0 && (
                        <p className="text-[12px] text-ink-muted">
                          予算: ¥{cat.budgetAmount.toLocaleString()}
                        </p>
                      )}
                    </div>
                    <button
                      onClick={() => startEdit(cat)}
                      className="flex-none w-8 h-8 flex items-center justify-center rounded-full text-ink-muted hover:bg-parchment transition-colors"
                      aria-label="編集"
                    >
                      <svg width="15" height="15" viewBox="0 0 15 15" fill="none">
                        <path
                          d="M10.5 1.5l3 3L4.5 13.5H1.5v-3L10.5 1.5z"
                          stroke="currentColor"
                          strokeWidth="1.3"
                          strokeLinecap="round"
                          strokeLinejoin="round"
                        />
                      </svg>
                    </button>
                    <button
                      onClick={() => handleDelete(cat.id)}
                      className="flex-none w-8 h-8 flex items-center justify-center rounded-full text-ink-muted hover:bg-red-50 hover:text-red-500 transition-colors"
                      aria-label="削除"
                    >
                      <svg width="14" height="14" viewBox="0 0 14 14" fill="none">
                        <path
                          d="M1 3.5h12M5 3.5V2h4v1.5M3 3.5l1 8.5h6l1-8.5"
                          stroke="currentColor"
                          strokeWidth="1.3"
                          strokeLinecap="round"
                          strokeLinejoin="round"
                        />
                      </svg>
                    </button>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* 登録・編集フォーム */}
          <div className="px-5 py-3 border-t border-hairline">
            <p className="text-[12px] text-ink-muted mb-3">
              {editingId !== null ? 'カテゴリーを編集' : '新しいカテゴリーを追加'}
            </p>

            {error && (
              <p className="text-red-500 text-[13px] mb-3">{error}</p>
            )}

            <div className="flex flex-col gap-3">
              <div className="flex flex-col gap-1">
                <label className="text-[12px] text-ink-muted">カテゴリー名 <span className="text-red-400">*</span></label>
                <input
                  ref={nameInputRef}
                  type="text"
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  placeholder="例：食費"
                  className="h-11 px-4 rounded-pill border border-hairline text-[17px] text-ink outline-none focus:border-primary transition-colors"
                />
              </div>

              <div className="flex flex-col gap-1">
                <label className="text-[12px] text-ink-muted">月の予算額（円）</label>
                <input
                  type="number"
                  value={budgetAmount}
                  onChange={(e) => setBudgetAmount(e.target.value)}
                  min={0}
                  placeholder="例：30000（0で未設定）"
                  className="h-11 px-4 rounded-pill border border-hairline text-[17px] text-ink outline-none focus:border-primary transition-colors"
                />
              </div>

              <div className="flex gap-2">
                {editingId !== null && (
                  <button
                    type="button"
                    onClick={cancelEdit}
                    className="flex-1 h-11 rounded-pill border border-hairline text-ink text-[15px] font-sans transition-colors hover:bg-parchment"
                  >
                    キャンセル
                  </button>
                )}
                <button
                  type="button"
                  onClick={handleSave}
                  disabled={saving}
                  className="flex-1 h-11 rounded-pill bg-primary text-white text-[15px] font-sans active:scale-95 transition-transform disabled:opacity-60"
                >
                  {saving ? '保存中...' : editingId !== null ? '更新する' : '追加する'}
                </button>
              </div>
            </div>
          </div>

          {/* 下部余白 */}
          <div className="h-4" />
        </div>
      </div>
    </div>
  )
}
