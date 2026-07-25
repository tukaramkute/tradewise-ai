import { zodResolver } from '@hookform/resolvers/zod'
import { CheckCircle2 } from 'lucide-react'
import { useState } from 'react'
import { useForm } from 'react-hook-form'
import toast from 'react-hot-toast'
import { Navigate, useLocation, useNavigate } from 'react-router-dom'
import { z } from 'zod'
import { Button } from '../../../components/ui/Button'
import { PasswordStrength } from '../../../components/ui/DataDisplay'
import { Alert } from '../../../components/ui/Feedback'
import { PasswordInput } from '../../../components/ui/FormControls'
import { useDocumentTitle } from '../../../hooks/useDocumentTitle'
import { toAppError } from '../../../utils/errors'
import { AuthHeader } from '../components/AuthHeader'
import { authService } from '../services/auth.service'
import { resetSchema } from '../validation/auth.schemas'

type Values = z.infer<typeof resetSchema>
export function ResetPasswordPage() {
  useDocumentTitle('Reset password'); const location = useLocation(); const navigate = useNavigate(); const state = location.state as { email?: string; otp?: string } | null; const [serverError, setServerError] = useState(''); const { register, handleSubmit, watch, formState: { errors, isSubmitting } } = useForm<Values>({ resolver: zodResolver(resetSchema), defaultValues: { email: state?.email ?? '', otp: state?.otp ?? '', newPassword: '', confirmPassword: '' } })
  if (!state?.email || !state.otp) return <Navigate to="/forgot-password" replace />
  const submit = async (values: Values) => { setServerError(''); try { await authService.resetPassword(values); toast.success('Password reset successfully'); navigate('/login', { replace: true }) } catch (reason) { const message = toAppError(reason).message; setServerError(message); toast.error(message) } }
  return <form onSubmit={handleSubmit(submit)} noValidate><AuthHeader eyebrow="New credentials" title="Choose a strong password" description="Your new password must be unique and meet all security requirements." /><div className="space-y-5">{serverError && <Alert>{serverError}</Alert>}<PasswordInput label="New password" autoComplete="new-password" error={errors.newPassword?.message} {...register('newPassword')} /><PasswordInput label="Confirm password" autoComplete="new-password" error={errors.confirmPassword?.message} {...register('confirmPassword')} /><PasswordStrength password={watch('newPassword')} /><Button type="submit" icon={CheckCircle2} loading={isSubmitting} full>Reset password</Button></div></form>
}