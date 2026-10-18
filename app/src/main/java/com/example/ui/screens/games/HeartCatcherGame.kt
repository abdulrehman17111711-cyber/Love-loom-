package com.example.ui.screens.games

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.createHeartPath
import kotlinx.coroutines.delay
import kotlin.random.Random

data class FallingHeart(
  val id: Long,
  var x: Float,
  var y: Float,
  val speed: Float,
  val size: Float,
  val points: Int,
  val isCloud: Boolean
)

@Composable
fun HeartCatcherGame(
  onGameFinished: (score: Int) -> Unit,
  onSoundCollect: () -> Unit,
  onSoundPop: () -> Unit,
  onBack: () -> Unit
) {
  var score by remember { mutableIntStateOf(0) }
  var timeLeft by remember { mutableIntStateOf(45) }
  var isPlaying by remember { mutableStateOf(true) }
  var isGameOver by remember { mutableStateOf(false) }

  // Basket horizontal position (0..1)
  var basketNormX by remember { mutableFloatStateOf(0.5f) }
  val fallingItems = remember { mutableStateListOf<FallingHeart>() }

  // Game loop
  LaunchedEffect(isPlaying, isGameOver) {
    var nextSpawnTime = 0
    var itemCounter = 0L

    while (isPlaying && !isGameOver) {
      delay(30)

      // Spawn new items
      nextSpawnTime += 30
      if (nextSpawnTime >= 500) {
        nextSpawnTime = 0
        val isCloud = Random.nextInt(100) < 18
        val isGold = !isCloud && Random.nextInt(100) < 25
        fallingItems.add(
          FallingHeart(
            id = ++itemCounter,
            x = 0.1f + Random.nextFloat() * 0.8f,
            y = -0.05f,
            speed = 0.007f + Random.nextFloat() * 0.008f,
            size = if (isCloud) 44f else if (isGold) 36f else 28f,
            points = if (isCloud) -15 else if (isGold) 30 else 10,
            isCloud = isCloud
          )
        )
      }

      // Update positions
      val toRemove = mutableListOf<FallingHeart>()
      for (item in fallingItems) {
        item.y += item.speed

        // Catch collision near bottom (y around 0.85f)
        if (item.y in 0.80f..0.89f) {
          val basketWidthNorm = 0.22f
          if (kotlin.math.abs(item.x - basketNormX) < basketWidthNorm * 0.5f) {
            toRemove.add(item)
            score = (score + item.points).coerceAtLeast(0)
            if (item.isCloud) onSoundPop() else onSoundCollect()
          }
        } else if (item.y > 1.05f) {
          toRemove.add(item)
        }
      }
      fallingItems.removeAll(toRemove)
    }
  }

  // Timer loop
  LaunchedEffect(isPlaying, isGameOver) {
    while (isPlaying && !isGameOver && timeLeft > 0) {
      delay(1000)
      timeLeft--
      if (timeLeft <= 0) {
        isGameOver = true
        isPlaying = false
        onGameFinished(score)
      }
    }
  }

  fun resetGame() {
    score = 0
    timeLeft = 45
    fallingItems.clear()
    basketNormX = 0.5f
    isGameOver = false
    isPlaying = true
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(Color(0xFFFFF0F5), Color(0xFFFFE4EC), Color(0xFFF3E8FF))
        )
      )
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      // Game Top Bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = onBack,
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.8f))
        ) {
          Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
        }

        // Score Pill
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Favorite,
              contentDescription = null,
              tint = Color(0xFFFF2E7E),
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Score: $score",
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp,
              color = Color(0xFF4A1525)
            )
          }
        }

        // Time Pill
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
          Text(
            text = "⏳ ${timeLeft}s",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = if (timeLeft < 10) Color.Red else Color(0xFF4A1525)
          )
        }
      }

      // Interactive Game Canvas
      Box(
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth()
          .pointerInput(Unit) {
            detectDragGestures { change, _ ->
              change.consume()
              val norm = (change.position.x / size.width.toFloat()).coerceIn(0.12f, 0.88f)
              basketNormX = norm
            }
          }
          .pointerInput(Unit) {
            detectTapGestures { offset ->
              val norm = (offset.x / size.width.toFloat()).coerceIn(0.12f, 0.88f)
              basketNormX = norm
            }
          }
      ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
          val w = size.width
          val h = size.height

          // Draw falling items
          fallingItems.forEach { item ->
            val px = item.x * w
            val py = item.y * h

            if (item.isCloud) {
              // Storm cloud
              drawCircle(
                color = Color(0xFF78909C),
                radius = item.size * 0.4f,
                center = Offset(px - 10f, py)
              )
              drawCircle(
                color = Color(0xFF546E7A),
                radius = item.size * 0.5f,
                center = Offset(px, py - 6f)
              )
              drawCircle(
                color = Color(0xFF78909C),
                radius = item.size * 0.4f,
                center = Offset(px + 10f, py)
              )
            } else {
              // Heart (Golden or Pink)
              val heartPath = createHeartPath(Offset(px, py), item.size)
              val heartColor = if (item.points >= 30) Color(0xFFFFD54F) else Color(0xFFFF2E7E)
              drawPath(path = heartPath, color = heartColor)
            }
          }

          // Basket & Cute Teddy Helper at basketNormX
          val bx = basketNormX * w
          val by = h * 0.86f
          val basketW = w * 0.24f
          val basketH = 34f

          // Cute Bear Paws / Head peeking out of basket
          drawCircle(
            color = Color(0xFFFAF3EC),
            radius = 20f,
            center = Offset(bx, by - 12f)
          )
          // Blush cheeks
          drawCircle(
            color = Color(0xFFFF8DA1),
            radius = 5f,
            center = Offset(bx - 12f, by - 8f)
          )
          drawCircle(
            color = Color(0xFFFF8DA1),
            radius = 5f,
            center = Offset(bx + 12f, by - 8f)
          )
          // Cute nose
          drawCircle(
            color = Color(0xFF332018),
            radius = 3.5f,
            center = Offset(bx, by - 10f)
          )

          // Woven basket base
          drawRoundRect(
            brush = Brush.verticalGradient(
              colors = listOf(Color(0xFFE8B87D), Color(0xFFC68A57))
            ),
            topLeft = Offset(bx - basketW * 0.5f, by),
            size = Size(basketW, basketH),
            cornerRadius = CornerRadius(12f, 12f)
          )
          // Ribbon
          drawRoundRect(
            color = Color(0xFFFF2E7E),
            topLeft = Offset(bx - basketW * 0.5f, by + 8f),
            size = Size(basketW, 8f),
            cornerRadius = CornerRadius(4f, 4f)
          )
        }
      }

      // Drag hint
      Text(
        text = "👈 Drag or tap left/right to catch the hearts! Avoid storm clouds 👉",
        fontSize = 11.5.sp,
        color = Color(0xFF8A4860),
        modifier = Modifier
          .align(Alignment.CenterHorizontally)
          .padding(bottom = 12.dp)
      )
    }

    // Game Over Overlay Dialog
    if (isGameOver) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
      ) {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
          shape = RoundedCornerShape(28.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(
              imageVector = Icons.Default.EmojiEvents,
              contentDescription = null,
              tint = Color(0xFFF59E0B),
              modifier = Modifier.size(54.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
              text = "Time's Up!",
              fontWeight = FontWeight.ExtraBold,
              fontSize = 22.sp,
              color = Color(0xFF4A1525)
            )

            Text(
              text = "You caught $score love points! 💖",
              fontSize = 15.sp,
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.primary,
              modifier = Modifier.padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            Button(
              onClick = { resetGame() },
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("game_retry_button"),
              colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
              shape = RoundedCornerShape(24.dp)
            ) {
              Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
              Spacer(modifier = Modifier.width(6.dp))
              Text("Play Again", fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
              onClick = onBack,
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
              shape = RoundedCornerShape(24.dp)
            ) {
              Text("Back to Games Hub")
            }
          }
        }
      }
    }
  }
}
