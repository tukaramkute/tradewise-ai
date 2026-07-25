import { useEffect, useState } from 'react'

export function useCountdown(initialSeconds: number) {
  const [seconds, setSeconds] = useState(initialSeconds)
  useEffect(() => { if (seconds <= 0) return; const timer = window.setTimeout(() => setSeconds((value) => value - 1), 1000); return () => window.clearTimeout(timer) }, [seconds])
  return { seconds, reset: () => setSeconds(initialSeconds), finished: seconds === 0 }
}