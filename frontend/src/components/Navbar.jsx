import { useState, useRef, useEffect } from 'react'
import { Link } from 'react-router-dom'

function getUserInitials(user) {
  if (!user) return ''
  const first = user.firstName?.[0] || ''
  const last = user.lastName?.[0] || ''
  const initials = (first + last).toUpperCase()
  return initials || user.email?.[0]?.toUpperCase() || '?'
}

function Navbar({ user, onLogout, onLoginClick }) {
  const [dropdownOpen, setDropdownOpen] = useState(false)
  const dropdownRef = useRef(null)

  useEffect(() => {
    function handleClickOutside(event) {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setDropdownOpen(false)
      }
    }
    document.addEventListener('mousedown', handleClickOutside)
    return () => document.removeEventListener('mousedown', handleClickOutside)
  }, [])

  const initials = getUserInitials(user)

  return (
    <nav className="bg-pink-600 text-white px-6 py-3 flex items-center justify-between shadow-md">
      <Link to="/" className="text-xl font-bold tracking-wide hover:text-pink-100">
        💎 EarringOfTheDay
      </Link>
      <div className="flex items-center gap-4">
        {user ? (
          <div className="relative" ref={dropdownRef}>
            <button
              onClick={() => setDropdownOpen((open) => !open)}
              className="flex items-center justify-center w-9 h-9 rounded-full overflow-hidden focus:outline-none focus:ring-2 focus:ring-white focus:ring-offset-2 focus:ring-offset-pink-600"
              aria-label="User menu"
            >
              {user.avatarUrl ? (
                <img
                  src={user.avatarUrl}
                  alt={user.firstName || user.email}
                  className="w-full h-full object-cover"
                />
              ) : (
                <span className="bg-white text-pink-600 font-bold text-sm w-full h-full flex items-center justify-center">
                  {initials}
                </span>
              )}
            </button>

            {dropdownOpen && (
              <div className="absolute right-0 mt-2 w-48 bg-white rounded-xl shadow-lg py-1 z-50 text-gray-700">
                <div className="px-4 py-2 border-b border-gray-100 text-xs text-gray-400 truncate">
                  {user.email}
                </div>
                <Link
                  to="/account"
                  className="block px-4 py-2 text-sm hover:bg-pink-50"
                  onClick={() => setDropdownOpen(false)}
                >
                  Account Settings
                </Link>
                {user.role === 'ADMIN' && (
                  <>
                    <Link
                      to="/admin"
                      className="block px-4 py-2 text-sm hover:bg-pink-50"
                      onClick={() => setDropdownOpen(false)}
                    >
                      EOTD Admin
                    </Link>
                    <Link
                      to="/admin/users"
                      className="block px-4 py-2 text-sm hover:bg-pink-50"
                      onClick={() => setDropdownOpen(false)}
                    >
                      Users
                    </Link>
                  </>
                )}
                <button
                  onClick={() => { setDropdownOpen(false); onLogout() }}
                  className="block w-full text-left px-4 py-2 text-sm text-red-500 hover:bg-pink-50 border-t border-gray-100"
                >
                  Log out
                </button>
              </div>
            )}
          </div>
        ) : (
          <button
            onClick={onLoginClick}
            className="bg-white text-pink-600 px-4 py-1 rounded-full text-sm font-medium hover:bg-pink-50 transition"
          >
            Log in
          </button>
        )}
      </div>
    </nav>
  )
}

export default Navbar

