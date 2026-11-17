import axios, { type AxiosRequestConfig } from "axios";
import { getAccessToken, setAccessToken } from "./tokenStore";
import type { AuthResponse } from "../auth/types";

const baseURL = import.meta.env.VITE_API_BASE_URL ?? "/api";

export const apiClient = axios.create({
  baseURL,
  withCredentials: true, // sends/receives the httpOnly refresh_token cookie
});

// Separate instance for the refresh call itself, so its own 401s don't re-trigger the interceptor below.
const refreshClient = axios.create({ baseURL, withCredentials: true });

let refreshInFlight: Promise<string> | null = null;

async function refreshAccessToken(): Promise<string> {
  if (!refreshInFlight) {
    refreshInFlight = refreshClient
      .post<AuthResponse>("/auth/refresh")
      .then((res) => {
        setAccessToken(res.data.accessToken);
        return res.data.accessToken;
      })
      .finally(() => {
        refreshInFlight = null;
      });
  }
  return refreshInFlight;
}

apiClient.interceptors.request.use((config) => {
  const token = getAccessToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config as (AxiosRequestConfig & { _retried?: boolean }) | undefined;
    const isUnauthorized = error.response?.status === 401;
    const isAuthEndpoint = originalRequest?.url?.startsWith("/auth/");

    if (isUnauthorized && originalRequest && !originalRequest._retried && !isAuthEndpoint) {
      originalRequest._retried = true;
      try {
        const newToken = await refreshAccessToken();
        originalRequest.headers = {
          ...originalRequest.headers,
          Authorization: `Bearer ${newToken}`,
        };
        return apiClient(originalRequest);
      } catch {
        setAccessToken(null);
        // Let the caller's catch handle redirect-to-login via AuthContext's status.
      }
    }

    return Promise.reject(error);
  }
);
