import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import { OrderProvider } from './context/OrderContext.jsx';
import { UIProvider } from './context/UIContext.jsx';
import { InventoryProvider } from './context/InventoryContext.jsx';
import { CustomerProvider } from './context/CustomerContext.jsx';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import DashboardPage from './pages/DashboardPage';

const PrivateRoute = ({ children }) => {
    const { user } = useAuth();
    return user ? children : <Navigate to="/login" replace />;
};

function App() {
    return (
        <AuthProvider>
            <UIProvider>
                <OrderProvider>
                    <InventoryProvider>
                        <CustomerProvider>
                            <BrowserRouter>
                                <Routes>
                                    <Route path="/login" element={<LoginPage />} />
                                    <Route path="/register" element={<RegisterPage />} />
                                    <Route
                                        path="/dashboard"
                                        element={
                                            <PrivateRoute>
                                                <DashboardPage />
                                            </PrivateRoute>
                                        }
                                    />
                                    <Route path="*" element={<Navigate to="/login" replace />} />
                                </Routes>
                            </BrowserRouter>
                        </CustomerProvider>
                    </InventoryProvider>
                </OrderProvider>
            </UIProvider>
        </AuthProvider>
    );
}

export default App;