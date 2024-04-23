package ru.gfastg98.myapplication.room

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Database(
    version = 2,
    entities = [Word::class],
    autoMigrations = [
        AutoMigration (from = 1, to = 2)
    ]
)

abstract class AppDatabase : RoomDatabase() {
    abstract val wordDao: WordDao

    companion object{
        private var instance: AppDatabase? = null

        fun create(@ApplicationContext context : Context) : AppDatabase {
            if (instance == null){
                instance = Room
                    .databaseBuilder(context, AppDatabase::class.java, "database.db")
                    .build()
            }
            return instance as AppDatabase
        }
    }
}

//hilt integration
@Module
@InstallIn(SingletonComponent::class)
object HiltModule {
    @Singleton
    @Provides
    fun create(@ApplicationContext context : Context) : AppDatabase {
        return AppDatabase.create(context)
    }
}