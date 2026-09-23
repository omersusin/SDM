# ABDM Fork — Dig Report (8 repos)

Base: AB Download Manager (Kotlin, Android+Desktop, multi-part engine ready)

## From the group list → where to take it from
1. Media grabber (1DM) → XDM's sniffing logic (m3u8/mpd parsing, fragment filtering) + 1DM WebView intercept recipe
2. 256 parts → aria2 technique (min-split-size, piece selector) + XDM dynamic splitting (start with 1 part, split large ones in half)
3. Torrent → libretorrent4j (LibreTorrent path, not FrostWire)
4. Adblock browser → 1DM recipe (EasyList intercept, popup blocking)
5. Obtainium home page → Seal's M3 UI patterns
6. Video/audio → YTDLnis + Seal (format picker, playlist, subtitles, metadata embedding)

## Engine improvements (to be added to ABDM)
- Dynamic fan-out (not fixed 8, split large ones)
- Crash-safe resume (chunks.db + ETag check)
- Refresh expired link (refresh from browser on 403)
- Mirror/adaptive ordering, disk cache, preallocation

## Queue/scheduler
- XDM scheduler (HH:MM + day mask), Motrix speed profiles (normal/turtle), Persepolis retry pass

## Suggested priority order
1. Grabber 2. Part engine 3. Torrent 4. UI 5. Adblock 6. Scheduler+

## 2nd wave (8 more repos)
- JDownloader: LinkGrabber two stages (collect/verify first, then download), plugin system, Packagizer rules
- Gopeed: Fetcher interface (http/bt/hls), work-stealing, JS plugins, REST+MCP
- pyLoad: hoster/crypter/account plugin hierarchy, captcha queue
- gallery-dl: 257 site extractors, filename template, SQLite archive (no re-download)
- lux/you-get: video extractor registry, subtitles, playlist selection
- axel/uGet/KGet: axel_divide balancing, .st resume, category tree, clipboard monitoring, metalink
- lftp/wget2: pget parallel, mirror mode, exponential reconnect, HSTS
- NDM/FDM (closed, from web): live speed/connection tuning, traffic modes, Site Manager, HLS merging
