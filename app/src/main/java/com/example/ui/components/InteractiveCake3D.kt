package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

data class CakeTheme(
  val name: String,
  val primaryFrosting: Color,
  val secondaryFrosting: Color,
  val creamColor: Color,
  val accentColor: Color
)

val AvailableCakeThemes = listOf(
  CakeTheme(
    name = "Strawberry Dream",
    primaryFrosting = Color(0xFFFF85A1),
    secondaryFrosting = Color(0xFFFF5288),
    creamColor = Color(0xFFFFF0F5),
    accentColor = Color(0xFFFF2E7E)
  ),
  CakeTheme(
    name = "Chocolate Velvet",
    primaryFrosting = Color(0xFF795548),
    secondaryFrosting = Color(0xFF4E342E),
    creamColor = Color(0xFFD7CCC8),
    accentColor = Color(0xFFFFB74D)
  ),
  CakeTheme(
    name = "Lavender Blossom",
    primaryFrosting = Color(0xFFB388FF),
    secondaryFrosting = Color(0xFF7C4DFF),
    creamColor = Color(0xFFEDE7F6),
    accentColor = Color(0xFFFF4081)
  ),
  CakeTheme(
    name = "Vanilla Peach",
    primaryFrosting = Color(0xFFFFCC80),
    secondaryFrosting = Color(0xFFFF9800),
    creamColor = Color(0xFFFFF8E1),
    accentColor = Color(0xFFE91E63)
  )
)

@Composable
fun InteractiveCake3D(
  recipientName: String,
  modifier: Modifier = Modifier,
  onCandleExtinguished: (() -> Unit)? = null,
  onAllCandlesBlown: (() -> Unit)? = null,
  onCakeCut: (() -> Unit)? = null,
  reducedMotion: Boolean = false
) {
  val scope = rememberCoroutineScope()
  var selectedThemeIndex by remember { mutableIntStateOf(0) }
  val currentCakeTheme = AvailableCakeThemes[selectedThemeIndex]

  // 5 candles state
  val candleLitStates = remember { mutableStateListOf(true, true, true, true, true) }
  var isCakeCut by remember { mutableStateOf(false) }
  val sliceOffsetAnim = remember { Animatable(0f) }
  val knifeCutProgress = remember { Animatable(0f) }

  val infiniteTransition = rememberInfiniteTransition(label = "CakeFlicker")
  val flameFlicker by infiniteTransition.animateFloat(
    initialValue = 0.85f,
    targetValue = if (reducedMotion) 1f else 1.15f,
    animationSpec = infiniteRepeatable(
      animation = tween(120, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "flame"
  )

  val allBlown = candleLitStates.none { it }

  LaunchedEffect(allBlown) {
    if (allBlown) {
      onAllCandlesBlown?.invoke()
    }
  }

  Column(
    modifier = modifier.fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // 3D Cake Canvas
    Box(
      modifier = Modifier
        .size(320.dp)
        .pointerInput(Unit) {
          detectTapGestures { offset ->
            // Tap area detection for 5 candles positioned across the top tier
            val w = size.width.toFloat()
            val h = size.height.toFloat()
            val candleYRange = (h * 0.10f)..(h * 0.38f)

            if (offset.y in candleYRange) {
              val candleXPositions = listOf(
                w * 0.22f,
                w * 0.36f,
                w * 0.50f,
                w * 0.64f,
                w * 0.78f
              )
              var hitIndex = -1
              var minDistance = Float.MAX_VALUE
              candleXPositions.forEachIndexed { index, cx ->
                val dist = kotlin.math.abs(offset.x - cx)
                if (dist < 40f && dist < minDistance) {
                  minDistance = dist
                  hitIndex = index
                }
              }
              if (hitIndex in candleLitStates.indices && candleLitStates[hitIndex]) {
                candleLitStates[hitIndex] = false
                onCandleExtinguished?.invoke()
              }
            }
          }
        },
      contentAlignment = Alignment.Center
    ) {
      Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Luxurious golden cake platter / stand
        drawCakeStand(w, h)

        // Tier 1 (Bottom Tier) with 3D cylindrical lighting
        drawCakeTier(
          center = Offset(w * 0.5f, h * 0.68f),
          width = w * 0.82f,
          height = h * 0.28f,
          theme = currentCakeTheme,
          text = "Happy Birthday $recipientName",
          isBottom = true
        )

        // Tier 2 (Top Tier)
        drawCakeTier(
          center = Offset(w * 0.5f, h * 0.44f),
          width = w * 0.62f,
          height = h * 0.24f,
          theme = currentCakeTheme,
          text = "💖",
          isBottom = false
        )

        // Top tier decorations (whipped cream rosettes & strawberries)
        drawWhippedCreamRosettes(
          center = Offset(w * 0.5f, h * 0.32f),
          width = w * 0.62f,
          creamColor = currentCakeTheme.creamColor
        )

        // 5 Birthday Candles
        val candleXPositions = listOf(
          w * 0.22f,
          w * 0.36f,
          w * 0.50f,
          w * 0.64f,
          w * 0.78f
        )
        val candleTopY = h * 0.24f
        val candleBottomY = h * 0.34f

        candleXPositions.forEachIndexed { index, cx ->
          val isLit = candleLitStates[index]
          drawCandle(
            cx = cx,
            topY = candleTopY,
            bottomY = candleBottomY,
            isLit = isLit,
            flameScale = if (isLit) flameFlicker else 0f
          )
        }

        // Animated knife cutting through cake
        if (knifeCutProgress.value > 0f) {
          drawVirtualKnife(
            w = w,
            h = h,
            progress = knifeCutProgress.value
          )
        }

        // Cut cake slice sliding outward onto decorative dessert plate!
        if (isCakeCut && sliceOffsetAnim.value > 0f) {
          drawServingSlice(
            center = Offset(w * 0.74f + sliceOffsetAnim.value * 0.6f, h * 0.74f + sliceOffsetAnim.value * 0.4f),
            size = w * 0.28f,
            theme = currentCakeTheme
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Interactive Action Controls
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp),
      horizontalArrangement = Arrangement.spacedBy(10.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Candle Blowing Button
      Button(
        onClick = {
          candleLitStates.indices.forEach { candleLitStates[it] = false }
          onCandleExtinguished?.invoke()
        },
        enabled = !allBlown,
        modifier = Modifier
          .weight(1f)
          .testTag("blow_candles_button"),
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.primary
        ),
        shape = RoundedCornerShape(20.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Celebration,
          contentDescription = "Blow Candles",
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.size(6.dp))
        Text(
          text = if (allBlown) "Candles Blown! 🎂" else "Blow Candles 🕯️",
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold
        )
      }

      // Cut Cake Button
      Button(
        onClick = {
          scope.launch {
            knifeCutProgress.animateTo(1f, animationSpec = tween(600, easing = FastOutSlowInEasing))
            isCakeCut = true
            sliceOffsetAnim.animateTo(60f, animationSpec = tween(700, easing = FastOutSlowInEasing))
            onCakeCut?.invoke()
          }
        },
        enabled = allBlown && !isCakeCut,
        modifier = Modifier
          .weight(1f)
          .testTag("cut_cake_button"),
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.secondary
        ),
        shape = RoundedCornerShape(20.dp)
      ) {
        Icon(
          imageVector = Icons.Default.ContentCut,
          contentDescription = "Cut Cake",
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.size(6.dp))
        Text(
          text = if (isCakeCut) "Slice Served! 🍰" else "Cut The Cake 🔪",
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold
        )
      }

      // Reset Button
      OutlinedButton(
        onClick = {
          scope.launch {
            candleLitStates.indices.forEach { candleLitStates[it] = true }
            isCakeCut = false
            sliceOffsetAnim.snapTo(0f)
            knifeCutProgress.snapTo(0f)
          }
        },
        modifier = Modifier.testTag("reset_cake_button"),
        shape = CircleShape
      ) {
        Icon(
          imageVector = Icons.Default.Refresh,
          contentDescription = "Relight & Reset",
          modifier = Modifier.size(18.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Cake Flavor Selection Chips
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp),
      horizontalArrangement = Arrangement.SpaceEvenly
    ) {
      AvailableCakeThemes.forEachIndexed { index, theme ->
        FilterChip(
          selected = selectedThemeIndex == index,
          onClick = { selectedThemeIndex = index },
          label = { Text(theme.name, fontSize = 11.sp) },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = theme.secondaryFrosting,
            selectedLabelColor = Color.White
          ),
          shape = RoundedCornerShape(14.dp)
        )
      }
    }
  }
}

private fun DrawScope.drawCakeStand(w: Float, h: Float) {
  // Soft Table Penumbra Shadow
  drawOval(
    brush = Brush.radialGradient(
      colors = listOf(Color(0x384E1A2C), Color(0x183D1422), Color.Transparent),
      center = Offset(w * 0.5f, h * 0.95f),
      radius = w * 0.48f
    ),
    topLeft = Offset(w * 0.02f, h * 0.88f),
    size = Size(w * 0.96f, h * 0.14f)
  )
  // Contact Umbra
  drawOval(
    brush = Brush.radialGradient(
      colors = listOf(Color(0x662B0B17), Color.Transparent),
      center = Offset(w * 0.5f, h * 0.92f),
      radius = w * 0.38f
    ),
    topLeft = Offset(w * 0.12f, h * 0.88f),
    size = Size(w * 0.76f, h * 0.08f)
  )

  // Pedestal base with metallic specular gradient
  drawOval(
    brush = Brush.radialGradient(
      colors = listOf(Color(0xFFFFF9C4), Color(0xFFFFD54F), Color(0xFFC79100), Color(0xFF8C6600)),
      center = Offset(w * 0.5f, h * 0.89f),
      radius = w * 0.45f
    ),
    topLeft = Offset(w * 0.08f, h * 0.84f),
    size = Size(w * 0.84f, h * 0.12f)
  )
}

private fun DrawScope.drawCakeTier(
  center: Offset,
  width: Float,
  height: Float,
  theme: CakeTheme,
  text: String,
  isBottom: Boolean
) {
  val top = center.y - height * 0.5f

  // Inter-tier contact Ambient Occlusion (soft crevice where top tier rests on bottom tier)
  if (!isBottom) {
    drawOval(
      brush = Brush.radialGradient(
        colors = listOf(Color(0x551E0E12), Color.Transparent),
        center = Offset(center.x, center.y + height * 0.5f),
        radius = width * 0.55f
      ),
      topLeft = Offset(center.x - width * 0.52f, center.y + height * 0.44f),
      size = Size(width * 1.04f, 20f),
      blendMode = BlendMode.Multiply
    )
  }

  // Tier Cylinder Body with 3D gradient
  drawRoundRect(
    brush = Brush.horizontalGradient(
      colors = listOf(
        theme.primaryFrosting.copy(alpha = 0.85f),
        theme.creamColor,
        theme.secondaryFrosting,
        theme.primaryFrosting
      ),
      startX = center.x - width * 0.5f,
      endX = center.x + width * 0.5f
    ),
    topLeft = Offset(center.x - width * 0.5f, top),
    size = Size(width, height),
    cornerRadius = CornerRadius(24f, 24f)
  )

  // Whipped cream icing drip along top edge
  val dripPath = Path().apply {
    val startX = center.x - width * 0.5f
    moveTo(startX, top)
    var currX = startX
    val step = width / 8f
    for (i in 0..7) {
      val midX = currX + step * 0.5f
      val endX = currX + step
      val dripDepth = if (i % 2 == 0) 18f else 10f
      quadraticTo(midX, top + dripDepth, endX, top)
      currX = endX
    }
    lineTo(center.x + width * 0.5f, top)
    close()
  }
  drawPath(path = dripPath, color = theme.creamColor)

  // Decorative Pearl Band
  val pearlY = center.y + height * 0.35f
  val pearlCount = if (isBottom) 12 else 8
  val spacing = width / (pearlCount + 1)
  for (i in 1..pearlCount) {
    val px = center.x - width * 0.5f + i * spacing
    drawCircle(
      color = Color.White,
      radius = 5f,
      center = Offset(px, pearlY)
    )
    drawCircle(
      color = theme.accentColor,
      radius = 2.5f,
      center = Offset(px, pearlY)
    )
  }
}

private fun DrawScope.drawWhippedCreamRosettes(
  center: Offset,
  width: Float,
  creamColor: Color
) {
  val rosetteCount = 7
  val spacing = width / (rosetteCount + 1)
  for (i in 1..rosetteCount) {
    val rx = center.x - width * 0.5f + i * spacing
    val ry = center.y
    // Cream swirl
    drawCircle(
      color = creamColor,
      radius = 12f,
      center = Offset(rx, ry)
    )
    // Sweet red sugar pearl or berry on top
    drawCircle(
      color = Color(0xFFFF2E7E),
      radius = 5f,
      center = Offset(rx, ry - 3f)
    )
  }
}

private fun DrawScope.drawCandle(
  cx: Float,
  topY: Float,
  bottomY: Float,
  isLit: Boolean,
  flameScale: Float
) {
  val candleWidth = 14f

  // Candle Body with festive spirals
  drawRoundRect(
    brush = Brush.verticalGradient(
      colors = listOf(Color(0xFFFFF9C4), Color(0xFFFFD54F), Color(0xFFFF8DA1))
    ),
    topLeft = Offset(cx - candleWidth * 0.5f, topY),
    size = Size(candleWidth, bottomY - topY),
    cornerRadius = CornerRadius(4f, 4f)
  )

  // Wick
  drawLine(
    color = Color(0xFF3E2723),
    start = Offset(cx, topY),
    end = Offset(cx, topY - 10f),
    strokeWidth = 3f
  )

  if (isLit && flameScale > 0f) {
    val flameCenter = Offset(cx, topY - 24f)
    val flameRadius = 14f * flameScale

    // Stage 3: Wide Atmospheric Bloom Aura
    drawCircle(
      brush = Brush.radialGradient(
        colors = listOf(Color(0x55FFA000), Color(0x22FFD54F), Color.Transparent),
        center = flameCenter,
        radius = flameRadius * 3.4f
      ),
      radius = flameRadius * 3.4f,
      center = flameCenter,
      blendMode = BlendMode.Screen
    )

    // Stage 2: Saturated Amber Corona Bloom
    drawCircle(
      brush = Brush.radialGradient(
        colors = listOf(Color(0xCCFFA000), Color(0x66FFD54F), Color.Transparent),
        center = flameCenter,
        radius = flameRadius * 2.0f
      ),
      radius = flameRadius * 2.0f,
      center = flameCenter,
      blendMode = BlendMode.Screen
    )

    // Stage 1: Specular White-Hot Core Bloom
    drawCircle(
      brush = Brush.radialGradient(
        colors = listOf(Color(0xEEFFFFFF), Color(0x88FFE082), Color.Transparent),
        center = flameCenter,
        radius = flameRadius * 0.85f
      ),
      radius = flameRadius * 0.85f,
      center = flameCenter,
      blendMode = BlendMode.Screen
    )

    // Outer warm flame
    val flamePath = Path().apply {
      moveTo(cx, flameCenter.y - flameRadius * 1.5f)
      cubicTo(
        cx + flameRadius * 0.9f, flameCenter.y - flameRadius * 0.5f,
        cx + flameRadius * 0.8f, flameCenter.y + flameRadius * 0.8f,
        cx, flameCenter.y + flameRadius
      )
      cubicTo(
        cx - flameRadius * 0.8f, flameCenter.y + flameRadius * 0.8f,
        cx - flameRadius * 0.9f, flameCenter.y - flameRadius * 0.5f,
        cx, flameCenter.y - flameRadius * 1.5f
      )
    }
    drawPath(
      path = flamePath,
      brush = Brush.radialGradient(
        colors = listOf(Color(0xFFFFFDE7), Color(0xFFFFCA28), Color(0xFFFF5722)),
        center = flameCenter,
        radius = flameRadius
      )
    )
  } else {
    // Extinguished delicate smoke swirl
    val smokePath = Path().apply {
      moveTo(cx, topY - 10f)
      cubicTo(cx - 8f, topY - 20f, cx + 8f, topY - 30f, cx, topY - 42f)
    }
    drawPath(
      path = smokePath,
      color = Color(0x66B0BEC5),
      style = Stroke(width = 3f)
    )
  }
}

private fun DrawScope.drawVirtualKnife(w: Float, h: Float, progress: Float) {
  val startKnife = Offset(w * 0.85f, h * 0.20f)
  val endKnife = Offset(w * 0.50f, h * 0.65f)
  val knifePos = Offset(
    startKnife.x + (endKnife.x - startKnife.x) * progress,
    startKnife.y + (endKnife.y - startKnife.y) * progress
  )

  // Golden blade
  rotate(degrees = -35f, pivot = knifePos) {
    drawRoundRect(
      brush = Brush.linearGradient(
        colors = listOf(Color(0xFFFFF9C4), Color(0xFFFFD54F), Color(0xFFFFA000))
      ),
      topLeft = Offset(knifePos.x - 8f, knifePos.y - 60f),
      size = Size(16f, 90f),
      cornerRadius = CornerRadius(6f, 6f)
    )
    // Wooden handle
    drawRoundRect(
      color = Color(0xFF5D4037),
      topLeft = Offset(knifePos.x - 10f, knifePos.y - 100f),
      size = Size(20f, 40f),
      cornerRadius = CornerRadius(6f, 6f)
    )
  }
}

private fun DrawScope.drawServingSlice(center: Offset, size: Float, theme: CakeTheme) {
  // Porcelain plate
  drawCircle(
    brush = Brush.radialGradient(
      colors = listOf(Color.White, Color(0xFFF5F5F5), Color(0xFFE0E0E0)),
      center = center,
      radius = size * 0.75f
    ),
    radius = size * 0.75f,
    center = center
  )

  // Wedge cake slice
  val slicePath = Path().apply {
    moveTo(center.x, center.y - size * 0.4f)
    lineTo(center.x + size * 0.45f, center.y + size * 0.25f)
    lineTo(center.x - size * 0.45f, center.y + size * 0.25f)
    close()
  }
  drawPath(
    path = slicePath,
    brush = Brush.verticalGradient(
      colors = listOf(theme.creamColor, theme.primaryFrosting, theme.secondaryFrosting)
    )
  )

  // Strawberry garnish on slice
  drawCircle(
    color = Color(0xFFFF2E7E),
    radius = 10f,
    center = Offset(center.x, center.y - size * 0.1f)
  )
}
