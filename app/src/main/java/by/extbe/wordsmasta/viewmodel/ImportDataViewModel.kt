package by.extbe.wordsmasta.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import by.extbe.wordsmasta.constant.ImportStatus
import by.extbe.wordsmasta.persistence.WordsMastaDatabase
import by.extbe.wordsmasta.service.WordService
import kotlinx.coroutines.launch

class ImportDataViewModel(application: Application) : AndroidViewModel(application) {
    val importStatus = MutableLiveData(ImportStatus.NO_DATA)
    val importCounter = MutableLiveData<Int>()
    val totalCounter = MutableLiveData<Int>()

    fun importWordsFromFile(fileUri: Uri) {
        importStatus.value = ImportStatus.IN_PROGRESS

        val contentResolver = getApplication<Application>().contentResolver
        contentResolver.openInputStream(fileUri)?.let {
            viewModelScope.launch {
                val db = WordsMastaDatabase.getDatabase(getApplication())
                val wordService = WordService(db)
                wordService.importFromFile(it, importCounter, totalCounter)
                importStatus.value = ImportStatus.COMPLETED
            }
        }
    }
}