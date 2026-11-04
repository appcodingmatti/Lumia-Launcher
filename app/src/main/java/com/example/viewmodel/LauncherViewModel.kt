package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.data.FavoritesPreferences
import com.example.model.AppItem
import com.example.model.BrandType
import com.example.model.WeatherCondition
import com.example.model.WeatherInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class LauncherViewModel(application: Application) : AndroidViewModel(application) {

  private val favoritesPrefs = FavoritesPreferences(application)

  private val _currentTime = MutableStateFlow(getCurrentFormattedTime())
  val currentTime: StateFlow<String> = _currentTime.asStateFlow()

  private val _currentDate = MutableStateFlow(getCurrentFormattedDate())
  val currentDate: StateFlow<String> = _currentDate.asStateFlow()

  private val _weatherInfo = MutableStateFlow(WeatherInfo())
  val weatherInfo: StateFlow<WeatherInfo> = _weatherInfo.asStateFlow()

  // 1. Favorites list (displayed in the top row)
  private val _favoriteApps = MutableStateFlow<List<AppItem>>(emptyList())
  val favoriteApps: StateFlow<List<AppItem>> = _favoriteApps.asStateFlow()

  // 2. All Apps list (displayed in the grid below, alphabetically sorted)
  private val _allApps = MutableStateFlow<List<AppItem>>(emptyList())
  val allApps: StateFlow<List<AppItem>> = _allApps.asStateFlow()

  private val _showScenicWallpaper = MutableStateFlow(false)
  val showScenicWallpaper: StateFlow<Boolean> = _showScenicWallpaper.asStateFlow()

  // Raw catalog of discovered apps
  private var rawDiscoveredApps: List<AppItem> = emptyList()

  init {
    startClockUpdater()
    loadInstalledApps()
    fetchWeatherData()
  }

  private fun getCurrentFormattedTime(): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
    } else {
      val now = java.util.Calendar.getInstance()
      String.format(
        Locale.getDefault(),
        "%02d:%02d",
        now.get(java.util.Calendar.HOUR_OF_DAY),
        now.get(java.util.Calendar.MINUTE)
      )
    }
  }

  private fun getCurrentFormattedDate(): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d. MMM", Locale.getDefault()))
    } else {
      val now = java.util.Calendar.getInstance()
      val sdf = java.text.SimpleDateFormat("EEEE, d. MMM", Locale.getDefault())
      sdf.format(now.time)
    }
  }

  private fun startClockUpdater() {
    viewModelScope.launch {
      while (isActive) {
        _currentTime.value = getCurrentFormattedTime()
        _currentDate.value = getCurrentFormattedDate()
        delay(1000L)
      }
    }
  }

  private fun fetchWeatherData() {
    viewModelScope.launch(Dispatchers.IO) {
      try {
        val url = URL("https://api.open-meteo.com/v1/forecast?latitude=52.52&longitude=13.41&current_weather=true")
        val conn = (url.openConnection() as HttpURLConnection).apply {
          requestMethod = "GET"
          connectTimeout = 6000
          readTimeout = 6000
        }

        if (conn.responseCode == HttpURLConnection.HTTP_OK) {
          val response = conn.inputStream.bufferedReader().use { it.readText() }
          val json = JSONObject(response)
          val current = json.optJSONObject("current_weather")
          if (current != null) {
            val temp = current.optDouble("temperature", 21.0)
            val code = current.optInt("weathercode", 2)
            val condition = mapWeatherCodeToCondition(code)
            withContext(Dispatchers.Main) {
              _weatherInfo.value = WeatherInfo(
                temperature = "${temp.toInt()}°C",
                condition = condition
              )
            }
          }
        }
      } catch (_: Exception) {
        // Fallback to default pleasant TV weather if network is unavailable
      }
    }
  }

  private fun mapWeatherCodeToCondition(code: Int): WeatherCondition {
    return when (code) {
      0 -> WeatherCondition.SUNNY
      1, 2 -> WeatherCondition.PARTLY_CLOUDY
      3, 45, 48 -> WeatherCondition.CLOUDY
      51, 53, 55, 61, 63, 65, 80, 81, 82 -> WeatherCondition.RAINY
      71, 73, 75, 77, 85, 86 -> WeatherCondition.SNOWY
      95, 96, 99 -> WeatherCondition.THUNDERSTORM
      else -> WeatherCondition.PARTLY_CLOUDY
    }
  }

  /**
   * Queries PackageManager for all installed apps that have a launch intent on TV,
   * strictly excluding Lumia itself.
   * Then splits them into Favorites and All Apps based on SharedPreferences.
   */
  fun loadInstalledApps() {
    viewModelScope.launch {
      val context = getApplication<Application>()
      val pm = context.packageManager

      val leanbackIntent = Intent(Intent.ACTION_MAIN).apply {
        addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER)
      }
      val launcherIntent = Intent(Intent.ACTION_MAIN).apply {
        addCategory(Intent.CATEGORY_LAUNCHER)
      }

      val leanbackApps = pm.queryIntentActivities(leanbackIntent, PackageManager.MATCH_ALL)
      val regularApps = pm.queryIntentActivities(launcherIntent, PackageManager.MATCH_ALL)

      val allResolveInfos = (leanbackApps + regularApps).distinctBy { it.activityInfo.packageName }

      // Filter out Lumia launcher package
      val realInstalledApps = allResolveInfos
        .filter { it.activityInfo.packageName != context.packageName }
        .map { resolveInfo ->
          val pkg = resolveInfo.activityInfo.packageName
          val label = resolveInfo.loadLabel(pm).toString()
          val icon = resolveInfo.loadIcon(pm)

          val brand = detectBrand(pkg, label)
          val color = getCardColorForBrand(brand)

          AppItem(
            id = pkg,
            name = label,
            packageName = pkg,
            brandType = brand,
            cardColor = color,
            iconDrawable = icon,
            isFavorite = false
          )
        }

      // If on test emulator with very few installed apps, supplement with realistic TV apps
      val mergedCatalog = if (realInstalledApps.size < 18) {
        val existingPkgs = realInstalledApps.map { it.packageName }.toSet()
        val supplements = getSampleTvCatalog().filter { it.packageName !in existingPkgs }
        realInstalledApps + supplements
      } else {
        realInstalledApps
      }

      rawDiscoveredApps = mergedCatalog
      applyFavoritesPartitioning()
    }
  }

  /**
   * Partitions discovered apps into:
   * 1. 'Favoriten' (ordered according to user's saved SharedPreferences)
   * 2. 'Alle Apps' (alphabetically sorted list of all apps)
   */
  private fun applyFavoritesPartitioning() {
    val savedFavPkgs = favoritesPrefs.getFavoritePackages()
    val appsByPkg = rawDiscoveredApps.associateBy { it.packageName }

    // Map saved packages in their saved order
    val favList = savedFavPkgs.mapNotNull { pkg ->
      appsByPkg[pkg]?.copy(isFavorite = true)
    }

    // All apps sorted alphabetically
    val allList = rawDiscoveredApps.map { app ->
      app.copy(isFavorite = savedFavPkgs.contains(app.packageName))
    }.sortedBy { it.name.lowercase() }

    _favoriteApps.value = favList
    _allApps.value = allList
  }

  /**
   * Moves a favorite app one position to the left.
   */
  fun moveFavoriteLeft(app: AppItem) {
    if (favoritesPrefs.moveFavoriteLeft(app.packageName)) {
      applyFavoritesPartitioning()
    }
  }

  /**
   * Moves a favorite app one position to the right.
   */
  fun moveFavoriteRight(app: AppItem) {
    if (favoritesPrefs.moveFavoriteRight(app.packageName)) {
      applyFavoritesPartitioning()
    }
  }

  fun getFavoriteIndex(app: AppItem): Int {
    return favoritesPrefs.getFavoriteIndex(app.packageName)
  }

  /**
   * Toggles favorite status for an app and persists it to SharedPreferences.
   */
  fun toggleFavorite(context: Context, app: AppItem) {
    val isNowFavorite = favoritesPrefs.toggleFavorite(app.packageName)
    applyFavoritesPartitioning()

    val toastMsg = if (isNowFavorite) {
      context.getString(R.string.added_to_favorites, app.name)
    } else {
      context.getString(R.string.removed_from_favorites, app.name)
    }
    Toast.makeText(context, toastMsg, Toast.LENGTH_SHORT).show()
  }

  fun launchApp(context: Context, app: AppItem) {
    val pm = context.packageManager
    val intent = pm.getLeanbackLaunchIntentForPackage(app.packageName)
      ?: pm.getLaunchIntentForPackage(app.packageName)

    if (intent != null) {
      try {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
      } catch (e: Exception) {
        Toast.makeText(
          context,
          "${app.name}: ${e.localizedMessage ?: "Fehler beim Starten"}",
          Toast.LENGTH_SHORT
        ).show()
      }
    } else {
      val message = context.getString(R.string.app_not_installed) + " (${app.name})"
      Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
  }

  fun openSettings(context: Context) {
    try {
      val intent = Intent(Settings.ACTION_SETTINGS).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      context.startActivity(intent)
    } catch (e: Exception) {
      Toast.makeText(context, "Einstellungen konnten nicht geöffnet werden", Toast.LENGTH_SHORT).show()
    }
  }

  fun openWifiSettings(context: Context) {
    try {
      val intent = Intent(Settings.ACTION_WIFI_SETTINGS).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      context.startActivity(intent)
    } catch (_: Exception) {
      try {
        val wirelessIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS).apply {
          addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(wirelessIntent)
      } catch (e2: Exception) {
        Toast.makeText(context, "WLAN-Einstellungen nicht verfügbar", Toast.LENGTH_SHORT).show()
      }
    }
  }

  fun toggleScenicWallpaper() {
    _showScenicWallpaper.value = !_showScenicWallpaper.value
  }

  private fun detectBrand(packageName: String, label: String): BrandType {
    val lowerPkg = packageName.lowercase()
    val lowerLabel = label.lowercase()
    return when {
      lowerPkg.contains("youtube.music") || lowerLabel.contains("youtube music") -> BrandType.YOUTUBE_MUSIC
      lowerPkg.contains("youtube") || lowerLabel == "youtube" -> BrandType.YOUTUBE
      lowerPkg.contains("netflix") || lowerLabel == "netflix" -> BrandType.NETFLIX
      lowerPkg.contains("amazonvideo") || lowerLabel.contains("prime video") -> BrandType.PRIME_VIDEO
      lowerPkg.contains("plex") || lowerLabel == "plex" -> BrandType.PLEX
      lowerPkg.contains("twitch") || lowerLabel == "twitch" -> BrandType.TWITCH
      lowerPkg.contains("spotify") || lowerLabel == "spotify" -> BrandType.SPOTIFY
      lowerPkg.contains("localsend") || lowerLabel == "localsend" -> BrandType.LOCALSEND
      lowerPkg.contains("vending") || lowerLabel.contains("play store") -> BrandType.PLAY_STORE
      lowerPkg.contains("browser") || lowerLabel.contains("browsehere") -> BrandType.BROWSE_HERE
      lowerPkg.contains("dazn") || lowerLabel == "dazn" -> BrandType.DAZN
      lowerPkg.contains("settings") || lowerLabel.contains("einstellung") -> BrandType.SETTINGS_APP
      lowerPkg.contains("arc.launcher") || lowerLabel.contains("arc launcher") -> BrandType.ARC_LAUNCHER
      lowerPkg.contains("play.games") || lowerLabel.contains("play games") -> BrandType.PLAY_GAMES
      else -> BrandType.GENERIC_APP
    }
  }

  private fun getCardColorForBrand(brand: BrandType): Color {
    return when (brand) {
      BrandType.PLEX -> Color(0xFF141414)
      BrandType.YOUTUBE -> Color(0xFFFFFFFF)
      BrandType.YOUTUBE_MUSIC -> Color(0xFFFFFFFF)
      BrandType.TWITCH -> Color(0xFF9146FF)
      BrandType.FREE_TV -> Color(0xFF181818)
      BrandType.SPOTIFY -> Color(0xFF121212)
      BrandType.LOCALSEND -> Color(0xFFEDF2F4)
      BrandType.PRIME_VIDEO -> Color(0xFF0073D1)
      BrandType.NETFLIX -> Color(0xFFFFFFFF)
      BrandType.PLAY_STORE -> Color(0xFFFFFFFF)
      BrandType.BROWSE_HERE -> Color(0xFF2563EB)
      BrandType.DAZN -> Color(0xFF111111)
      BrandType.SETTINGS_APP -> Color(0xFF2A2D34)
      BrandType.ARC_LAUNCHER -> Color(0xFF0F172A)
      BrandType.PLAY_GAMES -> Color(0xFF1A1A1A)
      BrandType.GENERIC_APP -> Color(0xFF1E2228)
    }
  }

  private fun getSampleTvCatalog(): List<AppItem> {
    return listOf(
      AppItem("plex", "Plex", "com.plexapp.android", BrandType.PLEX, Color(0xFF141414)),
      AppItem("youtube_app", "YouTube", "com.google.android.youtube.tv", BrandType.YOUTUBE, Color(0xFFFFFFFF)),
      AppItem("yt_music", "YouTube Music", "com.google.android.apps.youtube.music.tv", BrandType.YOUTUBE_MUSIC, Color(0xFFFFFFFF)),
      AppItem("twitch", "Twitch", "tv.twitch.android.app", BrandType.TWITCH, Color(0xFF9146FF)),
      AppItem("free_tv", "Free TV", "de.freenet.tv", BrandType.FREE_TV, Color(0xFF181818)),
      AppItem("spotify", "Spotify", "com.spotify.tv.android", BrandType.SPOTIFY, Color(0xFF121212)),
      AppItem("localsend", "LocalSend", "org.localsend.localsend_app", BrandType.LOCALSEND, Color(0xFFEDF2F4)),
      AppItem("prime_video", "prime video", "com.amazon.amazonvideo.livingroom", BrandType.PRIME_VIDEO, Color(0xFF0073D1)),
      AppItem("netflix", "NETFLIX", "com.netflix.ninja", BrandType.NETFLIX, Color(0xFFFFFFFF)),
      AppItem("play_store", "Google Play Store", "com.android.vending", BrandType.PLAY_STORE, Color(0xFFFFFFFF)),
      AppItem("user_guide", "User Guide", "com.android.tv.userguide", BrandType.GENERIC_APP, Color(0xFF374151)),
      AppItem("quick_panel", "Quick panel", "com.android.tv.quickpanel", BrandType.GENERIC_APP, Color(0xFF4B5563)),
      AppItem("tv_input", "TV", "com.android.tv.input", BrandType.GENERIC_APP, Color(0xFF1C1917)),
      AppItem("settings_tile", "Settings", "com.android.tv.settings", BrandType.SETTINGS_APP, Color(0xFF2A2D34)),
      AppItem("at_4k", "at 4k", "com.example.at4k", BrandType.GENERIC_APP, Color(0xFF111827)),
      AppItem("fx", "FX", "com.example.fx", BrandType.GENERIC_APP, Color(0xFF1F2937)),
      AppItem("browsehere", "BrowseHere", "com.tcl.browser", BrandType.BROWSE_HERE, Color(0xFF2563EB)),
      AppItem("s_stream", "S", "com.example.sstream", BrandType.GENERIC_APP, Color(0xFF7F1D1D)),
      AppItem("play_games", "Play Games", "com.google.android.play.games", BrandType.PLAY_GAMES, Color(0xFF1F2937)),
      AppItem("safety_guard", "Safety Guard", "com.example.safetyguard", BrandType.GENERIC_APP, Color(0xFF3B82F6)),
      AppItem("dazn", "DAZN", "com.dazn", BrandType.DAZN, Color(0xFF111111)),
      AppItem("fancode", "FANCODE", "com.fancode", BrandType.GENERIC_APP, Color(0xFFEA580C)),
      AppItem("arc_launcher", "arc launcher", "com.arc.launcher", BrandType.ARC_LAUNCHER, Color(0xFF0F172A)),
      AppItem("button_mapper", "Button Mapper", "flar2.homebutton", BrandType.GENERIC_APP, Color(0xFF0284C7)),
      AppItem("jiocinema", "JioCinema", "com.jio.media.ondemand", BrandType.GENERIC_APP, Color(0xFFBE185D)),
      AppItem("televizo", "TELEVIZO", "com.televizo", BrandType.GENERIC_APP, Color(0xFF18181B)),
      AppItem("sonyliv", "SONY liv", "com.msliv.tv", BrandType.GENERIC_APP, Color(0xFF09090B)),
      AppItem("alexa", "works with alexa", "com.amazon.dee.app", BrandType.GENERIC_APP, Color(0xFF0E7490)),
      AppItem("ottplay", "OTTplay", "com.ottplay", BrandType.GENERIC_APP, Color(0xFF312E81))
    )
  }
}
