import React, { useState, useEffect } from 'react';
import { apiFetch } from '../../api/apiClient.js';

export default function InventoryTab() {
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  // Controladores de visibilidad para formularios
  const [showCatForm, setShowCatForm] = useState(false);
  const [showProdForm, setShowProdForm] = useState(false);
  const [editingProduct, setEditingProduct] = useState(null); // Producto al que se le ajustará stock

  // Estados de formularios
  const [catName, setCatName] = useState('');
  const [catDescription, setCatDescription] = useState('');
  const [prodName, setProdName] = useState('');
  const [prodSku, setProdSku] = useState('');
  const [prodPrice, setProdPrice] = useState('');
  const [prodInitialStock, setProdInitialStock] = useState('');
  const [prodMinimumStock, setProdMinimumStock] = useState('');
  const [prodCategoryId, setProdCategoryId] = useState('');

  // Estados para el endpoint obligatorio: PUT /api/products/{id}/inventory (UpdateInventoryRequest)
  const [availableStock, setAvailableStock] = useState('');
  const [minimumStock, setMinimumStock] = useState('');

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

  // Enviar actualización aislada de inventario (PUT /api/products/{id}/inventory)
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

  const startInventoryEdit = (product) => {
    setEditingProduct(product);
    // Cargar los valores actuales si vienen en la respuesta del producto (o poner vacíos)
    setAvailableStock(product.inventory?.availableStock || product.availableStock || 0);
    setMinimumStock(product.inventory?.minimumStock || product.minimumStock || 0);
    setShowCatForm(false);
    setShowProdForm(false);
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
        <button onClick={() => { setShowCatForm(!showCatForm); setShowProdForm(false); setEditingProduct(null); }} style={{ background: showCatForm ? '#666' : '#34c759', width: 'auto', padding: '0.6rem 1.2rem' }}>
          {showCatForm ? '❌ Cancelar' : '➕ Nueva Categoría'}
        </button>
        <button onClick={() => { setShowProdForm(!showProdForm); setShowCatForm(false); setEditingProduct(null); }} style={{ background: showProdForm ? '#666' : '#0076ff', width: 'auto', padding: '0.6rem 1.2rem' }}>
          {showProdForm ? '❌ Cancelar' : '📦 Registrar Producto'}
        </button>
      </div>

      {/* COMPONENTE DE FORMULARIOS DESPLEGABLES */}
      {(showCatForm || showProdForm || editingProduct) && (
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

          {/* Formulario Especial: Ajustar Existencias de Inventario */}
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

        </div>
      )}

      {/* TABLA PRINCIPAL DE VISTA DE INVENTARIO */}
      <h3>Catálogo e Inventario</h3>
      <table>
        <thead>
          <tr>
            <th>SKU</th>
            <th>Nombre del Producto</th>
            <th>Precio</th>
            <th>Stock Actual</th>
            <th>Acción</th>
          </tr>
        </thead>
        <tbody>
          {products.length === 0 ? (
            <tr><td colSpan="5">No hay productos registrados en base de datos.</td></tr>
          ) : (
            products.map(p => {
              // Manejar si el stock viene anidado en el objeto inventory (1:1) o plano
              const currentStock = p.inventory ? p.inventory.availableStock : (p.availableStock ?? p.initialStock ?? 0);
              return (
                <tr key={p.id}>
                  <td><code style={{ background: '#eee', padding: '2px 4px', borderRadius: '3px' }}>{p.sku || 'N/A'}</code></td>
                  <td><strong>{p.name}</strong></td>
                  <td>${p.price?.toLocaleString()}</td>
                  <td>
                    <span style={{ fontWeight: 'bold', color: currentStock <= (p.inventory?.minimumStock || p.minimumStock) ? 'red' : 'inherit' }}>
                      {currentStock} unds
                    </span>
                  </td>
                  <td>
                    <button onClick={() => startInventoryEdit(p)} style={{ width: 'auto', padding: '4px 10px', fontSize: '0.85rem', margin: 0, background: '#f39c12' }}>
                      ⚙️ Ajustar Stock
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