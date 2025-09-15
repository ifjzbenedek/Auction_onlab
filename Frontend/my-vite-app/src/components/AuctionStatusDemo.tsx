import React, { useState, useEffect } from 'react';
import { Box, Typography, Card, CardContent, Button, Chip } from '@mui/material';
import { calculateAuctionStatus, getStatusColor, getStatusLabel } from '../utils/auctionStatusUtils';

interface DemoAuction {
  id: number;
  name: string;
  startDate: string | null;
  endDate: string;
}

/**
 * Demo komponens az idő alapú státusz számítás bemutatására
 */
const AuctionStatusDemo: React.FC = () => {
  const [currentTime, setCurrentTime] = useState(new Date());
  
  // Update time every second
  useEffect(() => {
    const timer = setInterval(() => {
      setCurrentTime(new Date());
    }, 1000);
    
    return () => clearInterval(timer);
  }, []);

  const now = new Date();
  
  const demoAuctions: DemoAuction[] = [
    {
      id: 1,
      name: "Upcoming Auction (starts in 30 min)",
      startDate: new Date(now.getTime() + 30 * 60 * 1000).toISOString(),
      endDate: new Date(now.getTime() + 90 * 60 * 1000).toISOString(),
    },
    {
      id: 2,
      name: "Active Auction (started 10 min ago)",
      startDate: new Date(now.getTime() - 10 * 60 * 1000).toISOString(),
      endDate: new Date(now.getTime() + 50 * 60 * 1000).toISOString(),
    },
    {
      id: 3,
      name: "Closed Auction (ended 5 min ago)",
      startDate: new Date(now.getTime() - 65 * 60 * 1000).toISOString(),
      endDate: new Date(now.getTime() - 5 * 60 * 1000).toISOString(),
    },
    {
      id: 4,
      name: "No Start Date - Active Now",
      startDate: null,
      endDate: new Date(now.getTime() + 30 * 60 * 1000).toISOString(),
    },
    {
      id: 5,
      name: "No Start Date - Expired",
      startDate: null,
      endDate: new Date(now.getTime() - 10 * 60 * 1000).toISOString(),
    }
  ];

  const handleTestInConsole = () => {
    import('../utils/testAuctionStatus').then(module => {
      module.testAuctionStatusCalculation();
    });
  };

  return (
    <Box sx={{ p: 3, maxWidth: 800, mx: 'auto' }}>
      <Typography variant="h4" gutterBottom>
        🎯 Auction Status Demo
      </Typography>
      
      <Typography variant="body1" sx={{ mb: 3 }}>
        Current time: <strong>{currentTime.toLocaleString('hu-HU')}</strong>
      </Typography>
      
      <Button 
        variant="contained" 
        onClick={handleTestInConsole}
        sx={{ mb: 3 }}
      >
        Run Console Tests
      </Button>

      <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
        {demoAuctions.map((auction) => {
          const statusInfo = calculateAuctionStatus(auction.startDate, auction.endDate);
          
          return (
            <Card key={auction.id} sx={{ boxShadow: 2 }}>
              <CardContent>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                  <Typography variant="h6">
                    {auction.name}
                  </Typography>
                  <Chip
                    label={getStatusLabel(statusInfo.status)}
                    color={getStatusColor(statusInfo.status)}
                    size="medium"
                    sx={{ fontWeight: 'bold' }}
                  />
                </Box>
                
                <Typography variant="body2" color="text.secondary">
                  Start: {auction.startDate ? new Date(auction.startDate).toLocaleString('hu-HU') : 'Immediate'}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  End: {new Date(auction.endDate).toLocaleString('hu-HU')}
                </Typography>
                
                <Box sx={{ mt: 2, p: 1, bgcolor: 'grey.100', borderRadius: 1 }}>
                  <Typography variant="caption" component="div">
                    <strong>Calculated Status:</strong> {statusInfo.status}
                  </Typography>
                  <Typography variant="caption" component="div">
                    <strong>Has Started:</strong> {statusInfo.hasStarted ? '✅' : '❌'}
                  </Typography>
                  <Typography variant="caption" component="div">
                    <strong>Is Expired:</strong> {statusInfo.isExpired ? '✅' : '❌'}
                  </Typography>
                </Box>
              </CardContent>
            </Card>
          );
        })}
      </Box>
    </Box>
  );
};

export default AuctionStatusDemo;
