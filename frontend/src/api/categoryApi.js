// Empty base = same origin. In development Vite forwards /api to the backend (see vite.config.js).
// For another backend address, set VITE_API_BASE_URL in a .env file, e.g. http://localhost:8080
const BASE_URL = import.meta.env.VITE_API_BASE_URL ?? '';

const UNREACHABLE =
  'Cannot reach the server. Check that the backend is running on port 8080.';

/**
 * POST /api/category-manager/add
 * Always resolves to { ok, status, message, data, fieldErrors } and never throws.
 */
export async function addCategory({ userId, category, reason }) {
  let response;
  try {
    response = await fetch(`${BASE_URL}/api/category-manager/add`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ userId: Number(userId), category, reason }),
    });
  } catch {
    return { ok: false, status: 0, message: UNREACHABLE, data: null, fieldErrors: {} };
  }

  let body = null;
  try {
    body = await response.json();
  } catch {
    body = null;
  }

  if (response.ok && body?.success) {
    return { ok: true, status: response.status, message: body.message, data: body.data, fieldErrors: {} };
  }

  // The dev proxy answers 5xx with an empty body when the backend is down.
  if (!body && response.status >= 500) {
    return { ok: false, status: response.status, message: UNREACHABLE, data: null, fieldErrors: {} };
  }

  // 400 returns { data: { fieldName: "message" } }
  const fieldErrors =
    response.status === 400 && body?.data && typeof body.data === 'object' ? body.data : {};

  return {
    ok: false,
    status: response.status,
    message: body?.message ?? `Request failed (${response.status}).`,
    data: null,
    fieldErrors,
  };
}
