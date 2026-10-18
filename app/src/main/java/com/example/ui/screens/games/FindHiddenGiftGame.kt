package com.example.ui.screens.games

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.random.Random

@Composable
fun FindHiddenGiftGame(
  onGameFinished: (score: Int) -> Unit,
  onSoundSuccess: () -> Unit,
  onSoundTap: () -> Unit,
  onBack: () -> Unit
) {
  val scope = rememberCoroutineScope()
  var round by remember { mutableIntStateOf(1) }
  var streak by remember { mutableIntStateOf(0) }
  var score by remember { mutableIntStateOf(0) }

  var giftIndex by remember { mutableIntStateOf(1) } // 0, 1, 2
  var isShuffling by remember { mutableStateOf(false) }
  var isRevealed by remember { mutableStateOf(false) }
  var selectedIndex by remember { mutableIntStateOf(-1) }
  var statusMessage by remember { mutableStateOf("Watch carefully where the golden heart hides! 💖") }

  // Offsets for shuffling animation
  val offset0 = remember { Animatable(0f) }
  val offset1 = remember { Animatable(0f) }
  val offset2 = remember { Animatable(0f) }

  fun startNewRound() {
    scope.launch {
      selectedIndex = -1
      isRevealed = false
      giftIndex = Random.nextInt(3)
      statusMessage = "Gift is in Box #${giftIndex + 1}! Closing lid..."
      delay(1200)

      isShuffling = true
      statusMessage = "Shuffling the boxes! Keep your eyes on it! 👀"

      val shuffleSwaps = 6
      for (s in 0 until shuffleSwaps) {
        val i1 = Random.nextInt(3)
        var i2 = Random.nextInt(3)
        while (i2 == i1) i2 = Random.nextInt(3)

        // Animate swap
        val a1 = when (i1) { 0 -> offset0; 1 -> offset1; else -> offset2 }
        val a2 = when (i2) { 0 -> offset0; 1 -> offset1; else -> offset2 }

        val delta = (i2 - i1) * 90f
        launch { a1.animateTo(delta, tween(160, easing = FastOutSlowInEasing)) }
        a2.animateTo(-delta, tween(160, easing = FastOutSlowInEasing))

        // Reset positions
        a1.snapTo(0f)
        a2.snapTo(0f)

        // Swap logical giftIndex if affected
        if (giftIndex == i1) giftIndex = i2
        else if (giftIndex == i2) giftIndex = i1

        delay(40)
      }

      isShuffling = false
      statusMessage = "Where is the hidden golden gift? Pick a box! 🎁"
    }
  }

  fun onBoxChosen(idx: Int) {
    if (isShuffling || isRevealed) return
    selectedIndex = idx
    isRevealed = true

    if (idx == giftIndex) {
      streak++
      score += 50 * streak
      statusMessage = "🎉 You found it! +${50 * streak} points! 💖"
      onSoundSuccess()
    } else {
      streak = 0
      statusMessage = "Oops! It was in Box #${giftIndex + 1}! Try next round! 🥺"
      onSoundTap()
    }
    onGameFinished(score)
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(Color(0xFFFEF3C7), Color(0xFFFDE68A), Color(0xFFFFFBEB))
        )
      )
  ) {
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
            text = "Score: $score  •  Streak: $streak 🔥",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = Color(0xFF92400E)
          )
        }
      }

      // Middle Stage - 3 Gift Boxes
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
          text = "Find the Hidden Gift 🎁",
          fontWeight = FontWeight.ExtraBold,
          fontSize = 20.sp,
          color = Color(0xFF92400E)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = statusMessage,
          fontSize = 13.sp,
          fontWeight = FontWeight.Medium,
          color = Color(0xFFB45309),
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(36.dp))

        // Row of 3 Gift Boxes
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceEvenly,
          verticalAlignment = Alignment.CenterVertically
        ) {
          val animators = listOf(offset0, offset1, offset2)

          repeat(3) { boxIdx ->
            val hasGift = boxIdx == giftIndex
            val isSelected = boxIdx == selectedIndex

            Box(
              modifier = Modifier
                .offset { IntOffset(animators[boxIdx].value.roundToInt(), 0) }
                .size(92.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                  when {
                    isRevealed && hasGift -> Brush.verticalGradient(listOf(Color(0xFFFBBF24), Color(0xFFD97706)))
                    isRevealed && isSelected -> Brush.verticalGradient(listOf(Color(0xFFF87171), Color(0xFFDC2626)))
                    else -> Brush.verticalGradient(listOf(Color(0xFFFF85A1), Color(0xFFFF4081)))
                  }
                )
                .clickable(enabled = !isShuffling && !isRevealed) {
                  onBoxChosen(boxIdx)
                }
                .testTag("gift_box_$boxIdx"),
              contentAlignment = Alignment.Center
            ) {
              if (isRevealed && hasGift) {
                Icon(
                  imageVector = Icons.Default.Favorite,
                  contentDescription = "Hidden Gift",
                  tint = Color.White,
                  modifier = Modifier.size(44.dp)
                )
              } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Icon(
                    imageVector = Icons.Default.CardGiftcard,
                    contentDescription = "Gift Box",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                  )
                  Text(
                    text = "#${boxIdx + 1}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color.White
                  )
                }
              }
            }
          }
        }
      }

      // Bottom Control
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Button(
          onClick = {
            round++
            startNewRound()
          },
          enabled = !isShuffling,
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("hidden_gift_start_button"),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
          shape = RoundedCornerShape(25.dp)
        ) {
          Icon(
            imageVector = if (isRevealed) Icons.Default.Refresh else Icons.Default.PlayArrow,
            contentDescription = null,
            tint = Color.White
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = if (isRevealed) "Next Round 🎮" else "Start Round $round",
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = 15.sp
          )
        }

        Spacer(modifier = Modifier.height(10.dp))
      }
    }
  }
}
