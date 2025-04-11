import axios from "axios"
import type { BidDTO } from "../types/bid"

// API alapbeállítások
// No need for full URL when using proxy
const API_BASE_URL = ""

// Axios instance létrehozása alapbeállításokkal
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
  // Add withCredentials to send cookies with cross-origin requests
  withCredentials: true,
})

// Kérés interceptor - pl. token hozzáadása
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token")
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  },
)

// Válasz interceptor - pl. hibakezelés
api.interceptors.response.use(
  (response) => {
    return response
  },
  (error) => {
    // Globális hibakezelés
    if (error.response) {
      // A szerver válaszolt hibakóddal
      console.error("API Error:", error.response.status, error.response.data)

      // 401 Unauthorized - token lejárt vagy érvénytelen
      if (error.response.status === 401) {
        // Instead of redirecting directly, return a specific error
        console.error("Authentication required. Please log in again.")
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
  createAuction: (auctionData: { title: string; description: string; startingPrice: number; endDate: string }) =>
    api.post("/auctions", auctionData),

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

  // Kategóriák lekérése
  getCategories: () => api.get("/categories"),

  // Bidek lekérdezése egy aukcióhoz
  getAuctionBids: (id: number) => api.get<BidDTO[]>(`/auctions/${id}/bids`),
}

export default api;
