package by.extbe.wordsmasta.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import by.extbe.wordsmasta.persistence.entity.Translation

@Dao
interface TranslationDao {
    @Query(
        "SELECT 1 " +
                "FROM translations " +
                "WHERE source_word_id IN (:sourceWordId, :targetWordId) " +
                "AND target_word_id IN (:sourceWordId, :targetWordId)"
    )
    suspend fun selectBySourceAndTargetIds(sourceWordId: Long, targetWordId: Long): Int?

    @Insert
    suspend fun insertOne(translation: Translation): Long
}