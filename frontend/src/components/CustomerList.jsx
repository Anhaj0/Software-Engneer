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
  );
}

export default CustomerList;
