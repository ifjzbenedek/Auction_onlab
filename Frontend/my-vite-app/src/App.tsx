import type React from "react"
import { BrowserRouter as Router, Routes, Route } from "react-router-dom"
import Home from "./pages/Home.tsx"
import Profile from "./pages/Profile.tsx"
import { Box } from "@mui/material"
import Login from "./pages/Login.tsx"
import BidVerseLanding from "./pages/BidVerseLanding.tsx"
import AuctionDetails from "./pages/AuctionDetails.tsx"
import UploadAuction from "./pages/UploadAuction.tsx"
import SetDetailsAuction from "./pages/SetDetailsAuction.tsx"
import UserAuctions from "./pages/UserAuctions.tsx"

const App: React.FC = () => {
  return (
    <Router>
      <Box>
        <Routes>
          <Route path="/" element={<BidVerseLanding />} />
          <Route path="/auction/:id" element={<AuctionDetails />} />
          <Route path="/upload-auction" element={<UploadAuction />} />
          <Route path="/set-details-auction" element={<SetDetailsAuction />} />
          <Route path="/my-auctions" element={<UserAuctions />} />
          <Route path="/users" element={<Home />} />
          <Route path="/users/me" element={<Profile />} />
          <Route path="/users/login" element={<Login />} />
        </Routes>
      </Box>
    </Router>
  )
}

export default App

