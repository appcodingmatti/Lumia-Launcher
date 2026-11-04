# Implementierungsplan – Lumia Android TV Launcher

Integrieren der beiden vom Nutzer ausgewählten Kernfeatures:
1. **Favoriten umsortieren:** Im bestehenden App-Optionsdialog (Long Press) Optionen zum Verschieben nach links oder rechts anbieten, mit direkter Speicherung in `SharedPreferences`.
2. **Wetteranzeige & erweiterte Top-Bar-Infos:** Ein elegantes TV-Wetter-Widget in der Top-Bar (Icon + Temperatur) sowie Datum mit Wochentag und Netzwerk-Status.

---

## Vorgeschlagene Änderungen

### 1. Favoriten-Umsortierung in `FavoritesPreferences` & `LauncherViewModel`
- **`data/FavoritesPreferences.kt`**:
  - `moveFavoriteLeft(packageName: String)`: Tauscht das Element mit dem linken Nachbarn in der gespeicherten Liste.
  - `moveFavoriteRight(packageName: String)`: Tauscht das Element mit dem rechten Nachbarn.
- **`viewmodel/LauncherViewModel.kt`**:
  - `moveFavoriteLeft(app: AppItem)` & `moveFavoriteRight(app: AppItem)`
  - Ruft `FavoritesPreferences` auf und triggert sofort `applyFavoritesPartitioning()`.

### 2. Erweiterung des Kontextdialogs (`AppOptionsDialog.kt`)
- Wenn `app.isFavorite` wahr ist:
  - Button **„Nach links verschieben“** (Pfeil links), aktiviert wenn Index > 0.
  - Button **„Nach rechts verschieben“** (Pfeil rechts), aktiviert wenn Index < Liste.size - 1.
- Beide Buttons sind vollständig über das D-Pad bedienbar. Bei Klick wird die Liste sofort neu angeordnet und die Position in der Favoriten-Reihe animiert angepasst.

### 3. Wetteranzeige & erweiterte Top-Bar (`TopBar.kt` & `WeatherService`)
- **Wetter-Logik (`LauncherViewModel` / `WeatherData`):**
  - Anbindung an die kostenlose, API-Key-freie **Open-Meteo API** (oder lokale TV-Lokalisierungsdaten) mit Coroutine Flow und Caching.
  - Datenmodell: Temperatur (°C), Wetterlage (Sonnig, Bewölkt, Regen, Gewitter etc.) und passendes Vector-Icon.
- **Top-Bar UI (`TopBar.kt`):**
  - **Rechts:** Datum mit Wochentag (z.B. "Donnerstag, 7. Nov") + Wetter-Icon & Temperatur (z.B. ☀️ 21°C) + große digitale Uhr (`HH:mm`).
  - **Links:** Quick-Settings-Buttons (Einstellungen & WLAN) mit visuellem WLAN-Verbindungsstatus.

---

## Verifizierungsplan

### Automatisierte Tests
- Ausführen von `compile_applet` zur Validierung fehlerfreier Kotlin- und Compose-Kompilierung.
- Ergänzen von Unit-Tests in `LauncherUnitTest.kt`:
  - Test für `moveFavoriteLeft` (Prüfung von Randbedingungen bei Index 0 und Listenmitte).
  - Test für `moveFavoriteRight` (Prüfung von Randbedingungen beim letzten Element).
- Ausführen von `gradle :app:testDebugUnitTest`.

### Manuelle / TV-Verifikation
- Öffnen des Kontextmenüs auf einer Favoriten-Kachel via Long-Press auf der Fernbedienung.
- Auswählen von „Nach links verschieben“ oder „Nach rechts verschieben“ und Überprüfung, ob die Kachel sofort in der oberen Reihe die Position wechselt.
- Prüfen, ob die Position nach App-Neustart erhalten bleibt.
- Prüfen der Lesbarkeit des Wetter-Widgets und der Zeitanzeige auf dem TV-Bildschirm.
