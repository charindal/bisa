import { createContext, useContext, useEffect, useState, type ReactNode } from "react";
import { apiClient } from "../lib/apiClient";
import { setAccessToken } from "../lib/tokenStore";
import type { AuthResponse, UserSummary } from "./types";

type AuthStatus = "loading" | "authenticated" | "unauthenticated";

interface AuthContextValue {
  status: AuthStatus;
  user: UserSummary | null;
  login: (email: string, password: string) => Promise<void>;
  logout: () => Promise<void>;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [status, setStatus] = useState<AuthStatus>("loading");
  const [user, setUser] = useState<UserSummary | null>(null);

  useEffect(() => {
    // Access tokens live in memory only, so a page reload needs to re-establish the
    // session from the httpOnly refresh cookie before any protected route can render.
    apiClient
      .post<AuthResponse>("/auth/refresh")
      .then((res) => {
        setAccessToken(res.data.accessToken);
        setUser(res.data.user);
        setStatus("authenticated");
      })
      .catch(() => {
        setAccessToken(null);
        setUser(null);
        setStatus("unauthenticated");
      });
  }, []);

  async function login(email: string, password: string) {
    const res = await apiClient.post<AuthResponse>("/auth/login", { email, password });
    setAccessToken(res.data.accessToken);
    setUser(res.data.user);
    setStatus("authenticated");
  }

  async function logout() {
    try {
      await apiClient.post("/auth/logout");
    } finally {
      setAccessToken(null);
      setUser(null);
      setStatus("unauthenticated");
    }
  }

  return (
    <AuthContext.Provider value={{ status, user, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext);
  if (!ctx) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return ctx;
}
