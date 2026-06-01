import React, { useState, useEffect } from 'react';
import { apiFetch } from '../../api/apiClient.js';
import { useCustomer } from '../../context/CustomerContext.jsx';

export default function CustomersTab() {
  const {
    customerState,
    setLoading, setError, setCustomers,
    setFormSuccess, setFormError, clearFormMessages,
    toggleCustomerForm, setEditingCustomer, closeEditingCustomer,
    setSelectedCustomer, closeAddressPanel,
    setAddresses, setLoadingAddresses, toggleAddressForm
  } = useCustomer();

  const {
    customers, loading, error,
    formSuccess, formError,
    showCustomerForm, editingCustomer,
    selectedCustomer, addresses, loadingAddresses, showAddressForm
  } = customerState;

  const [custFirstName, setCustFirstName] = useState('');
  const [custLastName, setCustLastName] = useState('');
  const [custEmail, setCustEmail] = useState('');
  const [custPhone, setCustPhone] = useState('');
  const [editFirstName, setEditFirstName] = useState('');
  const [editLastName, setEditLastName] = useState('');
  const [editEmail, setEditEmail] = useState('');
  const [editPhone, setEditPhone] = useState('');
  const [editStatus, setEditStatus] = useState('ACTIVE');
  const [street, setStreet] = useState('');
  const [city, setCity] = useState('');
  const [stateName, setStateName] = useState('');
  const [zipCode, setZipCode] = useState('');
  const [country, setCountry] = useState('');

  // ── ESTADO PARA EL FILTRO ──
  const [searchTerm, setSearchTerm] = useState('');

  const fetchCustomers = async () => {
    setLoading(true); setError('');
    try {
      const res = await apiFetch('/customers', { method: 'GET' });
      setCustomers(res || []);
    } catch (err) { setError(err.message || 'Error al cargar clientes desde la API'); } finally { setLoading(false); }
  };

  useEffect(() => { fetchCustomers(); }, []);

  const handleSelectCustomer = async (customer) => {
    if (selectedCustomer?.id === customer.id) { closeAddressPanel(); return; }
    setSelectedCustomer(customer); setLoadingAddresses(true); clearFormMessages();
    try {
      const res = await apiFetch(`/customers/${customer.id}/addresses`, { method: 'GET' });
      setAddresses(res || []);
    } catch (err) { setFormError('No se pudieron recuperar las direcciones.'); } finally { setLoadingAddresses(false); }
  };

  const handleCreateCustomer = async (e) => {
    e.preventDefault(); clearFormMessages();
    try {
      await apiFetch('/customers', {
        method: 'POST',
        body: { firstName: custFirstName, lastName: custLastName, email: custEmail, phone: custPhone }
      });
      setFormSuccess('¡Cliente registrado con éxito en Ceptu!');
      setCustFirstName(''); setCustLastName(''); setCustEmail(''); setCustPhone('');
      toggleCustomerForm(); fetchCustomers();
    } catch (err) { setFormError(err.message || 'Error al registrar el cliente'); }
  };

  const handleUpdateCustomer = async (e) => {
    e.preventDefault(); clearFormMessages();
    try {
      const updatedData = await apiFetch(`/customers/${editingCustomer.id}`, {
        method: 'PUT',
        body: { firstName: editFirstName, lastName: editLastName, email: editEmail, phone: editPhone, status: editStatus }
      });
      setFormSuccess(`¡El perfil fue actualizado con éxito!`);
      if (selectedCustomer?.id === editingCustomer.id) {
        setSelectedCustomer(updatedData || { ...selectedCustomer, firstName: editFirstName, lastName: editLastName, status: editStatus });
      }
      closeEditingCustomer(); fetchCustomers();
    } catch (err) { setFormError(err.message || 'Error al modificar el perfil'); }
  };

  const handleCreateAddress = async (e) => {
    e.preventDefault(); clearFormMessages();
    try {
      await apiFetch(`/customers/${selectedCustomer.id}/addresses`, {
        method: 'POST',
        body: { street, city, state: stateName, zipCode, country }
      });
      setFormSuccess('¡Dirección asociada con éxito!');
      setStreet(''); setCity(''); setStateName(''); setZipCode(''); setCountry('');
      toggleAddressForm();
      const updatedAddresses = await apiFetch(`/customers/${selectedCustomer.id}/addresses`, { method: 'GET' });
      setAddresses(updatedAddresses || []);
    } catch (err) { setFormError(err.message || 'Error al registrar la dirección'); }
  };

  const startCustomerEdit = (customer) => {
    setEditingCustomer(customer);
    setEditFirstName(customer.firstName); setEditLastName(customer.lastName);
    setEditEmail(customer.email); setEditPhone(customer.phone || ''); setEditStatus(customer.status || 'ACTIVE');
    clearFormMessages();
  };

  // ── LÓGICA DEL FILTRO ──
  const filteredCustomers = customers.filter(c =>
      (c.firstName + ' ' + c.lastName).toLowerCase().includes(searchTerm.toLowerCase()) ||
      c.email.toLowerCase().includes(searchTerm.toLowerCase()) ||
      (c.phone && c.phone.includes(searchTerm))
  );

  if (loading && customers.length === 0) return <p style={{ textAlign: 'center' }}>Consultando base de datos de clientes...</p>;

  return (
      <div style={{ display: 'flex', gap: '2rem', alignItems: 'flex-start' }}>
        <div style={{ flex: 2 }}>

          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
            <button onClick={() => { toggleCustomerForm(); closeEditingCustomer(); }} style={{ width: 'auto', padding: '0.5rem 1rem', background: showCustomerForm ? '#666' : '#28a745', margin: 0 }}>
              {showCustomerForm ? '❌ Cancelar' : '➕ Nuevo Cliente'}
            </button>

            {/* BUSCADOR DE CLIENTES */}
            <input
                type="text"
                placeholder="🔍 Buscar cliente por nombre, email o teléfono..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                style={{ padding: '0.5rem', width: '300px', borderRadius: '4px', border: '1px solid #ccc' }}
            />
          </div>

          {error && <p style={{ color: 'red' }}>⚠️ {error}</p>}
          {(formSuccess || formError) && (
              <div style={{ padding: '1rem', marginBottom: '1rem', borderRadius: '4px', background: formSuccess ? '#e8f5e9' : '#ffebee', color: formSuccess ? '#2e7d32' : '#c62828', fontWeight: 'bold' }}>
                {formSuccess || formError}
              </div>
          )}

          {/* ... FORMULARIOS ... */}
          {showCustomerForm && (
              <form onSubmit={handleCreateCustomer} style={{ background: '#f8f9fa', padding: '1.2rem', borderRadius: '6px', border: '1px solid #e9ecef', marginBottom: '1.5rem' }}>
                <h4 style={{ margin: '0 0 1rem 0' }}>Registrar Nuevo Cliente</h4>
                <div style={{ display: 'flex', gap: '0.5rem' }}>
                  <input type="text" placeholder="Nombre" value={custFirstName} onChange={e => setCustFirstName(e.target.value)} required />
                  <input type="text" placeholder="Apellido" value={custLastName} onChange={e => setCustLastName(e.target.value)} required />
                </div>
                <div style={{ display: 'flex', gap: '0.5rem' }}>
                  <input type="email" placeholder="Correo Electrónico" value={custEmail} onChange={e => setCustEmail(e.target.value)} required />
                  <input type="text" placeholder="Teléfono" value={custPhone} onChange={e => setCustPhone(e.target.value)} required />
                </div>
                <button type="submit" style={{ width: '100%', marginTop: '0.5rem', background: '#28a745' }}>Guardar Perfil</button>
              </form>
          )}

          {editingCustomer && (
              <form onSubmit={handleUpdateCustomer} style={{ background: '#fff3cd', padding: '1.2rem', borderRadius: '6px', border: '1px solid #ffeeba', marginBottom: '1.5rem' }}>
                <h4 style={{ margin: '0 0 1rem 0', color: '#856404' }}>✏️ Modificar Maestro de Cliente</h4>
                <div style={{ display: 'flex', gap: '0.5rem' }}>
                  <input type="text" value={editFirstName} onChange={e => setEditFirstName(e.target.value)} required />
                  <input type="text" value={editLastName} onChange={e => setEditLastName(e.target.value)} required />
                </div>
                <div style={{ display: 'flex', gap: '0.5rem' }}>
                  <input type="email" value={editEmail} onChange={e => setEditEmail(e.target.value)} required />
                  <input type="text" value={editPhone} onChange={e => setEditPhone(e.target.value)} required />
                </div>
                <div style={{ display: 'flex', gap: '0.5rem', marginTop: '0.5rem' }}>
                  <select value={editStatus} onChange={e => setEditStatus(e.target.value)} style={{ padding: '0.5rem', flex: 1 }} required>
                    <option value="ACTIVE">Activo</option><option value="INACTIVE">Inactivo</option>
                  </select>
                  <button type="submit" style={{ background: '#e67e22', margin: 0, flex: 1.5 }}>Actualizar</button>
                  <button type="button" onClick={closeEditingCustomer} style={{ background: '#666', margin: 0, flex: 0.5 }}>Cerrar</button>
                </div>
              </form>
          )}

          <table>
            <thead>
            <tr>
              <th>Nombre Completo</th><th>Email</th><th>Estado</th><th>Acciones Colectoras</th>
            </tr>
            </thead>
            <tbody>
            {filteredCustomers.length === 0 ? (
                <tr><td colSpan="4">No se encontraron clientes.</td></tr>
            ) : (
                // Se mapea el arreglo filtrado
                filteredCustomers.map(c => (
                    <tr key={c.id} style={{ background: selectedCustomer?.id === c.id ? '#f0f7ff' : 'transparent', transition: 'background 0.2s' }}>
                      <td><strong>{c.firstName} {c.lastName}</strong></td>
                      <td>{c.email}</td>
                      <td>
                    <span style={{ background: c.status === 'ACTIVE' ? '#e8f5e9' : '#ffebee', color: c.status === 'ACTIVE' ? '#2e7d32' : '#c62828', padding: '3px 8px', borderRadius: '12px', fontSize: '0.85rem', fontWeight: 'bold' }}>
                      {c.status || 'ACTIVE'}
                    </span>
                      </td>
                      <td style={{ display: 'flex', gap: '0.4rem' }}>
                        <button onClick={() => handleSelectCustomer(c)} style={{ width: 'auto', padding: '4px 10px', fontSize: '0.85rem', margin: 0, background: selectedCustomer?.id === c.id ? '#e11d48' : '#0076ff' }}>
                          {selectedCustomer?.id === c.id ? '❌ Ocultar' : '📍 Ubicaciones'}
                        </button>
                        <button onClick={() => startCustomerEdit(c)} style={{ width: 'auto', padding: '4px 10px', fontSize: '0.85rem', margin: 0, background: '#f1f5f9', color: '#334155', border: '1px solid #cbd5e1' }}>
                          ✏️ Editar
                        </button>
                      </td>
                    </tr>
                ))
            )}
            </tbody>
          </table>
        </div>

        {/* ... PANEL LATERAL DE DIRECCIONES ... */}
        {selectedCustomer && (
            <div style={{ flex: 1, background: '#f9f9fb', padding: '1.5rem', borderRadius: '8px', border: '1px solid #eee' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '1rem', borderBottom: '1px dashed #ddd', paddingBottom: '0.75rem' }}>
                <h4 style={{ margin: 0 }}>Direcciones de: <br /><span style={{ color: '#0076ff' }}>{selectedCustomer.firstName} {selectedCustomer.lastName}</span></h4>
                <button onClick={closeAddressPanel} style={{ width: 'auto', padding: '2px 8px', background: '#475569', margin: 0, fontSize: '0.75rem', textTransform: 'uppercase' }}>❌ Cerrar</button>
              </div>

              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
                <span style={{ fontSize: '0.85rem', color: '#666', fontWeight: 'bold' }}>Ubicaciones de Entrega</span>
                <button onClick={toggleAddressForm} style={{ width: 'auto', padding: '0.4rem 0.8rem', background: showAddressForm ? '#666' : '#0076ff', margin: 0, fontSize: '0.8rem' }}>
                  {showAddressForm ? '🚫 Cancelar' : '➕ Añadir'}
                </button>
              </div>

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

              {loadingAddresses ? (
                  <p style={{ color: '#666', fontSize: '0.9rem' }}>Consultando llaves foráneas...</p>
              ) : addresses.length === 0 ? (
                  <p style={{ color: '#888', fontStyle: 'italic', fontSize: '0.9rem' }}>Este cliente no tiene ubicaciones registradas.</p>
              ) : (
                  <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
                    {addresses.map(addr => (
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