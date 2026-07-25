import { zodResolver } from '@hookform/resolvers/zod'
import { ArrowRight, LockKeyhole } from 'lucide-react'
import { useState } from 'react'
import { useForm } from 'react-hook-form'
import toast from 'react-hot-toast'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { Button } from '../../../components/ui/Button'
import { Alert } from '../../../components/ui/Feedback'
import { Input, PasswordInput } from '../../../components/ui/FormControls'
import { useDocumentTitle } from '../../../hooks/useDocumentTitle'
import { useAuth } from '../../../hooks/useAuth'
import { toAppError } from '../../../utils/errors'
import { AuthHeader } from '../components/AuthHeader'
import { loginSchema, type LoginValues } from '../validation/auth.schemas'

export function LoginPage() {
  useDocumentTitle('Sign in'); const { login } = useAuth(); const navigate = useNavigate(); const location = useLocation(); const [serverError, setServerError] = useState('')
  const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm<LoginValues>({ resolver: zodResolver(loginSchema), defaultValues: { identifier: '', password: '', remember: true } })
  const submit = async ({ identifier, password }: LoginValues) => { setServerError(''); try { await login({ identifier, password }); toast.success('Welcome back'); const destination = (location.state as { from?: { pathname?: string } } | null)?.from?.pathname ?? '/app/profile'; navigate(destination, { replace: true }) } catch (error) { const appError = toAppError(error); setServerError(appError.message); toast.error(appError.message) } }
  return <form onSubmit={handleSubmit(submit)} noValidate><AuthHeader eyebrow="Secure access" title="Welcome back" description="Review your edge, risk, and decisions in one focused workspace." />
    <div className="space-y-5">{serverError && <Alert>{serverError}</Alert>}<Input label="Email or username" autoComplete="username" placeholder="trader@example.com" error={errors.identifier?.message} {...register('identifier')} /><PasswordInput label="Password" autoComplete="current-password" placeholder="Enter your password" error={errors.password?.message} {...register('password')} />
      <div className="flex items-center justify-between gap-4"><label className="flex cursor-pointer items-center gap-2 text-sm text-slate-400"><input type="checkbox" className="size-4 accent-mint-400" {...register('remember')} />Remember me</label><Link to="/forgot-password" className="focus-ring rounded text-sm font-semibold text-mint-400 hover:text-mint-500">Forgot password?</Link></div>
      <Button type="submit" loading={isSubmitting} icon={ArrowRight} full>Sign in</Button><div className="flex items-center gap-3 text-xs text-slate-600"><span className="h-px flex-1 bg-white/10" /><LockKeyhole className="size-3" />Encrypted session<span className="h-px flex-1 bg-white/10" /></div><p className="text-center text-sm text-slate-400">New to TradeWise? <Link to="/register" className="font-bold text-white hover:text-mint-400">Create an account</Link></p>
    </div></form>
}
