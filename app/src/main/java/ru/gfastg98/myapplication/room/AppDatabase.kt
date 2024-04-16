package ru.gfastg98.myapplication.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Database(version = 1, entities = [Word::class]
)
abstract class AppDatabase : RoomDatabase() {
    abstract val wordDao: WordDao
}

//hilt integration
@Module
@InstallIn(SingletonComponent::class)
object HiltModule {
    @Singleton
    @Provides
    fun create(@ApplicationContext context : Context) : AppDatabase {
        return Room
            .databaseBuilder(context, AppDatabase::class.java, "database.db")
            .build()
    }
}