import { AuctionBasicDTO } from "./auction";
import { UserBasicDTO } from "./user";

export interface AuctionImageDTO {
  id: number;
  cloudinaryUrl: string;
  isPrimary: boolean;
  orderIndex: number;
  uploadedBy: UserBasicDTO;
  auction: AuctionBasicDTO; 
  fileSizeKb: number;
  format: string;
}