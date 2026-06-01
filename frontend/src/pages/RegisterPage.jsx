import { useNavigate } from 'react-router-dom';
import Register from '../components/Register.jsx';

const RegisterPage = () => {
  const navigate = useNavigate();

  return (
    <Register 
      onRegisterSuccess={() => navigate('/login')} 
      onSwitchToLogin={() => navigate('/login')} 
    />
  );
};

export default RegisterPage;