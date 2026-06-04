import { apiFetch, extractErrorMessage, dispatchUnauthorized } from './client'
import type { InvestSetting } from '../types'

export async function getInvestSetting(): Promise<InvestSetting> {
  const res = await apiFetch('/api/invest-settings')
  if (!res.ok) {
    if (res.status === 401) dispatchUnauthorized()
    throw new Error(await extractErrorMessage(res, '投資設定の取得に失敗しました'))
  }
  return res.json()
}

export async function createInvestSetting(data: {
  amount: number
  investDate: number
}): Promise<InvestSetting> {
  const res = await apiFetch('/api/invest-settings', {
    method: 'POST',
    body: JSON.stringify(data),
  })
  if (!res.ok) {
    if (res.status === 401) dispatchUnauthorized()
    throw new Error(await extractErrorMessage(res, '投資設定の登録に失敗しました'))
  }
  return res.json()
}

export async function updateInvestSetting(data: {
  amount: number
  investDate: number
}): Promise<InvestSetting> {
  const res = await apiFetch('/api/invest-settings', {
    method: 'PATCH',
    body: JSON.stringify(data),
  })
  if (!res.ok) {
    if (res.status === 401) dispatchUnauthorized()
    throw new Error(await extractErrorMessage(res, '投資設定の更新に失敗しました'))
  }
  return res.json()
}
