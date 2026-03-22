import { useState, useEffect } from 'react'

function App() {
  const [status, setStatus] = useState(null)

  useEffect(() => {
    fetch('/api/health')
      .then((res) => res.json())
      .then((data) => setStatus(data))
      .catch(() => setStatus({ status: 'unavailable' }))
  }, [])

  return (
    <div className="min-h-screen bg-pink-50 flex flex-col items-center justify-center">
      <header className="mb-8 text-center">
        <h1 className="text-5xl font-bold text-pink-600 mb-2">💎 EarringOfTheDay</h1>
        <p className="text-gray-500 text-lg">Your daily earring inspiration</p>
      </header>

      <main className="bg-white rounded-2xl shadow-lg p-8 max-w-md w-full text-center">
        <p className="text-gray-700 mb-4">Welcome to EOTD — discover your earring of the day!</p>

        {status && (
          <div
            className={`mt-4 px-4 py-2 rounded-full text-sm font-medium inline-block ${
              status.status === 'ok'
                ? 'bg-green-100 text-green-700'
                : 'bg-red-100 text-red-700'
            }`}
          >
            Backend: {status.status === 'ok' ? '✅ connected' : '❌ unavailable'}
          </div>
        )}
      </main>
    </div>
  )
}

export default App
