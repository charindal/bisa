export type Role = "SUPER_ADMIN" | "ADMIN" | "LECTURER" | "STUDENT";

export interface UserSummary {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  role: Role;
}

export interface AuthResponse {
  accessToken: string;
  expiresInSeconds: number;
  user: UserSummary;
}
