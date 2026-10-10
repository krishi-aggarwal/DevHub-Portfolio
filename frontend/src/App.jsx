import { useEffect, useState } from "react";

const API = import.meta.env.VITE_API_URL;
const EMPTY = { title: "", description: "", techStack: "", githubUrl: "", liveUrl: "" };

// only http(s) links are allowed (blocks javascript: links)
const isUrl = (u) => !u || /^https?:\/\/\S+$/i.test(u);

export default function App() {
  const [projects, setProjects] = useState([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState(null);
  const [fieldErrors, setFieldErrors] = useState({});
  const [form, setForm] = useState(EMPTY);
  const [editingId, setEditingId] = useState(null);
  const [apiUp, setApiUp] = useState(null);

  const fe = (k) => fieldErrors[k] && <p className="field-error">{fieldErrors[k]}</p>;

  async function load() {
    try {
      setLoading(true);
      const res = await fetch(`${API}/api/projects`);
      if (!res.ok) throw new Error(`Server returned ${res.status}`);
      setProjects(await res.json());
      setError(null);
    } catch (e) {
      setError(e.message === "Failed to fetch" ? "Cannot reach the server. It may be waking up, try again in a minute." : e.message);
    } finally {
      setLoading(false);
    }
  }

  async function checkHealth() {
    try {
      const res = await fetch(`${API}/actuator/health`);
      setApiUp((await res.json()).status === "UP");
    } catch {
      setApiUp(false);
    }
  }

  useEffect(() => {
    load();
    checkHealth();
  }, []);

  const change = (key) => (e) => setForm({ ...form, [key]: e.target.value });

  function startEdit(p) {
    setEditingId(p.id);
    setFieldErrors({});
    setError(null);
    setForm({
      title: p.title || "",
      description: p.description || "",
      techStack: (p.techStack || []).join(", "),
      githubUrl: p.githubUrl || "",
      liveUrl: p.liveUrl || "",
    });
    window.scrollTo({ top: 0, behavior: "smooth" });
  }

  function cancelEdit() {
    setEditingId(null);
    setForm(EMPTY);
    setError(null);
    setFieldErrors({});
  }

  async function submit(e) {
    e.preventDefault();
    const body = {
      ...form,
      techStack: form.techStack.split(",").map((t) => t.trim()).filter(Boolean),
    };

    // quick client-side check (convenience only, the server is the real gate)
    const local = {};
    if (!body.title.trim()) local.title = "Title is required";
    if (!body.description.trim()) local.description = "Description is required";
    if (!isUrl(body.githubUrl.trim())) local.githubUrl = "Must start with http:// or https://";
    if (!isUrl(body.liveUrl.trim())) local.liveUrl = "Must start with http:// or https://";
    if (Object.keys(local).length) {
      setFieldErrors(local);
      setError(null);
      return;
    }

    const url = editingId ? `${API}/api/projects/${editingId}` : `${API}/api/projects`;
    setSaving(true);
    try {
      const res = await fetch(url, {
        method: editingId ? "PUT" : "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(body),
      });
      if (!res.ok) {
        const data = await res.json().catch(() => ({}));
        const errs = {};
        for (const [k, v] of Object.entries(data.fields || {})) errs[k.split("[")[0]] = v;
        setFieldErrors(errs);
        setError(data.fields ? "Please fix the highlighted fields" : data.error || "Request failed");
        return;
      }
      cancelEdit();
      load();
    } catch {
      setError("Cannot reach the server. Is the API running?");
    } finally {
      setSaving(false);
    }
  }

  async function remove(p) {
    if (!window.confirm(`Delete "${p.title}"?`)) return;
    try {
      const res = await fetch(`${API}/api/projects/${p.id}`, { method: "DELETE" });
      if (!res.ok) throw new Error(`Delete failed (${res.status})`);
      if (editingId === p.id) cancelEdit();
      load();
    } catch (err) {
      setError(err.message);
    }
  }

  const status = apiUp === null ? "checking" : apiUp ? "online" : "offline";

  return (
    <div className="page">
      <header className="header">
        <h1>Dev<span>Hub</span></h1>
        <span className={`badge ${status}`}>● API {status}</span>
      </header>

      <form className="card form" onSubmit={submit} noValidate>
        <h2>{editingId ? "Edit project" : "Add a project"}</h2>

        <input placeholder="Title" maxLength={100} value={form.title} onChange={change("title")} />
        {fe("title")}

        <textarea placeholder="Description" rows={3} maxLength={1000} value={form.description} onChange={change("description")} />
        {fe("description")}

        <input placeholder="Tech stack (comma separated, max 10)" value={form.techStack} onChange={change("techStack")} />
        {fe("techStack")}

        <div className="row">
          <div>
            <input placeholder="GitHub URL" maxLength={300} value={form.githubUrl} onChange={change("githubUrl")} />
            {fe("githubUrl")}
          </div>
          <div>
            <input placeholder="Live URL" maxLength={300} value={form.liveUrl} onChange={change("liveUrl")} />
            {fe("liveUrl")}
          </div>
        </div>

        {error && <p className="error">{error}</p>}

        <div className="row">
          <button className="btn primary" disabled={saving}>
            {saving ? "Saving..." : editingId ? "Save changes" : "Add project"}
          </button>
          {editingId && (
            <button type="button" className="btn ghost" onClick={cancelEdit}>Cancel</button>
          )}
        </div>
      </form>

      {loading && <p className="muted">Loading... (a sleeping free server can take about a minute to wake up)</p>}
      {!loading && projects.length === 0 && !error && (
        <p className="muted">No projects yet. Add your first one above.</p>
      )}

      <div className="grid">
        {projects.map((p) => (
          <article key={p.id} className="card project">
            <h3>{p.title}</h3>
            <p className="muted">{p.description}</p>
            <div className="tags">
              {(p.techStack || []).map((t) => <span key={t} className="tag">{t}</span>)}
            </div>
            <div className="links">
              {p.githubUrl && isUrl(p.githubUrl) && (
                <a href={p.githubUrl} target="_blank" rel="noopener noreferrer">GitHub ↗</a>
              )}
              {p.liveUrl && isUrl(p.liveUrl) && (
                <a href={p.liveUrl} target="_blank" rel="noopener noreferrer">Live ↗</a>
              )}
            </div>
            <div className="actions">
              <button className="btn small" onClick={() => startEdit(p)}>Edit</button>
              <button className="btn small danger" onClick={() => remove(p)}>Delete</button>
            </div>
          </article>
        ))}
      </div>
    </div>
  );
}