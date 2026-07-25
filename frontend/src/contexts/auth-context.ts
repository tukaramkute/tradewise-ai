import { createContext } from 'react'
import type { LoginRequest, UpdateProfileRequest, User } from '../features/auth/types/auth.types'

export interface AuthContextValue {
  user: User | null; authenticated: boolean; initializing: boolean;
  login: (request: LoginRequest) => Promise<void>; logout: () => Promise<void>;
  refreshUser: () => Promise<void>; updateUser: (request: UpdateProfileRequest) => Promise<User>;
}
export const AuthContext = createContext<AuthContextValue | null>(null)