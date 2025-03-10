import React, { useEffect, useState } from 'react';

interface User {
  userName: string;
  emailAddress: string;
  phoneNumber: string;
}

const Profile: React.FC = () => {
  const [user, setUser] = useState<User | null>(null);

  useEffect(() => {
    // Felhasználó adatainak lekérése a backendtől
    fetch('/users/me', { credentials: 'include' })
      .then((response) => {
        if (!response.ok) {
          throw new Error('Failed to fetch user data');
        }
        return response.json();
      })
      .then((data) => setUser(data))
      .catch((error) => console.error(error));
  }, []);

  if (!user) {
    return <div>Loading...</div>;
  }

  return (
    <div>
      <h1>Profile</h1>
      <p>Username: {user.userName}</p>
      <p>Email: {user.emailAddress}</p>
      <p>Phone: {user.phoneNumber}</p>
    </div>
  );
};

export default Profile;