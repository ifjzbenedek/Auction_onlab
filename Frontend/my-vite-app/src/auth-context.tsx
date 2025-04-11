"use client"

import React, { createContext, useContext, useEffect, useState } from "react"
import { UserBasicDTO } from "./types/user"
import api from "./services/api"

interface AuthContextType {
  isAuthenticated: boolean
  user: UserBasicDTO | null
  login: (token: string) => void
  logout: () => void
}

const AuthContext = createContext<AuthContextType>({
  isAuthenticated: false,
  user: null,
  login: () => {},
  logout: () => {},
})

export const AuthProvider = ({ children }: { children: React.ReactNode }) => {
  const [user, setUser] = useState<UserBasicDTO | null>(null)
  const [isAuthenticated, setIsAuthenticated] = useState(false)

  useEffect(() => {
    const token = localStorage.getItem("token")
    if (token) {
      api.get("/users/me")
        .then((res) => {
          setUser(res.data)
          setIsAuthenticated(true)
        })
        .catch(logout)
    }
  }, [])

  const login = (token: string) => {
    localStorage.setItem("token", token)
    api.get("/users/me")
      .then((res) => {
        setUser(res.data)
        setIsAuthenticated(true)
      })
  }

  const logout = () => {
    localStorage.removeItem("token")
    setUser(null)
    setIsAuthenticated(false)
  }

  return (
    <AuthContext.Provider value={{ isAuthenticated, user, login, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export const useAuth = () => useContext(AuthContext);