package ru.gfastg98.myapplication.room

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WordViewModel @Inject constructor(room: AppDatabase) : ViewModel() {

    private val dao = room.wordDao

    val words = dao.getAll().shareIn(viewModelScope, SharingStarted.Eagerly, replay = 1)
    fun save(word: Word){
        viewModelScope.launch { dao.insertAll(word) }
    }
    fun delete(vararg word: Word){
        viewModelScope.launch{
            dao.delete(*word)
        }
    }
    fun deleteAll(){
        viewModelScope.launch{ dao.deleteAll() }
    }
}