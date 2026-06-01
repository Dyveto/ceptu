const BASE_URL = 'http://localhost:8080/api';


export async function apiFetch(endpoint, options = {}) {
    const url = `${BASE_URL}${endpoint}`;

    const headers = {
        'Content-Type': 'application/json',
        ...options.headers,
    };

    const token = localStorage.getItem('ceptu_token');
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    const config = {
        ...options,
        headers,
    };

    if (config.body && typeof config.body === 'object') {
        config.body = JSON.stringify(config.body);
    }

    const response = await fetch(url, config);

    if (!response.ok) {
        if (response.status === 401 && !endpoint.includes('/auth/login')) {
            localStorage.removeItem('ceptu_token');
            window.location.href = '/login';
            return;
        }
        
        const errorData = await response.json().catch(() => ({}));
        const errorMessage = errorData.message || `Error HTTP: ${response.status}`;
        throw new Error(errorMessage);
    }

    if (response.status === 204) return null;

    return await response.json();
}