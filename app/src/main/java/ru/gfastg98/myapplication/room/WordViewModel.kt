package ru.gfastg98.myapplication.room

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

class WordViewModel(private val dao: WordDao) : ViewModel() {

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