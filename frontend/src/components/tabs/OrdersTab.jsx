import React, { useState, useEffect } from 'react';
import { apiFetch } from '../../api/apiClient.js';
import { useOrderContext } from '../../context/OrderContext.jsx';

export default function OrdersTab() {
  const [orders, setOrders] = useState([]);
  const [customers, setCustomers] = useState([]);
  const [products, setProducts] = useState([]);
  const [customerAddresses, setCustomerAddresses] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [showCreateForm, setShowCreateForm] = useState(false);
  const [selectedCustomerId, setSelectedCustomerId] = useState('');
  const [selectedAddressId, setSelectedAddressId] = useState('');
  const [cancelingOrderId, setCancelingOrderId] = useState(null);
  const [cancelNotes, setCancelNotes] = useState('');
  const [formSuccess, setFormSuccess] = useState('');
  const [formError, setFormError] = useState('');

  // ── ESTADO PARA EL FILTRO ──
  const [searchTerm, setSearchTerm] = useState('');

  const [formItems, setFormItems] = useState([{ productId: '', quantity: 1 }]);
  const { addItem, setCustomer, clearOrder } = useOrderContext();

  const loadInitialData = async () => {
    setLoading(true);
    setError('');
    try {
      const [ordersRes, customersRes, productsRes] = await Promise.all([
        apiFetch('/orders', { method: 'GET' }),
        apiFetch('/customers', { method: 'GET' }),
        apiFetch('/products', { method: 'GET' })
      ]);
      setOrders(ordersRes || []);
      setCustomers(customersRes || []);
      setProducts(productsRes || []);
    } catch (err) {
      setError(err.message || 'Error al cargar los datos de pedidos');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { loadInitialData(); }, []);

  useEffect(() => {
    if (!selectedCustomerId) {
      setCustomerAddresses([]);
      return;
    }
    const fetchAddresses = async () => {
      try {
        const res = await apiFetch(`/customers/${selectedCustomerId}/addresses`, { method: 'GET' });
        setCustomerAddresses(res || []);
        setSelectedAddressId('');
      } catch (err) {
        setFormError('Error al cargar las direcciones del cliente seleccionado.');
      }
    };
    fetchAddresses();
  }, [selectedCustomerId]);

  const handleAddItem = () => setFormItems(prev => [...prev, { productId: '', quantity: 1 }]);
  const handleRemoveItem = (index) => setFormItems(prev => prev.filter((_, i) => i !== index));
  const handleItemChange = (index, field, value) => {
    setFormItems(prev => {
      const updated = [...prev];
      updated[index] = { ...updated[index], [field]: value };
      return updated;
    });
  };

  const calculateEstimatedTotal = () => {
    return formItems.reduce((sum, item) => {
      const product = products.find(p => p.id === item.productId);
      return sum + (product ? product.price * parseInt(item.quantity || 0) : 0);
    }, 0);
  };

  const handleCreateOrder = async (e) => {
    e.preventDefault();
    setFormError('');
    setFormSuccess('');

    if (formItems.some(item => !item.productId || item.quantity < 1)) {
      setFormError('Por favor, selecciona productos y cantidades válidas.');
      return;
    }

    // ── PRE-VALIDACIÓN DE STOCK EN EL FRONTEND ──
    for (const item of formItems) {
      const product = products.find(p => p.id === item.productId);
      const availableStock = product?.inventory?.availableStock ?? product?.availableStock ?? 0;

      if (parseInt(item.quantity) > availableStock) {
        setFormError(`⚠️ No hay suficiente stock de "${product?.name}". Tienes ${availableStock} unidades disponibles y solicitaste ${item.quantity}.`);
        return; // Abortamos la ejecución para no causar el error 500 en el backend
      }
    }

    clearOrder();
    setCustomer(selectedCustomerId);
    formItems.forEach(item => {
      const product = products.find(p => p.id === item.productId);
      if (product) addItem(product, parseInt(item.quantity));
    });

    try {
      await apiFetch('/orders', {
        method: 'POST',
        body: {
          customerId: selectedCustomerId,
          addressId: selectedAddressId,
          items: formItems.map(item => ({
            productId: item.productId,
            quantity: parseInt(item.quantity)
          }))
        }
      });
      setFormSuccess('¡Pedido registrado con éxito en estado CREATED!');
      setShowCreateForm(false);
      setSelectedCustomerId('');
      setSelectedAddressId('');
      setFormItems([{ productId: '', quantity: 1 }]);
      clearOrder();
      loadInitialData();
    } catch (err) {
      setFormError(err.message || 'Error al procesar el pedido');
    }
  };

  const handleUpdateStatus = async (orderId, action) => {
    setFormError(''); setFormSuccess('');
    try {
      await apiFetch(`/orders/${orderId}/${action}`, { method: 'PUT' });
      setFormSuccess(`¡Pedido actualizado con éxito [Acción: ${action}]!`);
      loadInitialData();
    } catch (err) {
      setFormError(err.message || `Error al ejecutar la acción ${action}`);
    }
  };

  const handleCancelOrderSubmit = async (e) => {
    e.preventDefault();
    try {
      await apiFetch(`/orders/${cancelingOrderId}/cancel`, {
        method: 'PUT',
        body: { notes: cancelNotes }
      });
      setFormSuccess('Pedido cancelado correctamente.');
      setCancelingOrderId(null);
      setCancelNotes('');
      loadInitialData();
    } catch (err) {
      setFormError(err.message || 'Error al cancelar el pedido');
    }
  };

  // ── LÓGICA DEL FILTRO ──
  const filteredOrders = orders.filter(o =>
      o.id?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      o.customerFullName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      o.status?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  if (loading && orders.length === 0) return <p style={{ textAlign: 'center' }}>Procesando transacciones comerciales...</p>;

  return (
      <div>
        {error && <p style={{ color: 'red', background: '#ffebeb', padding: '1rem', borderRadius: '4px' }}>⚠️ {error}</p>}
        {(formSuccess || formError) && (
            <div style={{ padding: '1rem', marginBottom: '1rem', borderRadius: '4px', background: formSuccess ? '#e8f5e9' : '#ffebee', color: formSuccess ? '#2e7d32' : '#c62828', fontWeight: 'bold' }}>
              {formSuccess || formError}
            </div>
        )}

        <div style={{ marginBottom: '1.5rem', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <button onClick={() => {
            setShowCreateForm(!showCreateForm);
            if (showCreateForm) {
              clearOrder(); setFormItems([{ productId: '', quantity: 1 }]);
              setSelectedCustomerId(''); setSelectedAddressId('');
            }
          }}
                  style={{ background: showCreateForm ? '#666' : '#0076ff', width: 'auto', padding: '0.6rem 1.2rem' }}>
            {showCreateForm ? '❌ Cancelar Pedido' : '🛒 Crear Nuevo Pedido'}
          </button>

          {/* BUSCADOR DE PEDIDOS */}
          <input
              type="text"
              placeholder="🔍 Buscar por ID, cliente o estado..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              style={{ padding: '0.5rem', width: '300px', borderRadius: '4px', border: '1px solid #ccc' }}
          />
        </div>

        {/* ... FORMULARIO (Se mantiene igual a tu código original) ... */}
        {showCreateForm && (
            <form onSubmit={handleCreateOrder} style={{ background: '#f9f9fb', padding: '1.5rem', borderRadius: '8px', border: '1px solid #eee', marginBottom: '2rem' }}>
              <h4>Formulario de Pedido Comercial</h4>
              <div style={{ display: 'flex', gap: '1rem', marginBottom: '1rem' }}>
                <div style={{ flex: 1 }}>
                  <label style={{ display: 'block', marginBottom: '0.3rem', fontWeight: 'bold' }}>Cliente</label>
                  <select value={selectedCustomerId} onChange={e => setSelectedCustomerId(e.target.value)} required>
                    <option value="">-- Selecciona Cliente --</option>
                    {customers.filter(c => c.status === 'ACTIVE' || !c.status).map(c => (
                        <option key={c.id} value={c.id}>{c.firstName} {c.lastName} ({c.email})</option>
                    ))}
                  </select>
                </div>
                <div style={{ flex: 1 }}>
                  <label style={{ display: 'block', marginBottom: '0.3rem', fontWeight: 'bold' }}>Dirección de Despacho</label>
                  <select value={selectedAddressId} onChange={e => setSelectedAddressId(e.target.value)} disabled={!selectedCustomerId} required>
                    <option value="">-- Selecciona Dirección --</option>
                    {customerAddresses.map(a => (<option key={a.id} value={a.id}>{a.street}, {a.city}</option>))}
                  </select>
                </div>
              </div>

              <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: 'bold' }}>Artículos del Pedido</label>
              {formItems.map((item, index) => (
                  <div key={index} style={{ display: 'flex', gap: '0.5rem', marginBottom: '0.5rem', alignItems: 'center' }}>
                    <select style={{ flex: 2, margin: 0 }} value={item.productId} onChange={e => handleItemChange(index, 'productId', e.target.value)} required>
                      <option value="">-- Selecciona Producto --</option>
                      {products.map(p => (
                          <option key={p.id} value={p.id}>{p.name} (${p.price?.toLocaleString()})</option>
                      ))}
                    </select>
                    <input style={{ flex: 0.5, margin: 0 }} type="number" min="1" placeholder="Cant" value={item.quantity} onChange={e => handleItemChange(index, 'quantity', e.target.value)} required />
                    {formItems.length > 1 && (<button type="button" onClick={() => handleRemoveItem(index)} style={{ background: '#ff3b30', width: 'auto', margin: 0, padding: '0.5rem 0.8rem' }}>🗑️</button>)}
                  </div>
              ))}
              <button type="button" onClick={handleAddItem} style={{ background: '#666', width: 'auto', display: 'block', marginTop: '0.5rem', padding: '0.4rem 0.8rem', fontSize: '0.85rem' }}>➕ Añadir Otro Producto</button>

              <div style={{ margin: '1.5rem 0 0.5rem 0', padding: '1rem', background: '#fff', border: '1px dashed #cbd5e1', borderRadius: '6px', textAlign: 'right' }}>
                <span style={{ fontSize: '0.95rem', fontWeight: 'bold', color: '#64748b' }}>Total Estimado: </span>
                <span style={{ fontSize: '1.3rem', fontWeight: 'bold', color: '#0f172a' }}>${calculateEstimatedTotal().toLocaleString()} COP</span>
              </div>
              <button type="submit" style={{ background: '#34c759', width: '100%', marginTop: '1rem', fontSize: '1rem' }}>Procesar y Guardar Pedido</button>
            </form>
        )}

        {/* ... MODAL CANCELACIÓN (Se mantiene igual) ... */}
        {cancelingOrderId && (
            <div style={{ background: '#fff0f0', padding: '1.2rem', borderRadius: '6px', border: '1px solid #ffccd2', marginBottom: '1.5rem' }}>
              <form onSubmit={handleCancelOrderSubmit}>
                <h5 style={{ margin: '0 0 0.5rem 0', color: '#c62828' }}>Motivo de Cancelación Obligatorio</h5>
                <input type="text" placeholder="Escribe la razón..." value={cancelNotes} onChange={e => setCancelNotes(e.target.value)} required />
                <div style={{ display: 'flex', gap: '0.5rem', marginTop: '0.5rem' }}>
                  <button type="submit" style={{ background: '#d32f2f', width: 'auto', margin: 0 }}>Confirmar Cancelación</button>
                  <button type="button" onClick={() => setCancelingOrderId(null)} style={{ background: '#666', width: 'auto', margin: 0 }}>Volver</button>
                </div>
              </form>
            </div>
        )}

        <h3>Registro de Órdenes</h3>
        <table>
          <thead>
          <tr>
            <th>ID Pedido</th>
            <th>Cliente</th>
            <th>Fecha</th>
            <th>Total Facturado</th>
            <th>Estado Actual</th>
            <th>Acciones de Flujo</th>
          </tr>
          </thead>
          <tbody>
          {filteredOrders.length === 0 ? (
              <tr><td colSpan="6">No se encontraron pedidos.</td></tr>
          ) : (
              // Usamos el arreglo filtrado
              filteredOrders.map(o => (
                  <tr key={o.id}>
                    <td>
                      <code style={{ background: '#f1f5f9', padding: '2px 4px', borderRadius: '4px' }}>
                        <small style={{ color: '#475569' }}>{o.id?.substring(0, 8)}...</small>
                      </code>
                    </td>
                    <td><strong>{o.customerFullName || 'Cliente General'}</strong></td>
                    <td>{o.createdAt ? new Date(o.createdAt).toLocaleDateString() : 'Reciente'}</td>
                    <td style={{ fontWeight: 'bold', color: '#334155' }}>${o.total?.toLocaleString()} COP</td>
                    <td>
                  <span style={{
                    background: o.status === 'DELIVERED' ? '#e8f5e9' : o.status === 'CANCELLED' ? '#ffebee' : o.status === 'CREATED' ? '#fff3e0' : '#e1f5fe',
                    color: o.status === 'DELIVERED' ? '#2e7d32' : o.status === 'CANCELLED' ? '#c62828' : o.status === 'CREATED' ? '#ef6c00' : '#0288d1',
                    padding: '4px 8px', borderRadius: '4px', fontWeight: 'bold', fontSize: '0.85rem'
                  }}>
                    {o.status}
                  </span>
                    </td>
                    <td>
                      <div style={{ display: 'flex', gap: '0.3rem', margin: 0 }}>
                        {o.status === 'CREATED' && (
                            <>
                              <button onClick={() => handleUpdateStatus(o.id, 'pay')} style={{ background: '#34c759', fontSize: '0.8rem', padding: '4px 8px', margin: 0, width: 'auto' }}>💳 Pagar</button>
                              <button onClick={() => setCancelingOrderId(o.id)} style={{ background: '#ff3b30', fontSize: '0.8rem', padding: '4px 8px', margin: 0, width: 'auto' }}>🚫 Cancelar</button>
                            </>
                        )}
                        {o.status === 'PAID' && (
                            <>
                              <button onClick={() => handleUpdateStatus(o.id, 'ship')} style={{ background: '#0076ff', fontSize: '0.8rem', padding: '4px 8px', margin: 0, width: 'auto' }}>🚚 Despachar</button>
                              <button onClick={() => setCancelingOrderId(o.id)} style={{ background: '#ff3b30', fontSize: '0.8rem', padding: '4px 8px', margin: 0, width: 'auto' }}>🚫 Cancelar</button>
                            </>
                        )}
                        {o.status === 'SHIPPED' && (
                            <button onClick={() => handleUpdateStatus(o.id, 'deliver')} style={{ background: '#5856d6', fontSize: '0.8rem', padding: '4px 8px', margin: 0, width: 'auto' }}>🎁 Entregar</button>
                        )}
                        {['DELIVERED', 'CANCELLED'].includes(o.status) && (
                            <small style={{ color: '#aaa', fontStyle: 'italic' }}>Flujo Terminado</small>
                        )}
                      </div>
                    </td>
                  </tr>
              ))
          )}
          </tbody>
        </table>
      </div>
  );
}