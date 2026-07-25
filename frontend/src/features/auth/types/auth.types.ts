export type UserStatus = 'ACTIVE' | 'INACTIVE' | 'BANNED' | 'SUSPENDED'
export type UserRole = 'USER' | 'ADMIN' | 'TRADER' | string

export interface User {
  id: string; firstName: string; lastName: string; fullName: string; username: string;
  email: string; phoneNumber: string; country: string; timeZone: string;
  preferredCurrency: string; profileImageUrl?: string; status: UserStatus;
  roles: UserRole[]; emailVerified: boolean; createdDate: string; updatedDate: string;
}
export interface LoginRequest { identifier: string; password: string }
export interface RegisterRequest {
  firstName: string; lastName: string; username: string; email: string; phoneNumber: string;
  password: string; confirmPassword: string; country: string; timeZone: string;
  preferredCurrency: string; profileImageUrl?: string;
}
export interface AuthResponse { accessToken: string; refreshToken: string; tokenType: string; expiresIn: number; user: User }
export interface TokenResponse { accessToken: string; refreshToken: string; tokenType: string; expiresIn: number }
export interface OtpResponse { message: string; expiresAt: string; devOtp?: string }
export interface ResetPasswordRequest { email: string; otp: string; newPassword: string; confirmPassword: string }
export interface ChangePasswordRequest { oldPassword: string; newPassword: string; confirmPassword: string }
export type UpdateProfileRequest = Partial<Pick<User, 'firstName' | 'lastName' | 'phoneNumber' | 'profileImageUrl' | 'timeZone' | 'country' | 'preferredCurrency'>>