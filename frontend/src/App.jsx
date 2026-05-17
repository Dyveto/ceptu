import React, { useState, useEffect } from 'react';
import Login from './components/Login.jsx';
import Register from './components/Register.jsx';
import Dashboard from './components/Dashboard.jsx';

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [view, setView] = useState('login');

  useEffect(() => {
    const token = localStorage.getItem('ceptu_token');
    if (token) {
      setIsAuthenticated(true);
    }
  }, []);

  const handleLoginSuccess = (token) => {
    localStorage.setItem('ceptu_token', token);
    setIsAuthenticated(true);
  };

  const handleLogout = () => {
    localStorage.removeItem('ceptu_token');
    setIsAuthenticated(false);
    setView('login');
  };

  if (isAuthenticated) {
    return <Dashboard onLogout={handleLogout} />;
  }

  return (
    <>
      {view === 'login' ? (
        <Login 
          onLoginSuccess={handleLoginSuccess} 
          onSwitchToRegister={() => setView('register')} 
        />
      ) : (
        <Register 
          onRegisterSuccess={() => setView('login')} 
          onSwitchToLogin={() => setView('login')} 
        />
      )}
    </>
  );
}

export default App;