import { zodResolver } from '@hookform/resolvers/zod'
import { Save } from 'lucide-react'
import { useEffect, useState } from 'react'
import { useForm } from 'react-hook-form'
import toast from 'react-hot-toast'
import { z } from 'zod'
import { Button } from '../../../components/ui/Button'
import { Card } from '../../../components/ui/DataDisplay'
import { Alert } from '../../../components/ui/Feedback'
import { AvatarUpload, Input, Select } from '../../../components/ui/FormControls'
import { countries, currencies, timeZones } from '../../../constants/options'
import { useDocumentTitle } from '../../../hooks/useDocumentTitle'
import { useAuth } from '../../../hooks/useAuth'
import { toAppError } from '../../../utils/errors'
import { updateProfileSchema } from '../validation/auth.schemas'

type Values = z.infer<typeof updateProfileSchema>
export function ProfileSettingsPage() {
  useDocumentTitle('Profile settings'); const { user, updateUser } = useAuth(); const [error, setError] = useState(''); const { register, handleSubmit, reset, watch, setValue, formState: { errors, isSubmitting, isDirty } } = useForm<Values>({ resolver: zodResolver(updateProfileSchema), defaultValues: { firstName: '', lastName: '', phoneNumber: '', country: '', timeZone: '', preferredCurrency: '', profileImageUrl: '' } })
  useEffect(() => { if (user) reset({ firstName: user.firstName, lastName: user.lastName, phoneNumber: user.phoneNumber, country: user.country, timeZone: user.timeZone, preferredCurrency: user.preferredCurrency, profileImageUrl: user.profileImageUrl ?? '' }) }, [user, reset])
  const submit = async (values: Values) => { setError(''); try { await updateUser(values); toast.success('Profile updated') } catch (reason) { const message = toAppError(reason).message; setError(message); toast.error(message) } }
  return <div className="space-y-6"><header><span className="text-xs font-bold uppercase text-mint-400">Personalization</span><h1 className="mt-1 font-display text-3xl font-bold">Profile settings</h1><p className="mt-1 text-sm text-slate-400">Keep your identity and regional trading preferences current.</p></header><form onSubmit={handleSubmit(submit)} noValidate><Card className="space-y-6">{error && <Alert>{error}</Alert>}<AvatarUpload value={watch('profileImageUrl')} onChange={(value) => setValue('profileImageUrl', value, { shouldDirty: true })} /><div className="grid gap-4 sm:grid-cols-2"><Input label="First name" error={errors.firstName?.message} {...register('firstName')} /><Input label="Last name" error={errors.lastName?.message} {...register('lastName')} /><Input label="Phone number" type="tel" error={errors.phoneNumber?.message} {...register('phoneNumber')} /><Select label="Country" options={countries} error={errors.country?.message} {...register('country')} /><Select label="Time zone" options={timeZones} error={errors.timeZone?.message} {...register('timeZone')} /><Select label="Preferred currency" options={currencies} error={errors.preferredCurrency?.message} {...register('preferredCurrency')} /></div><div className="grid gap-4 border-t border-white/[.07] pt-6 sm:grid-cols-3"><Input label="Email" value={user?.email ?? ''} readOnly disabled /><Input label="Username" value={user?.username ?? ''} readOnly disabled /><Input label="Role" value={user?.roles.join(', ') ?? ''} readOnly disabled /></div><div className="flex justify-end"><Button type="submit" icon={Save} loading={isSubmitting} disabled={!isDirty}>Save changes</Button></div></Card></form></div>
}