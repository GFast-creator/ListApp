package ru.gfastg98.myapplication.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg words: Word)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(words: Word) : Long

    @Update
    suspend fun updateUsers(vararg words: Word)

    @Query("SELECT * FROM words ORDER BY word ASC")
    fun getAll(): Flow<List<Word>>

    @Query("SELECT * FROM words WHERE isSelected = true")
    fun getSelected(): Flow<List<Word>>

    @Delete
    suspend fun delete(vararg word: Word)

    @Query("DELETE FROM words")
    suspend fun _deleteAll()

    @Query("DELETE FROM words")
    fun deleteAll() : Int

//    @Query("UPDATE words SET isSelected = :newState WHERE id = :wordId")
//    suspend fun updateWordState(wordId: Word, newState: Booleam)
    @Update
    suspend fun update(word:Word)

}