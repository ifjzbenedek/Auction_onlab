import { useEffect, useState } from "react"
import {
  Box,
  Typography,
  CircularProgress,
  useTheme,
  Alert,
  Paper,
  Button,
  IconButton,
} from "@mui/material"
import EditIcon from "@mui/icons-material/Edit"
import ArrowBackIcon from "@mui/icons-material/ArrowBack"
import NotificationList from "../components/NotificationList"
import NotificationDetail from "../components/NotificationDetail"
import NotificationSender from "../components/NotificationSender"
import type { NotificationDTO } from "../types/notification"
import { notificationApi } from "../services/api"
import { useNavigate } from "react-router-dom"

function Mailbox() {
  const navigate = useNavigate()
  const [notifications, setNotifications] = useState<NotificationDTO[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [selectedNotification, setSelectedNotification] = useState<NotificationDTO | null>(null)
  const [detailDialogOpen, setDetailDialogOpen] = useState(false)
  const [senderDialogOpen, setSenderDialogOpen] = useState(false)
  const [replyToUserId, setReplyToUserId] = useState<number | undefined>(undefined)
  const theme = useTheme()

  const fetchNotifications = async () => {
    try {
      setLoading(true)
      setError(null)
      const response = await notificationApi.getMyNotifications()
      setNotifications(response.data)
    } catch {
      setError("Failed to load notifications. Please try again.")
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchNotifications()
  }, [])

  const handleNotificationClick = async (notification: NotificationDTO) => {
    setSelectedNotification(notification)
    setDetailDialogOpen(true)

    if (!notification.alreadyOpened && notification.id) {
      try {
        await notificationApi.getNotificationById(notification.id)
        fetchNotifications()
      } catch {
        // Continue even if marking as read fails
      }
    }
  }

  const handleDeleteNotification = async (id: number) => {
    try {
      await notificationApi.deleteNotification(id)
      fetchNotifications()
    } catch {
      setError("Failed to delete notification. Please try again.")
    }
  }

  const handleReply = (senderId: number) => {
    setReplyToUserId(senderId)
    setDetailDialogOpen(false)
    setSenderDialogOpen(true)
  }

  const handleNewMessage = () => {
    setReplyToUserId(undefined)
    setSenderDialogOpen(true)
  }

  const handleSendSuccess = () => {
    fetchNotifications()
    setSenderDialogOpen(false)
  }

  return (
    <Box sx={{ bgcolor: theme.palette.background.default, minHeight: "100vh", py: 4 }}>
      <Box sx={{ maxWidth: 1200, mx: "auto", px: { xs: 2, md: 4 }, py: 2 }}>
        <Paper sx={{ p: 3, mb: 3 }}>
          <Box sx={{ display: "flex", alignItems: "center", justifyContent: "space-between", mb: 2 }}>
            <Box sx={{ display: "flex", alignItems: "center", gap: 2 }}>
              <IconButton onClick={() => navigate("/")} color="primary" size="large">
                <ArrowBackIcon />
              </IconButton>
              <Box>
                <Typography
                  variant="h4"
                  sx={{
                    fontWeight: "bold",
                    color: theme.palette.text.primary,
                  }}
                >
                  Mailbox
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Manage your messages and notifications
                </Typography>
              </Box>
            </Box>
            <Button
              variant="contained"
              startIcon={<EditIcon />}
              onClick={handleNewMessage}
              size="large"
              sx={{ 
                minWidth: 150,
                boxShadow: 3,
                '&:hover': {
                  boxShadow: 6,
                }
              }}
            >
              New Message
            </Button>
          </Box>
        </Paper>

        {error && (
          <Alert severity="error" sx={{ mb: 3 }} onClose={() => setError(null)}>
            {error}
          </Alert>
        )}

        {loading ? (
          <Box sx={{ display: "flex", justifyContent: "center", alignItems: "center", height: "50vh" }}>
            <CircularProgress size={60} />
          </Box>
        ) : (
          <NotificationList
            notifications={notifications}
            onNotificationClick={handleNotificationClick}
            onDeleteNotification={handleDeleteNotification}
          />
        )}

        <NotificationDetail
          notification={selectedNotification}
          open={detailDialogOpen}
          onClose={() => setDetailDialogOpen(false)}
          onReply={handleReply}
        />

        <NotificationSender
          open={senderDialogOpen}
          onClose={() => setSenderDialogOpen(false)}
          recipientId={replyToUserId}
          onSuccess={handleSendSuccess}
        />
      </Box>
    </Box>
  )
}

export default Mailbox
