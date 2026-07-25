import axios from 'axios'
import type { ApiResponse, AppError } from '../types/api'

export function toAppError(error: unknown): AppError {
  if (axios.isAxiosError<ApiResponse<null>>(error)) {
    const payload = error.response?.data
    return {
      message: payload?.message || error.message || 'Something went wrong',
      code: payload?.error?.code,
      status: error.response?.status,
      fields: Object.fromEntries(payload?.error?.fieldErrors?.map(({ field, message }) => [field, message]) ?? []),
    }
  }
  return { message: error instanceof Error ? error.message : 'Something went wrong' }
}