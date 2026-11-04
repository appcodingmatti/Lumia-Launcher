package com.example.model

import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.Color

enum class BrandType {
  PLEX,
  YOUTUBE,
  YOUTUBE_MUSIC,
  TWITCH,
  FREE_TV,
  SPOTIFY,
  LOCALSEND,
  PRIME_VIDEO,
  NETFLIX,
  PLAY_STORE,
  BROWSE_HERE,
  DAZN,
  SETTINGS_APP,
  ARC_LAUNCHER,
  PLAY_GAMES,
  GENERIC_APP
}

data class AppItem(
  val id: String,
  val name: String,
  val packageName: String,
  val brandType: BrandType = BrandType.GENERIC_APP,
  val cardColor: Color = Color(0xFF1E2228),
  val iconDrawable: Drawable? = null,
  val isFavorite: Boolean = false
)
