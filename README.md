# Flow

Kundalik odat ilovasi: Uyqu, Havoda 30 daqiqa, Kodlash 30 daqiqa.
To'liq lokal (INTERNET ruxsati yo'q), Kotlin + Jetpack Compose + Material 3 + Room.
applicationId: com.gulimovbunyod.flow (HECH QACHON o'zgarmaydi).

## Build va update yo'riqnomasi

APK faqat GitHub Actions'da yig'iladi (Android Studio kerak emas).

1. O'zgarishni commit qilib main'ga push qiling (git push).
2. GitHub > Actions > oxirgi "Build" > Artifacts: flow-release-apk (yoki flow-debug-apk).
3. Zip'ni oching, APK'ni telefonga o'rnating. Eski versiya ustiga o'rnatilsa ma'lumot saqlanadi.

Update ishlashi sharti: har build BIR XIL kalit bilan imzolanadi.
Build logida "Imzo sertifikati (SHA-256)" chiqadi: u har versiyada bir xil bo'lishi kerak.
Shu qiymatni Settings > Secrets and variables > Actions > Variables ichida
EXPECTED_CERT_SHA256 nomi bilan saqlang: keyingi build'lar mos kelmasa to'xtaydi.

## Imzo kaliti

Keystore (flow-release.jks), alias va parol repodan TASHQARIDA zaxiralanadi.
Yo'qolsa ilovani yangilab bo'lmaydi. GitHub Secrets: KEYSTORE_BASE64, KEYSTORE_PASSWORD,
KEY_ALIAS, KEY_PASSWORD.

## Versiya

versionCode = flow.versionCodeBase (gradle.properties) + GITHUB_RUN_NUMBER.
versionName app/build.gradle.kts ichida qo'lda o'zgartiriladi.
.github/workflows/build.yml faylining nomini o'zgartirmang.

## Keyingi bo'limlar (keyingi bosqichlarda to'ldiriladi)

Arxitektura, streak qoidalari, keyingi rejalar (backup/restore, XP/level, dam olish kunlari, haftalik review).
