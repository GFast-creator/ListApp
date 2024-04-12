package ru.gfastg98.myapplication.room

import android.content.Context
import androidx.room.Database
import androidx.room.DeleteTable
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec

@Database(version = 1, entities = [Word::class]
)
abstract class AppDatabase : RoomDatabase() {
    abstract val wordDao: WordDao

    @DeleteTable( "Album")
    class WordsAutoMigration : AutoMigrationSpec

    companion object{
        fun create(context : Context, fromSimples: Boolean = true) : AppDatabase {
            return Room
                .databaseBuilder(context, AppDatabase::class.java, "database.db")
                .apply {
                    if (fromSimples){
                        createFromAsset("simple.db")
                    }
                }
                .build()

        }
    }
}