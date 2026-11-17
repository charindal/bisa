import { apiClient } from "../lib/apiClient";
import type { Role } from "../auth/types";

export interface UserRecord {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  role: Role;
  enabled: boolean;
  createdAt: string;
}

interface Page<T> {
  content: T[];
  totalElements: number;
}

export async function listUsers(): Promise<UserRecord[]> {
  const res = await apiClient.get<Page<UserRecord>>("/users", { params: { size: 100 } });
  return res.data.content;
}

export interface CreateUserInput {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  role: Role;
}

export async function createUser(input: CreateUserInput): Promise<UserRecord> {
  const res = await apiClient.post<UserRecord>("/users", input);
  return res.data;
}

export async function updateUserRole(id: string, role: Role): Promise<UserRecord> {
  const res = await apiClient.patch<UserRecord>(`/users/${id}/role`, { role });
  return res.data;
}

export async function updateUserStatus(id: string, enabled: boolean): Promise<UserRecord> {
  const res = await apiClient.patch<UserRecord>(`/users/${id}/status`, { enabled });
  return res.data;
}
