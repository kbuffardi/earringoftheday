import { useState, useRef, useEffect } from 'react'
import { Link } from 'react-router-dom'

function UserAvatar({ user }) {
  const initials =
    [user.firstName?.[0], user.lastName?.[0]].filter(Boolean).join('').toUpperCase() ||
    user.email?.[0]?.toUpperCase() ||
    '?'

  if (user.avatarUrl) {
    return (
      <img
        src={user.avatarUrl}
        alt={user.firstName || user.email}
        className="w-8 h-8 rounded-full object-cover"
        referrerPolicy="no-referrer"
      />
    )
  }

  return (
    <div className="w-8 h-8 rounded-full bg-pink-200 text-pink-700 flex items-center justify-center text-sm font-bold select-none">
      {initials}
    </div>
  )
}

function Navbar({ user, onLogout, onLoginClick }) {
  const [dropdownOpen, setDropdownOpen] = useState(false)
  const dropdownRef = useRef(null)

  useEffect(() => {
    const handleClickOutside = (e) => {
      if (dropdownRef.current && !dropdownRef.current.contains(e.target)) {
        setDropdownOpen(false)
      }
    }
    document.addEventListener('mousedown', handleClickOutside)
    return () => document.removeEventListener('mousedown', handleClickOutside)
  }, [])

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
              className="flex items-center gap-2 hover:opacity-80 transition"
              aria-label="User menu"
            >
              <UserAvatar user={user} />
              <span className="text-sm font-medium hidden sm:inline">
                {user.firstName || user.email}
              </span>
            </button>
            {dropdownOpen && (
              <div className="absolute right-0 mt-2 w-48 bg-white rounded-xl shadow-lg py-1 z-50 text-gray-800">
                <Link
                  to="/account"
                  className="block px-4 py-2 text-sm hover:bg-pink-50 transition"
                  onClick={() => setDropdownOpen(false)}
                >
                  Account Settings
                </Link>
                {user.role === 'ADMIN' && (
                  <>
                    <Link
                      to="/admin"
                      className="block px-4 py-2 text-sm hover:bg-pink-50 transition"
                      onClick={() => setDropdownOpen(false)}
                    >
                      EOTD Admin
                    </Link>
                    <Link
                      to="/admin/users"
                      className="block px-4 py-2 text-sm hover:bg-pink-50 transition"
                      onClick={() => setDropdownOpen(false)}
                    >
                      Users
                    </Link>
                  </>
                )}
                <hr className="my-1 border-gray-100" />
                <button
                  onClick={() => { setDropdownOpen(false); onLogout() }}
                  className="block w-full text-left px-4 py-2 text-sm text-red-500 hover:bg-pink-50 transition"
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

