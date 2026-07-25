import axios, { type AxiosError, type InternalAxiosRequestConfig } from 'axios'
import type { ApiResponse } from '../types/api'
import type { TokenResponse } from '../features/auth/types/auth.types'
import { tokenStorage } from '../utils/storage'

const baseURL = import.meta.env.VITE_API_BASE_URL ?? '/api/v1'
export const apiClient = axios.create({ baseURL, timeout: Number(import.meta.env.VITE_API_TIMEOUT_MS ?? 15000) })
const refreshClient = axios.create({ baseURL, timeout: 15000 })

apiClient.interceptors.request.use((config) => {
  const token = tokenStorage.getAccess()
  const userId = tokenStorage.getUserId()
  if (token) config.headers.Authorization = `Bearer ${token}`
  if (userId) config.headers['X-User-Id'] = userId
  return config
})

type RetryConfig = InternalAxiosRequestConfig & { _retry?: boolean }
let refreshing: Promise<string> | null = null
apiClient.interceptors.response.use(undefined, async (error: AxiosError) => {
  const config = error.config as RetryConfig | undefined
  if (error.response?.status !== 401 || !config || config._retry || config.url?.includes('/auth/refresh')) return Promise.reject(error)
  const refreshToken = tokenStorage.getRefresh()
  if (!refreshToken) { tokenStorage.clear(); window.dispatchEvent(new Event('auth:expired')); return Promise.reject(error) }
  config._retry = true
  try {
    refreshing ??= refreshClient.post<ApiResponse<TokenResponse>>('/auth/refresh', { refreshToken })
      .then(({ data }) => { tokenStorage.set(data.data.accessToken, data.data.refreshToken, tokenStorage.getUserId() ?? undefined); return data.data.accessToken })
      .finally(() => { refreshing = null })
    config.headers.Authorization = `Bearer ${await refreshing}`
    return apiClient(config)
  } catch (refreshError) {
    tokenStorage.clear(); window.dispatchEvent(new Event('auth:expired')); return Promise.reject(refreshError)
  }
})