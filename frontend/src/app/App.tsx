import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { BrowserRouter } from 'react-router-dom'
import { Toaster } from 'react-hot-toast'
import { AuthProvider } from '../contexts/AuthContext'
import { LoadingProvider } from '../contexts/LoadingContext'
import { ThemeProvider } from '../contexts/ThemeContext'
import { useTheme } from '../hooks/useTheme'
import { AppRoutes } from '../routes/AppRoutes'

const queryClient = new QueryClient({ defaultOptions: { queries: { staleTime: 30_000, retry: 1, refetchOnWindowFocus: false }, mutations: { retry: 0 } } })

export function App() {
  return <QueryClientProvider client={queryClient}><BrowserRouter><ThemeProvider><LoadingProvider><AuthProvider><AppRoutes /><ThemeToaster /></AuthProvider></LoadingProvider></ThemeProvider></BrowserRouter></QueryClientProvider>
}

function ThemeToaster() {
  const { theme } = useTheme()
  return <Toaster position="top-right" toastOptions={{ style: { background: theme === 'dark' ? '#131b27' : '#ffffff', color: theme === 'dark' ? '#f3f6f8' : '#111827', border: `1px solid ${theme === 'dark' ? 'rgba(255,255,255,.1)' : 'rgba(15,23,42,.1)'}`, boxShadow: '0 18px 45px -24px rgba(15,23,42,.45)' } }} />
}