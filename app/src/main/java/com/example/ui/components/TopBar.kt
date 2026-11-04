package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AcUnit
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.CloudQueue
import androidx.compose.material.icons.rounded.Grain
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.WeatherCondition
import com.example.model.WeatherInfo

@Composable
fun TopBar(
  currentTime: String,
  currentDate: String = "",
  weatherInfo: WeatherInfo = WeatherInfo(),
  onOpenSettings: () -> Unit,
  onOpenWifi: () -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Top-left: Settings, Wifi and Network Status
    Row(
      horizontalArrangement = Arrangement.spacedBy(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      TopBarIconButton(
        icon = Icons.Rounded.Settings,
        contentDescription = stringResource(R.string.settings),
        testTag = "settings_button",
        onClick = onOpenSettings
      )

      TopBarIconButton(
        icon = Icons.Rounded.Wifi,
        contentDescription = stringResource(R.string.wifi),
        testTag = "wifi_button",
        onClick = onOpenWifi
      )

      // Subtle network status badge
      Row(
        modifier = Modifier
          .clip(RoundedCornerShape(10.dp))
          .background(Color.Black.copy(alpha = 0.22f))
          .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)), RoundedCornerShape(10.dp))
          .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(7.dp)
            .background(Color(0xFF22C55E), CircleShape)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Online",
          color = Color.White.copy(alpha = 0.85f),
          fontSize = 12.sp,
          fontWeight = FontWeight.Medium
        )
      }
    }

    // Top-right: Date, Weather Widget and Digital Clock
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
      // Date and Weather Info
      Column(horizontalAlignment = Alignment.End) {
        if (currentDate.isNotEmpty()) {
          Text(
            text = currentDate,
            style = TextStyle(
              fontFamily = FontFamily.SansSerif,
              fontWeight = FontWeight.Medium,
              fontSize = 16.sp,
              color = Color.White.copy(alpha = 0.88f),
              shadow = Shadow(
                color = Color.Black.copy(alpha = 0.7f),
                offset = Offset(1f, 1f),
                blurRadius = 4f
              )
            )
          )
        }

        // Weather Widget (Icon + Temperature)
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.testTag("weather_widget")
        ) {
          val (weatherIcon, iconColor) = getWeatherIconAndColor(weatherInfo.condition)
          Icon(
            imageVector = weatherIcon,
            contentDescription = "Wetter",
            tint = iconColor,
            modifier = Modifier.size(17.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = weatherInfo.temperature,
            style = TextStyle(
              fontFamily = FontFamily.SansSerif,
              fontWeight = FontWeight.SemiBold,
              fontSize = 15.sp,
              color = Color.White,
              shadow = Shadow(
                color = Color.Black.copy(alpha = 0.7f),
                offset = Offset(1f, 1f),
                blurRadius = 4f
              )
            )
          )
        }
      }

      // Digital Clock (HH:mm)
      Text(
        text = currentTime,
        modifier = Modifier
          .testTag("digital_clock_text")
          .padding(end = 4.dp),
        style = TextStyle(
          fontFamily = FontFamily.SansSerif,
          fontWeight = FontWeight.Bold,
          fontSize = 38.sp,
          letterSpacing = 1.sp,
          color = Color.White,
          shadow = Shadow(
            color = Color.Black.copy(alpha = 0.8f),
            offset = Offset(2f, 2f),
            blurRadius = 8f
          )
        )
      )
    }
  }
}

private fun getWeatherIconAndColor(condition: WeatherCondition): Pair<ImageVector, Color> {
  return when (condition) {
    WeatherCondition.SUNNY -> Icons.Rounded.WbSunny to Color(0xFFFFB703)
    WeatherCondition.PARTLY_CLOUDY -> Icons.Rounded.CloudQueue to Color(0xFFE2E8F0)
    WeatherCondition.CLOUDY -> Icons.Rounded.Cloud to Color(0xFFCBD5E1)
    WeatherCondition.RAINY -> Icons.Rounded.Grain to Color(0xFF38BDF8)
    WeatherCondition.SNOWY -> Icons.Rounded.AcUnit to Color(0xFFBAE6FD)
    WeatherCondition.THUNDERSTORM -> Icons.Rounded.Bolt to Color(0xFFFACC15)
  }
}

@Composable
fun TopBarIconButton(
  icon: ImageVector,
  contentDescription: String,
  testTag: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  var isFocused by remember { mutableStateOf(false) }
  val interactionSource = remember { MutableInteractionSource() }

  val scale by animateFloatAsState(
    targetValue = if (isFocused) 1.18f else 1.0f,
    animationSpec = spring(dampingRatio = 0.75f, stiffness = 400f),
    label = "topbar_button_scale"
  )

  val backgroundColor by animateColorAsState(
    targetValue = if (isFocused) Color.White.copy(alpha = 0.35f) else Color.Black.copy(alpha = 0.28f),
    label = "topbar_button_bg"
  )

  val borderColor by animateColorAsState(
    targetValue = if (isFocused) Color.White else Color.White.copy(alpha = 0.40f),
    label = "topbar_button_border"
  )

  val iconColor by animateColorAsState(
    targetValue = if (isFocused) Color.White else Color.White.copy(alpha = 0.92f),
    label = "topbar_button_icon_color"
  )

  val shape = RoundedCornerShape(13.dp)

  Box(
    modifier = modifier
      .size(48.dp)
      .scale(scale)
      .testTag(testTag)
      .onFocusChanged { isFocused = it.isFocused }
      .focusable(interactionSource = interactionSource)
      .clip(shape)
      .background(backgroundColor, shape)
      .border(
        border = BorderStroke(if (isFocused) 2.5.dp else 1.5.dp, borderColor),
        shape = shape
      )
      .clickable(
        interactionSource = interactionSource,
        indication = null,
        onClick = onClick
      ),
    contentAlignment = Alignment.Center
  ) {
    Icon(
      imageVector = icon,
      contentDescription = contentDescription,
      tint = iconColor,
      modifier = Modifier.size(24.dp)
    )
  }
}
