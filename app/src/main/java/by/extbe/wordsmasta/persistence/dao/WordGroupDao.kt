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

    @Query("SELECT DISTINCT g.* FROM groups g, word_groups wg WHERE g.id = wg.group_id")
    suspend fun getAllGroupsThatHaveWords(): List<Group>
}