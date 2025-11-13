import React, { useState, useEffect, useRef } from 'react'
import { Box, Typography, IconButton, TextField, Button, MenuItem, Select, FormControl, InputLabel, SelectChangeEvent } from '@mui/material'
import { X, Send, Bot } from 'lucide-react'
import { auctionApi, agentApi } from '../services/api'
import type { AuctionBasicDTO } from '../types/auction'

interface Message {
  id: string
  sender: 'user' | 'agent'
  text: string
  timestamp: Date
}

interface AgentPanelProps {
  open: boolean
  onClose: () => void
}

export const AgentPanel: React.FC<AgentPanelProps> = ({ open, onClose }) => {
  const [messages, setMessages] = useState<Message[]>([])
  const [inputMessage, setInputMessage] = useState('')
  const [auctions, setAuctions] = useState<AuctionBasicDTO[]>([])
  const [selectedAuctionId, setSelectedAuctionId] = useState<number | ''>('')
  const [chatHistory, setChatHistory] = useState<Array<{ role: string; content: string }>>([])
  const [isProcessing, setIsProcessing] = useState(false)
  
  const messagesEndRef = useRef<HTMLDivElement>(null)

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' })
  }

  useEffect(() => {
    scrollToBottom()
  }, [messages])

  useEffect(() => {
    if (open) {
      // RESET: Clear all previous conversation when panel opens
      setMessages([
        {
          id: '1',
          sender: 'agent',
          text: 'Hi! I will help you set your automatic bidding agent! Choose and Auction from the panel!',
          timestamp: new Date(),
        },
      ])
      setChatHistory([])
      setSelectedAuctionId('')
      setInputMessage('')
      
      // Load all auctions
      loadAuctions()
    }
  }, [open])

  const loadAuctions = async () => {
    try {
      const response = await auctionApi.getAuctions()
      setAuctions(response.data || [])
    } catch (error) {
      console.error('Error loading auctions:', error)
      setMessages((prev) => [
        ...prev,
        {
          id: Date.now().toString(),
          sender: 'agent',
          text: 'Error during Auction recognition.',
          timestamp: new Date(),
        },
      ])
    }
  }

  const handleAuctionChange = (event: SelectChangeEvent<number | ''>) => {
    const auctionId = event.target.value as number
    setSelectedAuctionId(auctionId)
    
    const auction = auctions.find((a) => a.id === auctionId)
    if (auction) {
      const userMessage = `Auction chosen: ${auction.itemName} (ID: ${auction.id})`
      const agentMessage = `Alright! Now write how would you like to bid. For example:\n"I would like to bid up to maximum 1000 dollars, in 50 dollar steps, checked every 5 minutes."`
      
      setMessages((prev) => [
        ...prev,
        {
          id: Date.now().toString(),
          sender: 'user',
          text: userMessage,
          timestamp: new Date(),
        },
        {
          id: (Date.now() + 1).toString(),
          sender: 'agent',
          text: agentMessage,
          timestamp: new Date(),
        },
      ])
      
      // Add to chat history
      setChatHistory([
        { role: 'user', content: userMessage },
        { role: 'assistant', content: agentMessage },
      ])
    }
  }

  const handleSendMessage = async () => {
    if (!inputMessage.trim() || isProcessing) return

    const userMessage = inputMessage.trim()
    setInputMessage('')
    
    // Add user message
    setMessages((prev) => [
      ...prev,
      {
        id: Date.now().toString(),
        sender: 'user',
        text: userMessage,
        timestamp: new Date(),
      },
    ])

    // Add to chat history
    const newChatHistory = [...chatHistory, { role: 'user', content: userMessage }]
    setChatHistory(newChatHistory)

    setIsProcessing(true)

    try {
      // Call backend API with auctionId from frontend selection
      const response = await agentApi.processChat(selectedAuctionId as number, newChatHistory)
      
      // Add agent response
      const agentResponse = response.data.agentResponse || 'AutoBid successfully created!'
      setMessages((prev) => [
        ...prev,
        {
          id: Date.now().toString(),
          sender: 'agent',
          text: agentResponse,
          timestamp: new Date(),
        },
      ])

      // Update chat history with agent response
      setChatHistory([...newChatHistory, { role: 'assistant', content: agentResponse }])

      // If complete, show success details
      if (response.data.isComplete) {
        const config = response.data.config
        const successMessage = `Autobid set successfully!\n\n AutoBid details:\n• Auction ID: ${config.auctionId}\n• Starting bid: ${config.startingBidAmount || 'Not given'}\n• Step amount: ${config.incrementAmount} \n• Maximum bid: ${config.maxBidAmount} \n• Interval: ${config.intervalMinutes} minutes\n• AutoBid ID: ${config.id}\n\n The Autobid is on and it will start bidding for you!`
        
        setMessages((prev) => [
          ...prev,
          {
            id: (Date.now() + 1).toString(),
            sender: 'agent',
            text: successMessage,
            timestamp: new Date(),
          },
        ])

        // Reset for new conversation
        setTimeout(() => {
          setSelectedAuctionId('')
          setChatHistory([])
        }, 2000)
      }
    } catch (error) {
      const axiosError = error as { response?: { status: number; data?: { message?: string } } }
      let errorMessage = 'Error during creation of this Autobid.'

      if (axiosError.response) {
        if (axiosError.response.status === 409) {
          errorMessage = 'There is already an Autobid for this auction!'
        } else if (axiosError.response.status === 404) {
          errorMessage = ' Auction not found.'
        } else if (axiosError.response.data?.message) {
          errorMessage = ` Error: ${axiosError.response.data.message}`
        }
      }

      setMessages((prev) => [
        ...prev,
        {
          id: Date.now().toString(),
          sender: 'agent',
          text: errorMessage,
          timestamp: new Date(),
        },
      ])
    } finally {
      setIsProcessing(false)
    }
  }

  const handleKeyPress = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault()
      handleSendMessage()
    }
  }

  return (
    <>
      {/* Backdrop */}
      {open && (
        <Box
          sx={{
            position: 'fixed',
            top: 0,
            left: 0,
            right: 0,
            bottom: 0,
            backgroundColor: 'rgba(0, 0, 0, 0.5)',
            zIndex: 1200,
            transition: 'opacity 0.3s ease',
          }}
          onClick={onClose}
        />
      )}

      {/* Sliding Panel */}
      <Box
        sx={{
          position: 'fixed',
          top: 0,
          right: 0,
          bottom: 0,
          width: { xs: '100%', sm: '450px' },
          backgroundColor: 'white',
          boxShadow: '-4px 0 20px rgba(0, 0, 0, 0.1)',
          zIndex: 1300,
          transform: open ? 'translateX(0)' : 'translateX(100%)',
          transition: 'transform 0.3s ease-in-out',
          display: 'flex',
          flexDirection: 'column',
        }}
      >
        {/* Header */}
        <Box
          sx={{
            p: 2,
            borderBottom: '1px solid #e0e0e0',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            backgroundColor: '#f8f9fa',
          }}
        >
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
            <Bot size={24} color="#3498db" />
            <Typography variant="h6" sx={{ fontWeight: 600, color: '#2c3e50' }}>
              AutoBid Agent
            </Typography>
          </Box>
          <IconButton onClick={onClose} size="small">
            <X size={20} />
          </IconButton>
        </Box>

        {/* Auction Selector */}
        <Box sx={{ p: 2, borderBottom: '1px solid #e0e0e0', backgroundColor: 'white' }}>
          <FormControl fullWidth size="small">
            <InputLabel>Válassz egy aukciót</InputLabel>
            <Select
              value={selectedAuctionId}
              label="Válassz egy aukciót"
              onChange={handleAuctionChange}
            >
              <MenuItem value="">
                <em>Válassz...</em>
              </MenuItem>
              {auctions.map((auction) => (
                <MenuItem key={auction.id} value={auction.id}>
                  {auction.itemName} - {auction.lastBid || auction.minimumPrice} ({auction.status})
                </MenuItem>
              ))}
            </Select>
          </FormControl>
        </Box>

        {/* Messages */}
        <Box
          sx={{
            flex: 1,
            overflowY: 'auto',
            p: 2,
            backgroundColor: '#f8f9fa',
            display: 'flex',
            flexDirection: 'column',
            gap: 2,
          }}
        >
          {messages.map((message) => (
            <Box
              key={message.id}
              sx={{
                display: 'flex',
                justifyContent: message.sender === 'user' ? 'flex-end' : 'flex-start',
              }}
            >
              <Box
                sx={{
                  maxWidth: '75%',
                  backgroundColor: message.sender === 'user' ? '#3498db' : 'white',
                  color: message.sender === 'user' ? 'white' : '#2c3e50',
                  borderRadius: '12px',
                  p: 1.5,
                  boxShadow: '0 2px 4px rgba(0, 0, 0, 0.1)',
                }}
              >
                <Typography variant="body2" sx={{ whiteSpace: 'pre-line' }}>
                  {message.text}
                </Typography>
                <Typography
                  variant="caption"
                  sx={{
                    display: 'block',
                    mt: 0.5,
                    opacity: 0.7,
                    fontSize: '0.7rem',
                  }}
                >
                  {message.timestamp.toLocaleTimeString('hu-HU', {
                    hour: '2-digit',
                    minute: '2-digit',
                  })}
                </Typography>
              </Box>
            </Box>
          ))}
          <div ref={messagesEndRef} />
        </Box>

        {/* Input Area */}
        <Box
          sx={{
            p: 2,
            borderTop: '1px solid #e0e0e0',
            backgroundColor: 'white',
            display: 'flex',
            gap: 1,
          }}
        >
          <TextField
            fullWidth
            size="small"
            placeholder={selectedAuctionId ? "Write what you have in mind..." : "First choose an Auction!"}
            value={inputMessage}
            onChange={(e) => setInputMessage(e.target.value)}
            onKeyPress={handleKeyPress}
            disabled={!selectedAuctionId || isProcessing}
            multiline
            maxRows={3}
          />
          <Button
            variant="contained"
            onClick={handleSendMessage}
            disabled={!inputMessage.trim() || !selectedAuctionId || isProcessing}
            sx={{
              minWidth: '50px',
              backgroundColor: '#3498db',
              '&:hover': { backgroundColor: '#2980b9' },
            }}
          >
            <Send size={20} />
          </Button>
        </Box>
      </Box>
    </>
  )
}
