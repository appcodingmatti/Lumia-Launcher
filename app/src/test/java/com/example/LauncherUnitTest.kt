package com.example

import com.example.model.AppItem
import com.example.model.BrandType
import com.example.model.WeatherCondition
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LauncherUnitTest {

  @Test
  fun testAppItemProperties() {
    val plexItem = AppItem(
      id = "plex",
      name = "Plex",
      packageName = "com.plexapp.android",
      brandType = BrandType.PLEX,
      isFavorite = true
    )

    assertEquals("Plex", plexItem.name)
    assertEquals(BrandType.PLEX, plexItem.brandType)
    assertEquals("com.plexapp.android", plexItem.packageName)
    assertTrue(plexItem.isFavorite)
  }

  @Test
  fun testBrandTypeValues() {
    val brands = BrandType.values().map { it.name }
    assertTrue(brands.contains("PLEX"))
    assertTrue(brands.contains("YOUTUBE"))
    assertTrue(brands.contains("YOUTUBE_MUSIC"))
    assertTrue(brands.contains("PRIME_VIDEO"))
    assertTrue(brands.contains("NETFLIX"))
    assertTrue(brands.contains("PLAY_STORE"))
    assertTrue(brands.contains("TWITCH"))
    assertTrue(brands.contains("FREE_TV"))
    assertTrue(brands.contains("SPOTIFY"))
    assertTrue(brands.contains("LOCALSEND"))
  }

  @Test
  fun testFavoritesPartitioningLogic() {
    val sampleApps = listOf(
      AppItem("yt", "YouTube", "com.google.android.youtube.tv"),
      AppItem("netflix", "Netflix", "com.netflix.ninja"),
      AppItem("plex", "Plex", "com.plexapp.android"),
      AppItem("spotify", "Spotify", "com.spotify.tv.android")
    )

    val savedFavorites = listOf("com.netflix.ninja", "com.plexapp.android")
    val appsByPkg = sampleApps.associateBy { it.packageName }

    // Favorites in saved order
    val favorites = savedFavorites.mapNotNull { pkg ->
      appsByPkg[pkg]?.copy(isFavorite = true)
    }

    assertEquals(2, favorites.size)
    assertEquals("Netflix", favorites[0].name)
    assertEquals("Plex", favorites[1].name)
    assertTrue(favorites[0].isFavorite)
    assertTrue(favorites[1].isFavorite)

    // All apps alphabetically sorted
    val allApps = sampleApps.map { app ->
      app.copy(isFavorite = savedFavorites.contains(app.packageName))
    }.sortedBy { it.name.lowercase() }

    assertEquals(4, allApps.size)
    assertEquals("Netflix", allApps[0].name)
    assertEquals("Plex", allApps[1].name)
    assertEquals("Spotify", allApps[2].name)
    assertEquals("YouTube", allApps[3].name)

    assertTrue(allApps[0].isFavorite)
    assertTrue(allApps[1].isFavorite)
    assertFalse(allApps[2].isFavorite)
    assertFalse(allApps[3].isFavorite)
  }

  @Test
  fun testReorderingLogic() {
    val list = mutableListOf("pkg_a", "pkg_b", "pkg_c")

    // Move "pkg_b" left -> should become ["pkg_b", "pkg_a", "pkg_c"]
    val indexB = list.indexOf("pkg_b")
    assertTrue(indexB > 0)
    val itemB = list.removeAt(indexB)
    list.add(indexB - 1, itemB)
    assertEquals(listOf("pkg_b", "pkg_a", "pkg_c"), list)

    // Move "pkg_b" left again -> index is 0, cannot move left
    val newIndexB = list.indexOf("pkg_b")
    assertFalse(newIndexB > 0)

    // Move "pkg_a" right -> index is 1, size is 3 -> can move right to index 2
    val indexA = list.indexOf("pkg_a")
    assertTrue(indexA < list.size - 1)
    val itemA = list.removeAt(indexA)
    list.add(indexA + 1, itemA)
    assertEquals(listOf("pkg_b", "pkg_c", "pkg_a"), list)

    // Move "pkg_a" right again -> already at end, cannot move right
    val newIndexA = list.indexOf("pkg_a")
    assertFalse(newIndexA < list.size - 1)
  }

  @Test
  fun testWeatherConditionValues() {
    val conditions = WeatherCondition.values().map { it.name }
    assertTrue(conditions.contains("SUNNY"))
    assertTrue(conditions.contains("PARTLY_CLOUDY"))
    assertTrue(conditions.contains("CLOUDY"))
    assertTrue(conditions.contains("RAINY"))
    assertTrue(conditions.contains("THUNDERSTORM"))
    assertTrue(conditions.contains("SNOWY"))
  }
}
