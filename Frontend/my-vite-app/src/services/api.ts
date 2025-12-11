import axios from "axios"
import type { BidDTO } from "../types/bid"
import type { AuctionBasicDTO } from "../types/auction"

const API_BASE_URL = ""

const api = axios.create({
  baseURL: API_BASE_URL,
  withCredentials: true,
})

api.interceptors.request.use(
  (config) => config,
  (error) => Promise.reject(error)
)

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response) {
      console.error("API Error:", error.response.status, error.response.data)

      if (error.response.status === 401) {
        if (error.response.data?.authUrl) {
          window.location.href = error.response.data.authUrl
        } else {
          window.location.href = "/oauth2/authorization/google"
        }
        return Promise.reject({
          isAuthError: true,
          message: "Authentication required. Please log in again.",
        })
      }
    } else if (error.request) {
      console.error("No response received:", error.request)
    } else {
      console.error("Request error:", error.message)
    }

    return Promise.reject(error)
  }
)

export const auctionApi = {
  getAuctions: (params?: Record<string, string | number>) => api.get("/auctions", { params }),
  getAuctionById: (id: number) => api.get(`/auctions/${id}`),
  createAuction: (auctionData: Partial<AuctionBasicDTO>) => api.post("/auctions", auctionData),
  updateAuction: (
    id: number,
    auctionData: { title?: string; description?: string; startingPrice?: number; endDate?: string }
  ) => api.put(`/auctions/${id}`, auctionData),
  deleteAuction: (id: number) => api.delete(`/auctions/${id}`),
  placeBid: (auctionId: number, amount: number) => api.post(`/auctions/${auctionId}/bids`, { amount }),
  getMyAuctions: () => api.get("/auctions/my/createdAuctions"),
  getWatchedAuctions: () => api.get("/auctions/my/watchedAuctions"),
  getBiddedAuctions: () => api.get("/auctions/my/biddedAuctions"),
  smartSearch: (query: string) => api.get(`/auctions/smart-search?query=${encodeURIComponent(query)}`),
  getCategories: () => api.get("/categories"),
  getAuctionBids: (id: number) => api.get<BidDTO[]>(`/auctions/${id}/bids`),
  generateDescription: (images: FormData) => api.post("/auctions/generate-description", images)
}

export const imageApi = {
  uploadAuctionImages: (auctionId: number, filesData: FormData) =>
    api.post(`/auctions/${auctionId}/images`, filesData),
  getAuctionImages: (auctionId: number) =>
    api.get(`/auctions/${auctionId}/images`),
  getImageDetails: (auctionId: number, imageId: number) =>
    api.get(`/auctions/${auctionId}/images/${imageId}`),
}

export const notificationApi = {
  createNotification: (notificationData: {
    receiverId: number
    messageText: string
    titleText: string
    auctionId?: number | null
  }) => {
    const payload = {
      id: 0,
      sender: null,
      receiver: {
        id: notificationData.receiverId,
        userName: "",
        emailAddress: "",
        phoneNumber: "",
      },
      auction: notificationData.auctionId ? { id: notificationData.auctionId } : null,
      createdAt: new Date().toISOString(),
      messageText: notificationData.messageText,
      titleText: notificationData.titleText,
      alreadyOpened: false,
    }
    return api.post("/notifications", payload)
  },
  getMyNotifications: () => api.get("/notifications/me"),
  getNotificationById: (id: number) => api.get(`/notifications/${id}`),
  deleteNotification: (id: number) => api.delete(`/notifications/${id}`),
}

export const agentApi = {
  processChat: (auctionId: number, messages: Array<{ role: string; content: string }>) => 
    api.post("/agent/chat", { auctionId, messages }),
}

export default api