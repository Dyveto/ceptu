import React, { useState, useEffect } from 'react';
import { apiFetch } from '../../api/apiClient.js';

export default function InventoryTab() {
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const [showCatForm, setShowCatForm] = useState(false);
  const [showProdForm, setShowProdForm] = useState(false);
  const [editingProduct, setEditingProduct] = useState(null);
  const [editingProductDetails, setEditingProductDetails] = useState(null);

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

  const [formSuccess, setFormSuccess] = useState('');
  const [formError, setFormError] = useState('');

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

  useEffect(() => {
    loadInventoryData();
  }, []);

  const handleCreateCategory = async (e) => {
    e.preventDefault();
    setFormError(''); setFormSuccess('');
    try {
      await apiFetch('/categories', {
        method: 'POST',
        body: { name: catName, description: catDescription }
      });
      setFormSuccess('¡Categoría creada con éxito!');
      setCatName(''); setCatDescription('');
      setShowCatForm(false);
      loadInventoryData();
    } catch (err) {
      setFormError(err.message || 'Error al crear la categoría');
    }
  };

  const handleCreateProduct = async (e) => {
    e.preventDefault();
    setFormError(''); setFormSuccess('');
    if (!prodCategoryId) return setFormError('Selecciona una categoría.');

    try {
      await apiFetch('/products', {
        method: 'POST',
        body: {
          categoryId: prodCategoryId,
          name: prodName,
          sku: prodSku,
          price: parseFloat(prodPrice),
          initialStock: parseInt(prodInitialStock),
          minimumStock: parseInt(prodMinimumStock)
        }
      });
      setFormSuccess('¡Producto registrado con éxito!');
      setProdName(''); setProdSku(''); setProdPrice(''); setProdInitialStock(''); setProdMinimumStock('');
      setShowProdForm(false);
      loadInventoryData();
    } catch (err) {
      setFormError(err.message || 'Error al crear el producto');
    }
  };

  const handleUpdateInventory = async (e) => {
    e.preventDefault();
    setFormError(''); setFormSuccess('');
    try {
      await apiFetch(`/products/${editingProduct.id}/inventory`, {
        method: 'PUT',
        body: {
          availableStock: parseInt(availableStock),
          minimumStock: parseInt(minimumStock)
        }
      });
      setFormSuccess(`¡Inventario de "${editingProduct.name}" actualizado correctamente!`);
      setEditingProduct(null);
      setAvailableStock(''); setMinimumStock('');
      loadInventoryData();
    } catch (err) {
      setFormError(err.message || 'Error al actualizar el stock');
    }
  };

  const handleUpdateProductDetails = async (e) => {
    e.preventDefault();
    setFormError(''); setFormSuccess('');
    if (!editCategoryId) return setFormError('Selecciona una categoría válida.');

    try {
      await apiFetch(`/products/${editingProductDetails.id}`, {
        method: 'PUT',
        body: {
          name: editName,
          sku: editSku,
          price: parseFloat(editPrice),
          categoryId: editCategoryId,
          active: editActive
        }
      });
      setFormSuccess(`¡Datos del producto "${editName}" modificados con éxito!`);
      setEditingProductDetails(null);
      loadInventoryData();
    } catch (err) {
      setFormError(err.message || 'Error al actualizar los datos del producto');
    }
  };

  const startInventoryEdit = (product) => {
    setEditingProduct(product);
    setEditingProductDetails(null);
    setAvailableStock(product.inventory?.availableStock || product.availableStock || 0);
    setMinimumStock(product.inventory?.minimumStock || product.minimumStock || 0);
    setShowCatForm(false); setShowProdForm(false);
    setFormSuccess(''); setFormError('');
  };

  const startProductDetailsEdit = (product) => {
    setEditingProductDetails(product);
    setEditingProduct(null);
    setEditName(product.name);
    setEditSku(product.sku);
    setEditPrice(product.price || '');
    setEditCategoryId(product.category?.id || '');
    setEditActive(product.active ?? true);
    setShowCatForm(false); setShowProdForm(false);
    setFormSuccess(''); setFormError('');
  };

  if (loading && products.length === 0) return <p style={{ textAlign: 'center' }}>Cargando inventario de Ceptu...</p>;

  return (
    <div>
      {error && <p style={{ color: 'red', background: '#ffebeb', padding: '1rem', borderRadius: '4px' }}>⚠️ {error}</p>}
      
      {(formSuccess || formError) && (
        <div style={{ padding: '1rem', marginBottom: '1rem', borderRadius: '4px', background: formSuccess ? '#e8f5e9' : '#ffebee', color: formSuccess ? '#2e7d32' : '#c62828', fontWeight: 'bold' }}>
          {formSuccess || formError}
        </div>
      )}

      {/* ACCIONES DE CONTROL */}
      <div style={{ display: 'flex', gap: '1rem', marginBottom: '1.5rem' }}>
        <button onClick={() => { setShowCatForm(!showCatForm); setShowProdForm(false); setEditingProduct(null); setEditingProductDetails(null); }} style={{ background: showCatForm ? '#666' : '#34c759', width: 'auto', padding: '0.6rem 1.2rem' }}>
          {showCatForm ? '❌ Cancelar' : '➕ Nueva Categoría'}
        </button>
        <button onClick={() => { setShowProdForm(!showProdForm); setShowCatForm(false); setEditingProduct(null); setEditingProductDetails(null); }} style={{ background: showProdForm ? '#666' : '#0076ff', width: 'auto', padding: '0.6rem 1.2rem' }}>
          {showProdForm ? '❌ Cancelar' : '📦 Registrar Producto'}
        </button>
      </div>

      {/* COMPONENTE DE FORMULARIOS DESPLEGABLES */}
      {(showCatForm || showProdForm || editingProduct || editingProductDetails) && (
        <div style={{ background: '#f9f9fb', padding: '1.5rem', borderRadius: '8px', border: '1px solid #eee', marginBottom: '2rem' }}>
          
          {/* Formulario Categorías */}
          {showCatForm && (
            <form onSubmit={handleCreateCategory} style={{ maxWidth: '500px' }}>
              <h4 style={{ margin: '0 0 1rem 0' }}>Crear Categoría de Producto (HU-01)</h4>
              <input type="text" placeholder="Nombre (ej: Ropa Universitaria)" value={catName} onChange={e => setCatName(e.target.value)} required />
              <input type="text" placeholder="Descripción corta" value={catDescription} onChange={e => setCatDescription(e.target.value)} required />
              <button type="submit" style={{ background: '#34c759', marginTop: '0.5rem' }}>Guardar Categoría</button>
            </form>
          )}

          {/* Formulario Productos */}
          {showProdForm && (
            <form onSubmit={handleCreateProduct}>
              <h4 style={{ margin: '0 0 1rem 0' }}>Registrar Nuevo Producto (HU-02)</h4>
              <div style={{ display: 'flex', gap: '0.5rem' }}>
                <input type="text" placeholder="Nombre del artículo" value={prodName} onChange={e => setProdName(e.target.value)} required />
                <input type="text" placeholder="SKU (ej: SUD-001)" value={prodSku} onChange={e => setProdSku(e.target.value)} required />
              </div>
              <div style={{ display: 'flex', gap: '0.5rem' }}>
                <input type="number" placeholder="Precio Base" value={prodPrice} onChange={e => setProdPrice(e.target.value)} required />
                <input type="number" placeholder="Stock Inicial" value={prodInitialStock} onChange={e => setProdInitialStock(e.target.value)} required />
                <input type="number" placeholder="Alerta Mínima Stock" value={prodMinimumStock} onChange={e => setProdMinimumStock(e.target.value)} required />
              </div>
              <select value={prodCategoryId} onChange={e => setProdCategoryId(e.target.value)} style={{ display: 'block', width: '100%', margin: '0.75rem 0', padding: '0.5rem', borderRadius: '4px', border: '1px solid #ccc' }} required>
                <option value="">-- Selecciona la Categoría Correspondiente --</option>
                {categories.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
              </select>
              <button type="submit" style={{ marginTop: '0.5rem' }}>Guardar Producto en Inventario</button>
            </form>
          )}

          {/* Formulario Especial: Ajustar Existencias de Inventario (HU-03) */}
          {editingProduct && (
            <form onSubmit={handleUpdateInventory} style={{ maxWidth: '600px' }}>
              <h4 style={{ margin: '0 0 0.5rem 0' }}>🔄 Administrar Existencias (HU-03)</h4>
              <p style={{ margin: '0 0 1rem 0', color: '#666' }}>Modificando stock para: <strong style={{ color: '#0076ff' }}>{editingProduct.name}</strong></p>
              <div style={{ display: 'flex', gap: '1rem' }}>
                <div style={{ flex: 1 }}>
                  <label style={{ display: 'block', marginBottom: '0.3rem', fontSize: '0.85rem', fontWeight: 'bold' }}>Inventario Disponible</label>
                  <input type="number" min="0" placeholder="Stock Disponible" value={availableStock} onChange={e => setAvailableStock(e.target.value)} required />
                </div>
                <div style={{ flex: 1 }}>
                  <label style={{ display: 'block', marginBottom: '0.3rem', fontSize: '0.85rem', fontWeight: 'bold' }}>Alerta de Stock Mínimo</label>
                  <input type="number" min="0" placeholder="Mínimo de seguridad" value={minimumStock} onChange={e => setMinimumStock(e.target.value)} required />
                </div>
              </div>
              <div style={{ display: 'flex', gap: '0.5rem', marginTop: '1rem' }}>
                <button type="submit" style={{ background: '#0076ff', width: 'auto', margin: 0 }}>Actualizar Inventario</button>
                <button type="button" onClick={() => setEditingProduct(null)} style={{ background: '#666', width: 'auto', margin: 0 }}>Cerrar</button>
              </div>
            </form>
          )}

          {/* FORMULARIO NUEVO: Modificar Datos Básicos (PUT /api/products/{id}) */}
          {editingProductDetails && (
            <form onSubmit={handleUpdateProductDetails}>
              <h4 style={{ margin: '0 0 0.5rem 0', color: '#f39c12' }}>✏️ Modificar Catálogo de Producto</h4>
              <p style={{ margin: '0 0 1rem 0', color: '#666' }}>ID Comercial: <code>{editingProductDetails.id}</code></p>
              
              <div style={{ display: 'flex', gap: '0.5rem' }}>
                <div style={{ flex: 1 }}>
                  <label style={{ display: 'block', marginBottom: '0.2rem', fontSize: '0.85rem', fontWeight: 'bold' }}>Nombre del Artículo</label>
                  <input type="text" value={editName} onChange={e => setEditName(e.target.value)} required />
                </div>
                <div style={{ flex: 1 }}>
                  <label style={{ display: 'block', marginBottom: '0.2rem', fontSize: '0.85rem', fontWeight: 'bold' }}>Código SKU</label>
                  <input type="text" value={editSku} onChange={e => setEditSku(e.target.value)} required />
                </div>
              </div>

              <div style={{ display: 'flex', gap: '0.5rem', marginTop: '0.5rem' }}>
                <div style={{ flex: 1 }}>
                  <label style={{ display: 'block', marginBottom: '0.2rem', fontSize: '0.85rem', fontWeight: 'bold' }}>Precio Comercial ($)</label>
                  <input type="number" step="0.01" value={editPrice} onChange={e => setEditPrice(e.target.value)} required />
                </div>
                <div style={{ flex: 1 }}>
                  <label style={{ display: 'block', marginBottom: '0.2rem', fontSize: '0.85rem', fontWeight: 'bold' }}>Estado Operativo</label>
                  <select value={editActive} onChange={e => setEditActive(e.target.value === 'true')} style={{ padding: '0.5rem', width: '100%', borderRadius: '4px', border: '1px solid #ccc' }}>
                    <option value="true">Activo / Visible</option>
                    <option value="false">Inactivo / Oculto</option>
                  </select>
                </div>
              </div>

              <label style={{ display: 'block', marginTop: '0.75rem', marginBottom: '0.2rem', fontSize: '0.85rem', fontWeight: 'bold' }}>Categoría Asignada</label>
              <select value={editCategoryId} onChange={e => setEditCategoryId(e.target.value)} style={{ display: 'block', width: '100%', padding: '0.5rem', borderRadius: '4px', border: '1px solid #ccc' }} required>
                <option value="">-- Elige la Categoría Requerida --</option>
                {categories.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
              </select>

              <div style={{ display: 'flex', gap: '0.5rem', marginTop: '1.5rem' }}>
                <button type="submit" style={{ background: '#f39c12', width: 'auto', margin: 0 }}>Guardar Cambios Básicos</button>
                <button type="button" onClick={() => setEditingProductDetails(null)} style={{ background: '#666', width: 'auto', margin: 0 }}>Cancelar</button>
              </div>
            </form>
          )}

        </div>
      )}

      {/* TABLA PRINCIPAL DE VISTA DE INVENTARIO */}
      <h3>Catálogo e Inventario</h3>
      <table>
        <thead>
          <tr>
            <th>SKU</th>
            <th>Nombre del Producto</th>
            <th>Categoría</th>
            <th>Precio</th>
            <th>Stock Actual</th>
            <th>Acciones Colectoras</th>
          </tr>
        </thead>
        <tbody>
          {products.length === 0 ? (
            <tr><td colSpan="6">No hay productos registrados en base de datos.</td></tr>
          ) : (
            products.map(p => {
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
                    <button onClick={() => startProductDetailsEdit(p)} style={{ width: 'auto', padding: '4px 10px', fontSize: '0.85rem', margin: 0, background: '#f1f5f9', color: '#334155', border: '1px solid #cbd5e1' }}>
                      ✏️ Editar Info
                    </button>
                    <button onClick={() => startInventoryEdit(p)} style={{ width: 'auto', padding: '4px 10px', fontSize: '0.85rem', margin: 0, background: '#3b82f6' }}>
                      🔄 Stock
                    </button>
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