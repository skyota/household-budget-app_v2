import { apiFetch, extractErrorMessage, dispatchUnauthorized } from './client'
import type { FixedExpense, ListResponse } from '../types'

export async function listFixedExpenses(): Promise<ListResponse<FixedExpense>> {
  const res = await apiFetch('/api/fixed-expenses?page=1&perPage=100')
  if (!res.ok) {
    if (res.status === 401) dispatchUnauthorized()
    throw new Error(await extractErrorMessage(res, '固定費の取得に失敗しました'))
  }
  return res.json()
}

export async function createFixedExpense(data: {
  title: string
  price: number
  fixedExpenseDate: number
  memo: string | null
}): Promise<FixedExpense> {
  const res = await apiFetch('/api/fixed-expenses', {
    method: 'POST',
    body: JSON.stringify(data),
  })
  if (!res.ok) {
    if (res.status === 401) dispatchUnauthorized()
    throw new Error(await extractErrorMessage(res, '固定費の登録に失敗しました'))
  }
  return res.json()
}

export async function updateFixedExpense(id: number, data: {
  title: string
  price: number
  fixedExpenseDate: number
  memo: string | null
}): Promise<FixedExpense> {
  const res = await apiFetch(`/api/fixed-expenses/${id}`, {
    method: 'PATCH',
    body: JSON.stringify(data),
  })
  if (!res.ok) {
    if (res.status === 401) dispatchUnauthorized()
    throw new Error(await extractErrorMessage(res, '固定費の更新に失敗しました'))
  }
  return res.json()
}

export async function deleteFixedExpense(id: number): Promise<void> {
  const res = await apiFetch(`/api/fixed-expenses/${id}`, { method: 'DELETE' })
  if (!res.ok) {
    if (res.status === 401) dispatchUnauthorized()
    throw new Error(await extractErrorMessage(res, '固定費の削除に失敗しました'))
  }
}
