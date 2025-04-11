import type React from "react"
import { BrowserRouter, Routes, Route } from "react-router-dom"
import { AuthProvider } from ".//auth-context"
import Profile from "./pages/Profile"
import Login from "./pages/Login"
import BidVerseLanding from "./pages/BidVerseLanding"
import AuctionDetails from "./pages/AuctionDetails"
import UploadAuction from "./pages/UploadAuction"
import SetDetailsAuction from "./pages/SetDetailsAuction"
import UserAuctions from "./pages/UserAuctions"

const App: React.FC = () => {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<BidVerseLanding />} />
          <Route path="/auction/:id" element={<AuctionDetails />} />
          <Route path="/upload-auction" element={<UploadAuction />} />
          <Route path="/set-details-auction" element={<SetDetailsAuction />} />
          <Route path="/my-auctions" element={<UserAuctions />} />
          <Route path="/users/me" element={<Profile />} />
          <Route path="/users/login" element={<Login />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  )
}

export default App;