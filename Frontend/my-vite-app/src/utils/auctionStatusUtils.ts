import { parseBackendDate } from './timezoneUtils'

export type AuctionStatus = 'UPCOMING' | 'ACTIVE' | 'CLOSED'

export interface AuctionStatusInfo {
  status: AuctionStatus
  isExpired: boolean
  hasStarted: boolean
}

export const calculateAuctionStatus = (
  startDate: string | null | undefined, 
  expiredDate: string
): AuctionStatusInfo => {
  const now = new Date()
  const endTime = parseBackendDate(expiredDate)
  
  if (startDate) {
    const startTime = parseBackendDate(startDate)
    
    if (startTime.getTime() >= endTime.getTime()) {
      return calculateAuctionStatus(null, expiredDate)
    }
  }
  
  let startTime: Date
  
  if (startDate) {
    startTime = parseBackendDate(startDate)
  } else {
    startTime = new Date(0)
  }
  
  const hasStarted = now >= startTime
  const isExpired = now >= endTime
  
  let status: AuctionStatus
  
  if (!hasStarted) {
    status = 'UPCOMING'
  } else if (hasStarted && !isExpired) {
    status = 'ACTIVE'
  } else {
    status = 'CLOSED'
  }
  
  return {
    status,
    isExpired,
    hasStarted
  }
}

export const getStatusColor = (status: AuctionStatus): 'default' | 'primary' | 'secondary' | 'error' | 'info' | 'success' | 'warning' => {
  switch (status) {
    case 'ACTIVE':
      return 'success'
    case 'CLOSED':
      return 'error'
    case 'UPCOMING':
      return 'warning'
    default:
      return 'default'
  }
}

export const getStatusLabel = (status: AuctionStatus): string => {
  switch (status) {
    case 'ACTIVE':
      return 'Ongoing'
    case 'CLOSED':
      return 'Finished'
    case 'UPCOMING':
      return 'Upcoming'
    default:
      return 'Unknown'
  }
}
