package com.example.ui.screens.games

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Visibility
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import kotlin.math.abs
import kotlin.random.Random

@Composable
fun LovePuzzleGame(
  onGameFinished: (score: Int) -> Unit,
  onSoundSlide: () -> Unit,
  onSoundVictory: () -> Unit,
  onBack: () -> Unit
) {
  // 3x3 grid: 0 represents the empty slot, 1..8 represent tile numbers
  val tiles = remember { mutableStateListOf(1, 2, 3, 4, 5, 6, 7, 8, 0) }
  var moves by remember { mutableIntStateOf(0) }
  var isSolved by remember { mutableStateOf(false) }
  var showHint by remember { mutableStateOf(false) }

  fun checkSolved(): Boolean {
    for (i in 0..7) {
      if (tiles[i] != i + 1) return false
    }
    return tiles[8] == 0
  }

  fun shuffleTiles() {
    moves = 0
    isSolved = false

    // Perform 30 random valid sliding moves from solved state so puzzle is always solvable!
    val currentTiles = mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 0)
    for (step in 0..30) {
      val emptyIdx = currentTiles.indexOf(0)
      val row = emptyIdx / 3
      val col = emptyIdx % 3
      val validNeighbors = mutableListOf<Int>()
      if (row > 0) validNeighbors.add((row - 1) * 3 + col)
      if (row < 2) validNeighbors.add((row + 1) * 3 + col)
      if (col > 0) validNeighbors.add(row * 3 + (col - 1))
      if (col < 2) validNeighbors.add(row * 3 + (col + 1))

      val target = validNeighbors[Random.nextInt(validNeighbors.size)]
      currentTiles[emptyIdx] = currentTiles[target]
      currentTiles[target] = 0
    }

    tiles.clear()
    tiles.addAll(currentTiles)
  }

  LaunchedEffect(Unit) {
    shuffleTiles()
  }

  fun onTileClicked(index: Int) {
    if (isSolved || tiles[index] == 0) return

    val emptyIdx = tiles.indexOf(0)
    val tileRow = index / 3
    val tileCol = index % 3
    val emptyRow = emptyIdx / 3
    val emptyCol = emptyIdx % 3

    // Check if neighbor of empty slot
    val isNeighbor = (abs(tileRow - emptyRow) + abs(tileCol - emptyCol)) == 1
    if (isNeighbor) {
      tiles[emptyIdx] = tiles[index]
      tiles[index] = 0
      moves++
      onSoundSlide()

      if (checkSolved()) {
        isSolved = true
        onSoundVictory()
        val score = maxOf(100, 600 - moves * 20)
        onGameFinished(score)
      }
    }
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(Color(0xFFECFDF5), Color(0xFFD1FAE5), Color(0xFFFDF2F8))
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
            text = "Moves: $moves",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = Color(0xFF047857)
          )
        }

        IconButton(
          onClick = { shuffleTiles() },
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(Color.White)
            .testTag("puzzle_shuffle_button")
        ) {
          Icon(imageVector = Icons.Default.Refresh, contentDescription = "Shuffle")
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      Text(
        text = "Love Puzzle 🧩",
        fontWeight = FontWeight.ExtraBold,
        fontSize = 20.sp,
        color = Color(0xFF047857)
      )

      Text(
        text = "Slide adjacent tiles to order them 1 to 8!",
        fontSize = 12.sp,
        color = Color(0xFF059669)
      )

      Spacer(modifier = Modifier.height(20.dp))

      // 3x3 Puzzle Grid
      Box(
        modifier = Modifier
          .size(300.dp)
          .clip(RoundedCornerShape(20.dp))
          .background(Color(0xFF047857).copy(alpha = 0.15f))
          .padding(8.dp)
      ) {
        LazyVerticalGrid(
          columns = GridCells.Fixed(3),
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp),
          contentPadding = PaddingValues(0.dp),
          modifier = Modifier.fillMaxSize()
        ) {
          itemsIndexed(tiles) { index, tileValue ->
            if (tileValue == 0) {
              // Empty Slot
              Box(
                modifier = Modifier
                  .aspectRatio(1f)
                  .clip(RoundedCornerShape(12.dp))
                  .background(Color.White.copy(alpha = 0.3f))
              )
            } else {
              // Tile with number and cute emoji
              val emojis = listOf("", "🐻", "💖", "🎂", "🌸", "🎁", "🍓", "💍", "✨")
              Box(
                modifier = Modifier
                  .aspectRatio(1f)
                  .clip(RoundedCornerShape(12.dp))
                  .background(
                    Brush.verticalGradient(
                      colors = listOf(Color(0xFF34D399), Color(0xFF059669))
                    )
                  )
                  .clickable { onTileClicked(index) }
                  .testTag("puzzle_tile_$index"),
                contentAlignment = Alignment.Center
              ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Text(
                    text = emojis[tileValue],
                    fontSize = 20.sp
                  )
                  Text(
                    text = tileValue.toString(),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = Color.White
                  )
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      OutlinedButton(
        onClick = { showHint = !showHint },
        shape = RoundedCornerShape(20.dp)
      ) {
        Icon(imageVector = Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(if (showHint) "Hide Solved Order" else "Show Solved Order 1..8")
      }

      if (showHint) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "Target Order: [1, 2, 3] on row 1, [4, 5, 6] on row 2, [7, 8, Empty] on row 3! ✨",
          fontSize = 11.5.sp,
          color = Color(0xFF047857),
          fontWeight = FontWeight.Medium
        )
      }
    }

    // Victory Dialog
    if (isSolved) {
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
              tint = Color(0xFF059669),
              modifier = Modifier.size(54.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
              text = "Puzzle Solved! 🎉",
              fontWeight = FontWeight.ExtraBold,
              fontSize = 20.sp,
              color = Color(0xFF064E3B)
            )

            Text(
              text = "Completed in $moves moves! You're brilliant! 💖",
              fontSize = 14.sp,
              fontWeight = FontWeight.SemiBold,
              color = Color(0xFF059669),
              modifier = Modifier.padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            Button(
              onClick = { shuffleTiles() },
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("puzzle_replay_button"),
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
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
