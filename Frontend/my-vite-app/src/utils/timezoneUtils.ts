/**
 * Dátum kezelő függvények backend kommunikációhoz
 * 
 * A backend és frontend most konzisztensen kezeli az időzónákat.
 * Nem végzünk időzóna konverziókat, csak érvényességi ellenőrzéseket.
 */

/**
 * Parse-olja a backend dátumokat (mostantól konverziók nélkül)
 * @param dateString - A backend által küldött dátum string
 * @returns Date objektum a pontos idővel
 */
export const parseBackendDate = (dateString: string): Date => {
  // Ha a dátum string már tartalmaz időzóna információt (Z vagy +XX:XX), 
  // akkor használjuk közvetlenül
  if (dateString.includes('Z') || dateString.includes('+') || dateString.includes('-')) {
    return new Date(dateString);
  }
  
  // A backend és frontend most ugyanazt az időzónát használja
  // Nem kell konvertálni, egyszerűen parse-oljuk
  const parsedDate = new Date(dateString);
  
  // Ellenőrizzük, hogy érvényes dátum-e
  if (isNaN(parsedDate.getTime())) {
    console.warn('Invalid date string received from backend:', dateString);
    return new Date(); // Fallback az aktuális időre
  }
  
  console.log('Date parsing (no conversion):', {
    original: dateString,
    parsed: parsedDate.toISOString(),
    localTime: parsedDate.toLocaleString("hu-HU"),
    now: new Date().toISOString()
  });
  
  return parsedDate;
};

/**
 * Ellenőrzi, hogy szükséges-e időzóna korrekció
 * @param dateString - A backend dátum string
 * @returns true, ha korrekció szükséges
 */
export const needsTimezoneCorrection = (dateString: string): boolean => {
  return !dateString.includes('Z') && !dateString.includes('+') && !dateString.includes('-');
};

/**
 * Debug információkat jelenít meg a dátum kezelésről
 */
export const debugTimezoneIssues = (startDate: string | null, expiredDate: string) => {
  console.group(' Date Parsing Debug Info');
  
  console.log('Raw backend data:');
  console.log('- startDate:', startDate);
  console.log('- expiredDate:', expiredDate);
  
  if (startDate) {
    const parsedStart = parseBackendDate(startDate);
    
    console.log('StartDate parsing:');
    console.log('- Parsed result:', parsedStart.toISOString());
    console.log('- Local display:', parsedStart.toLocaleString("hu-HU"));
  }
  
  const parsedEnd = parseBackendDate(expiredDate);
  
  console.log('ExpiredDate parsing:');
  console.log('- Parsed result:', parsedEnd.toISOString());
  console.log('- Local display:', parsedEnd.toLocaleString("hu-HU"));
  
  console.log('Current time:', new Date().toISOString());
  
  console.groupEnd();
};
