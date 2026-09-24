# DOWNLOAD MANAGER — Agent Task Document

## Project
Fork of AB Download Manager (https://github.com/amir1376/ab-download-manager).
Kotlin Multiplatform. Android first. Single-codebase discipline.

## Mission
A free, open-source download manager surpassing IDM / ADM / 1DM / NDM / FDM.
Swiss-army-knife, but nothing illogical.

## Process (apply exactly)
1. RESEARCH: Find ALL download managers and everything download-related
   on the internet (open source + closed source, app + TUI + CLI + plugin).
   Research closed-source ones via the web (feature lists, reviews, videos).
2. DIG: Clone the open-source ones (into a scratch folder), study them in depth.
   Extract portable ideas from each repo: engine techniques, queueing,
   grabber, UI patterns, scheduler, torrent, subtitles, metadata.
3. ASK: Ask the user about your findings ONE AT A TIME, IN TURKISH.
   No multiple-choice. Ask a single "Should this be included?" question,
   wait for the answer, record it, then move on.
   If the user asks "which one is better?", give your honest recommendation;
   the user decides.
4. WRITE: Add the approved items to the "Planned" list in this document.
5. IMPLEMENT: Small steps, test at every step, every bug becomes a test.
   tube2note standard: no commit without clean ruff/compiler + green tests.

## Approved features (asked, answered yes)
1. Media grabber (in-app browser + page media list)
   - UI (2026-09-23): settings toggle — menu+badge vs auto-popup, user picks.
2. Torrent + magnet (same list, pause/resume)
3. Video downloading (yt-dlp, format picker, playlist, subtitles)
4. Adblock (in-app browser)
5. Bulk media extraction (site extractor + naming template + duplicate protection)
6. Hybrid dynamic engine (split slow parts, up to 256 parts)
7. Advanced scheduler (time+day, WiFi-only, speed profiles)
8. Obtainium-style home page (active on top + dense queue)
9. Two-stage queue (capture pool → select → download)
10. Link refresh (refresh from source on 403, then resume)
11. Site Manager (per-site auth/UA/connections)
12. Bulk pattern (file[001-100].zip)
13. Traffic modes (High/Low/Snail)

## Rejected
- PC browser extension (later if requested)

## Design (mandatory)
- FULL Material 3 Expressive. Full, not half.
- But it must NOT look AI-generated.
- Use design skills (taste/aesthetic skills). It will be stylish but
  never leave M3 Expressive. Style = typography, spacing, rhythm,
  color discipline. Nothing that feels like a template.
- Layout and order are mandatory: module structure, naming, file layout
  are set up cleanly from the start and must not degrade as the app grows.
- 2026-09-24 UI FREEZE (user order): keep ABDM's UI as-is. Do NOT migrate
  to Material 3 Expressive unless the user explicitly says so. The badge
  pill + tonal rows (design-1) stay; nothing further.
- Settings backlog (user approved 2026-09-24, all missing from settings UI):
  adblock on/off, speed profile selector (High/Low/Snail), scheduler
  time+day+WiFi editing, torrent add screen entry, video quality default.
  Only GrabberUiMode is in settings today.
  - 2026-09-24 settings-adblock + settings-profile DONE (both merged).
- 2026-09-24 settings-wifi: toggle merged (one hung runner, retry green).
  Enforcement via ABDMAppManager network monitor still open.
- 2026-09-24 wifi-gate: NetworkCallback stops all on WiFi loss (WiFi-only
  on). Auto-resume on return still open (no resume-all API).
- 2026-09-24 torrent-3: browser menu adds magnet from clipboard (session
  starts lazily, process lifetime). Progress UI still open.
- 2026-09-24 settings-video: max height pref + best-first dialog. CI lesson:
  check-latest causes JBR ECONNRESET flakes → removed from test.yml.
- 2026-09-24 bulk-1: BulkPattern ([N-M] expand + 3 tests) wired into batch
  sheet (batch now List-based).

## Dig reports
- ABDM_RAPOR.md (16 repos: ABDM, YTDLnis, Seal, XDM, Persepolis, Motrix,
  aria2, LibreTorrent, FrostWire, 1DM/IDM, JDownloader, Gopeed, pyLoad,
  gallery-dl, lux/you-get, axel/uGet/KGet, lftp/wget2, NDM/FDM)
- Every newly found repo will be added here.
- Clones are deleted when the work is done.

## Output
- Working code (in the fork) + tests + current version of this document.
- Report + questions to the user at every phase.

## UI research (mandatory, before design)
- If existing skills are insufficient, find NEW skills (scan registries/catalogs).
- Find UI templates: M3 Expressive examples, download manager
  screens, Obtainium/Seal/ADM screenshots and layouts.
- Study other apps' UIs: screen flow, card structure,
  information hierarchy, empty states, error states.
- Wherever undecided, ASK the user and shape it together.
  One question at a time, IN TURKISH, wait for the answer, continue.
  Never lock the UI by guessing.

## GitHub release download (fast path)
- The app must download directly from GitHub Releases:
  `GET repos/{owner}/{repo}/releases/latest` → pick the
  architecture-matching file from the assets list (e.g. *-arm64-v8a.apk)
  → `browser_download_url`.
- Works without login (60 requests/hour). With a token (if the user adds one)
  the limit rises to 5000, and private repos open up.
- The selected asset downloads with the normal engine (multi-part + resume), i.e. at full speed.
- Usage: "paste GitHub link → select release/asset → download".
  E.g. the tube2note v0.18.0 APK test was done this way.

## Store review mining (mandatory research branch)
- Read and analyze user reviews of download managers in the stores
  where they are published (Play Store, etc.).
- Look for: "this feature is missing" complaints, "exists but insufficient"
  complaints, recurring bug reports, praised features.
- Ask the user about each finding ONE AT A TIME, IN TURKISH:
  "Users of app X complain about Y, should we include it?" Never add without approval.
- Goal: an app sensitive to real user pain, shaped by reviews.

## Agent character (dynamic, proactive)
- The agent does not create files and abandon them. It follows through on
  every output: tests it, verifies it, shows it to the user, gets feedback, fixes it.
- It thinks ahead and acts without being told: when the user says X, it also does
  the next 3 steps X requires (test, docs, package). It does instead of asking;
  it asks ONE question, IN TURKISH, only where a real decision is needed.
- Questions + own initiative together: asks when undecided, acts when clear.
  Never waits silently, never makes big decisions without permission.
- It improves its own intelligence: each phase, it writes what it did well/badly
  into this document as a "Learnings" section and applies it in the next phase.
- Target above the tube2note standard: fewer questions, more hits,
  faster loops. The user should say "it feels like magic".

## Learnings (the agent writes here every phase)
- 2026-09-23 fork-base: ABDM (shallow clone, 1 commit) could not be fetched
  via git (shallow roots rejected) → imported via worktree copy excl. .git,
  upstream added as remote for future pulls. Apache-2.0 kept in
  LICENSE.UPSTREAM-Apache-2.0, repo LICENSE stays GPL pending user call.
  Upstream README kept in README.UPSTREAM.md. AGENTS.md + docs/ untouched.
- 2026-09-23 license: user confirmed GPLv3 stays (LICENSE). Apache-2.0 text
  preserved in LICENSE.UPSTREAM-Apache-2.0 for the forked files.
- 2026-09-23 grabber-1: MediaSniffer (ext+mime heuristic, stdlib-only) +
  9 tests in shared:utils commonTest + test.yml CI (:shared:utils:desktopTest).
  Repo had zero tests; kotlin("test") added via commonTest deps. CI green
  3m32s. Phone can't compile (JDK17 vs toolchain 25, no Android SDK) → branch
  + CI-gate + merge is the loop until a PC/CI with SDK exists.
- 2026-09-23 grabber-2: PageMediaCollector (dedup per-page list, commonMain)
  + 4 tests; DownloadInterceptor.interceptRequest feeds it keyed by page,
  mediaForPage() exposes the list (no UI yet — UI needs user shaping).
  CI extended with :android:app:compileDebugKotlin; both jobs green 3m54s.
- 2026-09-23 grabber-3: GrabberUiMode setting (MENU_BADGE default/AUTO_POPUP)
  stored via S.enum in android model + AppSettingsStorage flow + settings UI
  entry. en_US-only locales kept (27 files dropped per user). First CI red:
  catch{} lambda ambiguous → fixed with PlatformDefaultSettings::ref pattern.
- 2026-09-23 grabber-4: MENU_BADGE mode live — reactive mediaCounts flow in
  interceptor, activeMediaCount in BrowserComponent, badge+download button in
  address bar, MediaListDialog with per-item download. AUTO_POPUP still open.
- 2026-09-23 grabber-5: AUTO_POPUP mode — dialog opens itself once per page
  when media appears (per-page guard, no re-popup). Both UI modes done.
- 2026-09-23 engine-1: splitToRange locked with 5 tests (256-part, min-split
  guard, edge cases) in downloader/core commonTest. Finding: 256 cap already
  in UI (ThreadCountLimitation); dynamic split exists. Next: straggler split.
- 2026-09-23 engine-2: StragglerPicker (pure slowest-ETA selection + 4 tests).
  Speed feed wiring still open.
- 2026-09-23 engine-3: PartSpeedSampler (overall-average, kotlin.time) sampled
  in copyDataSync + job splits slowest-ETA part via StragglerPicker. Two CI
  reds: kotlinx.datetime Clock gone → kotlin.time; dropped paren. Live speed
  behavior still needs device verification.
- 2026-09-23 torrent-1: MagnetParser (hex/base32, dn/tr decode + 5 tests).
  libtorrent4j 2.1.0-38 mapped from LibreTorrent scratch clone; session next.
- 2026-09-23 torrent-2: libtorrent4j dep (android ABIs + desktop) + TorrentSession
  expect/actual seam. Two CI reds: catalog accessor naming, frostwire→
  org.libtorrent4j package, download() 3-arg signature (source-verified).
- 2026-09-23 video-1: VideoFormatPicker (codec split, +-join, best-height +
  5 tests). youtubedl-android runtime mapped from Seal scratch clone.
- 2026-09-23 video-2: youtubedl-android 0.17.3 dep + YtDlpInfoParser (JSON→
  VideoFormat, tested) + YtDlpRunner seam. One infra red (JBR ECONNRESET,
  rerun green).
- 2026-09-24 video-3: YtDlpRunner.init wired into ABDMApp.onCreate (IO thread,
  failure contained). Runtime behavior needs device.
- 2026-09-24 adblock-1: AdBlockMatcher (EasyList ||domain^ subset + 4 tests).
  WebView intercept wiring + popup blocking next.
- 2026-09-24 adblock-2: intercept returns empty response for blocked URLs;
  non-gesture windows refused (popup block). Filter list loading still open.
- 2026-09-24 scheduler-1: ScheduleTimes.isActiveAt (pure, incl. overnight) +
  3 tests. Scheduler core already existed (delay-based auto start/stop).
- 2026-09-24 scheduler-2: SpeedProfile (HIGH/LOW/SNAIL + 3 tests). WiFi-only
  gate still open (needs ConnectivityManager).
- 2026-09-24 apk-1: debug APK via apk.yml workflow_dispatch, 130MB,
  copied to phone Downloads. Release signing needs user secrets.
- 2026-09-24 queue-1: LinkPool capture pool (dedupe/select/remove + 4 tests).
- 2026-09-24 queue-2: media dialog captures page to pool. Pool UI still open.
- 2026-09-24 refresh-1: LinkRefreshPolicy (403/410 → refresh, else retry +
  2 tests). Re-resolution from downloadPage still open.
- 2026-09-24 design-1: badge pill (primary/onPrimary) + tonal dialog rows via
  existing tokens, no new deps. One red: duplicated brace (fixed).
- 2026-09-24 video-4: browser menu → VideoFormatsDialog (loading/ready/empty,
  per-format download via engine). Subtitles/playlist still open.

## Build, signing, CI (mandatory)
- App MUST be signed (release keystore). Maintainer provides signing secrets
  via GitHub Actions secrets; agent wires the signing config, never the keys.
- CI must be FAST: Gradle caching, parallel jobs, per-ABI splits built in
  parallel, no redundant rebuilds. Measure and cut minutes.
- R8 / ProGuard (minify + shrink + obfuscate) enabled for release builds.
  Keep rules minimal but working (no crashing release builds); verify the
  signed release APK installs and runs.
