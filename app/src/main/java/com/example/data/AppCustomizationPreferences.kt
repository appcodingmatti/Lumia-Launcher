package com.example.data

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.ui.graphics.Color
import com.example.model.BrandType
import org.json.JSONObject

data class AppCustomization(
  val customName: String? = null,
  val customBrand: BrandType? = null,
  val customColor: Color? = null
)

/**
 * Manages persistent storage of user-customized app names, icon styles, and colors
 * using SharedPreferences.
 */
class AppCustomizationPreferences(context: Context) {

  private val prefs: SharedPreferences =
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

  fun getCustomization(packageName: String): AppCustomization? {
    val raw = prefs.getString(packageName, null) ?: return null
    return try {
      val json = JSONObject(raw)
      val name = if (json.has("name") && !json.isNull("name")) json.getString("name") else null
      val brandStr = if (json.has("brand") && !json.isNull("brand")) json.getString("brand") else null
      val brand = brandStr?.let { runCatching { BrandType.valueOf(it) }.getOrNull() }
      val colorLong = if (json.has("color") && !json.isNull("color")) json.getLong("color") else null
      val color = colorLong?.let { Color(it.toULong()) }

      if (name != null || brand != null || color != null) {
        AppCustomization(customName = name, customBrand = brand, customColor = color)
      } else {
        null
      }
    } catch (_: Exception) {
      null
    }
  }

  fun saveCustomName(packageName: String, newName: String?) {
    val current = getCustomization(packageName) ?: AppCustomization()
    val updated = current.copy(customName = if (newName.isNullOrBlank()) null else newName.trim())
    save(packageName, updated)
  }

  fun saveCustomStyle(packageName: String, brand: BrandType?, color: Color?) {
    val current = getCustomization(packageName) ?: AppCustomization()
    val updated = current.copy(customBrand = brand, customColor = color)
    save(packageName, updated)
  }

  fun resetCustomization(packageName: String) {
    prefs.edit().remove(packageName).apply()
  }

  fun hasCustomization(packageName: String): Boolean {
    return prefs.contains(packageName)
  }

  private fun save(packageName: String, customization: AppCustomization) {
    if (customization.customName == null && customization.customBrand == null && customization.customColor == null) {
      resetCustomization(packageName)
      return
    }

    val json = JSONObject().apply {
      if (customization.customName != null) put("name", customization.customName)
      if (customization.customBrand != null) put("brand", customization.customBrand.name)
      if (customization.customColor != null) put("color", customization.customColor.value.toLong())
    }
    prefs.edit().putString(packageName, json.toString()).apply()
  }

  companion object {
    private const val PREFS_NAME = "lumia_app_customizations"
  }
}
