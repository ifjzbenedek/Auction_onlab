import type React from "react";
import { createContext, useState, useContext, useMemo, useCallback } from "react"; // useCallback importálása

// Az UploadAuction.tsx-ben definiált típus
interface UploadedImage {
  id: string;
  file: File;
  preview: string;
}

interface AuctionCreationState {
  description: string;
  images: UploadedImage[];
}

interface AuctionCreationContextType {
  auctionData: AuctionCreationState;
  setAuctionDescription: (description: string) => void;
  setAuctionImages: (images: UploadedImage[]) => void;
  clearAuctionData: () => void;
}

const AuctionCreationContext = createContext<AuctionCreationContextType | undefined>(undefined);

export const AuctionCreationProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [auctionData, setAuctionData] = useState<AuctionCreationState>({
    description: "",
    images: [],
  });

  const setAuctionDescription = useCallback((description: string) => {
    setAuctionData(prev => ({ ...prev, description }));
  }, []); // Üres függőségi tömb, mivel setAuctionData stabil

  const setAuctionImages = useCallback((images: UploadedImage[]) => {
    setAuctionData(prev => ({ ...prev, images }));
  }, []); // Üres függőségi tömb

  const clearAuctionData = useCallback(() => {
    setAuctionData({ description: "", images: [] });
  }, []); // Üres függőségi tömb

  const contextValue = useMemo(() => ({
    auctionData,
    setAuctionDescription,
    setAuctionImages,
    clearAuctionData,
  }), [auctionData, setAuctionDescription, setAuctionImages, clearAuctionData]); // A függőségek most már stabil függvények

  return (
    <AuctionCreationContext.Provider value={contextValue}>
      {children}
    </AuctionCreationContext.Provider>
  );
};

export const useAuctionCreation = (): AuctionCreationContextType => {
  const context = useContext(AuctionCreationContext);
  if (!context) {
    throw new Error("useAuctionCreation must be used within an AuctionCreationProvider");
  }
  return context;
};