package com.example.ui.screens.proposal

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.BearPose
import com.example.ui.components.CuteBears3D
import com.example.ui.components.FloatingParticles
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.random.Random

enum class ProposalStyle(val title: String, val subtitle: String, val text: String) {
  CUTE(
    "Cute & Sweet",
    "Fluffy Bear Promise",
    "You make every single day feel like warm strawberry milk, sweet cuddles, and starry nights. Will you be my special person forever?"
  ),
  FUNNY(
    "Playful & Funny",
    "Banter & Honey Bribes",
    "I promise to share all my fries, give you all the warm blankets, and let you pick the movies! Will you be my forever partner-in-crime?"
  ),
  CINEMATIC(
    "Cinematic Fairy Tale",
    "Starry Garden Arch",
    "Beneath millions of glowing stars in our secret garden, there is only one smile that outshines the entire cosmos. Will you be my forever love?"
  ),
  HEARTFELT(
    "Deep & Heartfelt",
    "Soulmate Vow",
    "My heart found its true home the very moment I met you. Every beat whispers your name. Will you hold my hand through every journey of life?"
  )
}

@Composable
fun ProposalScreen(
  recipientName: String,
  onNavigateHome: () -> Unit,
  onSoundYes: () -> Unit,
  onSoundTease: () -> Unit,
  reducedMotion: Boolean = false
) {
  val scope = rememberCoroutineScope()
  var selectedStyle by remember { mutableStateOf(ProposalStyle.CUTE) }
  var hasAccepted by remember { mutableStateOf(false) }

  // Playful dodge offset for NOT NOW button
  var dodgeCount by remember { mutableIntStateOf(0) }
  val dodgeOffsetX = remember { Animatable(0f) }
  val dodgeOffsetY = remember { Animatable(0f) }

  val playfulMessages = listOf(
    "Wait! Look at the cute bears first! 🥺",
    "Are you sure? I brought strawberry cake! 🍰",
    "Try clicking the big shiny pink button instead! 💖",
    "Look at those puppy eyes! Can you really say no? 🐻",
    "Oops, the button got ticklish and jumped! 🙈"
  )
  var currentPlayfulMessage by remember { mutableStateOf("") }

  fun onPlayfulDodge() {
    dodgeCount++
    currentPlayfulMessage = playfulMessages[dodgeCount % playfulMessages.size]
    onSoundTease()
    scope.launch {
      val randomX = (Random.nextInt(60) - 30) * 4f
      val randomY = (Random.nextInt(40) - 20) * 3f
      dodgeOffsetX.animateTo(randomX, tween(250, easing = FastOutSlowInEasing))
      dodgeOffsetY.animateTo(randomY, tween(250, easing = FastOutSlowInEasing))
    }
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.background
          )
        )
      )
  ) {
    FloatingParticles(
      particleCount = if (hasAccepted) 50 else 24,
      reducedMotion = reducedMotion
    )

    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp, vertical = 20.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Style Selector Chips
      if (!hasAccepted) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceEvenly
        ) {
          ProposalStyle.values().forEach { style ->
            FilterChip(
              selected = selectedStyle == style,
              onClick = { selectedStyle = style },
              label = { Text(style.title, fontSize = 11.sp) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primary,
                selectedLabelColor = Color.White
              ),
              shape = RoundedCornerShape(14.dp)
            )
          }
        }
        Spacer(modifier = Modifier.height(14.dp))
      }

      // 3D Proposal Hero Scene Card
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(32.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(260.dp),
          contentAlignment = Alignment.Center
        ) {
          Image(
            painter = painterResource(id = R.drawable.img_bear_proposal),
            contentDescription = "Romantic Bear Proposal",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
          )

          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(
                Brush.verticalGradient(
                  colors = listOf(
                    Color.Transparent,
                    Color.Black.copy(alpha = 0.55f)
                  )
                )
              )
          )

          Box(
            modifier = Modifier
              .align(Alignment.BottomCenter)
              .padding(bottom = 12.dp)
              .clip(RoundedCornerShape(20.dp))
              .background(Color.Black.copy(alpha = 0.5f))
              .padding(horizontal = 16.dp, vertical = 6.dp)
          ) {
            Text(
              text = if (hasAccepted) "💍 She Said YES! Forever & Always! 💖" else "Will you be my forever? 🌹",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      if (!hasAccepted) {
        // Question Card
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(24.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = "Dearest $recipientName 💖",
              fontSize = 19.sp,
              fontWeight = FontWeight.ExtraBold,
              color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
              text = selectedStyle.text,
              fontSize = 14.sp,
              color = MaterialTheme.colorScheme.onSurface,
              textAlign = TextAlign.Center,
              lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
              text = "Will you be my special person?",
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary,
              textAlign = TextAlign.Center
            )

            if (currentPlayfulMessage.isNotBlank()) {
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = currentPlayfulMessage,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center
              )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // YES Button (Primary Big Action)
            Button(
              onClick = {
                hasAccepted = true
                onSoundYes()
              },
              modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("proposal_yes_button"),
              colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
              ),
              shape = RoundedCornerShape(27.dp)
            ) {
              Icon(imageVector = Icons.Default.Favorite, contentDescription = null, tint = Color.White)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "YES! A THOUSAND TIMES YES! 💖",
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
              )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Playful NOT NOW button (Moves evasively on touch, but has accessible fallback below)
            Box(
              modifier = Modifier
                .offset {
                  IntOffset(
                    dodgeOffsetX.value.roundToInt(),
                    dodgeOffsetY.value.roundToInt()
                  )
                }
            ) {
              OutlinedButton(
                onClick = { onPlayfulDodge() },
                modifier = Modifier
                  .height(44.dp)
                  .testTag("proposal_not_now_button"),
                shape = RoundedCornerShape(22.dp)
              ) {
                Text(
                  text = if (dodgeCount == 0) "NOT NOW 🙈" else "Still thinking... 🥺",
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  fontSize = 13.sp
                )
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Mandatory accessible exit/decline button as required by prompt
            TextButton(
              onClick = onNavigateHome,
              modifier = Modifier.testTag("proposal_accessible_exit_button")
            ) {
              Text(
                text = "Return to Home Screen without answering",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.5.sp
              )
            }
          }
        }
      } else {
        // YES Celebration Card!
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(26.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
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
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(54.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
              text = "💖 FOREVER & ALWAYS 💖",
              fontSize = 20.sp,
              fontWeight = FontWeight.ExtraBold,
              color = MaterialTheme.colorScheme.primary,
              textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
              text = "Official LoveLoom Certificate\nAwarded to $recipientName with infinite cuddles, strawberry kisses, and eternal happiness! 🎉✨",
              fontSize = 14.sp,
              color = MaterialTheme.colorScheme.onSurface,
              textAlign = TextAlign.Center,
              lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            CuteBears3D(
              pose = BearPose.CELEBRATING,
              size = 180.dp,
              reducedMotion = reducedMotion
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
              onClick = onNavigateHome,
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("proposal_celebration_home_button"),
              colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
              shape = RoundedCornerShape(24.dp)
            ) {
              Icon(imageVector = Icons.Default.Home, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Back to Home Screen", fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
              onClick = {
                hasAccepted = false
                dodgeCount = 0
                currentPlayfulMessage = ""
              },
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(24.dp)
            ) {
              Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Replay Proposal Experience")
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}
