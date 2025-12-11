"use client"

import type React from "react"
import { Box, Typography, TableRow, TableCell, IconButton, Tooltip } from "@mui/material"
import { Edit2, Trash2, Eye } from "lucide-react"

interface MyAuctionItemProps {
  auction: {
    id: number
    itemName: string
    remainingTime: string
    highestBid: number
  }
  onView: (id: number) => void
  onEdit: (id: number) => void
  onDelete: (id: number) => void
}

const MyAuctionItem: React.FC<MyAuctionItemProps> = ({ auction, onView, onEdit, onDelete }) => {
  return (
    <TableRow hover>
      <TableCell>
        <Typography variant="body1" fontWeight="medium" sx={{ cursor: "pointer" }} onClick={() => onView(auction.id)}>
          {auction.itemName}
        </Typography>
      </TableCell>
      <TableCell>{auction.remainingTime}</TableCell>
      <TableCell>${auction.highestBid.toFixed(2)}</TableCell>
      <TableCell align="right">
        <Box sx={{ display: "flex", justifyContent: "flex-end", gap: 1 }}>
          <Tooltip title="View">
            <IconButton size="small" onClick={() => onView(auction.id)} sx={{ color: "#3498db" }}>
              <Eye size={18} />
            </IconButton>
          </Tooltip>
          <Tooltip title="Edit">
            <IconButton size="small" onClick={() => onEdit(auction.id)} sx={{ color: "#f39c12" }}>
              <Edit2 size={18} />
            </IconButton>
          </Tooltip>
          <Tooltip title="Delete">
            <IconButton size="small" onClick={() => onDelete(auction.id)} sx={{ color: "#e74c3c" }}>
              <Trash2 size={18} />
            </IconButton>
          </Tooltip>
        </Box>
      </TableCell>
    </TableRow>
  )
}

export default MyAuctionItem;