import { useState } from 'react'

const NOTIFICATION_OPTIONS = [
  { value: 'DAILY', label: 'Daily emails', description: 'Get an email every day about new EOTD posts' },
  { value: 'WEEKLY', label: 'Weekly emails', description: 'Get a weekly digest of new EOTD posts' },
  { value: 'NONE', label: 'No emails', description: 'Do not receive email notifications' },
]

function AccountSettings({ user, onUserUpdated }) {
  const [preference, setPreference] = useState(user?.notificationPreference || 'NONE')
  const [saving, setSaving] = useState(false)
  const [message, setMessage] = useState(null)

  const handleSave = async () => {
    setSaving(true)
    setMessage(null)
    try {
      const res = await fetch('/api/user/me/notifications', {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({ notificationPreference: preference }),
      })
      if (res.ok) {
        const updated = await res.json()
        onUserUpdated(updated)
        setMessage({ type: 'success', text: 'Notification preferences saved.' })
      } else {
        setMessage({ type: 'error', text: 'Failed to save preferences. Please try again.' })
      }
    } catch {
      setMessage({ type: 'error', text: 'Network error. Please try again.' })
    } finally {
      setSaving(false)
    }
  }

  if (!user) return null

  return (
    <div className="min-h-screen bg-pink-50 flex flex-col items-center justify-start px-4 py-12">
      <div className="bg-white rounded-2xl shadow-lg p-8 max-w-lg w-full">
        <h2 className="text-2xl font-bold text-pink-600 mb-1">Account Settings</h2>
        <p className="text-gray-500 text-sm mb-6">Manage your profile and notification preferences</p>

        <div className="mb-6">
          <h3 className="text-sm font-semibold text-gray-700 uppercase tracking-wide mb-3">Profile</h3>
          <div className="bg-gray-50 rounded-xl p-4 text-sm text-gray-700 space-y-1">
            <p><span className="font-medium">Name:</span> {user.firstName} {user.lastName}</p>
            <p><span className="font-medium">Email:</span> {user.email}</p>
            <p><span className="font-medium">Role:</span> {user.role}</p>
            <p><span className="font-medium">Member since:</span> {new Date(user.registrationDate).toLocaleDateString()}</p>
          </div>
        </div>

        <div className="mb-6">
          <h3 className="text-sm font-semibold text-gray-700 uppercase tracking-wide mb-3">Email Notifications</h3>
          <div className="space-y-3">
            {NOTIFICATION_OPTIONS.map((opt) => (
              <label
                key={opt.value}
                className={`flex items-start gap-3 p-3 rounded-xl border cursor-pointer transition ${
                  preference === opt.value
                    ? 'border-pink-400 bg-pink-50'
                    : 'border-gray-200 hover:border-pink-300'
                }`}
              >
                <input
                  type="radio"
                  name="notification"
                  value={opt.value}
                  checked={preference === opt.value}
                  onChange={() => setPreference(opt.value)}
                  className="mt-0.5 accent-pink-600"
                />
                <div>
                  <p className="font-medium text-gray-800">{opt.label}</p>
                  <p className="text-xs text-gray-500">{opt.description}</p>
                </div>
              </label>
            ))}
          </div>
        </div>

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

        <button
          onClick={handleSave}
          disabled={saving}
          className="w-full bg-pink-600 text-white py-2 rounded-xl font-medium hover:bg-pink-700 transition disabled:opacity-60"
        >
          {saving ? 'Saving…' : 'Save Preferences'}
        </button>
      </div>
    </div>
  )
}

export default AccountSettings
