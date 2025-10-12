import type { UserBasicDTO } from "./user"
import type { AuctionBasicDTO } from "./auction"

export interface NotificationDTO {
  id?: number
  sender?: UserBasicDTO | null
  receiver: UserBasicDTO
  auction?: AuctionBasicDTO | null
  createdAt?: string
  messageText: string
  titleText: string
  alreadyOpened?: boolean
}

export interface NotificationCreateDTO {
  receiverUsername: string
  messageText: string
  titleText: string
  auctionId?: number | null
}
