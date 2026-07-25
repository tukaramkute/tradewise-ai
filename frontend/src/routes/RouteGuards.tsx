import type { ReactNode } from 'react'
import { Navigate, Outlet, useLocation } from 'react-router-dom'
import { useAuth } from '../hooks/useAuth'
import { Loader } from '../components/ui/Feedback'

export function ProtectedRoute() { const { authenticated, initializing } = useAuth(); const location = useLocation(); if (initializing) return <Loader label="Securing your workspace" />; return authenticated ? <Outlet /> : <Navigate to="/login" replace state={{ from: location }} /> }
export function GuestRoute() { const { authenticated, initializing } = useAuth(); if (initializing) return <Loader />; return authenticated ? <Navigate to="/app/profile" replace /> : <Outlet /> }
export function RoleRoute({ roles, children }: { roles: string[]; children: ReactNode }) { const { user } = useAuth(); return user?.roles.some((role) => roles.includes(role)) ? children : <Navigate to="/unauthorized" replace /> }