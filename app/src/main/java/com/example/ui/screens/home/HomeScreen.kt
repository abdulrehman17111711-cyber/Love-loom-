package com.example.ui.screens.home

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.FloatingParticles
import com.example.ui.navigation.AppScreen
import com.example.ui.viewmodel.BirthdayCountdownState

data class HomeCardItem(
  val id: String,
  val title: String,
  val subtitle: String,
  val icon: ImageVector,
  val screen: AppScreen,
  val gradientColors: List<Color>,
  val badge: String? = null
)

@Composable
fun HomeScreen(
  recipientName: String,
  countdown: BirthdayCountdownState,
  onNavigate: (AppScreen) -> Unit,
  reducedMotion: Boolean = false,
  isPlaying: Boolean = false,
  currentTrackTitle: String = "Kuch Is Tarah",
  onToggleMusic: () -> Unit = {}
) {
  val infiniteTransition = rememberInfiniteTransition(label = "CardGlow")
  val spinAngle by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(if (isPlaying && !reducedMotion) 4000 else 600000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "miniVinylSpin"
  )

  val eqHeight1 by infiniteTransition.animateFloat(
    initialValue = 0.2f, targetValue = 0.9f,
    animationSpec = infiniteRepeatable(tween(220, easing = LinearEasing), RepeatMode.Reverse),
    label = "eqH1"
  )
  val eqHeight2 by infiniteTransition.animateFloat(
    initialValue = 0.8f, targetValue = 0.3f,
    animationSpec = infiniteRepeatable(tween(180, easing = LinearEasing), RepeatMode.Reverse),
    label = "eqH2"
  )
  val eqHeight3 by infiniteTransition.animateFloat(
    initialValue = 0.3f, targetValue = 1.0f,
    animationSpec = infiniteRepeatable(tween(280, easing = LinearEasing), RepeatMode.Reverse),
    label = "eqH3"
  )

  val homeCards = listOf(
    HomeCardItem(
      id = "birthday_celebration",
      title = "Birthday Celebration",
      subtitle = "Multi-stage 3D party",
      icon = Icons.Default.Celebration,
      screen = AppScreen.CELEBRATION,
      gradientColors = listOf(Color(0xFFFF8DA1), Color(0xFFFF5288)),
      badge = "Must Try!"
    ),
    HomeCardItem(
      id = "interactive_cake",
      title = "Interactive 3D Cake",
      subtitle = "Candles & virtual cutting",
      icon = Icons.Default.Cake,
      screen = AppScreen.CELEBRATION,
      gradientColors = listOf(Color(0xFFFFB6C1), Color(0xFFFF85A1))
    ),
    HomeCardItem(
      id = "proposal_experience",
      title = "Proposal Experience",
      subtitle = "Romantic & funny scenes",
      icon = Icons.Default.Favorite,
      screen = AppScreen.PROPOSAL,
      gradientColors = listOf(Color(0xFFFF4081), Color(0xFFC2185B)),
      badge = "Romantic"
    ),
    HomeCardItem(
      id = "mini_games",
      title = "Mini Games",
      subtitle = "6 fun bear couple games",
      icon = Icons.Default.Gamepad,
      screen = AppScreen.MINI_GAMES_HUB,
      gradientColors = listOf(Color(0xFF67E8F9), Color(0xFF06B6D4)),
      badge = "6 Games"
    ),
    HomeCardItem(
      id = "quizzes",
      title = "Fun Quizzes",
      subtitle = "Trivia, Couple & Custom",
      icon = Icons.Default.Quiz,
      screen = AppScreen.QUIZ_HUB,
      gradientColors = listOf(Color(0xFFC084FC), Color(0xFF9333EA))
    ),
    HomeCardItem(
      id = "virtual_gifts",
      title = "Virtual Gifts",
      subtitle = "Unwrap 3D surprises",
      icon = Icons.Default.Redeem,
      screen = AppScreen.VIRTUAL_GIFTS,
      gradientColors = listOf(Color(0xFFFBBF24), Color(0xFFF59E0B)),
      badge = "New"
    ),
    HomeCardItem(
      id = "love_letters",
      title = "Love Letters",
      subtitle = "Heartfelt scrolls & wishes",
      icon = Icons.Default.Email,
      screen = AppScreen.LOVE_LETTERS,
      gradientColors = listOf(Color(0xFFF472B6), Color(0xFFDB2777))
    ),
    HomeCardItem(
      id = "memories_gallery",
      title = "Memories Gallery",
      subtitle = "Private photo polaroids",
      icon = Icons.Default.PhotoLibrary,
      screen = AppScreen.MEMORIES_GALLERY,
      gradientColors = listOf(Color(0xFF818CF8), Color(0xFF4F46E5))
    ),
    HomeCardItem(
      id = "birthday_countdown",
      title = "Birthday Countdown",
      subtitle = "${countdown.days}d ${countdown.hours}h left",
      icon = Icons.Default.HourglassBottom,
      screen = AppScreen.COUNTDOWN,
      gradientColors = listOf(Color(0xFF34D399), Color(0xFF059669))
    ),
    HomeCardItem(
      id = "music_sounds",
      title = "Atif Aslam Romance",
      subtitle = "Kuch Is Tarah, Doorie, Aadat",
      icon = Icons.Default.MusicNote,
      screen = AppScreen.MUSIC_PLAYER,
      gradientColors = listOf(Color(0xFFA78BFA), Color(0xFF7C3AED)),
      badge = "Atif Aslam 🇵🇰"
    ),
    HomeCardItem(
      id = "bear_characters",
      title = "3D Bear Studio",
      subtitle = "15 cute animations",
      icon = Icons.Default.Pets,
      screen = AppScreen.BEAR_STUDIO,
      gradientColors = listOf(Color(0xFFFB923C), Color(0xFFEA580C))
    ),
    HomeCardItem(
      id = "settings_personalize",
      title = "Settings & Themes",
      subtitle = "4 themes, PIN & profile",
      icon = Icons.Default.Settings,
      screen = AppScreen.SETTINGS,
      gradientColors = listOf(Color(0xFF9CA3AF), Color(0xFF4B5563))
    )
  )

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(
            MaterialTheme.colorScheme.background,
            Color(0xFFFFF0F5),
            MaterialTheme.colorScheme.surfaceVariant
          )
        )
      )
  ) {
    FloatingParticles(reducedMotion = reducedMotion)

    LazyVerticalGrid(
      columns = GridCells.Fixed(2),
      contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 28.dp),
      horizontalArrangement = Arrangement.spacedBy(14.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
      modifier = Modifier.fillMaxSize()
    ) {
      // Cinematic 3D Hero Banner Span
      item(span = { GridItemSpan(2) }) {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .shadow(14.dp, RoundedCornerShape(32.dp))
            .clip(RoundedCornerShape(32.dp))
            .clickable { onNavigate(AppScreen.CELEBRATION) },
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = BorderStroke(1.5.dp, Color.White.copy(alpha = 0.85f))
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(246.dp)
          ) {
            Image(
              painter = painterResource(id = R.drawable.img_bear_couple_hero),
              contentDescription = "Cute Bear Couple",
              modifier = Modifier.fillMaxSize(),
              contentScale = ContentScale.Crop
            )

            // Realistic multi-tier cinematic lighting gradient overlay
            Box(
              modifier = Modifier
                .fillMaxSize()
                .background(
                  Brush.verticalGradient(
                    colors = listOf(
                      Color.Black.copy(alpha = 0.50f),
                      Color.Transparent,
                      Color.Black.copy(alpha = 0.82f)
                    )
                  )
                )
            )

            // Top Greeting & Authentic Atif Aslam Urdu Quote
            Column(
              modifier = Modifier
                .align(Alignment.TopStart)
                .padding(18.dp)
            ) {
              Text(
                text = "Hello $recipientName! 💖",
                style = MaterialTheme.typography.titleLarge.copy(
                  fontWeight = FontWeight.ExtraBold,
                  color = Color.White,
                  fontSize = 22.sp
                )
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "“کچھ اس طرح تیری پلکیں میری پلکوں سے ملا دے” ✨",
                style = MaterialTheme.typography.bodyMedium.copy(
                  color = Color(0xFFFFD1DC),
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp
                )
              )
              Text(
                text = "“Kuch is tarah... aansoo tere saare meri palkon pe sajaa de”",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = Color(0xFFFFF0F5).copy(alpha = 0.9f),
                  fontWeight = FontWeight.Medium,
                  fontSize = 10.5.sp
                )
              )
            }

            // Bottom CTA Pill & Countdown Pill
            Row(
              modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(14.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(22.dp))
                  .background(
                    Brush.horizontalGradient(
                      colors = listOf(Color(0xFFFF5288), Color(0xFFFF2E7E))
                    )
                  )
                  .padding(horizontal = 16.dp, vertical = 9.dp)
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.Celebration,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(17.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = if (countdown.isToday) "Celebrate Today! 🎉" else "Open Celebration 🎂",
                    color = Color.White,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }

              // Countdown Pill with glassmorphic backing
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(20.dp))
                  .background(Color.White.copy(alpha = 0.28f))
                  .padding(horizontal = 12.dp, vertical = 7.dp)
              ) {
                Text(
                  text = "${countdown.days}d to Birthday ⏳",
                  color = Color.White,
                  fontSize = 11.5.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }
      }

      // Dedicated Atif Aslam Romantic Player Widget (Span 2)
      item(span = { GridItemSpan(2) }) {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .clickable { onNavigate(AppScreen.MUSIC_PLAYER) }
            .testTag("home_atif_music_card"),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = BorderStroke(1.5.dp, Color(0xFFFFD1DC))
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(
                Brush.horizontalGradient(
                  colors = listOf(Color(0xFFFFF0F5), Color(0xFFFDF2F8), Color(0xFFF5F3FF))
                )
              )
              .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.weight(1f)
            ) {
              // Mini Vinyl Record Icon
              Box(
                modifier = Modifier
                  .size(46.dp)
                  .rotate(spinAngle)
                  .shadow(4.dp, CircleShape)
                  .clip(CircleShape)
                  .background(
                    Brush.radialGradient(
                      colors = listOf(Color(0xFF1E1B4B), Color(0xFF0F172A), Color(0xFF020617))
                    )
                  ),
                contentAlignment = Alignment.Center
              ) {
                Box(
                  modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFF2E7E)),
                  contentAlignment = Alignment.Center
                ) {
                  Text(text = "🇵🇰", fontSize = 10.sp)
                }
              }

              Spacer(modifier = Modifier.width(12.dp))

              Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = "Atif Aslam Romance 🎵",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF831843)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(6.dp))
                      .background(Color(0xFFFFE4EC))
                      .padding(horizontal = 4.dp, vertical = 1.dp)
                  ) {
                    Text(
                      text = "Pakistani Hits",
                      fontSize = 8.5.sp,
                      color = Color(0xFFBE185D),
                      fontWeight = FontWeight.Bold
                    )
                  }
                }
                Text(
                  text = "$currentTrackTitle • Tap for lyrics & vinyl",
                  fontSize = 11.5.sp,
                  color = Color(0xFF4B5563),
                  fontWeight = FontWeight.Medium,
                  maxLines = 1
                )
              }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
              // Mini animated equalizer bars if playing
              if (isPlaying && !reducedMotion) {
                Row(
                  horizontalArrangement = Arrangement.spacedBy(2.dp),
                  verticalAlignment = Alignment.Bottom,
                  modifier = Modifier
                    .height(20.dp)
                    .padding(end = 8.dp)
                ) {
                  Box(
                    modifier = Modifier
                      .width(3.dp)
                      .height((18 * eqHeight1).dp)
                      .clip(RoundedCornerShape(1.dp))
                      .background(Color(0xFFFF2E7E))
                  )
                  Box(
                    modifier = Modifier
                      .width(3.dp)
                      .height((18 * eqHeight2).dp)
                      .clip(RoundedCornerShape(1.dp))
                      .background(Color(0xFFFF5288))
                  )
                  Box(
                    modifier = Modifier
                      .width(3.dp)
                      .height((18 * eqHeight3).dp)
                      .clip(RoundedCornerShape(1.dp))
                      .background(Color(0xFFFF2E7E))
                  )
                }
              }

              IconButton(
                onClick = onToggleMusic,
                modifier = Modifier
                  .size(38.dp)
                  .shadow(4.dp, CircleShape)
                  .clip(CircleShape)
                  .background(Color(0xFFFF2E7E))
              ) {
                Icon(
                  imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                  contentDescription = if (isPlaying) "Pause" else "Play",
                  tint = Color.White,
                  modifier = Modifier.size(20.dp)
                )
              }
            }
          }
        }
      }

      // Feature Cards Grid with soft drop shadows and realistic borders
      items(homeCards) { card ->
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .height(144.dp)
            .shadow(6.dp, RoundedCornerShape(24.dp))
            .testTag("home_card_${card.id}")
            .clickable { onNavigate(card.screen) },
          shape = RoundedCornerShape(24.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = BorderStroke(1.dp, Color(0xFFFFE8EF))
        ) {
          Box(modifier = Modifier.fillMaxSize()) {
            Column(
              modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
              verticalArrangement = Arrangement.SpaceBetween
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
              ) {
                Box(
                  modifier = Modifier
                    .size(44.dp)
                    .shadow(4.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.linearGradient(card.gradientColors)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = card.icon,
                    contentDescription = card.title,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                  )
                }

                if (card.badge != null) {
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(12.dp))
                      .background(Color(0xFFFFF0F5))
                      .padding(horizontal = 8.dp, vertical = 3.dp)
                  ) {
                    Text(
                      text = card.badge,
                      color = Color(0xFFBE185D),
                      fontSize = 9.5.sp,
                      fontWeight = FontWeight.ExtraBold
                    )
                  }
                }
              }

              Column {
                Text(
                  text = card.title,
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.5.sp,
                  color = Color(0xFF1F2937),
                  maxLines = 1
                )
                Text(
                  text = card.subtitle,
                  fontSize = 10.5.sp,
                  color = Color(0xFF6B7280),
                  maxLines = 1
                )
              }
            }
          }
        }
      }
    }
  }
}
