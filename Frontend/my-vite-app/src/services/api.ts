import axios from "axios"
import type { BidDTO } from "../types/bid"
import type { AuctionBasicDTO } from "../types/auction"

// API alapbeállítások
// No need for full URL when using proxy
const API_BASE_URL = ""

// Axios instance létrehozása alapbeállításokkal
const api = axios.create({
  baseURL: API_BASE_URL,
  
  // Add withCredentials to send cookies with cross-origin requests
  withCredentials: true,
})

// Kérés interceptor - mivel session-based auth-ot használunk, nincs szükség token-re
api.interceptors.request.use(
  (config) => {
    // Session-based auth esetén a browser automatikusan küldi a cookie-kat
    // withCredentials: true már be van állítva az axios instance-ban
    return config
  },
  (error) => {
    return Promise.reject(error)
  },
)

// Válasz interceptor - hibakezelés session-based auth-hoz
api.interceptors.response.use(
  (response) => {
    return response
  },
  (error) => {
    // Globális hibakezelés
    if (error.response) {
      // A szerver válaszolt hibakóddal
      console.error("API Error:", error.response.status, error.response.data)

      // 401 Unauthorized - nincs bejelentkezve vagy session lejárt
      if (error.response.status === 401) {
        // Ha JSON választ kapunk authUrl-lel, használjuk azt
        if (error.response.data?.authUrl) {
          window.location.href = error.response.data.authUrl
        } else {
          // Fallback: redirect az OAuth endpoint-ra
          window.location.href = "/oauth2/authorization/google"
        }
        return Promise.reject({
          isAuthError: true,
          message: "Authentication required. Please log in again.",
        })
      }
    } else if (error.request) {
      // A kérés elküldve, de nem érkezett válasz
      console.error("No response received:", error.request)
    } else {
      // Hiba a kérés beállításakor
      console.error("Request error:", error.message)
    }

    return Promise.reject(error)
  },
)

// API végpontok
export const auctionApi = {
  // Aukciók lekérése
  getAuctions: (params?: Record<string, string | number>) => api.get("/auctions", { params }),

  // Egy aukció részleteinek lekérése
  getAuctionById: (id: number) => api.get(`/auctions/${id}`),

  // Új aukció létrehozása
  createAuction: (auctionData: Partial<AuctionBasicDTO>) => api.post("/auctions", auctionData),

  // Aukció frissítése
  updateAuction: (
    id: number,
    auctionData: { title?: string; description?: string; startingPrice?: number; endDate?: string },
  ) => api.put(`/auctions/${id}`, auctionData),

  // Aukció törlése
  deleteAuction: (id: number) => api.delete(`/auctions/${id}`),

  // Licit elhelyezése
  placeBid: (auctionId: number, amount: number) => api.post(`/auctions/${auctionId}/bids`, { amount }),

  // Saját aukciók lekérése
  getMyAuctions: () => api.get("/auctions/my/createdAuctions"),

  // Figyelt aukciók lekérése
  getWatchedAuctions: () => api.get("/auctions/my/watchedAuctions"),

  // Licitált aukciók lekérése
  getBiddedAuctions: () => api.get("/auctions/my/biddedAuctions"),

  // Smart search aukciók között
  smartSearch: (query: string) => api.get(`/auctions/smart-search?query=${encodeURIComponent(query)}`),

  // Kategóriák lekérése
  getCategories: () => api.get("/categories"),

  // Bidek lekérdezése egy aukcióhoz
  getAuctionBids: (id: number) => api.get<BidDTO[]>(`/auctions/${id}/bids`),

  // AI generált leírás kérése
  generateDescription: (images: FormData) => api.post("/auctions/generate-description", images)
}

export const imageApi = {
  // Upload images to an auction
  // The backend ImageController is mapped to "/api/auctions/{auctionId}/images"
  uploadAuctionImages: (auctionId: number, filesData: FormData) =>
    api.post(`/auctions/${auctionId}/images`, filesData), // Axios will set Content-Type for FormData

  // Get all images for an auction
  getAuctionImages: (auctionId: number) =>
    api.get(`/auctions/${auctionId}/images`),

  // Get specific image details
  getImageDetails: (auctionId: number, imageId: number) =>
    api.get(`/auctions/${auctionId}/images/${imageId}`),
};

export const notificationApi = {
  // Create a new notification (send message)
  createNotification: (notificationData: {
    receiverId: number
    messageText: string
    titleText: string
    auctionId?: number | null
  }) => {
    const payload = {
      id: 0, // Will be set by backend
      sender: null,
      receiver: {
        id: notificationData.receiverId,
        userName: "",
        emailAddress: "",
        phoneNumber: "",
      },
      auction: notificationData.auctionId ? { id: notificationData.auctionId } : null,
      createdAt: new Date().toISOString(), // Will be overridden by backend
      messageText: notificationData.messageText,
      titleText: notificationData.titleText,
      alreadyOpened: false,
    }
    return api.post("/notifications", payload)
  },

  // Get all notifications for the current user
  getMyNotifications: () => api.get("/notifications/me"),

  // Get specific notification by ID
  getNotificationById: (id: number) => api.get(`/notifications/${id}`),

  // Delete a notification
  deleteNotification: (id: number) => api.delete(`/notifications/${id}`),
};

export const agentApi = {
  // Process chat messages and create AutoBid
  // Now includes auctionId from frontend selection
  processChat: (auctionId: number, messages: Array<{ role: string; content: string }>) => 
    api.post("/agent/chat", { auctionId, messages }),
};

export default api;