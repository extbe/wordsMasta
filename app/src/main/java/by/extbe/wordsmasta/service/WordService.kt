package by.extbe.wordsmasta.service

import androidx.lifecycle.MutableLiveData
import by.extbe.wordsmasta.constant.DefaultGroup
import by.extbe.wordsmasta.dto.WordForTranslation
import by.extbe.wordsmasta.dto.WordGroupsImportDto
import by.extbe.wordsmasta.persistence.WordsMastaDatabase
import by.extbe.wordsmasta.persistence.entity.Group
import by.extbe.wordsmasta.persistence.entity.Translation
import by.extbe.wordsmasta.persistence.entity.Word
import by.extbe.wordsmasta.persistence.entity.WordGroup
import kotlinx.coroutines.delay
import kotlinx.coroutines.yield
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor
import java.io.InputStream

class WordService(db: WordsMastaDatabase) {
    private val wordDao = db.wordDao()
    private val languageDao = db.languageDao()
    private val translationDao = db.translationDao()
    private val groupDao = db.groupDao()
    private val wordGroupDao = db.wordGroupDao()

    companion object {
        const val NUMBER_OF_TRANSLATION_CHOICES = 4
        const val WORD_FOR_TRANSLATION_POSITION = 0
    }

    // todo: split
    suspend fun importFromFile(
        fileIn: InputStream,
        importCounter: MutableLiveData<Int>,
        totalCounter: MutableLiveData<Int>
    ) {
        val yaml = Yaml(Constructor(WordGroupsImportDto::class.java))
        val wordGroupsImportDto = fileIn.use { yaml.load(it) as WordGroupsImportDto }

        if (wordGroupsImportDto.groups.isEmpty()) {
            return
        }

        importCounter.value = 0

        var wordsTotal = 0
        wordGroupsImportDto.groups.forEach { wordGroup ->
            wordsTotal += wordGroup.words.size
        }
        totalCounter.value = wordsTotal

        val allWordsGroupId = getGroupIdByName(DefaultGroup.ALL_WORDS.title)
        for (wordGroupDto in wordGroupsImportDto.groups) {
            val wordGroupId = findGroupIdByName(wordGroupDto.groupName)
                ?: createWordGroup(wordGroupDto.groupName)
            val sourceLangId = getLanguageIdByCode(wordGroupDto.sourceLanguageCode)
            val targetLangId = getLanguageIdByCode(wordGroupDto.targetLanguageCode)

            for ((word, translation) in wordGroupDto.words) {
                val wordId = getOrCreateWord(sourceLangId, word)
                val translationId = getOrCreateWord(targetLangId, translation)
                createTranslationIfNotExists(wordId, translationId)
                val notExistingGroups = listOf(
                    WordGroup(wordId, allWordsGroupId),
                    WordGroup(wordId, wordGroupId),
                    WordGroup(translationId, allWordsGroupId),
                    WordGroup(translationId, wordGroupId)
                ).filter { wordGroupDao.existsByWordIdAndGroupId(it.wordId, it.groupId) == null }
                if (notExistingGroups.isNotEmpty()) {
                    wordGroupDao.insertMany(notExistingGroups)
                }
                importCounter.value = importCounter.value!! + 1
                yield()
            }
        }
    }

    suspend fun getLanguageIdByName(name: String): Long =
        languageDao.getIdByName(name) ?: error("Language with name [$name] not found")

    suspend fun getGroupIdByName(name: String) =
        findGroupIdByName(name) ?: error("Group with name [$name] not found")

    private suspend fun getOrCreateWord(languageId: Long, value: String): Long {
        return wordDao.selectIdByLanguageIdAndValue(languageId, value)
            ?: wordDao.insertOne(Word(languageId, value))
    }

    private suspend fun createTranslationIfNotExists(wordId: Long, translationId: Long) {
        translationDao.selectBySourceAndTargetIds(wordId, translationId)
            ?: translationDao.insertOne(Translation(wordId, translationId))
    }

    private suspend fun findGroupIdByName(name: String): Long? = groupDao.selectIdByName(name)

    private suspend fun getLanguageIdByCode(code: String): Long =
        languageDao.getIdByCode(code) ?: error("Language with code [$code] not found")

    private suspend fun createWordGroup(name: String): Long = groupDao.insertOne(Group(name))

    suspend fun getWordForTranslation(
        sourceLangId: Long,
        targetLangId: Long,
        wordGroupId: Long
    ): WordForTranslation {
        val words = wordDao.selectNRandomWordsWithTranslation(
            sourceLangId,
            targetLangId,
            wordGroupId,
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