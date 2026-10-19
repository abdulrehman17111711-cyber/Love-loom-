package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.GameRecord
import com.example.data.model.LoveLetter
import com.example.data.model.MemoryItem
import com.example.data.model.QuizQuestion
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface LoveLoomDao {

  // User Profile
  @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
  fun getUserProfileFlow(): Flow<UserProfile?>

  @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
  suspend fun getUserProfile(): UserProfile?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdateProfile(profile: UserProfile)

  // Memories
  @Query("SELECT * FROM memories ORDER BY timestamp DESC")
  fun getAllMemoriesFlow(): Flow<List<MemoryItem>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMemory(memory: MemoryItem): Long

  @Delete
  suspend fun deleteMemory(memory: MemoryItem)

  @Update
  suspend fun updateMemory(memory: MemoryItem)

  // Love Letters
  @Query("SELECT * FROM love_letters ORDER BY isFavorite DESC, id ASC")
  fun getAllLettersFlow(): Flow<List<LoveLetter>>

  @Query("SELECT * FROM love_letters")
  suspend fun getAllLetters(): List<LoveLetter>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertLetter(letter: LoveLetter): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAllLetters(letters: List<LoveLetter>)

  @Update
  suspend fun updateLetter(letter: LoveLetter)

  @Delete
  suspend fun deleteLetter(letter: LoveLetter)

  @Query("SELECT COUNT(*) FROM love_letters")
  suspend fun getLetterCount(): Int

  // Quiz Questions
  @Query("SELECT * FROM quiz_questions WHERE category = :category ORDER BY id ASC")
  fun getQuestionsByCategoryFlow(category: String): Flow<List<QuizQuestion>>

  @Query("SELECT * FROM quiz_questions WHERE category = :category ORDER BY id ASC")
  suspend fun getQuestionsByCategory(category: String): List<QuizQuestion>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertQuestion(question: QuizQuestion): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAllQuestions(questions: List<QuizQuestion>)

  @Query("SELECT COUNT(*) FROM quiz_questions")
  suspend fun getQuestionCount(): Int

  // Game Records
  @Query("SELECT * FROM game_records")
  fun getAllGameRecordsFlow(): Flow<List<GameRecord>>

  @Query("SELECT * FROM game_records WHERE gameKey = :key LIMIT 1")
  suspend fun getGameRecord(key: String): GameRecord?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdateGameRecord(record: GameRecord)
}
