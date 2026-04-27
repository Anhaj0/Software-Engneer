import { useState } from 'react';

const initialState = {
  name: '',
  email: '',
  phone: ''
};

function CustomerForm({ onSubmit, loading }) {
  const [form, setForm] = useState(initialState);

  const handleChange = (event) => {
    const { name, value } = event.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    onSubmit(form);
    setForm(initialState);
  };

  return (
    <form
      onSubmit={handleSubmit}
      className="rounded-lg border border-gray-200 bg-white p-6 shadow-sm"
    >
      <h2 className="mb-4 text-lg font-semibold">Add Customer</h2>
      <div className="grid gap-4 md:grid-cols-3">
        <input
          className="rounded border border-gray-300 px-3 py-2"
          name="name"
          placeholder="Name"
          value={form.name}
          onChange={handleChange}
          required
        />
        <input
          className="rounded border border-gray-300 px-3 py-2"
          name="email"
          type="email"
          placeholder="Email"
          value={form.email}
          onChange={handleChange}
          required
        />
        <input
          className="rounded border border-gray-300 px-3 py-2"
          name="phone"
          placeholder="Phone"
          value={form.phone}
          onChange={handleChange}
          required
        />
      </div>
      <button
        className="mt-4 rounded bg-blue-600 px-4 py-2 font-medium text-white hover:bg-blue-700 disabled:bg-blue-300"
        type="submit"
        disabled={loading}
      >
        {loading ? 'Saving...' : 'Save Customer'}
      </button>
    </form>
  );
}

export default CustomerForm;
