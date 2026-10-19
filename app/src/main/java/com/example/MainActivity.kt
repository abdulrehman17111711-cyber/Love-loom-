package com.example

import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.components.LoveLoomTopBar
import com.example.ui.navigation.AppScreen
import com.example.ui.screens.celebration.BirthdayCelebrationScreen
import com.example.ui.screens.characters.BearStudioScreen
import com.example.ui.screens.countdown.BirthdayCountdownScreen
import com.example.ui.screens.gallery.MemoriesGalleryScreen
import com.example.ui.screens.games.BalloonPopGame
import com.example.ui.screens.games.FindHiddenGiftGame
import com.example.ui.screens.games.GiftBoxSurpriseGame
import com.example.ui.screens.games.HeartCatcherGame
import com.example.ui.screens.games.LovePuzzleGame
import com.example.ui.screens.games.MemoryMatchGame
import com.example.ui.screens.games.MiniGamesHubScreen
import com.example.ui.screens.gifts.VirtualGiftsScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.letters.LoveLettersScreen
import com.example.ui.screens.music.MusicPlayerScreen
import com.example.ui.screens.onboarding.BirthdayDateScreen
import com.example.ui.screens.onboarding.UnlockPinScreen
import com.example.ui.screens.onboarding.WelcomeScreen
import com.example.ui.screens.proposal.ProposalScreen
import com.example.ui.screens.quiz.QuizHubScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.theme.LoveLoomTheme
import com.example.ui.viewmodel.LoveLoomViewModel

class MainActivity : ComponentActivity() {

  private val viewModel: LoveLoomViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      val userProfile by viewModel.userProfile.collectAsState()
      val currentScreen by viewModel.currentScreen.collectAsState()
      val countdown by viewModel.countdown.collectAsState()
      val memories by viewModel.memories.collectAsState()
      val letters by viewModel.letters.collectAsState()
      val gameRecords by viewModel.gameRecords.collectAsState()

      val isMusicPlaying by viewModel.audioEngine.isPlaying.collectAsState()
      val currentTrackIdx by viewModel.audioEngine.currentTrackIndex.collectAsState()
      val currentTrackTitle = viewModel.audioEngine.tracks[currentTrackIdx.coerceIn(0, viewModel.audioEngine.tracks.size - 1)].title
      val volume by viewModel.audioEngine.volume.collectAsState()
      val isMusicMuted by viewModel.audioEngine.isMusicMuted.collectAsState()
      val isSoundFxMuted by viewModel.audioEngine.isSoundFxMuted.collectAsState()

      // Handle Android Back Gesture properly
      BackHandler(enabled = currentScreen != AppScreen.HOME && currentScreen != AppScreen.WELCOME) {
        viewModel.navigateBack()
      }

      LoveLoomTheme(themeIndex = userProfile.themeIndex) {
        val showTopBar = currentScreen != AppScreen.WELCOME &&
            currentScreen != AppScreen.BIRTHDAY_DATE &&
            currentScreen != AppScreen.UNLOCK_PIN &&
            currentScreen != AppScreen.MUSIC_PLAYER

        Scaffold(
          modifier = Modifier.fillMaxSize(),
          topBar = {
            if (showTopBar) {
              LoveLoomTopBar(
                title = when (currentScreen) {
                  AppScreen.HOME -> "LoveLoom 3D"
                  AppScreen.CELEBRATION -> "Birthday Celebration"
                  AppScreen.PROPOSAL -> "Proposal Experience"
                  AppScreen.MINI_GAMES_HUB -> "Mini Games"
                  AppScreen.GAME_HEART_CATCHER -> "Heart Catcher"
                  AppScreen.GAME_BALLOON_POP -> "Balloon Pop"
                  AppScreen.GAME_HIDDEN_GIFT -> "Find the Gift"
                  AppScreen.GAME_MEMORY_MATCH -> "Memory Match"
                  AppScreen.GAME_LOVE_PUZZLE -> "Love Puzzle"
                  AppScreen.GAME_GIFT_SURPRISE -> "Gift Surprise"
                  AppScreen.QUIZ_HUB -> "Fun Quizzes"
                  AppScreen.VIRTUAL_GIFTS -> "Virtual Gifts"
                  AppScreen.LOVE_LETTERS -> "Love Letters"
                  AppScreen.MEMORIES_GALLERY -> "Memories"
                  AppScreen.COUNTDOWN -> "Birthday Countdown"
                  AppScreen.BEAR_STUDIO -> "3D Bear Studio"
                  AppScreen.SETTINGS -> "Settings"
                  else -> "LoveLoom 3D"
                },
                recipientName = userProfile.recipientName,
                isPlaying = isMusicPlaying,
                currentTrackTitle = currentTrackTitle,
                onToggleMusic = { viewModel.audioEngine.togglePlayPause() },
                onMusicPillClick = { viewModel.navigateTo(AppScreen.MUSIC_PLAYER) },
                onBackClick = if (currentScreen != AppScreen.HOME) {
                  { viewModel.navigateBack() }
                } else null
              )
            }
          },
          bottomBar = {
            if (showTopBar) {
              Card(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 14.dp, vertical = 6.dp)
                  .shadow(8.dp, RoundedCornerShape(18.dp))
                  .clickable { viewModel.navigateTo(AppScreen.MUSIC_PLAYER) }
                  .testTag("persistent_mini_player"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.2.dp, Color(0xFFFFCCD5))
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                  ) {
                    Box(
                      modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (isMusicPlaying) Color(0xFFFF2E7E) else Color(0xFFFFE4EC)),
                      contentAlignment = Alignment.Center
                    ) {
                      Icon(
                        imageVector = if (isMusicPlaying) Icons.Default.GraphicEq else Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = if (isMusicPlaying) Color.White else Color(0xFFBE185D),
                        modifier = Modifier.size(18.dp)
                      )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                      Text(
                        text = currentTrackTitle,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        maxLines = 1,
                        color = MaterialTheme.colorScheme.onSurface
                      )
                      Text(
                        text = "Atif Aslam • Tap for full player 🎵",
                        fontSize = 10.5.sp,
                        color = Color(0xFFBE185D)
                      )
                    }
                  }

                  Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                      onClick = { viewModel.audioEngine.togglePlayPause() },
                      modifier = Modifier.size(38.dp).testTag("mini_player_play_pause")
                    ) {
                      Icon(
                        imageVector = if (isMusicPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isMusicPlaying) "Pause" else "Play",
                        tint = Color(0xFFFF2E7E)
                      )
                    }
                    IconButton(
                      onClick = { viewModel.audioEngine.nextTrack() },
                      modifier = Modifier.size(38.dp).testTag("mini_player_next")
                    ) {
                      Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next Track",
                        tint = Color(0xFFBE185D)
                      )
                    }
                  }
                }
              }
            }
          }
        ) { innerPadding ->
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(MaterialTheme.colorScheme.background)
              .padding(innerPadding)
          ) {
            Crossfade(targetState = currentScreen, label = "ScreenTransition") { screen ->
              when (screen) {
                AppScreen.WELCOME -> {
                  WelcomeScreen(
                    initialName = userProfile.recipientName,
                    onNameConfirmed = { confirmedName ->
                      viewModel.draftName.value = confirmedName
                      viewModel.navigateTo(AppScreen.BIRTHDAY_DATE)
                    },
                    reducedMotion = userProfile.reducedMotion
                  )
                }

                AppScreen.BIRTHDAY_DATE -> {
                  BirthdayDateScreen(
                    recipientName = viewModel.draftName.value.ifBlank { userProfile.recipientName },
                    initialDay = userProfile.birthDay,
                    initialMonth = userProfile.birthMonth,
                    initialYear = userProfile.birthYear,
                    onDateConfirmed = { day, month, year, pin, lockEnabled ->
                      viewModel.completeOnboarding(
                        name = viewModel.draftName.value,
                        day = day,
                        month = month,
                        year = year,
                        pin = pin,
                        enableLock = lockEnabled
                      )
                    },
                    reducedMotion = userProfile.reducedMotion
                  )
                }

                AppScreen.UNLOCK_PIN -> {
                  UnlockPinScreen(
                    recipientName = userProfile.recipientName,
                    expectedPin = userProfile.pinCode,
                    birthDay = userProfile.birthDay,
                    birthMonth = userProfile.birthMonth,
                    onUnlocked = { viewModel.navigateTo(AppScreen.HOME) },
                    onForgotPin = { viewModel.resetAllData() },
                    reducedMotion = userProfile.reducedMotion
                  )
                }

                AppScreen.HOME -> {
                  HomeScreen(
                    recipientName = userProfile.recipientName,
                    countdown = countdown,
                    onNavigate = { dest -> viewModel.navigateTo(dest) },
                    reducedMotion = userProfile.reducedMotion,
                    isPlaying = isMusicPlaying,
                    currentTrackTitle = currentTrackTitle,
                    onToggleMusic = { viewModel.audioEngine.togglePlayPause() }
                  )
                }

                AppScreen.CELEBRATION -> {
                  BirthdayCelebrationScreen(
                    recipientName = userProfile.recipientName,
                    onNavigateHome = { viewModel.navigateTo(AppScreen.HOME) },
                    onPlaySong = { viewModel.audioEngine.playTrack(0) }, // Plays Atif Aslam's "Kuch Is Tarah"
                    onSoundCandle = { viewModel.audioEngine.playCandleBlowSound() },
                    onSoundCakeCut = { viewModel.audioEngine.playCakeCutSound() },
                    onSoundCheer = { viewModel.audioEngine.playCheerSound() },
                    reducedMotion = userProfile.reducedMotion
                  )
                }

                AppScreen.PROPOSAL -> {
                  ProposalScreen(
                    recipientName = userProfile.recipientName,
                    onNavigateHome = { viewModel.navigateTo(AppScreen.HOME) },
                    onSoundYes = { viewModel.audioEngine.playCheerSound() },
                    onSoundTease = { viewModel.audioEngine.playTapSound() },
                    reducedMotion = userProfile.reducedMotion
                  )
                }

                AppScreen.MINI_GAMES_HUB -> {
                  MiniGamesHubScreen(
                    gameRecords = gameRecords,
                    onSelectGame = { gameScreen -> viewModel.navigateTo(gameScreen) },
                    reducedMotion = userProfile.reducedMotion
                  )
                }

                AppScreen.GAME_HEART_CATCHER -> {
                  HeartCatcherGame(
                    onGameFinished = { finalScore ->
                      viewModel.recordGameScore("heart_catcher", "Heart Catcher", finalScore)
                    },
                    onSoundCollect = { viewModel.audioEngine.playHeartCollectSound() },
                    onSoundPop = { viewModel.audioEngine.playBalloonPopSound() },
                    onBack = { viewModel.navigateBack() }
                  )
                }

                AppScreen.GAME_BALLOON_POP -> {
                  BalloonPopGame(
                    onGameFinished = { finalScore ->
                      viewModel.recordGameScore("balloon_pop", "Balloon Pop", finalScore)
                    },
                    onSoundPop = { viewModel.audioEngine.playBalloonPopSound() },
                    onBack = { viewModel.navigateBack() }
                  )
                }

                AppScreen.GAME_HIDDEN_GIFT -> {
                  FindHiddenGiftGame(
                    onGameFinished = { finalScore ->
                      viewModel.recordGameScore("hidden_gift", "Find Hidden Gift", finalScore)
                    },
                    onSoundSuccess = { viewModel.audioEngine.playCheerSound() },
                    onSoundTap = { viewModel.audioEngine.playTapSound() },
                    onBack = { viewModel.navigateBack() }
                  )
                }

                AppScreen.GAME_MEMORY_MATCH -> {
                  MemoryMatchGame(
                    onGameFinished = { finalScore ->
                      viewModel.recordGameScore("memory_match", "Memory Match", finalScore)
                    },
                    onSoundMatch = { viewModel.audioEngine.playCheerSound() },
                    onSoundFlip = { viewModel.audioEngine.playTapSound() },
                    onBack = { viewModel.navigateBack() }
                  )
                }

                AppScreen.GAME_LOVE_PUZZLE -> {
                  LovePuzzleGame(
                    onGameFinished = { finalScore ->
                      viewModel.recordGameScore("love_puzzle", "Love Puzzle", finalScore)
                    },
                    onSoundSlide = { viewModel.audioEngine.playTapSound() },
                    onSoundVictory = { viewModel.audioEngine.playCheerSound() },
                    onBack = { viewModel.navigateBack() }
                  )
                }

                AppScreen.GAME_GIFT_SURPRISE -> {
                  GiftBoxSurpriseGame(
                    onGameFinished = { finalScore ->
                      viewModel.recordGameScore("gift_surprise", "Gift Box Surprise", finalScore)
                    },
                    onSoundUnwrap = { viewModel.audioEngine.playUnwrapSound() },
                    onSoundFanfare = { viewModel.audioEngine.playCheerSound() },
                    onBack = { viewModel.navigateBack() }
                  )
                }

                AppScreen.QUIZ_HUB -> {
                  QuizHubScreen(
                    recipientName = userProfile.recipientName,
                    onLoadQuestions = { category, callback ->
                      viewModel.getQuestionsForCategory(category, callback)
                    },
                    onAddCustomQuestion = { category, q, a, b, c, d, idx, exp ->
                      viewModel.addCustomQuizQuestion(category, q, a, b, c, d, idx, exp)
                    },
                    onSoundCorrect = { viewModel.audioEngine.playHeartCollectSound() },
                    onSoundWrong = { viewModel.audioEngine.playTapSound() },
                    onBack = { viewModel.navigateBack() },
                    reducedMotion = userProfile.reducedMotion
                  )
                }

                AppScreen.VIRTUAL_GIFTS -> {
                  VirtualGiftsScreen(
                    recipientName = userProfile.recipientName,
                    onSoundOpen = { viewModel.audioEngine.playUnwrapSound() },
                    onBack = { viewModel.navigateBack() },
                    reducedMotion = userProfile.reducedMotion
                  )
                }

                AppScreen.LOVE_LETTERS -> {
                  LoveLettersScreen(
                    recipientName = userProfile.recipientName,
                    letters = letters,
                    onAddLetter = { title, content, cat ->
                      viewModel.addLetter(title, content, cat)
                    },
                    onDeleteLetter = { letter ->
                      viewModel.deleteLetter(letter)
                    },
                    onUpdateRecipientName = { newName ->
                      viewModel.updateRecipientName(newName)
                    },
                    onBack = { viewModel.navigateBack() },
                    reducedMotion = userProfile.reducedMotion
                  )
                }

                AppScreen.MEMORIES_GALLERY -> {
                  MemoriesGalleryScreen(
                    recipientName = userProfile.recipientName,
                    memories = memories,
                    onAddMemory = { uri, title, date, caption ->
                      viewModel.addMemory(uri, title, date, caption)
                    },
                    onDeleteMemory = { memory ->
                      viewModel.deleteMemory(memory)
                    },
                    onBack = { viewModel.navigateBack() },
                    reducedMotion = userProfile.reducedMotion
                  )
                }

                AppScreen.COUNTDOWN -> {
                  BirthdayCountdownScreen(
                    recipientName = userProfile.recipientName,
                    birthDay = userProfile.birthDay,
                    birthMonth = userProfile.birthMonth,
                    birthYear = userProfile.birthYear,
                    countdown = countdown,
                    onGoToCelebration = { viewModel.navigateTo(AppScreen.CELEBRATION) },
                    onBack = { viewModel.navigateBack() },
                    reducedMotion = userProfile.reducedMotion
                  )
                }

                AppScreen.MUSIC_PLAYER -> {
                  MusicPlayerScreen(
                    audioEngine = viewModel.audioEngine,
                    isPlaying = isMusicPlaying,
                    currentTrackIndex = currentTrackIdx,
                    volume = volume,
                    isMusicMuted = isMusicMuted,
                    isSoundFxMuted = isSoundFxMuted,
                    onBack = { viewModel.navigateBack() },
                    reducedMotion = userProfile.reducedMotion
                  )
                }

                AppScreen.BEAR_STUDIO -> {
                  BearStudioScreen(
                    onSoundTap = { viewModel.audioEngine.playHeartCollectSound() },
                    onBack = { viewModel.navigateBack() },
                    reducedMotion = userProfile.reducedMotion
                  )
                }

                AppScreen.SETTINGS -> {
                  SettingsScreen(
                    userProfile = userProfile,
                    onUpdateProfile = { name, day, month, year ->
                      viewModel.updateProfileDetails(name, day, month, year)
                    },
                    onUpdateTheme = { themeIdx ->
                      viewModel.updateTheme(themeIdx)
                    },
                    onUpdateLock = { enabled, pin ->
                      viewModel.updateLockSettings(enabled, pin)
                    },
                    onToggleReducedMotion = {
                      viewModel.toggleReducedMotion()
                    },
                    onResetData = {
                      viewModel.resetAllData()
                    },
                    onBack = { viewModel.navigateBack() }
                  )
                }
              }
            }
          }
        }
      }
    }
  }

  override fun onPause() {
    super.onPause()
    viewModel.audioEngine.pauseMusic()
  }

  override fun onStop() {
    super.onStop()
    viewModel.audioEngine.pauseMusic()
  }

  override fun onDestroy() {
    super.onDestroy()
    if (isFinishing) {
      viewModel.audioEngine.release()
    }
  }

  override fun onResume() {
    super.onResume()
    if (viewModel.userProfile.value.isOnboarded && viewModel.userProfile.value.musicEnabled && viewModel.currentScreen.value != AppScreen.UNLOCK_PIN) {
      viewModel.audioEngine.resumeMusic()
    }
  }
}
