package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_records")
data class GameRecord(
  @PrimaryKey val gameKey: String, // "heart_catcher", "balloon_pop", "hidden_gift", "memory_match", "love_puzzle", "gift_surprise"
  val gameTitle: String,
  val highScore: Int = 0,
  val gamesPlayed: Int = 0,
  val bestTimeSeconds: Int = 0,
  val lastPlayedTimestamp: Long = System.currentTimeMillis()
)
