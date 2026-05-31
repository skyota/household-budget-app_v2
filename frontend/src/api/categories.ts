import { apiFetch, extractErrorMessage, dispatchUnauthorized } from './client'
import type { Category } from '../types'

export async function listCategories(): Promise<Category[]> {
  const res = await apiFetch('/categories')
  if (!res.ok) {
    if (res.status === 401) dispatchUnauthorized()
    throw new Error(await extractErrorMessage(res, 'カテゴリの取得に失敗しました'))
  }
  return res.json()
}

export async function createCategory(name: string, budgetAmount: number): Promise<Category> {
  const res = await apiFetch('/categories', {
    method: 'POST',
    body: JSON.stringify({ name, budgetAmount }),
  })
  if (!res.ok) {
    if (res.status === 401) dispatchUnauthorized()
    throw new Error(await extractErrorMessage(res, 'カテゴリの作成に失敗しました'))
  }
  return res.json()
}

export async function updateCategory(id: number, name: string, budgetAmount: number): Promise<Category> {
  const res = await apiFetch(`/categories/${id}`, {
    method: 'PUT',
    body: JSON.stringify({ name, budgetAmount }),
  })
  if (!res.ok) {
    if (res.status === 401) dispatchUnauthorized()
    throw new Error(await extractErrorMessage(res, 'カテゴリの更新に失敗しました'))
  }
  return res.json()
}

export async function deleteCategory(id: number): Promise<void> {
  const res = await apiFetch(`/categories/${id}`, { method: 'DELETE' })
  if (!res.ok) {
    if (res.status === 401) dispatchUnauthorized()
    throw new Error(await extractErrorMessage(res, 'カテゴリの削除に失敗しました'))
  }
}
