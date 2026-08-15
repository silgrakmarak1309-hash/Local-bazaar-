package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        ListingEntity::class,
        FavoriteEntity::class,
        ConversationEntity::class,
        MessageEntity::class,
        NotificationEntity::class,
        ReviewEntity::class,
        SafetyReportEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class LocalBazaarDatabase : RoomDatabase() {
    abstract fun listingDao(): ListingDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun chatDao(): ChatDao
    abstract fun notificationDao(): NotificationDao
    abstract fun reviewDao(): ReviewDao
    abstract fun safetyReportDao(): SafetyReportDao

    companion object {
        @Volatile
        private var INSTANCE: LocalBazaarDatabase? = null

        fun getInstance(context: Context): LocalBazaarDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LocalBazaarDatabase::class.java,
                    "localbazaar_database.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
