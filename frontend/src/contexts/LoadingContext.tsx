import { useState, type ReactNode } from 'react'
import { LoadingContext } from './loading-context'
export function LoadingProvider({ children }: { children: ReactNode }) { const [loading, setLoading] = useState(false); return <LoadingContext.Provider value={{ loading, setLoading }}>{children}</LoadingContext.Provider> }
