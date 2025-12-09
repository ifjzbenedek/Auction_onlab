import React, { useState } from "react"
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Button,
  Box,
  Alert,
  CircularProgress,
  IconButton,
} from "@mui/material"
import CloseIcon from "@mui/icons-material/Close"
import SendIcon from "@mui/icons-material/Send"
import { notificationApi } from "../services/api"

interface NotificationSenderProps {
  open: boolean
  onClose: () => void
  recipientId?: number // Ha van előre kitöltött címzett ID (reply esetén)
  onSuccess?: () => void 
}

const NotificationSender: React.FC<NotificationSenderProps> = ({
  open,
  onClose,
  recipientId,
  onSuccess,
}) => {
  const [recipientIdInput, setRecipientIdInput] = useState(recipientId?.toString() || "")
  const [title, setTitle] = useState("")
  const [message, setMessage] = useState("")
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [success, setSuccess] = useState(false)

  React.useEffect(() => {
    if (recipientId) {
      setRecipientIdInput(recipientId.toString())
    }
  }, [recipientId])

  const handleSend = async () => {
    if (!recipientIdInput.trim()) {
      setError("Please enter a recipient ID")
      return
    }
    
    // Extract numeric ID from input (e.g., "123" or "user123" -> extract first number)
    const idMatch = recipientIdInput.trim().match(/\d+/)
    if (!idMatch) {
      setError("Please enter a valid recipient ID containing a number")
      return
    }
    const recipientIdNum = parseInt(idMatch[0])
    
    if (!title.trim()) {
      setError("Please enter a title")
      return
    }
    if (!message.trim()) {
      setError("Please enter a message")
      return
    }

    setLoading(true)
    setError(null)

    try {
      await notificationApi.createNotification({
        receiverId: recipientIdNum,
        titleText: title.trim(),
        messageText: message.trim(),
      })

      setSuccess(true)
      setTimeout(() => {
        handleClose()
        if (onSuccess) {
          onSuccess()
        }
      }, 1500)
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : "Failed to send notification. Please try again."
      setError(errorMessage)
    } finally {
      setLoading(false)
    }
  }

  const handleClose = () => {
    if (!loading) {
      setRecipientIdInput("")
      setTitle("")
      setMessage("")
      setError(null)
      setSuccess(false)
      onClose()
    }
  }

  return (
    <Dialog open={open} onClose={handleClose} maxWidth="sm" fullWidth>
      <DialogTitle sx={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
        New Message
        <IconButton onClick={handleClose} disabled={loading} size="small">
          <CloseIcon />
        </IconButton>
      </DialogTitle>
      <DialogContent>
        <Box sx={{ display: "flex", flexDirection: "column", gap: 2, pt: 1 }}>
          {error && <Alert severity="error">{error}</Alert>}
          {success && <Alert severity="success">Message sent successfully!</Alert>}

          <TextField
            label="Recipient ID"
            fullWidth
            value={recipientIdInput}
            onChange={(e) => setRecipientIdInput(e.target.value)}
            disabled={loading || success}
            required
            placeholder="Enter recipient user ID (e.g., 123 or user123)"
          />

          <TextField
            label="Title"
            fullWidth
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            disabled={loading || success}
            required
            placeholder="Enter message title"
          />

          <TextField
            label="Message"
            fullWidth
            multiline
            rows={6}
            value={message}
            onChange={(e) => setMessage(e.target.value)}
            disabled={loading || success}
            required
            placeholder="Write your message here..."
          />
        </Box>
      </DialogContent>
      <DialogActions sx={{ px: 3, pb: 2 }}>
        <Button onClick={handleClose} disabled={loading || success}>
          Cancel
        </Button>
        <Button
          variant="contained"
          onClick={handleSend}
          disabled={loading || success}
          startIcon={loading ? <CircularProgress size={20} /> : <SendIcon />}
        >
          {loading ? "Sending..." : "Send"}
        </Button>
      </DialogActions>
    </Dialog>
  )
}

export default NotificationSender
