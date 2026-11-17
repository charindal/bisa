import { Link } from "react-router-dom";

export function UnauthorizedPage() {
  return (
    <div className="centered-page">
      <h1>You don't have access to this page</h1>
      <p>If you think this is a mistake, contact your administrator.</p>
      <Link to="/">Back to dashboard</Link>
    </div>
  );
}
