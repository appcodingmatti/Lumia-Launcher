package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.AppItem
import com.example.ui.components.AllAppsGrid
import com.example.ui.components.AppOptionsDialog
import com.example.ui.components.EditAppNameDialog
import com.example.ui.components.EditAppStyleDialog
import com.example.ui.components.FavoritesRow
import com.example.ui.components.TopBar
import com.example.viewmodel.LauncherViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Main Android TV Launcher Home Screen for Lumia.
 * Built with Jetpack Compose for TV following the layout in IMG_4041, IMG_4042, and IMG_4043.
 *
 * Controls & Features:
 * - TopBar: Weather widget (Icon + Temp), Formatted Date/Weekday, Network status, Digital Clock, Settings & Wifi.
 * - Short press: Launches the focused app immediately.
 * - Long press: Opens an elegant centered options dialog.
 * - Dialog options:
 *   - "Zu Favoriten hinzufügen" / "Aus Favoriten entfernen"
 *   - "Nach links verschieben" / "Nach rechts verschieben" (reorders favorites in SharedPreferences)
 *   - "App öffnen" & "Abbrechen"
 */
@Composable
fun HomeScreen(
  viewModel: LauncherViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  val listState = rememberLazyListState()

  val currentTime by viewModel.currentTime.collectAsState()
  val currentDate by viewModel.currentDate.collectAsState()
  val weatherInfo by viewModel.weatherInfo.collectAsState()
  val favorites by viewModel.favoriteApps.collectAsState()
  val allApps by viewModel.allApps.collectAsState()
  val showScenicWallpaper by viewModel.showScenicWallpaper.collectAsState()

  var selectedAppForDialog by remember { mutableStateOf<AppItem?>(null) }
  var appToRename by remember { mutableStateOf<AppItem?>(null) }
  var appToStyle by remember { mutableStateOf<AppItem?>(null) }
  val firstFavoriteFocusRequester = remember { FocusRequester() }

  // Initial focus requested on the first favorite app card for immediate D-pad readiness
  LaunchedEffect(Unit) {
    delay(250)
    try {
      firstFavoriteFocusRequester.requestFocus()
    } catch (_: Exception) {
      // Ignored if view hierarchy is still settling
    }
  }

  // Handle remote Back button: dismiss modals or return to top favorites shelf
  BackHandler {
    when {
      appToRename != null -> appToRename = null
      appToStyle != null -> appToStyle = null
      selectedAppForDialog != null -> selectedAppForDialog = null
      else -> {
        coroutineScope.launch {
          listState.animateScrollToItem(0)
          try {
            firstFavoriteFocusRequester.requestFocus()
          } catch (_: Exception) {
            // Ignored
          }
        }
      }
    }
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .testTag("home_screen_root")
  ) {
    // Optional scenic wallpaper overlay: only visible if toggled for demo/preview.
    // By default, it is hidden so the window is 100% transparent and
    // the system TV wallpaper shines through as requested!
    AnimatedVisibility(
      visible = showScenicWallpaper,
      enter = fadeIn(),
      exit = fadeOut()
    ) {
      Image(
        painter = painterResource(id = R.drawable.bridge_wallpaper),
        contentDescription = "Scenic Wallpaper",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
      )
    }

    // Main Launcher UI with standard Android TV overscan safe padding
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(start = 48.dp, end = 48.dp, top = 28.dp, bottom = 16.dp)
    ) {
      // Pinned TopBar with Settings, Wifi, Date, Weather Widget, and Digital Clock
      TopBar(
        currentTime = currentTime,
        currentDate = currentDate,
        weatherInfo = weatherInfo,
        onOpenSettings = { viewModel.openSettings(context) },
        onOpenWifi = { viewModel.openWifiSettings(context) }
      )

      Spacer(modifier = Modifier.height(18.dp))

      // Vertical scrolling TvLazyColumn holding:
      // 1. Favorites shelf row
      // 2. 6-column Grid of all installed apps (alphabetically sorted)
      LazyColumn(
        state = listState,
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
          .testTag("tv_lazy_column"),
        contentPadding = PaddingValues(top = 12.dp, bottom = 48.dp)
      ) {
        // Item 1: Favoriten-Reihe (frosted shelf)
        item(key = "favorites_section") {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(bottom = 24.dp)
          ) {
            FavoritesRow(
              apps = favorites,
              onLaunchApp = { app -> viewModel.launchApp(context, app) },
              onOpenOptions = { app -> selectedAppForDialog = app },
              firstItemFocusRequester = firstFavoriteFocusRequester
            )
          }
        }

        // Item 2: All Installed Apps 6-column Grid (sorted alphabetically)
        item(key = "all_apps_grid_section") {
          AllAppsGrid(
            apps = allApps,
            onLaunchApp = { app -> viewModel.launchApp(context, app) },
            onOpenOptions = { app -> selectedAppForDialog = app },
            columnsCount = 6
          )
        }
      }
    }

    // Elegant Centered Options Dialog on TV Remote Long-Press
    selectedAppForDialog?.let { targetApp ->
      val freshApp = (allApps + favorites).firstOrNull { it.id == targetApp.id } ?: targetApp
      val favIndex = viewModel.getFavoriteIndex(freshApp)
      val totalFavs = favorites.size

      AppOptionsDialog(
        app = freshApp,
        favoriteIndex = favIndex,
        totalFavoritesCount = totalFavs,
        onDismiss = { selectedAppForDialog = null },
        onToggleFavorite = {
          viewModel.toggleFavorite(context, freshApp)
        },
        onMoveLeft = {
          viewModel.moveFavoriteLeft(freshApp)
        },
        onMoveRight = {
          viewModel.moveFavoriteRight(freshApp)
        },
        onRename = {
          appToRename = freshApp
        },
        onChangeStyle = {
          appToStyle = freshApp
        },
        onResetCustomization = {
          viewModel.resetAppCustomization(context, freshApp)
        },
        onLaunchApp = {
          viewModel.launchApp(context, freshApp)
        }
      )
    }

    // Modal Dialog: Rename App (Enter new name via TV keyboard)
    appToRename?.let { targetApp ->
      val freshApp = (allApps + favorites).firstOrNull { it.id == targetApp.id } ?: targetApp
      EditAppNameDialog(
        app = freshApp,
        onDismiss = { appToRename = null },
        onSave = { newName ->
          viewModel.renameApp(context, freshApp, newName)
        }
      )
    }

    // Modal Dialog: Change App Brand Style and Background Color
    appToStyle?.let { targetApp ->
      val freshApp = (allApps + favorites).firstOrNull { it.id == targetApp.id } ?: targetApp
      EditAppStyleDialog(
        app = freshApp,
        onDismiss = { appToStyle = null },
        onSave = { brand, color ->
          viewModel.changeAppStyle(context, freshApp, brand, color)
        },
        onReset = {
          viewModel.resetAppCustomization(context, freshApp)
        }
      )
    }
  }
}
