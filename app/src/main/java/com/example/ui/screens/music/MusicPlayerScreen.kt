package com.example.ui.screens.music

import android.view.ViewGroup
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.audio.AudioTrackInfo
import com.example.audio.LoveAudioEngine
import com.example.audio.PlaybackRepeatMode
import com.example.ui.components.FloatingParticles

enum class MusicDisplayMode {
  YOUTUBE_PLAYER,
  TURNTABLE_DECK
}

@Composable
fun MusicPlayerScreen(
  audioEngine: LoveAudioEngine,
  isPlaying: Boolean,
  currentTrackIndex: Int,
  volume: Float,
  isMusicMuted: Boolean,
  isSoundFxMuted: Boolean,
  onBack: () -> Unit,
  reducedMotion: Boolean = false
) {
  val context = LocalContext.current
  val safeTrackIndex = currentTrackIndex.coerceIn(0, audioEngine.tracks.size - 1)
  val currentTrack = audioEngine.tracks[safeTrackIndex]
  val progressSec by audioEngine.trackProgressSeconds.collectAsState()
  val durationSec by audioEngine.trackDurationSeconds.collectAsState()
  val isBuffering by audioEngine.isBuffering.collectAsState()
  val errorMessage by audioEngine.errorMessage.collectAsState()
  val isShuffle by audioEngine.isShuffle.collectAsState()
  val repeatMode by audioEngine.repeatMode.collectAsState()
  val isMusicEnabled by audioEngine.isMusicEnabled.collectAsState()

  var displayMode by remember { mutableStateOf(MusicDisplayMode.YOUTUBE_PLAYER) }

  val infiniteTransition = rememberInfiniteTransition(label = "VinylSpin")
  val spinAngle by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(if (isPlaying && !reducedMotion) 4500 else 600000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "spin"
  )

  // 6-Band Dynamic Equalizer Visualizer
  val eq1 by infiniteTransition.animateFloat(
    initialValue = 0.25f, targetValue = 0.95f,
    animationSpec = infiniteRepeatable(tween(210, easing = LinearEasing), RepeatMode.Reverse), label = "eq1"
  )
  val eq2 by infiniteTransition.animateFloat(
    initialValue = 0.85f, targetValue = 0.35f,
    animationSpec = infiniteRepeatable(tween(170, easing = LinearEasing), RepeatMode.Reverse), label = "eq2"
  )
  val eq3 by infiniteTransition.animateFloat(
    initialValue = 0.40f, targetValue = 1.0f,
    animationSpec = infiniteRepeatable(tween(290, easing = LinearEasing), RepeatMode.Reverse), label = "eq3"
  )
  val eq4 by infiniteTransition.animateFloat(
    initialValue = 0.90f, targetValue = 0.40f,
    animationSpec = infiniteRepeatable(tween(230, easing = LinearEasing), RepeatMode.Reverse), label = "eq4"
  )
  val eq5 by infiniteTransition.animateFloat(
    initialValue = 0.30f, targetValue = 0.80f,
    animationSpec = infiniteRepeatable(tween(190, easing = LinearEasing), RepeatMode.Reverse), label = "eq5"
  )
  val eq6 by infiniteTransition.animateFloat(
    initialValue = 0.70f, targetValue = 0.20f,
    animationSpec = infiniteRepeatable(tween(250, easing = LinearEasing), RepeatMode.Reverse), label = "eq6"
  )

  fun formatTime(sec: Int): String {
    val m = sec / 60
    val s = sec % 60
    return String.format("%02d:%02d", m, s)
  }

  val totalDuration = maxOf(durationSec, currentTrack.durationSeconds)

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(
            Color(0xFFFFF0F5),
            Color(0xFFFCE7F3),
            Color(0xFFF3E8FF),
            Color(0xFFFAF5FF)
          )
        )
      )
  ) {
    FloatingParticles(reducedMotion = reducedMotion)

    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 18.dp, vertical = 14.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Top Navigation & Control Bar
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = onBack,
          modifier = Modifier
            .size(42.dp)
            .shadow(6.dp, CircleShape)
            .clip(CircleShape)
            .background(Color.White)
            .testTag("music_back_button")
        ) {
          Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Back",
            tint = Color(0xFF831843)
          )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "Atif Aslam Hits 🎵",
              fontWeight = FontWeight.ExtraBold,
              fontSize = 17.sp,
              color = Color(0xFF831843)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "🇵🇰", fontSize = 16.sp)
          }
          Text(
            text = if (isMusicEnabled) {
              if (isPlaying) "Playing In-App • Original Audio" else "Paused • Ready"
            } else {
              "Music Power is OFF"
            },
            fontSize = 11.5.sp,
            color = if (isMusicEnabled) Color(0xFFBE185D) else Color.Gray,
            fontWeight = FontWeight.SemiBold
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          // Mute Toggle
          IconButton(
            onClick = { audioEngine.toggleMusicMute() },
            modifier = Modifier
              .size(40.dp)
              .shadow(4.dp, CircleShape)
              .clip(CircleShape)
              .background(Color.White)
              .testTag("mute_toggle_button")
          ) {
            Icon(
              imageVector = if (isMusicMuted) Icons.Default.MusicOff else Icons.Default.MusicNote,
              contentDescription = "Mute",
              tint = if (isMusicMuted) Color.Gray else Color(0xFFFF2E7E),
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Music Master ON/OFF Banner
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
          containerColor = if (isMusicEnabled) Color(0xFFFFF1F2) else Color(0xFFF3F4F6)
        ),
        border = BorderStroke(1.dp, if (isMusicEnabled) Color(0xFFFFCCD5) else Color(0xFFE5E7EB))
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.PowerSettingsNew,
              contentDescription = null,
              tint = if (isMusicEnabled) Color(0xFFBE185D) else Color.Gray,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = if (isMusicEnabled) "Music ON" else "Music OFF",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (isMusicEnabled) Color(0xFF831843) else Color(0xFF4B5563)
              )
              Text(
                text = if (isMusicEnabled) "Plays continuously across all app screens" else "Playback completely stopped",
                fontSize = 10.sp,
                color = if (isMusicEnabled) Color(0xFFBE185D) else Color(0xFF6B7280)
              )
            }
          }

          Switch(
            checked = isMusicEnabled,
            onCheckedChange = { audioEngine.setMusicPower(it) },
            modifier = Modifier.testTag("music_power_switch"),
            colors = SwitchDefaults.colors(
              checkedThumbColor = Color(0xFFFF2E7E),
              checkedTrackColor = Color(0xFFFFCCD5),
              uncheckedThumbColor = Color.Gray,
              uncheckedTrackColor = Color(0xFFD1D5DB)
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Mode Switcher: Video Player vs Vinyl Turntable Deck
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(Color.White.copy(alpha = 0.95f))
          .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
      ) {
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(12.dp))
            .then(
              if (displayMode == MusicDisplayMode.YOUTUBE_PLAYER) {
                Modifier.background(Brush.horizontalGradient(listOf(Color(0xFFFF2E7E), Color(0xFFBE185D))))
              } else {
                Modifier.background(Color.Transparent)
              }
            )
            .clickable { displayMode = MusicDisplayMode.YOUTUBE_PLAYER }
            .padding(vertical = 8.dp)
            .testTag("mode_youtube_tab"),
          contentAlignment = Alignment.Center
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Videocam,
              contentDescription = null,
              tint = if (displayMode == MusicDisplayMode.YOUTUBE_PLAYER) Color.White else Color(0xFF831843),
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Album Showcase 🎵",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = if (displayMode == MusicDisplayMode.YOUTUBE_PLAYER) Color.White else Color(0xFF831843)
            )
          }
        }

        Spacer(modifier = Modifier.width(6.dp))

        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(12.dp))
            .then(
              if (displayMode == MusicDisplayMode.TURNTABLE_DECK) {
                Modifier.background(Brush.horizontalGradient(listOf(Color(0xFFFF2E7E), Color(0xFFBE185D))))
              } else {
                Modifier.background(Color.Transparent)
              }
            )
            .clickable { displayMode = MusicDisplayMode.TURNTABLE_DECK }
            .padding(vertical = 8.dp)
            .testTag("mode_turntable_tab"),
          contentAlignment = Alignment.Center
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.GraphicEq,
              contentDescription = null,
              tint = if (displayMode == MusicDisplayMode.TURNTABLE_DECK) Color.White else Color(0xFF831843),
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Vinyl Turntable 💽",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = if (displayMode == MusicDisplayMode.TURNTABLE_DECK) Color.White else Color(0xFF831843)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Main Media Showcase: Album Showcase Card or Turntable Deck
      if (displayMode == MusicDisplayMode.YOUTUBE_PLAYER) {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .shadow(16.dp, RoundedCornerShape(26.dp)),
          shape = RoundedCornerShape(26.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
          border = BorderStroke(1.5.dp, Color(0xFFFFD1DC))
        ) {
          Column {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
                .clip(RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp))
                .background(
                  Brush.verticalGradient(
                    colors = listOf(Color(0xFF1E1B4B), Color(0xFF0F172A), Color(0xFF31102B))
                  )
                ),
              contentAlignment = Alignment.Center
            ) {
              Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(16.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                      Brush.radialGradient(
                        colors = listOf(Color(0xFFFF5288), Color(0xFFFF2E7E), Color(0xFF831843))
                      )
                    ),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = if (isPlaying) Icons.Default.GraphicEq else Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(42.dp)
                  )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                  text = currentTrack.title,
                  fontWeight = FontWeight.Black,
                  fontSize = 18.sp,
                  color = Color.White,
                  textAlign = TextAlign.Center
                )
                Text(
                  text = "Atif Aslam • ${currentTrack.album} (${currentTrack.releaseYear})",
                  fontSize = 12.sp,
                  color = Color(0xFFFFD1DC),
                  fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x33FF2E7E))
                    .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                  Text(
                    text = "High Fidelity Native Audio 🎵",
                    color = Color(0xFFFF8DA1),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }

              // Buffering / Loading Overlay
              if (isBuffering) {
                Box(
                  modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.55f)),
                  contentAlignment = Alignment.Center
                ) {
                  Row(
                    modifier = Modifier
                      .clip(RoundedCornerShape(12.dp))
                      .background(Color.Black.copy(alpha = 0.85f))
                      .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    CircularProgressIndicator(
                      modifier = Modifier.size(18.dp),
                      color = Color(0xFFFF2E7E),
                      strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                      text = "Loading audio... 🎵",
                      color = Color.White,
                      fontSize = 12.sp,
                      fontWeight = FontWeight.SemiBold
                    )
                  }
                }
              }
            }

            // Media player footer details
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E293B))
                .padding(horizontal = 16.dp, vertical = 10.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "${currentTrack.title} — Atif Aslam",
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.5.sp,
                  color = Color.White
                )
                Text(
                  text = "Album: ${currentTrack.album} (${currentTrack.releaseYear})",
                  fontSize = 11.sp,
                  color = Color(0xFF94A3B8)
                )
              }

              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(Color(0xFFBE185D))
                  .clickable {
                    val videoId = currentTrack.youtubeVideoId.ifBlank { "dQm7vX18K5M" }
                    val intent = android.content.Intent(
                      android.content.Intent.ACTION_VIEW,
                      android.net.Uri.parse("https://www.youtube.com/watch?v=$videoId")
                    )
                    try { context.startActivity(intent) } catch (_: Throwable) {}
                  }
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text(
                  text = "Watch Video 📺",
                  color = Color.White,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }
      } else {
        // Realistic 3D Turntable Player Deck
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .shadow(16.dp, RoundedCornerShape(34.dp)),
          shape = RoundedCornerShape(34.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = BorderStroke(1.5.dp, Color(0xFFFFD1DC))
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 24.dp),
            contentAlignment = Alignment.Center
          ) {
            // Turntable Metal Platter
            Box(
              modifier = Modifier
                .size(246.dp)
                .clip(CircleShape)
                .background(
                  Brush.radialGradient(
                    colors = listOf(Color(0xFFE2E8F0), Color(0xFF94A3B8), Color(0xFF475569))
                  )
                ),
              contentAlignment = Alignment.Center
            ) {
              // Spinning Vinyl Disc
              Box(
                modifier = Modifier
                  .size(228.dp)
                  .rotate(spinAngle)
                  .clip(CircleShape)
                  .background(
                    Brush.radialGradient(
                      colors = listOf(
                        Color(0xFF1E1B4B),
                        Color(0xFF0F172A),
                        Color(0xFF1E293B),
                        Color(0xFF090D16)
                      )
                    )
                  ),
                contentAlignment = Alignment.Center
              ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                  val r = size.width * 0.5f
                  val c = Offset(r, r)
                  for (radiusStep in listOf(0.38f, 0.48f, 0.58f, 0.68f, 0.78f, 0.88f)) {
                    drawCircle(
                      color = Color.White.copy(alpha = 0.09f),
                      radius = r * radiusStep,
                      center = c,
                      style = Stroke(width = 1.4f)
                    )
                  }
                }

                // Center Vinyl Label
                Box(
                  modifier = Modifier
                    .size(92.dp)
                    .clip(CircleShape)
                    .background(
                      Brush.radialGradient(
                        colors = listOf(Color(0xFFFF8DA1), Color(0xFFFF2E7E), Color(0xFF9D174D))
                      )
                    ),
                  contentAlignment = Alignment.Center
                ) {
                  Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "🇵🇰", fontSize = 16.sp)
                    Text(
                      text = "ATIF ASLAM",
                      fontSize = 9.sp,
                      fontWeight = FontWeight.Black,
                      color = Color.White,
                      letterSpacing = 1.sp
                    )
                    Text(
                      text = currentTrack.album,
                      fontSize = 7.5.sp,
                      color = Color(0xFFFFD1DC),
                      fontWeight = FontWeight.Medium
                    )
                  }
                }
              }
            }

            // Stylus Tonearm Needle
            Box(
              modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 22.dp, top = 8.dp)
                .size(width = 24.dp, height = 110.dp)
                .rotate(if (isPlaying) 26f else 8f)
            ) {
              Canvas(modifier = Modifier.fillMaxSize()) {
                drawRoundRect(
                  color = Color(0xFF64748B),
                  topLeft = Offset(size.width * 0.35f, 0f),
                  size = Size(6f, size.height * 0.85f),
                  cornerRadius = CornerRadius(3f, 3f)
                )
                drawCircle(
                  color = Color(0xFFE2E8F0),
                  radius = 8f,
                  center = Offset(size.width * 0.35f + 3f, 8f)
                )
                drawRoundRect(
                  color = Color(0xFFFF2E7E),
                  topLeft = Offset(size.width * 0.2f, size.height * 0.85f),
                  size = Size(14f, 20f),
                  cornerRadius = CornerRadius(4f, 4f)
                )
              }
            }
          }
        }
      }

      // Playback Error Banner with Retry button
      errorMessage?.let { err ->
        Spacer(modifier = Modifier.height(10.dp))
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
          border = BorderStroke(1.dp, Color(0xFFFCA5A5))
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Audio Notice",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = Color(0xFF991B1B)
              )
              Text(
                text = err,
                fontSize = 11.sp,
                color = Color(0xFFB91C1C)
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
              onClick = { audioEngine.retryCurrentTrack() },
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.testTag("retry_audio_button")
            ) {
              Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Retry",
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text("Retry", fontSize = 11.sp)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Track Information Card
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = currentTrack.title,
            fontWeight = FontWeight.Black,
            fontSize = 20.sp,
            color = Color(0xFF831843)
          )
          Text(
            text = "${currentTrack.artist} • ${currentTrack.album} (${currentTrack.releaseYear})",
            fontSize = 12.5.sp,
            color = Color(0xFFBE185D),
            fontWeight = FontWeight.Medium
          )
          Text(
            text = currentTrack.mood,
            fontSize = 11.sp,
            color = Color(0xFF6B7280)
          )
        }

        // Live Audio Equalizer Bars
        Row(
          horizontalArrangement = Arrangement.spacedBy(3.5.dp),
          verticalAlignment = Alignment.Bottom,
          modifier = Modifier.height(30.dp)
        ) {
          val bars = listOf(eq1, eq2, eq3, eq4, eq5, eq6)
          bars.forEach { h ->
            Box(
              modifier = Modifier
                .width(4.5.dp)
                .height((28 * if (isPlaying) h else 0.2f).dp)
                .clip(RoundedCornerShape(3.dp))
                .background(
                  Brush.verticalGradient(
                    colors = listOf(Color(0xFFFF2E7E), Color(0xFFFF5288))
                  )
                )
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Seekable Progress Scrubber Bar
      Column(modifier = Modifier.fillMaxWidth()) {
        Slider(
          value = progressSec.toFloat().coerceIn(0f, totalDuration.toFloat()),
          onValueChange = { audioEngine.seekTo(it.toInt()) },
          valueRange = 0f..totalDuration.toFloat(),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("progress_slider"),
          colors = SliderDefaults.colors(
            thumbColor = Color(0xFFFF2E7E),
            activeTrackColor = Color(0xFFFF5288),
            inactiveTrackColor = Color(0xFFFFE4EC)
          )
        )
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = formatTime(progressSec),
            fontSize = 11.5.sp,
            color = Color(0xFFBE185D),
            fontWeight = FontWeight.Bold
          )
          Text(
            text = formatTime(totalDuration),
            fontSize = 11.5.sp,
            color = Color(0xFF9CA3AF),
            fontWeight = FontWeight.Medium
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Media Controls Row: Shuffle, Previous, Play/Pause, Next, Repeat
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Shuffle Toggle
        IconButton(
          onClick = { audioEngine.toggleShuffle() },
          modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(if (isShuffle) Color(0xFFFFE4EC) else Color.White)
            .testTag("music_shuffle_button")
        ) {
          Icon(
            imageVector = Icons.Default.Shuffle,
            contentDescription = "Shuffle",
            tint = if (isShuffle) Color(0xFFFF2E7E) else Color(0xFF9CA3AF),
            modifier = Modifier.size(22.dp)
          )
        }

        // Previous Song
        IconButton(
          onClick = { audioEngine.previousTrack() },
          modifier = Modifier
            .size(54.dp)
            .shadow(6.dp, CircleShape)
            .clip(CircleShape)
            .background(Color.White)
            .testTag("music_prev_button")
        ) {
          Icon(
            imageVector = Icons.Default.SkipPrevious,
            contentDescription = "Previous",
            tint = Color(0xFFBE185D),
            modifier = Modifier.size(30.dp)
          )
        }

        // Main Play / Pause Button
        Box(
          modifier = Modifier
            .size(72.dp)
            .shadow(10.dp, CircleShape)
            .clip(CircleShape)
            .background(
              Brush.linearGradient(
                colors = listOf(Color(0xFFFF5288), Color(0xFFFF2E7E), Color(0xFFBE185D))
              )
            )
            .clickable { audioEngine.togglePlayPause() }
            .testTag("music_play_pause_button"),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = if (isPlaying) "Pause" else "Play",
            tint = Color.White,
            modifier = Modifier.size(38.dp)
          )
        }

        // Next Song
        IconButton(
          onClick = { audioEngine.nextTrack() },
          modifier = Modifier
            .size(54.dp)
            .shadow(6.dp, CircleShape)
            .clip(CircleShape)
            .background(Color.White)
            .testTag("music_next_button")
        ) {
          Icon(
            imageVector = Icons.Default.SkipNext,
            contentDescription = "Next",
            tint = Color(0xFFBE185D),
            modifier = Modifier.size(30.dp)
          )
        }

        // Repeat Toggle (All, One, Off)
        IconButton(
          onClick = { audioEngine.toggleRepeat() },
          modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(
              if (repeatMode != PlaybackRepeatMode.OFF) Color(0xFFFFE4EC) else Color.White
            )
            .testTag("music_repeat_button")
        ) {
          Icon(
            imageVector = if (repeatMode == PlaybackRepeatMode.REPEAT_ONE) Icons.Default.RepeatOne else Icons.Default.Repeat,
            contentDescription = "Repeat",
            tint = if (repeatMode != PlaybackRepeatMode.OFF) Color(0xFFFF2E7E) else Color(0xFF9CA3AF),
            modifier = Modifier.size(22.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Volume Slider
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Default.VolumeDown,
          contentDescription = null,
          tint = Color(0xFFBE185D)
        )
        Slider(
          value = volume,
          onValueChange = { audioEngine.setVolume(it) },
          modifier = Modifier
            .weight(1f)
            .padding(horizontal = 8.dp)
            .testTag("volume_slider"),
          colors = SliderDefaults.colors(
            thumbColor = Color(0xFFFF2E7E),
            activeTrackColor = Color(0xFFFF5288),
            inactiveTrackColor = Color(0xFFFFE4EC)
          )
        )
        Icon(
          imageVector = Icons.Default.VolumeUp,
          contentDescription = null,
          tint = Color(0xFFBE185D)
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Romantic Urdu Lyrics & Poetry Display Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        border = BorderStroke(1.5.dp, Color(0xFFFFD1DC))
      ) {
        Column(
          modifier = Modifier.padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFFFE4EC))
                .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
              Text(
                text = "Romantic Urdu Poetry 💌",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFBE185D)
              )
            }
            Text(
              text = "Atif Aslam 🇵🇰",
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              color = Color(0xFF831843)
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Authentic Urdu Calligraphic Lyrics
          Text(
            text = currentTrack.lyricsUrdu,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF831843),
            textAlign = TextAlign.Center,
            lineHeight = 28.sp
          )

          Spacer(modifier = Modifier.height(8.dp))

          // Roman Urdu Translation
          Text(
            text = "“${currentTrack.lyricsRoman}”",
            fontSize = 12.5.sp,
            color = Color(0xFF4B5563),
            textAlign = TextAlign.Center,
            lineHeight = 18.sp,
            fontWeight = FontWeight.Medium
          )
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Pakistani Romantic Songs Playlist
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        border = BorderStroke(1.dp, Color(0xFFFFE4EC))
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Atif Aslam Pakistani Hits 🇵🇰",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                color = Color(0xFF831843)
              )
              Text(
                text = "Original In-App Tracks & Playback",
                fontSize = 10.5.sp,
                color = Color(0xFF9CA3AF)
              )
            }
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFFFE4EC))
                .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
              Text(
                text = "${audioEngine.tracks.size} Songs",
                color = Color(0xFFBE185D),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          audioEngine.tracks.forEachIndexed { index, track ->
            val isCurrent = index == safeTrackIndex

            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .then(
                  if (isCurrent) {
                    Modifier.background(
                      Brush.horizontalGradient(
                        colors = listOf(Color(0xFFFFE4EC), Color(0xFFFFF0F5))
                      )
                    )
                  } else {
                    Modifier.background(Color.Transparent)
                  }
                )
                .clickable {
                  audioEngine.playTrack(index)
                }
                .padding(horizontal = 14.dp, vertical = 12.dp)
                .testTag("atif_track_$index"),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
              ) {
                Box(
                  modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isCurrent) Color(0xFFFF2E7E) else Color(0xFFFFF0F5)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = if (isCurrent && isPlaying) Icons.Default.GraphicEq else Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = if (isCurrent) Color.White else Color(0xFFBE185D),
                    modifier = Modifier.size(20.dp)
                  )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                  Text(
                    text = track.title,
                    fontWeight = if (isCurrent) FontWeight.ExtraBold else FontWeight.SemiBold,
                    fontSize = 14.5.sp,
                    color = if (isCurrent) Color(0xFF831843) else Color(0xFF1F2937)
                  )
                  Text(
                    text = "${track.album} (${track.releaseYear}) • ${track.mood}",
                    fontSize = 11.sp,
                    color = if (isCurrent) Color(0xFFBE185D) else Color(0xFF6B7280)
                  )
                }
              }

              if (isCurrent) {
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFFF2E7E))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                  Text(
                    text = if (isPlaying) "Playing ▶" else "Paused ⏸",
                    fontSize = 10.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                  )
                }
              } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = formatTime(track.durationSeconds),
                    fontSize = 11.sp,
                    color = Color(0xFF9CA3AF),
                    fontWeight = FontWeight.Medium
                  )
                }
              }
            }
            if (index < audioEngine.tracks.size - 1) {
              Spacer(modifier = Modifier.height(4.dp))
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(28.dp))
    }
  }
}
