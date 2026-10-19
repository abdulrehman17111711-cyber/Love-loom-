package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.LoveLetter
import com.example.data.model.QuizQuestion
import com.example.data.model.UserProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context matches app name`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("LoveLoom 3D", appName)
  }

  @Test
  fun `verify UserProfile default state`() {
    val profile = UserProfile()
    assertEquals("Ayesha", profile.recipientName)
    assertEquals(11, profile.birthDay)
    assertEquals(12, profile.birthMonth)
    assertEquals(2008, profile.birthYear)
    assertTrue(profile.musicEnabled)
  }

  @Test
  fun `verify QuizQuestion options helper`() {
    val question = QuizQuestion(
      category = "Birthday Trivia",
      questionText = "What is the sweetest tradition?",
      optionA = "Making a wish",
      optionB = "Blowing candles",
      optionC = "Eating cake",
      optionD = "Sharing hugs",
      correctOptionIndex = 0
    )
    val options = question.getOptions()
    assertEquals(4, options.size)
    assertEquals("Making a wish", options[0])
  }

  @Test
  fun `verify LoveLetter entity fields`() {
    val letter = LoveLetter(
      title = "Happy Birthday",
      content = "Wishing you a lifetime of joy!",
      category = "Birthday",
      dateCreated = "Today"
    )
    assertNotNull(letter)
    assertEquals("Birthday", letter.category)
    assertTrue(letter.content.contains("lifetime of joy"))
  }

  @Test
  fun `verify Atif Aslam Pakistani playlist tracks`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val audioEngine = com.example.audio.LoveAudioEngine(context)
    val tracks = audioEngine.tracks

    assertEquals(13, tracks.size)
    assertEquals("Kuch Is Tarah", tracks[0].title)
    assertEquals("Doorie", tracks[0].album)
    assertEquals("Atif Aslam", tracks[0].artist)
    assertTrue(tracks[0].lyricsUrdu.contains("کچھ اس طرح"))
    assertEquals("dQm7vX18K5M", tracks[0].youtubeVideoId)

    assertEquals("Doorie", tracks[1].title)
    assertEquals("Aadat (Acoustic)", tracks[2].title)
    assertEquals("Jal Pari", tracks[2].album)

    assertEquals("Mahi Ve", tracks[3].title)
    assertEquals("Kadi Te Has Bol Ve", tracks[4].title)
    assertEquals("Yakeen", tracks[5].title)
    assertEquals("Ehsaas", tracks[6].title)
    assertEquals("Pehli Nazar Mein", tracks[7].title)
    assertEquals("Tera Hone Laga Hoon", tracks[8].title)
  }

  @Test
  fun `verify 3D post processing configuration and environment moods`() {
    val config = com.example.ui.components.PostProcessingConfig(
      ambientOcclusion = true,
      aoIntensity = 1.0f,
      dynamicSoftShadows = true,
      shadowSoftness = 1.0f,
      refinedBloom = true,
      bloomIntensity = 1.2f,
      rimLighting = true,
      studioEnvironment = true,
      mood = com.example.ui.components.EnvironmentMood.PASTEL_SUNSET
    )

    assertTrue(config.ambientOcclusion)
    assertTrue(config.dynamicSoftShadows)
    assertTrue(config.refinedBloom)
    assertTrue(config.rimLighting)
    assertEquals(4, com.example.ui.components.EnvironmentMood.values().size)
  }
}
