import { createContext, useState, useContext, useMemo, useCallback } from "react"

interface UploadedImage {
  id: string
  file: File
  preview: string
}

interface AuctionCreationState {
  description: string
  images: UploadedImage[]
  category: string
  itemState: string
  condition: number
}

interface AuctionCreationContextType {
  auctionData: AuctionCreationState
  setAuctionDescription: (description: string) => void
  setAuctionImages: (images: UploadedImage[]) => void
  setAuctionCategory: (category: string) => void
  setAuctionItemState: (itemState: string) => void
  setAuctionCondition: (condition: number) => void
  clearAuctionData: () => void
}

const AuctionCreationContext = createContext<AuctionCreationContextType | undefined>(undefined)

export const AuctionCreationProvider = ({ children }: { children: React.ReactNode }) => {
  const [auctionData, setAuctionData] = useState<AuctionCreationState>({
    description: "",
    images: [],
    category: "",
    itemState: "Brand new",
    condition: 50,
  })

  const setAuctionDescription = useCallback((description: string) => {
    setAuctionData(prev => ({ ...prev, description }))
  }, [])

  const setAuctionImages = useCallback((images: UploadedImage[]) => {
    setAuctionData(prev => ({ ...prev, images }))
  }, [])

  const setAuctionCategory = useCallback((category: string) => {
    setAuctionData(prev => ({ ...prev, category }))
  }, [])

  const setAuctionItemState = useCallback((itemState: string) => {
    setAuctionData(prev => ({ ...prev, itemState }))
  }, [])

  const setAuctionCondition = useCallback((condition: number) => {
    setAuctionData(prev => ({ ...prev, condition }))
  }, [])

  const clearAuctionData = useCallback(() => {
    setAuctionData({ 
      description: "", 
      images: [], 
      category: "", 
      itemState: "Brand new", 
      condition: 50 
    })
  }, [])

  const contextValue = useMemo(() => ({
    auctionData,
    setAuctionDescription,
    setAuctionImages,
    setAuctionCategory,
    setAuctionItemState,
    setAuctionCondition,
    clearAuctionData,
  }), [auctionData, setAuctionDescription, setAuctionImages, setAuctionCategory, setAuctionItemState, setAuctionCondition, clearAuctionData])

  return (
    <AuctionCreationContext.Provider value={contextValue}>
      {children}
    </AuctionCreationContext.Provider>
  )
}

export const useAuctionCreation = () => {
  const context = useContext(AuctionCreationContext)
  if (!context) {
    throw new Error("useAuctionCreation must be used within an AuctionCreationProvider")
  }
  return context
}