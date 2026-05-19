import React, { useState, useEffect } from 'react';
import { apiFetch } from '../../api/apiClient.js';

export default function ReportsTab() {
  const [lowStock, setLowStock] = useState([]);
  const [monthlyIncome, setMonthlyIncome] = useState(0); 
  const [topCustomers, setTopCustomers] = useState([]);
  const [bestSelling, setBestSelling] = useState([]);

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const [startDate, setStartDate] = useState('2026-01-01T00:00:00Z');
  const [endDate, setEndDate] = useState('2026-12-31T23:59:59Z');
  const [limit, setLimit] = useState(5);

  const loadAnalyticsData = async () => {
    setLoading(true);
    setError('');
    try {
      const topCustomersPath = `/reports/top-customers?limit=${limit}`;
      const bestSellingPath = `/reports/best-selling-products?startDate=${encodeURIComponent(startDate)}&endDate=${encodeURIComponent(endDate)}&limit=${limit}`;

      const [lowRes, incomeRes, topCustRes, bestSellRes] = await Promise.all([
        apiFetch('/reports/low-stock-products', { method: 'GET' }),
        apiFetch('/reports/monthly-income', { method: 'GET' }),
        apiFetch(topCustomersPath, { method: 'GET' }),
        apiFetch(bestSellingPath, { method: 'GET' })
      ]);

      setLowStock(lowRes || []);
      setTopCustomers(topCustRes || []);
      setBestSelling(bestSellRes || []);

      if (Array.isArray(incomeRes)) {
        const totalConsolidado = incomeRes.reduce((acc, curr) => acc + (curr.total || 0), 0);
        setMonthlyIncome(totalConsolidado);
      } else {
        setMonthlyIncome(0);
      }

    } catch (err) {
      setError(err.message || 'Error al compilar las métricas del servidor');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadAnalyticsData();
  }, [startDate, endDate, limit]);

  return (
    <div>
      {error && <p style={{ color: 'red', background: '#ffebee', padding: '1rem', borderRadius: '4px' }}>⚠️ {error}</p>}

      {/* BARRA DE FILTROS TEMPORALES Y CONTROL DE LÍMITES */}
      <div style={{ display: 'flex', gap: '1rem', background: '#f1f5f9', padding: '1rem', borderRadius: '6px', marginBottom: '1.5rem', alignItems: 'center', flexWrap: 'wrap' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <label style={{ fontWeight: 'bold', fontSize: '0.9rem' }}>Desde:</label>
          <input 
            type="date" 
            value={startDate.split('T')[0]} 
            onChange={e => setStartDate(`${e.target.value}T00:00:00Z`)} 
            style={{ margin: 0, padding: '4px 8px' }}
          />
        </div>
        
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <label style={{ fontWeight: 'bold', fontSize: '0.9rem' }}>Hasta:</label>
          <input 
            type="date" 
            value={endDate.split('T')[0]} 
            onChange={e => setEndDate(`${e.target.value}T23:59:59Z`)} 
            style={{ margin: 0, padding: '4px 8px' }}
          />
        </div>

        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <label style={{ fontWeight: 'bold', fontSize: '0.9rem' }}>Límite Filas:</label>
          <select 
            value={limit} 
            onChange={e => setLimit(parseInt(e.target.value))} 
            style={{ margin: 0, padding: '4px', width: '70px' }}
          >
            <option value="5">5</option>
            <option value="10">10</option>
            <option value="20">20</option>
          </select>
        </div>

        <button 
          onClick={loadAnalyticsData} 
          style={{ width: 'auto', margin: 0, padding: '0.5rem 1rem', background: '#0076ff', fontSize: '0.85rem' }}
          disabled={loading}
        >
          🔄 {loading ? 'Sincronizando...' : 'Refrescar Analítica'}
        </button>
      </div>

      {/* TARJETAS DE MÉTRICAS PRINCIPALES (SCORECARDS) */}
      <div style={{ display: 'flex', gap: '1rem', marginBottom: '2rem' }}>
        <div style={{ flex: 1, background: '#e3f2fd', borderLeft: '5px solid #2196f3', padding: '1.2rem', borderRadius: '6px' }}>
          <small style={{ color: '#0d47a1', fontWeight: 'bold', textTransform: 'uppercase' }}>💰 Ingresos Consolidados</small>
          <h2 style={{ margin: '0.5rem 0 0 0', color: '#0d47a1' }}>${monthlyIncome.toLocaleString()} COP</h2>
        </div>
        <div style={{ flex: 1, background: lowStock.length > 0 ? '#ffebee' : '#e8f5e9', borderLeft: `5px solid ${lowStock.length > 0 ? '#f44336' : '#4caf50'}`, padding: '1.2rem', borderRadius: '6px' }}>
          <small style={{ color: lowStock.length > 0 ? '#c62828' : '#1b5e20', fontWeight: 'bold', textTransform: 'uppercase' }}>⚠️ Alertas de Reposición</small>
          <h2 style={{ margin: '0.5rem 0 0 0', color: lowStock.length > 0 ? '#c62828' : '#1b5e20' }}>
            {lowStock.length} {lowStock.length === 1 ? 'Producto crítico' : 'Productos críticos bajo el mínimo'}
          </h2>
        </div>
      </div>

      {/* GRILLA DE TABLAS DE REPORTES */}
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '2rem', marginTop: '1rem' }}>
        
        {/* TABLA: PRODUCTOS MÁS VENDIDOS */}
        <div style={{ background: 'white', padding: '1rem', borderRadius: '6px', border: '1px solid #e2e8f0' }}>
          <h4 style={{ margin: '0 0 1rem 0', color: '#333' }}>🔥 Productos Más Vendidos</h4>
          <table>
            <thead>
              <tr><th>Producto</th><th>Unidades Vendidas</th></tr>
            </thead>
            <tbody>
              {bestSelling.length === 0 ? (
                <tr><td colSpan="2" style={{ color: '#999', fontStyle: 'italic' }}>Sin movimientos en este rango de fechas.</td></tr>
              ) : (
                bestSelling.map((item, index) => (
                  <tr key={index}>
                    <td><strong>{item.productName}</strong></td>
                    <td style={{ color: '#0076ff', fontWeight: 'bold' }}>{item.totalSold || 0} unds</td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        {/* TABLA: TOP CLIENTES */}
        <div style={{ background: 'white', padding: '1rem', borderRadius: '6px', border: '1px solid #e2e8f0' }}>
          <h4 style={{ margin: '0 0 1rem 0', color: '#333' }}>👑 Clientes Destacados (Top Facturación)</h4>
          <table>
            <thead>
              <tr><th>Cliente</th><th>Total Compras</th></tr>
            </thead>
            <tbody>
              {topCustomers.length === 0 ? (
                <tr><td colSpan="2" style={{ color: '#999', fontStyle: 'italic' }}>No hay registros comerciales aún.</td></tr>
              ) : (
                topCustomers.map((item, index) => (
                  <tr key={index}>
                    <td>{item.fullName}</td>
                    <td style={{ color: '#34c759', fontWeight: 'bold' }}>${item.totalSpent?.toLocaleString()} COP</td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

      </div>

      {/* DETALLE INFERIOR: SECCIÓN ALERTA DE STOCK COMPLETA */}
      <div style={{ marginTop: '2rem', background: '#fff', padding: '1rem', borderRadius: '6px', border: '1px solid #e2e8f0' }}>
        <h4 style={{ margin: '0 0 1rem 0', color: '#333' }}>📦 Auditoría de Inventario Mínimo Crítico</h4>
        <table>
          <thead>
            <tr><th>Producto</th><th>Stock Actual</th><th>Mínimo Requerido</th><th>Estado de Alerta</th></tr>
          </thead>
          <tbody>
            {lowStock.length === 0 ? (
              <tr><td colSpan="4" style={{ color: 'green', fontWeight: 'bold', textAlign: 'center' }}>✅ Todos los productos operan sobre el margen de seguridad establecido.</td></tr>
            ) : (
              lowStock.map((item, index) => (
                <tr key={index}>
                  <td><strong>{item.productName}</strong></td>
                  <td style={{ color: 'red', fontWeight: 'bold' }}>{item.availableStock} unds</td>
                  <td>{item.minimumStock} unds</td>
                  <td><span style={{ background: '#ffebee', color: '#c62828', padding: '2px 6px', borderRadius: '4px', fontSize: '0.8rem', fontWeight: 'bold' }}>Reabastecer Ya</span></td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

    </div>
  );
}