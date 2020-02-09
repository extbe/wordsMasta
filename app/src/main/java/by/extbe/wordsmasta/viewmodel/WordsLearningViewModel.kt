package by.extbe.wordsmasta.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import by.extbe.wordsmasta.dto.WordForTranslation
import by.extbe.wordsmasta.persistence.WordsMastaDatabase
import by.extbe.wordsmasta.service.WordService
import kotlinx.coroutines.launch

class WordsLearningViewModel(application: Application) : AndroidViewModel(application) {
    private var sourceLangId: Long? = null
    private var targetLangId: Long? = null

    private lateinit var wordService: WordService

    val wordForTranslation = MutableLiveData<WordForTranslation>()

    fun initContext(sourceLangCode: String, targetLangCode: String) = viewModelScope.launch {
        val db = WordsMastaDatabase.getDatabase(getApplication())
        wordService = WordService(db.wordDao(), db.languageDao(), db.translationDao())

        sourceLangCode.let { sourceLangId = wordService.getLanguageIdByName(it) }
        targetLangCode.let { targetLangId = wordService.getLanguageIdByName(it) }

        retrieveNextWordForTranslation()
    }

    fun fetchNextWord() = viewModelScope.launch {
        retrieveNextWordForTranslation()
    }

    private suspend fun retrieveNextWordForTranslation() {
        wordForTranslation.value = wordService.getWordForTranslation(sourceLangId!!, targetLangId!!)
    }
}