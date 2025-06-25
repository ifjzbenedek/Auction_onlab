import React from 'react';
import { Box, Typography, Chip } from '@mui/material';
import { Clock } from 'lucide-react';
import { useTimer } from './TimeHook';
import { TimeDisplayProps } from '../types/TimeUtils/TimeDisplayProps';
import { calculateTimeLeft } from '../utils/timeUtils';

const TimeDisplay: React.FC<TimeDisplayProps> = ({
  expiredDate,
  variant = 'compact',
  showIcon = true,
  size = 'medium'
}) => {
  // Use the timer to trigger re-renders
  useTimer(1000);
  
  const { timeString, isExpired, totalSeconds } = calculateTimeLeft(expiredDate);

  // Színek meghatározása
  const getColor = () => {
    if (isExpired) return '#f44336'; // Piros
    if (totalSeconds < 3600) return '#ff9800'; // Narancs (kevesebb mint 1 óra)
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