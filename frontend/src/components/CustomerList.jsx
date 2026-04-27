function CustomerList({ customers }) {
  return (
    <section className="rounded-lg border border-gray-200 bg-white p-6 shadow-sm">
      <h2 className="mb-4 text-lg font-semibold">Customers</h2>
      {customers.length === 0 ? (
        <p className="text-gray-500">No customers found.</p>
      ) : (
        <ul className="space-y-2">
          {customers.map((customer) => (
            <li
              key={customer.id || `${customer.email}-${customer.phone}`}
              className="rounded border border-gray-100 bg-gray-50 px-4 py-3"
            >
              <p className="font-medium">{customer.name}</p>
              <p className="text-sm text-gray-600">{customer.email}</p>
              <p className="text-sm text-gray-600">{customer.phone}</p>
            </li>
          ))}
        </ul>
      )}
    </section>
import { useEffect, useState } from 'react';
import { getCustomers } from '../api';

function CustomerList({ onEdit, onCreate, reloadToken }) {
  const [customers, setCustomers] = useState([]);
  const [page, setPage] = useState(1);
  const [size] = useState(10);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchCustomers = async () => {
      setLoading(true);
      setError('');
      try {
        const data = await getCustomers({ page, size });
        const items = data?.items || data?.content || [];
        setCustomers(items);
        setTotalPages(data?.totalPages || Math.max(1, Math.ceil((data?.total || items.length) / size)));
      } catch (err) {
        setError('Failed to load customers. Please try again.');
      } finally {
        setLoading(false);
      }
    };

    fetchCustomers();
  }, [page, reloadToken, size]);

  return (
    <div>
      <div className="mb-4 flex items-center justify-between">
        <h2 className="text-lg font-semibold text-gray-800">Customer List</h2>
        <button
          onClick={onCreate}
          className="rounded bg-blue-600 px-3 py-2 text-sm font-medium text-white hover:bg-blue-700"
        >
          Create Customer
        </button>
      </div>

      {error && <p className="mb-3 rounded bg-red-50 px-3 py-2 text-sm text-red-600">{error}</p>}

      <div className="overflow-hidden rounded border border-gray-200">
        <table className="min-w-full table-auto text-left text-sm">
          <thead className="bg-gray-50 text-gray-700">
            <tr>
              <th className="px-4 py-3">Name</th>
              <th className="px-4 py-3">Email</th>
              <th className="px-4 py-3">Mobiles</th>
              <th className="px-4 py-3">Actions</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td className="px-4 py-4 text-gray-500" colSpan={4}>
                  Loading customers...
                </td>
              </tr>
            ) : customers.length === 0 ? (
              <tr>
                <td className="px-4 py-4 text-gray-500" colSpan={4}>
                  No customers found.
                </td>
              </tr>
            ) : (
              customers.map((customer) => (
                <tr key={customer.id} className="border-t border-gray-100">
                  <td className="px-4 py-3">{customer.name}</td>
                  <td className="px-4 py-3">{customer.email}</td>
                  <td className="px-4 py-3">{(customer.mobiles || []).join(', ')}</td>
                  <td className="px-4 py-3">
                    <button
                      onClick={() => onEdit(customer.id)}
                      className="rounded bg-amber-500 px-3 py-1 text-xs font-medium text-white hover:bg-amber-600"
                    >
                      Edit
                    </button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      <div className="mt-4 flex items-center justify-end gap-2">
        <button
          onClick={() => setPage((prev) => Math.max(1, prev - 1))}
          disabled={page === 1}
          className="rounded border border-gray-300 px-3 py-1 text-sm disabled:cursor-not-allowed disabled:opacity-50"
        >
          Previous
        </button>
        <span className="text-sm text-gray-600">
          Page {page} of {totalPages}
        </span>
        <button
          onClick={() => setPage((prev) => Math.min(totalPages, prev + 1))}
          disabled={page >= totalPages}
          className="rounded border border-gray-300 px-3 py-1 text-sm disabled:cursor-not-allowed disabled:opacity-50"
        >
          Next
        </button>
      </div>
    </div>
  );
}

export default CustomerList;
