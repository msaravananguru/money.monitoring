import { useRef, useState } from 'react';
import { addCategory } from '../api/categoryApi.js';

const LIMITS = { category: 100, reason: 255 };
const EMPTY = { userId: '', category: '', reason: '' };

function validate(values) {
  const errors = {};
  if (!/^[1-9]\d*$/.test(values.userId.trim())) {
    errors.userId = 'Enter the user ID as a positive number.';
  }
  const category = values.category.trim();
  if (!category) errors.category = 'Enter a category.';
  else if (category.length > LIMITS.category) {
    errors.category = `Use ${LIMITS.category} characters or fewer.`;
  }
  const reason = values.reason.trim();
  if (!reason) errors.reason = 'Enter a reason.';
  else if (reason.length > LIMITS.reason) {
    errors.reason = `Use ${LIMITS.reason} characters or fewer.`;
  }
  return errors;
}

export default function CategoryAddForm() {
  const [values, setValues] = useState(EMPTY);
  const [errors, setErrors] = useState({});
  const [status, setStatus] = useState(null); // { type: 'success' | 'error', text }
  const [submitting, setSubmitting] = useState(false);
  const reasonRef = useRef(null);

  function handleChange(event) {
    const { name, value } = event.target;
    setValues((current) => ({ ...current, [name]: value }));
    if (errors[name]) setErrors((current) => ({ ...current, [name]: undefined }));
    if (status) setStatus(null);
  }

  async function handleSubmit(event) {
    event.preventDefault();
    if (submitting) return;

    const found = validate(values);
    setErrors(found);
    setStatus(null);
    if (Object.keys(found).length > 0) return;

    setSubmitting(true);
    const result = await addCategory({
      userId: values.userId.trim(),
      category: values.category.trim(),
      reason: values.reason.trim(),
    });
    setSubmitting(false);

    if (result.ok) {
      setStatus({
        type: 'success',
        text: `Category added. S.No ${result.data.serialNo}.`,
      });
      // Keep user and category: one category can hold many reasons.
      setValues((current) => ({ ...current, reason: '' }));
      reasonRef.current?.focus();
      return;
    }

    // A duplicate (409) belongs to the reason field; other errors go to the status line.
    if (result.status === 409) setErrors({ reason: result.message });
    else setErrors(result.fieldErrors);
    setStatus(
      result.status === 409 || Object.keys(result.fieldErrors).length > 0
        ? null
        : { type: 'error', text: result.message }
    );
  }

  return (
    <form className="card" onSubmit={handleSubmit} noValidate>
      <h1 className="title">Add category</h1>
      <p className="lead">Save a reason under a category for a user.</p>

      <div className="field">
        <label htmlFor="userId">User ID</label>
        <input
          id="userId"
          name="userId"
          type="text"
          inputMode="numeric"
          autoComplete="off"
          placeholder="101"
          value={values.userId}
          onChange={handleChange}
          aria-invalid={Boolean(errors.userId)}
          aria-describedby={errors.userId ? 'userId-error' : undefined}
        />
        {errors.userId && (
          <p className="error" id="userId-error">{errors.userId}</p>
        )}
      </div>

      <div className="field">
        <label htmlFor="category">Category</label>
        <input
          id="category"
          name="category"
          type="text"
          autoComplete="off"
          placeholder="Food"
          maxLength={LIMITS.category}
          value={values.category}
          onChange={handleChange}
          aria-invalid={Boolean(errors.category)}
          aria-describedby={errors.category ? 'category-error' : undefined}
        />
        {errors.category && (
          <p className="error" id="category-error">{errors.category}</p>
        )}
      </div>

      <div className="field">
        <div className="label-row">
          <label htmlFor="reason">Reason</label>
          <span className="count" aria-hidden="true">
            {values.reason.length}/{LIMITS.reason}
          </span>
        </div>
        <input
          id="reason"
          name="reason"
          type="text"
          autoComplete="off"
          placeholder="Monthly groceries"
          maxLength={LIMITS.reason}
          ref={reasonRef}
          value={values.reason}
          onChange={handleChange}
          aria-invalid={Boolean(errors.reason)}
          aria-describedby={errors.reason ? 'reason-error' : undefined}
        />
        {errors.reason && (
          <p className="error" id="reason-error">{errors.reason}</p>
        )}
      </div>

      <button className="submit" type="submit" disabled={submitting}>
        {submitting ? 'Adding…' : 'Add category'}
      </button>

      <div className="status-area" aria-live="polite">
        {status && (
          <p className={`status status-${status.type}`} role={status.type === 'error' ? 'alert' : 'status'}>
            {status.text}
          </p>
        )}
      </div>
    </form>
  );
}
