package com.example.data.local

import android.content.Context
import com.example.data.model.GameRecord
import com.example.data.model.LoveLetter
import com.example.data.model.MemoryItem
import com.example.data.model.QuizQuestion
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.Flow

class LoveLoomRepository(private val dao: LoveLoomDao) {

  val userProfileFlow: Flow<UserProfile?> = dao.getUserProfileFlow()
  val memoriesFlow: Flow<List<MemoryItem>> = dao.getAllMemoriesFlow()
  val lettersFlow: Flow<List<LoveLetter>> = dao.getAllLettersFlow()
  val gameRecordsFlow: Flow<List<GameRecord>> = dao.getAllGameRecordsFlow()

  suspend fun getUserProfile(): UserProfile {
    return dao.getUserProfile() ?: UserProfile().also {
      dao.insertOrUpdateProfile(it)
    }
  }

  suspend fun saveUserProfile(profile: UserProfile) {
    dao.insertOrUpdateProfile(profile)
  }

  suspend fun addMemory(memory: MemoryItem): Long = dao.insertMemory(memory)
  suspend fun deleteMemory(memory: MemoryItem) = dao.deleteMemory(memory)
  suspend fun updateMemory(memory: MemoryItem) = dao.updateMemory(memory)

  suspend fun addLetter(letter: LoveLetter): Long = dao.insertLetter(letter)
  suspend fun updateLetter(letter: LoveLetter) = dao.updateLetter(letter)
  suspend fun deleteLetter(letter: LoveLetter) = dao.deleteLetter(letter)

  fun getQuestionsByCategoryFlow(category: String): Flow<List<QuizQuestion>> =
    dao.getQuestionsByCategoryFlow(category)

  suspend fun getQuestionsByCategory(category: String): List<QuizQuestion> =
    dao.getQuestionsByCategory(category)

  suspend fun addQuestion(question: QuizQuestion): Long = dao.insertQuestion(question)

  suspend fun recordGameResult(gameKey: String, gameTitle: String, score: Int, timeSec: Int = 0) {
    val existing = dao.getGameRecord(gameKey)
    val newHigh = if (existing != null) maxOf(existing.highScore, score) else score
    val newBestTime = if (existing != null && existing.bestTimeSeconds > 0 && timeSec > 0) {
      minOf(existing.bestTimeSeconds, timeSec)
    } else if (timeSec > 0) timeSec else (existing?.bestTimeSeconds ?: 0)

    val updated = GameRecord(
      gameKey = gameKey,
      gameTitle = gameTitle,
      highScore = newHigh,
      gamesPlayed = (existing?.gamesPlayed ?: 0) + 1,
      bestTimeSeconds = newBestTime,
      lastPlayedTimestamp = System.currentTimeMillis()
    )
    dao.insertOrUpdateGameRecord(updated)
  }

  suspend fun seedDefaultsIfEmpty(recipientName: String) {
    // Seed default letters if empty
    if (dao.getLetterCount() == 0) {
      val defaultLetters = listOf(
        LoveLetter(
          title = "Kuch Is Tarah — To My Soulmate",
          content = "Dear $recipientName,\n\n“کچھ اس طرح تیری پلکیں میری پلکوں سے ملا دے\nآنسو تیرے سارے میری پلکوں پہ سجا دے”\n\nLike Atif Aslam’s soulful Pakistani romance, every breath of mine finds its melody in your laughter. You are the warmth in my heart, the calm in my storms, and my forever favorite song. Happy Birthday, my shining light! 🇵🇰💖🎶",
          category = "Poetry & Romance",
          dateCreated = "Special",
          isFavorite = true,
          isCustom = false
        ),
        LoveLetter(
          title = "My Beautiful Birthday Star",
          content = "Dear $recipientName,\n\nOn this special day, the world became infinitely brighter and more magical because you were born. Every smile of yours lights up my universe. I hope your birthday is filled with all the joy, sweetness, and cuddles you bring to everyone around you. Happy Birthday, my love! 🎂💖",
          category = "Birthday",
          dateCreated = "Today",
          isFavorite = true,
          isCustom = false
        ),
        LoveLetter(
          title = "A Cuddle For Every Star",
          content = "Dear $recipientName,\n\nIf I had a soft rose petal for every time you made me smile, our garden would overflow to the moon and back. Thank you for being the sweetest, most caring soul in my life. You make every ordinary day feel like a magical fairy tale. 🌸🐻",
          category = "Romantic",
          dateCreated = "Special",
          isFavorite = true,
          isCustom = false
        ),
        LoveLetter(
          title = "Forever Beside You",
          content = "To my dearest $recipientName,\n\nThrough every season, sunny days and gentle rain, my favorite place in the world is simply walking beside you, holding your hand. Here is to making countless more unforgettable memories together. 🌟✨",
          category = "Proposal",
          dateCreated = "Special",
          isFavorite = false,
          isCustom = false
        ),
        LoveLetter(
          title = "The Sweetest Secret",
          content = "Dear $recipientName,\n\nDo you know what the two little bears whisper to each other every night under the glowing stars? They whisper that $recipientName is the kindest, cutest, most wonderful person in the whole universe! 💌🧸",
          category = "Cute",
          dateCreated = "Special",
          isFavorite = false,
          isCustom = false
        )
      )
      dao.insertAllLetters(defaultLetters)
    }

    // Seed default questions if empty
    if (dao.getQuestionCount() == 0) {
      val defaultQuestions = listOf(
        // Birthday Trivia
        QuizQuestion(
          category = "Birthday Trivia",
          questionText = "What is the sweetest tradition when a birthday cake is presented?",
          optionA = "Making a wish & blowing candles",
          optionB = "Throwing cake across the room",
          optionC = "Counting backwards from 100",
          optionD = "Hiding in the closet",
          correctOptionIndex = 0,
          explanation = "Making a wish and blowing out candles is believed to carry your heart's desire to the stars!"
        ),
        QuizQuestion(
          category = "Birthday Trivia",
          questionText = "Which flower is celebrated worldwide as the eternal symbol of deep love and passion?",
          optionA = "Velvet Red Rose",
          optionB = "Thorny Cactus",
          optionC = "Garden Dandelion",
          optionD = "Wild Fern",
          correctOptionIndex = 0,
          explanation = "Red roses represent romance, devotion, and beauty!"
        ),
        QuizQuestion(
          category = "Birthday Trivia",
          questionText = "What makes a birthday celebration truly unforgettable?",
          optionA = "The love, laughter & special people",
          optionB = "Having 50 alarm clocks",
          optionC = "Writing homework",
          optionD = "Cold soup",
          correctOptionIndex = 0,
          explanation = "Shared warmth and heartfelt moments make memories that last forever!"
        ),
        // Cute Couple Quiz
        QuizQuestion(
          category = "Cute Couple Quiz",
          questionText = "What is the ultimate cure for a tough or tiring day?",
          optionA = "A long warm bear hug & sweet words",
          optionB = "Reading the phone book",
          optionC = "Sitting in heavy traffic",
          optionD = "Eating a raw lemon",
          correctOptionIndex = 0,
          explanation = "A warm hug from someone special releases comfort and melts away all stress!"
        ),
        QuizQuestion(
          category = "Cute Couple Quiz",
          questionText = "What do the two cute LoveLoom bears love doing the most?",
          optionA = "Holding a glowing heart together",
          optionB = "Fighting over honey",
          optionC = "Sleeping through birthdays",
          optionD = "Running away",
          correctOptionIndex = 0,
          explanation = "The two bears are inseparable and always hold their sparkling heart together!"
        ),
        QuizQuestion(
          category = "Cute Couple Quiz",
          questionText = "What is the best recipe for romantic happiness?",
          optionA = "Kindness, smiles, and mutual care",
          optionB = "Arguing over tiny things",
          optionC = "Forgetting special dates",
          optionD = "Never talking",
          correctOptionIndex = 0,
          explanation = "Sweet surprises and genuine affection create eternal warmth!"
        ),
        // How Well Do You Know Me?
        QuizQuestion(
          category = "How Well Do You Know Me?",
          questionText = "What sweet flavor best represents our sweet celebration?",
          optionA = "Strawberry cream & vanilla kisses",
          optionB = "Bitter herbs",
          optionC = "Black pepper",
          optionD = "Salted seaweed",
          correctOptionIndex = 0,
          explanation = "Sweet strawberry and rich vanilla are as delightful as our sweet moments!"
        ),
        QuizQuestion(
          category = "How Well Do You Know Me?",
          questionText = "What would be our dream romantic date?",
          optionA = "Fairy-lit garden picnic under the stars",
          optionB = "Waiting at a dentist clinic",
          optionC = "Standing in a thunderstorm",
          optionD = "A crowded grocery aisle",
          correctOptionIndex = 0,
          explanation = "Twinkling lights, gentle breeze, and sweet conversation make magic!"
        ),
        QuizQuestion(
          category = "How Well Do You Know Me?",
          questionText = "What is the most important date to celebrate?",
          optionA = "$recipientName's Special Birthday!",
          optionB = "National Sock Day",
          optionC = "Tax Deadline",
          optionD = "Random Tuesday",
          correctOptionIndex = 0,
          explanation = "Because $recipientName deserves all the celebration and love in the universe!"
        )
      )
      dao.insertAllQuestions(defaultQuestions)
    }
  }

  companion object {
    @Volatile
    private var INSTANCE: LoveLoomRepository? = null

    fun getRepository(context: Context): LoveLoomRepository {
      return INSTANCE ?: synchronized(this) {
        val db = LoveLoomDatabase.getDatabase(context)
        val instance = LoveLoomRepository(db.loveLoomDao())
        INSTANCE = instance
        instance
      }
    }
  }
}
