import { TimeLeft } from '../types/TimeUtils/TimeLeft'
import { parseBackendDate } from './timezoneUtils'
import { calculateAuctionStatus, AuctionStatus } from './auctionStatusUtils'

export const calculateTimeLeft = (expiredDate: string): TimeLeft => {
  const now = new Date()
  const end = parseBackendDate(expiredDate)
  const diff = end.getTime() - now.getTime()
  const totalSeconds = Math.floor(diff / 1000)

  if (diff <= 0) {
    return { timeString: "00:00:00", isExpired: true, totalSeconds: 0 }
  }

  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))
  const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60))
  const seconds = Math.floor((diff % (1000 * 60)) / 1000)

  let timeString = ""
  if (days > 0) {
    timeString = `${days}d ${hours.toString().padStart(2, "0")}:${minutes.toString().padStart(2, "0")}:${seconds.toString().padStart(2, "0")}`
  } else {
    timeString = `${hours.toString().padStart(2, "0")}:${minutes.toString().padStart(2, "0")}:${seconds.toString().padStart(2, "0")}`
  }

  return { timeString, isExpired: false, totalSeconds }
}

export const calculateSmartTimeLeft = (
  startDate: string | null | undefined, 
  expiredDate: string
): TimeLeft & { targetEvent: 'start' | 'end' | 'expired'; status: AuctionStatus } => {
  const { status } = calculateAuctionStatus(startDate, expiredDate)
  const now = new Date()
  
  let targetDate: Date
  let targetEvent: 'start' | 'end' | 'expired'
  
  if (status === 'UPCOMING' && startDate) {
    targetDate = parseBackendDate(startDate)
    targetEvent = 'start'
  } else {
    targetDate = parseBackendDate(expiredDate)
    targetEvent = status === 'CLOSED' ? 'expired' : 'end'
  }
  
  const diff = targetDate.getTime() - now.getTime()
  const totalSeconds = Math.floor(diff / 1000)

  if (diff <= 0) {
    return { 
      timeString: "00:00:00", 
      isExpired: true, 
      totalSeconds: 0, 
      targetEvent,
      status 
    }
  }

  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))
  const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60))
  const seconds = Math.floor((diff % (1000 * 60)) / 1000)

  let timeString = ""
  if (days > 0) {
    timeString = `${days}d ${hours.toString().padStart(2, "0")}:${minutes.toString().padStart(2, "0")}:${seconds.toString().padStart(2, "0")}`
  } else {
    timeString = `${hours.toString().padStart(2, "0")}:${minutes.toString().padStart(2, "0")}:${seconds.toString().padStart(2, "0")}`
  }

  return { 
    timeString, 
    isExpired: false, 
    totalSeconds,
    targetEvent,
    status
  }
}