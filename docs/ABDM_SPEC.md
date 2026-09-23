# İNDİRME YÖNETİCİSİ — Ajan Görev Dokümanı

## Proje
AB Download Manager (https://github.com/amir1376/ab-download-manager) çatalı.
Kotlin Multiplatform. Öncelik Android. Tek kod disiplini.

## Misyon
IDM / ADM / 1DM / NDM / FDM seviyesini geçen, açık kaynak, ücretsiz
indirme yöneticisi. Swiss-army-knife ama mantık dışı hiçbir şey yok.

## Süreç (aynen uygula)
1. ARAŞTIR: İnternetteki BÜTÜN indirme yöneticilerini ve indirme-ile-alakalı
   her şeyi bul (açık kaynak + kapalı kaynak, app + TUI + CLI + eklenti).
   Kapalı olanları web'den (özellik listesi, inceleme, video) araştır.
2. KAZ: Açık kaynak olanları klonla (scratch klasörüne), derinlemesine incele.
   Her repodan taşınabilir fikirleri çıkar: motor teknikleri, kuyruk,
   grabber, UI kalıpları, zamanlayıcı, torrent, altyazı, metadata.
3. SOR: Bulduklarını kullanıcıya TEKER TEKER sor. Çoktan seçmeli yok.
   "Bu olsun mu?" diye tek soru sor, cevabı bekle, kaydet, sonrakine geç.
   Kullanıcı "hangisi daha iyi?" derse dürüst önerini söyle, kararı o verir.
4. YAZ: Onaylananları bu dokümana "Olacaklar" listesine işle.
5. UYGULA: Küçük adımlar, her adımda test, her bug teste dönüşür.
   tube2note standardı: ruff/derleyici temiz + test yeşil olmadan commit yok.

## Onaylı özellikler (soruldu, evet dendi)
1. Media grabber (uygulama içi tarayıcı + sayfa medya listesi)
2. Torrent + magnet (aynı liste, duraklat/devam)
3. Video indirme (yt-dlp, format seçici, playlist, altyazı)
4. Adblock (uygulama içi tarayıcı)
5. Toplu medya çekme (site çıkarıcı + ad şablonu + tekrar koruması)
6. Hibrit dinamik motor (yavaş parçayı böl, 256 parçaya kadar)
7. Gelişmiş zamanlayıcı (saat+gün, WiFi-only, hız profilleri)
8. Obtainium tarzı ana sayfa (aktif üstte + yoğun kuyruk)
9. İki aşamalı kuyruk (yakala havuzu → seç → indir)
10. Link tazeleme (403'te kaynaktan yenile, devam et)
11. Site Yöneticisi (site başına auth/UA/bağlantı)
12. Toplu kalıp (dosya[001-100].zip)
13. Trafik modları (Yüksek/Düşük/Salyangoz)

## Elenenler
- PC tarayıcı eklentisi (istenirse sonra)

## Tasarım (zorunlu)
- TAM Material 3 Expressive. Yarım değil, tam.
- Ama yapay zekâ elinden çıkmış gibi GÖRÜNMEYECEK.
- Tasarım skill'lerini kullan (taste/estetik skill'leri). Şık olacak ama
  M3 Expressive dışına taşmayacak. Şıklık = tipografi, boşluk, ritim,
  renk disiplini. Şablon hissi veren hiçbir şey yok.
- Düzen ve nizam zorunlu: modül yapısı, isimlendirme, dosya düzeni
  baştan temiz kurulacak, uygulama büyürken bozulmayacak.

## Kazı raporları
- ABDM_RAPOR.md (16 repo: ABDM, YTDLnis, Seal, XDM, Persepolis, Motrix,
  aria2, LibreTorrent, FrostWire, 1DM/IDM, JDownloader, Gopeed, pyLoad,
  gallery-dl, lux/you-get, axel/uGet/KGet, lftp/wget2, NDM/FDM)
- Yeni bulunan her repo buraya eklenecek.
- Klonlar iş bitince silinir.

## Çıktı
- Çalışan kod (çatalda) + testler + bu dokümanın güncel hali.
- Kullanıcıya her fazda rapor + sorular.

## Arayüz araştırması (zorunlu, tasarım öncesi)
- Mevcut skill'ler yetersizse YENİ skill bul (registry/katalog tara).
- Arayüz şablonları bul: M3 Expressive örnekleri, indirme yöneticisi
  ekranları, Obtainium/Seal/ADM ekran görüntüleri ve düzenleri.
- Diğer uygulamaların arayüzlerine bak: ekran akışı, kart yapısı,
  bilgi hiyerarşisi, boş durumlar, hata durumları.
- Kararsız kaldığın her noktada kullanıcıya SOR, birlikte şekillendirin.
  Tek soru, cevap bekle, devam et. Tahminle arayüz kilitleme.

## GitHub sürüm indirme (hızlı yol)
- Uygulama GitHub Releases'ten doğrudan indirebilmeli:
  `GET repos/{owner}/{repo}/releases/latest` → assets listesinden
  mimariye uygun dosyayı seç (örn. *-arm64-v8a.apk) → `browser_download_url`.
- Girişsiz de çalışır (saatte 60 istek). Token varsa (kullanıcı eklerse)
  limit 5000'e çıkar, özel repolar da açılır.
- Seçilen asset normal motorla iner (çok parça + resume), yani ışık hızında.
- Kullanım: "GitHub linki yapıştır → sürüm/asset seç → indir".
  Örn. tube2note v0.18.0 APK testi bununla yapıldı.

## Market yorumu madenciliği (zorunlu araştırma kolu)
- İndirme yöneticilerinin yayınlandığı marketlerdeki (Play Store vb.)
  kullanıcı yorumlarını oku ve analiz et.
- Aranan: "şu özellik yok" şikayetleri, "var ama yetersiz" şikayetleri,
  tekrar eden hata bildirimleri, övülen özellikler.
- Her bulguyu kullanıcıya TEKER TEKER sor: "X uygulamasında kullanıcılar
  Y'den şikayetçi, bizde olsun mu?" Onaylanmadan ekleme.
- Amaç: gerçek kullanıcı acılarına duyarlı, yorumlarla şekillenen uygulama.

## Ajan karakteri (dinamik, proaktif)
- Ajan dosya oluşturup bırakmaz. Her çıktının peşinden gider: test eder,
  doğrular, kullanıcıya gösterir, geri bildirimi alır, düzeltir.
- Söylemeden düşünür ve yapar: kullanıcı X deyince X'in gerektirdiği
  sonraki 3 adımı da yapar (test, doküman, paket). Sormak yerine yapar;
  gerçekten karar gereken yerde TEK soru sorar.
- Sorular + kendi inisiyatifi birlikte: kararsızsa sorar, belliyse yapar.
  Asla sessizce beklemez, asla izinsiz büyük karar vermez.
- Kendi zekâsını geliştirir: her fazda neyi iyi/kötü yaptığını bu dokümana
  "Öğrenilenler" bölümü olarak yazar, sonraki fazda uygular.
- tube2note standardının üstü hedeflenir: daha az soru, daha çok isabet,
  daha hızlı döngü. Kullanıcı "sihir gibi" demeli.

## Öğrenilenler (ajan her fazda buraya yazar)
- (boş — ilk fazda dolar)
