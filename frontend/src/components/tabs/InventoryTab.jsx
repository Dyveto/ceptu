import React, { useState, useEffect } from 'react';
import { apiFetch } from '../../api/apiClient.js';
import { useInventory } from '../../context/InventoryContext.jsx';

export default function InventoryTab() {
  const {
    inventoryState,
    setLoading, setError, setProducts, setCategories,
    setFormSuccess, setFormError, clearFormMessages,
    toggleCatForm, toggleProdForm,
    setEditingProduct, setEditingProductDetails, closeAllForms
  } = useInventory();

  const {
    products, categories, loading, error,
    formSuccess, formError,
    showCatForm, showProdForm, editingProduct, editingProductDetails
  } = inventoryState;

  const [catName, setCatName] = useState('');
  const [catDescription, setCatDescription] = useState('');
  const [prodName, setProdName] = useState('');
  const [prodSku, setProdSku] = useState('');
  const [prodPrice, setProdPrice] = useState('');
  const [prodInitialStock, setProdInitialStock] = useState('');
  const [prodMinimumStock, setProdMinimumStock] = useState('');
  const [prodCategoryId, setProdCategoryId] = useState('');
  const [availableStock, setAvailableStock] = useState('');
  const [minimumStock, setMinimumStock] = useState('');
  const [editName, setEditName] = useState('');
  const [editSku, setEditSku] = useState('');
  const [editPrice, setEditPrice] = useState('');
  const [editCategoryId, setEditCategoryId] = useState('');
  const [editActive, setEditActive] = useState(true);

  // ── ESTADO PARA EL FILTRO ──
  const [searchTerm, setSearchTerm] = useState('');

  const loadInventoryData = async () => {
    setLoading(true); setError('');
    try {
      const [prodRes, catRes] = await Promise.all([
        apiFetch('/products', { method: 'GET' }),
        apiFetch('/categories', { method: 'GET' })
      ]);
      setProducts(prodRes || []);
      setCategories(catRes || []);
    } catch (err) {
      setError(err.message || 'Error al cargar el inventario');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { loadInventoryData(); }, []);

  const handleCreateCategory = async (e) => {
    e.preventDefault(); clearFormMessages();
    try {
      await apiFetch('/categories', { method: 'POST', body: { name: catName, description: catDescription } });
      setFormSuccess('¡Categoría creada con éxito!');
      setCatName(''); setCatDescription('');
      toggleCatForm();
      loadInventoryData();
    } catch (err) { setFormError(err.message || 'Error al crear la categoría'); }
  };

  const handleCreateProduct = async (e) => {
    e.preventDefault(); clearFormMessages();
    if (!prodCategoryId) return setFormError('Selecciona una categoría.');
    try {
      await apiFetch('/products', {
        method: 'POST',
        body: {
          categoryId: prodCategoryId, name: prodName, sku: prodSku,
          price: parseFloat(prodPrice), initialStock: parseInt(prodInitialStock),
          minimumStock: parseInt(prodMinimumStock)
        }
      });
      setFormSuccess('¡Producto registrado con éxito!');
      setProdName(''); setProdSku(''); setProdPrice(''); setProdInitialStock(''); setProdMinimumStock('');
      toggleProdForm();
      loadInventoryData();
    } catch (err) { setFormError(err.message || 'Error al crear el producto'); }
  };

  const handleUpdateInventory = async (e) => {
    e.preventDefault(); clearFormMessages();
    try {
      await apiFetch(`/products/${editingProduct.id}/inventory`, {
        method: 'PUT',
        body: { availableStock: parseInt(availableStock), minimumStock: parseInt(minimumStock) }
      });
      setFormSuccess(`¡Inventario de "${editingProduct.name}" actualizado correctamente!`);
      closeAllForms();
      setAvailableStock(''); setMinimumStock('');
      loadInventoryData();
    } catch (err) { setFormError(err.message || 'Error al actualizar el stock'); }
  };

  const handleUpdateProductDetails = async (e) => {
    e.preventDefault(); clearFormMessages();
    if (!editCategoryId) return setFormError('Selecciona una categoría válida.');
    try {
      await apiFetch(`/products/${editingProductDetails.id}`, {
        method: 'PUT',
        body: { name: editName, sku: editSku, price: parseFloat(editPrice), categoryId: editCategoryId, active: editActive }
      });
      setFormSuccess(`¡Datos del producto "${editName}" modificados con éxito!`);
      closeAllForms();
      loadInventoryData();
    } catch (err) { setFormError(err.message || 'Error al actualizar los datos del producto'); }
  };

  const startInventoryEdit = (product) => {
    setEditingProduct(product);
    setAvailableStock(product.inventory?.availableStock ?? product.availableStock ?? 0);
    setMinimumStock(product.inventory?.minimumStock ?? product.minimumStock ?? 0);
    clearFormMessages();
  };

  const startProductDetailsEdit = (product) => {
    setEditingProductDetails(product);
    setEditName(product.name); setEditSku(product.sku); setEditPrice(product.price || '');
    setEditCategoryId(product.category?.id || ''); setEditActive(product.active ?? true);
    clearFormMessages();
  };

  // ── LÓGICA DEL FILTRO ──
  const filteredProducts = products.filter(p =>
      p.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      p.sku.toLowerCase().includes(searchTerm.toLowerCase()) ||
      p.category?.name.toLowerCase().includes(searchTerm.toLowerCase())
  );

  if (loading && products.length === 0) return <p style={{ textAlign: 'center' }}>Cargando inventario...</p>;

  return (
      <div>
        {error && <p style={{ color: 'red', background: '#ffebeb', padding: '1rem', borderRadius: '4px' }}>⚠️ {error}</p>}
        {(formSuccess || formError) && (
            <div style={{ padding: '1rem', marginBottom: '1rem', borderRadius: '4px', background: formSuccess ? '#e8f5e9' : '#ffebee', color: formSuccess ? '#2e7d32' : '#c62828', fontWeight: 'bold' }}>
              {formSuccess || formError}
            </div>
        )}

        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
          <div style={{ display: 'flex', gap: '0.5rem' }}>
            <button onClick={toggleCatForm} style={{ background: showCatForm ? '#666' : '#34c759', width: 'auto', padding: '0.5rem 1rem' }}>
              {showCatForm ? '❌ Cancelar' : '🏷️ Nueva Categoría'}
            </button>
            <button onClick={toggleProdForm} style={{ background: showProdForm ? '#666' : '#0076ff', width: 'auto', padding: '0.5rem 1rem' }}>
              {showProdForm ? '❌ Cancelar' : '📦 Registrar Producto'}
            </button>
          </div>

          {/* BUSCADOR DE INVENTARIO */}
          <input
              type="text"
              placeholder="🔍 Buscar producto, SKU o categoría..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              style={{ padding: '0.5rem', width: '300px', borderRadius: '4px', border: '1px solid #ccc' }}
          />
        </div>

        {/* ... FORMULARIOS (Se mantienen igual) ... */}
        {(showCatForm || showProdForm || editingProduct || editingProductDetails) && (
            <div style={{ background: '#f9f9fb', padding: '1.5rem', borderRadius: '8px', border: '1px solid #eee', marginBottom: '2rem' }}>
              {showCatForm && (
                  <form onSubmit={handleCreateCategory} style={{ maxWidth: '500px' }}>
                    <h4 style={{ margin: '0 0 1rem 0' }}>Crear Categoría de Producto</h4>
                    <input type="text" placeholder="Nombre" value={catName} onChange={e => setCatName(e.target.value)} required />
                    <input type="text" placeholder="Descripción corta" value={catDescription} onChange={e => setCatDescription(e.target.value)} required />
                    <button type="submit" style={{ background: '#34c759', marginTop: '0.5rem' }}>Guardar Categoría</button>
                  </form>
              )}
              {showProdForm && (
                  <form onSubmit={handleCreateProduct}>
                    <h4 style={{ margin: '0 0 1rem 0' }}>Registrar Nuevo Producto</h4>
                    <div style={{ display: 'flex', gap: '0.5rem' }}>
                      <input type="text" placeholder="Nombre del artículo" value={prodName} onChange={e => setProdName(e.target.value)} required />
                      <input type="text" placeholder="SKU" value={prodSku} onChange={e => setProdSku(e.target.value)} required />
                    </div>
                    <div style={{ display: 'flex', gap: '0.5rem' }}>
                      <input type="number" placeholder="Precio Base" value={prodPrice} onChange={e => setProdPrice(e.target.value)} required />
                      <input type="number" placeholder="Stock Inicial" value={prodInitialStock} onChange={e => setProdInitialStock(e.target.value)} required />
                      <input type="number" placeholder="Alerta Mínima" value={prodMinimumStock} onChange={e => setProdMinimumStock(e.target.value)} required />
                    </div>
                    <select value={prodCategoryId} onChange={e => setProdCategoryId(e.target.value)} style={{ display: 'block', width: '100%', margin: '0.75rem 0', padding: '0.5rem', borderRadius: '4px', border: '1px solid #ccc' }} required>
                      <option value="">-- Selecciona la Categoría --</option>
                      {categories.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
                    </select>
                    <button type="submit" style={{ marginTop: '0.5rem' }}>Guardar Producto</button>
                  </form>
              )}
              {editingProduct && (
                  <form onSubmit={handleUpdateInventory} style={{ maxWidth: '600px' }}>
                    <h4 style={{ margin: '0 0 0.5rem 0', color: '#854d0e' }}>🔄 Ajuste de Stock: {editingProduct.name}</h4>
                    <div style={{ display: 'flex', gap: '1rem', alignItems: 'flex-end' }}>
                      <div><label style={{ fontSize: '0.8rem', display: 'block' }}>Stock Disponible:</label><input type="number" min="0" value={availableStock} onChange={e => setAvailableStock(e.target.value)} required /></div>
                      <div><label style={{ fontSize: '0.8rem', display: 'block' }}>Alerta Mínima:</label><input type="number" min="0" value={minimumStock} onChange={e => setMinimumStock(e.target.value)} required /></div>
                      <button type="submit" style={{ background: '#ca8a04', color: '#fff' }}>💾 Guardar</button>
                      <button type="button" onClick={closeAllForms} style={{ background: 'transparent', color: '#666' }}>Cancelar</button>
                    </div>
                  </form>
              )}
              {editingProductDetails && (
                  <form onSubmit={handleUpdateProductDetails}>
                    <h4 style={{ margin: '0 0 0.5rem 0', color: '#0369a1' }}>✏️ Editar: {editingProductDetails.name}</h4>
                    <div style={{ display: 'flex', gap: '0.5rem' }}>
                      <div style={{ flex: 1 }}><input type="text" value={editName} onChange={e => setEditName(e.target.value)} required /></div>
                      <div style={{ flex: 1 }}><input type="text" value={editSku} onChange={e => setEditSku(e.target.value)} required /></div>
                    </div>
                    <div style={{ display: 'flex', gap: '0.5rem', marginTop: '0.5rem' }}>
                      <div style={{ flex: 1 }}><input type="number" step="0.01" value={editPrice} onChange={e => setEditPrice(e.target.value)} required /></div>
                      <div style={{ flex: 1 }}>
                        <select value={editActive} onChange={e => setEditActive(e.target.value === 'true')} style={{ padding: '0.5rem', width: '100%' }}>
                          <option value="true">Activo / Visible</option>
                          <option value="false">Inactivo / Oculto</option>
                        </select>
                      </div>
                    </div>
                    <select value={editCategoryId} onChange={e => setEditCategoryId(e.target.value)} style={{ width: '100%', padding: '0.5rem', marginTop: '0.5rem' }} required>
                      <option value="">-- Elige la Categoría --</option>
                      {categories.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
                    </select>
                    <div style={{ display: 'flex', gap: '0.5rem', marginTop: '1.5rem' }}>
                      <button type="submit" style={{ background: '#f39c12', width: 'auto', margin: 0 }}>Guardar Cambios</button>
                      <button type="button" onClick={closeAllForms} style={{ background: '#666', width: 'auto', margin: 0 }}>Cancelar</button>
                    </div>
                  </form>
              )}
            </div>
        )}

        <h3>Catálogo e Inventario</h3>
        <table>
          <thead>
          <tr>
            <th>SKU</th><th>Nombre del Producto</th><th>Categoría</th><th>Precio</th><th>Stock Actual</th><th>Acciones Colectoras</th>
          </tr>
          </thead>
          <tbody>
          {filteredProducts.length === 0 ? (
              <tr><td colSpan="6">No se encontraron productos.</td></tr>
          ) : (
              // Mapeamos el arreglo filtrado
              filteredProducts.map(p => {
                const currentStock = p.inventory ? p.inventory.availableStock : (p.availableStock ?? p.initialStock ?? 0);
                const minStock = p.inventory ? p.inventory.minimumStock : (p.minimumStock || 0);
                return (
                    <tr key={p.id}>
                      <td><code style={{ background: '#eee', padding: '2px 4px', borderRadius: '3px' }}>{p.sku || 'N/A'}</code></td>
                      <td>
                        <strong>{p.name}</strong>
                        {!p.active && <span style={{ marginLeft: '6px', background: '#e11d48', color: '#fff', padding: '1px 5px', borderRadius: '3px', fontSize: '0.7rem' }}>INACTIVO</span>}
                      </td>
                      <td style={{ color: '#555', fontSize: '0.9rem' }}>{p.category?.name || 'General'}</td>
                      <td>${p.price?.toLocaleString()}</td>
                      <td>
                    <span style={{ fontWeight: 'bold', color: currentStock <= minStock ? 'red' : 'inherit' }}>
                      {currentStock} unds
                    </span>
                      </td>
                      <td style={{ display: 'flex', gap: '0.5rem' }}>
                        <button onClick={() => startProductDetailsEdit(p)} style={{ width: 'auto', padding: '4px 10px', fontSize: '0.85rem', margin: 0, background: '#f1f5f9', color: '#334155', border: '1px solid #cbd5e1' }}>✏️ Editar</button>
                        <button onClick={() => startInventoryEdit(p)} style={{ width: 'auto', padding: '4px 10px', fontSize: '0.85rem', margin: 0, background: '#3b82f6' }}>🔄 Stock</button>
                      </td>
                    </tr>
                );
              })
          )}
          </tbody>
        </table>
      </div>
  );
}