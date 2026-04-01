import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'

function HomePage({ user }) {
  const [status, setStatus] = useState(null)

  useEffect(() => {
    fetch('/api/health')
      .then((res) => res.json())
      .then((data) => setStatus(data))
      .catch(() => setStatus({ status: 'unavailable' }))
  }, [])

  return (
    <div className="min-h-screen bg-pink-50 flex flex-col items-center justify-center px-4">
      <header className="mb-8 text-center">
        <h1 className="text-5xl font-bold text-pink-600 mb-2">💎 EarringOfTheDay</h1>
        <p className="text-gray-500 text-lg">Your daily earring inspiration</p>
      </header>

      <main className="bg-white rounded-2xl shadow-lg p-8 max-w-md w-full text-center">
        {user ? (
          <div>
            <p className="text-gray-700 mb-2">
              Welcome back, <span className="font-semibold text-pink-600">{user.firstName || user.email}</span>!
            </p>
            <p className="text-gray-500 text-sm mb-4">
              Check out today&apos;s earring of the day.
            </p>
            <Link
              to="/account"
              className="inline-block mt-2 text-pink-600 underline text-sm hover:text-pink-800"
            >
              Manage account settings
            </Link>
          </div>
        ) : (
          <div>
            <p className="text-gray-700 mb-4">Welcome to EOTD — discover your earring of the day!</p>
            <Link
              to="/login"
              className="inline-block bg-pink-600 text-white px-6 py-2 rounded-full font-medium hover:bg-pink-700 transition"
            >
              Log in
            </Link>
          </div>
        )}

        {status && (
          <div
            className={`mt-6 px-4 py-2 rounded-full text-sm font-medium inline-block ${
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

export default HomePage
