export async function apiFetch(path: string, options?: RequestInit): Promise<Response> {
  try {
    return await fetch(path, {
      credentials: 'include',
      ...options,
      headers: {
        'Content-Type': 'application/json',
        ...options?.headers,
      },
    })
  } catch {
    throw new Error('ネットワークエラーが発生しました。接続を確認してください。')
  }
}

export async function extractErrorMessage(res: Response, fallback: string): Promise<string> {
  try {
    const data = await res.json()
    return data.message ?? fallback
  } catch {
    return fallback
  }
}
