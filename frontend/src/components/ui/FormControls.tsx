import { Eye, EyeOff, Upload, X } from 'lucide-react'
import { forwardRef, useId, useState, type InputHTMLAttributes, type SelectHTMLAttributes, type TextareaHTMLAttributes } from 'react'

interface InputProps extends InputHTMLAttributes<HTMLInputElement> { label: string; error?: string; hint?: string }
export const Input = forwardRef<HTMLInputElement, InputProps>(({ label, error, hint, id, className = '', ...props }, ref) => {
  const generatedId = useId(); const inputId = id ?? generatedId
  return <label htmlFor={inputId} className="block space-y-1.5"><span className="text-sm font-semibold text-slate-300">{label}</span><input ref={ref} id={inputId} className={`field ${error ? 'border-coral-400/70' : ''} ${className}`} aria-invalid={Boolean(error)} aria-describedby={error ? `${inputId}-error` : undefined} {...props} />{error ? <span id={`${inputId}-error`} role="alert" className="block text-xs text-coral-400">{error}</span> : hint ? <span className="block text-xs text-slate-500">{hint}</span> : null}</label>
})
Input.displayName = 'Input'

export const PasswordInput = forwardRef<HTMLInputElement, InputProps>((props, ref) => { const [visible, setVisible] = useState(false); return <div className="relative"><Input {...props} ref={ref} type={visible ? 'text' : 'password'} className="pr-12" /><button type="button" onClick={() => setVisible((value) => !value)} aria-label={visible ? 'Hide password' : 'Show password'} className="focus-ring absolute right-2 top-8 rounded p-2 text-slate-400 hover:text-white">{visible ? <EyeOff className="size-4" /> : <Eye className="size-4" />}</button></div> })
PasswordInput.displayName = 'PasswordInput'

interface SelectProps extends SelectHTMLAttributes<HTMLSelectElement> { label: string; error?: string; options: readonly string[] }
export const Select = forwardRef<HTMLSelectElement, SelectProps>(({ label, error, options, id, ...props }, ref) => { const generatedId = useId(); const inputId = id ?? generatedId; return <label htmlFor={inputId} className="block space-y-1.5"><span className="text-sm font-semibold text-slate-300">{label}</span><select ref={ref} id={inputId} className="field appearance-none" {...props}><option value="">Select {label.toLowerCase()}</option>{options.map((option) => <option key={option} value={option}>{option}</option>)}</select>{error && <span role="alert" className="block text-xs text-coral-400">{error}</span>}</label> })
Select.displayName = 'Select'

interface TextAreaProps extends TextareaHTMLAttributes<HTMLTextAreaElement> { label: string; error?: string }
export const TextArea = forwardRef<HTMLTextAreaElement, TextAreaProps>(({ label, error, id, ...props }, ref) => { const generatedId = useId(); const inputId = id ?? generatedId; return <label htmlFor={inputId} className="block space-y-1.5"><span className="text-sm font-semibold text-slate-300">{label}</span><textarea ref={ref} id={inputId} className="field min-h-28 resize-y" {...props} />{error && <span role="alert" className="text-xs text-coral-400">{error}</span>}</label> })
TextArea.displayName = 'TextArea'

export function AvatarUpload({ value, onChange, compact = false }: { value?: string; onChange: (value: string) => void; compact?: boolean }) {
  const choose = (file?: File) => { if (!file) return; if (file.size > 2_000_000) return; const reader = new FileReader(); reader.onload = () => onChange(String(reader.result)); reader.readAsDataURL(file) }
  return <div className={`flex items-center ${compact ? 'gap-3' : 'gap-4'}`}><div className={`flex shrink-0 items-center justify-center overflow-hidden rounded-full border border-white/10 bg-white/5 ${compact ? 'size-12' : 'size-20'}`}>{value ? <img src={value} alt="Profile preview" className="h-full w-full object-cover" /> : <Upload className={compact ? 'size-4 text-slate-500' : 'size-6 text-slate-500'} />}</div><div className={compact ? 'flex flex-wrap items-center gap-x-3 gap-y-1' : 'space-y-2'}><label className={`focus-ring inline-flex cursor-pointer items-center gap-2 rounded-md border border-white/10 font-semibold text-slate-200 hover:bg-white/5 ${compact ? 'px-3 py-1.5 text-xs' : 'px-3 py-2 text-sm'}`}><Upload className="size-4" />Choose image<input className="sr-only" type="file" accept="image/png,image/jpeg,image/webp" onChange={(event) => choose(event.target.files?.[0])} /></label>{value && <button type="button" onClick={() => onChange('')} className="inline-flex items-center gap-1 text-xs text-slate-400 hover:text-white"><X className="size-3" />Remove</button>}<p className="text-xs text-slate-500">PNG, JPG or WebP. Max 2 MB.</p></div></div>
}