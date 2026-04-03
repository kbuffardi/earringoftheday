function LoginPage() {
  const providers = [
    { id: 'google', name: 'Google', icon: '🔵', color: 'bg-white border border-gray-300 text-gray-700 hover:bg-gray-50' },
    { id: 'microsoft', name: 'Microsoft', icon: '🟦', color: 'bg-white border border-gray-300 text-gray-700 hover:bg-gray-50' },
    { id: 'apple', name: 'Apple', icon: '⬛', color: 'bg-black text-white hover:bg-gray-900' },
    { id: 'facebook', name: 'Facebook', icon: '🔷', color: 'bg-blue-600 text-white hover:bg-blue-700' },
  ]

  const handleLogin = (providerId) => {
    window.location.href = `/oauth2/authorization/${providerId}`
  }

  return (
    <div className="min-h-screen bg-pink-50 flex flex-col items-center justify-center px-4">
      <div className="bg-white rounded-2xl shadow-lg p-8 max-w-sm w-full text-center">
        <h1 className="text-3xl font-bold text-pink-600 mb-2">💎 EarringOfTheDay</h1>
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

export default LoginPage
