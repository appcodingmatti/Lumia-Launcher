package com.example.data

import android.content.Context
import android.content.SharedPreferences

/**
 * Manages persistent storage of favorite app package names using SharedPreferences.
 * Preserves the user's custom ordering of favorites and supports shifting positions left/right.
 */
class FavoritesPreferences(context: Context) {

  private val prefs: SharedPreferences =
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

  fun getFavoritePackages(): List<String> {
    val raw = prefs.getString(KEY_FAVORITES, null) ?: return emptyList()
    if (raw.isBlank()) return emptyList()
    return raw.split(",").map { it.trim() }.filter { it.isNotEmpty() }
  }

  fun setFavoritePackages(packages: List<String>) {
    val raw = packages.distinct().joinToString(",")
    prefs.edit().putString(KEY_FAVORITES, raw).apply()
  }

  fun addFavorite(packageName: String) {
    val current = getFavoritePackages().toMutableList()
    if (!current.contains(packageName)) {
      current.add(packageName)
      setFavoritePackages(current)
    }
  }

  fun removeFavorite(packageName: String) {
    val current = getFavoritePackages().toMutableList()
    if (current.remove(packageName)) {
      setFavoritePackages(current)
    }
  }

  fun toggleFavorite(packageName: String): Boolean {
    val current = getFavoritePackages().toMutableList()
    return if (current.contains(packageName)) {
      current.remove(packageName)
      setFavoritePackages(current)
      false
    } else {
      current.add(packageName)
      setFavoritePackages(current)
      true
    }
  }

  fun isFavorite(packageName: String): Boolean {
    return getFavoritePackages().contains(packageName)
  }

  fun getFavoriteIndex(packageName: String): Int {
    return getFavoritePackages().indexOf(packageName)
  }

  fun moveFavoriteLeft(packageName: String): Boolean {
    val current = getFavoritePackages().toMutableList()
    val index = current.indexOf(packageName)
    if (index > 0) {
      val item = current.removeAt(index)
      current.add(index - 1, item)
      setFavoritePackages(current)
      return true
    }
    return false
  }

  fun moveFavoriteRight(packageName: String): Boolean {
    val current = getFavoritePackages().toMutableList()
    val index = current.indexOf(packageName)
    if (index >= 0 && index < current.size - 1) {
      val item = current.removeAt(index)
      current.add(index + 1, item)
      setFavoritePackages(current)
      return true
    }
    return false
  }

  fun clear() {
    prefs.edit().remove(KEY_FAVORITES).apply()
  }

  companion object {
    private const val PREFS_NAME = "lumia_favorites_prefs"
    private const val KEY_FAVORITES = "favorite_package_names"
  }
}
