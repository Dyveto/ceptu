import { useAuth } from '../context/AuthContext';
import Dashboard from '../components/Dashboard.jsx';

const DashboardPage = () => {
  const { logout } = useAuth();

  return <Dashboard onLogout={logout} />;
};

export default DashboardPage;