import { useContext } from 'react'
import { LoadingContext } from '../contexts/loading-context'
export const useLoading = () => useContext(LoadingContext)