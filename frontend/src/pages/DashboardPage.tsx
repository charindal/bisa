import type { ComponentType } from "react";
import { useAuth } from "../auth/AuthContext";
import { SuperAdminDashboard } from "./dashboards/SuperAdminDashboard";
import { AdminDashboard } from "./dashboards/AdminDashboard";
import { LecturerDashboard } from "./dashboards/LecturerDashboard";
import { StudentDashboard } from "./dashboards/StudentDashboard";
import type { Role } from "../auth/types";

const ROLE_LABELS: Record<Role, string> = {
  SUPER_ADMIN: "Super Admin",
  ADMIN: "Admin",
  LECTURER: "Lecturer",
  STUDENT: "Student",
};

const DASHBOARDS: Record<Role, ComponentType> = {
  SUPER_ADMIN: SuperAdminDashboard,
  ADMIN: AdminDashboard,
  LECTURER: LecturerDashboard,
  STUDENT: StudentDashboard,
};

export function DashboardPage() {
  const { user, logout } = useAuth();
  if (!user) return null;

  const RoleDashboard = DASHBOARDS[user.role];

  return (
    <div className="app-shell">
      <header className="app-header">
        <h1>BISA SIS</h1>
        <div className="app-header-user">
          <span>
            {user.firstName} {user.lastName} · {ROLE_LABELS[user.role]}
          </span>
          <button onClick={() => logout()}>Sign out</button>
        </div>
      </header>

      <main className="app-main">
        <RoleDashboard />
      </main>
    </div>
  );
}
