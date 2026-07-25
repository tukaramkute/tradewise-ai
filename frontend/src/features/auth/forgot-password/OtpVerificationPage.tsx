import { ArrowRight, RotateCcw } from 'lucide-react'
import { useState } from 'react'
import toast from 'react-hot-toast'
import { Navigate, useLocation, useNavigate } from 'react-router-dom'
import { Button } from '../../../components/ui/Button'
import { Alert } from '../../../components/ui/Feedback'
import { useCountdown } from '../../../hooks/useCountdown'
import { useDocumentTitle } from '../../../hooks/useDocumentTitle'
import { toAppError } from '../../../utils/errors'
import { AuthHeader } from '../components/AuthHeader'
import { OtpInput } from '../components/OtpInput'
import { authService } from '../services/auth.service'

interface RecoveryState { email: string; expiresAt?: string; devOtp?: string }
export function OtpVerificationPage() {
  useDocumentTitle('Verify OTP'); const location = useLocation(); const navigate = useNavigate(); const state = location.state as RecoveryState | null; const [code, setCode] = useState(state?.devOtp ?? ''); const [error, setError] = useState(''); const [resending, setResending] = useState(false); const timer = useCountdown(60)
  if (!state?.email) return <Navigate to="/forgot-password" replace />
  const verify = () => { if (code.length !== 6) { setError('Enter the complete 6-digit code'); return } navigate('/reset-password', { state: { email: state.email, otp: code } }) }
  const resend = async () => { setResending(true); setError(''); try { const response = await authService.forgotPassword(state.email); if (response.devOtp) setCode(response.devOtp); timer.reset(); toast.success('A new code was sent') } catch (reason) { setError(toAppError(reason).message) } finally { setResending(false) } }
  return <div><AuthHeader eyebrow="Identity check" title="Enter verification code" description={`We sent a six-digit code to ${state.email}. It expires shortly.`} /><div className="space-y-5">{error && <Alert>{error}</Alert>}{state.devOtp && <Alert tone="info">Development code: <strong>{state.devOtp}</strong></Alert>}<OtpInput value={code} onChange={(value) => { setCode(value); setError('') }} error={error} /><Button onClick={verify} icon={ArrowRight} full>Verify code</Button><div className="flex items-center justify-between text-sm text-slate-400"><span>{timer.finished ? 'Code can be resent' : `Resend available in 0:${String(timer.seconds).padStart(2, '0')}`}</span><button type="button" disabled={!timer.finished || resending} onClick={() => void resend()} className="focus-ring inline-flex items-center gap-1 rounded font-bold text-mint-400 disabled:text-slate-600"><RotateCcw className={`size-3.5 ${resending ? 'animate-spin' : ''}`} />Resend</button></div></div></div>
}