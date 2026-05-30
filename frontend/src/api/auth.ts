import { apiFetch } from './client'

export async function login(username: string, password: string): Promise<{ id: string; username: string }> {
  const res = await apiFetch('/user/login', {
    method: 'POST',
    body: JSON.stringify({ username, password }),
  })
  if (!res.ok) {
    const data = await res.json()
    throw new Error(data.message ?? 'ログインに失敗しました')
  }
  return res.json()
}

export async function register(username: string, password: string): Promise<{ id: string; username: string }> {
  const res = await apiFetch('/user/register', {
    method: 'POST',
    body: JSON.stringify({ username, password }),
  })
  if (!res.ok) {
    const data = await res.json()
    throw new Error(data.message ?? '登録に失敗しました')
  }
  return res.json()
}

export async function logout(): Promise<void> {
  await apiFetch('/user/logout', { method: 'POST' })
}

export async function getMe(): Promise<{ id: string; username: string | null }> {
  const res = await apiFetch('/user/me')
  if (!res.ok) throw new Error('unauthorized')
  return res.json()
}
