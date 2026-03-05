# ₿ Bitcoin Dashboard — Android App

Eine native Android-App für das Bitcoin Dashboard, optimiert für **Snapdragon 8** (arm64-v8a).

---

## 📱 Features

- **Live Bitcoin-Preis** via Binance WebSocket (Echtzeit)
- **Fear & Greed Index** mit Gauge-Anzeige und 30-Tage-Verlauf
- **Sentiment-Analyse** (Retail / Institutional / IBIT)
- **Blocktrainer News Feed** — aktuelle Bitcoin-Nachrichten auf Deutsch
- **Interaktive Charts** mit Timeframe-Auswahl (Stunden / Tage / Wochen / Jahre)
- Swipe-to-Refresh zum manuellen Aktualisieren
- Dark Theme optimiert für OLED-Displays

---

## 🛠️ Tech Stack

| Komponente | Technologie |
|---|---|
| App-Wrapper | Kotlin + Android WebView |
| Frontend | HTML5 / CSS3 / Vanilla JS |
| Preis-API | Binance WebSocket |
| Fear & Greed | alternative.me API |
| News | Blocktrainer RSS Feed |
| GPU-Rendering | Adreno (Snapdragon-optimiert) |

---

## 🚀 APK bauen

### Option 1 — GitHub Actions (empfohlen, kein Setup nötig)

1. Repository auf GitHub erstellen
2. Diesen Ordner pushen:
   ```bash
   git init
   git add .
   git commit -m "Initial commit"
   git remote add origin https://github.com/DEIN-USER/bitcoin-dashboard.git
   git push -u origin main
   ```
3. GitHub → **Actions** Tab → Build startet automatisch (~3–5 Min)
4. Fertiger APK unter **Actions → Artifacts → BitcoinDashboard-APK** herunterladen

### Option 2 — Lokal mit Android Studio

**Voraussetzungen:**
- Android Studio Hedgehog oder neuer
- JDK 17
- Android SDK (API 34)

**Schritte:**
1. Projekt öffnen: `File → Open` → diesen Ordner auswählen
2. Gradle sync abwarten
3. Gerät anschließen oder Emulator starten
4. `Run → Run 'app'` (▶️)

---

## 📲 APK installieren

1. APK-Datei auf das Android-Gerät übertragen (USB oder Cloud)
2. Einstellungen → Sicherheit → **Installation aus unbekannten Quellen** aktivieren
3. APK-Datei antippen und installieren

> ⚠️ Die App ist nicht signiert (Debug-Build). Für eine Release-Version muss ein Keystore eingerichtet werden.

---

## 📁 Projektstruktur

```
BitcoinApp/
├── .github/
│   └── workflows/
│       └── build.yml          # GitHub Actions CI/CD
├── app/
│   └── src/main/
│       ├── assets/
│       │   └── index.html     # Bitcoin Dashboard (HTML/JS/CSS)
│       ├── java/de/bitcoindashboard/
│       │   └── MainActivity.kt
│       ├── res/
│       │   ├── layout/        # XML Layouts
│       │   ├── values/        # Themes, Colors, Strings
│       │   └── xml/           # Network Security Config
│       └── AndroidManifest.xml
├── gradle/
│   └── libs.versions.toml     # Dependency Versions
├── build.gradle.kts
└── settings.gradle.kts
```

---

## ⚙️ Snapdragon 8 Optimierungen

- **ABI Filter:** Nur `arm64-v8a` (kein unnötiger x86-Code)
- **Hardware-Beschleunigung:** Adreno GPU via `LAYER_TYPE_HARDWARE`
- **R8 Minification:** Aktiviert für kleinere APK-Größe
- **RenderPriority.HIGH:** Für flüssiges Chart-Rendering

---

## 🔒 Netzwerk-Whitelist

Folgende Domains sind in der `network_security_config.xml` freigegeben:

| Domain | Zweck |
|---|---|
| `api.binance.com` / `stream.binance.com` | Live-Preisdaten |
| `alternative.me` | Fear & Greed Index |
| `blocktrainer.de` | News RSS Feed |
| `query1.finance.yahoo.com` | Historische Kursdaten |
| `allorigins.win` / `corsproxy.io` | CORS-Proxy für RSS |

---

## 📋 Anforderungen

- Android 10+ (API Level 29)
- Snapdragon 8 empfohlen (läuft auch auf anderen ARM64-Geräten)
- Internetverbindung erforderlich

---

## 📄 Lizenz

Privates Projekt — kein öffentliches Release.
