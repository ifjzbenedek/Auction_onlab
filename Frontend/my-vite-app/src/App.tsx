import React from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import Home from './pages/Home.tsx';
import Profile from './pages/Profile.tsx';
import { AppBar, Toolbar, Typography, Button } from '@mui/material';
import Login from './pages/Login.tsx';

const App: React.FC = () => {
  return (
    <Router>
      <AppBar position="static">
        <Toolbar>
          <Typography variant="h6" style={{ flexGrow: 1 }}>
            BidVerse App
          </Typography>
          <Button color="inherit" component={Link} to="/users">
            Home
          </Button>
          <Button color="inherit" component={Link} to="/users/me">
            Profile
          </Button>
          <Button color="inherit" component={Link} to="/users/login">
            Login
          </Button>
        </Toolbar>
      </AppBar>
      <Routes>
        <Route path="/users" element={<Home />} />
        <Route path="/users/me" element={<Profile />} />
        <Route path="/users/login" element={<Login />} />
      </Routes>
    </Router>
  );
};

export default App;