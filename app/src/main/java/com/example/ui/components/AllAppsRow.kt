package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AppItem

@Composable
fun AllAppsRow(
  apps: List<AppItem>,
  onLaunchApp: (AppItem) -> Unit,
  modifier: Modifier = Modifier
) {
  if (apps.isEmpty()) return

  Column(modifier = modifier.fillMaxWidth()) {
    Text(
      text = stringResource(R.string.all_apps),
      modifier = Modifier
        .testTag("all_apps_header")
        .padding(start = 12.dp, bottom = 8.dp),
      style = TextStyle(
        color = Color.White.copy(alpha = 0.85f),
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        shadow = Shadow(
          color = Color.Black.copy(alpha = 0.6f),
          offset = Offset(1f, 1f),
          blurRadius = 4f
        )
      )
    )

    LazyRow(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("all_apps_tv_lazy_row"),
      contentPadding = PaddingValues(horizontal = 8.dp),
      horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      items(apps, key = { it.id }) { app ->
        AppCard(
          app = app,
          onLaunch = { onLaunchApp(app) }
        )
      }
    }
  }
}
