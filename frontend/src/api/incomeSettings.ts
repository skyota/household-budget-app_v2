import { apiFetch, extractErrorMessage, dispatchUnauthorized } from './client'
import type { IncomeSetting, ListResponse } from '../types'

export async function listIncomeSettings(): Promise<ListResponse<IncomeSetting>> {
  const res = await apiFetch('/api/income-settings?page=1&perPage=100')
  if (!res.ok) {
    if (res.status === 401) dispatchUnauthorized()
    throw new Error(await extractErrorMessage(res, '収入設定の取得に失敗しました'))
  }
  return res.json()
}

export async function createIncomeSetting(data: {
  title: string
  amount: number
  incomeDate: number
  memo: string | null
  isAutoGenerate: boolean
}): Promise<IncomeSetting> {
  const res = await apiFetch('/api/income-settings', {
    method: 'POST',
    body: JSON.stringify(data),
  })
  if (!res.ok) {
    if (res.status === 401) dispatchUnauthorized()
    throw new Error(await extractErrorMessage(res, '収入設定の登録に失敗しました'))
  }
  return res.json()
}

export async function updateIncomeSetting(id: number, data: {
  title: string
  amount: number
  incomeDate: number
  memo: string | null
  isAutoGenerate: boolean
}): Promise<IncomeSetting> {
  const res = await apiFetch(`/api/income-settings/${id}`, {
    method: 'PATCH',
    body: JSON.stringify(data),
  })
  if (!res.ok) {
    if (res.status === 401) dispatchUnauthorized()
    throw new Error(await extractErrorMessage(res, '収入設定の更新に失敗しました'))
  }
  return res.json()
}

export async function deleteIncomeSetting(id: number): Promise<void> {
  const res = await apiFetch(`/api/income-settings/${id}`, { method: 'DELETE' })
  if (!res.ok) {
    if (res.status === 401) dispatchUnauthorized()
    throw new Error(await extractErrorMessage(res, '収入設定の削除に失敗しました'))
  }
}
