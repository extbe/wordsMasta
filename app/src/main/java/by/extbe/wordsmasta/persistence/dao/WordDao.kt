package by.extbe.wordsmasta.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import by.extbe.wordsmasta.persistence.entity.Word
import by.extbe.wordsmasta.persistence.entity.composite.WordWithTranslation

@Dao
interface WordDao {
    @Query(
        "SELECT w.value as sourceValue, t.value AS translation " +
                "FROM words w " +
                "INNER JOIN translations tr ON w.id = tr.source_word_id " +
                "INNER JOIN words t ON tr.target_word_id = t.id " +
                "WHERE w.language_id = :sourceLangId " +
                "AND t.language_id = :targetLangId " +
                "ORDER BY RANDOM() " +
                "LIMIT :limit"
    )
    suspend fun getNRandomWordsWithTranslation(
        sourceLangId: Long,
        targetLangId: Long,
        limit: Int
    ): List<WordWithTranslation>

    @Insert
    suspend fun insertOne(word: Word): Long
}