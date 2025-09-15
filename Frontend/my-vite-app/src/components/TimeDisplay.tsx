import React, { useRef, useEffect } from 'react';
import { Box, Typography, Chip } from '@mui/material';
import { Clock } from 'lucide-react';
import { useTimer } from './TimeHook';
import { TimeDisplayProps } from '../types/TimeUtils/TimeDisplayProps';
import { calculateSmartTimeLeft } from '../utils/timeUtils';
import { AuctionStatus, calculateAuctionStatus } from '../utils/auctionStatusUtils';

interface ExtendedTimeDisplayProps extends TimeDisplayProps {
  auctionId?: number;
  currentStatus?: AuctionStatus | string;
  onStatusChange?: (auctionId: number, newStatus: string) => void;
}

const TimeDisplay: React.FC<ExtendedTimeDisplayProps> = ({
  expiredDate,
  startDate,
  variant = 'compact',
  showIcon = true,
  size = 'medium',
  auctionId,
  currentStatus,
  onStatusChange
}) => {
  const prevStatusRef = useRef<string | null>(null);
  
  useTimer(1000);
  
  // Státusz alapján dönt, hogy startDate-ig vagy expiredDate-ig számoljon
  const { timeString, isExpired, totalSeconds, targetEvent, status: smartStatus } = calculateSmartTimeLeft(startDate, expiredDate);
  
  // Kiszámoljuk az aktuális státuszt az időpontok alapján
  const { status: calculatedStatus } = calculateAuctionStatus(startDate, expiredDate);

  // Automatic status change logic - csak frontend státusz frissítés
  useEffect(() => {
    if (!auctionId || !currentStatus || !onStatusChange) return;

    const currentStatusStr = typeof currentStatus === 'string' ? currentStatus : currentStatus;
    
    // Ha a számított státusz eltér a jelenlegi státusztól és még nem frissítettük
    if (calculatedStatus !== currentStatusStr.toUpperCase() && prevStatusRef.current !== calculatedStatus) {
      prevStatusRef.current = calculatedStatus;
      
      // Frontend + Backend státusz frissítés a StatusHook-on keresztül
      onStatusChange(auctionId, calculatedStatus);
      console.log(`🔄 Auction ${auctionId} status automatically updated from ${currentStatusStr} to ${calculatedStatus}`);
    }
  }, [auctionId, currentStatus, calculatedStatus, onStatusChange]);

  // Szöveg meghatározása aszerint, hogy mihez számolunk vissza
  const getDisplayLabel = () => {
    if (isExpired) {
      return smartStatus === 'UPCOMING' ? "Starting now" : "Expired";
    }
    
    switch (targetEvent) {
      case 'start':
        return "Starts in";
      case 'end':
        return "Ends in";
      case 'expired':
        return "Expired";
      default:
        return "Time left";
    }
  };

  // Chip label
  const getChipLabel = () => {
    if (isExpired) {
      return smartStatus === 'UPCOMING' ? "Starting" : "Expired";
    }
    return timeString;
  };

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
        label={getChipLabel()}
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
          {getDisplayLabel()}
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
            {smartStatus === 'UPCOMING' ? 'Auction is starting' : 'This auction has expired'}
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
        {isExpired ? getDisplayLabel() : `${getDisplayLabel()}: ${timeString}`}
      </Typography>
    </Box>
  );
};

export default TimeDisplay;