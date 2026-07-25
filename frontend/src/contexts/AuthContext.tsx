import { useEffect, useState, type ReactNode } from 'react'
import { useNavigate } from 'react-router-dom'
import toast from 'react-hot-toast'
import { authService } from '../features/auth/services/auth.service'
import type { LoginRequest, UpdateProfileRequest, User } from '../features/auth/types/auth.types'
import { tokenStorage } from '../utils/storage'
import { toAppError } from '../utils/errors'
import { AuthContext } from './auth-context'

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null)
  const [initializing, setInitializing] = useState(true)
  const navigate = useNavigate()

  const refreshUser = async () => { const current = await authService.me(); setUser(current) }
  useEffect(() => {
    const bootstrap = async () => {
      if (!tokenStorage.getAccess()) { setInitializing(false); return }
      try { await refreshUser() } catch { tokenStorage.clear(); setUser(null) } finally { setInitializing(false) }
    }
    void bootstrap()
  }, [])
  useEffect(() => {
    const expire = () => { setUser(null); navigate('/session-expired', { replace: true }) }
    window.addEventListener('auth:expired', expire)
    return () => window.removeEventListener('auth:expired', expire)
  }, [navigate])

  const login = async (request: LoginRequest) => {
    const response = await authService.login(request)
    tokenStorage.set(response.accessToken, response.refreshToken, response.user.id)
    setUser(response.user)
  }
  const logout = async () => {
    const refreshToken = tokenStorage.getRefresh()
    try { if (refreshToken) await authService.logout(refreshToken) } catch (error) { toast.error(toAppError(error).message) }
    finally { tokenStorage.clear(); setUser(null); navigate('/login', { replace: true }) }
  }
  const updateUser = async (request: UpdateProfileRequest) => { const updated = await authService.updateProfile(request); setUser(updated); return updated }

  return <AuthContext.Provider value={{ user, authenticated: Boolean(user), initializing, login, logout, refreshUser, updateUser }}>{children}</AuthContext.Provider>
}
