package by.extbe.wordsmasta.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import by.extbe.wordsmasta.persistence.WordsMastaDatabase
import kotlinx.coroutines.launch

class WordsLearningPreparationViewModel(application: Application) : AndroidViewModel(application) {
    val languageNames = MutableLiveData<List<String>>()
    val wordGroups = MutableLiveData<List<String>>()

    init {
        viewModelScope.launch {
            val database = WordsMastaDatabase.getDatabase(application)
            launch {
                languageNames.value = database.languageDao()
                    .getAllNames()
            }
            launch {
                wordGroups.value = database.wordGroupDao()
                    .getAllGroupsThatHaveWords()
                    .map { it.name }
            }
        }
    }

    fun getLanguageNamesExceptGiven(nameToExclude: String): List<String> {
        return languageNames.value?.filter { it != nameToExclude } ?: listOf()
    }
}