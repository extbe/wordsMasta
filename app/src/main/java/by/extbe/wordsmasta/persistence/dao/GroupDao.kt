package by.extbe.wordsmasta.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import by.extbe.wordsmasta.persistence.entity.Group

@Dao
interface GroupDao {
    @Insert
    suspend fun insertAll(groups: List<Group>)

    @Query("SELECT id FROM groups WHERE name = :name")
    suspend fun selectIdByName(name: String): Long?
}