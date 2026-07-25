import { LoaderCircle, type LucideIcon } from 'lucide-react'
import type { ButtonHTMLAttributes } from 'react'

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> { loading?: boolean; icon?: LucideIcon; variant?: 'primary' | 'secondary' | 'danger' | 'ghost'; full?: boolean }
export function Button({ children, loading, icon: Icon, variant = 'primary', full, className = '', disabled, ...props }: ButtonProps) {
  const styles = { primary: 'bg-mint-400 text-ink-950 hover:bg-mint-500 shadow-[0_10px_30px_-14px_rgba(69,224,173,.8)]', secondary: 'border border-white/12 bg-white/[.05] text-white hover:bg-white/[.09]', danger: 'bg-coral-400 text-ink-950 hover:bg-red-400', ghost: 'text-slate-300 hover:bg-white/[.06] hover:text-white' }
  return <button className={`focus-ring inline-flex min-h-11 items-center justify-center gap-2 rounded-md px-4 py-2.5 text-sm font-bold transition hover:-translate-y-px active:translate-y-0 active:scale-[.98] disabled:cursor-not-allowed disabled:opacity-50 ${styles[variant]} ${full ? 'w-full' : ''} ${className}`} disabled={disabled || loading} {...props}>
    {loading ? <LoaderCircle className="size-4 animate-spin" aria-hidden /> : Icon ? <Icon className="size-4" aria-hidden /> : null}{children}
  </button>
}