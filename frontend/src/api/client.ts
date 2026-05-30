export async function apiFetch(path: string, options?: RequestInit): Promise<Response> {
  return fetch(path, {
    credentials: 'include',
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...options?.headers,
    },
  })
}
