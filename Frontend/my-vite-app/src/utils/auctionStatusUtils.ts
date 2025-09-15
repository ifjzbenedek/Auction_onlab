import { parseBackendDate } from './timezoneUtils';

export type AuctionStatus = 'UPCOMING' | 'ACTIVE' | 'CLOSED';

export interface AuctionStatusInfo {
  status: AuctionStatus;
  isExpired: boolean;
  hasStarted: boolean;
}

/**
 * Kiszámolja az aukció aktuális státuszát az időpontok alapján
 * @param startDate - Az aukció kezdési időpontja (opcionális)
 * @param expiredDate - Az aukció befejezési időpontja
 * @returns Az aktuális státusz információ
 */
export const calculateAuctionStatus = (
  startDate: string | null | undefined, 
  expiredDate: string
): AuctionStatusInfo => {
  const now = new Date();
  
  // ⭐ ÚJ: Időzóna-tudatos dátum kezelés
  const endTime = parseBackendDate(expiredDate);
  
  // ⭐ VÉDEKEZŐ LOGIKA: Ellenőrizzük, hogy az adatok érvényesek-e
  if (startDate) {
    const startTime = parseBackendDate(startDate);
    
    // Ha a startDate későbbi, mint az expiredDate, akkor rossz adatok vannak
    if (startTime.getTime() >= endTime.getTime()) {
      console.warn('🚨 Invalid auction data: startDate is after expiredDate!', {
        startDate,
        expiredDate,
        startTime: startTime.toISOString(),
        endTime: endTime.toISOString()
      });
      
      // Ebben az esetben kezeljük úgy, mintha nincs startDate
      return calculateAuctionStatus(null, expiredDate);
    }
  }
  
  // Ha nincs startDate megadva, akkor az aukció azonnal aktív
  let startTime: Date;
  
  if (startDate) {
    startTime = parseBackendDate(startDate);
  } else {
    // Ha nincs startDate, akkor az aukció azonnal aktív (régi viselkedés)
    startTime = new Date(0); // 1970-01-01, tehát minden "now" későbbi lesz
  }
  
  const hasStarted = now >= startTime;
  const isExpired = now >= endTime;
  
  let status: AuctionStatus;
  
  if (!hasStarted) {
    // Az aukció még nem kezdődött el
    status = 'UPCOMING';
  } else if (hasStarted && !isExpired) {
    // Az aukció elkezdődött, de még nem járt le
    status = 'ACTIVE';
  } else {
    // Az aukció lejárt
    status = 'CLOSED';
  }
  
  return {
    status,
    isExpired,
    hasStarted
  };
};

/**
 * Meghatározza a státusz színét a MUI színpaletta alapján
 */
export const getStatusColor = (status: AuctionStatus): 'default' | 'primary' | 'secondary' | 'error' | 'info' | 'success' | 'warning' => {
  switch (status) {
    case 'ACTIVE':
      return 'success';
    case 'CLOSED':
      return 'error';
    case 'UPCOMING':
      return 'warning';
    default:
      return 'default';
  }
};

/**
 * Meghatározza a státusz megjelenített szövegét
 */
export const getStatusLabel = (status: AuctionStatus): string => {
  switch (status) {
    case 'ACTIVE':
      return 'Ongoing';
    case 'CLOSED':
      return 'Finished';
    case 'UPCOMING':
      return 'Upcoming';
    default:
      return 'Unknown';
  }
};
