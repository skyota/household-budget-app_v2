import { apiFetch, extractErrorMessage, dispatchUnauthorized } from './client'
import type { CashBalance, BalanceType, ListResponse } from '../types'

export async function listCashBalances(): Promise<ListResponse<CashBalance>> {
  const res = await apiFetch('/api/cash-balances?page=1&perPage=100')
  if (!res.ok) {
    if (res.status === 401) dispatchUnauthorized()
    throw new Error(await extractErrorMessage(res, '現金残高の取得に失敗しました'))
  }
  return res.json()
}

export async function createCashBalance(data: {
  amount: number
  balanceDate: string
  balanceType: BalanceType
  memo: string | null
}): Promise<CashBalance> {
  const res = await apiFetch('/api/cash-balances', {
    method: 'POST',
    body: JSON.stringify(data),
  })
  if (!res.ok) {
    if (res.status === 401) dispatchUnauthorized()
    throw new Error(await extractErrorMessage(res, '現金残高の登録に失敗しました'))
  }
  return res.json()
}

export async function deleteCashBalance(id: number): Promise<void> {
  const res = await apiFetch(`/api/cash-balances/${id}`, { method: 'DELETE' })
  if (!res.ok) {
    if (res.status === 401) dispatchUnauthorized()
    throw new Error(await extractErrorMessage(res, '現金残高の削除に失敗しました'))
  }
}
