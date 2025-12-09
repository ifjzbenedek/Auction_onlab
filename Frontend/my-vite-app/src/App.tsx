import { BrowserRouter, Routes, Route } from "react-router-dom"
import Profile from "./pages/Profile.tsx"
import Login from "./pages/Login.tsx"
import BidVerseLanding from "./pages/BidVerseLanding.tsx"
import AuctionDetails from "./pages/AuctionDetails.tsx"
import UploadAuction from "./pages/UploadAuction.tsx"
import SetDetailsAuction from "./pages/SetDetailsAuction.tsx"
import UserAuctions from "./pages/UserAuctions.tsx"
import Mailbox from "./pages/Mailbox.tsx"
import AuthGuard from "./components/AuthGuard.tsx"
import { AuctionCreationProvider } from "./contexts/AuctionCreationContext.tsx"

const App = () => {
  return (
    <BrowserRouter>
      <AuctionCreationProvider>
        <Routes>
          <Route path="/" element={<BidVerseLanding />} />
          <Route path="/auction/:id" element={<AuctionDetails />} />
          <Route path="/users/login" element={<Login />} />
          
          <Route path="/upload-auction" element={<AuthGuard><UploadAuction /></AuthGuard>} />
          <Route path="/set-details-auction" element={<AuthGuard><SetDetailsAuction /></AuthGuard>} />
          <Route path="/my-auctions" element={<AuthGuard><UserAuctions /></AuthGuard>} />
          <Route path="/users/me" element={<AuthGuard><Profile /></AuthGuard>} />
          <Route path="/mailbox" element={<AuthGuard><Mailbox /></AuthGuard>} />
        </Routes>
      </AuctionCreationProvider>
    </BrowserRouter>
  )
}

export default App

