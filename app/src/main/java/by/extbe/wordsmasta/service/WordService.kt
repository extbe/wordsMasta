package by.extbe.wordsmasta.service

import by.extbe.wordsmasta.constant.DefaultGroup
import by.extbe.wordsmasta.dto.WordForTranslation
import by.extbe.wordsmasta.dto.WordGroupsImportDto
import by.extbe.wordsmasta.persistence.WordsMastaDatabase
import by.extbe.wordsmasta.persistence.entity.Group
import by.extbe.wordsmasta.persistence.entity.Translation
import by.extbe.wordsmasta.persistence.entity.Word
import by.extbe.wordsmasta.persistence.entity.WordGroup
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

    suspend fun importFromFile(fileIn: InputStream) {
        val yaml = Yaml(Constructor(WordGroupsImportDto::class.java))
        val wordGroupsImportDto = fileIn.use { yaml.load(it) as WordGroupsImportDto }

        if (wordGroupsImportDto.groups.isEmpty()) {
            return
        }

        val allWordsGroupId = getGroupIdByName(DefaultGroup.ALL_WORDS.title)
        for (wordGroupDto in wordGroupsImportDto.groups) {
            val wordGroupId = findGroupIdByName(wordGroupDto.groupName)
                ?: createWordGroup(wordGroupDto.groupName)
            val sourceLangId = getLanguageIdByCode(wordGroupDto.sourceLanguageCode)
            val targetLangId = getLanguageIdByCode(wordGroupDto.targetLanguageCode)

            for ((word, translation) in wordGroupDto.words) {
                val wordId = wordDao.insertOne(Word(sourceLangId, word))
                val translationId = wordDao.insertOne(Word(targetLangId, translation))
                translationDao.insertOne(Translation(wordId, translationId))
                wordGroupDao.insertMany(
                    listOf(
                        WordGroup(wordId, allWordsGroupId),
                        WordGroup(wordId, wordGroupId),
                        WordGroup(translationId, allWordsGroupId),
                        WordGroup(translationId, wordGroupId)
                    )
                )
            }
        }
    }

    suspend fun getLanguageIdByName(name: String): Long =
        languageDao.getIdByName(name) ?: error("Language with name [$name] not found")

    suspend fun getGroupIdByName(name: String) =
        findGroupIdByName(name) ?: error("Group with name [$name] not found")

    private suspend fun findGroupIdByName(name: String): Long? = groupDao.selectIdByName(name)

    private suspend fun getLanguageIdByCode(code: String): Long =
        languageDao.getIdByCode(code) ?: error("Language with code [$code] not found")

    private suspend fun createWordGroup(name: String): Long = groupDao.insertOne(Group(name))

    suspend fun getWordForTranslation(
        sourceLangId: Long,
        targetLangId: Long,
        wordGroupId: Long
    ): WordForTranslation {
        val words = wordDao.getNRandomWordsWithTranslation(
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