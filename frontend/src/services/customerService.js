import { apiFetch } from '../api/apiClient';

export const customerService = {
  getAll: (token) => apiFetch('/customers', {
    headers: { 'Authorization': `Bearer ${token}` }
  }),
  create: (customerData, token) => apiFetch('/customers', {
    method: 'POST',
    body: JSON.stringify(customerData),
    headers: { 'Authorization': `Bearer ${token}` }
  })
};