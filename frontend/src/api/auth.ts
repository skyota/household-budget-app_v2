import { apiFetch, extractErrorMessage } from './client'

export async function login(username: string, password: string): Promise<{ id: string; username: string }> {
  const res = await apiFetch('/user/login', {
    method: 'POST',
    body: JSON.stringify({ username, password }),
  })
  if (!res.ok) throw new Error(await extractErrorMessage(res, 'ログインに失敗しました'))
  return res.json()
}

export async function register(username: string, password: string): Promise<{ id: string; username: string }> {
  const res = await apiFetch('/user/register', {
    method: 'POST',
    body: JSON.stringify({ username, password }),
  })
  if (!res.ok) throw new Error(await extractErrorMessage(res, '登録に失敗しました'))
  return res.json()
}

export async function logout(): Promise<void> {
  const res = await apiFetch('/user/logout', { method: 'POST' })
  if (!res.ok) throw new Error(await extractErrorMessage(res, 'ログアウトに失敗しました'))
}

export async function getMe(): Promise<{ id: string; username: string | null }> {
  const res = await apiFetch('/user/me')
  if (!res.ok) throw new Error('unauthorized')
  return res.json()
}

export async function getApiToken(): Promise<{ token: string }> {
  const res = await apiFetch('/user/token')
  if (!res.ok) throw new Error('トークンの取得に失敗しました')
  return res.json()
}

export async function regenerateApiToken(): Promise<{ token: string }> {
  const res = await apiFetch('/user/token/regenerate', { method: 'POST' })
  if (!res.ok) throw new Error('トークンの再生成に失敗しました')
  return res.json()
}
