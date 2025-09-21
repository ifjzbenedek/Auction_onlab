import type React from "react"
import { BrowserRouter, Routes, Route } from "react-router-dom"
import Profile from "./pages/Profile.tsx"
import Login from "./pages/Login.tsx"
import BidVerseLanding from "./pages/BidVerseLanding.tsx"
import AuctionDetails from "./pages/AuctionDetails.tsx"
import UploadAuction from "./pages/UploadAuction.tsx"
import SetDetailsAuction from "./pages/SetDetailsAuction.tsx"
import UserAuctions from "./pages/UserAuctions.tsx"
import AuthGuard from "./components/AuthGuard.tsx"
import { AuctionCreationProvider } from "./contexts/AuctionCreationContext.tsx"

const App: React.FC = () => {
  return (
    <BrowserRouter>
      <AuctionCreationProvider>
        <Routes>
          <Route path="/" element={<BidVerseLanding />} />
          <Route path="/auction/:id" element={<AuctionDetails />} />
          
          {/* Protected routes - require authentication */}
          <Route 
            path="/upload-auction" 
            element={
              <AuthGuard>
                <UploadAuction />
              </AuthGuard>
            } 
          />
          <Route 
            path="/set-details-auction" 
            element={
              <AuthGuard>
                <SetDetailsAuction />
              </AuthGuard>
            } 
          />
          <Route 
            path="/my-auctions" 
            element={
              <AuthGuard>
                <UserAuctions />
              </AuthGuard>
            } 
          />
          <Route 
            path="/users/me" 
            element={
              <AuthGuard>
                <Profile />
              </AuthGuard>
            } 
          />
          
          {/* Public routes */}
          <Route path="/users/login" element={<Login />} />
        </Routes>
      </AuctionCreationProvider>
    </BrowserRouter>
  )
}

export default App;

