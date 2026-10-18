package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
  @PrimaryKey val id: Int = 1,
  val recipientName: String = "Ayesha",
  val birthDay: Int = 11,
  val birthMonth: Int = 12, // 1-12
  val birthYear: Int = 2008,
  val pinCode: String = "",
  val isLockEnabled: Boolean = false,
  val themeIndex: Int = 0, // 0: Romantic Rose, 1: Lavender Dream, 2: Golden Champagne, 3: Sweet Midnight
  val reducedMotion: Boolean = false,
  val musicEnabled: Boolean = true,
  val soundFxEnabled: Boolean = true,
  val volume: Float = 0.8f,
  val isOnboarded: Boolean = false,
  val createdAt: Long = System.currentTimeMillis()
)
