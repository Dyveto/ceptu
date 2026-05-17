import React, { useState } from 'react';
import { apiFetch } from '../api/apiClient.js';

function Login({ onLoginSuccess, onSwitchToRegister }) {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');

        try {
            const data = await apiFetch('/auth/login', {
                method: 'POST',
                body: { email, password },
            });

            if (data && data.accessToken) {
                onLoginSuccess(data.accessToken);
            } else {
                setError('El servidor no devolvió un token de acceso válido.');
            }
        } catch (err) {
            setError(err.message || 'Credenciales inválidas');
        }
    };

    return (
        <div className="card">
            <h2>Ceptu - Iniciar Sesión</h2>
            <form onSubmit={handleSubmit}>
                <label>Correo Electrónico:</label>
                <input
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    placeholder="admin@unimagdalena.edu.co"
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

                {error && <p style={{ color: 'red', fontSize: '0.9rem', margin: '0.5rem 0' }}>{error}</p>}

                <button type="submit">Ingresar</button>
            </form>

            <p style={{ textAlign: 'center', fontSize: '0.9rem', marginTop: '1.2rem' }}>
                ¿No tienes una cuenta?{' '}
                <span
                    onClick={onSwitchToRegister}
                    style={{ color: '#0076ff', cursor: 'pointer', textDecoration: 'underline', fontWeight: 'bold' }}
                >
                    Regístrate aquí
                </span>
            </p>
        </div>
    );
}

export default Login;