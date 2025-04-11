import type React from "react"
import { BrowserRouter, Routes, Route } from "react-router-dom"
import Profile from "./pages/Profile.tsx"
import Login from "./pages/Login.tsx"
import BidVerseLanding from "./pages/BidVerseLanding.tsx"
import AuctionDetails from "./pages/AuctionDetails.tsx"
import UploadAuction from "./pages/UploadAuction.tsx"
import SetDetailsAuction from "./pages/SetDetailsAuction.tsx"
import UserAuctions from "./pages/UserAuctions.tsx"

const App: React.FC = () => {
  return (
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
  )
}

export default App;

