package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memories")
data class MemoryItem(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val imageUri: String,
  val title: String,
  val dateText: String,
  val caption: String,
  val isFavorite: Boolean = false,
  val timestamp: Long = System.currentTimeMillis()
)
