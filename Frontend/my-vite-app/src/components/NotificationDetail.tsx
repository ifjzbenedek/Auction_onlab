import React from "react"
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  Typography,
  Box,
  Divider,
  IconButton,
  Chip,
} from "@mui/material"
import CloseIcon from "@mui/icons-material/Close"
import ReplyIcon from "@mui/icons-material/Reply"
import type { NotificationDTO } from "../types/notification"

interface NotificationDetailProps {
  notification: NotificationDTO | null
  open: boolean
  onClose: () => void
  onReply: (senderId: number) => void
}

const NotificationDetail: React.FC<NotificationDetailProps> = ({
  notification,
  open,
  onClose,
  onReply,
}) => {
  if (!notification) return null

  const formatDate = (dateString?: string) => {
    if (!dateString) return ""
    const date = new Date(dateString)
    return date.toLocaleString("hu-HU", {
      year: "numeric",
      month: "long",
      day: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    })
  }

  const handleReply = () => {
    if (notification.sender?.id) {
      onReply(notification.sender.id)
    }
  }

  return (
    <Dialog open={open} onClose={onClose} maxWidth="md" fullWidth>
      <DialogTitle sx={{ display: "flex", justifyContent: "space-between", alignItems: "center", pb: 2 }}>
        <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
          <Typography variant="h6">{notification.titleText}</Typography>
          {!notification.alreadyOpened && (
            <Chip label="New" size="small" color="primary" />
          )}
        </Box>
        <IconButton onClick={onClose} size="small">
          <CloseIcon />
        </IconButton>
      </DialogTitle>

      <Divider />

      <DialogContent>
        <Box sx={{ mb: 3 }}>
          <Box sx={{ display: "flex", justifyContent: "space-between", mb: 2 }}>
            <Box>
              <Typography variant="caption" color="text.secondary">
                From:
              </Typography>
              <Typography variant="body1" sx={{ fontWeight: "medium" }}>
                {notification.sender?.userName || "System"}
              </Typography>
            </Box>
            <Box sx={{ textAlign: "right" }}>
              <Typography variant="caption" color="text.secondary">
                To:
              </Typography>
              <Typography variant="body1" sx={{ fontWeight: "medium" }}>
                {notification.receiver.userName}
              </Typography>
            </Box>
          </Box>

          <Typography variant="caption" color="text.secondary">
            {formatDate(notification.createdAt)}
          </Typography>
        </Box>

        <Divider sx={{ mb: 3 }} />

        <Box sx={{ mb: 2 }}>
          <Typography
            variant="body1"
            sx={{
              whiteSpace: "pre-wrap",
              wordBreak: "break-word",
              lineHeight: 1.6,
            }}
          >
            {notification.messageText}
          </Typography>
        </Box>

        {notification.auction && (
          <Box
            sx={{
              mt: 3,
              p: 2,
              backgroundColor: "action.hover",
              borderRadius: 1,
            }}
          >
            <Typography variant="caption" color="text.secondary" sx={{ mb: 1, display: "block" }}>
              Related Auction:
            </Typography>
            <Typography variant="body2" sx={{ fontWeight: "medium" }}>
              {notification.auction.itemName || `Auction #${notification.auction.id}`}
            </Typography>
          </Box>
        )}
      </DialogContent>

      <Divider />

      <DialogActions sx={{ px: 3, py: 2 }}>
        <Button onClick={onClose}>Close</Button>
        {notification.sender && (
          <Button
            variant="contained"
            startIcon={<ReplyIcon />}
            onClick={handleReply}
          >
            Reply
          </Button>
        )}
      </DialogActions>
    </Dialog>
  )
}

export default NotificationDetail
