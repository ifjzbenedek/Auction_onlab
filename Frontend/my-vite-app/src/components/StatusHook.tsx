import { useCallback } from 'react';
import { auctionApi } from '../services/api';

export const useAuctionStatusManager = () => {
  const handleStatusChange = useCallback(async (auctionId: number, newStatus: string) => {
    try {
      console.log(`Attempting to update auction ${auctionId} status to: ${newStatus}`);
      
      await auctionApi.updateAuctionStatus(auctionId, newStatus);
      
      console.log(`Auction ${auctionId} status successfully updated to: ${newStatus}`);
    } catch (error) {
      console.error(`Failed to update status for auction ${auctionId}:`, error);
    }
  }, []);

  return { handleStatusChange };
};