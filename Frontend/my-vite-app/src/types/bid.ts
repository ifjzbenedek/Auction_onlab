export interface BidRequestDTO {
    amount: number 
  }

  export interface BidDTO {
    id: number;
    value: number;
    timeStamp: string; // ISO dátum formátumban érkezik, pl. "2025-03-24T19:26:28.3"
    isWinning: boolean;
    bidder: {
      id: number;
      userName: string; // Elég a felhasználónév, egyéb adatok (email, tel.) nem szükségesek
    };
  }
  