package by.extbe.wordsmasta.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import by.extbe.wordsmasta.persistence.entity.Language

@Dao
interface LanguageDao {
    @Insert
    suspend fun insertOne(language: Language): Long

    @Insert
    suspend fun insertAll(languages: List<Language>)

    @Query("SELECT id FROM languages WHERE code = :code")
    suspend fun getIdByCode(code: String): Long?

    @Query("SELECT id FROM languages WHERE name = :name")
    suspend fun getIdByName(name: String): Long?

    @Query("SELECT name FROM languages ORDER BY name")
    suspend fun getAllNames(): List<String>
}