import { AnimatePresence, motion } from 'framer-motion'
import { Moon, Sun } from 'lucide-react'
import { useTheme } from '../../hooks/useTheme'

export function ThemeToggle({ className = '' }: { className?: string }) {
  const { theme, toggleTheme } = useTheme()
  const isDark = theme === 'dark'

  return <motion.button
    type="button"
    onClick={toggleTheme}
    whileHover={{ scale: 1.04 }}
    whileTap={{ scale: .94 }}
    className={`focus-ring relative grid size-10 place-items-center overflow-hidden rounded-md border border-white/10 bg-white/[.04] text-slate-400 transition-colors hover:border-mint-400/30 hover:text-mint-400 ${className}`}
    aria-label={`Switch to ${isDark ? 'light' : 'dark'} mode`}
    title={`Switch to ${isDark ? 'light' : 'dark'} mode`}
  >
    <AnimatePresence mode="wait" initial={false}>
      <motion.span key={theme} initial={{ opacity: 0, rotate: -35, scale: .7 }} animate={{ opacity: 1, rotate: 0, scale: 1 }} exit={{ opacity: 0, rotate: 35, scale: .7 }} transition={{ duration: .18 }}>
        {isDark ? <Sun className="size-[18px]" /> : <Moon className="size-[18px]" />}
      </motion.span>
    </AnimatePresence>
  </motion.button>
}