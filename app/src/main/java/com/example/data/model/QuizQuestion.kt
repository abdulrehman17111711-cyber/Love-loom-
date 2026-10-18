package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quiz_questions")
data class QuizQuestion(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val category: String, // "Birthday Trivia", "Cute Couple Quiz", "How Well Do You Know Me?"
  val questionText: String,
  val optionA: String,
  val optionB: String,
  val optionC: String,
  val optionD: String,
  val correctOptionIndex: Int,
  val explanation: String = "",
  val isCustom: Boolean = false
) {
  fun getOptions(): List<String> = listOf(optionA, optionB, optionC, optionD).filter { it.isNotBlank() }
}
