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
                "FROM word_groups wg " +
                "INNER JOIN words w ON wg.word_id = w.id " +
                "LEFT JOIN translations tr1 ON w.id = tr1.source_word_id " +
                "LEFT JOIN translations tr2 ON w.id = tr2.target_word_id " +
                "INNER JOIN words t ON tr1.target_word_id = t.id OR tr2.source_word_id = t.id " +
                "WHERE wg.group_id = :wordGroupId " +
                "AND w.language_id = :sourceLangId " +
                "AND t.language_id = :targetLangId " +
                "ORDER BY RANDOM() " +
                "LIMIT :limit"
    )
    suspend fun getNRandomWordsWithTranslation(
        sourceLangId: Long,
        targetLangId: Long,
        wordGroupId: Long,
        limit: Int
    ): List<WordWithTranslation>

    @Insert
    suspend fun insertOne(word: Word): Long
}