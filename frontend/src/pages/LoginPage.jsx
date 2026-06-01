import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import Login from '../components/Login.jsx';

const LoginPage = () => {
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleLoginSuccess = (token) => {
    login(token);
    navigate('/dashboard');
  };

  return (
    <Login 
      onLoginSuccess={handleLoginSuccess} 
      onSwitchToRegister={() => navigate('/register')} 
    />
  );
};

export default LoginPage;