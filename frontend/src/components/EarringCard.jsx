import Markdown from 'react-markdown'

export default function EarringCard({ eotd }) {
  const handleBuy = (e) => {
    e.preventDefault()
    // Track click via redirect endpoint
    window.open(`/api/eotd/${eotd.id}/redirect`, '_blank', 'noopener,noreferrer')
  }

  return (
    <div className="bg-white rounded-2xl shadow-md overflow-hidden flex flex-col">
      {/* Instagram embed */}
      {eotd.instagramPostUrl && (
        <div className="flex justify-center bg-gray-50 p-4">
          <blockquote
            className="instagram-media"
            data-instgrm-permalink={eotd.instagramPostUrl}
            data-instgrm-version="14"
            style={{ maxWidth: '540px', width: '100%', minWidth: '326px' }}
          />
        </div>
      )}

      {/* Product image fallback when no Instagram post */}
      {!eotd.instagramPostUrl && eotd.productImageUrl && (
        <div className="flex justify-center bg-gray-50 p-4">
          <img
            src={eotd.productImageUrl}
            alt={eotd.productName || 'Earring'}
            className="max-h-64 object-contain rounded-lg"
          />
        </div>
      )}

      <div className="p-6 flex flex-col flex-1">
        {/* Brand and product name */}
        <div className="mb-3">
          {eotd.brand && (
            <span className="text-xs font-semibold uppercase tracking-widest text-pink-400">
              {eotd.brand}
            </span>
          )}
          {eotd.productName && (
            <h2 className="text-xl font-bold text-gray-800 mt-1">{eotd.productName}</h2>
          )}
        </div>

        {/* Instructions (markdown) */}
        {eotd.instructions && (
          <div className="prose prose-sm text-gray-600 mb-4 flex-1">
            <Markdown>{eotd.instructions}</Markdown>
          </div>
        )}

        {/* Product image (shown alongside Instagram embed if both exist) */}
        {eotd.instagramPostUrl && eotd.productImageUrl && (
          <div className="mb-4 flex justify-center">
            <img
              src={eotd.productImageUrl}
              alt={eotd.productName || 'Earring'}
              className="max-h-40 object-contain rounded-lg border"
            />
          </div>
        )}

        {/* Buy button */}
        {eotd.referralLink && (
          <a
            href={`/api/eotd/${eotd.id}/redirect`}
            onClick={handleBuy}
            target="_blank"
            rel="noopener noreferrer"
            className="mt-auto block text-center bg-pink-500 hover:bg-pink-600 text-white font-semibold py-3 px-6 rounded-xl transition-colors"
          >
            Shop Now 💎
          </a>
        )}
      </div>
    </div>
  )
}
