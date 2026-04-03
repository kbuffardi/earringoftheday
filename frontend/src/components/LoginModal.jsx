import { useEffect } from 'react'

const providers = [
  { id: 'google', name: 'Google', icon: '🔵', color: 'bg-white border border-gray-300 text-gray-700 hover:bg-gray-50' },
  { id: 'microsoft', name: 'Microsoft', icon: '🟦', color: 'bg-white border border-gray-300 text-gray-700 hover:bg-gray-50' },
  { id: 'apple', name: 'Apple', icon: '⬛', color: 'bg-black text-white hover:bg-gray-900' },
  { id: 'facebook', name: 'Facebook', icon: '🔷', color: 'bg-blue-600 text-white hover:bg-blue-700' },
]

function LoginModal({ onClose }) {
  useEffect(() => {
    const handleKeyDown = (e) => {
      if (e.key === 'Escape') onClose()
    }
    document.addEventListener('keydown', handleKeyDown)
    return () => document.removeEventListener('keydown', handleKeyDown)
  }, [onClose])

  const handleLogin = (providerId) => {
    window.location.href = `/oauth2/authorization/${providerId}`
  }

  return (
    <div
      className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50 px-4"
      onClick={(e) => { if (e.target === e.currentTarget) onClose() }}
    >
      <div className="bg-white rounded-2xl shadow-lg p-8 max-w-sm w-full text-center relative">
        <button
          onClick={onClose}
          className="absolute top-4 right-4 text-gray-400 hover:text-gray-600 text-xl leading-none"
          aria-label="Close"
        >
          ✕
        </button>

        <h2 className="text-2xl font-bold text-pink-600 mb-2">💎 EarringOfTheDay</h2>
        <p className="text-gray-500 mb-8">Sign in to access your account</p>

        <div className="flex flex-col gap-3">
          {providers.map((provider) => (
            <button
              key={provider.id}
              onClick={() => handleLogin(provider.id)}
              className={`flex items-center justify-center gap-3 w-full px-4 py-3 rounded-xl font-medium transition ${provider.color}`}
            >
              <span aria-hidden="true">{provider.icon}</span>
              Continue with {provider.name}
            </button>
          ))}
        </div>

        <p className="mt-6 text-xs text-gray-400">
          By signing in you agree to receive earring-related awesomeness.
        </p>
      </div>
    </div>
  )
}

export default LoginModal
