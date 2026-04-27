import { useEffect, useState } from 'react';
import { bulkUploadCustomers, createCustomer, getCustomers } from './api';
import BulkUpload from './components/BulkUpload';
import CustomerForm from './components/CustomerForm';
import CustomerList from './components/CustomerList';

function App() {
  const [customers, setCustomers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const loadCustomers = async () => {
    try {
      const response = await getCustomers();
      setCustomers(response.data || []);
    } catch (err) {
      setError('Failed to load customers. Please check backend service.');
    }
  };

  useEffect(() => {
    loadCustomers();
  }, []);

  const handleCreate = async (payload) => {
    setLoading(true);
    setError('');
    try {
      await createCustomer(payload);
      await loadCustomers();
    } catch (err) {
      setError('Failed to save customer.');
    } finally {
      setLoading(false);
    }
  };

  const handleBulkUpload = async (file) => {
    const formData = new FormData();
    formData.append('file', file);

    setLoading(true);
    setError('');
    try {
      await bulkUploadCustomers(formData);
      await loadCustomers();
    } catch (err) {
      setError('Bulk upload failed.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="min-h-screen bg-gray-100 p-6 md:p-10">
      <div className="mx-auto flex max-w-5xl flex-col gap-6">
        <h1 className="text-3xl font-bold">Customer Management</h1>
        {error && <p className="rounded bg-red-100 px-4 py-2 text-red-600">{error}</p>}
        <CustomerForm onSubmit={handleCreate} loading={loading} />
        <BulkUpload onUpload={handleBulkUpload} loading={loading} />
        <CustomerList customers={customers} />
      </div>
    </main>
  );
}

export default App;
