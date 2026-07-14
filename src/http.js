// 封装 fetch，在每个请求中自动带上 token
const BASE_URL = ''

export async function api(url, options = {}) {
  const token = localStorage.getItem('token')

  const headers = {
    'Content-Type': 'application/json',
    'Cache-Control': 'no-cache',
    ...options.headers,
  }

  // 如果有 token，自动加到请求头
  if (token) {
    headers['Authorization'] = `Bearer ${token}`
  }

  const res = await fetch(`${BASE_URL}${url}`, {
    ...options,
    headers,
  })

  // 如果返回 401/403（未授权），跳转到登录页
  if (res.status === 401 || res.status === 403) {
    localStorage.clear()
    window.location.href = '/login'
    throw new Error('Unauthorized')
  }

  const body = await res.json()

  if(body.code == 200){
    return body.data
  }
  throw new Error(body.msg || 'Request failed')
}