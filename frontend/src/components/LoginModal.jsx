import { useEffect } from 'react'

const providers = [
  { id: 'google', name: 'Google', icon: '🔵', color: 'bg-white border border-gray-300 text-gray-700 hover:bg-gray-50' },
  { id: 'microsoft', name: 'Microsoft', icon: '🟦', color: 'bg-white border border-gray-300 text-gray-700 hover:bg-gray-50' },
  { id: 'apple', name: 'Apple', icon: '⬛', color: 'bg-black text-white hover:bg-gray-900' },
  { id: 'facebook', name: 'Facebook', icon: '🔷', color: 'bg-blue-600 text-white hover:bg-blue-700' },
]

function LoginModal({ onClose }) {
  const handleLogin = (providerId) => {
    window.location.href = `/oauth2/authorization/${providerId}`
  }

  useEffect(() => {
    const handleKeyDown = (e) => {
      if (e.key === 'Escape') onClose()
    }
    document.addEventListener('keydown', handleKeyDown)
    return () => document.removeEventListener('keydown', handleKeyDown)
  }, [onClose])

  return (
    <div
      className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 px-4"
      onClick={onClose}
    >
      <div
        className="bg-white rounded-2xl shadow-xl p-8 max-w-sm w-full text-center"
        onClick={(e) => e.stopPropagation()}
      >
        <h2 className="text-2xl font-bold text-pink-600 mb-2">Sign In</h2>
        <p className="text-gray-500 mb-6">Choose your preferred sign-in method</p>
        <div className="flex flex-col gap-3">
          {providers.map((provider) => (
            <button
              key={provider.id}
              onClick={() => handleLogin(provider.id)}
              className={`flex items-center justify-center gap-3 w-full px-4 py-3 rounded-xl font-medium transition ${provider.color}`}
            >
              <span>{provider.icon}</span>
              Continue with {provider.name}
            </button>
          ))}
        </div>
        <p className="mt-6 text-xs text-gray-400">
          By signing in you agree to receive earring-related awesomeness.
        </p>
        <button
          onClick={onClose}
          className="mt-4 text-sm text-gray-400 hover:text-gray-600 transition"
        >
          Cancel
        </button>
      </div>
    </div>
  )
}

export default LoginModal
