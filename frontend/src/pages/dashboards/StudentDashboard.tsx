export function StudentDashboard() {
  return (
    <>
      <h2>My academic record</h2>
      <p className="muted">
        A read-only view of your own profile, enrolments, and results. Nothing here can be
        edited by you.
      </p>

      <div className="tile-grid">
        <div className="tile tile-disabled">
          <span className="tile-badge">Planned</span>
          <h3>My profile</h3>
          <p>Your programme, mode of study, and contact details on file.</p>
        </div>

        <div className="tile tile-disabled">
          <span className="tile-badge">Planned</span>
          <h3>My enrolments</h3>
          <p>Courses you're enrolled in, this term and historically.</p>
        </div>

        <div className="tile tile-disabled">
          <span className="tile-badge">Planned</span>
          <h3>My results</h3>
          <p>Assessment grades as recorded by your lecturers.</p>
        </div>
      </div>
    </>
  );
}
