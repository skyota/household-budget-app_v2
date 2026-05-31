import { apiFetch, extractErrorMessage, dispatchUnauthorized } from './client'
import type { ExpenseListResponse } from '../types'

export async function listExpenses(
  year: number,
  month: number,
  page = 1,
  perPage = 20
): Promise<ExpenseListResponse> {
  const params = new URLSearchParams({
    year: String(year),
    month: String(month),
    page: String(page),
    perPage: String(perPage),
  })
  const res = await apiFetch(`/expenses?${params}`)
  if (!res.ok) {
    if (res.status === 401) dispatchUnauthorized()
    throw new Error(await extractErrorMessage(res, '支出の取得に失敗しました'))
  }
  return res.json()
}

export async function createExpense(data: {
  title: string
  price: number
  expenseDate: string
  categoryId: number | null
  memo: string | null
}): Promise<{ id: number }> {
  const res = await apiFetch('/expenses', {
    method: 'POST',
    body: JSON.stringify(data),
  })
  if (!res.ok) {
    if (res.status === 401) dispatchUnauthorized()
    throw new Error(await extractErrorMessage(res, '支出の登録に失敗しました'))
  }
  return res.json()
}

export async function updateExpense(id: number, data: {
  title: string
  price: number
  expenseDate: string
  categoryId: number | null
  memo: string | null
}): Promise<void> {
  const res = await apiFetch(`/expenses/${id}`, {
    method: 'PUT',
    body: JSON.stringify(data),
  })
  if (!res.ok) {
    if (res.status === 401) dispatchUnauthorized()
    throw new Error(await extractErrorMessage(res, '支出の更新に失敗しました'))
  }
}

export async function deleteExpense(id: number): Promise<void> {
  const res = await apiFetch(`/expenses/${id}`, { method: 'DELETE' })
  if (!res.ok) {
    if (res.status === 401) dispatchUnauthorized()
    throw new Error(await extractErrorMessage(res, '支出の削除に失敗しました'))
  }
}
