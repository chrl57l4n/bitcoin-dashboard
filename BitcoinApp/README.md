# ₿ Bitcoin Dashboard – Android App
### Optimiert für Snapdragon 8 (arm64-v8a)

---

## 🚀 Setup in Android Studio

1. **Projekt öffnen**
   - Android Studio starten → `File → Open` → diesen Ordner wählen

2. **Gradle Sync abwarten** (ca. 1–2 Min beim ersten Start)

3. **Gerät verbinden** (USB-Debugging aktivieren)
   - Einstellungen → Entwickleroptionen → USB-Debugging: AN

4. **App starten** → ▶️ Run

---

## 📱 Anforderungen

| Eigenschaft | Wert |
|---|---|
| Min. Android | 10 (API 29) |
| Ziel-Android | 14 (API 34) |
| Architektur | arm64-v8a (Snapdragon 8) |
| Gradle | 8.6 |
| AGP | 8.4.2 |
| Kotlin | 1.9.24 |

---

## ⚡ Snapdragon 8 Optimierungen

- **arm64-v8a only** – kein x86/x86_64 Overhead
- **Hardware-Accelerated WebView** – Adreno GPU für Canvas/WebGL-Charts
- **RenderPriority.HIGH** – priorisiertes Rendering
- **Edge-to-Edge** – Punch-hole Display & curved screen Support
- **ProGuard R8** – minimierte APK-Größe

---

## 🔌 API-Verbindungen

| Service | URL |
|---|---|
| Binance (Kurse) | `api.binance.com` |
| Fear & Greed | `api.alternative.me` |
| Blocktrainer RSS | `blocktrainer.de/feed/` |
| IBIT ETF | `query2.finance.yahoo.com` |

---

## 🛠️ APK Release bauen

```bash
./gradlew assembleRelease
```
APK liegt dann unter: `app/build/outputs/apk/release/`

> **Hinweis:** Für den Play Store muss die APK noch signiert werden.
> `Build → Generate Signed Bundle / APK`

---

## 📲 JavaScript Bridge

Die App stellt native Android-Funktionen für die WebApp bereit:

```javascript
// Aus dem HTML/JS heraus aufrufen:
window.Android.showToast("Nachricht");
window.Android.copyToClipboard("$95,000");
window.Android.isAndroid();       // → true
window.Android.getDeviceInfo();   // → "Snapdragon 8 | Android 14"
```
