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
import { useEffect, useRef, useState } from 'react';
import { getUploadJobStatus, uploadCustomersSheet } from '../api';

function BulkUpload({ onDone }) {
  const [file, setFile] = useState(null);
  const [error, setError] = useState('');
  const [jobId, setJobId] = useState('');
  const [status, setStatus] = useState('IDLE');
  const [details, setDetails] = useState(null);
  const [uploading, setUploading] = useState(false);
  const pollTimer = useRef(null);

  useEffect(() => {
    if (!jobId) return undefined;

    const pollStatus = async () => {
      try {
        const response = await getUploadJobStatus(jobId);
        setStatus(response?.status || 'UNKNOWN');
        setDetails(response);

        if (['COMPLETED', 'FAILED'].includes(response?.status)) {
          if (pollTimer.current) {
            clearInterval(pollTimer.current);
            pollTimer.current = null;
          }
        }
      } catch (err) {
        setError('Failed to poll upload status.');
      }
    };

    pollStatus();
    pollTimer.current = setInterval(pollStatus, 2500);

    return () => {
      if (pollTimer.current) {
        clearInterval(pollTimer.current);
        pollTimer.current = null;
      }
    };
  }, [jobId]);

  const validateFile = (selectedFile) => {
    if (!selectedFile) return 'Please choose an .xlsx file.';
    if (!selectedFile.name.toLowerCase().endsWith('.xlsx')) {
      return 'Only .xlsx files are supported.';
    }
    return '';
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError('');

    const validationMessage = validateFile(file);
    if (validationMessage) {
      setError(validationMessage);
      return;
    }

    setUploading(true);
    try {
      const response = await uploadCustomersSheet(file);
      const id = response?.jobId || response?.id;
      setJobId(id);
      setStatus('SUBMITTED');
      setDetails(response);
    } catch (err) {
      setError('Upload failed. Please try again.');
    } finally {
      setUploading(false);
    }
  };

  return (
    <div>
      <h2 className="mb-3 text-lg font-semibold text-gray-800">Bulk Upload Customers</h2>
      <p className="mb-4 text-sm text-gray-600">Upload an Excel file (.xlsx) and monitor processing status.</p>

      {error && <p className="mb-3 rounded bg-red-50 px-3 py-2 text-sm text-red-600">{error}</p>}

      <form onSubmit={handleSubmit} className="space-y-4">
        <input
          type="file"
          accept=".xlsx"
          onChange={(e) => setFile(e.target.files?.[0] || null)}
          className="block w-full rounded border border-gray-300 p-2 text-sm"
        />

        <div className="flex gap-3">
          <button
            type="submit"
            disabled={uploading}
            className="rounded bg-indigo-600 px-4 py-2 text-sm font-medium text-white hover:bg-indigo-700 disabled:opacity-60"
          >
            {uploading ? 'Uploading...' : 'Upload File'}
          </button>
          <button
            type="button"
            onClick={onDone}
            className="rounded border border-gray-300 px-4 py-2 text-sm font-medium text-gray-700"
          >
            Back
          </button>
        </div>
      </form>

      {jobId && (
        <div className="mt-5 rounded border border-gray-200 bg-gray-50 p-4 text-sm">
          <p>
            <span className="font-medium text-gray-700">Job ID:</span> {jobId}
          </p>
          <p>
            <span className="font-medium text-gray-700">Status:</span> {status}
          </p>
          {details?.processedCount !== undefined && (
            <p>
              <span className="font-medium text-gray-700">Processed:</span> {details.processedCount}
            </p>
          )}
          {details?.failedCount !== undefined && (
            <p>
              <span className="font-medium text-gray-700">Failed:</span> {details.failedCount}
            </p>
          )}
        </div>
      )}
    </div>
  );
}

export default BulkUpload;
