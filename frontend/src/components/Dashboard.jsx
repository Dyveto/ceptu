import React, { useState, useEffect } from 'react';
import { apiFetch } from '../api/apiClient.js';

function Dashboard({ onLogout }) {
  const [activeTab, setActiveTab] = useState('inventory');
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      setError('');
      setData([]);
      
      try {
        let endpoint = '';
        if (activeTab === 'inventory') endpoint = '/products';
        if (activeTab === 'customers') endpoint = '/customers';
        if (activeTab === 'orders') endpoint = '/orders';
        if (activeTab === 'reports') endpoint = '/reports/low-stock-products';

        const res = await apiFetch(endpoint, { method: 'GET' });
        setData(res || []);
      } catch (err) {
        setError(err.message || 'Error al cargar los datos del módulo');
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [activeTab]);

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
      <div style={{ display: 'flex', gap: '0.5rem', marginTop: '1.5rem', borderBottom: '1px solid #ccc', paddingBottom: '0px' }}>
        <button 
          onClick={() => setActiveTab('inventory')} 
          style={tabStyle(activeTab === 'inventory')}
        >
          📦 Inventario
        </button>
        <button 
          onClick={() => setActiveTab('customers')} 
          style={tabStyle(activeTab === 'customers')}
        >
          👥 Clientes
        </button>
        <button 
          onClick={() => setActiveTab('orders')} 
          style={tabStyle(activeTab === 'orders')}
        >
          🛒 Pedidos
        </button>
        <button 
          onClick={() => setActiveTab('reports')} 
          style={tabStyle(activeTab === 'reports')}
        >
          📊 Reportes Alertas
        </button>
      </div>

      {/* CONTENIDO DINÁMICO */}
      <div style={{ marginTop: '1.5rem', minHeight: '300px' }}>
        {loading && <p style={{ textAlign: 'center', color: '#666' }}>Cargando datos desde la API segura de Ceptu...</p>}
        {error && <p style={{ color: 'red', fontWeight: 'bold', padding: '1rem', background: '#ffebeb', borderRadius: '4px' }}>⚠️ Error: {error}</p>}
        
        {!loading && !error && (
          <>
            {activeTab === 'inventory' && renderInventoryTable(data)}
            {activeTab === 'customers' && renderCustomersTable(data)}
            {activeTab === 'orders' && renderOrdersTable(data)}
            {activeTab === 'reports' && renderReportsTable(data)}
          </>
        )}
      </div>
    </div>
  );
}

function renderInventoryTable(products) {
  return (
    <div>
      <h3>Inventario de Productos</h3>
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>Nombre del Producto</th>
            <th>SKU</th>
            <th>Precio</th>
          </tr>
        </thead>
        <tbody>
          {products.length === 0 ? (
            <tr><td colSpan="4">No hay productos registrados en base de datos.</td></tr>
          ) : (
            products.map((p) => (
              <tr key={p.id}>
                <td><small style={{ color: '#888' }}>{p.id}</small></td>
                <td><strong>{p.name}</strong></td>
                <td><code style={{ background: '#eee', padding: '2px 4px', borderRadius: '3px' }}>{p.sku || 'N/A'}</code></td>
                <td>${p.price?.toLocaleString()}</td>
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  );
}

function renderCustomersTable(customers) {
  return (
    <div>
      <h3>Gestión de Clientes</h3>
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>Nombre</th>
            <th>Email</th>
            <th>Estado</th>
          </tr>
        </thead>
        <tbody>
          {customers.length === 0 ? (
            <tr><td colSpan="4">No hay clientes registrados en el sistema.</td></tr>
          ) : (
            customers.map((c) => (
              <tr key={c.id}>
                <td><small style={{ color: '#888' }}>{c.id}</small></td>
                <td>{c.firstName} {c.lastName}</td>
                <td>{c.email}</td>
                <td>
                  <span style={{ background: c.status === 'ACTIVE' ? '#e1f5fe' : '#ffe0b2', color: '#0288d1', padding: '3px 8px', borderRadius: '12px', fontSize: '0.85rem' }}>
                    {c.status}
                  </span>
                </td>
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  );
}

function renderOrdersTable(orders) {
  return (
    <div>
      <h3>Órdenes y Pedidos</h3>
      <table>
        <thead>
          <tr>
            <th>ID de Pedido</th>
            <th>Cliente</th>
            <th>Fecha</th>
            <th>Estado</th>
          </tr>
        </thead>
        <tbody>
          {orders.length === 0 ? (
            <tr><td colSpan="4">No se han procesado pedidos aún.</td></tr>
          ) : (
            orders.map((o) => (
              <tr key={o.id}>
                <td><small style={{ color: '#888' }}>{o.id}</small></td>
                <td>{o.customerName || 'Cliente de la App'}</td>
                <td>{o.orderDate ? new Date(o.orderDate).toLocaleDateString() : 'Reciente'}</td>
                <td>
                  <span style={{ background: '#e8f5e9', color: '#2e7d32', padding: '3px 8px', borderRadius: '4px', fontWeight: 'bold', fontSize: '0.85rem' }}>
                    {o.status}
                  </span>
                </td>
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  );
}

function renderReportsTable(lowStockData) {
  return (
    <div>
      <h3>Alerta Crítica: Productos con Bajo Stock</h3>
      <p style={{ fontSize: '0.9rem', color: '#555' }}>Lista en tiempo real mapeada directamente desde el sub-módulo de analítica analizando mínimos requeridos.</p>
      <table>
        <thead>
          <tr>
            <th>Producto</th>
            <th>Stock Actual</th>
            <th>Stock Mínimo</th>
            <th>Estado</th>
          </tr>
        </thead>
        <tbody>
          {lowStockData.length === 0 ? (
            <tr><td colSpan="4" style={{ color: 'green', fontWeight: 'bold' }}>✅ Todos los productos tienen niveles estables de stock.</td></tr>
          ) : (
            lowStockData.map((item, index) => (
              <tr key={index}>
                <td><strong>{item.productName || item.name}</strong></td>
                <td style={{ color: 'red', fontWeight: 'bold' }}>{item.currentStock || item.availableStock} unds</td>
                <td>{item.minimumStock} unds</td>
                <td><span style={{ background: '#ffebee', color: '#c62828', padding: '2px 6px', borderRadius: '4px', fontSize: '0.8rem' }}>Reabastecer</span></td>
              </tr>
            ))
          )}
        </tbody>
      </table>
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
    transform: isActive ? 'translateY(1px)' : 'none'
  };
}

export default Dashboard;