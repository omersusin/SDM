# ABDM Fork — Kazı Raporu (8 repo)

Taban: AB Download Manager (Kotlin, Android+Desktop, çok parçalı motor hazır)

## Grup listesinden gelenler → nereden alınır
1. Media grabber (1DM) → XDM'in sniff mantığı (m3u8/mpd parse, fragment filtresi) + 1DM WebView intercept tarifi
2. 256 parça → aria2 tekniği (min-split-size, piece selector) + XDM dinamik bölme (1 parçayla başla, büyüğü yarıya böl)
3. Torrent → libretorrent4j (LibreTorrent yolu, FrostWire değil)
4. Adblock tarayıcı → 1DM tarifi (EasyList intercept, popup engelle)
5. Obtainium ana sayfa → Seal'in M3 UI kalıpları
6. Video/ses → YTDLnis + Seal (format picker, playlist, altyazı, metadata gömme)

## Motor iyileştirmeleri (ABDM'ye eklenecek)
- Dinamik fan-out (sabit 8 değil, büyüğü böl)
- Crash-safe resume (chunks.db + ETag kontrolü)
- Refresh expired link (403'te tarayıcıdan tazele)
- Mirror/adaptive sıralama, disk cache, preallocate

## Kuyruk/zamanlayıcı
- XDM scheduler (HH:MM + gün maskesi), Motrix hız profilleri (normal/turtle), Persepolis retry pass

## Öncelik sırası önerisi
1. Grabber 2. Parça motoru 3. Torrent 4. UI 5. Adblock 6. Scheduler+

## 2. dalga (8 repo daha)
- JDownloader: LinkGrabber iki aşama (önce topla/doğrula, sonra indir), eklenti sistemi, Packagizer kuralları
- Gopeed: Fetcher arayüzü (http/bt/hls), work-stealing, JS eklentiler, REST+MCP
- pyLoad: hoster/crypter/account eklenti hiyerarşisi, captcha kuyruğu
- gallery-dl: 257 site extractor, dosya adı şablonu, SQLite arşiv (tekrar indirme)
- lux/you-get: video extractor kayıt defteri, altyazı, playlist seçimi
- axel/uGet/KGet: axel_divide dengeleme, .st resume, kategori ağacı, pano izleme, metalink
- lftp/wget2: pget paralel, mirror modu, üstel yeniden bağlanma, HSTS
- NDM/FDM (kapalı, web'den): canlı hız/bağlantı ayarı, trafik modları, Site Manager, HLS birleştirme
