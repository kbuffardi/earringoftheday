import { useState, useEffect } from 'react'
import EarringCarousel from '../components/EarringCarousel'

export default function Home() {
  const [earrings, setEarrings] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    fetch('/api/eotd/today')
      .then((res) => {
        if (!res.ok) throw new Error('Failed to fetch today\'s EOTD')
        return res.json()
      })
      .then((data) => {
        setEarrings(data)
        setLoading(false)
      })
      .catch((err) => {
        setError(err.message)
        setLoading(false)
      })
  }, [])

  return (
    <div className="min-h-screen bg-pink-50">
      {/* Header */}
      <header className="bg-white shadow-sm sticky top-0 z-10">
        <div className="max-w-4xl mx-auto px-4 py-4 flex items-center justify-between">
          <h1 className="text-2xl font-bold text-pink-600">💎 EarringOfTheDay</h1>
          <a
            href="/admin"
            className="text-sm text-gray-400 hover:text-pink-500 transition-colors"
          >
            Admin
          </a>
        </div>
      </header>

      {/* Main content */}
      <main className="max-w-4xl mx-auto px-4 py-10">
        <div className="text-center mb-8">
          <h2 className="text-3xl font-extrabold text-gray-800 mb-2">
            Today&apos;s Earring of the Day
          </h2>
          <p className="text-gray-500">Your daily earring inspiration ✨</p>
        </div>

        {loading && (
          <div className="flex justify-center py-16">
            <div className="animate-spin rounded-full h-12 w-12 border-4 border-pink-400 border-t-transparent" />
          </div>
        )}

        {error && (
          <div className="text-center py-16 text-red-500">
            <p>{error}</p>
          </div>
        )}

        {!loading && !error && earrings.length === 0 && (
          <div className="text-center py-16">
            <p className="text-gray-400 text-lg">No earring featured today — check back soon! 👀</p>
          </div>
        )}

        {!loading && !error && earrings.length > 0 && (
          <EarringCarousel earrings={earrings} />
        )}
      </main>
    </div>
  )
}
