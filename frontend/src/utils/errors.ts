import axios from 'axios'
import type { ApiResponse, AppError } from '../types/api'

export function toAppError(error: unknown): AppError {
  if (axios.isAxiosError<ApiResponse<null>>(error)) {
    const payload = error.response?.data
    const status = error.response?.status
    const fallbackMessage = getRequestErrorMessage(status, error.code)
    return {
      message: payload?.message || fallbackMessage,
      code: payload?.error?.code,
      status,
      fields: Object.fromEntries(payload?.error?.fieldErrors?.map(({ field, message }) => [field, message]) ?? []),
    }
  }
  return { message: error instanceof Error ? error.message : 'Something went wrong' }
}

function getRequestErrorMessage(status?: number, code?: string) {
  if (code === 'ECONNABORTED') return 'The request timed out. Please try again.'
  if (!status) return 'Unable to connect to TradeWise. Check your connection and try again.'
  if ([502, 503, 504].includes(status)) return 'TradeWise is temporarily unavailable. Please try again in a few moments.'
  if (status === 429) return 'Too many attempts. Please wait a moment and try again.'
  if (status >= 500) return 'Something went wrong on our side. Please try again shortly.'
  return 'We could not complete your request. Please check your details and try again.'
}