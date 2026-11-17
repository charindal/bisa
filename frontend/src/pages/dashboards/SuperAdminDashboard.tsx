import { Link } from "react-router-dom";

export function SuperAdminDashboard() {
  return (
    <>
      <h2>System overview</h2>
      <p className="muted">
        You manage who can access BISA SIS and what they can do. Academic data modules land here
        as they're built.
      </p>

      <div className="tile-grid">
        <Link className="tile" to="/admin/users">
          <span className="tile-badge tile-badge-live">Live</span>
          <h3>Users &amp; roles</h3>
          <p>Create accounts, assign roles, activate or deactivate staff and students.</p>
        </Link>

        <div className="tile tile-disabled">
          <span className="tile-badge">Planned</span>
          <h3>Audit log</h3>
          <p>Who changed what, and when — every role and status change is already being recorded.</p>
        </div>

        <div className="tile tile-disabled">
          <span className="tile-badge">Planned</span>
          <h3>System configuration</h3>
          <p>Institution settings, academic calendar, integrations.</p>
        </div>

        <div className="tile tile-disabled">
          <span className="tile-badge">Planned</span>
          <h3>Programmes &amp; courses</h3>
          <p>Catalogue of programmes, courses, and NQF mappings.</p>
        </div>
      </div>
    </>
  );
}
