package com.example.ui.screens.games

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.FloatingParticles

data class SurpriseItem(
  val title: String,
  val subtitle: String,
  val emoji: String,
  val points: Int
)

val SurpriseRewards = listOf(
  SurpriseItem("Giant Cuddle Bear", "Soft, warm, and ready for hugs anytime!", "🧸", 100),
  SurpriseItem("Enchanted Crystal Heart", "Glowing with eternal romantic starlight!", "💎", 150),
  SurpriseItem("99 Velvet Red Roses", "Hand-picked with deepest love and care!", "🌹", 120),
  SurpriseItem("Strawberry Cream Delicacy", "Freshly baked sweet birthday treat!", "🍓", 80),
  SurpriseItem("Golden Love Key", "The key to my heart forever & ever!", "🗝️", 200)
)

@Composable
fun GiftBoxSurpriseGame(
  onGameFinished: (score: Int) -> Unit,
  onSoundUnwrap: () -> Unit,
  onSoundFanfare: () -> Unit,
  onBack: () -> Unit
) {
  // 12 taps total: 4 for ribbon, 4 for wrapping paper, 4 for lid!
  var tapCount by remember { mutableIntStateOf(0) }
  var rewardIndex by remember { mutableIntStateOf(0) }
  var isFullyOpened by remember { mutableStateOf(false) }

  val maxTaps = 12
  val currentLayer = when {
    tapCount < 4 -> "Layer 1: Golden Silk Ribbon (${4 - tapCount} taps left)"
    tapCount < 8 -> "Layer 2: Pastel Strawberry Wrapping (${8 - tapCount} taps left)"
    tapCount < 12 -> "Layer 3: Surprise Box Lid (${12 - tapCount} taps left)"
    else -> "Unwrapped!"
  }

  val boxScale by animateFloatAsState(
    targetValue = 1f + (tapCount % 4) * 0.05f,
    animationSpec = tween(120),
    label = "boxScale"
  )

  fun onBoxTapped() {
    if (isFullyOpened) return
    tapCount++
    onSoundUnwrap()

    if (tapCount >= maxTaps) {
      isFullyOpened = true
      onSoundFanfare()
      onGameFinished(SurpriseRewards[rewardIndex].points)
    }
  }

  fun resetBox() {
    tapCount = 0
    isFullyOpened = false
    rewardIndex = (rewardIndex + 1) % SurpriseRewards.size
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(Color(0xFFFFF1F2), Color(0xFFFFE4E6), Color(0xFFFCE7F3))
        )
      )
  ) {
    FloatingParticles(particleCount = if (isFullyOpened) 40 else 20)

    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      // Top Bar
      Row(
        modifier = Modifier.fillMaxWidth(),
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
            text = "Tap to Unwrap! 🎁",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = Color(0xFFBE123C)
          )
        }

        IconButton(
          onClick = { resetBox() },
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(Color.White)
            .testTag("gift_reset_button")
        ) {
          Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset")
        }
      }

      // Middle Interactive Gift Box Area
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = if (!isFullyOpened) currentLayer else "Surprise Revealed! 🎉",
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp,
          color = Color(0xFFBE123C),
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        LinearProgressIndicator(
          progress = { (tapCount.toFloat() / maxTaps).coerceIn(0f, 1f) },
          modifier = Modifier
            .fillMaxWidth(0.7f)
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp)),
          color = Color(0xFFBE123C),
          trackColor = Color(0xFFFFCCD5)
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Big Tap Target Box
        Box(
          modifier = Modifier
            .size(240.dp)
            .scale(boxScale)
            .clip(RoundedCornerShape(32.dp))
            .clickable(enabled = !isFullyOpened) { onBoxTapped() }
            .testTag("interactive_surprise_box"),
          contentAlignment = Alignment.Center
        ) {
          if (!isFullyOpened) {
            Image(
              painter = painterResource(id = R.drawable.img_gift_surprise),
              contentDescription = "Magical Gift Box",
              modifier = Modifier.fillMaxSize(),
              contentScale = ContentScale.Crop
            )

            // Dynamic overlay according to layer
            if (tapCount < 4) {
              Box(
                modifier = Modifier
                  .align(Alignment.Center)
                  .clip(RoundedCornerShape(16.dp))
                  .background(Color.Black.copy(alpha = 0.5f))
                  .padding(horizontal = 12.dp, vertical = 6.dp)
              ) {
                Text(
                  text = "🎀 Tap Bow (${4 - tapCount})",
                  color = Color.White,
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp
                )
              }
            } else if (tapCount < 8) {
              Box(
                modifier = Modifier
                  .align(Alignment.Center)
                  .clip(RoundedCornerShape(16.dp))
                  .background(Color.Black.copy(alpha = 0.5f))
                  .padding(horizontal = 12.dp, vertical = 6.dp)
              ) {
                Text(
                  text = "📦 Peel Paper (${8 - tapCount})",
                  color = Color.White,
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp
                )
              }
            } else {
              Box(
                modifier = Modifier
                  .align(Alignment.Center)
                  .clip(RoundedCornerShape(16.dp))
                  .background(Color.Black.copy(alpha = 0.5f))
                  .padding(horizontal = 12.dp, vertical = 6.dp)
              ) {
                Text(
                  text = "✨ Open Lid (${12 - tapCount})",
                  color = Color.White,
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp
                )
              }
            }
          } else {
            // Revealed Reward Card inside
            val reward = SurpriseRewards[rewardIndex]
            Card(
              modifier = Modifier.fillMaxSize(),
              shape = RoundedCornerShape(32.dp),
              colors = CardDefaults.cardColors(containerColor = Color.White),
              elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
              Column(
                modifier = Modifier
                  .fillMaxSize()
                  .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
              ) {
                Text(
                  text = reward.emoji,
                  fontSize = 54.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                  text = reward.title,
                  fontWeight = FontWeight.ExtraBold,
                  fontSize = 17.sp,
                  color = Color(0xFFBE123C),
                  textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = reward.subtitle,
                  fontSize = 12.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                  text = "+${reward.points} Love Points! 💖",
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp,
                  color = Color(0xFFF59E0B)
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
          text = if (!isFullyOpened) "Tap the gift box rapidly to unwrap layer by layer! ✨" else "Item added to your Romantic Collection! 🎁",
          fontSize = 12.sp,
          color = Color(0xFFE11D48),
          fontWeight = FontWeight.Medium,
          textAlign = TextAlign.Center
        )
      }

      // Bottom Button
      Column(modifier = Modifier.fillMaxWidth()) {
        if (isFullyOpened) {
          Button(
            onClick = { resetBox() },
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("unwrap_another_button"),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBE123C)),
            shape = RoundedCornerShape(24.dp)
          ) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(6.dp))
            Text("Unwrap Another Gift Box 🎁", fontWeight = FontWeight.Bold, color = Color.White)
          }
          Spacer(modifier = Modifier.height(8.dp))
        }

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
