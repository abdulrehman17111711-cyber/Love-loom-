package com.example.ui.screens.games

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.EmojiEvents
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

data class FloatingBalloon(
  val id: Long,
  var x: Float, // 0..1
  var y: Float, // 0..1
  val speed: Float,
  val radius: Float,
  val color: Color,
  val points: Int,
  val isHeart: Boolean
)

@Composable
fun BalloonPopGame(
  onGameFinished: (score: Int) -> Unit,
  onSoundPop: () -> Unit,
  onBack: () -> Unit
) {
  var score by remember { mutableIntStateOf(0) }
  var timeLeft by remember { mutableIntStateOf(40) }
  var isPlaying by remember { mutableStateOf(true) }
  var isGameOver by remember { mutableStateOf(false) }

  val balloons = remember { mutableStateListOf<FloatingBalloon>() }
  val colors = listOf(
    Color(0xFFFF6B9D),
    Color(0xFFC084FC),
    Color(0xFF67E8F9),
    Color(0xFFFDE047),
    Color(0xFFF472B6),
    Color(0xFF34D399)
  )

  // Floating Loop
  LaunchedEffect(isPlaying, isGameOver) {
    var spawnTimer = 0
    var idGen = 0L

    while (isPlaying && !isGameOver) {
      delay(30)
      spawnTimer += 30

      if (spawnTimer >= 400 && balloons.size < 16) {
        spawnTimer = 0
        val isHeart = Random.nextBoolean()
        val isStar = !isHeart && Random.nextInt(100) < 25
        balloons.add(
          FloatingBalloon(
            id = ++idGen,
            x = 0.12f + Random.nextFloat() * 0.76f,
            y = 1.05f,
            speed = 0.006f + Random.nextFloat() * 0.007f,
            radius = if (isStar) 34f else 28f,
            color = if (isStar) Color(0xFFFFD54F) else colors[Random.nextInt(colors.size)],
            points = if (isStar) 30 else if (isHeart) 20 else 10,
            isHeart = isHeart
          )
        )
      }

      val toRemove = mutableListOf<FloatingBalloon>()
      for (b in balloons) {
        b.y -= b.speed
        if (b.y < -0.1f) {
          toRemove.add(b)
        }
      }
      balloons.removeAll(toRemove)
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
    timeLeft = 40
    balloons.clear()
    isGameOver = false
    isPlaying = true
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(Color(0xFFF0F9FF), Color(0xFFE0F2FE), Color(0xFFFDF2F8))
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
            .background(Color.White)
        ) {
          Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
          Text(
            text = "🎈 Popped: $score",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = Color(0xFF0369A1)
          )
        }

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
            color = if (timeLeft < 10) Color.Red else Color(0xFF0369A1)
          )
        }
      }

      // Interactive Canvas
      Box(
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth()
          .pointerInput(Unit) {
            detectTapGestures { tapOffset ->
              val w = size.width.toFloat()
              val h = size.height.toFloat()

              // Check if tapped any balloon
              val hit = balloons.findLast { b ->
                val bx = b.x * w
                val by = b.y * h
                val dist = kotlin.math.hypot(tapOffset.x - bx, tapOffset.y - by)
                dist <= b.radius * 1.5f
              }

              if (hit != null) {
                balloons.remove(hit)
                score += hit.points
                onSoundPop()
              }
            }
          }
      ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
          val w = size.width
          val h = size.height

          balloons.forEach { b ->
            val cx = b.x * w
            val cy = b.y * h

            // String
            drawLine(
              color = Color(0x66000000),
              start = Offset(cx, cy + b.radius * 0.9f),
              end = Offset(cx, cy + b.radius * 1.9f),
              strokeWidth = 2f
            )

            if (b.isHeart) {
              val heartPath = createHeartPath(Offset(cx, cy), b.radius * 2f)
              drawPath(path = heartPath, color = b.color)
            } else {
              // Round Balloon with 3D highlight
              drawOval(
                brush = Brush.radialGradient(
                  colors = listOf(Color.White.copy(alpha = 0.9f), b.color),
                  center = Offset(cx - b.radius * 0.3f, cy - b.radius * 0.3f),
                  radius = b.radius
                ),
                topLeft = Offset(cx - b.radius * 0.8f, cy - b.radius),
                size = Size(b.radius * 1.6f, b.radius * 2f)
              )
            }
          }
        }
      }

      Text(
        text = "🎈 Tap floating balloons to pop them! Golden balloons = 30 pts! ✨",
        fontSize = 11.5.sp,
        color = Color(0xFF0284C7),
        modifier = Modifier
          .align(Alignment.CenterHorizontally)
          .padding(bottom = 12.dp)
      )
    }

    // Victory / Game Over Overlay
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
              imageVector = Icons.Default.Celebration,
              contentDescription = null,
              tint = Color(0xFF0284C7),
              modifier = Modifier.size(54.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
              text = "Balloon Blitz Complete!",
              fontWeight = FontWeight.ExtraBold,
              fontSize = 20.sp,
              color = Color(0xFF0C4A6E)
            )

            Text(
              text = "You scored $score points! 🎈🎉",
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF0284C7),
              modifier = Modifier.padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            Button(
              onClick = { resetGame() },
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("balloon_retry_button"),
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
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
