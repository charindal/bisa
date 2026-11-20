import { useEffect, useState, type FormEvent } from "react";
import { isAxiosError } from "axios";
import { Link } from "react-router-dom";
import {
  createUser,
  listUsers,
  updateUserRole,
  updateUserStatus,
  type UserRecord,
} from "../../api/users";
import { useAuth } from "../../auth/AuthContext";
import type { Role } from "../../auth/types";

const ROLES: Role[] = ["SUPER_ADMIN", "ADMIN"];

export function UsersPage() {
  const { user: currentUser } = useAuth();
  const [users, setUsers] = useState<UserRecord[]>([]);
  const [loading, setLoading] = useState(true);
  const [loadError, setLoadError] = useState<string | null>(null);
  const [actionError, setActionError] = useState<string | null>(null);

  useEffect(() => {
    refresh();
  }, []);

  async function refresh() {
    setLoading(true);
    setLoadError(null);
    try {
      setUsers(await listUsers());
    } catch {
      setLoadError("Could not load users.");
    } finally {
      setLoading(false);
    }
  }

  async function handleRoleChange(id: string, role: Role) {
    setActionError(null);
    try {
      const updated = await updateUserRole(id, role);
      setUsers((prev) => prev.map((u) => (u.id === id ? updated : u)));
    } catch (err) {
      setActionError(extractMessage(err, "Could not change that user's role."));
    }
  }

  async function handleToggleEnabled(user: UserRecord) {
    setActionError(null);
    try {
      const updated = await updateUserStatus(user.id, !user.enabled);
      setUsers((prev) => prev.map((u) => (u.id === user.id ? updated : u)));
    } catch (err) {
      setActionError(extractMessage(err, "Could not update that user's status."));
    }
  }

  return (
    <div className="app-shell">
      <header className="app-header">
        <h1>Users &amp; roles</h1>
        <Link to="/">← Back to dashboard</Link>
      </header>

      <main className="app-main">
        <CreateUserForm onCreated={(created) => setUsers((prev) => [created, ...prev])} />

        {actionError && <p className="form-error">{actionError}</p>}
        {loadError && <p className="form-error">{loadError}</p>}

        {loading ? (
          <p>Loading users…</p>
        ) : (
          <table className="data-table">
            <thead>
              <tr>
                <th>Name</th>
                <th>Email</th>
                <th>Role</th>
                <th>Status</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {users.map((u) => {
                const isSelf = u.id === currentUser?.id;
                return (
                  <tr key={u.id}>
                    <td>
                      {u.firstName} {u.lastName}
                    </td>
                    <td>{u.email}</td>
                    <td>
                      <select
                        value={u.role}
                        disabled={isSelf}
                        onChange={(e) => handleRoleChange(u.id, e.target.value as Role)}
                      >
                        {ROLES.map((r) => (
                          <option key={r} value={r}>
                            {r}
                          </option>
                        ))}
                      </select>
                    </td>
                    <td>
                      <span className={`pill ${u.enabled ? "pill-active" : "pill-inactive"}`}>
                        {u.enabled ? "Active" : "Disabled"}
                      </span>
                    </td>
                    <td>
                      <button disabled={isSelf} onClick={() => handleToggleEnabled(u)}>
                        {u.enabled ? "Deactivate" : "Activate"}
                      </button>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        )}
      </main>
    </div>
  );
}

function CreateUserForm({ onCreated }: { onCreated: (user: UserRecord) => void }) {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [role, setRole] = useState<Role>("ADMIN");
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    setError(null);
    setSubmitting(true);
    try {
      const created = await createUser({ email, password, firstName, lastName, role });
      onCreated(created);
      setEmail("");
      setPassword("");
      setFirstName("");
      setLastName("");
      setRole("ADMIN");
    } catch (err) {
      setError(extractMessage(err, "Could not create that user."));
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <form className="inline-form" onSubmit={handleSubmit}>
      <h2>Add a user</h2>
      <div className="inline-form-row">
        <input
          placeholder="First name"
          value={firstName}
          onChange={(e) => setFirstName(e.target.value)}
          required
        />
        <input
          placeholder="Last name"
          value={lastName}
          onChange={(e) => setLastName(e.target.value)}
          required
        />
        <input
          type="email"
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />
        <input
          type="password"
          placeholder="Temporary password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          minLength={8}
          required
        />
        <select value={role} onChange={(e) => setRole(e.target.value as Role)}>
          {ROLES.map((r) => (
            <option key={r} value={r}>
              {r}
            </option>
          ))}
        </select>
        <button type="submit" disabled={submitting}>
          {submitting ? "Adding…" : "Add user"}
        </button>
      </div>
      {error && <p className="form-error">{error}</p>}
    </form>
  );
}

function extractMessage(err: unknown, fallback: string): string {
  if (isAxiosError(err) && typeof err.response?.data?.message === "string") {
    return err.response.data.message;
  }
  return fallback;
}
