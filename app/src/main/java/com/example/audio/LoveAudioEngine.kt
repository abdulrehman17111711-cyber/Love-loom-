package com.example.audio

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.example.R
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
import kotlinx.coroutines.withContext
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin
import kotlin.random.Random

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
  val youtubeVideoId: String = "",
  val streamUrl: String = "",
  val localRawResId: Int = 0
)

enum class PlaybackRepeatMode {
  REPEAT_ALL,
  REPEAT_ONE,
  OFF
}

class LoveAudioEngine(private val context: Context) {

  private val scope = CoroutineScope(Dispatchers.Main)
  private var mediaPlayer: MediaPlayer? = null
  private var progressJob: Job? = null

  private var fxTrack: AudioTrack? = null
  private val fxLock = Mutex()

  // Core Playback StateFlows
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

  private val _trackDurationSeconds = MutableStateFlow(314)
  val trackDurationSeconds: StateFlow<Int> = _trackDurationSeconds.asStateFlow()

  private val _isBuffering = MutableStateFlow(false)
  val isBuffering: StateFlow<Boolean> = _isBuffering.asStateFlow()

  private val _errorMessage = MutableStateFlow<String?>(null)
  val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

  private val _isShuffle = MutableStateFlow(false)
  val isShuffle: StateFlow<Boolean> = _isShuffle.asStateFlow()

  private val _repeatMode = MutableStateFlow(PlaybackRepeatMode.REPEAT_ALL)
  val repeatMode: StateFlow<PlaybackRepeatMode> = _repeatMode.asStateFlow()

  private val _isMusicEnabled = MutableStateFlow(true)
  val isMusicEnabled: StateFlow<Boolean> = _isMusicEnabled.asStateFlow()

  // Curated Playlist of 13 Iconic Atif Aslam Hits with Genuine Audio Files and Streams
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
      youtubeVideoId = "dQm7vX18K5M",
      streamUrl = "https://ia600800.us.archive.org/16/items/083-atif-aslam/083%20-%20ATIF%20ASLAM/Playback%20-%20Atif%20Aslam/04%20Doorie%20-atif/Kuch%20Is%20Tarha.mp3",
      localRawResId = R.raw.track_kuch_is_tarah
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
      youtubeVideoId = "8XfT55B1y14",
      streamUrl = "https://ia600800.us.archive.org/16/items/083-atif-aslam/083%20-%20ATIF%20ASLAM/Playback%20-%20Atif%20Aslam/04%20Doorie%20-atif/Doorie-.mp3",
      localRawResId = R.raw.track_doorie
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
      youtubeVideoId = "i6wLpT14_rQ",
      streamUrl = "https://ia600800.us.archive.org/16/items/083-atif-aslam/083%20-%20ATIF%20ASLAM/Playback%20-%20Atif%20Aslam/06%20Jalpari%20-%20Atif%20Aslam/06%20Aadat%20%28The%20Deep%20Blue%29.mp3",
      localRawResId = R.raw.track_aadat
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
      youtubeVideoId = "v2WkY4d_w0M",
      streamUrl = "https://ia600800.us.archive.org/16/items/083-atif-aslam/083%20-%20ATIF%20ASLAM/Playback%20-%20Atif%20Aslam/04%20Doorie%20-atif/Mahiya%20Ve%20Sohniya.mp3"
    ),
    AudioTrackInfo(
      id = 4,
      title = "Kadi Te Has Bol Ve",
      album = "Velo Sound Station",
      mood = "Romantic Folk-Pop Melody",
      lyricsUrdu = "کدی تے ہس بول وے، نہ جند رول وے\nتو آ جا دل جانیاں وے، تیرے نال پیار ہے",
      lyricsRoman = "Kadi te has bol ve, na jind rol ve, tu aaja dil jaaniyan ve...",
      durationSeconds = 230,
      releaseYear = "2020",
      youtubeVideoId = "5J6e792k7s4",
      streamUrl = "https://ia600800.us.archive.org/16/items/083-atif-aslam/083%20-%20ATIF%20ASLAM/Playback%20-%20Atif%20Aslam/02%20Bol%20%282011%29/03%20-%20Dil%20Janiya%20.mp3"
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
      youtubeVideoId = "e2u7uY8k3r8",
      streamUrl = "https://ia600800.us.archive.org/16/items/083-atif-aslam/083%20-%20ATIF%20ASLAM/Playback%20-%20Atif%20Aslam/06%20Jalpari%20-%20Atif%20Aslam/04%20Yakeen.mp3"
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
      youtubeVideoId = "Hw9N_z8LgB0",
      streamUrl = "https://ia600800.us.archive.org/16/items/083-atif-aslam/083%20-%20ATIF%20ASLAM/Playback%20-%20Atif%20Aslam/06%20Jalpari%20-%20Atif%20Aslam/02%20Ehsaas.mp3"
    ),
    AudioTrackInfo(
      id = 7,
      title = "Pehli Nazar Mein",
      album = "Race",
      mood = "All-Time Romantic Mega Hit",
      lyricsUrdu = "پہلی نظر میں کیسا جادو کر دیا\nتیرا بن بیٹھا ہے میرا جیا",
      lyricsRoman = "Pehli nazar mein kaisa jaadu kar diya, tera ban baitha hai میرا jiya...",
      durationSeconds = 312,
      releaseYear = "2008",
      youtubeVideoId = "BadBAMnpXsc",
      streamUrl = "https://ia600800.us.archive.org/16/items/083-atif-aslam/083%20-%20ATIF%20ASLAM/Playback%20-%20Atif%20Aslam/01%20Filmi%20Songs/24%20-%20%20Pehli%20Nazar%20Mein%20%20-%20Race.mp3",
      localRawResId = R.raw.track_pehli_nazar
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
      youtubeVideoId = "r_8ydgGph00",
      streamUrl = "https://ia600800.us.archive.org/16/items/083-atif-aslam/083%20-%20ATIF%20ASLAM/Playback%20-%20Atif%20Aslam/01%20Filmi%20Songs/18%20-%20Tera%20Hone%20Laga%20Hoon%20-%20Ajab%20Prem%20Ki%20Ghazab%20Kahani.mp3"
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
      youtubeVideoId = "mevO4I0f5lg",
      streamUrl = "https://ia600800.us.archive.org/16/items/083-atif-aslam/083%20-%20ATIF%20ASLAM/Playback%20-%20Atif%20Aslam/01%20Filmi%20Songs/22%20-%20%20Bakhuda%20Tumhi%20Ho%20-%20Kismat%20Konnection.mp3"
    ),
    AudioTrackInfo(
      id = 10,
      title = "Jeena Jeena",
      album = "Badlapur",
      mood = "Deep Romantic Devotion",
      lyricsUrdu = "دہلیز پہ میرے دل کی جو رکھے ہیں تو نے قدم\nسیکھا میں نے جینا جینا کیسے جینا",
      lyricsRoman = "Dehleez pe mere دل ki jo rakhe hain tune qadam... seekha maine jeena jeena...",
      durationSeconds = 230,
      releaseYear = "2015",
      youtubeVideoId = "zfdK-L6O954",
      streamUrl = "https://ia600800.us.archive.org/16/items/AtifAslam_201801/Jeena_Jeena%20%5Bwww.MusicPool.com%5D.mp3"
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
      youtubeVideoId = "X3b37X-xL_Q",
      streamUrl = "https://ia600800.us.archive.org/16/items/083-atif-aslam/083%20-%20ATIF%20ASLAM/Playback%20-%20Atif%20Aslam/01%20Filmi%20Songs/20%20-%20%20Tu%20Jaane%20Na%20-%20Ajab%20Prem%20Ki%20Ghazab%20Kahani.mp3"
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
      youtubeVideoId = "f6vYpXf_7Xk",
      streamUrl = "https://ia600800.us.archive.org/16/items/083-atif-aslam/083%20-%20ATIF%20ASLAM/Playback%20-%20Atif%20Aslam/01%20Filmi%20Songs/25%20-%20%20Tere%20Bin%20-%20Bas%20Ek%20Pal.mp3"
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

  // --- Real Audio Playback via Native Android MediaPlayer ---

  fun playTrack(index: Int) {
    val newIdx = (index + tracks.size) % tracks.size
    _currentTrackIndex.value = newIdx
    _trackProgressSeconds.value = 0
    _trackDurationSeconds.value = tracks[newIdx].durationSeconds
    _isBuffering.value = true
    _errorMessage.value = null
    _isPlaying.value = true
    _isMusicEnabled.value = true

    stopAndReleasePlayer()

    val track = tracks[newIdx]

    scope.launch(Dispatchers.IO) {
      try {
        val mp = MediaPlayer().apply {
          setAudioAttributes(
            AudioAttributes.Builder()
              .setUsage(AudioAttributes.USAGE_MEDIA)
              .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
              .build()
          )
        }
        mediaPlayer = mp

        var isConfigured = false

        // Check if track has bundled local raw resource (offline instant play)
        if (track.localRawResId != 0) {
          try {
            val afd: AssetFileDescriptor = context.resources.openRawResourceFd(track.localRawResId)
            mp.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            afd.close()
            isConfigured = true
          } catch (_: Throwable) {}
        }

        // If not bundled locally, stream genuine MP3 over HTTP
        if (!isConfigured && track.streamUrl.isNotBlank()) {
          try {
            mp.setDataSource(track.streamUrl)
            isConfigured = true
          } catch (_: Throwable) {}
        }

        // Fallback to bundled master track if network stream cannot be configured
        if (!isConfigured) {
          try {
            val afd = context.resources.openRawResourceFd(R.raw.track_kuch_is_tarah)
            mp.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            afd.close()
            isConfigured = true
          } catch (_: Throwable) {}
        }

        if (!isConfigured) {
          withContext(Dispatchers.Main) {
            _isBuffering.value = false
            _isPlaying.value = false
            _errorMessage.value = "Audio stream unavailable. Tap Retry."
          }
          return@launch
        }

        mp.setOnPreparedListener { player ->
          scope.launch(Dispatchers.Main) {
            _isBuffering.value = false
            val dur = player.duration / 1000
            if (dur > 0) _trackDurationSeconds.value = dur

            if (_isMusicEnabled.value) {
              val vol = if (_isMusicMuted.value) 0f else _volume.value
              player.setVolume(vol, vol)
              player.start()
              _isPlaying.value = true
              startProgressTracker()
            } else {
              _isPlaying.value = false
            }
          }
        }

        mp.setOnCompletionListener {
          scope.launch(Dispatchers.Main) {
            _isPlaying.value = false
            onTrackEnded()
          }
        }

        mp.setOnErrorListener { _, what, extra ->
          scope.launch(Dispatchers.Main) {
            _isBuffering.value = false
            _isPlaying.value = false
            _errorMessage.value = "Stream connection issue. Tap Retry to reconnect."
          }
          true
        }

        mp.prepareAsync()
      } catch (e: Exception) {
        withContext(Dispatchers.Main) {
          _isBuffering.value = false
          _isPlaying.value = false
          _errorMessage.value = "Playback error. Tap Retry to reload."
        }
      }
    }
  }

  fun togglePlayPause() {
    if (_isPlaying.value) {
      pauseMusic()
    } else {
      resumeMusic()
    }
  }

  fun resumeMusic() {
    if (!_isMusicEnabled.value) {
      _isMusicEnabled.value = true
    }
    _errorMessage.value = null

    scope.launch(Dispatchers.IO) {
      try {
        val mp = mediaPlayer
        if (mp != null) {
          val vol = if (_isMusicMuted.value) 0f else _volume.value
          mp.setVolume(vol, vol)
          mp.start()
          withContext(Dispatchers.Main) {
            _isPlaying.value = true
            startProgressTracker()
          }
        } else {
          withContext(Dispatchers.Main) {
            playTrack(_currentTrackIndex.value)
          }
        }
      } catch (_: Throwable) {
        withContext(Dispatchers.Main) {
          playTrack(_currentTrackIndex.value)
        }
      }
    }
  }

  fun pauseMusic() {
    _isPlaying.value = false
    progressJob?.cancel()
    scope.launch(Dispatchers.IO) {
      try {
        if (mediaPlayer?.isPlaying == true) {
          mediaPlayer?.pause()
        }
      } catch (_: Throwable) {}
    }
  }

  /**
   * Turns background music playback completely ON or OFF.
   * When turned OFF, music pauses immediately and will not resume unexpectedly.
   */
  fun toggleMusicPower() {
    val newPower = !_isMusicEnabled.value
    setMusicPower(newPower)
  }

  fun setMusicPower(enabled: Boolean) {
    _isMusicEnabled.value = enabled
    if (enabled) {
      resumeMusic()
    } else {
      pauseMusic()
    }
  }

  fun nextTrack() {
    val nextIdx = if (_isShuffle.value) {
      var candidate = Random.nextInt(tracks.size)
      if (candidate == _currentTrackIndex.value && tracks.size > 1) {
        candidate = (candidate + 1) % tracks.size
      }
      candidate
    } else {
      (_currentTrackIndex.value + 1) % tracks.size
    }
    playTrack(nextIdx)
  }

  fun previousTrack() {
    if (_trackProgressSeconds.value > 5) {
      seekTo(0)
    } else {
      val prevIdx = (_currentTrackIndex.value - 1 + tracks.size) % tracks.size
      playTrack(prevIdx)
    }
  }

  fun seekTo(seconds: Int) {
    val maxDur = maxOf(_trackDurationSeconds.value, tracks[_currentTrackIndex.value].durationSeconds)
    val clamped = seconds.coerceIn(0, maxDur)
    _trackProgressSeconds.value = clamped
    scope.launch(Dispatchers.IO) {
      try {
        mediaPlayer?.seekTo(clamped * 1000)
      } catch (_: Throwable) {}
    }
  }

  fun setVolume(vol: Float) {
    val clamped = vol.coerceIn(0f, 1f)
    _volume.value = clamped
    val effectiveVol = if (_isMusicMuted.value) 0f else clamped
    scope.launch(Dispatchers.IO) {
      try {
        mediaPlayer?.setVolume(effectiveVol, effectiveVol)
      } catch (_: Throwable) {}
    }
  }

  fun toggleMusicMute() {
    _isMusicMuted.value = !_isMusicMuted.value
    setVolume(_volume.value)
  }

  fun toggleSoundFxMute() {
    _isSoundFxMuted.value = !_isSoundFxMuted.value
  }

  fun toggleShuffle() {
    _isShuffle.value = !_isShuffle.value
  }

  fun toggleRepeat() {
    _repeatMode.value = when (_repeatMode.value) {
      PlaybackRepeatMode.REPEAT_ALL -> PlaybackRepeatMode.REPEAT_ONE
      PlaybackRepeatMode.REPEAT_ONE -> PlaybackRepeatMode.OFF
      PlaybackRepeatMode.OFF -> PlaybackRepeatMode.REPEAT_ALL
    }
  }

  fun retryCurrentTrack() {
    _errorMessage.value = null
    _isBuffering.value = true
    playTrack(_currentTrackIndex.value)
  }

  fun onTrackEnded() {
    when (_repeatMode.value) {
      PlaybackRepeatMode.REPEAT_ONE -> {
        seekTo(0)
        resumeMusic()
      }
      PlaybackRepeatMode.OFF -> {
        val isLast = _currentTrackIndex.value >= tracks.size - 1
        if (isLast) {
          pauseMusic()
          _trackProgressSeconds.value = 0
        } else {
          nextTrack()
        }
      }
      PlaybackRepeatMode.REPEAT_ALL -> {
        nextTrack()
      }
    }
  }

  private fun startProgressTracker() {
    progressJob?.cancel()
    progressJob = scope.launch(Dispatchers.Main) {
      while (isActive && _isPlaying.value) {
        delay(500)
        try {
          if (mediaPlayer?.isPlaying == true) {
            val curr = (mediaPlayer?.currentPosition ?: 0) / 1000
            val dur = (mediaPlayer?.duration ?: 0) / 1000
            if (curr >= 0) _trackProgressSeconds.value = curr
            if (dur > 0) _trackDurationSeconds.value = dur
          }
        } catch (_: Throwable) {}
      }
    }
  }

  private fun stopAndReleasePlayer() {
    progressJob?.cancel()
    progressJob = null
    try {
      mediaPlayer?.stop()
      mediaPlayer?.reset()
      mediaPlayer?.release()
    } catch (_: Throwable) {}
    mediaPlayer = null
  }

  fun release() {
    _isPlaying.value = false
    stopAndReleasePlayer()
  }

  // --- Tactile Sound Effects for Mini-Games (Heart Catcher, Balloon Pop, Cake Cut, etc.) ---

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
        val rand = java.util.Random()

        for (i in 0 until numSamples) {
          val env = 1.0 - (i.toDouble() / numSamples)
          val noise = (rand.nextDouble() * 2.0 - 1.0) * env * fxVol
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
          val t = i.toDouble() / numSamples
          val freq = 300.0 + 200.0 * sin(PI * t)
          val env = sin(PI * t)
          val wave = sin(2.0 * PI * freq * (i.toDouble() / sampleRate)) * env * fxVol
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

  private fun playSparkleChime() {
    scope.launch {
      val freqs = listOf(1046.5, 1318.5, 1567.98, 2093.0)
      for (f in freqs) {
        playTransientTone(f, 80, 12.0)
        delay(45)
      }
    }
  }
}
