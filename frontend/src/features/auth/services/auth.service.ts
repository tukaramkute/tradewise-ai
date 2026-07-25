import type { ApiResponse } from '../../../types/api'
import { apiClient } from '../../../services/api-client'
import type { AuthResponse, ChangePasswordRequest, LoginRequest, OtpResponse, RegisterRequest, ResetPasswordRequest, TokenResponse, UpdateProfileRequest, User } from '../types/auth.types'

export const authService = {
  login: async (request: LoginRequest) => (await apiClient.post<ApiResponse<AuthResponse>>('/auth/login', request)).data.data,
  register: async (request: RegisterRequest) => (await apiClient.post<ApiResponse<User>>('/auth/register', request)).data.data,
  refresh: async (refreshToken: string) => (await apiClient.post<ApiResponse<TokenResponse>>('/auth/refresh', { refreshToken })).data.data,
  logout: async (refreshToken: string) => { await apiClient.post('/auth/logout', { refreshToken }) },
  forgotPassword: async (email: string) => (await apiClient.post<ApiResponse<OtpResponse>>('/auth/forgot-password', { email })).data.data,
  resetPassword: async (request: ResetPasswordRequest) => { await apiClient.post('/auth/reset-password', request) },
  me: async () => (await apiClient.get<ApiResponse<User>>('/users/me')).data.data,
  updateProfile: async (request: UpdateProfileRequest) => (await apiClient.put<ApiResponse<User>>('/users/me', request)).data.data,
  changePassword: async (request: ChangePasswordRequest) => { await apiClient.put('/users/me/password', request) },
}