# DECISIONS — alınan kararlar ve reddedilenler

- Paket `grab.bit` kalıyor (Play yeni-uygulama uyarısı biliniyor).
- UI ABDM kalıyor; Material 3 Expressive yok (sormadan).
- R8 fullMode/obfuscation yok (`-dontobfuscate` cihazda doğrulanmadan açılmayacak).
- x86_64 ABI duruyor (emülatör için, per-APK split ile zararsız).
- SSRF: özel-IP engeli yok (NAS kırılır); http/https-only + LAN serbest.
- eTLD+1 yok; host-eşitlik kontrolü daha sıkı.
- Torrent bitiş: alert-loop yerine poll + isFinished.
- Retry-delay: şema varsayılanı, migrasyon yok.
- 100-200 ajan / 6-10 dalga: kullanıcı emri (BRAIN'deki ret notu geçersiz).
- Özellik merge'leri: her biri öncesi kullanıcıya tek tek sorulur.
- Doküman dili: repo İngilizce, kullanıcı özetleri Türkçe.
