"use client"

import type React from "react"
import { Box, Typography, TableRow, TableCell, IconButton, Tooltip } from "@mui/material"
import { Eye, Heart } from "lucide-react"

interface FollowedAuctionItemProps {
  auction: {
    id: number
    itemName: string
    remainingTime: string
    highestBid: number
  }
  onView: (id: number) => void
  onUnfollow: (id: number) => void
}

const FollowedAuctionItem: React.FC<FollowedAuctionItemProps> = ({ auction, onView, onUnfollow }) => {
  return (
    <TableRow hover sx={{ "& .MuiTableCell-root": { userSelect: "none" } }}>
      <TableCell>
        <Typography
          variant="body1"
          fontWeight="medium"
          sx={{ userSelect: "none", cursor: "pointer" }}
          onClick={() => onView(auction.id)}
        >
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
          <Tooltip title="Unfollow">
            <IconButton size="small" onClick={() => onUnfollow(auction.id)} sx={{ color: "#e74c3c" }}>
              <Heart fill="#e74c3c" size={18} />
            </IconButton>
          </Tooltip>
        </Box>
      </TableCell>
    </TableRow>
  )
}

export default FollowedAuctionItem;