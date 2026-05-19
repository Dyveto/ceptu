import React, { useState, useEffect } from 'react';
import { apiFetch } from '../../api/apiClient.js';

export default function CustomersTab() {
  const [customers, setCustomers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  // Controladores de visibilidad para formularios
  const [showCustomerForm, setShowCustomerForm] = useState(false);
  const [showAddressForm, setShowAddressForm] = useState(false);

  // Estados para la gestión de direcciones del cliente seleccionado
  const [selectedCustomer, setSelectedCustomer] = useState(null);
  const [addresses, setAddresses] = useState([]);
  const [loadingAddresses, setLoadingAddresses] = useState(false);

  // Estados para el formulario de NUEVO CLIENTE (CreateCustomerRequest)
  const [custFirstName, setCustFirstName] = useState('');
  const [custLastName, setCustLastName] = useState('');
  const [custEmail, setCustEmail] = useState('');
  const [custPhone, setCustPhone] = useState('');

  // Estados para el formulario de DIRECCIÓN (CreateAddressRequest)
  const [street, setStreet] = useState('');
  const [city, setCity] = useState('');
  const [stateName, setStateName] = useState('');
  const [zipCode, setZipCode] = useState('');
  const [country, setCountry] = useState('');

  const [formSuccess, setFormSuccess] = useState('');
  const [formError, setFormError] = useState('');

  // Función para cargar/refrescar la lista de clientes
  const fetchCustomers = async () => {
    setLoading(true); setError('');
    try {
      const res = await apiFetch('/customers', { method: 'GET' });
      setCustomers(res || []);
    } catch (err) {
      setError(err.message || 'Error al cargar clientes desde la API');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCustomers();
  }, []);

  // Cargar direcciones cuando se selecciona un cliente
  const handleSelectCustomer = async (customer) => {
    setSelectedCustomer(customer);
    setAddresses([]);
    setShowAddressForm(false);
    setFormSuccess('');
    setFormError('');
    setLoadingAddresses(true);

    try {
      const res = await apiFetch(`/customers/${customer.id}/addresses`, { method: 'GET' });
      setAddresses(res || []);
    } catch (err) {
      setFormError('No se pudieron recuperar las direcciones de este cliente.');
    } finally {
      setLoadingAddresses(false);
    }
  };

  /* ==========================================================================
     MANEJADORES DE ENVÍO (POST)
     ========================================================================== */
  
  // Guardar nuevo cliente (POST /api/customers)
  const handleCreateCustomer = async (e) => {
    e.preventDefault();
    setFormError(''); setFormSuccess('');

    try {
      await apiFetch('/customers', {
        method: 'POST',
        body: { 
          firstName: custFirstName, 
          lastName: custLastName, 
          email: custEmail, 
          phone: custPhone 
        }
      });

      setFormSuccess('¡Cliente registrado con éxito en Ceptu!');
      // Limpiar campos y colapsar formulario
      setCustFirstName(''); setCustLastName(''); setCustEmail(''); setCustPhone('');
      setShowCustomerForm(false);
      
      // Refrescar la tabla
      fetchCustomers();
    } catch (err) {
      setFormError(err.message || 'Error al registrar el cliente');
    }
  };

  // Guardar nueva dirección (POST /api/customers/{id}/addresses)
  const handleCreateAddress = async (e) => {
    e.preventDefault();
    setFormError(''); setFormSuccess('');

    try {
      await apiFetch(`/customers/${selectedCustomer.id}/addresses`, {
        method: 'POST',
        body: { street, city, state: stateName, zipCode, country }
      });

      setFormSuccess('¡Dirección asociada con éxito!');
      setStreet(''); setCity(''); setStateName(''); setZipCode(''); setCountry('');
      setShowAddressForm(false);

      // Refrescar direcciones del cliente actual
      const updatedAddresses = await apiFetch(`/customers/${selectedCustomer.id}/addresses`, { method: 'GET' });
      setAddresses(updatedAddresses || []);
    } catch (err) {
      setFormError(err.message || 'Error al registrar la dirección');
    }
  };

  if (loading && customers.length === 0) return <p style={{ textAlign: 'center' }}>Consultando base de datos de clientes...</p>;

  return (
    <div style={{ display: 'flex', gap: '2rem', alignItems: 'flex-start' }}>
      
      {/* SECCIÓN IZQUIERDA: GESTIÓN Y TABLA DE CLIENTES */}
      <div style={{ flex: 1.3 }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
          <h3 style={{ margin: 0 }}>Gestión de Clientes</h3>
          <button 
            onClick={() => { setShowCustomerForm(!showCustomerForm); setFormSuccess(''); setFormError(''); }}
            style={{ width: 'auto', padding: '0.5rem 1rem', background: showCustomerForm ? '#666' : '#28a745', margin: 0 }}
          >
            {showCustomerForm ? '❌ Cancelar' : '➕ Nuevo Cliente'}
          </button>
        </div>

        {error && <p style={{ color: 'red' }}>⚠️ {error}</p>}
        
        {/* Avisos generales de éxito o error en formularios */}
        {(formSuccess || formError) && (
          <div style={{ padding: '1rem', marginBottom: '1rem', borderRadius: '4px', background: formSuccess ? '#e8f5e9' : '#ffebee', color: formSuccess ? '#2e7d32' : '#c62828', fontWeight: 'bold' }}>
            {formSuccess || formError}
          </div>
        )}

        {/* Formulario Desplegable: Registrar Cliente */}
        {showCustomerForm && (
          <form onSubmit={handleCreateCustomer} style={{ background: '#f8f9fa', padding: '1.2rem', borderRadius: '6px', border: '1px solid #e9ecef', marginBottom: '1.5rem' }}>
            <h4 style={{ margin: '0 0 1rem 0' }}>Registrar Nuevo Cliente (HU-04)</h4>
            <div style={{ display: 'flex', gap: '0.5rem' }}>
              <input type="text" placeholder="Nombre" value={custFirstName} onChange={e => setCustFirstName(e.target.value)} required />
              <input type="text" placeholder="Apellido" value={custLastName} onChange={e => setCustLastName(e.target.value)} required />
            </div>
            <div style={{ display: 'flex', gap: '0.5rem' }}>
              <input type="email" placeholder="Correo Electrónico" value={custEmail} onChange={e => setCustEmail(e.target.value)} required />
              <input type="text" placeholder="Teléfono" value={custPhone} onChange={e => setCustPhone(e.target.value)} required />
            </div>
            <button type="submit" style={{ width: '100%', marginTop: '0.5rem', background: '#28a745' }}>Guardar Perfil de Cliente</button>
          </form>
        )}

        {/* Tabla Base de Clientes */}
        <table>
          <thead>
            <tr>
              <th>Nombre Completo</th>
              <th>Email</th>
              <th>Estado</th>
              <th>Acción</th>
            </tr>
          </thead>
          <tbody>
            {customers.length === 0 ? (
              <tr><td colSpan="4">No hay clientes registrados en el sistema.</td></tr>
            ) : (
              customers.map(c => (
                <tr 
                  key={c.id} 
                  style={{ 
                    background: selectedCustomer?.id === c.id ? '#f0f7ff' : 'transparent',
                    transition: 'background 0.2s' 
                  }}
                >
                  <td><strong>{c.firstName} {c.lastName}</strong></td>
                  <td>{c.email}</td>
                  <td>
                    <span style={{ background: c.status === 'ACTIVE' ? '#e8f5e9' : '#ffebee', color: c.status === 'ACTIVE' ? '#2e7d32' : '#c62828', padding: '3px 8px', borderRadius: '12px', fontSize: '0.85rem', fontWeight: 'bold' }}>
                      {c.status || 'ACTIVE'}
                    </span>
                  </td>
                  <td>
                    <button 
                      onClick={() => handleSelectCustomer(c)}
                      style={{ width: 'auto', padding: '4px 10px', fontSize: '0.85rem', margin: 0, background: '#0076ff' }}
                    >
                      📍 Ubicaciones
                    </button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {/* SECCIÓN DERECHA: DIRECCIONES DEL CLIENTE SELECCIONADO */}
      {selectedCustomer && (
        <div style={{ flex: 1, background: '#f9f9fb', padding: '1.5rem', borderRadius: '8px', border: '1px solid #eee' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
            <h4 style={{ margin: 0 }}>Direcciones de: <br/><span style={{ color: '#0076ff' }}>{selectedCustomer.firstName} {selectedCustomer.lastName}</span></h4>
            <button 
              onClick={() => { setShowAddressForm(!showAddressForm); setFormSuccess(''); setFormError(''); }}
              style={{ width: 'auto', padding: '0.5rem 1rem', background: showAddressForm ? '#666' : '#0076ff', margin: 0 }}
            >
              {showAddressForm ? '❌ Cerrar' : '➕ Añadir'}
            </button>
          </div>

          {/* Formulario Desplegable: Nueva Dirección (HU-05) */}
          {showAddressForm && (
            <form onSubmit={handleCreateAddress} style={{ marginBottom: '1.5rem', borderBottom: '1px solid #ddd', paddingBottom: '1.5rem' }}>
              <input type="text" placeholder="Dirección (ej: Carrera 15 # 22-10)" value={street} onChange={e => setStreet(e.target.value)} required />
              <div style={{ display: 'flex', gap: '0.5rem' }}>
                <input type="text" placeholder="Ciudad" value={city} onChange={e => setCity(e.target.value)} required />
                <input type="text" placeholder="Departamento" value={stateName} onChange={e => setStateName(e.target.value)} required />
              </div>
              <div style={{ display: 'flex', gap: '0.5rem' }}>
                <input type="text" placeholder="Cód. Postal" value={zipCode} onChange={e => setZipCode(e.target.value)} required />
                <input type="text" placeholder="País" value={country} onChange={e => setCountry(e.target.value)} required />
              </div>
              <button type="submit" style={{ width: '100%', marginTop: '0.5rem', background: '#0076ff' }}>Vincular Dirección</button>
            </form>
          )}

          {/* Listado asíncrono de direcciones */}
          {loadingAddresses ? (
            <p style={{ color: '#666', fontSize: '0.9rem' }}>Consultando llaves foráneas...</p>
          ) : addresses.length === 0 ? (
            <p style={{ color: '#888', fontStyle: 'italic', fontSize: '0.9rem' }}>Este cliente no tiene ubicaciones de entrega registradas.</p>
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
              {addresses.map((addr) => (
                <div key={addr.id} style={{ background: 'white', padding: '1rem', borderRadius: '6px', border: '1px solid #e2e8f0', fontSize: '0.9rem' }}>
                  <div style={{ fontWeight: 'bold', color: '#333' }}>🏠 {addr.street}</div>
                  <div style={{ color: '#666', marginTop: '2px' }}>{addr.city}, {addr.state} — {addr.zipCode}</div>
                  <div style={{ color: '#999', fontSize: '0.8rem', marginTop: '4px' }}>{addr.country}</div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

    </div>
  );
}