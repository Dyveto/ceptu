import React, { useState } from 'react';
import InventoryTab from './tabs/InventoryTab.jsx';
import CustomersTab from './tabs/CustomersTab.jsx';
import OrdersTab from './tabs/OrdersTab.jsx';
import ReportsTab from './tabs/ReportsTab.jsx';

function Dashboard({ onLogout }) {
  const [activeTab, setActiveTab] = useState('inventory');

  return (
    <div className="dashboard-container" style={{ maxWidth: '1100px', width: '100%' }}>
      {/* CABECERA */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderBottom: '2px solid #eee', paddingBottom: '1rem' }}>
        <div>
          <h2 style={{ margin: 0, color: '#111' }}>Ceptu Secure Admin</h2>
          <small style={{ color: '#666' }}>Panel de Control e Inventario</small>
        </div>
        <button onClick={onLogout} style={{ width: 'auto', padding: '0.6rem 1.2rem', background: '#ff3b30', margin: 0 }}>
          Cerrar Sesión
        </button>
      </div>

      {/* BARRA DE TABS */}
      <div style={{ display: 'flex', gap: '0.5rem', marginTop: '1.5rem', borderBottom: '1px solid #ccc' }}>
        <button onClick={() => setActiveTab('inventory')} style={tabStyle(activeTab === 'inventory')}>📦 Inventario</button>
        <button onClick={() => setActiveTab('customers')} style={tabStyle(activeTab === 'customers')}>👥 Clientes</button>
        <button onClick={() => setActiveTab('orders')} style={tabStyle(activeTab === 'orders')}>🛒 Pedidos</button>
        <button onClick={() => setActiveTab('reports')} style={tabStyle(activeTab === 'reports')}>📊 Reportes Alertas</button>
      </div>

      {/* RENDERIZADO CONDICIONAL DE COMPONENTES MODULARES */}
      <div style={{ marginTop: '1.5rem', minHeight: '300px' }}>
        {activeTab === 'inventory' && <InventoryTab />}
        {activeTab === 'customers' && <CustomersTab />}
        {activeTab === 'orders' && <OrdersTab />}
        {activeTab === 'reports' && <ReportsTab />}
      </div>
    </div>
  );
}

function tabStyle(isActive) {
  return {
    background: isActive ? '#0076ff' : 'transparent',
    color: isActive ? 'white' : '#333',
    border: '1px solid #ccc',
    borderBottom: isActive ? '1px solid #0076ff' : '1px solid #ccc',
    borderRadius: '4px 4px 0 0',
    padding: '0.6rem 1.2rem',
    cursor: 'pointer',
    fontWeight: 'bold',
    margin: 0,
    transition: 'all 0.2s ease',
  };
}

export default Dashboard;