# Implementierungsplan – App-Namen & App-Icons anpassen im Kontextmenü

Ermöglicht dem Nutzer, für jede beliebige App auf dem Android TV einen eigenen Anzeigenamen und ein individuelles Icon/Design (Marken-Presets, Hintergrundfarben und Stile) direkt über das Kontextmenü zu konfigurieren.

---

## Vorgeschlagene Änderungen

### 1. Persistente Speicherung von Anpassungen (`AppCustomizationPreferences.kt`)
- **`data/AppCustomizationPreferences.kt`**:
  - Speichert benutzerdefinierte Einstellungen pro Package-Name in `SharedPreferences`:
    - `customName`: Eigener Anzeigename (z.B. "Mein Kino" statt "Plex", "Mediathek" statt generischem Namen).
    - `customBrand`: Zugeordnetes Marken-Design (z.B. YouTube, Netflix, Prime Video, Plex, Twitch, Spotify, DAZN, etc.).
    - `customColor`: Farb-Override für die Kachel (z.B. Rot, Blau, Violett, Dunkelgrau, Smaragdgrün).
  - Methoden:
    - `saveCustomization(packageName: String, name: String?, brand: BrandType?, color: Color?)`
    - `getCustomization(packageName: String): AppCustomization?`
    - `resetCustomization(packageName: String)`

### 2. Integration in `LauncherViewModel.kt`
- Beim Laden und Partitionieren der Apps werden benutzerdefinierte Namen, Brandings und Farben automatisch auf die `AppItem`-Objekte angewendet.
- Neue ViewModel-Aktionen:
  - `renameApp(app: AppItem, newName: String)`
  - `changeAppStyle(app: AppItem, brand: BrandType, color: Color)`
  - `resetAppCustomization(app: AppItem)`
- Aktualisiert `favoriteApps` und `allApps` sofort reaktiv, sodass Änderungen in beiden Reihen ohne Neuladen sichtbar werden.

### 3. Erweiterung des Kontextdialogs (`AppOptionsDialog.kt`)
- Zwei neue Menüpunkte im Kontextmenü:
  - **„App umbenennen“** (mit Editier-Stift-Icon).
  - **„Design & Icon ändern“** (mit Farbpalette-Icon).
- Zeigt einen Zurücksetzen-Button an, falls für die App bereits Anpassungen hinterlegt wurden.

### 4. TV-optimierte Bearbeitungsdialoge
- **`EditAppNameDialog.kt`**:
  - Ein fokussierbares Eingabefeld mit Bestätigungs- und Abbrechen-Buttons.
  - Funktioniert mit der virtuellen TV-Tastatur von Android TV sowie Fernbedienungs-Eingaben.
- **`EditAppStyleDialog.kt`**:
  - **Marken-Presets:** Horizontale Auswahlliste beliebter TV-Brandings (Netflix, YouTube, Prime Video, Twitch, Spotify, Plex, DAZN, etc.).
  - **Farbpalette:** 8 kuratierte TV-Farben (Deep Black, Vibrant Purple, Crimson Red, Royal Blue, Neon Green, Sunset Amber, Clean White, Cool Slate).
  - Live-Vorschau der Kachel direkt im Dialog mit sofortigem Speichern bei Klick.

---

## Status: Vollständig abgeschlossen & verifiziert

- [x] Persistente Speicherung von Anpassungen (`AppCustomizationPreferences.kt`)
- [x] Integration in `LauncherViewModel.kt` mit reaktiven StateFlows
- [x] Erweiterung des Kontextmenüs (`AppOptionsDialog.kt`) um „App umbenennen“, „Icon & Stil anpassen“ und „Auf Standard zurücksetzen“
- [x] TV-optimierter Umbenennungs-Dialog (`EditAppNameDialog.kt`)
- [x] TV-optimiertes Icon- & Stil-Studio (`EditAppStyleDialog.kt`) mit Live-Kachelvorschau, Marken-Presets und Farbpalette
- [x] Vollständige D-Pad-Navigation und saubere Focus-Traversal
- [x] Automatisierte Tests: Alle Unit-Tests erfolgreich (`:app:testDebugUnitTest` bestanden in 10s)
- [x] Kompilierung: `compile_applet` erfolgreich ohne Fehler abgeschlossen
