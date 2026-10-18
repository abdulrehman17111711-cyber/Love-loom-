package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.GameRecord
import com.example.data.model.LoveLetter
import com.example.data.model.MemoryItem
import com.example.data.model.QuizQuestion
import com.example.data.model.UserProfile

@Database(
  entities = [
    UserProfile::class,
    MemoryItem::class,
    LoveLetter::class,
    QuizQuestion::class,
    GameRecord::class
  ],
  version = 1,
  exportSchema = false
)
abstract class LoveLoomDatabase : RoomDatabase() {
  abstract fun loveLoomDao(): LoveLoomDao

  companion object {
    @Volatile
    private var INSTANCE: LoveLoomDatabase? = null

    fun getDatabase(context: Context): LoveLoomDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          LoveLoomDatabase::class.java,
          "loveloom_3d.db"
        ).fallbackToDestructiveMigration().build()
        INSTANCE = instance
        instance
      }
    }
  }
}
