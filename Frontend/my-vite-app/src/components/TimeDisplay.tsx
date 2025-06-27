import React, { useRef, useEffect } from 'react';
import { Box, Typography, Chip } from '@mui/material';
import { Clock } from 'lucide-react';
import { useTimer } from './TimeHook';
import { TimeDisplayProps } from '../types/TimeUtils/TimeDisplayProps';
import { calculateTimeLeft } from '../utils/timeUtils';

interface ExtendedTimeDisplayProps extends TimeDisplayProps {
  auctionId?: number;
  currentStatus?: string;
  onStatusChange?: (auctionId: number, newStatus: string) => void;
}

const TimeDisplay: React.FC<ExtendedTimeDisplayProps> = ({
  expiredDate,
  variant = 'compact',
  showIcon = true,
  size = 'medium',
  auctionId,
  currentStatus,
  onStatusChange
}) => {
  const prevStatusRef = useRef<string | null>(null);
  
  useTimer(1000);
  
  const { timeString, isExpired, totalSeconds } = calculateTimeLeft(expiredDate);

  // Automatic status change logic
  useEffect(() => {
  if (!auctionId || !currentStatus || !onStatusChange) return;

  const now = new Date();
  const expiredDateTime = new Date(expiredDate);
  const isCurrentlyExpired = now >= expiredDateTime;

  // PENDING/UPCOMING -> ACTIVE (auction starts)
  if (currentStatus.toUpperCase() === 'PENDING' && !isCurrentlyExpired && totalSeconds > 0 && prevStatusRef.current !== 'started') {
    // ⭐ JAVÍTÁS: Egyszerűsített logika - ha PENDING és még nem járt le, akkor indítjuk
    if (totalSeconds > 0) {
      onStatusChange(auctionId, 'ACTIVE');
      prevStatusRef.current = 'started';
      console.log(`🚀 Auction ${auctionId} started automatically`);
    }
  }

  // ACTIVE -> CLOSED (auction expires)
  if (currentStatus.toUpperCase() === 'ACTIVE' && isCurrentlyExpired && prevStatusRef.current !== 'expired') {
    onStatusChange(auctionId, 'CLOSED');
    prevStatusRef.current = 'expired';
    console.log(` Auction ${auctionId} expired automatically`);
  }
}, [auctionId, currentStatus, expiredDate, onStatusChange, totalSeconds, isExpired]);

  // Színek meghatározása
  const getColor = () => {
    if (isExpired) return '#f44336'; // Piros
    if (totalSeconds < 86400) return '#ff9800'; // Narancs (kevesebb mint 1 nap)
    return '#00c853'; // Zöld
  };

  // Szöveg méret - use proper MUI typography variants
  const getTextVariant = (): "body2" | "body1" | "h6" => {
    switch (size) {
      case 'small': return 'body2';
      case 'large': return 'h6';
      default: return 'body1';
    }
  };

  // Ikon méret
  const getIconSize = () => {
    switch (size) {
      case 'small': return 14;
      case 'large': return 24;
      default: return 18;
    }
  };

  // Chip variant
  if (variant === 'chip') {
    return (
      <Chip
        icon={showIcon ? <Clock size={14} /> : undefined}
        label={isExpired ? "Expired" : timeString}
        size={size === 'large' ? 'medium' : 'small'}
        sx={{
          backgroundColor: getColor(),
          color: 'white',
          fontWeight: 'bold',
          '& .MuiChip-icon': {
            color: 'white'
          }
        }}
      />
    );
  }

  // Detailed variant
  if (variant === 'detailed') {
    return (
      <Box sx={{ textAlign: 'center', p: 2, border: '1px solid #e0e0e0', borderRadius: 2 }}>
        {showIcon && (
          <Box sx={{ display: 'flex', justifyContent: 'center', mb: 1 }}>
            <Clock 
              size={getIconSize()} 
              color={getColor()}
            />
          </Box>
        )}
        <Typography variant={getTextVariant()} sx={{ fontWeight: 'bold', mb: 1 }}>
          {isExpired ? "Auction Ended" : "Time Remaining"}
        </Typography>
        <Typography 
          variant={size === 'large' ? 'h5' : 'h6'} 
          sx={{ 
            color: getColor(),
            fontWeight: 'bold',
            fontFamily: 'monospace'
          }}
        >
          {timeString}
        </Typography>
        {isExpired && (
          <Typography variant="caption" color="error" sx={{ mt: 1, display: 'block' }}>
            This auction has expired
          </Typography>
        )}
      </Box>
    );
  }

  // Compact variant (default)
  return (
    <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
      {showIcon && (
        <Clock size={getIconSize()} color={getColor()} />
      )}
      <Typography 
        variant={getTextVariant()}
        sx={{ 
          color: getColor(),
          fontWeight: isExpired ? "bold" : "normal",
          fontFamily: 'monospace'
        }}
      >
        {isExpired ? "Expired" : timeString}
      </Typography>
    </Box>
  );
};

export default TimeDisplay;