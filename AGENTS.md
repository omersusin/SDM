# SDM — agent instructions (read this first)

Before working in this repo, read `docs/ABDM_SPEC.md` end-to-end and follow
it exactly. Mission, process, approved features, design rules are there.
Reference excavation findings: `docs/ABDM_RAPOR.md`.

Short rules:
- Ask one question at a time, never multiple-choice. Wait for the answer,
  record it, move on. (User speaks Turkish — ask in Turkish.)
- Small steps + tests. No commit unless the compiler is clean and tests green.
- If unsure, ask; if obvious, do it. Never drop files and leave.
- After each phase, append learnings to `docs/ABDM_SPEC.md` > Öğrenilenler.

## Operating discipline (distilled, always on)
- Evidence before synthesis: read the code before claiming anything about it.
  The diff is ground truth — a description that disagrees with the diff is a finding.
- Verify by execution, not by re-reading: CI green + tests that actually run
  (a test no CI job executes is decoration — wire it into test.yml or move it).
- Parallelize by default: independent work goes to parallel agents in one turn;
  one writer per file/branch; read-only scouts never write.
- Reversible vs irreversible: local edits and reads are cheap — decide and go.
  Pushes, deletes, merges and releases wait for CI or an explicit go.
- Concise reports: what was done + file:line evidence + what remains. No essays.
- No doc sprawl: update STATE/SPEC instead of creating new files. Never create
  a file the user didn't ask for unless the repo process requires it.
- Finish the loop: implement → CI → merge → delete branch → update docs.
  Half-done work reported as done is the worst failure mode.
