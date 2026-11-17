export function AdminDashboard() {
  return (
    <>
      <h2>Registry overview</h2>
      <p className="muted">
        Student records, admissions, and the course catalogue will live here. The Identity &amp;
        Access module is built — the rest of the registry modules come next.
      </p>

      <div className="tile-grid">
        <div className="tile tile-disabled">
          <span className="tile-badge">Planned</span>
          <h3>Student records</h3>
          <p>Search and manage every student's profile and status.</p>
        </div>

        <div className="tile tile-disabled">
          <span className="tile-badge">Planned</span>
          <h3>Admissions</h3>
          <p>Review applications and record admission decisions.</p>
        </div>

        <div className="tile tile-disabled">
          <span className="tile-badge">Planned</span>
          <h3>Programmes &amp; courses</h3>
          <p>Maintain the course catalogue and programme structures.</p>
        </div>

        <div className="tile tile-disabled">
          <span className="tile-badge">Planned</span>
          <h3>Classes &amp; enrolments</h3>
          <p>Schedule classes and manage student enrolments.</p>
        </div>

        <div className="tile tile-disabled">
          <span className="tile-badge">Planned</span>
          <h3>Reporting &amp; exports</h3>
          <p>Registry-wide reports and data exports.</p>
        </div>
      </div>
    </>
  );
}
