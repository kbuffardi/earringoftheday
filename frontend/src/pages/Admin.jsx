import { useState, useEffect } from 'react'
import { apiFetch } from '../api'

const EMPTY_FORM = {
  date: '',
  brand: '',
  productName: '',
  referralLink: '',
  instructions: '',
  productImageUrl: '',
  instagramPostUrl: '',
  displayOrder: 0,
}

export default function Admin() {
  const [entries, setEntries] = useState([])
  const [form, setForm] = useState(EMPTY_FORM)
  const [editingId, setEditingId] = useState(null)
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState(null)
  const [success, setSuccess] = useState(null)

  const fetchAll = () => {
    setLoading(true)
    apiFetch('/api/eotd', { credentials: 'include' })
      .then((r) => r.json())
      .then((data) => {
        setEntries(data.sort((a, b) => new Date(b.date) - new Date(a.date)))
        setLoading(false)
      })
      .catch(() => {
        setError('Failed to load entries')
        setLoading(false)
      })
  }

  useEffect(() => { fetchAll() }, [])

  const handleChange = (e) => {
    const { name, value } = e.target
    setForm((f) => ({ ...f, [name]: name === 'displayOrder' ? Number(value) : value }))
  }

  const handleEdit = (entry) => {
    setEditingId(entry.id)
    setForm({
      date: entry.date || '',
      brand: entry.brand || '',
      productName: entry.productName || '',
      referralLink: entry.referralLink || '',
      instructions: entry.instructions || '',
      productImageUrl: entry.productImageUrl || '',
      instagramPostUrl: entry.instagramPostUrl || '',
      displayOrder: entry.displayOrder ?? 0,
    })
    setError(null)
    setSuccess(null)
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }

  const handleCancel = () => {
    setEditingId(null)
    setForm(EMPTY_FORM)
    setError(null)
    setSuccess(null)
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError(null)
    setSuccess(null)
    setSaving(true)

    const url = editingId ? `/api/eotd/${editingId}` : '/api/eotd'
    const method = editingId ? 'PUT' : 'POST'

    try {
      const res = await apiFetch(url, {
        method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(form),
      })
      if (!res.ok) throw new Error(`Server error: ${res.status}`)
      setSuccess(editingId ? 'Entry updated!' : 'Entry created!')
      setEditingId(null)
      setForm(EMPTY_FORM)
      fetchAll()
    } catch (err) {
      setError(err.message)
    } finally {
      setSaving(false)
    }
  }

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this entry?')) return
    try {
      const res = await apiFetch(`/api/eotd/${id}`, { method: 'DELETE' })
      if (!res.ok) throw new Error(`Server error: ${res.status}`)
      setSuccess('Entry deleted.')
      if (editingId === id) handleCancel()
      fetchAll()
    } catch (err) {
      setError(err.message)
    }
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <main className="max-w-5xl mx-auto px-4 py-8 space-y-8">
        {/* Form */}
        <section className="bg-white rounded-2xl shadow p-6">
          <h2 className="text-lg font-semibold text-gray-700 mb-4">
            {editingId ? 'Edit EOTD Entry' : 'New EOTD Entry'}
          </h2>

          {error && <div className="mb-4 text-red-600 text-sm">{error}</div>}
          {success && <div className="mb-4 text-green-600 text-sm">{success}</div>}

          <form onSubmit={handleSubmit} className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-600 mb-1">
                Date (Pacific time) <span className="text-red-400">*</span>
              </label>
              <input
                type="date"
                name="date"
                required
                value={form.date}
                onChange={handleChange}
                className="w-full border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-pink-300"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-600 mb-1">Display Order</label>
              <input
                type="number"
                name="displayOrder"
                min="0"
                value={form.displayOrder}
                onChange={handleChange}
                className="w-full border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-pink-300"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-600 mb-1">Brand</label>
              <input
                type="text"
                name="brand"
                value={form.brand}
                onChange={handleChange}
                placeholder="Brand name"
                className="w-full border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-pink-300"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-600 mb-1">Product Name</label>
              <input
                type="text"
                name="productName"
                value={form.productName}
                onChange={handleChange}
                placeholder="Product name or ID"
                className="w-full border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-pink-300"
              />
            </div>

            <div className="md:col-span-2">
              <label className="block text-sm font-medium text-gray-600 mb-1">Referral Link</label>
              <input
                type="url"
                name="referralLink"
                value={form.referralLink}
                onChange={handleChange}
                placeholder="https://..."
                className="w-full border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-pink-300"
              />
            </div>

            <div className="md:col-span-2">
              <label className="block text-sm font-medium text-gray-600 mb-1">
                Instagram Post URL
              </label>
              <input
                type="url"
                name="instagramPostUrl"
                value={form.instagramPostUrl}
                onChange={handleChange}
                placeholder="https://www.instagram.com/p/..."
                className="w-full border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-pink-300"
              />
            </div>

            <div className="md:col-span-2">
              <label className="block text-sm font-medium text-gray-600 mb-1">
                Official Product Image URL
              </label>
              <input
                type="url"
                name="productImageUrl"
                value={form.productImageUrl}
                onChange={handleChange}
                placeholder="https://..."
                className="w-full border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-pink-300"
              />
            </div>

            <div className="md:col-span-2">
              <label className="block text-sm font-medium text-gray-600 mb-1">
                Instructions <span className="text-gray-400 font-normal">(Markdown supported)</span>
              </label>
              <textarea
                name="instructions"
                rows={6}
                value={form.instructions}
                onChange={handleChange}
                placeholder="Description and/or details (supports Markdown)"
                className="w-full border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-pink-300 font-mono"
              />
            </div>

            <div className="md:col-span-2 flex gap-3">
              <button
                type="submit"
                disabled={saving}
                className="bg-pink-500 hover:bg-pink-600 text-white font-semibold py-2 px-6 rounded-xl transition-colors disabled:opacity-50"
              >
                {saving ? 'Saving…' : editingId ? 'Update Entry' : 'Create Entry'}
              </button>
              {editingId && (
                <button
                  type="button"
                  onClick={handleCancel}
                  className="bg-gray-100 hover:bg-gray-200 text-gray-600 font-semibold py-2 px-6 rounded-xl transition-colors"
                >
                  Cancel
                </button>
              )}
            </div>
          </form>
        </section>

        {/* Entry list */}
        <section className="bg-white rounded-2xl shadow p-6">
          <h2 className="text-lg font-semibold text-gray-700 mb-4">All Entries</h2>

          {loading ? (
            <div className="flex justify-center py-8">
              <div className="animate-spin rounded-full h-8 w-8 border-4 border-pink-400 border-t-transparent" />
            </div>
          ) : entries.length === 0 ? (
            <p className="text-gray-400 text-sm">No entries yet.</p>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full text-sm">
                <thead>
                  <tr className="text-left text-gray-500 border-b">
                    <th className="pb-2 pr-4">Date</th>
                    <th className="pb-2 pr-4">Brand</th>
                    <th className="pb-2 pr-4">Product</th>
                    <th className="pb-2 pr-4">Order</th>
                    <th className="pb-2" />
                  </tr>
                </thead>
                <tbody>
                  {entries.map((entry) => (
                    <tr key={entry.id} className="border-b last:border-0 hover:bg-pink-50 transition-colors">
                      <td className="py-2 pr-4 text-gray-700">{entry.date}</td>
                      <td className="py-2 pr-4 text-gray-700">{entry.brand || '—'}</td>
                      <td className="py-2 pr-4 text-gray-700">{entry.productName || '—'}</td>
                      <td className="py-2 pr-4 text-gray-500">{entry.displayOrder}</td>
                      <td className="py-2 flex gap-2">
                        <button
                          onClick={() => handleEdit(entry)}
                          className="text-pink-500 hover:text-pink-700 font-medium"
                        >
                          Edit
                        </button>
                        <button
                          onClick={() => handleDelete(entry.id)}
                          className="text-red-400 hover:text-red-600 font-medium"
                        >
                          Delete
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </section>
      </main>
    </div>
  )
}
