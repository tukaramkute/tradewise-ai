import { lazy, Suspense } from 'react'
import { Route, Routes } from 'react-router-dom'
import { Loader } from '../components/ui/Feedback'
import { AuthLayout } from '../layouts/AuthLayout'
import { DashboardLayout } from '../layouts/DashboardLayout'
import { GuestRoute, ProtectedRoute } from './RouteGuards'

const LoginPage = lazy(() => import('../features/auth/login/LoginPage').then((module) => ({ default: module.LoginPage })))
const RegisterPage = lazy(() => import('../features/auth/register/RegisterPage').then((module) => ({ default: module.RegisterPage })))
const ForgotPasswordPage = lazy(() => import('../features/auth/forgot-password/ForgotPasswordPage').then((module) => ({ default: module.ForgotPasswordPage })))
const OtpVerificationPage = lazy(() => import('../features/auth/forgot-password/OtpVerificationPage').then((module) => ({ default: module.OtpVerificationPage })))
const ResetPasswordPage = lazy(() => import('../features/auth/reset-password/ResetPasswordPage').then((module) => ({ default: module.ResetPasswordPage })))
const ChangePasswordPage = lazy(() => import('../features/auth/change-password/ChangePasswordPage').then((module) => ({ default: module.ChangePasswordPage })))
const ProfilePage = lazy(() => import('../features/auth/profile/ProfilePage').then((module) => ({ default: module.ProfilePage })))
const ProfileSettingsPage = lazy(() => import('../features/auth/profile/ProfileSettingsPage').then((module) => ({ default: module.ProfileSettingsPage })))
const LandingPage = lazy(() => import('../pages/LandingPage').then((module) => ({ default: module.LandingPage })))
const SplashPage = lazy(() => import('../pages/SplashPage').then((module) => ({ default: module.SplashPage })))
const NotFoundPage = lazy(() => import('../pages/SystemPages').then((module) => ({ default: module.NotFoundPage })))
const SessionExpiredPage = lazy(() => import('../pages/SystemPages').then((module) => ({ default: module.SessionExpiredPage })))
const UnauthorizedPage = lazy(() => import('../pages/SystemPages').then((module) => ({ default: module.UnauthorizedPage })))

export function AppRoutes() {
  return <Suspense fallback={<Loader label="Loading workspace" />}><Routes>
    <Route path="/" element={<LandingPage />} /><Route path="/splash" element={<SplashPage />} />
    <Route element={<GuestRoute />}><Route element={<AuthLayout />}><Route path="/login" element={<LoginPage />} /><Route path="/register" element={<RegisterPage />} /><Route path="/forgot-password" element={<ForgotPasswordPage />} /><Route path="/verify-otp" element={<OtpVerificationPage />} /><Route path="/reset-password" element={<ResetPasswordPage />} /></Route></Route>
    <Route element={<ProtectedRoute />}><Route path="/app" element={<DashboardLayout />}><Route path="profile" element={<ProfilePage />} /><Route path="settings" element={<ProfileSettingsPage />} /><Route path="change-password" element={<ChangePasswordPage />} /></Route></Route>
    <Route path="/session-expired" element={<SessionExpiredPage />} /><Route path="/unauthorized" element={<UnauthorizedPage />} /><Route path="*" element={<NotFoundPage />} />
  </Routes></Suspense>
}