import { apiFetch, extractErrorMessage, dispatchUnauthorized } from './client'
import type { ListResponse } from '../types'

export interface IncomeRecord {
  id: number
  title: string
  amount: number
  incomeDate: string
  memo: string | null
  isRegular: boolean
}

export async function listIncomeRecords(): Promise<ListResponse<IncomeRecord>> {
  const res = await apiFetch('/api/income-records?page=1&perPage=100')
  if (!res.ok) {
    if (res.status === 401) dispatchUnauthorized()
    throw new Error(await extractErrorMessage(res, '収入履歴の取得に失敗しました'))
  }
  return res.json()
}

export async function createIncomeRecord(data: {
  title: string
  amount: number
  incomeDate: string
  memo: string | null
  isRegular: boolean
}): Promise<IncomeRecord> {
  const res = await apiFetch('/api/income-records', {
    method: 'POST',
    body: JSON.stringify(data),
  })
  if (!res.ok) {
    if (res.status === 401) dispatchUnauthorized()
    throw new Error(await extractErrorMessage(res, '収入の登録に失敗しました'))
  }
  return res.json()
}

export async function deleteIncomeRecord(id: number): Promise<void> {
  const res = await apiFetch(`/api/income-records/${id}`, { method: 'DELETE' })
  if (!res.ok) {
    if (res.status === 401) dispatchUnauthorized()
    throw new Error(await extractErrorMessage(res, '収入の削除に失敗しました'))
  }
}
