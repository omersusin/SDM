# BRAIN — GrabBit ikinci beyin (otonom işletim kuralları)

Kullanıcının emri: her şeyi sormadan yap, söylenmeyeni de düşün, şaşırt.
Bu dosya, gelecekteki her işte otomatik uygulanır.

## Kimlik
- Uygulama: GrabBit (paket `grab.bit`), Android-only (desktop 2026-09-25'te söküldü).
- Kullanıcı dili: Türkçe (tercihen), bazen İngilizce. Kısa ve net konuş.
- Kullanıcı nefret eder: gereksiz soru, laf kalabalığı, kısmi iş, "sonra bakılır".

## Kalıcı talimatlar (kullanıcıdan)
1. Her özellik sonrası CI'da derleyerek doğrula, test çalıştırma (ajan test koşamaz).
2. Branch → CI yeşil → main merge → branch sil. Direkt main'e iş yok.
3. Gereksiz soru sorma; ama özellik kararları kullanıcıya aittir — karar gerekenleri sor, sonra ekle.
4. Ayar standardı: hiçbir ayar açıklaması/örneği/uygun kontrolü olmadan kalmayacak.
5. UI, ABDM'nin kalsın; Material 3 Expressive'e sormadan geçme.
6. Paket adı `grab.bit` kalsın (kullanıcı onayladı).
7. APK'lar tek zip'te olmayacak (ABI başına artifact).

## Çalışma disiplini
- Ponytail: en tembel doğru çözüm. Önce kodu baştan anla, sonra en kısa diff.
- Varsayım yok: emin değilsen koddan doğrula (grep/read), web'de ara.
- Ajanlar salt okunur tarama yapar; yazmayı tek el yapar (çakışma olmasın).
- 100-200 ajan / 6-10 dalga kullanıcı emridir (2026-09-26): dalgalar halinde uygula.
  Dosya başına tek yazar; izciler salt okunur.
- Session mesajları: `~/storage/downloads/session-messages.txt` (session DB'den tazele).
- Tüm mesaj dökümü + analiz: `user-messages.txt` (tmp/opencode).

## Proje durumu (2026-09-26)
- Main CI yeşil (torrent-finished merge edildi).
- Kalıcı hafıza: docs/STATE.md, ROADMAP.md, DECISIONS.md, COMPETITORS.md, GAPS.md, SECURITY.md, PERF.md.
- APK: arm64 27,0 MB / v7a 24,6 MB / x86_64 26,7 MB (sabit).
- Main CI yeşil. Release APK imzalı (V2), ABI splitli.
- Biten: maskot marka (launcher+in-app), ayar UX standardı, stepper, retry-delay ayarı, desktop sökümü (311 dosya), 4 tur ajan denetim düzeltmesi.
- Bilinen borç: torrent motoru desktop'la gitti (Android libtorrent4j seam SPEC'te); hata sayfalarında Retry için tesisat yok; updater'da kaynak/boyut satırı yok (veri yok).

## Rakip bilgisi (klonlar tmp/opencode'da: dm-navi)
- 1DM/ADM: 16-32 parça, torrent/magnet, clipboard yakalama, link tazeleme, arşiv, MD5, gizli klasör, programlanabilir hız.
- aria2: -x/-s/-k parçalama, çoklu ayna, .aria2 kontrol dosyası, Metalink, RPC.
- Download Navi (GPL): 16 parça, hız limiti, MD5/SHA-256, otomatik arşiv açma, UA kontrolü.

## Yapılacaklar (öncelik sırası)
1. Kullanıcıların rakiplerde bulamadığı şeyler → GrabBit'e ekle (torrent Android, otomatik arşiv açma, clipboard yakalama...).
2. Ölü/orphan/deprecated süpürme (sürekli).
3. APK küçültme (R8 haritası + lib/res denetimi).
4. Animasyon iyileştirme.
5. Güvenlik saldırısı → açık kapatma (sürekli).
