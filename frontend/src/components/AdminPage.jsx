import { useState, useEffect, useCallback } from 'react'
import { apiFetch } from '../api'

function AdminPage() {
  const [query, setQuery] = useState('')
  const [users, setUsers] = useState([])
  const [loading, setLoading] = useState(false)
  const [message, setMessage] = useState(null)

  const fetchUsers = useCallback(async (searchQuery) => {
    setLoading(true)
    setMessage(null)
    try {
      const url = searchQuery
        ? `/api/admin/users?query=${encodeURIComponent(searchQuery)}`
        : '/api/admin/users'
      const res = await apiFetch(url)
      if (res.ok) {
        setUsers(await res.json())
      } else {
        setMessage({ type: 'error', text: 'Failed to load users.' })
      }
    } catch {
      setMessage({ type: 'error', text: 'Network error.' })
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    fetchUsers('')
  }, [fetchUsers])

  const handleSearch = (e) => {
    e.preventDefault()
    fetchUsers(query)
  }

  const handleRoleChange = async (userId, newRole) => {
    try {
      const res = await apiFetch(`/api/admin/users/${userId}/role`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ role: newRole }),
      })
      if (res.ok) {
        const updated = await res.json()
        setUsers((prev) => prev.map((u) => (u.id === updated.id ? updated : u)))
        setMessage({ type: 'success', text: `Updated ${updated.email} to ${updated.role}.` })
      } else {
        setMessage({ type: 'error', text: 'Failed to update role.' })
      }
    } catch {
      setMessage({ type: 'error', text: 'Network error.' })
    }
  }

  return (
    <div className="min-h-screen bg-pink-50 flex flex-col items-center justify-start px-4 py-12">
      <div className="bg-white rounded-2xl shadow-lg p-8 max-w-3xl w-full">
        <h2 className="text-2xl font-bold text-pink-600 mb-1">Admin — User Management</h2>
        <p className="text-gray-500 text-sm mb-6">Search users and manage their account roles</p>

        <form onSubmit={handleSearch} className="flex gap-3 mb-6">
          <input
            type="text"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            placeholder="Search by name or email…"
            className="flex-1 border border-gray-300 rounded-xl px-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-pink-400"
          />
          <button
            type="submit"
            className="bg-pink-600 text-white px-5 py-2 rounded-xl text-sm font-medium hover:bg-pink-700 transition"
          >
            Search
          </button>
        </form>

        {message && (
          <div
            className={`mb-4 px-4 py-2 rounded-xl text-sm ${
              message.type === 'success'
                ? 'bg-green-100 text-green-700'
                : 'bg-red-100 text-red-700'
            }`}
          >
            {message.text}
          </div>
        )}

        {loading ? (
          <p className="text-gray-500 text-sm">Loading…</p>
        ) : users.length === 0 ? (
          <p className="text-gray-500 text-sm">No users found.</p>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-sm text-left">
              <thead>
                <tr className="text-gray-500 border-b">
                  <th className="pb-2 pr-4 font-medium">Name</th>
                  <th className="pb-2 pr-4 font-medium">Email</th>
                  <th className="pb-2 pr-4 font-medium">Role</th>
                  <th className="pb-2 font-medium">Actions</th>
                </tr>
              </thead>
              <tbody>
                {users.map((u) => (
                  <tr key={u.id} className="border-b last:border-0 hover:bg-pink-50">
                    <td className="py-3 pr-4 text-gray-800">
                      {u.firstName} {u.lastName}
                    </td>
                    <td className="py-3 pr-4 text-gray-600">{u.email}</td>
                    <td className="py-3 pr-4">
                      <span
                        className={`px-2 py-0.5 rounded-full text-xs font-medium ${
                          u.role === 'ADMIN'
                            ? 'bg-pink-100 text-pink-700'
                            : 'bg-gray-100 text-gray-600'
                        }`}
                      >
                        {u.role}
                      </span>
                    </td>
                    <td className="py-3">
                      {u.role === 'SUBSCRIBER' ? (
                        <button
                          onClick={() => handleRoleChange(u.id, 'ADMIN')}
                          className="text-pink-600 hover:underline text-xs font-medium"
                        >
                          Upgrade to Admin
                        </button>
                      ) : (
                        <button
                          onClick={() => handleRoleChange(u.id, 'SUBSCRIBER')}
                          className="text-gray-500 hover:underline text-xs font-medium"
                        >
                          Downgrade to Subscriber
                        </button>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  )
}

export default AdminPage
