import { useMemo, useState } from 'react';
import { createCustomer, updateCustomer } from '../api';

const emptyAddress = { line1: '', city: '', state: '', zip: '' };

const normalizeFormData = (initialData) => ({
  name: initialData?.name || '',
  email: initialData?.email || '',
  mobiles: initialData?.mobiles?.length ? initialData.mobiles : [''],
  addresses: initialData?.addresses?.length ? initialData.addresses : [{ ...emptyAddress }],
  familyMemberIds: initialData?.familyMemberIds || []
});

function CustomerForm({ initialData, existingCustomers = [], onCancel, onSaved }) {
  const [formData, setFormData] = useState(normalizeFormData(initialData));
  const [errors, setErrors] = useState({});
  const [saving, setSaving] = useState(false);

  const isEdit = useMemo(() => Boolean(initialData?.id), [initialData]);

  const validate = () => {
    const nextErrors = {};

    if (!formData.name.trim()) {
      nextErrors.name = 'Name is required.';
    }

    if (!formData.email.trim()) {
      nextErrors.email = 'Email is required.';
    } else if (!/^\S+@\S+\.\S+$/.test(formData.email)) {
      nextErrors.email = 'Please enter a valid email address.';
    }

    const hasInvalidMobile = formData.mobiles.some((mobile) => !mobile.trim());
    if (hasInvalidMobile) {
      nextErrors.mobiles = 'All mobile fields must be filled.';
    }

    const invalidAddress = formData.addresses.some(
      (addr) => !addr.line1.trim() || !addr.city.trim() || !addr.state.trim() || !addr.zip.trim()
    );
    if (invalidAddress) {
      nextErrors.addresses = 'Please complete all address fields.';
    }

    setErrors(nextErrors);
    return Object.keys(nextErrors).length === 0;
  };

  const updateField = (key, value) => {
    setFormData((prev) => ({ ...prev, [key]: value }));
  };

  const updateMobile = (index, value) => {
    const mobiles = [...formData.mobiles];
    mobiles[index] = value;
    updateField('mobiles', mobiles);
  };

  const addMobile = () => updateField('mobiles', [...formData.mobiles, '']);

  const removeMobile = (index) => {
    updateField(
      'mobiles',
      formData.mobiles.filter((_, mobileIdx) => mobileIdx !== index)
    );
  };

  const updateAddress = (index, key, value) => {
    const addresses = [...formData.addresses];
    addresses[index] = { ...addresses[index], [key]: value };
    updateField('addresses', addresses);
  };

  const addAddress = () => updateField('addresses', [...formData.addresses, { ...emptyAddress }]);

  const removeAddress = (index) => {
    updateField(
      'addresses',
      formData.addresses.filter((_, addressIdx) => addressIdx !== index)
    );
  };

  const toggleFamilyMember = (memberId) => {
    const exists = formData.familyMemberIds.includes(memberId);
    updateField(
      'familyMemberIds',
      exists
        ? formData.familyMemberIds.filter((id) => id !== memberId)
        : [...formData.familyMemberIds, memberId]
    );
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    if (!validate()) return;

    setSaving(true);
    try {
      if (isEdit) {
        await updateCustomer(initialData.id, formData);
      } else {
        await createCustomer(formData);
      }
      onSaved();
    } catch (error) {
      setErrors((prev) => ({ ...prev, submit: 'Failed to save customer. Please try again.' }));
    } finally {
      setSaving(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-5">
      <h2 className="text-lg font-semibold text-gray-800">{isEdit ? 'Edit Customer' : 'Create Customer'}</h2>

      {errors.submit && <p className="rounded bg-red-50 px-3 py-2 text-sm text-red-600">{errors.submit}</p>}

      <div>
        <label className="mb-1 block text-sm font-medium text-gray-700">Name</label>
        <input
          value={formData.name}
          onChange={(e) => updateField('name', e.target.value)}
          className="w-full rounded border border-gray-300 px-3 py-2 text-sm"
        />
        {errors.name && <p className="mt-1 text-xs text-red-600">{errors.name}</p>}
      </div>

      <div>
        <label className="mb-1 block text-sm font-medium text-gray-700">Email</label>
        <input
          value={formData.email}
          onChange={(e) => updateField('email', e.target.value)}
          className="w-full rounded border border-gray-300 px-3 py-2 text-sm"
        />
        {errors.email && <p className="mt-1 text-xs text-red-600">{errors.email}</p>}
      </div>

      <div>
        <div className="mb-2 flex items-center justify-between">
          <label className="text-sm font-medium text-gray-700">Mobile Numbers</label>
          <button type="button" onClick={addMobile} className="text-xs text-blue-600 hover:underline">
            + Add Mobile
          </button>
        </div>
        {formData.mobiles.map((mobile, index) => (
          <div key={`mobile-${index}`} className="mb-2 flex gap-2">
            <input
              value={mobile}
              onChange={(e) => updateMobile(index, e.target.value)}
              className="w-full rounded border border-gray-300 px-3 py-2 text-sm"
              placeholder="Enter mobile number"
            />
            {formData.mobiles.length > 1 && (
              <button
                type="button"
                onClick={() => removeMobile(index)}
                className="rounded border border-gray-300 px-3 py-2 text-xs"
              >
                Remove
              </button>
            )}
          </div>
        ))}
        {errors.mobiles && <p className="text-xs text-red-600">{errors.mobiles}</p>}
      </div>

      <div>
        <div className="mb-2 flex items-center justify-between">
          <label className="text-sm font-medium text-gray-700">Addresses</label>
          <button type="button" onClick={addAddress} className="text-xs text-blue-600 hover:underline">
            + Add Address
          </button>
        </div>

        {formData.addresses.map((address, index) => (
          <div key={`address-${index}`} className="mb-3 rounded border border-gray-200 p-3">
            <div className="grid grid-cols-1 gap-3 md:grid-cols-2">
              <input
                value={address.line1}
                onChange={(e) => updateAddress(index, 'line1', e.target.value)}
                placeholder="Address Line 1"
                className="rounded border border-gray-300 px-3 py-2 text-sm"
              />
              <input
                value={address.city}
                onChange={(e) => updateAddress(index, 'city', e.target.value)}
                placeholder="City"
                className="rounded border border-gray-300 px-3 py-2 text-sm"
              />
              <input
                value={address.state}
                onChange={(e) => updateAddress(index, 'state', e.target.value)}
                placeholder="State"
                className="rounded border border-gray-300 px-3 py-2 text-sm"
              />
              <input
                value={address.zip}
                onChange={(e) => updateAddress(index, 'zip', e.target.value)}
                placeholder="ZIP"
                className="rounded border border-gray-300 px-3 py-2 text-sm"
              />
            </div>
            {formData.addresses.length > 1 && (
              <button
                type="button"
                onClick={() => removeAddress(index)}
                className="mt-2 text-xs text-red-600 hover:underline"
              >
                Remove Address
              </button>
            )}
          </div>
        ))}
        {errors.addresses && <p className="text-xs text-red-600">{errors.addresses}</p>}
      </div>

      <div>
        <label className="mb-2 block text-sm font-medium text-gray-700">Family Members</label>
        <div className="max-h-40 overflow-y-auto rounded border border-gray-200 p-3">
          {existingCustomers
            .filter((customer) => customer.id !== initialData?.id)
            .map((customer) => (
              <label key={customer.id} className="mb-1 flex items-center gap-2 text-sm text-gray-700">
                <input
                  type="checkbox"
                  checked={formData.familyMemberIds.includes(customer.id)}
                  onChange={() => toggleFamilyMember(customer.id)}
                />
                {customer.name} ({customer.email})
              </label>
            ))}
          {existingCustomers.length === 0 && (
            <p className="text-xs text-gray-500">No existing customers available for selection.</p>
          )}
        </div>
      </div>

      <div className="flex gap-3">
        <button
          type="submit"
          disabled={saving}
          className="rounded bg-green-600 px-4 py-2 text-sm font-medium text-white hover:bg-green-700 disabled:opacity-60"
        >
          {saving ? 'Saving...' : isEdit ? 'Update Customer' : 'Create Customer'}
        </button>
        <button
          type="button"
          onClick={onCancel}
          className="rounded border border-gray-300 px-4 py-2 text-sm font-medium text-gray-700"
        >
          Cancel
        </button>
      </div>
    </form>
  );
}

export default CustomerForm;
