import type { CategoryDTO } from "./category"
import type { UserBasicDTO } from "./user"

// Main auction interface
export interface AuctionBasicDTO {
  id?: number; // Optional for creation
  user: UserBasicDTO;
  category: CategoryDTO;
  itemName: string;
  minimumPrice: number; // Frontend uses number, backend will convert to BigDecimal
  status: string;
  createDate?: string; // Optional, backend will set
  expiredDate: string; // ISO string format
  lastBid?: number | null; // Optional
  description: string;
  type: "FIXED" | "EXTENDED";
  extraTime?: string | null; // ISO string format
  itemState: string;
  tags: string | null;
  minStep: number;
  condition: number;
  images?: string[]; // Optional if handled separately
}

// For the card view
export interface AuctionCardDTO {
  id: number
  itemName: string
  createDate: string
  expiredDate: string
  lastBid: number | null
  status: string
}

// Add this new interface after the AuctionCardDTO interface
// For the auction details form
export interface AuctionDetailsDTO {
  name: string
  status: string
  condition: number
  category: string
  minimumPrice: number
  minStep: number
  expiredDate: string
  extraTime?: string // Optional, only for EXTENDED type
}

// Type for form state management
export interface AuctionFormState {
  // Step 1 data (from UploadAuction)
  description: string
  images: File[] // Temporary files before upload

  // Step 2 data (from SetDetailsAuction)
  itemName: string
  categoryId: number
  condition: number
  itemState: string
  auctionType: "FIXED" | "EXTENDED"
  minimumPrice: number
  minStep: number
  expiredDate: string
  extraTime?: string
}
