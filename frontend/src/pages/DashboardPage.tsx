import { Link } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";

const ROLE_LABELS: Record<string, string> = {
  SUPER_ADMIN: "Super Admin",
  ADMIN: "Admin",
  LECTURER: "Lecturer",
  STUDENT: "Student",
};

export function DashboardPage() {
  const { user, logout } = useAuth();

  return (
    <div className="app-shell">
      <header className="app-header">
        <h1>BISA SIS</h1>
        <div className="app-header-user">
          <span>
            {user?.firstName} {user?.lastName} · {ROLE_LABELS[user?.role ?? ""] ?? user?.role}
          </span>
          <button onClick={() => logout()}>Sign out</button>
        </div>
      </header>

      <main className="app-main">
        <h2>Welcome back, {user?.firstName}.</h2>
        <p className="muted">
          This is the Identity &amp; Access module. Student records, admissions, and the other
          modules from the overview will slot in here next.
        </p>

        {user?.role === "SUPER_ADMIN" && (
          <Link className="card-link" to="/admin/users">
            Manage users &amp; roles →
          </Link>
        )}
      </main>
    </div>
  );
}
