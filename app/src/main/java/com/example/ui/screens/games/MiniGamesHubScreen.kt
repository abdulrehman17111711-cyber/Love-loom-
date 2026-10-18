package com.example.ui.screens.games

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GameRecord
import com.example.ui.components.FloatingParticles
import com.example.ui.navigation.AppScreen

data class MiniGameInfo(
  val id: String,
  val title: String,
  val description: String,
  val icon: ImageVector,
  val screen: AppScreen,
  val gradient: List<Color>
)

val AllMiniGames = listOf(
  MiniGameInfo(
    id = "heart_catcher",
    title = "Heart Catcher",
    description = "Catch falling sparkling hearts with the cute bear basket!",
    icon = Icons.Default.Favorite,
    screen = AppScreen.GAME_HEART_CATCHER,
    gradient = listOf(Color(0xFFFF8DA1), Color(0xFFFF2E7E))
  ),
  MiniGameInfo(
    id = "balloon_pop",
    title = "Balloon Pop",
    description = "Pop colorful floating birthday balloons before they float away!",
    icon = Icons.Default.Gamepad,
    screen = AppScreen.GAME_BALLOON_POP,
    gradient = listOf(Color(0xFF67E8F9), Color(0xFF0284C7))
  ),
  MiniGameInfo(
    id = "hidden_gift",
    title = "Find the Hidden Gift",
    description = "Watch 3 magical gift boxes shuffle and pick the lucky one!",
    icon = Icons.Default.Visibility,
    screen = AppScreen.GAME_HIDDEN_GIFT,
    gradient = listOf(Color(0xFFFBBF24), Color(0xFFD97706))
  ),
  MiniGameInfo(
    id = "memory_match",
    title = "Memory Match",
    description = "Flip cards and pair up the cute bear poses & sweets!",
    icon = Icons.Default.HelpOutline,
    screen = AppScreen.GAME_MEMORY_MATCH,
    gradient = listOf(Color(0xFFC084FC), Color(0xFF7E22CE))
  ),
  MiniGameInfo(
    id = "love_puzzle",
    title = "Love Puzzle",
    description = "Slide the 3x3 tiles to assemble the romantic bear picture!",
    icon = Icons.Default.Extension,
    screen = AppScreen.GAME_LOVE_PUZZLE,
    gradient = listOf(Color(0xFF34D399), Color(0xFF059669))
  ),
  MiniGameInfo(
    id = "gift_surprise",
    title = "Gift Box Surprise",
    description = "Unwrap ribbons and wrapping paper to reveal surprise toys!",
    icon = Icons.Default.CardGiftcard,
    screen = AppScreen.GAME_GIFT_SURPRISE,
    gradient = listOf(Color(0xFFFB7185), Color(0xFFE11D48))
  )
)

@Composable
fun MiniGamesHubScreen(
  gameRecords: List<GameRecord>,
  onSelectGame: (AppScreen) -> Unit,
  reducedMotion: Boolean = false
) {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    FloatingParticles(reducedMotion = reducedMotion)

    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      item {
        Column(modifier = Modifier.padding(bottom = 6.dp)) {
          Text(
            text = "Cute Mini-Games 🎮",
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.ExtraBold,
              color = MaterialTheme.colorScheme.primary
            )
          )
          Text(
            text = "Play together, beat your high scores & unlock celebrations!",
            style = MaterialTheme.typography.bodySmall.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          )
        }
      }

      items(AllMiniGames) { game ->
        val record = gameRecords.find { it.gameKey == game.id }

        Card(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .clickable { onSelectGame(game.screen) }
            .testTag("game_item_${game.id}"),
          shape = RoundedCornerShape(22.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(54.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Brush.linearGradient(game.gradient)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = game.icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
              )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = game.title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = game.description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
              )

              if (record != null && record.gamesPlayed > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = Color(0xFFF59E0B),
                    modifier = Modifier.size(14.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "High Score: ${record.highScore}  •  Played: ${record.gamesPlayed}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}
