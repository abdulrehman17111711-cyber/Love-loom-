package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.random.Random

data class Particle(
  val initialX: Float, // 0..1
  val initialY: Float, // 0..1
  val speed: Float,
  val size: Float,
  val alpha: Float,
  val isHeart: Boolean,
  val color: Color
)

@Composable
fun FloatingParticles(
  modifier: Modifier = Modifier,
  particleCount: Int = 22,
  reducedMotion: Boolean = false
) {
  if (reducedMotion) return

  val particles = remember {
    val colors = listOf(
      Color(0xFFFF8DA1),
      Color(0xFFFF2E7E),
      Color(0xFFFFD54F),
      Color(0xFFC493E8),
      Color(0xFF80D8FF)
    )
    List(particleCount) {
      Particle(
        initialX = Random.nextFloat(),
        initialY = Random.nextFloat(),
        speed = 0.05f + Random.nextFloat() * 0.08f,
        size = 6f + Random.nextFloat() * 10f,
        alpha = 0.35f + Random.nextFloat() * 0.45f,
        isHeart = Random.nextBoolean(),
        color = colors[Random.nextInt(colors.size)]
      )
    }
  }

  val infiniteTransition = rememberInfiniteTransition(label = "ParticlesAnim")
  val progress by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(12000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "progress"
  )

  Canvas(modifier = modifier.fillMaxSize()) {
    val w = size.width
    val h = size.height

    particles.forEach { p ->
      val currentY = (p.initialY - progress * p.speed * 4f) % 1.0f
      val adjustedY = if (currentY < 0f) currentY + 1f else currentY
      val sway = kotlin.math.sin((progress * 4f * Math.PI + p.initialX * 10f)).toFloat() * 18f
      val px = p.initialX * w + sway
      val py = adjustedY * h

      if (p.isHeart) {
        val heartPath = createHeartPath(Offset(px, py), p.size * 1.5f)
        drawPath(
          path = heartPath,
          color = p.color.copy(alpha = p.alpha)
        )
      } else {
        drawCircle(
          color = p.color.copy(alpha = p.alpha),
          radius = p.size * 0.5f,
          center = Offset(px, py)
        )
      }
    }
  }
}
