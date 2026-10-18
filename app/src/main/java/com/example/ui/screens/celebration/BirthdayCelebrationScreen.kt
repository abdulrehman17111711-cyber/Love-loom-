package com.example.ui.screens.celebration

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.BearPose
import com.example.ui.components.CuteBears3D
import com.example.ui.components.FloatingParticles
import com.example.ui.components.InteractiveCake3D

@Composable
fun BirthdayCelebrationScreen(
  recipientName: String,
  onNavigateHome: () -> Unit,
  onPlaySong: () -> Unit,
  onSoundCandle: () -> Unit,
  onSoundCakeCut: () -> Unit,
  onSoundCheer: () -> Unit,
  reducedMotion: Boolean = false
) {
  val context = LocalContext.current
  // Stages: 1: Room Reveal, 2: 3D Cake Reveal, 3: Candle Interaction, 4: Cake Cutting, 5: Confetti & Bear Dancing, 6: Completed / Replay
  var currentStage by remember { mutableIntStateOf(1) }
  var showCelebrationBanner by remember { mutableStateOf(false) }

  LaunchedEffect(currentStage) {
    if (currentStage == 5) {
      showCelebrationBanner = true
      onSoundCheer()
      onPlaySong()
    }
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(
            MaterialTheme.colorScheme.background,
            Color(0xFFFFF0F5),
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.background
          )
        )
      )
  ) {
    FloatingParticles(
      particleCount = if (currentStage >= 5) 45 else 22,
      reducedMotion = reducedMotion
    )

    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp, vertical = 16.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Stage Indicator Pill
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(16.dp))
          .background(MaterialTheme.colorScheme.primaryContainer)
          .padding(horizontal = 14.dp, vertical = 6.dp)
      ) {
        Text(
          text = when (currentStage) {
            1 -> "Stage 1: Welcome Room ✨"
            2 -> "Stage 2: 3D Cake Reveal 🎂"
            3 -> "Stage 3: Make a Wish & Blow Candles 🕯️"
            4 -> "Stage 4: Virtual Cake Cutting 🍰"
            5 -> "Stage 5: Dancing Celebration! 🎉"
            else -> "Stage 6: Joyful Celebration 💖"
          },
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onPrimaryContainer
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      Text(
        text = "Happy Birthday, $recipientName!",
        style = MaterialTheme.typography.headlineSmall.copy(
          fontWeight = FontWeight.ExtraBold,
          color = MaterialTheme.colorScheme.primary
        ),
        textAlign = TextAlign.Center
      )

      Text(
        text = "May your year be as sweet, lovely, and radiant as you! 🌸",
        style = MaterialTheme.typography.bodySmall.copy(
          color = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Animated Bears in appropriate stage pose!
      val bearPose = when (currentStage) {
        1 -> BearPose.HOLDING_BALLOONS
        2 -> BearPose.PRESENTING_CAKE
        3 -> BearPose.BLOWING_CANDLES
        4 -> BearPose.CUTTING_CAKE
        5, 6 -> BearPose.CELEBRATING
        else -> BearPose.HOLDING_HEART
      }

      CuteBears3D(
        pose = bearPose,
        size = if (currentStage in 2..4) 150.dp else 220.dp,
        reducedMotion = reducedMotion
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Multi-stage Content
      when (currentStage) {
        1 -> {
          // Room Reveal Card with realistic 3D Cake Artwork Preview
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .shadow(10.dp, RoundedCornerShape(28.dp)),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              // 3D Cake Preview Image
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .height(160.dp)
                  .clip(RoundedCornerShape(20.dp))
              ) {
                Image(
                  painter = painterResource(id = R.drawable.img_birthday_cake),
                  contentDescription = "Realistic 3D Cake",
                  modifier = Modifier.fillMaxSize(),
                  contentScale = ContentScale.Crop
                )
                Box(
                  modifier = Modifier
                    .fillMaxSize()
                    .background(
                      Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))
                      )
                    )
                )
                Text(
                  text = "Special Strawberry & Rose Cake 🎂",
                  color = Color.White,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
                )
              }

              Spacer(modifier = Modifier.height(14.dp))

              Text(
                text = "The Birthday Hall is Ready! 🎈",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 17.sp,
                color = MaterialTheme.colorScheme.onSurface
              )
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "The room is filled with soft glowing fairy lights, floating balloons, fresh rose petals, and our two little bear companions eagerly waiting for you!",
                fontSize = 12.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
              )
              Spacer(modifier = Modifier.height(18.dp))
              Button(
                onClick = { currentStage = 2 },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(48.dp)
                  .testTag("stage1_next_button"),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(24.dp)
              ) {
                Text("Reveal Interactive 3D Cake 🎂", fontWeight = FontWeight.Bold, color = Color.White)
              }
            }
          }
        }

        in 2..4 -> {
          // 3D Interactive Cake & Cutting
          InteractiveCake3D(
            recipientName = recipientName,
            onCandleExtinguished = {
              onSoundCandle()
              if (currentStage == 2) currentStage = 3
            },
            onAllCandlesBlown = {
              currentStage = 4
            },
            onCakeCut = {
              onSoundCakeCut()
              currentStage = 5
            },
            reducedMotion = reducedMotion
          )
        }

        5, 6 -> {
          // Grand Celebration Finale
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .shadow(12.dp, RoundedCornerShape(28.dp)),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Icon(
                imageVector = Icons.Default.Celebration,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
              )
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "🎉 Happy Birthday $recipientName! 🎉",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
              )
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "May all your secret wishes come true and may every moment today sparkle with sweet romance, happiness, and love! 💖",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
              )

              Spacer(modifier = Modifier.height(12.dp))

              // Romantic Atif Aslam Birthday Serenade Banner
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(16.dp))
                  .background(Color(0xFFFFF0F5))
                  .padding(horizontal = 14.dp, vertical = 8.dp)
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = Color(0xFFFF2E7E),
                    modifier = Modifier.size(18.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "Playing Atif Aslam: “Kuch Is Tarah” 🇵🇰",
                    fontSize = 11.5.sp,
                    color = Color(0xFF831843),
                    fontWeight = FontWeight.Bold
                  )
                }
              }

              Spacer(modifier = Modifier.height(18.dp))

              // Replay and Navigation buttons
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Button(
                  onClick = {
                    val sendIntent = Intent().apply {
                      action = Intent.ACTION_SEND
                      putExtra(
                        Intent.EXTRA_TEXT,
                        "Wishing $recipientName the happiest, most magical birthday with LoveLoom 3D! 🎂💖 May all your dreams sparkle!"
                      )
                      type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(sendIntent, "Share Birthday Wishes"))
                  },
                  modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("share_wishes_button"),
                  colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                  shape = RoundedCornerShape(24.dp)
                ) {
                  Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Share", fontWeight = FontWeight.Bold, color = Color.White)
                }

                OutlinedButton(
                  onClick = { currentStage = 1 },
                  modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("replay_celebration_button"),
                  shape = RoundedCornerShape(24.dp)
                ) {
                  Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Replay", fontWeight = FontWeight.Bold)
                }
              }

              Spacer(modifier = Modifier.height(10.dp))

              Button(
                onClick = onNavigateHome,
                modifier = Modifier
                  .fillMaxWidth()
                  .height(48.dp)
                  .testTag("celebration_home_button"),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                shape = RoundedCornerShape(24.dp)
              ) {
                Icon(imageVector = Icons.Default.Home, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Return to Home Screen", fontWeight = FontWeight.Bold, color = Color.White)
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}
