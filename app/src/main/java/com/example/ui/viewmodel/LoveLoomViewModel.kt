package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.LoveAudioEngine
import com.example.data.local.LoveLoomRepository
import com.example.data.model.GameRecord
import com.example.data.model.LoveLetter
import com.example.data.model.MemoryItem
import com.example.data.model.QuizQuestion
import com.example.data.model.UserProfile
import com.example.ui.navigation.AppScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Calendar

data class BirthdayCountdownState(
  val days: Long = 0,
  val hours: Long = 0,
  val minutes: Long = 0,
  val seconds: Long = 0,
  val isToday: Boolean = false,
  val turnsAge: Int = 0
)

class LoveLoomViewModel(application: Application) : AndroidViewModel(application) {

  private val repository = LoveLoomRepository.getRepository(application)
  val audioEngine = LoveAudioEngine(application)

  private val _currentScreen = MutableStateFlow(AppScreen.WELCOME)
  val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

  private val screenBackStack = mutableListOf<AppScreen>()

  private val _userProfile = MutableStateFlow(UserProfile())
  val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

  private val _countdown = MutableStateFlow(BirthdayCountdownState())
  val countdown: StateFlow<BirthdayCountdownState> = _countdown.asStateFlow()

  val memories: StateFlow<List<MemoryItem>> = repository.memoriesFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val letters: StateFlow<List<LoveLetter>> = repository.lettersFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val gameRecords: StateFlow<List<GameRecord>> = repository.gameRecordsFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Onboarding draft state
  val draftName = MutableStateFlow("")
  val draftDay = MutableStateFlow(11)
  val draftMonth = MutableStateFlow(12)
  val draftYear = MutableStateFlow(2008)

  init {
    viewModelScope.launch {
      try {
        val profile = repository.getUserProfile()
        _userProfile.value = profile
        draftName.value = profile.recipientName
        draftDay.value = profile.birthDay
        draftMonth.value = profile.birthMonth
        draftYear.value = profile.birthYear

        repository.seedDefaultsIfEmpty(profile.recipientName)

        // Start music if enabled and onboarded
        if (profile.isOnboarded && profile.musicEnabled && !profile.isLockEnabled) {
          audioEngine.resumeMusic()
        }

        // Initial routing
        if (!profile.isOnboarded) {
          _currentScreen.value = AppScreen.WELCOME
        } else if (profile.isLockEnabled) {
          _currentScreen.value = AppScreen.UNLOCK_PIN
        } else {
          _currentScreen.value = AppScreen.HOME
        }
      } catch (e: Exception) {
        e.printStackTrace()
        _currentScreen.value = AppScreen.WELCOME
      }
    }

    // Live countdown timer loop
    startCountdownLoop()
  }

  fun navigateTo(screen: AppScreen) {
    screenBackStack.add(_currentScreen.value)
    _currentScreen.value = screen
    audioEngine.playTapSound()
  }

  fun navigateBack(): Boolean {
    if (screenBackStack.isNotEmpty()) {
      _currentScreen.value = screenBackStack.removeAt(screenBackStack.size - 1)
      audioEngine.playTapSound()
      return true
    }
    return false
  }

  fun completeOnboarding(name: String, day: Int, month: Int, year: Int, pin: String = "", enableLock: Boolean = false) {
    viewModelScope.launch {
      val trimmedName = if (name.isBlank()) "Ayesha" else name.trim()
      val updated = _userProfile.value.copy(
        recipientName = trimmedName,
        birthDay = day,
        birthMonth = month,
        birthYear = year,
        pinCode = pin,
        isLockEnabled = enableLock && pin.isNotBlank(),
        isOnboarded = true
      )
      _userProfile.value = updated
      repository.saveUserProfile(updated)
      repository.seedDefaultsIfEmpty(trimmedName)
      repository.updateRecipientNameInLetters(trimmedName)

      if (updated.musicEnabled) {
        audioEngine.resumeMusic()
      }
      _currentScreen.value = AppScreen.HOME
      screenBackStack.clear()
    }
  }

  fun updateTheme(themeIndex: Int) {
    viewModelScope.launch {
      val updated = _userProfile.value.copy(themeIndex = themeIndex)
      _userProfile.value = updated
      repository.saveUserProfile(updated)
      audioEngine.playTapSound()
    }
  }

  fun toggleReducedMotion() {
    viewModelScope.launch {
      val updated = _userProfile.value.copy(reducedMotion = !_userProfile.value.reducedMotion)
      _userProfile.value = updated
      repository.saveUserProfile(updated)
    }
  }

  fun updateLockSettings(enabled: Boolean, pin: String) {
    viewModelScope.launch {
      val updated = _userProfile.value.copy(isLockEnabled = enabled, pinCode = pin)
      _userProfile.value = updated
      repository.saveUserProfile(updated)
    }
  }

  fun updateRecipientName(name: String) {
    viewModelScope.launch {
      val trimmed = name.trim().ifBlank { _userProfile.value.recipientName }
      val updated = _userProfile.value.copy(recipientName = trimmed)
      _userProfile.value = updated
      repository.saveUserProfile(updated)
      repository.updateRecipientNameInLetters(trimmed)
    }
  }

  fun updateProfileDetails(name: String, day: Int, month: Int, year: Int) {
    viewModelScope.launch {
      val trimmed = if (name.isBlank()) _userProfile.value.recipientName else name.trim()
      val updated = _userProfile.value.copy(
        recipientName = trimmed,
        birthDay = day,
        birthMonth = month,
        birthYear = year
      )
      _userProfile.value = updated
      repository.saveUserProfile(updated)
      repository.updateRecipientNameInLetters(trimmed)
    }
  }

  fun resetAllData() {
    viewModelScope.launch {
      val fresh = UserProfile(
        recipientName = "Ayesha",
        birthDay = 11,
        birthMonth = 12,
        birthYear = 2008,
        isOnboarded = false,
        isLockEnabled = false
      )
      _userProfile.value = fresh
      repository.saveUserProfile(fresh)
      screenBackStack.clear()
      _currentScreen.value = AppScreen.WELCOME
    }
  }

  // Countdown calculations
  private fun startCountdownLoop() {
    viewModelScope.launch {
      while (isActive) {
        calculateCountdown()
        delay(1000)
      }
    }
  }

  fun calculateCountdown() {
    val profile = _userProfile.value
    val now = Calendar.getInstance()

    val nextBday = Calendar.getInstance().apply {
      set(Calendar.MONTH, profile.birthMonth - 1)
      set(Calendar.DAY_OF_MONTH, profile.birthDay)
      set(Calendar.HOUR_OF_DAY, 0)
      set(Calendar.MINUTE, 0)
      set(Calendar.SECOND, 0)
      set(Calendar.MILLISECOND, 0)
    }

    // If birthday already passed this year, point to next year
    if (nextBday.before(now)) {
      // Check if today is birthday
      val isSameDay = now.get(Calendar.MONTH) == profile.birthMonth - 1 &&
          now.get(Calendar.DAY_OF_MONTH) == profile.birthDay
      if (!isSameDay) {
        nextBday.add(Calendar.YEAR, 1)
      }
    }

    val diffMillis = nextBday.timeInMillis - now.timeInMillis
    val isToday = now.get(Calendar.MONTH) == profile.birthMonth - 1 &&
        now.get(Calendar.DAY_OF_MONTH) == profile.birthDay

    val days = if (diffMillis > 0) diffMillis / (1000 * 60 * 60 * 24) else 0
    val hours = if (diffMillis > 0) (diffMillis / (1000 * 60 * 60)) % 24 else 0
    val minutes = if (diffMillis > 0) (diffMillis / (1000 * 60)) % 60 else 0
    val seconds = if (diffMillis > 0) (diffMillis / 1000) % 60 else 0

    val turnsAge = if (profile.birthYear in 1900..2100) {
      nextBday.get(Calendar.YEAR) - profile.birthYear
    } else 0

    _countdown.value = BirthdayCountdownState(
      days = days,
      hours = hours,
      minutes = minutes,
      seconds = seconds,
      isToday = isToday,
      turnsAge = turnsAge
    )
  }

  // Memory Gallery actions
  fun addMemory(imageUri: String, title: String, dateText: String, caption: String) {
    viewModelScope.launch {
      repository.addMemory(
        MemoryItem(
          imageUri = imageUri,
          title = if (title.isBlank()) "Sweet Memory" else title.trim(),
          dateText = if (dateText.isBlank()) "Today" else dateText.trim(),
          caption = caption.trim()
        )
      )
      audioEngine.playHeartCollectSound()
    }
  }

  fun deleteMemory(memory: MemoryItem) {
    viewModelScope.launch {
      repository.deleteMemory(memory)
      audioEngine.playTapSound()
    }
  }

  // Love Letter actions
  fun addLetter(title: String, content: String, category: String) {
    viewModelScope.launch {
      repository.addLetter(
        LoveLetter(
          title = if (title.isBlank()) "Heartfelt Note" else title.trim(),
          content = content.trim(),
          category = category,
          dateCreated = "Today",
          isCustom = true
        )
      )
      audioEngine.playHeartCollectSound()
    }
  }

  fun deleteLetter(letter: LoveLetter) {
    viewModelScope.launch {
      repository.deleteLetter(letter)
      audioEngine.playTapSound()
    }
  }

  // Quiz actions
  fun getQuestionsForCategory(category: String, onResult: (List<QuizQuestion>) -> Unit) {
    viewModelScope.launch {
      val questions = repository.getQuestionsByCategory(category)
      onResult(questions)
    }
  }

  fun addCustomQuizQuestion(
    category: String,
    question: String,
    optA: String,
    optB: String,
    optC: String,
    optD: String,
    correctIdx: Int,
    explanation: String
  ) {
    viewModelScope.launch {
      repository.addQuestion(
        QuizQuestion(
          category = category,
          questionText = question.trim(),
          optionA = optA.trim(),
          optionB = optB.trim(),
          optionC = optC.trim(),
          optionD = optD.trim(),
          correctOptionIndex = correctIdx,
          explanation = explanation.trim(),
          isCustom = true
        )
      )
      audioEngine.playHeartCollectSound()
    }
  }

  // Game recording
  fun recordGameScore(gameKey: String, gameTitle: String, score: Int, timeSec: Int = 0) {
    viewModelScope.launch {
      repository.recordGameResult(gameKey, gameTitle, score, timeSec)
    }
  }

  override fun onCleared() {
    super.onCleared()
    audioEngine.release()
  }
}
