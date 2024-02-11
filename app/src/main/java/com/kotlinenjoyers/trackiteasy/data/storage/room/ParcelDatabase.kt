package com.kotlinenjoyers.trackiteasy.data.storage.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kotlinenjoyers.trackiteasy.data.storage.room.parcel.Parcel
import com.kotlinenjoyers.trackiteasy.data.storage.room.parcel.ParcelDao
import com.kotlinenjoyers.trackiteasy.data.storage.room.parcelhistory.ParcelHistory
import com.kotlinenjoyers.trackiteasy.data.storage.room.parcelhistory.ParcelHistoryDao

@Database(entities = [Parcel::class, ParcelHistory::class], version = 1)
@TypeConverters(Converters::class)
abstract class ParcelDatabase : RoomDatabase() {
    abstract fun parcelDao(): ParcelDao
    abstract fun parcelHistoryDao(): ParcelHistoryDao

    companion object {
        @Volatile
        private var Instance: ParcelDatabase? = null

        fun getDatabase(context: Context): ParcelDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, ParcelDatabase::class.java, "parcel_database")
                    /**
                     * Setting this option in your app's database builder means that Room
                     * permanently deletes all data from the tables in your database when it
                     * attempts to perform a migration with no defined migration path.
                     */
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}