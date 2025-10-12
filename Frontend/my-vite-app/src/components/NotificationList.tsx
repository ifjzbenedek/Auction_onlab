import React from "react"
import {
  List,
  ListItem,
  ListItemButton,
  ListItemText,
  Paper,
  Typography,
  Box,
  Chip,
  IconButton,
} from "@mui/material"
import DeleteIcon from "@mui/icons-material/Delete"
import MailOutlineIcon from "@mui/icons-material/MailOutline"
import DraftsIcon from "@mui/icons-material/Drafts"
import type { NotificationDTO } from "../types/notification"

interface NotificationListProps {
  notifications: NotificationDTO[]
  onNotificationClick: (notification: NotificationDTO) => void
  onDeleteNotification: (id: number) => void
}

const NotificationList: React.FC<NotificationListProps> = ({
  notifications,
  onNotificationClick,
  onDeleteNotification,
}) => {
  const getMessagePreview = (message: string) => {
    if (message.length <= 50) return message
    return message.substring(0, 50) + "..."
  }

  const formatDate = (dateString?: string) => {
    if (!dateString) return ""
    const date = new Date(dateString)
    return date.toLocaleDateString("hu-HU", {
      year: "numeric",
      month: "short",
      day: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    })
  }

  if (notifications.length === 0) {
    return (
      <Paper sx={{ p: 4, textAlign: "center" }}>
        <MailOutlineIcon sx={{ fontSize: 60, color: "text.secondary", mb: 2 }} />
        <Typography variant="h6" color="text.secondary">
          No messages yet
        </Typography>
        <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
          Your mailbox is empty
        </Typography>
      </Paper>
    )
  }

  return (
    <Paper>
      <List sx={{ p: 0 }}>
        {notifications.map((notification, index) => (
          <React.Fragment key={notification.id}>
            <ListItem
              disablePadding
              secondaryAction={
                <IconButton
                  edge="end"
                  aria-label="delete"
                  onClick={(e) => {
                    e.stopPropagation()
                    if (notification.id) {
                      onDeleteNotification(notification.id)
                    }
                  }}
                >
                  <DeleteIcon />
                </IconButton>
              }
            >
              <ListItemButton
                onClick={() => onNotificationClick(notification)}
                sx={{
                  backgroundColor: notification.alreadyOpened ? "transparent" : "action.hover",
                  "&:hover": {
                    backgroundColor: "action.selected",
                  },
                }}
              >
                <Box sx={{ mr: 2, display: "flex", alignItems: "center" }}>
                  {notification.alreadyOpened ? (
                    <DraftsIcon color="action" />
                  ) : (
                    <MailOutlineIcon color="primary" />
                  )}
                </Box>
                <ListItemText
                  primary={
                    <Box sx={{ display: "flex", alignItems: "center", gap: 1, mb: 0.5 }}>
                      <Typography
                        variant="subtitle1"
                        sx={{
                          fontWeight: notification.alreadyOpened ? "normal" : "bold",
                          flex: 1,
                        }}
                      >
                        {notification.titleText}
                      </Typography>
                      {!notification.alreadyOpened && (
                        <Chip label="New" size="small" color="primary" />
                      )}
                    </Box>
                  }
                  secondary={
                    <Box>
                      <Typography
                        variant="body2"
                        color="text.secondary"
                        sx={{
                          overflow: "hidden",
                          textOverflow: "ellipsis",
                          display: "-webkit-box",
                          WebkitLineClamp: 2,
                          WebkitBoxOrient: "vertical",
                        }}
                      >
                        {getMessagePreview(notification.messageText)}
                      </Typography>
                      <Box sx={{ display: "flex", justifyContent: "space-between", mt: 1 }}>
                        <Typography variant="caption" color="text.secondary">
                          From: {notification.sender?.userName || "System"}
                        </Typography>
                        <Typography variant="caption" color="text.secondary">
                          {formatDate(notification.createdAt)}
                        </Typography>
                      </Box>
                    </Box>
                  }
                />
              </ListItemButton>
            </ListItem>
            {index < notifications.length - 1 && <Box sx={{ borderBottom: 1, borderColor: "divider" }} />}
          </React.Fragment>
        ))}
      </List>
    </Paper>
  )
}

export default NotificationList
