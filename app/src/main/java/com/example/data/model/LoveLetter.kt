package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "love_letters")
data class LoveLetter(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val title: String,
  val content: String,
  val category: String, // "Birthday", "Romantic", "Proposal", "Cute", "Custom"
  val dateCreated: String,
  val isFavorite: Boolean = false,
  val isCustom: Boolean = false
)
