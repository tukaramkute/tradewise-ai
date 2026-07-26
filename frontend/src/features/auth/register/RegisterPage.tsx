import { zodResolver } from '@hookform/resolvers/zod'
import { ArrowRight } from 'lucide-react'
import { useState } from 'react'
import { useForm } from 'react-hook-form'
import toast from 'react-hot-toast'
import { Link, useNavigate } from 'react-router-dom'
import { Button } from '../../../components/ui/Button'
import { PasswordStrength } from '../../../components/ui/DataDisplay'
import { Alert } from '../../../components/ui/Feedback'
import { AvatarUpload, Input, PasswordInput, Select } from '../../../components/ui/FormControls'
import { countries, currencies, timeZones } from '../../../constants/options'
import { useDocumentTitle } from '../../../hooks/useDocumentTitle'
import { toAppError } from '../../../utils/errors'
import { AuthHeader } from '../components/AuthHeader'
import { authService } from '../services/auth.service'
import { registerSchema, type RegisterValues } from '../validation/auth.schemas'

export function RegisterPage() {
  useDocumentTitle('Create account'); const navigate = useNavigate(); const [serverError, setServerError] = useState('')
  const { register, handleSubmit, watch, setValue, setError, formState: { errors, isSubmitting } } = useForm<RegisterValues>({ resolver: zodResolver(registerSchema), mode: 'onChange', defaultValues: { firstName: '', lastName: '', username: '', email: '', phoneNumber: '', password: '', confirmPassword: '', country: '', timeZone: Intl.DateTimeFormat().resolvedOptions().timeZone, preferredCurrency: 'USD', profileImageUrl: '', terms: false } })
  const submit = async ({ terms: _terms, ...request }: RegisterValues) => { setServerError(''); try { await authService.register(request); toast.success('Account created. Sign in to continue.'); navigate('/login') } catch (error) { const appError = toAppError(error); Object.entries(appError.fields ?? {}).forEach(([field, message]) => setError(field as keyof RegisterValues, { message })); setServerError(appError.message); toast.error(appError.message) } }
  const password = watch('password'); const image = watch('profileImageUrl')
  return <form className="register-form" onSubmit={handleSubmit(submit)} noValidate><AuthHeader eyebrow="Trader onboarding" title="Create your account" description="Set your identity and trading preferences." />
    <div className="space-y-3">{serverError && <Alert>{serverError}</Alert>}<AvatarUpload compact value={image} onChange={(value) => setValue('profileImageUrl', value, { shouldDirty: true })} /><div className="grid gap-x-3 gap-y-2 sm:grid-cols-2 xl:grid-cols-3"><Input label="First name" autoComplete="given-name" error={errors.firstName?.message} {...register('firstName')} /><Input label="Last name" autoComplete="family-name" error={errors.lastName?.message} {...register('lastName')} /><Input label="Username" autoComplete="username" error={errors.username?.message} {...register('username')} /><Input label="Email" type="email" autoComplete="email" error={errors.email?.message} {...register('email')} /><Input label="Phone number" type="tel" autoComplete="tel" placeholder="+14155552671" error={errors.phoneNumber?.message} {...register('phoneNumber')} /><Select label="Country" options={countries} error={errors.country?.message} {...register('country')} /><Select label="Time zone" options={timeZones} error={errors.timeZone?.message} {...register('timeZone')} /><Select label="Preferred currency" options={currencies} error={errors.preferredCurrency?.message} {...register('preferredCurrency')} /></div>
      <div className="grid gap-3 sm:grid-cols-2"><PasswordInput label="Password" autoComplete="new-password" error={errors.password?.message} {...register('password')} /><PasswordInput label="Confirm password" autoComplete="new-password" error={errors.confirmPassword?.message} {...register('confirmPassword')} /></div><PasswordStrength compact password={password} />
      <div className="flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between"><div><label className="flex cursor-pointer items-center gap-2 text-xs text-slate-400"><input type="checkbox" className="size-4 shrink-0 accent-mint-400" {...register('terms')} /><span>I agree to the Terms of Service and Privacy Policy.</span></label>{errors.terms && <p role="alert" className="mt-1 text-xs text-coral-400">{errors.terms.message}</p>}</div><Button type="submit" loading={isSubmitting} icon={ArrowRight} className="shrink-0">Create account</Button></div><p className="text-center text-xs text-slate-400">Already have an account? <Link to="/login" className="font-bold text-white hover:text-mint-400">Sign in</Link></p>
    </div></form>
}