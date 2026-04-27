import { useState } from 'react';

function BulkUpload({ onUpload, loading }) {
  const [file, setFile] = useState(null);

  const handleSubmit = (event) => {
    event.preventDefault();
    if (!file) return;
    onUpload(file);
    setFile(null);
    event.target.reset();
  };

  return (
    <form
      onSubmit={handleSubmit}
      className="rounded-lg border border-gray-200 bg-white p-6 shadow-sm"
    >
      <h2 className="mb-4 text-lg font-semibold">Bulk Upload (Excel)</h2>
      <input
        className="w-full rounded border border-gray-300 px-3 py-2"
        type="file"
        accept=".xlsx,.xls"
        onChange={(event) => setFile(event.target.files?.[0] || null)}
      />
      <button
        className="mt-4 rounded bg-emerald-600 px-4 py-2 font-medium text-white hover:bg-emerald-700 disabled:bg-emerald-300"
        type="submit"
        disabled={loading || !file}
      >
        {loading ? 'Uploading...' : 'Upload File'}
      </button>
    </form>
  );
}

export default BulkUpload;
