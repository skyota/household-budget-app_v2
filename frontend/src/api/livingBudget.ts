import { apiFetch, dispatchUnauthorized, extractErrorMessage } from './client'
import type { LivingBudget } from '../types'

export async function getLivingBudget(): Promise<LivingBudget> {
  const res = await apiFetch('/api/living-budget')
  if (!res.ok) {
    if (res.status === 401) dispatchUnauthorized()
    throw new Error(await extractErrorMessage(res, '生活費予算の取得に失敗しました'))
  }
  return res.json()
}

export async function saveLivingBudget(amount: number): Promise<LivingBudget> {
  const res = await apiFetch('/api/living-budget', {
    method: 'PUT',
    body: JSON.stringify({ amount }),
  })
  if (!res.ok) {
    if (res.status === 401) dispatchUnauthorized()
    throw new Error(await extractErrorMessage(res, '生活費予算の保存に失敗しました'))
  }
  return res.json()
}
