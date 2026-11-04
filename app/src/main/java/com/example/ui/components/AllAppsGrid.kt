package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.AppItem

/**
 * 6-column Grid of all installed TV apps matching IMG_4042 & IMG_4043.
 * Supports smooth D-Pad navigation between rows and columns, as well as
 * long-press to add or remove apps from Favorites.
 */
@Composable
fun AllAppsGrid(
  apps: List<AppItem>,
  onLaunchApp: (AppItem) -> Unit,
  modifier: Modifier = Modifier,
  onOpenOptions: ((AppItem) -> Unit)? = null,
  columnsCount: Int = 6
) {
  if (apps.isEmpty()) return

  val rows = apps.chunked(columnsCount)

  Column(
    modifier = modifier
      .fillMaxWidth()
      .testTag("all_apps_grid"),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    rows.forEachIndexed { rowIndex, rowApps ->
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("all_apps_row_$rowIndex"),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        rowApps.forEach { app ->
          AppCard(
            app = app,
            onLaunch = { onLaunchApp(app) },
            onLongClick = { onOpenOptions?.invoke(app) },
            modifier = Modifier.weight(1f)
          )
        }

        // Fill remaining spaces in the last row to preserve alignment
        if (rowApps.size < columnsCount) {
          repeat(columnsCount - rowApps.size) {
            Spacer(modifier = Modifier.weight(1f))
          }
        }
      }
    }
  }
}
