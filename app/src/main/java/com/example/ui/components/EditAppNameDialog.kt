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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import kotlinx.coroutines.delay

@Composable
fun EditAppNameDialog(
  app: AppItem,
  onDismiss: () -> Unit,
  onSave: (String) -> Unit
) {
  var nameText by remember { mutableStateOf(app.name) }
  val textFieldFocusRequester = remember { FocusRequester() }

  LaunchedEffect(Unit) {
    delay(150)
    try {
      textFieldFocusRequester.requestFocus()
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
        .background(Color(0xFF181B22).copy(alpha = 0.96f))
        .border(BorderStroke(1.5.dp, Color.White.copy(alpha = 0.25f)), dialogShape)
        .padding(24.dp)
        .testTag("edit_app_name_dialog")
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
            imageVector = Icons.Rounded.Edit,
            contentDescription = null,
            tint = Color(0xFF00E5FF),
            modifier = Modifier.padding(end = 12.dp)
          )
          Column {
            Text(
              text = stringResource(R.string.rename_app),
              color = Color.White,
              fontSize = 20.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = stringResource(R.string.original_name, app.originalName),
              color = Color.White.copy(alpha = 0.6f),
              fontSize = 13.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Text Field
        OutlinedTextField(
          value = nameText,
          onValueChange = { nameText = it },
          modifier = Modifier
            .fillMaxWidth()
            .focusRequester(textFieldFocusRequester)
            .testTag("app_name_input_field"),
          shape = RoundedCornerShape(14.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = Color.White,
            unfocusedBorderColor = Color.White.copy(alpha = 0.35f),
            focusedContainerColor = Color.White.copy(alpha = 0.12f),
            unfocusedContainerColor = Color.White.copy(alpha = 0.06f)
          ),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Buttons
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          DialogActionButton(
            label = stringResource(R.string.cancel),
            icon = Icons.Rounded.Close,
            onClick = onDismiss,
            modifier = Modifier.weight(1f),
            testTag = "rename_cancel_btn"
          )

          DialogActionButton(
            label = stringResource(R.string.save),
            icon = Icons.Rounded.Check,
            isPrimary = true,
            onClick = {
              onSave(nameText)
              onDismiss()
            },
            modifier = Modifier.weight(1f),
            testTag = "rename_save_btn"
          )
        }
      }
    }
  }
}

@Composable
private fun DialogActionButton(
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

  val shape = RoundedCornerShape(14.dp)

  Row(
    modifier = modifier
      .height(48.dp)
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
      .padding(horizontal = 14.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Center
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = Color.White,
      modifier = Modifier.padding(end = 8.dp)
    )
    Text(
      text = label,
      color = Color.White,
      fontSize = 15.sp,
      fontWeight = if (isFocused || isPrimary) FontWeight.Bold else FontWeight.Medium
    )
  }
}
