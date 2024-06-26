package ru.gfastg98.myapplication.room

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WordViewModel @Inject constructor(db: AppDatabase) : ViewModel() {

    private val dao = db.wordDao

    val words = dao.getAll()

    val selectedWords = dao.getSelected()

    object DialogStateObject{
        private val _isDialog = MutableStateFlow(false)
        val isDialogStateFlow: StateFlow<Boolean>
            get() = _isDialog.asStateFlow()

        fun update(newState : Boolean){
            _isDialog.value = newState
        }
    }
    val dialogStateObj = DialogStateObject

    object DeleteWordsObj{

        private val _wordsForDelete = MutableStateFlow(emptyList<Word>())
        val stateFlowInstant : StateFlow<List<Word>>
            get() = _wordsForDelete.asStateFlow()

        operator fun plusAssign(w: Word) {
            _wordsForDelete.value += w
        }

        operator fun minusAssign(w: Word) {
            _wordsForDelete.value -= w
        }

        fun update(l : List<Word>){
            _wordsForDelete.value = l
        }
    }
    val deleteWordsObj = DeleteWordsObj

    fun save(word: Word){
        viewModelScope.launch { dao.insertAll(word) }
    }
    fun delete(vararg word: Word){
        viewModelScope.launch{
            dao.delete(*word)
        }
    }
    fun deleteAll(){
        viewModelScope.launch{ dao._deleteAll() }
    }

    fun updateWordState(word: Word, newState: Boolean){
        word.isSelected = newState
        viewModelScope.launch { dao.update(word) }
    }
}
