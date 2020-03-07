package by.extbe.wordsmasta.viewmodel

import android.app.Application
import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import by.extbe.wordsmasta.constant.ImportStatus
import by.extbe.wordsmasta.persistence.WordsMastaDatabase
import by.extbe.wordsmasta.service.WordService
import kotlinx.coroutines.launch

class ImportDataViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        val FILE_NAME_REGEX = "^.*_([a-zA-Z]+)_([a-zA-Z]+)\\.txt$".toRegex()
        val WORDS_SEPARATOR_REGEX = "\\s+:\\s+".toRegex()
    }

    var importStatus = MutableLiveData(ImportStatus.NO_DATA)

    fun importWordsFromFile(fileUri: Uri) {
        val contentResolver = getApplication<Application>().contentResolver
        val fileName = getFileName(contentResolver, fileUri)
        val (sourceLangCode, targetLangCode) = FILE_NAME_REGEX.matchEntire(fileName)?.destructured
            ?: throw IllegalStateException("Bad file name")

        importStatus.value = ImportStatus.IN_PROGRESS

        val words = hashMapOf<String, String>()
        contentResolver.openInputStream(fileUri)?.bufferedReader()?.use { reader ->
            reader.lines()
                .map { it.split(WORDS_SEPARATOR_REGEX) }
                .forEach { words[it[0]] = it[1] }
        }
        viewModelScope.launch {
            val db = WordsMastaDatabase.getDatabase(getApplication())
            val wordService = WordService(db)
            wordService.saveWords(words, sourceLangCode, targetLangCode)
            importStatus.value = ImportStatus.COMPLETED
        }
    }

    private fun getFileName(contentResolver: ContentResolver, fileUri: Uri) = contentResolver.query(
        fileUri,
        null,
        null,
        null,
        null
    )?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        cursor.moveToFirst()
        cursor.getString(nameIndex)
    } ?: throw IllegalStateException("Cannot obtain file name")
}