import React, { useState } from 'react';
import { apiFetch } from '../api/apiClient.js';

function Register({ onRegisterSuccess, onSwitchToLogin }) {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [role, setRole] = useState('ROLE_ADMIN');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    try {
      await apiFetch('/auth/register', {
        method: 'POST',
        body: { email, password, role },
      });

      setSuccess('¡Registro exitoso! Redirigiendo al inicio de sesión...');
      
      setTimeout(() => {
        onRegisterSuccess();
      }, 2000);

    } catch (err) {
      setError(err.message || 'Error al intentar registrar el usuario');
    }
  };

  return (
    <div className="card">
      <h2>Ceptu - Crear Cuenta</h2>
      <form onSubmit={handleSubmit}>
        <label>Correo Electrónico:</label>
        <input
          type="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          placeholder="ejemplo@unimagdalena.edu.co"
          required
        />

        <label>Contraseña:</label>
        <input
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          placeholder="••••••••"
          required
        />

        <label>Rol de Acceso:</label>
        <select 
          value={role} 
          onChange={(e) => setRole(e.target.value)}
          style={{ display: 'block', width: '100%', margin: '0.75rem 0', padding: '0.5rem', borderRadius: '4px', border: '1px solid #ccc' }}
        >
          <option value="ROLE_ADMIN">Administrador (ADMIN)</option>
          <option value="ROLE_USER">Usuario Regular (USER)</option>
        </select>

        {error && <p style={{ color: 'red', fontSize: '0.9rem', margin: '0.5rem 0' }}>{error}</p>}
        {success && <p style={{ color: 'green', fontSize: '0.9rem', margin: '0.5rem 0' }}>{success}</p>}

        <button type="submit">Registrarse</button>
      </form>

      <p style={{ textAlign: 'center', fontSize: '0.9rem', marginTop: '1.2rem' }}>
        ¿Ya tienes una cuenta?{' '}
        <span 
          onClick={onSwitchToLogin} 
          style={{ color: '#0076ff', cursor: 'pointer', textDecoration: 'underline', fontWeight: 'bold' }}
        >
          Inicia sesión aquí
        </span>
      </p>
    </div>
  );
}

export default Register;