/**
 * Reads a cookie value by name.
 */
function getCookie(name) {
  const match = document.cookie.match(new RegExp('(?:^|; )' + name + '=([^;]*)'))
  return match ? decodeURIComponent(match[1]) : null
}

/**
 * Makes an authenticated fetch request, automatically including the XSRF-TOKEN
 * cookie value in the X-XSRF-TOKEN header for state-changing requests (PUT, POST, DELETE).
 */
export async function apiFetch(url, options = {}) {
  const method = (options.method || 'GET').toUpperCase()
  const headers = { ...(options.headers || {}) }

  if (['POST', 'PUT', 'PATCH', 'DELETE'].includes(method)) {
    const csrfToken = getCookie('XSRF-TOKEN')
    if (csrfToken) {
      headers['X-XSRF-TOKEN'] = csrfToken
    }
  }

  return fetch(url, {
    ...options,
    headers,
    credentials: 'include',
  })
}
