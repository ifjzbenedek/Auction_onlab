import type React from "react"
import { BrowserRouter as Router, Routes, Route } from "react-router-dom"
import Home from "./pages/Home.tsx"
import Profile from "./pages/Profile.tsx"
import { Box } from "@mui/material"
import Login from "./pages/Login.tsx"
import BidVerseLanding from "./pages/BidVerseLanding.tsx"

const App: React.FC = () => {
  return (
    <Router>
      <Box>
        <Routes>
          <Route path="/" element={<BidVerseLanding />} />
          <Route path="/users" element={<Home />} />
          <Route path="/users/me" element={<Profile />} />
          <Route path="/users/login" element={<Login />} />
        </Routes>
      </Box>
    </Router>
  )
}

export default App

