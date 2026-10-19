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
  val releaseYear: String,
  val youtubeVideoId: String = ""
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
      releaseYear = "2006",
      youtubeVideoId = "dQm7vX18K5M"
    ),
    AudioTrackInfo(
      id = 1,
      title = "Doorie",
      album = "Doorie",
      mood = "Iconic Romantic Anthem",
      lyricsUrdu = "دوری صحیح جائے نہ، خاموشی صحیح جائے نہ\nاک نشہ ہے، اک خمار ہے، تیرا ہی پیار ہے",
      lyricsRoman = "Doorie sahi jaaye na, khamoshi sahi jaaye na... ik nasha hai, ik khumaar hai...",
      durationSeconds = 295,
      releaseYear = "2006",
      youtubeVideoId = "8XfT55B1y14"
    ),
    AudioTrackInfo(
      id = 2,
      title = "Aadat (Acoustic)",
      album = "Jal Pari",
      mood = "Timeless Pakistani Pop Classic",
      lyricsUrdu = "جدا ہو کے بھی تو مجھ میں کہیں باقی ہے\nپلکوں پہ بن کے آنسو تو چلی آتی ہے",
      lyricsRoman = "Juda hoke bhi tu mujhmein kahin baaqi hai, palkon pe ban ke aansoo tu chali aati hai...",
      durationSeconds = 268,
      releaseYear = "2004",
      youtubeVideoId = "i6wLpT14_rQ"
    ),
    AudioTrackInfo(
      id = 3,
      title = "Mahi Ve",
      album = "Doorie",
      mood = "Sweet Punjabi-Urdu Melody",
      lyricsUrdu = "ماہی وے محبتاں سچیاں نے\nتیرے باجھوں دل نہیں لگدا، ماہی وے",
      lyricsRoman = "Mahi ve mohabbatan sachiyan ne, tere baajhon dil nahi lagda, mahi ve...",
      durationSeconds = 242,
      releaseYear = "2006",
      youtubeVideoId = "v2WkY4d_w0M"
    ),
    AudioTrackInfo(
      id = 4,
      title = "Kadi Te Has Bol Ve",
      album = "Velo Sound Station",
      mood = "Romantic Folk-Pop Melody",
      lyricsUrdu = "کدی تے ہس بول وے، نہ جند رول وے\nتو آ جا دل جانیاں وے، تیرے نال پیار ہے",
      lyricsRoman = "Kadi te has bol ve, na jind rol ve, tu aaja دل jaaniyan ve...",
      durationSeconds = 230,
      releaseYear = "2020",
      youtubeVideoId = "5J6e792k7s4"
    ),
    AudioTrackInfo(
      id = 5,
      title = "Yakeen",
      album = "Doorie",
      mood = "Emotional Acoustic Ballad",
      lyricsUrdu = "بھلا دینا مجھے آساں نہیں\nیقین تو کر میرے ہمسفر، تیرے بنا ادھورا ہوں",
      lyricsRoman = "Bhula dena mujhe aasan nahi, yakeen toh kar mere humsafar...",
      durationSeconds = 240,
      releaseYear = "2006",
      youtubeVideoId = "e2u7uY8k3r8"
    ),
    AudioTrackInfo(
      id = 6,
      title = "Ehsaas",
      album = "Jal Pari",
      mood = "Gentle Romantic Serenade",
      lyricsUrdu = "یہ احساس ہے کیا، جو دل کو چھو جائے\nتیری یادوں میں کھو کے، راتیں گزر جائیں",
      lyricsRoman = "Yeh ehsaas hai kya, jo dil ko chhoo jaaye, teri yaadon mein kho ke...",
      durationSeconds = 224,
      releaseYear = "2004",
      youtubeVideoId = "Hw9N_z8LgB0"
    ),
    AudioTrackInfo(
      id = 7,
      title = "Pehli Nazar Mein",
      album = "Race",
      mood = "All-Time Romantic Mega Hit",
      lyricsUrdu = "پہلی نظر میں کیسا جادو کر دیا\nتیرا بن بیٹھا ہے میرا جیا",
      lyricsRoman = "Pehli nazar mein kaisa jaadu kar diya, tera ban baitha hai mera jiya...",
      durationSeconds = 312,
      releaseYear = "2008",
      youtubeVideoId = "BadBAMnpXsc"
    ),
    AudioTrackInfo(
      id = 8,
      title = "Tera Hone Laga Hoon",
      album = "Ajab Prem Ki Ghazab Kahani",
      mood = "Magical Romantic Duet",
      lyricsUrdu = "ہوا یہ کیا اثر، تیرا ہونے لگا ہوں\nکھونے لگا ہوں، تیرے پیار میں",
      lyricsRoman = "Shining in the setting sun like a pearl upon the ocean... tera hone laga hoon...",
      durationSeconds = 300,
      releaseYear = "2009",
      youtubeVideoId = "r_8ydgGph00"
    ),
    AudioTrackInfo(
      id = 9,
      title = "Dil Diyan Gallan",
      album = "Tiger Zinda Hai",
      mood = "Soulful Acoustic Romance",
      lyricsUrdu = "کچی ڈوریاں سے مَیں نوں توں باندھ لے\nدل دیاں گلاں، کراں گے نال نال بہہ کے",
      lyricsRoman = "Dil diyan gallan, karaange naal naal beh ke, akh naale akh nu mila ke...",
      durationSeconds = 260,
      releaseYear = "2017",
      youtubeVideoId = "mevO4I0f5lg"
    ),
    AudioTrackInfo(
      id = 10,
      title = "Jeena Jeena",
      album = "Badlapur",
      mood = "Deep Romantic Devotion",
      lyricsUrdu = "دہلیز پہ میرے دل کی جو رکھے ہیں تو نے قدم\nسیکھا میں نے جینا جینا کیسے جینا",
      lyricsRoman = "Dehleez pe mere dil ki jo rakhe hain tune qadam... seekha maine jeena jeena...",
      durationSeconds = 230,
      releaseYear = "2015",
      youtubeVideoId = "zfdK-L6O954"
    ),
    AudioTrackInfo(
      id = 11,
      title = "Tu Jaane Na",
      album = "Ajab Prem Ki Ghazab Kahani",
      mood = "Heartfelt Love Melody",
      lyricsUrdu = "کیسے بتائیں کیوں تجھ کو چاہیں، یارا بتا نہ پائیں\nتو جانے نہ، تو جانے نہ",
      lyricsRoman = "Kaise batayein kyun tujhko chaahein, yaara bata na paayein...",
      durationSeconds = 341,
      releaseYear = "2009",
      youtubeVideoId = "X3b37X-xL_Q"
    ),
    AudioTrackInfo(
      id = 12,
      title = "Tere Sang Yaara",
      album = "Rustom",
      mood = "Pure Romantic Serenade",
      lyricsUrdu = "تیرے سنگ یارا، خوش رنگ بہارا\nتو رات کالی، میں چاند ستارہ",
      lyricsRoman = "Tere sang yaara, khush rang bahaara, tu raat kaali main chaand sitaara...",
      durationSeconds = 290,
      releaseYear = "2016",
      youtubeVideoId = "f6vYpXf_7Xk"
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
      while (isActive && _isPlaying.value) {
        delay(1000)
        val trackIdx = _currentTrackIndex.value
        val track = tracks.getOrNull(trackIdx)
        val duration = track?.durationSeconds ?: 200
        val nextSec = _trackProgressSeconds.value + 1
        if (nextSec >= duration) {
          _trackProgressSeconds.value = 0
          nextTrack()
        } else {
          _trackProgressSeconds.value = nextSec
        }
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
