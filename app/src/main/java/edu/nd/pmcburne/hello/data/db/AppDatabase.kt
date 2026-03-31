package edu.nd.pmcburne.hello.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [LocationEntity::class, TagEntity::class, LocationTagEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun placemarkDao(): PlacemarkDao
}