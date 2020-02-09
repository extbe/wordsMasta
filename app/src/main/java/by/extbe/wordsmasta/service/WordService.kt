package by.extbe.wordsmasta.service

import by.extbe.wordsmasta.dto.WordForTranslation
import by.extbe.wordsmasta.persistence.dao.LanguageDao
import by.extbe.wordsmasta.persistence.dao.TranslationDao
import by.extbe.wordsmasta.persistence.dao.WordDao
import by.extbe.wordsmasta.persistence.entity.Translation
import by.extbe.wordsmasta.persistence.entity.Word

class WordService(
    private val wordDao: WordDao,
    private val languageDao: LanguageDao,
    private val translationDao: TranslationDao
) {
    companion object {
        const val NUMBER_OF_TRANSLATION_CHOICES = 4
        const val WORD_FOR_TRANSLATION_POSITION = 0
    }

    suspend fun saveWords(
        words: Map<String, String>,
        sourceLangCode: String,
        targetLangCode: String
    ) {
        val sourceLangId = getLanguageIdByCode(sourceLangCode)
        val targetLangId = getLanguageIdByCode(targetLangCode)

        for ((word, translation) in words) {
            val wordId = wordDao.insertOne(Word(sourceLangId, word))
            val translationId = wordDao.insertOne(Word(targetLangId, translation))
            translationDao.insertOne(Translation(wordId, translationId))
        }
    }

    private suspend fun getLanguageIdByCode(code: String): Long =
        languageDao.getIdByCode(code) ?: error("Language with code [$code] not found")

    suspend fun getLanguageIdByName(name: String): Long =
        languageDao.getIdByName(name) ?: error("Language with name [$name] not found")

    suspend fun getWordForTranslation(sourceLangId: Long, targetLangId: Long): WordForTranslation {
        val words = wordDao.getNRandomWordsWithTranslation(
            sourceLangId,
            targetLangId,
            NUMBER_OF_TRANSLATION_CHOICES
        )

        if (words.size < NUMBER_OF_TRANSLATION_CHOICES)
            error("Not enough words to generate translation choices")

        val word = words[WORD_FOR_TRANSLATION_POSITION].sourceValue
        val translation = words[WORD_FOR_TRANSLATION_POSITION].translation
        val translationChoices = words.map { it.translation }.shuffled()
        return WordForTranslation(word, translation, translationChoices)
    }

    private fun error(message: String): Nothing {
        throw IllegalStateException(message)
    }
}