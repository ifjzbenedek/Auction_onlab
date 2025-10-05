import { calculateAuctionStatus } from './auctionStatusUtils';

/**
 * Debug és teszt funkció - kiírja az aukció státusz változásokat
 */
export const testAuctionStatusCalculation = () => {
  const now = new Date();
  
  // Test case 1: Upcoming auction (starts in 1 hour, ends in 2 hours)
  const startDate1 = new Date(now.getTime() + 60 * 60 * 1000).toISOString(); // +1 hour
  const endDate1 = new Date(now.getTime() + 2 * 60 * 60 * 1000).toISOString(); // +2 hours
  const status1 = calculateAuctionStatus(startDate1, endDate1);
  console.log(' Test 1 - Upcoming auction:', status1);
  
  // Test case 2: Active auction (started 1 hour ago, ends in 1 hour)
  const startDate2 = new Date(now.getTime() - 60 * 60 * 1000).toISOString(); // -1 hour
  const endDate2 = new Date(now.getTime() + 60 * 60 * 1000).toISOString(); // +1 hour
  const status2 = calculateAuctionStatus(startDate2, endDate2);
  console.log(' Test 2 - Active auction:', status2);
  
  // Test case 3: Closed auction (started 2 hours ago, ended 1 hour ago)
  const startDate3 = new Date(now.getTime() - 2 * 60 * 60 * 1000).toISOString(); // -2 hours
  const endDate3 = new Date(now.getTime() - 60 * 60 * 1000).toISOString(); // -1 hour
  const status3 = calculateAuctionStatus(startDate3, endDate3);
  console.log(' Test 3 - Closed auction:', status3);
  
  // Test case 4: Auction without start date (immediately active)
  const endDate4 = new Date(now.getTime() + 60 * 60 * 1000).toISOString(); // +1 hour
  const status4 = calculateAuctionStatus(null, endDate4);
  console.log(' Test 4 - No start date auction:', status4);
  
  // Test case 5: Expired auction without start date
  const endDate5 = new Date(now.getTime() - 60 * 60 * 1000).toISOString(); // -1 hour
  const status5 = calculateAuctionStatus(null, endDate5);
  console.log(' Test 5 - Expired no start date auction:', status5);
};
