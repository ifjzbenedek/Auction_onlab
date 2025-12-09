export const parseBackendDate = (dateString: string): Date => {
  if (dateString.includes('Z') || dateString.includes('+') || dateString.includes('-')) {
    return new Date(dateString)
  }
  
  const parsedDate = new Date(dateString)
  
  if (isNaN(parsedDate.getTime())) {
    console.warn('Invalid date string received from backend:', dateString)
    return new Date()
  }
  
  return parsedDate
}

export const needsTimezoneCorrection = (dateString: string): boolean => {
  return !dateString.includes('Z') && !dateString.includes('+') && !dateString.includes('-')
}
