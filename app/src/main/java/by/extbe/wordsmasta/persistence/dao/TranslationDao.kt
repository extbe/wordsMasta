package by.extbe.wordsmasta.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import by.extbe.wordsmasta.persistence.entity.Translation

@Dao
interface TranslationDao {
    @Insert
    suspend fun insertOne(translation: Translation): Long
}