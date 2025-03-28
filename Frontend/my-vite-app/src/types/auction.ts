import { CategoryDTO } from "./category"
import { UserBasicDTO } from "./user"

// Kotlin: AuctionBasicDTO.kt
export interface AuctionBasicDTO {
  id: number
  user: UserBasicDTO
  category: CategoryDTO
  itemName: string
  minimumPrice: number
  status: string // Példa enum helyettesítés
  createDate: string // ISO dátum string
  expiredDate: string // ISO dátum string
  lastBid: number | null
  description: string
  type: string
  extraTime: string | null
  itemState: string
  tags: string | null
  minStep: number | null
  condition: number
  images: string[]
}

// Kotlin: AuctionCardDTO.kt
export interface AuctionCardDTO {
  id: number
  itemName: string
  createDate: string
  expiredDate: string
  lastBid: number | null
  status: string
}