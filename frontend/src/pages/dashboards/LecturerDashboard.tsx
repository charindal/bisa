export function LecturerDashboard() {
  return (
    <>
      <h2>My teaching</h2>
      <p className="muted">
        Your class rosters and grade capture will appear here, scoped to the classes you're
        assigned to.
      </p>

      <div className="tile-grid">
        <div className="tile tile-disabled">
          <span className="tile-badge">Planned</span>
          <h3>My classes</h3>
          <p>Rosters for every class you're assigned to teach this term.</p>
        </div>

        <div className="tile tile-disabled">
          <span className="tile-badge">Planned</span>
          <h3>Grade capture</h3>
          <p>Record and update assessment grades for your own classes only.</p>
        </div>

        <div className="tile tile-disabled">
          <span className="tile-badge">Planned</span>
          <h3>Student contact info</h3>
          <p>Read-only contact details for students in your classes.</p>
        </div>
      </div>
    </>
  );
}
