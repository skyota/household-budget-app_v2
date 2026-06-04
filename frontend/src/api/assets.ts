import { apiFetch, extractErrorMessage, dispatchUnauthorized } from './client'
import type { AssetSummary } from '../types'

export async function getAssetSummary(): Promise<AssetSummary> {
  const res = await apiFetch('/api/assets/summary')
  if (!res.ok) {
    if (res.status === 401) dispatchUnauthorized()
    throw new Error(await extractErrorMessage(res, '資産情報の取得に失敗しました'))
  }
  return res.json()
}
