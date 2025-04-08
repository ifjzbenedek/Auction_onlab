import type React from "react"
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom"
import Profile from "./pages/Profile.tsx"
import Login from "./pages/Login.tsx"
import BidVerseLanding from "./pages/BidVerseLanding.tsx"
import AuctionDetails from "./pages/AuctionDetails.tsx"
import UploadAuction from "./pages/UploadAuction.tsx"
import SetDetailsAuction from "./pages/SetDetailsAuction.tsx"
import UserAuctions from "./pages/UserAuctions.tsx"
import authService from "./auth/auth-service"

// Protected route component
const ProtectedRoute = ({ children }: { children: React.ReactNode }) => {
  const isAuthenticated = authService.isLoggedIn()

  if (!isAuthenticated) {
    // Redirect to login with return URL
    const returnUrl = encodeURIComponent(window.location.pathname)
    return <Navigate to={`/users/login?returnUrl=${returnUrl}`} />
  }

  return <>{children}</>
}

const App: React.FC = () => {
  return (
    <BrowserRouter>
      <Routes>
        {/* Public routes */}
        <Route path="/" element={<BidVerseLanding />} />
        <Route path="/auction/:id" element={<AuctionDetails />} />
        <Route path="/users/login" element={<Login />} />

        {/* Protected routes - require authentication */}
        <Route
          path="/upload-auction"
          element={
            <ProtectedRoute>
              <UploadAuction />
            </ProtectedRoute>
          }
        />
        <Route
          path="/set-details-auction"
          element={
            <ProtectedRoute>
              <SetDetailsAuction />
            </ProtectedRoute>
          }
        />
        <Route
          path="/my-auctions"
          element={
            <ProtectedRoute>
              <UserAuctions />
            </ProtectedRoute>
          }
        />
        <Route
          path="/users/me"
          element={
            <ProtectedRoute>
              <Profile />
            </ProtectedRoute>
          }
        />

        {/* Fallback route */}
        <Route path="*" element={<Navigate to="/" />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App;
