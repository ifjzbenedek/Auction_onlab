import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

const Login: React.FC = () => {
    const navigate = useNavigate();

    useEffect(() => {
        window.location.href = 'https://localhost:8081/oauth2/authorization/google';
    }, [navigate]);

    return <div>Redirecting to Google Login...</div>;
};

export default Login;