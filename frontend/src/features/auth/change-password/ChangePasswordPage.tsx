import { zodResolver } from '@hookform/resolvers/zod'
import { KeyRound, ShieldCheck } from 'lucide-react'
import { useState } from 'react'
import { useForm } from 'react-hook-form'
import toast from 'react-hot-toast'
import { z } from 'zod'
import { Button } from '../../../components/ui/Button'
import { Card, PasswordStrength } from '../../../components/ui/DataDisplay'
import { Alert } from '../../../components/ui/Feedback'
import { PasswordInput } from '../../../components/ui/FormControls'
import { useDocumentTitle } from '../../../hooks/useDocumentTitle'
import { toAppError } from '../../../utils/errors'
import { authService } from '../services/auth.service'
import { changePasswordSchema } from '../validation/auth.schemas'

type Values = z.infer<typeof changePasswordSchema>
export function ChangePasswordPage() {
  useDocumentTitle('Change password'); const [error, setError] = useState(''); const { register, handleSubmit, watch, reset, formState: { errors, isSubmitting } } = useForm<Values>({ resolver: zodResolver(changePasswordSchema), defaultValues: { oldPassword: '', newPassword: '', confirmPassword: '' } })
  const submit = async (values: Values) => { setError(''); try { await authService.changePassword(values); reset(); toast.success('Password changed successfully') } catch (reason) { const message = toAppError(reason).message; setError(message); toast.error(message) } }
  return <div className="grid gap-6 lg:grid-cols-[minmax(0,1fr)_320px]"><div><header className="mb-6"><span className="text-xs font-bold uppercase text-mint-400">Account security</span><h1 className="mt-1 font-display text-3xl font-bold">Change password</h1><p className="mt-1 text-sm text-slate-400">Rotate your credentials without interrupting your current session.</p></header><form onSubmit={handleSubmit(submit)} noValidate><Card className="space-y-5">{error && <Alert>{error}</Alert>}<PasswordInput label="Current password" autoComplete="current-password" error={errors.oldPassword?.message} {...register('oldPassword')} /><PasswordInput label="New password" autoComplete="new-password" error={errors.newPassword?.message} {...register('newPassword')} /><PasswordInput label="Confirm new password" autoComplete="new-password" error={errors.confirmPassword?.message} {...register('confirmPassword')} /><PasswordStrength password={watch('newPassword')} /><Button type="submit" icon={KeyRound} loading={isSubmitting}>Update password</Button></Card></form></div><Card className="h-fit lg:mt-[104px]"><ShieldCheck className="mb-4 size-7 text-mint-400" /><h2 className="font-display text-lg font-bold">Security checklist</h2><ul className="mt-3 space-y-3 text-sm leading-6 text-slate-400"><li>Use a password unique to TradeWise.</li><li>Avoid names, symbols, and broker account IDs.</li><li>Review active sessions after any unexpected login.</li></ul></Card></div>
}