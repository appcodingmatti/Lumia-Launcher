package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BookmarkAdd
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
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

/**
 * TV Lazy Row for Favorites ("Favoriten").
 * Displays favorite apps inside a frosted glass shelf.
 * If empty, displays the required placeholder hint: 'Füge Apps zu deinen Favoriten hinzu'.
 */
@Composable
fun FavoritesRow(
  apps: List<AppItem>,
  onLaunchApp: (AppItem) -> Unit,
  modifier: Modifier = Modifier,
  onOpenOptions: ((AppItem) -> Unit)? = null,
  firstItemFocusRequester: FocusRequester? = null
) {
  val containerShape = RoundedCornerShape(26.dp)

  // Frosted glass container holding the Favoriten row, as shown in the screenshot
  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(containerShape)
      .background(
        color = Color.White.copy(alpha = 0.08f),
        shape = containerShape
      )
      .border(
        border = BorderStroke(1.2.dp, Color.White.copy(alpha = 0.22f)),
        shape = containerShape
      )
      .padding(vertical = 12.dp, horizontal = 12.dp),
    contentAlignment = Alignment.CenterStart
  ) {
    if (apps.isEmpty()) {
      // Empty state placeholder when favorites list is empty
      EmptyFavoritesPlaceholder(
        modifier = if (firstItemFocusRequester != null) {
          Modifier.focusRequester(firstItemFocusRequester)
        } else {
          Modifier
        }
      )
    } else {
      LazyRow(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("favorites_tv_lazy_row"),
        contentPadding = PaddingValues(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        itemsIndexed(apps, key = { _, app -> app.id }) { index, app ->
          val cardModifier = if (index == 0 && firstItemFocusRequester != null) {
            Modifier.focusRequester(firstItemFocusRequester)
          } else {
            Modifier
          }

          AppCard(
            app = app,
            onLaunch = { onLaunchApp(app) },
            onLongClick = { onOpenOptions?.invoke(app) },
            modifier = cardModifier
          )
        }
      }
    }
  }
}

@Composable
private fun EmptyFavoritesPlaceholder(modifier: Modifier = Modifier) {
  var isFocused by remember { mutableStateOf(false) }
  val interactionSource = remember { MutableInteractionSource() }

  val scale by animateFloatAsState(
    targetValue = if (isFocused) 1.02f else 1.0f,
    animationSpec = spring(dampingRatio = 0.75f, stiffness = 400f),
    label = "empty_fav_scale"
  )

  val placeholderShape = RoundedCornerShape(18.dp)

  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(98.dp)
      .scale(scale)
      .testTag("empty_favorites_placeholder")
      .onFocusChanged { isFocused = it.isFocused }
      .focusable(interactionSource = interactionSource)
      .clip(placeholderShape)
      .background(
        color = if (isFocused) Color.White.copy(alpha = 0.14f) else Color.White.copy(alpha = 0.04f),
        shape = placeholderShape
      )
      .border(
        border = BorderStroke(
          if (isFocused) 2.dp else 1.dp,
          if (isFocused) Color.White else Color.White.copy(alpha = 0.15f)
        ),
        shape = placeholderShape
      )
      .padding(horizontal = 24.dp),
    contentAlignment = Alignment.Center
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      Icon(
        imageVector = Icons.Rounded.BookmarkAdd,
        contentDescription = null,
        tint = if (isFocused) Color(0xFFFFD700) else Color.White.copy(alpha = 0.8f),
        modifier = Modifier.size(32.dp)
      )
      Spacer(modifier = Modifier.width(16.dp))
      Column {
        Text(
          text = stringResource(R.string.add_to_favorites_hint),
          style = TextStyle(
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            shadow = Shadow(
              color = Color.Black.copy(alpha = 0.6f),
              offset = Offset(1f, 1f),
              blurRadius = 4f
            )
          )
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = stringResource(R.string.favorites_long_click_hint),
          style = TextStyle(
            color = Color.White.copy(alpha = 0.72f),
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal
          )
        )
      }
    }
  }
}
