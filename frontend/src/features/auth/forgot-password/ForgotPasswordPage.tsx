import { zodResolver } from '@hookform/resolvers/zod'
import { ArrowRight, MailCheck } from 'lucide-react'
import { useState } from 'react'
import { useForm } from 'react-hook-form'
import toast from 'react-hot-toast'
import { Link, useNavigate } from 'react-router-dom'
import { z } from 'zod'
import { Button } from '../../../components/ui/Button'
import { Alert } from '../../../components/ui/Feedback'
import { Input } from '../../../components/ui/FormControls'
import { useDocumentTitle } from '../../../hooks/useDocumentTitle'
import { toAppError } from '../../../utils/errors'
import { AuthHeader } from '../components/AuthHeader'
import { authService } from '../services/auth.service'
import { emailSchema } from '../validation/auth.schemas'

type Values = z.infer<typeof emailSchema>
export function ForgotPasswordPage() {
  useDocumentTitle('Forgot password'); const navigate = useNavigate(); const [error, setError] = useState(''); const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm<Values>({ resolver: zodResolver(emailSchema), defaultValues: { email: '' } })
  const submit = async ({ email }: Values) => { setError(''); try { const otp = await authService.forgotPassword(email); toast.success('Verification code sent'); navigate('/verify-otp', { state: { email, expiresAt: otp.expiresAt, devOtp: otp.devOtp } }) } catch (reason) { const message = toAppError(reason).message; setError(message); toast.error(message) } }
  return <form onSubmit={handleSubmit(submit)} noValidate><AuthHeader eyebrow="Account recovery" title="Reset access securely" description="Enter the email linked to your account. We will send a short-lived verification code." /><div className="space-y-5">{error && <Alert>{error}</Alert>}<div className="mb-2 grid size-12 place-items-center rounded-md bg-mint-400/10 text-mint-400"><MailCheck className="size-5" /></div><Input label="Email address" type="email" autoComplete="email" placeholder="trader@example.com" error={errors.email?.message} {...register('email')} /><Button type="submit" icon={ArrowRight} loading={isSubmitting} full>Send OTP</Button><p className="text-center text-sm text-slate-400"><Link to="/login" className="font-bold text-white hover:text-mint-400">Back to sign in</Link></p></div></form>
}