package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Launch
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarOutline
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.graphics.drawable.toBitmap
import com.example.R
import com.example.model.AppItem
import kotlinx.coroutines.delay

@Composable
fun AppOptionsDialog(
  app: AppItem,
  favoriteIndex: Int = -1,
  totalFavoritesCount: Int = 0,
  onDismiss: () -> Unit,
  onToggleFavorite: () -> Unit,
  onMoveLeft: (() -> Unit)? = null,
  onMoveRight: (() -> Unit)? = null,
  onRename: () -> Unit = {},
  onChangeStyle: () -> Unit = {},
  onResetCustomization: (() -> Unit)? = null,
  onLaunchApp: () -> Unit
) {
  val primaryFocusRequester = remember { FocusRequester() }

  LaunchedEffect(Unit) {
    delay(150)
    try {
      primaryFocusRequester.requestFocus()
    } catch (_: Exception) {
      // Ignored
    }
  }

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
        .width(420.dp)
        .shadow(24.dp, dialogShape)
        .clip(dialogShape)
        .background(Color(0xFF181B22).copy(alpha = 0.95f))
        .border(BorderStroke(1.5.dp, Color.White.copy(alpha = 0.25f)), dialogShape)
        .padding(24.dp)
        .testTag("app_options_dialog")
    ) {
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // App Header: Icon + Title
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          if (app.iconDrawable != null) {
            val bitmap = remember(app.iconDrawable) {
              app.iconDrawable.toBitmap(96, 96).asImageBitmap()
            }
            Image(
              bitmap = bitmap,
              contentDescription = app.name,
              modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
            )
          } else {
            Box(
              modifier = Modifier
                .size(48.dp)
                .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = app.name.take(1).uppercase(),
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }

          Spacer(modifier = Modifier.width(16.dp))

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = app.name,
              color = Color.White,
              fontSize = 20.sp,
              fontWeight = FontWeight.Bold,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
            Text(
              text = (if (app.isFavorite) {
                if (favoriteIndex >= 0) "Favorit (${favoriteIndex + 1} von $totalFavoritesCount)" else "In Favoriten"
              } else {
                "Nicht in Favoriten"
              }) + (if (app.isCustomized) " • Angepasst" else ""),
              color = if (app.isFavorite) Color(0xFFFFD700) else Color.White.copy(alpha = 0.6f),
              fontSize = 13.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Action 1: Toggle Favorite Button (Initial Focus)
        DialogActionButton(
          label = if (app.isFavorite) {
            stringResource(R.string.remove_from_favorites)
          } else {
            stringResource(R.string.add_to_favorites)
          },
          icon = if (app.isFavorite) Icons.Rounded.StarOutline else Icons.Rounded.Star,
          iconTint = if (app.isFavorite) Color.White.copy(alpha = 0.8f) else Color(0xFFFFD700),
          isPrimary = true,
          testTag = "dialog_toggle_favorite_button",
          modifier = Modifier.focusRequester(primaryFocusRequester),
          onClick = {
            onToggleFavorite()
            onDismiss()
          }
        )

        // Action 2 & 3: Move Left / Move Right (only if item is in favorites)
        if (app.isFavorite && favoriteIndex > 0 && onMoveLeft != null) {
          Spacer(modifier = Modifier.height(10.dp))
          DialogActionButton(
            label = stringResource(R.string.move_left),
            icon = Icons.AutoMirrored.Rounded.ArrowBack,
            isPrimary = false,
            testTag = "dialog_move_left_button",
            onClick = {
              onMoveLeft()
              onDismiss()
            }
          )
        }

        if (app.isFavorite && favoriteIndex >= 0 && favoriteIndex < totalFavoritesCount - 1 && onMoveRight != null) {
          Spacer(modifier = Modifier.height(10.dp))
          DialogActionButton(
            label = stringResource(R.string.move_right),
            icon = Icons.AutoMirrored.Rounded.ArrowForward,
            isPrimary = false,
            testTag = "dialog_move_right_button",
            onClick = {
              onMoveRight()
              onDismiss()
            }
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Action 4: Rename App
        DialogActionButton(
          label = stringResource(R.string.rename_app),
          icon = Icons.Rounded.Edit,
          isPrimary = false,
          testTag = "dialog_rename_app_button",
          onClick = {
            onDismiss()
            onRename()
          }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Action 5: Change Brand Style & Color
        DialogActionButton(
          label = stringResource(R.string.change_style),
          icon = Icons.Rounded.Palette,
          isPrimary = false,
          testTag = "dialog_change_style_button",
          onClick = {
            onDismiss()
            onChangeStyle()
          }
        )

        // Action 6: Reset Customization (if customized)
        if (app.isCustomized && onResetCustomization != null) {
          Spacer(modifier = Modifier.height(10.dp))
          DialogActionButton(
            label = stringResource(R.string.reset_customization),
            icon = Icons.Rounded.RestartAlt,
            iconTint = Color(0xFFFF8A80),
            isPrimary = false,
            testTag = "dialog_reset_customization_button",
            onClick = {
              onDismiss()
              onResetCustomization()
            }
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Action 7: Open App
        DialogActionButton(
          label = stringResource(R.string.open_app),
          icon = Icons.Rounded.Launch,
          isPrimary = false,
          testTag = "dialog_open_app_button",
          onClick = {
            onDismiss()
            onLaunchApp()
          }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Action 5: Cancel
        DialogActionButton(
          label = stringResource(R.string.cancel),
          icon = Icons.Rounded.Close,
          isPrimary = false,
          testTag = "dialog_cancel_button",
          onClick = onDismiss
        )
      }
    }
  }
}

@Composable
private fun DialogActionButton(
  label: String,
  icon: ImageVector,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  iconTint: Color = Color.White,
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
      isFocused -> Color.White.copy(alpha = 0.28f)
      isPrimary -> Color.White.copy(alpha = 0.12f)
      else -> Color.White.copy(alpha = 0.06f)
    },
    label = "dialog_btn_bg"
  )

  val borderColor by animateColorAsState(
    targetValue = if (isFocused) Color.White else Color.White.copy(alpha = 0.20f),
    label = "dialog_btn_border"
  )

  val shape = RoundedCornerShape(14.dp)

  Row(
    modifier = modifier
      .fillMaxWidth()
      .height(48.dp)
      .scale(scale)
      .testTag(testTag)
      .clip(shape)
      .background(backgroundColor, shape)
      .border(BorderStroke(if (isFocused) 2.5.dp else 1.dp, borderColor), shape)
      .onFocusChanged { isFocused = it.isFocused }
      .focusable(interactionSource = interactionSource)
      .clickable(
        interactionSource = interactionSource,
        indication = null,
        onClick = onClick
      )
      .padding(horizontal = 16.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Start
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = if (isFocused) Color.White else iconTint,
      modifier = Modifier.size(20.dp)
    )
    Spacer(modifier = Modifier.width(14.dp))
    Text(
      text = label,
      color = Color.White,
      fontSize = 15.sp,
      fontWeight = if (isFocused || isPrimary) FontWeight.Bold else FontWeight.Medium
    )
  }
}
