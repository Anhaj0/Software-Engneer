import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080/api';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
});

export const getCustomers = async ({ page = 1, size = 10, search = '' } = {}) => {
  const backendPage = Math.max(0, page - 1);
  const response = await apiClient.get('/customers', {
    params: { page: backendPage, size, search }
  });
  return response.data;
};

export const getCustomerById = async (id) => {
  const response = await apiClient.get(`/customers/${id}`);
  return response.data;
};

export const createCustomer = async (payload) => {
  const response = await apiClient.post('/customers', payload);
  return response.data;
};

export const updateCustomer = async (id, payload) => {
  const response = await apiClient.put(`/customers/${id}`, payload);
  return response.data;
};

export const deleteCustomer = async (id) => {
  const response = await apiClient.delete(`/customers/${id}`);
  return response.data;
};

export const uploadCustomersSheet = async (file) => {
  const formData = new FormData();
  formData.append('file', file);

  const response = await apiClient.post('/customers/bulk-upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
  return response.data;
};

// Aliases for compatibility with different versions of components
export const bulkUploadCustomers = uploadCustomersSheet;

export const getUploadJobStatus = async (jobId) => {
  const response = await apiClient.get(`/jobs/${jobId}/status`);
  return response.data;
};

export const pollUploadJobStatus = async (
  jobId,
  {
    intervalMs = 2000,
    timeoutMs = 60000,
    onUpdate = () => {},
    isTerminalState = (status) => ['COMPLETED', 'FAILED'].includes(status)
  } = {}
) => {
  const start = Date.now();

  while (Date.now() - start <= timeoutMs) {
    const data = await getUploadJobStatus(jobId);
    onUpdate(data);

    if (isTerminalState(data?.status)) {
      return data;
    }

    await new Promise((resolve) => setTimeout(resolve, intervalMs));
  }

  throw new Error('Polling timed out before job reached terminal state.');
};

export default apiClient;
