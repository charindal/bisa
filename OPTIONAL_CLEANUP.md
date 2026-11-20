# Optional Cleanup: Unused Dashboard Files

The following files are no longer imported after removing Student and Lecturer roles.
They are safe to delete in a future cleanup commit.

## Files to Remove

```
frontend/src/pages/dashboards/LecturerDashboard.tsx
frontend/src/pages/dashboards/StudentDashboard.tsx
```

## Why They're Unused

- `DashboardPage.tsx` previously imported and mapped these dashboards to the role types
- With LECTURER and STUDENT roles removed from the type, these imports were removed
- The dashboards are no longer part of the application flow
- They are not referenced anywhere else in the codebase

## Removal Instructions

When ready to clean up:

```bash
git rm frontend/src/pages/dashboards/LecturerDashboard.tsx
git rm frontend/src/pages/dashboards/StudentDashboard.tsx
git commit -m "Remove unused Lecturer and Student dashboard components"
```

This cleanup is optional and can be deferred. The application will function correctly with or without these files, but removing them reduces code clutter.
