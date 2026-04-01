import { useState, useEffect, useRef } from 'react'
import EarringCard from './EarringCard'

export default function EarringCarousel({ earrings }) {
  const [current, setCurrent] = useState(0)
  const scriptLoadedRef = useRef(false)

  const prev = () => setCurrent((c) => (c === 0 ? earrings.length - 1 : c - 1))
  const next = () => setCurrent((c) => (c === earrings.length - 1 ? 0 : c + 1))

  // Load Instagram embed script once the earrings are displayed
  useEffect(() => {
    if (scriptLoadedRef.current) {
      // Re-process embeds when navigating slides
      if (window.instgrm) {
        window.instgrm.Embeds.process()
      }
      return
    }
    const existing = document.getElementById('instagram-embed-script')
    if (!existing) {
      const script = document.createElement('script')
      script.id = 'instagram-embed-script'
      script.src = 'https://www.instagram.com/embed.js'
      script.async = true
      script.defer = true
      document.body.appendChild(script)
      scriptLoadedRef.current = true
    }
  }, [current])

  if (!earrings || earrings.length === 0) return null

  return (
    <div className="w-full max-w-2xl mx-auto">
      {/* Single earring - no controls needed */}
      {earrings.length === 1 ? (
        <EarringCard eotd={earrings[0]} />
      ) : (
        <>
          {/* Slide indicator */}
          <div className="flex items-center justify-center gap-2 mb-4">
            {earrings.map((_, i) => (
              <button
                key={i}
                onClick={() => setCurrent(i)}
                className={`w-2.5 h-2.5 rounded-full transition-colors ${
                  i === current ? 'bg-pink-500' : 'bg-pink-200'
                }`}
                aria-label={`Go to earring ${i + 1}`}
              />
            ))}
          </div>

          {/* Card with prev/next buttons */}
          <div className="relative flex items-center gap-2">
            <button
              onClick={prev}
              className="flex-shrink-0 bg-white border border-pink-200 hover:bg-pink-50 text-pink-500 rounded-full w-10 h-10 flex items-center justify-center shadow transition-colors"
              aria-label="Previous earring"
            >
              ‹
            </button>

            <div className="flex-1">
              <EarringCard eotd={earrings[current]} />
            </div>

            <button
              onClick={next}
              className="flex-shrink-0 bg-white border border-pink-200 hover:bg-pink-50 text-pink-500 rounded-full w-10 h-10 flex items-center justify-center shadow transition-colors"
              aria-label="Next earring"
            >
              ›
            </button>
          </div>

          <p className="text-center text-sm text-gray-400 mt-3">
            {current + 1} of {earrings.length}
          </p>
        </>
      )}
    </div>
  )
}
