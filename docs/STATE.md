# STATE — GrabBit çalışma durumu (oturumlar arası)

Güncelleme: 2026-09-26. Main: CI yeşil.

## Şu an
- `feature/torrent-finished` merge edildi (TorrentProgress.isFinished + poll-stop), dal silindi.
- Aktif dal: yok. Ağaç temiz, main == origin/main.

## Yapıldı (bu tur öncesi özet)
- Ayar UX standardı, stepper kontroller, retry-delay, auto-unzip, clipboard-monitor.
- Round-4 ilk yarı: WebView şema koruması, filtre temizleme, per-host silme onayı.
- Desktop sökümü (311 dosya) + 4 denetim turu + imzalı ABI-split release.

## Yapılmadı (sırada)
1. Round-4 artıkları: filtre rozeti, updater ilerleme/iptal/hash-hatası UX, bildirim gruplama, hata sayfası Retry.
2. Wave 2-4: exhaustive rakip keşfi → klon+matris → şikayet madenciliği (COMPETITORS.md, GAPS.md).
3. Wave 5: sentez + tek-tek ASK kapısı.
4. Wave 6: desktop kırıntıları + ölü kod (hedef listesi bu dosyada aşağıda).
5. Wave 7: performans + animasyon (PERF.md).
6. Wave 8: güvenlik denetimi + saldırı simülasyonları (SECURITY.md).
7. Wave 9: onaylı özellikler (parti parti, merge öncesi sorulur).

## Desktop kırıntı hedefleri (doğrulandı, silinmeyi bekliyor)
- Sil: installer-plugin ölü sınıfları, nucleus bağımlılığı, yorum DesktopSettings x4,
  scripts/install.sh + uninstall.sh, CiUtils ölü blok (136-168), 26 öksüz string,
  kotlin-coroutines-android + 6 ölü plugin + skiko sürümü.
- Dokunma: `jvm("desktop")` (yeniden adlandır), Platform.Desktop enum,
  InstallerTargetFormat.Apk, ResponsiveTarget.Desktop, DesktopDiskStat adı.

## APK boyutu (#123 cevabı)
- 2026-09-25 00:03: arm64 26.995.123 B, v7a 24.580.141 B, x86_64 26.738.678 B.
- 2026-09-25 23:49: arm64 27.001.172 B, v7a 24.586.311 B, x86_64 26.745.863 B.
- Sonuç: ~27,0 / 24,6 / 26,7 MB, iki build arası fark +6 KB (sabit).
