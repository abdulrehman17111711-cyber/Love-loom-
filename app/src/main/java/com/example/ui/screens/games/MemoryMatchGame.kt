package com.example.ui.screens.games

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class MemoryCard(
  val id: Int,
  val symbol: String,
  val name: String,
  var isFaceUp: Boolean = false,
  var isMatched: Boolean = false
)

private val CardSymbols = listOf(
  Pair("🐻", "Teddy"),
  Pair("🎂", "Cake"),
  Pair("💖", "Heart"),
  Pair("🎁", "Gift"),
  Pair("🌹", "Rose"),
  Pair("🎈", "Balloon"),
  Pair("💍", "Ring"),
  Pair("🍓", "Berry")
)

@Composable
fun MemoryMatchGame(
  onGameFinished: (score: Int) -> Unit,
  onSoundMatch: () -> Unit,
  onSoundFlip: () -> Unit,
  onBack: () -> Unit
) {
  val scope = androidx.compose.runtime.rememberCoroutineScope()
  val cards = remember { mutableStateListOf<MemoryCard>() }
  var moves by remember { mutableIntStateOf(0) }
  var matchesFound by remember { mutableIntStateOf(0) }
  var isVictory by remember { mutableStateOf(false) }

  var firstSelectedCardIndex by remember { mutableStateOf<Int?>(null) }
  var isProcessingTurn by remember { mutableStateOf(false) }

  fun setupGame() {
    cards.clear()
    moves = 0
    matchesFound = 0
    isVictory = false
    firstSelectedCardIndex = null
    isProcessingTurn = false

    // Create 8 pairs, total 16 cards, shuffled
    val deck = mutableListOf<MemoryCard>()
    var idCounter = 0
    CardSymbols.forEach { item ->
      deck.add(MemoryCard(id = idCounter++, symbol = item.first, name = item.second))
      deck.add(MemoryCard(id = idCounter++, symbol = item.first, name = item.second))
    }
    deck.shuffle()
    cards.addAll(deck)
  }

  LaunchedEffect(Unit) {
    setupGame()
  }

  fun onCardClick(index: Int) {
    if (isProcessingTurn || cards[index].isFaceUp || cards[index].isMatched) return

    cards[index] = cards[index].copy(isFaceUp = true)
    onSoundFlip()

    if (firstSelectedCardIndex == null) {
      firstSelectedCardIndex = index
    } else {
      val firstIdx = firstSelectedCardIndex!!
      firstSelectedCardIndex = null
      moves++
      isProcessingTurn = true

      if (cards[firstIdx].symbol == cards[index].symbol) {
        // Matched!
        cards[firstIdx] = cards[firstIdx].copy(isMatched = true)
        cards[index] = cards[index].copy(isMatched = true)
        matchesFound++
        onSoundMatch()
        isProcessingTurn = false

        if (matchesFound == 8) {
          isVictory = true
          val finalScore = maxOf(100, 500 - moves * 15)
          onGameFinished(finalScore)
        }
      } else {
        // Not matched, flip back after brief pause
        scope.launch {
          delay(700)
          cards[firstIdx] = cards[firstIdx].copy(isFaceUp = false)
          cards[index] = cards[index].copy(isFaceUp = false)
          isProcessingTurn = false
        }
      }
    }
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(Color(0xFFF3E8FF), Color(0xFFE9D5FF), Color(0xFFFCE7F3))
        )
      )
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally
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
            text = "Pairs: $matchesFound / 8  •  Moves: $moves",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = Color(0xFF6B21A8)
          )
        }

        IconButton(
          onClick = { setupGame() },
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(Color.White)
            .testTag("memory_match_restart_button")
        ) {
          Icon(imageVector = Icons.Default.Refresh, contentDescription = "Restart")
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      Text(
        text = "Memory Match 💖",
        fontWeight = FontWeight.ExtraBold,
        fontSize = 20.sp,
        color = Color(0xFF6B21A8)
      )

      Text(
        text = "Find all 8 matching romantic pairs!",
        fontSize = 12.sp,
        color = Color(0xFF9333EA)
      )

      Spacer(modifier = Modifier.height(16.dp))

      // 4x4 Grid of Cards
      LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(4.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        itemsIndexed(cards) { index, card ->
          val rotation by animateFloatAsState(
            targetValue = if (card.isFaceUp || card.isMatched) 180f else 0f,
            animationSpec = tween(300),
            label = "cardFlip"
          )

          Box(
            modifier = Modifier
              .aspectRatio(0.85f)
              .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
              }
              .clip(RoundedCornerShape(14.dp))
              .background(
                when {
                  card.isMatched -> Color(0xFF86EFAC)
                  card.isFaceUp -> Color.White
                  else -> Color(0xFFA855F7)
                }
              )
              .clickable(enabled = !card.isFaceUp && !card.isMatched) {
                onCardClick(index)
              }
              .testTag("memory_card_$index"),
            contentAlignment = Alignment.Center
          ) {
            if (rotation > 90f) {
              // Face-up content
              Box(
                modifier = Modifier
                  .fillMaxSize()
                  .graphicsLayer { rotationY = 180f },
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = card.symbol,
                  fontSize = 26.sp
                )
              }
            } else {
              // Back of card (Heart pattern)
              Text(
                text = "💖",
                fontSize = 18.sp
              )
            }
          }
        }
      }
    }

    // Victory Dialog
    if (isVictory) {
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
              tint = Color(0xFFA855F7),
              modifier = Modifier.size(54.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
              text = "Spectacular Memory! 🎉",
              fontWeight = FontWeight.ExtraBold,
              fontSize = 20.sp,
              color = Color(0xFF4A1525)
            )

            Text(
              text = "You matched all 8 pairs in $moves moves! 💖",
              fontSize = 14.sp,
              fontWeight = FontWeight.SemiBold,
              color = Color(0xFF9333EA),
              modifier = Modifier.padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            Button(
              onClick = { setupGame() },
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("memory_replay_button"),
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA855F7)),
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
