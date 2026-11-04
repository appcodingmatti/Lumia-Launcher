# Aurora Launcher (Lumia) 🌌

> Ein eleganter, werbefreier und blitzschneller Launcher für **Google TV & Android TV**, maßgeschneidert und optimiert für den **Philips 65OLED850** (Android 14 / API 34).

---

## 📺 Übersicht & Philosophie

Der **Aurora Launcher** (Projektname: *Lumia*) wurde von Grund auf für ein modernes, klares Fernseherlebnis entwickelt. Im Gegensatz zum überladenen Standard-Startbildschirm verzichtet Aurora auf Werbung, gesponserte Video-Vorschauen und visuelles Rauschen. 

### Highlights
- **OLED-optimiert:** Echtes Tiefschwarz, transparente System-Hintergründe und dezente Verläufe verhindern Bild-Einbrennen (Burn-In-Schutz) und schonen das Display.
- **3-Meter-Ergonomie (10-Foot UI):** Klare Typografie, großzügige Touch-/Fokus-Ziele und deutliche visuelle Rückmeldungen garantieren perfekte Ablesbarkeit vom Sofa aus.
- **100 % Fernbedienungs-freundlich:** Vollständig auf D-Pad-Steuerung (Hoch, Runter, Links, Rechts, OK, Zurück) ausgelegt – ohne Maus-Zeiger oder Touch-Abhängigkeiten.
- **Sideload-fähig:** Konzipiert für die unkomplizierte Installation per USB-Stick ohne Google Play Store oder ADB-Kabelverbindung.

---

## ✨ Funktionen im Detail

### 1. Intelligente App-Verwaltung
- **Automatischer Scan:** Erkennt alle auf dem Fernseher installierten TV- und Android-Apps über den `PackageManager` (mittels `QUERY_ALL_PACKAGES` und Leanback-/Launcher-Intents).
- **Zwei-Ebenen-Layout:**
  - **Obere Reihe:** Schneller Zugriff auf persönliche Favoriten (`FavoritesRow`).
  - **Untere Ansicht:** Vollständiges Raster aller installierten Anwendungen (`AllAppsGrid`), alphabetisch sortiert.
- **Flüssiger Fokus-Wechsel:** Beim Drücken von *D-Pad Runter* springt der Fokus nahtlos von der Favoriten-Leiste in das App-Grid.

### 2. D-Pad Kontextmenü (Langer Klick auf OK)
Wird auf einer App-Kachel die OK-Taste lang gedrückt gehalten, öffnet sich ein elegantes Menü mit vollem Funktionsumfang:
- ⭐ **Favoriten umschalten:** App mit einem Klick zu den Favoriten hinzufügen oder daraus entfernen.
- ⬅️ ➡️ **Favoriten umsortieren:** App in der Favoritenleiste nach links oder rechts verschieben.
- ✏️ **App umbenennen:** Beliebigen Anzeigenamen für die Kachel vergeben (über einen TV-optimierten Tastatur-Dialog).
- 🎨 **Kachel-Stil & Brand-Logo anpassen:**
  - Auswahl aus Marken-Presets (YouTube, Netflix, Prime Video, Plex, Twitch, Spotify, DAZN, Disney+, etc.).
  - 8 kuratierte, augenschonende OLED-Farben für den Kachelhintergrund.
- 🔄 **Anpassungen zurücksetzen:** Stellt Original-Name und Original-Icon der App wieder her.

### 3. TV-Status & Schnelleinstellungen
- **Live-Uhr & Datum:** Präzise digitale Zeitanzeige und formatierter Wochentag im oberen Bereich.
- **Netzwerkstatus:** Schneller visueller Indikator für WLAN- und Netzwerkverbindung.
- **Direktzugriff:** Schnelltasten zum Öffnen der Android TV-Systemeinstellungen und der Netzwerkkonfiguration.

---

## 🎮 Steuerung per Fernbedienung (D-Pad)

| Taste | Aktion |
| :--- | :--- |
| **D-Pad Richtungstasten** | Navigation zwischen den Kacheln und Menüoptionen |
| **D-Pad OK (Kurzer Klick)** | Ausgewählte App sofort starten |
| **D-Pad OK (Langer Klick)** | Kontextmenü für die ausgewählte App öffnen |
| **D-Pad Zurück (Back)** | Dialoge / Menüs schließen bzw. Fokus nach oben bringen |

### Fokus-Effekt
- Aktive Kacheln vergrößern sich sanft um den Faktor **1.1x** (`scale = 1.1f`).
- Ein leuchtender, kontrastreicher Fokus-Rahmen hebt das aktuell gewählte Element unmissverständlich hervor.

---

## 📦 Installation per USB-Stick (Sideload-Anleitung)

Die Installation auf dem **Philips 65OLED850 (Google TV)** erfolgt komfortabel ohne PC oder Entwickler-Tools über einen herkömmlichen USB-Stick:

### Schritt 1: APK vorbereiten
1. Die kompilierte APK (`app-debug.apk` oder signierte Release-APK) generieren.
2. Die Datei auf einen mit **FAT32** oder **exFAT** formatierten USB-Stick kopieren.

### Schritt 2: Fernseher vorbereiten (Entwickleroptionen)
1. Am Fernseher auf das **Zahnrad-Symbol** (Einstellungen) gehen.
2. Zu **System > Über das Gerät (Info)** navigieren.
3. Nach unten zu **Android TV-Betriebssystem-Build** scrollen und **7-mal hintereinander die OK-Taste drücken**, bis die Meldung *"Sie sind jetzt Entwickler"* erscheint.

### Schritt 3: Dateimanager & Installation
1. Im Google Play Store auf dem Fernseher einen Dateimanager installieren (empfohlen: **AnExplorer**, **File Commander** oder **FX File Explorer**).
2. Zu **Einstellungen > Apps > Sicherheit & Einschränkungen > Unbekannte Apps installieren** navigieren.
3. Dem installierten Dateimanager die Berechtigung erteilen, APK-Dateien zu installieren.
4. Den USB-Stick in einen USB-Port des Philips OLED stecken.
5. Den Dateimanager öffnen, auf den USB-Stick wechseln und die `Aurora_Launcher.apk` auswählen.
6. Auf **Installieren** klicken. Nach Abschluss kann die App direkt geöffnet werden.

> **Hinweis zu den Entwicklungsphasen:**  
> Aurora startet aktuell als normale Anwendung in Ihrer App-Liste. So können Sie den Launcher risikofrei testen und personalisieren. Die Konfiguration als Standard-Home-Launcher (`HOME`-Intent) wird erst aktiviert, wenn das System vollständig Ihren Wünschen entspricht.

---

## 🛠️ Technische Details & Architektur

- **Plattform:** Android TV / Google TV
- **Min SDK:** 28 (Android 9.0 Pie)
- **Target SDK / Compile SDK:** 34 (Android 14)
- **Programmiersprache:** Kotlin
- **UI-Framework:** [Jetpack Compose for TV](https://developer.android.com/jetpack/androidx/releases/tv) (`androidx.tv:tv-foundation`, `androidx.tv:tv-material`)
- **Design System:** Material Design 3 (M3)
- **Persistenz:** Lokale `SharedPreferences` für blitzschnelles Laden von:
  - Favoriten-Reihenfolge
  - Benutzerdefinierten App-Namen
  - Benutzerdefinierten Brand-Logos und Hintergrundfarben
- **Overscan-Schutz:** Standardisierte Ränder (58 dp horizontal, 28 dp vertikal) gemäß Google TV-Richtlinien, damit an Bildschirmrändern nichts abgeschnitten wird.

---

## 📂 Projektstruktur

```
├── app/
│   ├── src/main/
│   │   ├── AndroidManifest.xml          # TV Leanback-Deklarationen & Permissions
│   │   ├── java/com/example/
│   │   │   ├── MainActivity.kt          # Haupteinstiegspunkt & D-Pad-Handling
│   │   │   ├── data/
│   │   │   │   ├── FavoritesPreferences.kt        # Speicherung der Favoritenliste
│   │   │   │   └── AppCustomizationPreferences.kt # Speicherung von Namen & Farben
│   │   │   ├── model/
│   │   │   │   └── AppItem.kt           # Datenmodell für TV-Apps & Brands
│   │   │   ├── ui/
│   │   │   │   ├── components/
│   │   │   │   │   ├── AppCard.kt       # Animierte D-Pad Fokus-Kachel
│   │   │   │   │   ├── FavoritesRow.kt  # Horizontale Favoritenleiste
│   │   │   │   │   ├── AllAppsGrid.kt   # Vertikales Raster aller Apps
│   │   │   │   │   ├── TopBar.kt        # Uhrzeit, Status & Schnelleinstellungen
│   │   │   │   │   ├── AppOptionsDialog.kt    # Haupt-Kontextmenü (Long-Press)
│   │   │   │   │   ├── EditAppNameDialog.kt   # Dialog zum Umbenennen
│   │   │   │   │   └── EditAppStyleDialog.kt  # Style-Studio für Farben & Logos
│   │   │   │   ├── screens/
│   │   │   │   │   └── HomeScreen.kt    # Gesamte Startseite
│   │   │   │   └── theme/
│   │   │   │       ├── Color.kt         # OLED-Farbpalette & Akzente
│   │   │   │       ├── Theme.kt         # TV-Theme & Window-Transparenz
│   │   │   │       └── Type.kt          # TV-lesbare Typografie
│   │   │   └── viewmodel/
│   │   │       └── LauncherViewModel.kt # App-Scan, Sortierung & Zustand
│   │   └── res/                         # Banner, Icons & Strings
│   └── build.gradle.kts
└── README.md
```

---

## 💡 Geplante Erweiterungen (Roadmap)
- 🌤️ **Wetter-Widget:** Wetterabfrage über Open-Meteo mit Standort-Abfrage oder manueller Ortseingabe.
- 🖼️ **Hintergrund-Galerie:** Umschalten zwischen transparentem System-Wallpaper und hochauflösenden 4K-Hintergrundbildern.
- 👁️ **Apps ausblenden:** Möglichkeit, System-Apps oder ungenutzte Einträge im All-Apps-Grid zu verstecken.
- 🏠 **Standard-Launcher-Modus:** Offizielle Abfrage zur Festlegung als primärer Home-Launcher.
