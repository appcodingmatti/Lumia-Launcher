package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.SportsEsports
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.graphics.drawable.toBitmap
import com.example.model.AppItem
import com.example.model.BrandType

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.material.icons.rounded.Star
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppCard(
  app: AppItem,
  onLaunch: () -> Unit,
  modifier: Modifier = Modifier,
  cardWidth: Dp? = 172.dp,
  cardHeight: Dp = 94.dp,
  onLongClick: (() -> Unit)? = null,
  onFocusChangedCallback: ((Boolean) -> Unit)? = null
) {
  var isFocused by remember { mutableStateOf(false) }
  val interactionSource = remember { MutableInteractionSource() }

  var isDpadCenterHeld by remember { mutableStateOf(false) }
  var isLongPressHandled by remember { mutableStateOf(false) }
  val coroutineScope = rememberCoroutineScope()
  var holdJob by remember { mutableStateOf<Job?>(null) }

  // Exact 1.1f scale when focused as requested
  val scale by animateFloatAsState(
    targetValue = if (isFocused) 1.1f else 1.0f,
    animationSpec = spring(dampingRatio = 0.70f, stiffness = 420f),
    label = "app_card_scale"
  )

  val shape = RoundedCornerShape(16.dp)

  val borderStroke = if (isFocused) {
    BorderStroke(3.dp, Color.White)
  } else {
    BorderStroke(1.2.dp, Color.White.copy(alpha = 0.22f))
  }

  val shadowElevation = if (isFocused) 20.dp else 2.dp

  val baseModifier = if (cardWidth != null) {
    modifier.width(cardWidth).height(cardHeight)
  } else {
    modifier.height(cardHeight)
  }

  Box(
    modifier = baseModifier
      .zIndex(if (isFocused) 2f else 0f)
      .scale(scale)
      .shadow(
        elevation = shadowElevation,
        shape = shape,
        spotColor = if (isFocused) Color.White.copy(alpha = 0.65f) else Color.Black.copy(alpha = 0.4f),
        ambientColor = Color.Black.copy(alpha = 0.5f)
      )
      .clip(shape)
      .testTag("app_card_${app.id}")
      .onFocusChanged { focusState ->
        isFocused = focusState.isFocused
        if (!focusState.isFocused) {
          holdJob?.cancel()
          isDpadCenterHeld = false
          // Note: Keep isLongPressHandled until KeyUp event completes
          // to avoid accidental launches when the modal steals focus during button release
        }
        onFocusChangedCallback?.invoke(focusState.isFocused)
      }
      .focusable(interactionSource = interactionSource)
      .onKeyEvent { keyEvent ->
        val isCenterKey = keyEvent.key == Key.DirectionCenter ||
          keyEvent.key == Key.Enter ||
          keyEvent.key == Key.NumPadEnter
        if (isCenterKey) {
          if (keyEvent.type == KeyEventType.KeyDown) {
            if (!isDpadCenterHeld) {
              isDpadCenterHeld = true
              isLongPressHandled = false
              holdJob?.cancel()
              holdJob = coroutineScope.launch {
                delay(450L) // Long press threshold
                if (isDpadCenterHeld) {
                  isLongPressHandled = true
                  onLongClick?.invoke()
                }
              }
            }
            true
          } else if (keyEvent.type == KeyEventType.KeyUp) {
            holdJob?.cancel()
            val handledAsLong = isLongPressHandled
            isDpadCenterHeld = false
            isLongPressHandled = false
            if (!handledAsLong) {
              onLaunch()
            }
            true
          } else {
            false
          }
        } else {
          false
        }
      }
      .background(app.cardColor, shape)
      .border(borderStroke, shape)
      .combinedClickable(
        interactionSource = interactionSource,
        indication = null,
        onClick = onLaunch,
        onLongClick = onLongClick
      ),
    contentAlignment = Alignment.Center
  ) {
    when (app.brandType) {
      BrandType.PLEX -> PlexCardContent()
      BrandType.YOUTUBE -> YouTubeCardContent()
      BrandType.YOUTUBE_MUSIC -> YouTubeMusicCardContent()
      BrandType.TWITCH -> TwitchCardContent()
      BrandType.FREE_TV -> FreeTvCardContent()
      BrandType.SPOTIFY -> SpotifyCardContent()
      BrandType.LOCALSEND -> LocalSendCardContent()
      BrandType.PRIME_VIDEO -> PrimeVideoCardContent()
      BrandType.NETFLIX -> NetflixCardContent()
      BrandType.PLAY_STORE -> PlayStoreCardContent()
      BrandType.BROWSE_HERE -> BrowseHereCardContent()
      BrandType.DAZN -> DaznCardContent()
      BrandType.SETTINGS_APP -> SettingsAppCardContent()
      BrandType.ARC_LAUNCHER -> ArcLauncherCardContent()
      BrandType.PLAY_GAMES -> PlayGamesCardContent()
      else -> GenericAppCardContent(app)
    }

    // Subtle favorite star badge
    if (app.isFavorite) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(8.dp),
        contentAlignment = Alignment.TopEnd
      ) {
        Icon(
          imageVector = Icons.Rounded.Star,
          contentDescription = "Favorit",
          tint = Color(0xFFFFD700).copy(alpha = if (isFocused) 0.95f else 0.75f),
          modifier = Modifier.size(14.dp)
        )
      }
    }
  }
}

@Composable
private fun PlexCardContent() {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Center,
    modifier = Modifier.padding(horizontal = 14.dp)
  ) {
    Text(
      text = "plex",
      color = Color.White,
      fontSize = 30.sp,
      fontWeight = FontWeight.Black,
      fontFamily = FontFamily.SansSerif,
      letterSpacing = (-0.5).sp
    )
    Spacer(modifier = Modifier.width(4.dp))
    Canvas(modifier = Modifier.size(18.dp, 20.dp)) {
      val path = Path().apply {
        moveTo(0f, 0f)
        lineTo(size.width * 0.7f, size.height * 0.5f)
        lineTo(0f, size.height)
        lineTo(size.width * 0.35f, size.height)
        lineTo(size.width, size.height * 0.5f)
        lineTo(size.width * 0.35f, 0f)
        close()
      }
      drawPath(path = path, color = Color(0xFFE5A00D))
    }
  }
}

@Composable
private fun YouTubeCardContent() {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Center,
    modifier = Modifier.padding(horizontal = 12.dp)
  ) {
    Box(
      modifier = Modifier
        .size(30.dp, 22.dp)
        .background(Color(0xFFFF0000), RoundedCornerShape(5.dp)),
      contentAlignment = Alignment.Center
    ) {
      Canvas(modifier = Modifier.size(10.dp, 10.dp)) {
        val path = Path().apply {
          moveTo(0f, 0f)
          lineTo(size.width, size.height / 2f)
          lineTo(0f, size.height)
          close()
        }
        drawPath(path = path, color = Color.White)
      }
    }
    Spacer(modifier = Modifier.width(6.dp))
    Text(
      text = "YouTube",
      color = Color(0xFF1F1F1F),
      fontSize = 20.sp,
      fontWeight = FontWeight.Bold,
      fontFamily = FontFamily.SansSerif
    )
  }
}

@Composable
private fun YouTubeMusicCardContent() {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Center,
    modifier = Modifier.padding(horizontal = 10.dp)
  ) {
    Box(
      modifier = Modifier
        .size(26.dp)
        .background(Color(0xFFFF0000), CircleShape),
      contentAlignment = Alignment.Center
    ) {
      Canvas(modifier = Modifier.size(8.dp, 8.dp)) {
        val path = Path().apply {
          moveTo(0f, 0f)
          lineTo(size.width, size.height / 2f)
          lineTo(0f, size.height)
          close()
        }
        drawPath(path = path, color = Color.White)
      }
    }
    Spacer(modifier = Modifier.width(6.dp))
    Text(
      text = "YouTube Music",
      color = Color(0xFF111827),
      fontSize = 15.sp,
      fontWeight = FontWeight.Bold
    )
  }
}

@Composable
private fun PrimeVideoCardContent() {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
    modifier = Modifier.padding(horizontal = 10.dp)
  ) {
    Text(
      text = "prime video",
      color = Color.White,
      fontSize = 20.sp,
      fontWeight = FontWeight.Bold,
      fontFamily = FontFamily.SansSerif
    )
    Canvas(modifier = Modifier.size(34.dp, 8.dp)) {
      val path = Path().apply {
        moveTo(2f, 2f)
        quadraticTo(size.width * 0.5f, size.height, size.width - 2f, 2f)
      }
      drawPath(path = path, color = Color.White, style = Stroke(width = 2.dp.toPx()))
    }
  }
}

@Composable
private fun NetflixCardContent() {
  Text(
    text = "NETFLIX",
    color = Color(0xFFE50914),
    fontSize = 26.sp,
    fontWeight = FontWeight.Black,
    fontFamily = FontFamily.SansSerif,
    letterSpacing = 1.sp
  )
}

@Composable
private fun PlayStoreCardContent() {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Center,
    modifier = Modifier.padding(horizontal = 10.dp)
  ) {
    Canvas(modifier = Modifier.size(24.dp)) {
      val trianglePath = Path().apply {
        moveTo(2f, 2f)
        lineTo(size.width - 2f, size.height / 2f)
        lineTo(2f, size.height - 2f)
        close()
      }
      drawPath(path = trianglePath, color = Color(0xFF00C7FF))
    }
    Spacer(modifier = Modifier.width(6.dp))
    Column {
      Text(
        text = "Google Play",
        color = Color(0xFF374151),
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 14.sp
      )
      Text(
        text = "Store",
        color = Color(0xFF6B7280),
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 12.sp
      )
    }
  }
}

@Composable
private fun BrowseHereCardContent() {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Center,
    modifier = Modifier.padding(horizontal = 10.dp)
  ) {
    Box(
      modifier = Modifier
        .size(24.dp)
        .background(Color.White.copy(alpha = 0.25f), CircleShape),
      contentAlignment = Alignment.Center
    ) {
      Canvas(modifier = Modifier.size(14.dp)) {
        drawCircle(color = Color.White, style = Stroke(width = 2.dp.toPx()))
      }
    }
    Spacer(modifier = Modifier.width(6.dp))
    Text(
      text = "BrowseHere",
      color = Color.White,
      fontSize = 15.sp,
      fontWeight = FontWeight.Bold
    )
  }
}

@Composable
private fun DaznCardContent() {
  Box(
    modifier = Modifier
      .padding(horizontal = 12.dp)
      .border(1.5.dp, Color.White, RoundedCornerShape(4.dp))
      .padding(horizontal = 10.dp, vertical = 4.dp),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = "DA\nZN",
      color = Color.White,
      fontSize = 14.sp,
      fontWeight = FontWeight.Black,
      textAlign = TextAlign.Center,
      lineHeight = 13.sp
    )
  }
}

@Composable
private fun SettingsAppCardContent() {
  Icon(
    imageVector = Icons.Rounded.Settings,
    contentDescription = "Settings",
    tint = Color.White,
    modifier = Modifier.size(42.dp)
  )
}

@Composable
private fun ArcLauncherCardContent() {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Text(
      text = "arc",
      color = Color(0xFF38BDF8),
      fontSize = 16.sp,
      fontWeight = FontWeight.Black
    )
    Text(
      text = "launcher",
      color = Color.White,
      fontSize = 18.sp,
      fontWeight = FontWeight.Bold
    )
  }
}

@Composable
private fun PlayGamesCardContent() {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Center,
    modifier = Modifier.padding(horizontal = 10.dp)
  ) {
    Icon(
      imageVector = Icons.Rounded.SportsEsports,
      contentDescription = "Play Games",
      tint = Color(0xFF22C55E),
      modifier = Modifier.size(28.dp)
    )
    Spacer(modifier = Modifier.width(6.dp))
    Text(
      text = "Play Games",
      color = Color.White,
      fontSize = 14.sp,
      fontWeight = FontWeight.Bold
    )
  }
}

@Composable
private fun TwitchCardContent() {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Center,
    modifier = Modifier.padding(horizontal = 12.dp)
  ) {
    Canvas(modifier = Modifier.size(20.dp, 22.dp)) {
      val bubblePath = Path().apply {
        moveTo(0f, 0f)
        lineTo(size.width, 0f)
        lineTo(size.width, size.height * 0.75f)
        lineTo(size.width * 0.65f, size.height * 0.75f)
        lineTo(size.width * 0.45f, size.height)
        lineTo(size.width * 0.45f, size.height * 0.75f)
        lineTo(0f, size.height * 0.75f)
        close()
      }
      drawPath(path = bubblePath, color = Color.White)
      drawRect(
        color = Color(0xFF9146FF),
        topLeft = Offset(size.width * 0.32f, size.height * 0.22f),
        size = Size(size.width * 0.12f, size.height * 0.30f)
      )
      drawRect(
        color = Color(0xFF9146FF),
        topLeft = Offset(size.width * 0.62f, size.height * 0.22f),
        size = Size(size.width * 0.12f, size.height * 0.30f)
      )
    }
    Spacer(modifier = Modifier.width(6.dp))
    Text(
      text = "twitch",
      color = Color.White,
      fontSize = 22.sp,
      fontWeight = FontWeight.ExtraBold,
      fontFamily = FontFamily.SansSerif
    )
  }
}

@Composable
private fun FreeTvCardContent() {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Center,
    modifier = Modifier.padding(horizontal = 10.dp)
  ) {
    Box(
      modifier = Modifier
        .size(32.dp)
        .background(
          brush = Brush.radialGradient(listOf(Color(0xFF8B0000), Color(0xFF2B0000))),
          shape = CircleShape
        )
        .border(1.dp, Color(0xFFEE2222), CircleShape),
      contentAlignment = Alignment.Center
    ) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
          text = "free",
          color = Color.White,
          fontSize = 9.sp,
          fontStyle = FontStyle.Italic,
          fontWeight = FontWeight.Bold,
          lineHeight = 9.sp
        )
        Text(
          text = "TV",
          color = Color(0xFFFF5252),
          fontSize = 8.sp,
          fontWeight = FontWeight.Black,
          lineHeight = 8.sp
        )
      }
    }
    Spacer(modifier = Modifier.width(8.dp))
    Text(
      text = "Free TV",
      color = Color.White,
      fontSize = 16.sp,
      fontWeight = FontWeight.Medium
    )
  }
}

@Composable
private fun SpotifyCardContent() {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Center,
    modifier = Modifier.padding(horizontal = 12.dp)
  ) {
    Canvas(modifier = Modifier.size(26.dp)) {
      drawCircle(color = Color(0xFF1ED760))
      val strokeWidth = 2.2.dp.toPx()
      drawArc(
        color = Color(0xFF121212),
        startAngle = 205f,
        sweepAngle = 130f,
        useCenter = false,
        topLeft = Offset(size.width * 0.24f, size.height * 0.44f),
        size = Size(size.width * 0.52f, size.height * 0.42f),
        style = Stroke(width = strokeWidth)
      )
      drawArc(
        color = Color(0xFF121212),
        startAngle = 205f,
        sweepAngle = 130f,
        useCenter = false,
        topLeft = Offset(size.width * 0.20f, size.height * 0.28f),
        size = Size(size.width * 0.60f, size.height * 0.48f),
        style = Stroke(width = strokeWidth)
      )
      drawArc(
        color = Color(0xFF121212),
        startAngle = 205f,
        sweepAngle = 130f,
        useCenter = false,
        topLeft = Offset(size.width * 0.16f, size.height * 0.14f),
        size = Size(size.width * 0.68f, size.height * 0.52f),
        style = Stroke(width = strokeWidth)
      )
    }
    Spacer(modifier = Modifier.width(6.dp))
    Text(
      text = "Spotify",
      color = Color(0xFF1ED760),
      fontSize = 18.sp,
      fontWeight = FontWeight.Bold
    )
  }
}

@Composable
private fun LocalSendCardContent() {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
    modifier = Modifier.padding(4.dp)
  ) {
    Canvas(modifier = Modifier.size(26.dp)) {
      val center = Offset(size.width / 2f, size.height / 2f)
      drawCircle(color = Color(0xFF0D9488), radius = 4.dp.toPx(), center = center)
      drawCircle(color = Color(0xFF0D9488), radius = 8.dp.toPx(), center = center, style = Stroke(width = 1.8.dp.toPx()))
      drawCircle(color = Color(0xFF0D9488).copy(alpha = 0.5f), radius = 12.dp.toPx(), center = center, style = Stroke(width = 1.2.dp.toPx()))
    }
    Spacer(modifier = Modifier.height(3.dp))
    Text(
      text = "LocalSend",
      color = Color(0xFF1F2937),
      fontSize = 15.sp,
      fontWeight = FontWeight.ExtraBold
    )
  }
}

@Composable
private fun GenericAppCardContent(app: AppItem) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Start,
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 12.dp)
  ) {
    if (app.iconDrawable != null) {
      val bitmap = remember(app.iconDrawable) {
        app.iconDrawable.toBitmap(84, 84).asImageBitmap()
      }
      Image(
        bitmap = bitmap,
        contentDescription = app.name,
        modifier = Modifier
          .size(40.dp)
          .clip(RoundedCornerShape(8.dp))
      )
    } else {
      Box(
        modifier = Modifier
          .size(40.dp)
          .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = app.name.take(1).uppercase(),
          color = Color.White,
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }
    Spacer(modifier = Modifier.width(10.dp))
    Text(
      text = app.name,
      color = Color.White,
      fontSize = 14.sp,
      fontWeight = FontWeight.SemiBold,
      maxLines = 2,
      overflow = TextOverflow.Ellipsis
    )
  }
}
