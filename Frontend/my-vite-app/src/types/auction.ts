import type { CategoryDTO } from "./category"
import type { UserBasicDTO } from "./user"

// Main auction interface
export interface AuctionBasicDTO {
  id?: number; // Optional for creation
  user?: UserBasicDTO; // Optional for creation
  category: CategoryDTO;
  itemName: string;
  minimumPrice: number;
  status?: string; // Optional for creation
  createDate?: string;
  expiredDate: string;
  lastBid?: number | null;
  description: string;
  type: "FIXED" | "EXTENDED";
  extraTime?: string | null;
  itemState: string;
  tags: string | null;
  minStep: number;
  condition: number;
  images?: string[];
  startDate?: string | null;
}

// For the card view
export interface AuctionCardDTO {
  id: number
  itemName: string
  createDate: string
  expiredDate: string
  lastBid: number | null
  status: string
  images?: string[] // Optional, if handled separately
  imageUrl?: string // Add this line for the primary image URL
  startDate?: string | null
}

// For the auction details form
export interface AuctionDetailsDTO {
  name: string
  status: string
  condition: number
  category: string
  minimumPrice: number
  minStep: number
  expiredDate: string
  startDate?: string | null
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
  startDate?: string | null
}
