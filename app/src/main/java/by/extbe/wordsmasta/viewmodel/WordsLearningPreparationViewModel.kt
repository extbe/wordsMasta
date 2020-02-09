package by.extbe.wordsmasta.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import by.extbe.wordsmasta.persistence.WordsMastaDatabase
import kotlinx.coroutines.launch

class WordsLearningPreparationViewModel(application: Application) : AndroidViewModel(application) {
    val languageNames = MutableLiveData<List<String>>()

    init {
        viewModelScope.launch {
            languageNames.value = WordsMastaDatabase.getDatabase(application)
                .languageDao()
                .getAllNames()
        }
    }

    fun getLanguageNamesExceptGiven(nameToExclude: String): List<String> {
        return languageNames.value?.filter { it != nameToExclude } ?: listOf()
    }
}