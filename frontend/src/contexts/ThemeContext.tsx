import { useEffect, useState, type ReactNode } from 'react'
import { ThemeContext, type Theme } from './theme-context'

export function ThemeProvider({ children }: { children: ReactNode }) {
  const [theme, setTheme] = useState<Theme>(() => (localStorage.getItem('tradewise.theme') as Theme) || 'dark')
  useEffect(() => { document.documentElement.dataset.theme = theme; localStorage.setItem('tradewise.theme', theme) }, [theme])
  return <ThemeContext.Provider value={{ theme, toggleTheme: () => setTheme((value) => value === 'dark' ? 'light' : 'dark') }}>{children}</ThemeContext.Provider>
}
