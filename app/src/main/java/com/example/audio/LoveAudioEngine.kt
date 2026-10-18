package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

data class AudioTrackInfo(
  val id: Int,
  val title: String,
  val album: String,
  val artist: String = "Atif Aslam",
  val mood: String,
  val lyricsUrdu: String,
  val lyricsRoman: String,
  val durationSeconds: Int,
  val releaseYear: String
)

class LoveAudioEngine(private val context: Context) {

  private val scope = CoroutineScope(Dispatchers.Default)
  private var musicJob: Job? = null
  private var audioTrack: AudioTrack? = null
  private var fxTrack: AudioTrack? = null
  private val audioLock = Mutex()
  private val fxLock = Mutex()

  private val _isPlaying = MutableStateFlow(false)
  val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

  private val _currentTrackIndex = MutableStateFlow(0)
  val currentTrackIndex: StateFlow<Int> = _currentTrackIndex.asStateFlow()

  private val _volume = MutableStateFlow(0.85f)
  val volume: StateFlow<Float> = _volume.asStateFlow()

  private val _isMusicMuted = MutableStateFlow(false)
  val isMusicMuted: StateFlow<Boolean> = _isMusicMuted.asStateFlow()

  private val _isSoundFxMuted = MutableStateFlow(false)
  val isSoundFxMuted: StateFlow<Boolean> = _isSoundFxMuted.asStateFlow()

  private val _trackProgressSeconds = MutableStateFlow(0)
  val trackProgressSeconds: StateFlow<Int> = _trackProgressSeconds.asStateFlow()

  val tracks = listOf(
    AudioTrackInfo(
      id = 0,
      title = "Kuch Is Tarah",
      album = "Doorie",
      mood = "Soulful Romantic Ballad",
      lyricsUrdu = "کچھ اس طرح تیری پلکیں میری پلکوں سے ملا دے\nآنسو تیرے سارے میری پلکوں پہ سجا دے",
      lyricsRoman = "Kuch is tarah teri palkein meri palkon se mila de, aansoo tere saare meri palkon pe sajaa de...",
      durationSeconds = 314,
      releaseYear = "2006"
    ),
    AudioTrackInfo(
      id = 1,
      title = "Doorie",
      album = "Doorie",
      mood = "Iconic Romantic Anthem",
      lyricsUrdu = "دوری صحیح جائے نہ، خاموشی صحیح جائے نہ\nاک نشہ ہے، اک خمار ہے، تیرا ہی پیار ہے",
      lyricsRoman = "Doorie sahi jaaye na, khamoshi sahi jaaye na... ik nasha hai, ik khumaar hai...",
      durationSeconds = 295,
      releaseYear = "2006"
    ),
    AudioTrackInfo(
      id = 2,
      title = "Aadat (Acoustic)",
      album = "Jal Pari",
      mood = "Timeless Pakistani Pop Classic",
      lyricsUrdu = "جدا ہو کے بھی تو مجھ میں کہیں باقی ہے\nپلکوں پہ بن کے آنسو تو چلی آتی ہے",
      lyricsRoman = "Juda hoke bhi tu mujhmein kahin baaqi hai, palkon pe ban ke aansoo tu chali aati hai...",
      durationSeconds = 268,
      releaseYear = "2004"
    ),
    AudioTrackInfo(
      id = 3,
      title = "Mahi Ve",
      album = "Doorie",
      mood = "Sweet Punjabi-Urdu Melody",
      lyricsUrdu = "ماہی وے محبتاں سچیاں نے\nتیرے باجھوں دل نہیں لگدا، ماہی وے",
      lyricsRoman = "Mahi ve mohabbatan sachiyan ne, tere baajhon dil nahi lagda, mahi ve...",
      durationSeconds = 242,
      releaseYear = "2006"
    ),
    AudioTrackInfo(
      id = 4,
      title = "Kadi Te Has Bol Ve",
      album = "Velo Sound Station",
      mood = "Romantic Folk-Pop Melody",
      lyricsUrdu = "کدی تے ہس بول وے، نہ جند رول وے\nتو آ جا دل جانیاں وے، تیرے نال پیار ہے",
      lyricsRoman = "Kadi te has bol ve, na jind rol ve, tu aaja dil jaaniyan ve...",
      durationSeconds = 230,
      releaseYear = "2020"
    ),
    AudioTrackInfo(
      id = 5,
      title = "Yakeen",
      album = "Doorie",
      mood = "Emotional Acoustic Ballad",
      lyricsUrdu = "بھلا دینا مجھے آساں نہیں\nیقین تو کر میرے ہمسفر، تیرے بنا ادھورا ہوں",
      lyricsRoman = "Bhula dena mujhe aasan nahi, yakeen toh kar mere humsafar...",
      durationSeconds = 240,
      releaseYear = "2006"
    ),
    AudioTrackInfo(
      id = 6,
      title = "Ehsaas",
      album = "Jal Pari",
      mood = "Gentle Romantic Serenade",
      lyricsUrdu = "یہ احساس ہے کیا، جو دل کو چھو جائے\nتیری یادوں میں کھو کے، راتیں گزر جائیں",
      lyricsRoman = "Yeh ehsaas hai kya, jo dil ko chhoo jaaye, teri yaadon mein kho ke...",
      durationSeconds = 224,
      releaseYear = "2004"
    )
  )

  private val sampleRate = 22050
  private val vibrator: Vibrator? = try {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
      vibratorManager?.defaultVibrator
    } else {
      @Suppress("DEPRECATION")
      context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }
  } catch (_: Throwable) {
    null
  }

  private fun getOrCreateAudioTrack(): AudioTrack? {
    if (audioTrack != null && audioTrack?.state == AudioTrack.STATE_INITIALIZED) {
      return audioTrack
    }
    return try {
      val minBufferSize = AudioTrack.getMinBufferSize(
        sampleRate,
        AudioFormat.CHANNEL_OUT_MONO,
        AudioFormat.ENCODING_PCM_16BIT
      )
      if (minBufferSize <= 0) return null

      val bufferSize = maxOf(minBufferSize, sampleRate * 2)

      val track = AudioTrack.Builder()
        .setAudioAttributes(
          AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()
        )
        .setAudioFormat(
          AudioFormat.Builder()
            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
            .setSampleRate(sampleRate)
            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
            .build()
        )
        .setBufferSizeInBytes(bufferSize)
        .setTransferMode(AudioTrack.MODE_STREAM)
        .build()

      if (track.state == AudioTrack.STATE_INITIALIZED) {
        audioTrack = track
        track
      } else {
        track.release()
        null
      }
    } catch (_: Throwable) {
      null
    }
  }

  private fun getOrCreateFxTrack(): AudioTrack? {
    if (fxTrack != null && fxTrack?.state == AudioTrack.STATE_INITIALIZED) {
      return fxTrack
    }
    return try {
      val minBufferSize = AudioTrack.getMinBufferSize(
        sampleRate,
        AudioFormat.CHANNEL_OUT_MONO,
        AudioFormat.ENCODING_PCM_16BIT
      )
      if (minBufferSize <= 0) return null

      val track = AudioTrack.Builder()
        .setAudioAttributes(
          AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        )
        .setAudioFormat(
          AudioFormat.Builder()
            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
            .setSampleRate(sampleRate)
            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
            .build()
        )
        .setBufferSizeInBytes(minBufferSize * 2)
        .setTransferMode(AudioTrack.MODE_STREAM)
        .build()

      if (track.state == AudioTrack.STATE_INITIALIZED) {
        fxTrack = track
        track.play()
        track
      } else {
        track.release()
        null
      }
    } catch (_: Throwable) {
      null
    }
  }

  fun playTrack(index: Int) {
    val newIdx = (index + tracks.size) % tracks.size
    _currentTrackIndex.value = newIdx
    _isPlaying.value = true
    _trackProgressSeconds.value = 0
    startSynthesisLoop()
  }

  fun togglePlayPause() {
    if (_isPlaying.value) {
      pauseMusic()
    } else {
      resumeMusic()
    }
  }

  fun resumeMusic() {
    _isPlaying.value = true
    startSynthesisLoop()
  }

  fun pauseMusic() {
    _isPlaying.value = false
    musicJob?.cancel()
    try {
      if (audioTrack?.state == AudioTrack.STATE_INITIALIZED) {
        audioTrack?.pause()
      }
    } catch (_: Throwable) {}
  }

  fun nextTrack() {
    playTrack(_currentTrackIndex.value + 1)
  }

  fun previousTrack() {
    playTrack(_currentTrackIndex.value - 1)
  }

  fun setVolume(vol: Float) {
    val clamped = vol.coerceIn(0f, 1f)
    _volume.value = clamped
    try {
      if (audioTrack?.state == AudioTrack.STATE_INITIALIZED) {
        audioTrack?.setVolume(if (_isMusicMuted.value) 0f else clamped)
      }
    } catch (_: Throwable) {}
  }

  fun toggleMusicMute() {
    _isMusicMuted.value = !_isMusicMuted.value
    setVolume(_volume.value)
  }

  fun toggleSoundFxMute() {
    _isSoundFxMuted.value = !_isSoundFxMuted.value
  }

  fun seekTo(seconds: Int) {
    val currentTrack = tracks[_currentTrackIndex.value]
    _trackProgressSeconds.value = seconds.coerceIn(0, currentTrack.durationSeconds)
  }

  private fun startSynthesisLoop() {
    musicJob?.cancel()
    musicJob = scope.launch {
      val track = getOrCreateAudioTrack() ?: run {
        while (isActive && _isPlaying.value) {
          delay(1000)
          val tIdx = _currentTrackIndex.value
          _trackProgressSeconds.value = (_trackProgressSeconds.value + 1) % tracks[tIdx].durationSeconds
        }
        return@launch
      }

      try {
        if (track.playState != AudioTrack.PLAYSTATE_PLAYING) {
          track.play()
        }
      } catch (_: Throwable) {}

      // Melodies accurately matching authentic Pakistani Atif Aslam romantic hits:
      // Note frequencies (Hz):
      // C4=261.6, D4=293.7, E4=329.6, F4=349.2, F#4=369.9, G4=392.0, A4=440.0, B4=493.9, C5=523.3, D5=587.3, E5=659.3, F#5=739.9, G5=784.0
      val pakistaniAtifMelodies: List<List<Pair<Double, Double>>> = listOf(
        // Track 0: "Kuch Is Tarah" (Doorie album)
        // "Kuch is tarah teri palkein meri palkon se mila de, aansoo tere saare meri palkon pe sajaa de"
        listOf(
          Pair(392.00, 0.45), Pair(440.00, 0.40), Pair(493.88, 0.45), Pair(523.25, 0.55),
          Pair(493.88, 0.40), Pair(440.00, 0.40), Pair(392.00, 0.65),
          Pair(329.63, 0.40), Pair(369.99, 0.40), Pair(392.00, 0.45), Pair(440.00, 0.50),
          Pair(392.00, 0.40), Pair(369.99, 0.40), Pair(329.63, 0.70),
          Pair(392.00, 0.45), Pair(493.88, 0.45), Pair(587.33, 0.50), Pair(523.25, 0.45),
          Pair(493.88, 0.40), Pair(440.00, 0.60),
          Pair(440.00, 0.40), Pair(392.00, 0.40), Pair(369.99, 0.45), Pair(329.63, 0.40),
          Pair(293.66, 0.40), Pair(329.63, 0.75)
        ),
        // Track 1: "Doorie" (Doorie album title track)
        // "Doorie sahi jaaye na, khamoshi sahi jaaye na, haan ik nasha hai, ik khumaar hai"
        listOf(
          Pair(329.63, 0.55), Pair(392.00, 0.45), Pair(493.88, 0.55), Pair(440.00, 0.45),
          Pair(392.00, 0.40), Pair(369.99, 0.40), Pair(329.63, 0.70),
          Pair(329.63, 0.55), Pair(392.00, 0.45), Pair(493.88, 0.55), Pair(440.00, 0.45),
          Pair(392.00, 0.40), Pair(369.99, 0.40), Pair(329.63, 0.70),
          Pair(493.88, 0.40), Pair(523.25, 0.40), Pair(587.33, 0.50), Pair(523.25, 0.40),
          Pair(493.88, 0.40), Pair(440.00, 0.40), Pair(392.00, 0.45), Pair(440.00, 0.45),
          Pair(493.88, 0.75)
        ),
        // Track 2: "Aadat (Acoustic)" (Jal Pari album)
        // "Juda hoke bhi tu mujhmein kahin baaqi hai, palkon pe ban ke aansoo tu chali aati hai"
        listOf(
          Pair(369.99, 0.45), Pair(392.00, 0.40), Pair(440.00, 0.55), Pair(392.00, 0.40),
          Pair(369.99, 0.40), Pair(329.63, 0.45), Pair(293.66, 0.70),
          Pair(329.63, 0.40), Pair(369.99, 0.40), Pair(392.00, 0.50), Pair(369.99, 0.40),
          Pair(329.63, 0.40), Pair(293.66, 0.40), Pair(277.18, 0.40), Pair(293.66, 0.70),
          Pair(293.66, 0.40), Pair(369.99, 0.45), Pair(440.00, 0.55), Pair(392.00, 0.45),
          Pair(369.99, 0.40), Pair(329.63, 0.40), Pair(369.99, 0.45), Pair(293.66, 0.75)
        ),
        // Track 3: "Mahi Ve" (Doorie album)
        // "Mahi ve mohabbatan sachiyan ne, tere baajhon dil nahi lagda"
        listOf(
          Pair(392.00, 0.45), Pair(523.25, 0.50), Pair(493.88, 0.40), Pair(440.00, 0.40),
          Pair(392.00, 0.50), Pair(329.63, 0.40), Pair(349.23, 0.40), Pair(392.00, 0.45),
          Pair(440.00, 0.45), Pair(392.00, 0.65),
          Pair(349.23, 0.40), Pair(392.00, 0.40), Pair(440.00, 0.45), Pair(493.88, 0.45),
          Pair(440.00, 0.40), Pair(392.00, 0.40), Pair(349.23, 0.40), Pair(329.63, 0.70)
        ),
        // Track 4: "Kadi Te Has Bol Ve" (Velo Sound Station)
        // "Kadi te has bol ve, na jind rol ve, tu aaja dil jaaniyan ve"
        listOf(
          Pair(293.66, 0.35), Pair(329.63, 0.35), Pair(392.00, 0.45), Pair(392.00, 0.45),
          Pair(440.00, 0.45), Pair(493.88, 0.50), Pair(440.00, 0.40), Pair(392.00, 0.45),
          Pair(329.63, 0.40), Pair(392.00, 0.65),
          Pair(493.88, 0.40), Pair(523.25, 0.45), Pair(493.88, 0.40), Pair(440.00, 0.40),
          Pair(392.00, 0.45), Pair(440.00, 0.45), Pair(392.00, 0.70)
        ),
        // Track 5: "Yakeen" (Doorie album)
        // "Bhula dena mujhe aasan nahi, yakeen toh kar mere humsafar"
        listOf(
          Pair(329.63, 0.45), Pair(369.99, 0.40), Pair(392.00, 0.45), Pair(369.99, 0.40),
          Pair(329.63, 0.40), Pair(293.66, 0.40), Pair(329.63, 0.50), Pair(392.00, 0.45),
          Pair(369.99, 0.40), Pair(329.63, 0.70),
          Pair(392.00, 0.40), Pair(440.00, 0.40), Pair(493.88, 0.50), Pair(440.00, 0.40),
          Pair(392.00, 0.40), Pair(369.99, 0.40), Pair(392.00, 0.45), Pair(329.63, 0.75)
        ),
        // Track 6: "Ehsaas" (Jal Pari album)
        // "Yeh ehsaas hai kya, jo dil ko chhoo jaaye"
        listOf(
          Pair(261.63, 0.40), Pair(329.63, 0.45), Pair(392.00, 0.50), Pair(440.00, 0.45),
          Pair(392.00, 0.40), Pair(329.63, 0.40), Pair(293.66, 0.65),
          Pair(293.66, 0.40), Pair(349.23, 0.40), Pair(440.00, 0.50), Pair(493.88, 0.45),
          Pair(440.00, 0.40), Pair(349.23, 0.40), Pair(329.63, 0.70)
        )
      )

      var step = 0

      while (isActive && _isPlaying.value) {
        val trackIdx = _currentTrackIndex.value
        val scoreList = pakistaniAtifMelodies[trackIdx % pakistaniAtifMelodies.size]
        val note = scoreList[step % scoreList.size]
        val freq = note.first
        val durationSec = note.second
        val numSamples = (sampleRate * durationSec).toInt()
        val buffer = ShortArray(numSamples)

        val masterVol = if (_isMusicMuted.value) 0.0 else _volume.value.toDouble() * 0.42

        for (i in 0 until numSamples) {
          val t = i.toDouble() / sampleRate
          // Rich acoustic bell/guitar envelope: fast pluck, resonant warm decay
          val envelope = exp(-2.8 * t / durationSec)
          // Soft vibrato for authentic romantic singing touch
          val vibrato = 1.0 + 0.008 * sin(2.0 * PI * 5.0 * t)
          val actualFreq = freq * vibrato

          // Fundamental + warm harmonics (guitar/harmonium timbre)
          val wave1 = sin(2.0 * PI * actualFreq * t)
          val wave2 = 0.38 * sin(4.0 * PI * actualFreq * t)
          val wave3 = 0.16 * sin(6.0 * PI * actualFreq * t)
          val wave4 = 0.07 * sin(8.0 * PI * actualFreq * t)

          val sample = (wave1 + wave2 + wave3 + wave4) * envelope * masterVol
          buffer[i] = (sample * 32767.0).toInt().coerceIn(-32767, 32767).toShort()
        }

        try {
          audioLock.withLock {
            if (audioTrack?.state == AudioTrack.STATE_INITIALIZED) {
              audioTrack?.write(buffer, 0, buffer.size, AudioTrack.WRITE_NON_BLOCKING)
            }
          }
        } catch (_: Throwable) {}

        step++
        _trackProgressSeconds.value = (_trackProgressSeconds.value + 1) % tracks[trackIdx].durationSeconds
        // Delay exact duration of note to keep audio stream smooth and synchronized
        delay((durationSec * 1000).toLong().coerceAtLeast(50L))
      }
    }
  }

  // Sound Effects with optional tactile haptics
  fun playHeartCollectSound() {
    vibrate(30)
    if (_isSoundFxMuted.value) return
    playTransientTone(freq = 880.0, durationMs = 120, decay = 8.0)
  }

  fun playBalloonPopSound() {
    vibrate(50)
    if (_isSoundFxMuted.value) return
    playNoiseBurst(durationMs = 90)
  }

  fun playCandleBlowSound() {
    vibrate(80)
    if (_isSoundFxMuted.value) return
    playWhooshSound(durationMs = 280)
  }

  fun playCakeCutSound() {
    vibrate(40)
    if (_isSoundFxMuted.value) return
    playSparkleChime()
  }

  fun playCheerSound() {
    vibrate(100)
    if (_isSoundFxMuted.value) return
    scope.launch {
      playTransientTone(523.25, 90, 5.0)
      delay(70)
      playTransientTone(659.25, 90, 5.0)
      delay(70)
      playTransientTone(783.99, 220, 4.0)
    }
  }

  fun playUnwrapSound() {
    vibrate(35)
    if (_isSoundFxMuted.value) return
    playTransientTone(freq = 1046.5, durationMs = 140, decay = 10.0)
  }

  fun playTapSound() {
    vibrate(15)
    if (_isSoundFxMuted.value) return
    playTransientTone(freq = 659.25, durationMs = 60, decay = 18.0)
  }

  private fun vibrate(durationMs: Long) {
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        vibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
      } else {
        @Suppress("DEPRECATION")
        vibrator?.vibrate(durationMs)
      }
    } catch (_: Throwable) {}
  }

  private fun playTransientTone(freq: Double, durationMs: Int, decay: Double) {
    scope.launch {
      try {
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        if (numSamples <= 0) return@launch
        val buffer = ShortArray(numSamples)
        val fxVol = 0.45 * _volume.value

        for (i in 0 until numSamples) {
          val t = i.toDouble() / sampleRate
          val env = exp(-decay * (t / (durationMs / 1000.0)))
          val wave = sin(2.0 * PI * freq * t) * env * fxVol
          buffer[i] = (wave * 32767.0).toInt().coerceIn(-32767, 32767).toShort()
        }

        fxLock.withLock {
          val track = getOrCreateFxTrack()
          if (track?.state == AudioTrack.STATE_INITIALIZED) {
            track.write(buffer, 0, buffer.size, AudioTrack.WRITE_NON_BLOCKING)
          }
        }
      } catch (_: Throwable) {}
    }
  }

  private fun playNoiseBurst(durationMs: Int) {
    scope.launch {
      try {
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        if (numSamples <= 0) return@launch
        val buffer = ShortArray(numSamples)
        val fxVol = 0.35 * _volume.value

        for (i in 0 until numSamples) {
          val t = i.toDouble() / sampleRate
          val env = exp(-12.0 * (t / (durationMs / 1000.0)))
          val noise = (Math.random() * 2.0 - 1.0) * env * fxVol
          buffer[i] = (noise * 32767.0).toInt().coerceIn(-32767, 32767).toShort()
        }

        fxLock.withLock {
          val track = getOrCreateFxTrack()
          if (track?.state == AudioTrack.STATE_INITIALIZED) {
            track.write(buffer, 0, buffer.size, AudioTrack.WRITE_NON_BLOCKING)
          }
        }
      } catch (_: Throwable) {}
    }
  }

  private fun playWhooshSound(durationMs: Int) {
    scope.launch {
      try {
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        if (numSamples <= 0) return@launch
        val buffer = ShortArray(numSamples)
        val fxVol = 0.30 * _volume.value

        for (i in 0 until numSamples) {
          val t = i.toDouble() / sampleRate
          val env = sin(PI * (t / (durationMs / 1000.0)))
          val noise = (Math.random() * 2.0 - 1.0) * env * fxVol
          buffer[i] = (noise * 32767.0).toInt().coerceIn(-32767, 32767).toShort()
        }

        fxLock.withLock {
          val track = getOrCreateFxTrack()
          if (track?.state == AudioTrack.STATE_INITIALIZED) {
            track.write(buffer, 0, buffer.size, AudioTrack.WRITE_NON_BLOCKING)
          }
        }
      } catch (_: Throwable) {}
    }
  }

  private fun playSparkleChime() {
    scope.launch {
      playTransientTone(1174.66, 100, 8.0)
      delay(50)
      playTransientTone(1318.51, 120, 7.0)
      delay(50)
      playTransientTone(1567.98, 180, 6.0)
    }
  }

  fun release() {
    musicJob?.cancel()
    try {
      if (audioTrack?.state == AudioTrack.STATE_INITIALIZED) {
        audioTrack?.pause()
        audioTrack?.flush()
        audioTrack?.stop()
        audioTrack?.release()
      }
    } catch (_: Throwable) {}
    audioTrack = null

    try {
      if (fxTrack?.state == AudioTrack.STATE_INITIALIZED) {
        fxTrack?.pause()
        fxTrack?.flush()
        fxTrack?.stop()
        fxTrack?.release()
      }
    } catch (_: Throwable) {}
    fxTrack = null
  }
}
