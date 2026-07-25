import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { BrowserRouter } from 'react-router-dom'
import { Toaster } from 'react-hot-toast'
import { AuthProvider } from '../contexts/AuthContext'
import { LoadingProvider } from '../contexts/LoadingContext'
import { ThemeProvider } from '../contexts/ThemeContext'
import { AppRoutes } from '../routes/AppRoutes'

const queryClient = new QueryClient({ defaultOptions: { queries: { staleTime: 30_000, retry: 1, refetchOnWindowFocus: false }, mutations: { retry: 0 } } })

export function App() {
  return <QueryClientProvider client={queryClient}><BrowserRouter><ThemeProvider><LoadingProvider><AuthProvider><AppRoutes /><Toaster position="top-right" toastOptions={{ style: { background: '#131b27', color: '#f3f6f8', border: '1px solid rgba(255,255,255,.1)' } }} /></AuthProvider></LoadingProvider></ThemeProvider></BrowserRouter></QueryClientProvider>
}