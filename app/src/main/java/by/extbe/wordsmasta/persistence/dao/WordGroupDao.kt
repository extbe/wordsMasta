package by.extbe.wordsmasta.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import by.extbe.wordsmasta.persistence.entity.Group
import by.extbe.wordsmasta.persistence.entity.WordGroup

@Dao
interface WordGroupDao {
    @Insert
    suspend fun insertOne(wordGroup: WordGroup)

    @Insert
    suspend fun insertMany(wordGroups: List<WordGroup>)

    @Query(
        "SELECT DISTINCT g.* " +
                "FROM groups g, word_groups wg " +
                "WHERE g.id = wg.group_id " +
                "ORDER BY g.name"
    )
    suspend fun getAllGroupsThatHaveWords(): List<Group>

    @Query("SELECT 1 FROM word_groups WHERE word_id = :wordId AND group_id = :groupId")
    suspend fun existsByWordIdAndGroupId(wordId: Long, groupId: Long): Int?
}