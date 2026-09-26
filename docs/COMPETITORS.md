# COMPETITORS — rakip özellik matrisi

Wave 2: 25 izci → 200+ proje. Wave 3: klon + web-matris (33 proje derin).
Lejant: ✓ var · ◐ kısmi · ✗ yok · ? bilinmiyor. Parça = eşzamanlı segment.

## Mobil (Android öncelikli)

| Proje | Tür | Parça | Torrent | HLS/DASH | Zamanlayıcı | Pano | Link-tazele | Hash | Arşiv | Not |
|---|---|---|---|---|---|---|---|---|---|---|
| download-navi 1.6.2 (ölü) | Java | 16 | ✗ | ✗ | ✗ | ✓ | ✗ | MD5+SHA256 | zip/tar/7z oto-açma | Referer+UA alanları, güç/pil kapıları, TV |
| Seal 1.13/v2α | Kotlin+yt-dlp | aria2c | ✗ | yt-dlp | ✗ (v2α kuyruk) | ? | ✗ | ✗ | ✗ | Format sorter, altyazı cilası, özel-komut şablonları |
| YTDLnis 1.9 | Flutter+yt-dlp | aria2c | ✗ | yt-dlp | AlarmManager | txt-batch | ✗ | ✗ | ✗ | Plugin-APK runtime, lazy playlist, observe-sources, Tasker |
| Aurora (kapalı-kaynak eğilimli) | Flutter+native | ✓+limit | libtorrent | AES+MediaMuxer | ? | ? | dead-link revival (Pro) | SHA-256 | ? | Sniffer-boru hattı, cookie mirası, katmanlı freemium |
| Kite | Flutter+yt-dlp | ? | ✗ | yt-dlp | ✗ | ? | ✗ | ✗ | ✗ | Cookie-auth, Telegram iletme |
| LibreTorrent 4.x | Java+libtorrent4j | — (BT) | ✓ DHT/PEX/RSS | ✗ | ✓ | oto-fetch | ✗ | piece | ✗ | Proxy kill-switch, split şifreleme, session journal |
| Aria2App 5.10 | Java+aria2 | aria2 | ✓ | ✗ | ✗ | ? | ✗ | ✗ | ✗ | SSID profili, local=RPC profili, DirectDownload |
| Fetch (lib) | Kotlin lib | HTTP seg | ✗ | ✗ | ✗ | ✗ | retry | ✗ | ✗ | Namespace kuyruk, grup-gözlemci |
| Ketch (WIP) | KMP | 4/dl+4/host | saf-Kotlin BT | planlı | wifi-koşullu | ✗ | ETag resume | rehash | ✗ | Local/remote aynı UI, multi-network, token-bucket |
| Transdroid | Java remote | — | 16 adaptör | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | Adaptör soyutlaması, RSS, cookie-enjeksiyon |
| qBitController 2.2 | KMP remote | — | qB only+RSS | ✗ | ✗ | magnet-cap | ✗ | ✗ | ✗ | Derin qB sadakati, yol önerisi |
| Azhar ADM | — | — | — | — | — | — | — | — | — | 404, listeden düştü |
| youtubedl-android (lib) | Java+Python | — | ✗ | ffmpeg merge | ✗ | ✗ | ✗ | ✗ | ✗ | Self-update, lazy extractor, 16KB-page dersi |

## Masaüstü

| Proje | Tür | Parça | Torrent | HLS/DASH | Zamanlayıcı | Pano | Link-tazele | Hash | Arşiv | Not |
|---|---|---|---|---|---|---|---|---|---|---|
| Motrix 55k★ | Electron+aria2 | aria2 | ✓ | FFmpeg birleştirme | ✓ (rezervli) | ? | ✗ | ✗ | ✗ | Eklenti pazarı, site-glob filtresi, AI-CLI |
| Gopeed 26k★ | Go+Flutter | ✓ | ✓+ed2k | ? | ? | ? | ✗ | ✗ | ✗ | REST+webhook, git-eklenti, mobil+web |
| XDM 7.x (ölü) | Java | 5-6x | ✗ | DASH/HLS/HDS | ✓ | ✓ | parent-page dialog | ✗ | ✗ | Dahili dönüştürücü, takeover listesi |
| Persepolis 7.5k★ | Python+aria2 | 64 | ✗ | yt-dlp | kuyruk | ? | ? | ✗ | ✗ | Kuyruk-birinci-nesne, BSD paketleme |
| Varia 1.9k★ | Python GTK | aria2 | ✓ | gömülü m3u8 yok | hafta-günlük | ✗ | ✗ | ✗ | ✗ | Uzak-aria2, çerez-devir |
| JDownloader | Java | ✓ | ✗ | decrypter | ✓ | ✓ | otomatik directlink | ✗ | RAR+şifre | Grabber-staging, 1000+ eklenti |
| FreeRapid (ölü) | Java | ✓ | ✗ | ✗ | ? | ✓ | ✗ | ✗ | ✗ | Basit eklenti API |
| FileCentipede (çekirdek kapalı) | C++ | ✓ | ✓+ed2k | JS-key sniff | ? | ? | Refresh-address | checksum | ? | Video-bar, site-kuralları, file-manager |
| dlman 316★ | Rust/Tauri | 1-32 adaptif | ✗ | parser var/baglı değil | ✓+geri-sayım | ? | ✗ | ✗ | ✗ | SQLite-per-segment, CLI-paylaşımlı |
| Rayburst | Tauri2+aria2-next | aria2 | ✓ | ✓ | ✓ | ? | ? | ✗ | ✗ | Bakımlı aria2 fork |
| QDM 112★ | Tauri+Rust | 1-32 | ✗ | HLS-AES | zamanlanmış kuyruk | ✓ | expired-URL kurtarma | ✗ | ✗ | Erken-enjeksiyon sniff |
| FireDM fork | Python+pycurl | ✓ | ✗ | ✓+refresh_urls | ✓ | ✓ | ✓ (HLS) | ✓ | ✗ | Üçlü düğme (chunk+conn+eşzaman) |
| pyIDM (silinmiş) | Python | 10 worker | ✗ | DASH merge | basit | ✓ | Refresh düğmesi | ✗ | ✗ | Fikir-madeni, bağımlılık yok |
| PIDM 22★ | Python httpx | ✓ | planlı | format-seçim | takvim | ✗ | ✗ | ✗ | ✗ | SQLite persist, proxy-trigger |
| youwee | Tauri+yt-dlp | yt-dlp | ✗ | yt-dlp | ? | ? | ? | ✗ | ✗ | Follow+Telegram+AI özet |
| FDM (kısmi OSS) | C++ | ✓ | ✓ | ✓ | ✓ | ✓ | ? | ? | ✓ | Forum istekleri madende |

## CLI / motor

| Proje | Parça | Multi-kaynak | RPC/API | Zamanlayıcı | Not |
|---|---|---|---|---|---|
| aria2 42k★ | -x/-s/-k | HTTP+BT aynı anda | JSON-RPC+WS | ✗ | Ayna zekası, kontrol-dosyası, inorder-streaming |
| yt-dlp 193k★ | fragmanlar | ? | self | ✗ | 1000+ site, format sözdizimi |
| lux 31k★ | ✓ | ? | lib | ✗ | Hızlı Go, JSON çıktı |
| N_m3u8DL-RE 8.8k★ | thread=CPU | ✗ | ✗ | task-start | Canlı düğmeler, reklam-atlama, şablon-ad |
| BBDown (arşivli) | -mt | ? | HTTP server | ? | Tek-site derinliği, danmaku |
| vsd 545★ | 1-16 | ✗ | lib | ✗ | Widevine/PlayReady edinimi |
| streamlink 11.8k★ | segment-thread | ✗ | API | ✗ | Plugin altın-standardı, live-edge |
| gallery-dl 19.8k★ | ? | ? | config | sleep | Matcher+auth matrisi+arşiv |
| cobalt 43.8k★ | ? | proxy | tunnel/picker | ✗ | Temiz API, federasyon |
| curl | paralel | ? | ✗ | retry-after | Token-bucket, resume |

## Web / self-hosted / eklenti

| Proje | Model | Abonelik | Sniffer | Not |
|---|---|---|---|---|
| pyLoad 3.9k★ | hoster-eklenti | ✗ | ✗ | Collector-staging, captcha |
| MeTube 14.9k★ | yt-dlp WebUI | ✓+şablon | ✗ | Tipli çıktı-şablonları |
| AriaNg 13k★ | aria2-RPC UI | ✗ | ✗ | Backend'siz ayrık UI |
| Flood 2.8k★ | 4 torrent backend | ✗ | ✗ | Tek UI çok backend |
| cat-catch 21.5k★ | eklenti | ✗ | ✓+parser | Eklenti-içi önizleme+script |
| hls-downloader | eklenti | ✗ | ✓+picker | Yerel ffmpeg.wasm mux |
| DownThemAll | eklenti | ✗ | ✗ | Rename-maske, toplu-seçim |
| TurboDM | eklenti | ✗ | ? | Eklenti-içi thread |

## Düşenler (ölü/404/arşiv)
Azhar ADM (404) · pyIDM (silinmiş) · BBDown (arşivli, fork: BBDownT) · XDM (durmuş) ·
uGet (arşivli) · FreeRapid (terk) · Steadyflow (arşivli) · AllTube (arşivli).
