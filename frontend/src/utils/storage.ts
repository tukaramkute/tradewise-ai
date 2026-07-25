const ACCESS_TOKEN = 'tradewise.accessToken'
const REFRESH_TOKEN = 'tradewise.refreshToken'
const USER_ID = 'tradewise.userId'

export const tokenStorage = {
  getAccess: () => localStorage.getItem(ACCESS_TOKEN),
  getRefresh: () => localStorage.getItem(REFRESH_TOKEN),
  getUserId: () => localStorage.getItem(USER_ID),
  set(accessToken: string, refreshToken: string, userId?: string) {
    localStorage.setItem(ACCESS_TOKEN, accessToken)
    localStorage.setItem(REFRESH_TOKEN, refreshToken)
    if (userId) localStorage.setItem(USER_ID, userId)
  },
  clear() { localStorage.removeItem(ACCESS_TOKEN); localStorage.removeItem(REFRESH_TOKEN); localStorage.removeItem(USER_ID) },
}