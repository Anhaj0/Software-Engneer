import axios from 'axios';

const api = axios.create({
  baseURL: process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json'
  }
});

export const getCustomers = () => api.get('/customers');
export const createCustomer = (payload) => api.post('/customers', payload);
export const bulkUploadCustomers = (formData) =>
  api.post('/customers/bulk-upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });

export default api;
