import { Link } from 'react-router-dom'

function Navbar({ user, onLogout }) {
  return (
    <nav className="bg-pink-600 text-white px-6 py-3 flex items-center justify-between shadow-md">
      <Link to="/" className="text-xl font-bold tracking-wide hover:text-pink-100">
        💎 EarringOfTheDay
      </Link>
      <div className="flex items-center gap-4">
        {user ? (
          <>
            <Link to="/account" className="hover:text-pink-100 text-sm font-medium">
              {user.firstName || user.email}
            </Link>
            {user.role === 'ADMIN' && (
              <Link to="/admin" className="hover:text-pink-100 text-sm font-medium">
                Admin
              </Link>
            )}
            <button
              onClick={onLogout}
              className="bg-white text-pink-600 px-3 py-1 rounded-full text-sm font-medium hover:bg-pink-50 transition"
            >
              Log out
            </button>
          </>
        ) : (
          <Link
            to="/login"
            className="bg-white text-pink-600 px-4 py-1 rounded-full text-sm font-medium hover:bg-pink-50 transition"
          >
            Log in
          </Link>
        )}
      </div>
    </nav>
  )
}

export default Navbar
