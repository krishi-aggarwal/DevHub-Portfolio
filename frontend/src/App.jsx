import { useEffect, useState } from "react";

const API = import.meta.env.VITE_API_URL;
const EMPTY = { title: "", description: "", techStack: "", githubUrl: "", liveUrl: "" };

export default function App() {
  const [projects, setProjects] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [form, setForm] = useState(EMPTY);
  const [editingId, setEditingId] = useState(null);
  const [apiUp, setApiUp] = useState(null);

  async function load() {
    try {
      setLoading(true);
      const res = await fetch(`${API}/api/projects`);
      if (!res.ok) throw new Error(`Server returned ${res.status}`);
      setProjects(await res.json());
      setError(null);
    } catch (e) {
      setError(e.message);
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
  }

  async function submit(e) {
    e.preventDefault();
    const body = {
      ...form,
      techStack: form.techStack.split(",").map((t) => t.trim()).filter(Boolean),
    };
    const url = editingId ? `${API}/api/projects/${editingId}` : `${API}/api/projects`;
    try {
      const res = await fetch(url, {
        method: editingId ? "PUT" : "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(body),
      });
      if (!res.ok) {
        const data = await res.json();
        const details = data.fields ? Object.values(data.fields).join(", ") : data.error;
        throw new Error(details || "Request failed");
      }
      cancelEdit();
      load();
    } catch (err) {
      setError(err.message);
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

      <form className="card form" onSubmit={submit}>
        <h2>{editingId ? "Edit project" : "Add a project"}</h2>
        <input placeholder="Title" value={form.title} onChange={change("title")} />
        <textarea placeholder="Description" rows={3} value={form.description} onChange={change("description")} />
        <input placeholder="Tech stack (comma separated)" value={form.techStack} onChange={change("techStack")} />
        <div className="row">
          <input placeholder="GitHub URL" value={form.githubUrl} onChange={change("githubUrl")} />
          <input placeholder="Live URL" value={form.liveUrl} onChange={change("liveUrl")} />
        </div>
        {error && <p className="error">{error}</p>}
        <div className="row">
          <button className="btn primary">{editingId ? "Save changes" : "Add project"}</button>
          {editingId && (
            <button type="button" className="btn ghost" onClick={cancelEdit}>Cancel</button>
          )}
        </div>
      </form>

      {loading && <p className="muted">Loading...</p>}
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
              {p.githubUrl && <a href={p.githubUrl} target="_blank" rel="noopener noreferrer">GitHub ↗</a>}
              {p.liveUrl && <a href={p.liveUrl} target="_blank" rel="noopener noreferrer">Live ↗</a>}
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