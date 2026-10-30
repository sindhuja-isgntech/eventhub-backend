# eventhub-backend
Backend of the EventHub project

## Account endpoints

- `POST /api/auth/register` creates a `CUSTOMER` or `ORGANIZER` account. The password must be at least 8 characters and is stored as a BCrypt hash.
- `POST /api/auth/login` verifies the submitted email and password against the users table.

Both endpoints return a signed JWT and the account profile (`id`, `name`, `email`, `phone`, and `role`), never the password. Send the token on protected API requests using `Authorization: Bearer <token>`. Tokens expire after one hour by default; configure `JWT_EXPIRATION_SECONDS` to change this. Admin accounts must be provisioned outside public registration; the frontend uses the role returned by login to direct admins to the admin pages.

Set `JWT_SECRET` to a randomly generated secret of at least 32 bytes before starting the backend. For local development, the backend loads a Git-ignored `.env` file from the backend working directory. Create it from PowerShell:

```powershell
$bytes = [byte[]]::new(32)
$rng = [System.Security.Cryptography.RandomNumberGenerator]::Create()
$rng.GetBytes($bytes)
$rng.Dispose()
("JWT_SECRET=" + [Convert]::ToBase64String($bytes)) | Set-Content -NoNewline .env
```

Then run `.\mvnw.cmd spring-boot:run` from the backend directory. Alternatively, set `JWT_SECRET` in the process environment or deployment secret store; environment variables take precedence over `.env`. Keep the secret private and use the same value across backend instances. The frontend stores the JWT in `sessionStorage` and clears it when the user signs out.

Password reset uses `POST /api/auth/forgot-password` and `POST /api/auth/reset-password`. Reset links are sent by SMTP, expire after 15 minutes, and are single-use. Configure SMTP using `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`, `MAIL_SMTP_AUTH`, and `MAIL_SMTP_STARTTLS`. Set `FRONTEND_URL` to the frontend origin used in reset links (defaults to `http://localhost:5173`). Do not commit SMTP credentials.
