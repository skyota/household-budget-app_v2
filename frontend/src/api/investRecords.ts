import { apiFetch, extractErrorMessage, dispatchUnauthorized } from './client'
import type { InvestRecord, InvestType, ListResponse } from '../types'

export async function listInvestRecords(): Promise<ListResponse<InvestRecord>> {
  const res = await apiFetch('/api/invest-records?page=1&perPage=500')
  if (!res.ok) {
    if (res.status === 401) dispatchUnauthorized()
    throw new Error(await extractErrorMessage(res, '投資履歴の取得に失敗しました'))
  }
  return res.json()
}

export async function createInvestRecord(data: {
  amount: number
  investDate: string
  investType: InvestType
  memo: string | null
}): Promise<InvestRecord> {
  const res = await apiFetch('/api/invest-records', {
    method: 'POST',
    body: JSON.stringify(data),
  })
  if (!res.ok) {
    if (res.status === 401) dispatchUnauthorized()
    throw new Error(await extractErrorMessage(res, '投資履歴の登録に失敗しました'))
  }
  return res.json()
}

export async function deleteInvestRecord(id: number): Promise<void> {
  const res = await apiFetch(`/api/invest-records/${id}`, { method: 'DELETE' })
  if (!res.ok) {
    if (res.status === 401) dispatchUnauthorized()
    throw new Error(await extractErrorMessage(res, '投資履歴の削除に失敗しました'))
  }
}
