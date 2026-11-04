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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.model.AppItem
import com.example.model.BrandType
import kotlinx.coroutines.delay

@Composable
fun EditAppStyleDialog(
  app: AppItem,
  onDismiss: () -> Unit,
  onSave: (BrandType, Color) -> Unit,
  onReset: () -> Unit
) {
  var selectedBrand by remember { mutableStateOf(app.brandType) }
  var selectedColor by remember { mutableStateOf(app.cardColor) }

  val firstChipFocusRequester = remember { FocusRequester() }

  LaunchedEffect(Unit) {
    delay(150)
    try {
      firstChipFocusRequester.requestFocus()
    } catch (_: Exception) {
      // Ignored
    }
  }

  val availableBrands = listOf(
    BrandType.GENERIC_APP to "Standard",
    BrandType.YOUTUBE to "YouTube",
    BrandType.NETFLIX to "Netflix",
    BrandType.PRIME_VIDEO to "Prime",
    BrandType.SPOTIFY to "Spotify",
    BrandType.TWITCH to "Twitch",
    BrandType.PLEX to "Plex",
    BrandType.YOUTUBE_MUSIC to "YT Music",
    BrandType.PLAY_STORE to "Play Store",
    BrandType.DAZN to "DAZN",
    BrandType.BROWSE_HERE to "Browser",
    BrandType.ARC_LAUNCHER to "Arc",
    BrandType.PLAY_GAMES to "Games",
    BrandType.FREE_TV to "Free TV",
    BrandType.LOCALSEND to "LocalSend"
  )

  val availableColors = listOf(
    Color(0xFF141414), // Deep Black
    Color(0xFF1F2937), // Cool Slate
    Color(0xFF2563EB), // Royal Blue
    Color(0xFF9146FF), // Twitch Violet
    Color(0xFFDC2626), // Netflix Red
    Color(0xFF16A34A), // Emerald Green
    Color(0xFFD97706), // Amber
    Color(0xFFFFFFFF)  // Clean White
  )

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(
      usePlatformDefaultWidth = false,
      dismissOnBackPress = true,
      dismissOnClickOutside = true
    )
  ) {
    val dialogShape = RoundedCornerShape(24.dp)

    Box(
      modifier = Modifier
        .width(480.dp)
        .shadow(24.dp, dialogShape)
        .clip(dialogShape)
        .background(Color(0xFF181B22).copy(alpha = 0.96f))
        .border(BorderStroke(1.5.dp, Color.White.copy(alpha = 0.25f)), dialogShape)
        .padding(24.dp)
        .testTag("edit_app_style_dialog")
    ) {
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Title
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Rounded.Palette,
            contentDescription = null,
            tint = Color(0xFF00E5FF),
            modifier = Modifier.padding(end = 12.dp)
          )
          Column {
            Text(
              text = stringResource(R.string.change_style),
              color = Color.White,
              fontSize = 20.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = app.name,
              color = Color.White.copy(alpha = 0.6f),
              fontSize = 13.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Live Preview of the App Card
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .padding(vertical = 12.dp),
          contentAlignment = Alignment.Center
        ) {
          val previewApp = app.copy(
            brandType = selectedBrand,
            cardColor = selectedColor
          )
          AppCard(
            app = previewApp,
            onLaunch = {},
            cardWidth = 160.dp,
            cardHeight = 90.dp
          )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Brand Style Picker Section
        Text(
          text = stringResource(R.string.select_brand_style),
          color = Color.White.copy(alpha = 0.85f),
          fontSize = 14.sp,
          fontWeight = FontWeight.SemiBold,
          modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
          items(availableBrands) { (brand, label) ->
            val isSelected = selectedBrand == brand
            val chipModifier = if (brand == availableBrands.first().first) {
              Modifier.focusRequester(firstChipFocusRequester)
            } else {
              Modifier
            }

            StyleChip(
              label = label,
              isSelected = isSelected,
              modifier = chipModifier,
              onClick = {
                selectedBrand = brand
                // Auto adapt color to recognized default if standard
                selectedColor = when (brand) {
                  BrandType.YOUTUBE, BrandType.NETFLIX, BrandType.PLAY_STORE, BrandType.LOCALSEND -> Color(0xFFFFFFFF)
                  BrandType.TWITCH -> Color(0xFF9146FF)
                  BrandType.PRIME_VIDEO, BrandType.BROWSE_HERE -> Color(0xFF0073D1)
                  BrandType.SPOTIFY -> Color(0xFF121212)
                  BrandType.PLEX, BrandType.DAZN, BrandType.ARC_LAUNCHER -> Color(0xFF141414)
                  else -> selectedColor
                }
              }
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Color Picker Section
        Text(
          text = stringResource(R.string.select_color),
          color = Color.White.copy(alpha = 0.85f),
          fontSize = 14.sp,
          fontWeight = FontWeight.SemiBold,
          modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          availableColors.forEach { color ->
            ColorPickerCircle(
              color = color,
              isSelected = selectedColor == color,
              onClick = { selectedColor = color }
            )
          }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Actions: Reset, Cancel, Save
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          DialogButton(
            label = stringResource(R.string.reset_customization),
            icon = Icons.Rounded.RestartAlt,
            modifier = Modifier.weight(1.1f),
            testTag = "style_reset_btn",
            onClick = {
              onReset()
              onDismiss()
            }
          )

          DialogButton(
            label = stringResource(R.string.cancel),
            icon = Icons.Rounded.Close,
            modifier = Modifier.weight(0.9f),
            testTag = "style_cancel_btn",
            onClick = onDismiss
          )

          DialogButton(
            label = stringResource(R.string.save),
            icon = Icons.Rounded.Check,
            isPrimary = true,
            modifier = Modifier.weight(1f),
            testTag = "style_save_btn",
            onClick = {
              onSave(selectedBrand, selectedColor)
              onDismiss()
            }
          )
        }
      }
    }
  }
}

@Composable
private fun StyleChip(
  label: String,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  var isFocused by remember { mutableStateOf(false) }
  val interactionSource = remember { MutableInteractionSource() }

  val scale by animateFloatAsState(
    targetValue = if (isFocused) 1.1f else 1.0f,
    animationSpec = spring(dampingRatio = 0.75f, stiffness = 420f),
    label = "chip_scale"
  )

  val shape = RoundedCornerShape(10.dp)

  Box(
    modifier = modifier
      .scale(scale)
      .clip(shape)
      .background(
        when {
          isFocused -> Color.White.copy(alpha = 0.35f)
          isSelected -> Color(0xFF00E5FF).copy(alpha = 0.25f)
          else -> Color.White.copy(alpha = 0.08f)
        },
        shape
      )
      .border(
        BorderStroke(
          if (isFocused) 2.dp else if (isSelected) 1.5.dp else 1.dp,
          if (isFocused) Color.White else if (isSelected) Color(0xFF00E5FF) else Color.White.copy(alpha = 0.2f)
        ),
        shape
      )
      .onFocusChanged { isFocused = it.isFocused }
      .focusable(interactionSource = interactionSource)
      .clickable(
        interactionSource = interactionSource,
        indication = null,
        onClick = onClick
      )
      .padding(horizontal = 14.dp, vertical = 8.dp),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = label,
      color = if (isSelected) Color(0xFF00E5FF) else Color.White,
      fontSize = 13.sp,
      fontWeight = if (isSelected || isFocused) FontWeight.Bold else FontWeight.Medium
    )
  }
}

@Composable
private fun ColorPickerCircle(
  color: Color,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  var isFocused by remember { mutableStateOf(false) }
  val interactionSource = remember { MutableInteractionSource() }

  val scale by animateFloatAsState(
    targetValue = if (isFocused) 1.25f else if (isSelected) 1.1f else 1.0f,
    animationSpec = spring(dampingRatio = 0.75f, stiffness = 420f),
    label = "color_scale"
  )

  Box(
    modifier = Modifier
      .size(34.dp)
      .scale(scale)
      .clip(CircleShape)
      .background(color, CircleShape)
      .border(
        BorderStroke(
          if (isFocused) 3.dp else if (isSelected) 2.dp else 1.dp,
          if (isFocused) Color.White else if (isSelected) Color(0xFF00E5FF) else Color.White.copy(alpha = 0.4f)
        ),
        CircleShape
      )
      .onFocusChanged { isFocused = it.isFocused }
      .focusable(interactionSource = interactionSource)
      .clickable(
        interactionSource = interactionSource,
        indication = null,
        onClick = onClick
      ),
    contentAlignment = Alignment.Center
  ) {
    if (isSelected) {
      Box(
        modifier = Modifier
          .size(10.dp)
          .background(if (color == Color.White) Color.Black else Color.White, CircleShape)
      )
    }
  }
}

@Composable
private fun DialogButton(
  label: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  isPrimary: Boolean = false,
  testTag: String = ""
) {
  var isFocused by remember { mutableStateOf(false) }
  val interactionSource = remember { MutableInteractionSource() }

  val scale by animateFloatAsState(
    targetValue = if (isFocused) 1.05f else 1.0f,
    animationSpec = spring(dampingRatio = 0.75f, stiffness = 420f),
    label = "dialog_btn_scale"
  )

  val backgroundColor by animateColorAsState(
    targetValue = when {
      isFocused -> Color.White.copy(alpha = 0.32f)
      isPrimary -> Color(0xFF00E5FF).copy(alpha = 0.25f)
      else -> Color.White.copy(alpha = 0.08f)
    },
    label = "dialog_btn_bg"
  )

  val borderColor by animateColorAsState(
    targetValue = if (isFocused) Color.White else Color.White.copy(alpha = 0.20f),
    label = "dialog_btn_border"
  )

  val shape = RoundedCornerShape(12.dp)

  Row(
    modifier = modifier
      .height(44.dp)
      .scale(scale)
      .testTag(testTag)
      .clip(shape)
      .background(backgroundColor, shape)
      .border(BorderStroke(if (isFocused) 2.dp else 1.dp, borderColor), shape)
      .onFocusChanged { isFocused = it.isFocused }
      .focusable(interactionSource = interactionSource)
      .clickable(
        interactionSource = interactionSource,
        indication = null,
        onClick = onClick
      )
      .padding(horizontal = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Center
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = Color.White,
      modifier = Modifier
        .size(18.dp)
        .padding(end = 6.dp)
    )
    Text(
      text = label,
      color = Color.White,
      fontSize = 13.sp,
      fontWeight = if (isFocused || isPrimary) FontWeight.Bold else FontWeight.Medium
    )
  }
}
