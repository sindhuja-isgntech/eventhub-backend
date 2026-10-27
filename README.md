# eventhub-backend
Backend of the EventHub project

## Account endpoints

- `POST /api/auth/register` creates a `CUSTOMER` or `ORGANIZER` account. The password must be at least 8 characters and is stored as a BCrypt hash.
- `POST /api/auth/login` verifies the submitted email and password against the users table.

Both endpoints return the account profile (`id`, `name`, `email`, `phone`, and `role`), never the password. Admin accounts must be provisioned outside public registration; the frontend uses the role returned by login to direct admins to the admin pages.

Password reset uses `POST /api/auth/forgot-password` and `POST /api/auth/reset-password`. Reset links are sent by SMTP, expire after 15 minutes, and are single-use. Configure SMTP using `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`, `MAIL_SMTP_AUTH`, and `MAIL_SMTP_STARTTLS`. Set `FRONTEND_URL` to the frontend origin used in reset links (defaults to `http://localhost:5173`). Do not commit SMTP credentials.
