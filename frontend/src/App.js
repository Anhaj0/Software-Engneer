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
import { useEffect, useMemo, useState } from 'react';
import CustomerList from './components/CustomerList';
import CustomerForm from './components/CustomerForm';
import BulkUpload from './components/BulkUpload';
import { getCustomerById, getCustomers } from './api';

const VIEWS = {
  LIST: 'list',
  FORM: 'form',
  BULK_UPLOAD: 'bulk-upload'
};

function App() {
  const [view, setView] = useState(VIEWS.LIST);
  const [selectedCustomer, setSelectedCustomer] = useState(null);
  const [customers, setCustomers] = useState([]);
  const [reloadToken, setReloadToken] = useState(0);

  const pageParams = useMemo(() => ({ page: 1, size: 100 }), []);

  useEffect(() => {
    const fetchCustomers = async () => {
      try {
        const data = await getCustomers(pageParams);
        setCustomers(data?.items || data?.content || []);
      } catch (error) {
        console.error('Failed to fetch customers for form options:', error);
      }
    };

    fetchCustomers();
  }, [pageParams, reloadToken]);

  const handleCreate = () => {
    setSelectedCustomer(null);
    setView(VIEWS.FORM);
  };

  const handleEdit = async (customerId) => {
    try {
      const data = await getCustomerById(customerId);
      setSelectedCustomer(data);
      setView(VIEWS.FORM);
    } catch (error) {
      console.error('Failed to fetch customer details:', error);
    }
  };

  const handleSaved = () => {
    setView(VIEWS.LIST);
    setSelectedCustomer(null);
    setReloadToken((token) => token + 1);
  };

  const handleBackToList = () => {
    setView(VIEWS.LIST);
    setSelectedCustomer(null);
  };

  return (
    <div className="min-h-screen bg-gray-100 p-6">
      <div className="mx-auto max-w-6xl rounded-lg bg-white p-6 shadow">
        <div className="mb-6 flex items-center justify-between">
          <h1 className="text-2xl font-bold text-gray-800">Customer Management</h1>
          <div className="flex gap-3">
            <button
              onClick={() => setView(VIEWS.LIST)}
              className="rounded bg-gray-200 px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-300"
            >
              Customers
            </button>
            <button
              onClick={handleCreate}
              className="rounded bg-blue-600 px-4 py-2 text-sm font-medium text-white hover:bg-blue-700"
            >
              New Customer
            </button>
            <button
              onClick={() => setView(VIEWS.BULK_UPLOAD)}
              className="rounded bg-indigo-600 px-4 py-2 text-sm font-medium text-white hover:bg-indigo-700"
            >
              Bulk Upload
            </button>
          </div>
        </div>

        {view === VIEWS.LIST && (
          <CustomerList onEdit={handleEdit} reloadToken={reloadToken} onCreate={handleCreate} />
        )}

        {view === VIEWS.FORM && (
          <CustomerForm
            existingCustomers={customers}
            initialData={selectedCustomer}
            onCancel={handleBackToList}
            onSaved={handleSaved}
          />
        )}

        {view === VIEWS.BULK_UPLOAD && <BulkUpload onDone={handleBackToList} />}
      </div>
    </div>
  );
}

export default App;
