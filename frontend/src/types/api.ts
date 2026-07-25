export interface FieldError { field: string; rejectedValue?: unknown; message: string }
export interface ApiErrorDetails { code: string; path: string; status: number; fieldErrors?: FieldError[] }
export interface ApiResponse<T> { success: boolean; message: string; data: T; error: ApiErrorDetails | null; timestamp: string }
export interface AppError { message: string; code?: string; status?: number; fields?: Record<string, string> }