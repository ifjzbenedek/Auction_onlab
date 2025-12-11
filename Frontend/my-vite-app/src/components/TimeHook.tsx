import { useState, useEffect } from 'react'

export const useTimer = (refreshInterval: number = 1000) => {
  const [currentTime, setCurrentTime] = useState(new Date())

  useEffect(() => {
    const timer = setInterval(() => {
      setCurrentTime(new Date())
    }, refreshInterval)

    return () => clearInterval(timer)
  }, [refreshInterval])

  return currentTime
}