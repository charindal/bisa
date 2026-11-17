/**
 * The access token lives in memory only (never localStorage) to limit XSS exposure.
 * It is lost on a full page reload by design — AuthProvider restores it on mount
 * by calling /auth/refresh, which relies on the httpOnly refresh cookie instead.
 */
let currentAccessToken: string | null = null;

export function getAccessToken(): string | null {
  return currentAccessToken;
}

export function setAccessToken(token: string | null): void {
  currentAccessToken = token;
}
